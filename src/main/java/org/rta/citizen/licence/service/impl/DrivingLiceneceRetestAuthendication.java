package org.rta.citizen.licence.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class DrivingLiceneceRetestAuthendication extends AuthenticationService {

	private static final Logger log = Logger.getLogger(DrivingLiceneceRetestAuthendication.class);

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private ActivitiService activitiService;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException {

		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getUid_num())) {
			log.info("Citizen LLR number OR Aadhar details not provided.");
			throw new AadharNotFoundException("Citizen LLR number OR Aadhar details not found.");
		}
		String aadharNumber = citizen.getUid_num();
		String uniqueKey = getUniqueKey(citizen);
		Boolean isRejectedApp = false;
		// TODO: This should be removed soon with more generic approach
		List<String> checkList = new ArrayList<>(Arrays.asList(ServiceType.LL_FRESH.getCode(),
				ServiceType.LL_ENDORSEMENT.getCode(), ServiceType.LL_RETEST.getCode(), ServiceType.DL_FRESH.getCode(),
				ServiceType.DL_ENDORSMENT.getCode(), ServiceType.DL_CHANGE_ADDRESS.getCode(),
				ServiceType.DL_BADGE.getCode(), ServiceType.DL_SURRENDER.getCode(), ServiceType.DL_REVO_SUS.getCode(),
				ServiceType.DL_EXPIRED.getCode(), ServiceType.DL_INT_PERMIT.getCode(), ServiceType.DL_MILITRY.getCode(),
				ServiceType.DL_FOREIGN_CITIZEN.getCode(), ServiceType.DL_CHANGEADDRS_OS.getCode()));
		List<String> activitiList = new ArrayList<>(Arrays.asList("mvi", "cco", "ao_rto"));
		List<UserSessionEntity> activeSessions = userSessionDAO.getAppliedSessions(aadharNumber);
		List<UserSessionEntity> dlFreshRejectedSessions = userSessionDAO.getRejectedAppSessions(aadharNumber,
				ServiceType.DL_FRESH.getCode());
		List<UserSessionEntity> dlEndorseRejectedSessions = userSessionDAO.getRejectedAppSessions(aadharNumber,
				ServiceType.DL_ENDORSMENT.getCode());
		List<UserSessionEntity> dlExpireRejectedSessions = userSessionDAO.getRejectedAppSessions(aadharNumber,
				ServiceType.DL_EXPIRED.getCode());
		for (UserSessionEntity active : activeSessions) {
			if (!ObjectsUtil.isNullOrEmpty(activeSessions) && checkList.contains(active.getServiceCode())) {
				log.info("User can't apply for " + serviceType + "as He has already applied for "
						+ active.getServiceCode());
				throw new ConflictException("User can't apply for " + serviceType + " as He has already applied for "
						+ active.getServiceCode());
			}
			if (!ObjectsUtil.isNullOrEmpty(activeSessions)
					&& active.getServiceCode().equalsIgnoreCase(serviceType.getCode())) {
				String instanceId = applicationService.getProcessInstanceId(active.getSessionId());
				ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
				if (activitiList.contains(actRes.getActiveTasks().get(0).getTaskDefKey())) {
					log.info("Application is pending at RTA office level");
					throw new ConflictException("Application is pending at RTA office level");
				}
			}
		}
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> details = registrationLicenseService
				.getLicenseHolderDtls(aadharNumber, null, null);

		for (UserSessionEntity rejected : dlFreshRejectedSessions) {
			if (!ObjectsUtil.isNullOrEmpty(dlFreshRejectedSessions) && rejected.getUniqueKey().equals(uniqueKey)) {
				isRejectedApp = true;
			}
		}
		for (UserSessionEntity rejected : dlEndorseRejectedSessions) {
			if (!ObjectsUtil.isNullOrEmpty(dlEndorseRejectedSessions) && (rejected.getUniqueKey().equals(uniqueKey)
					|| (details.getResponseBody().getDriversPermitDetailsList().get(0).getDlNo().equals(uniqueKey)
							|| details.getResponseBody().getDriversPermitDetailsList().get(0).getLlrNo()
									.equals(uniqueKey)))) {
				isRejectedApp = true;
			}
		}
		for (UserSessionEntity rejected : dlExpireRejectedSessions) {
			if (!ObjectsUtil.isNullOrEmpty(dlExpireRejectedSessions) && rejected.getUniqueKey().equals(uniqueKey)) {
				isRejectedApp = true;
			}
		}
		if (!isRejectedApp) {
			log.info("User can't apply for DLRE. No rejected application found for LLR number: "
					+ citizen.getLlrNumber());
			throw new ConflictException(
					"User can't apply for DLRE. Not able to find any Rejected application with LLR number:"
							+ citizen.getLlrNumber());
		}

		String pinCode = null;
		boolean validate = false;
		String rtaOfficeCode = null;
		Boolean isExpired = false;
		if (isValidllrNo(citizen) || isValiddlNo(citizen)) {
			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
					.getLicenseHolderDtls(aadharNumber, null, uniqueKey);
			if (null != holderDetails) {
				LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
				List<LearnersPermitDtlModel> permitDtlsModel = holder.getLearnersPermitDetailsList();
				if (null != holder) {
					LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
					if ("Y".equalsIgnoreCase(holderDetail.getIsAdharVerify())
							|| null != holderDetail.getRtaOfficeDetails()) {
						rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
						pinCode = holderDetail.getPresAddrPinCode();
						if (!ObjectsUtil.isNullOrEmpty(permitDtlsModel) && holderDetail.getLicenceHolderId()
								.equals(permitDtlsModel.get(0).getLicenceHolderId()))
							validate = true;
					} else {
						log.info("Aadhaar is not seeded OR RTA office code is not present:"
								+ citizen.getApplicationNumber());
						throw new NotFoundException("Aadhaar is not seeded OR RTA office code is not present:"
								+ citizen.getApplicationNumber());
					}
					for (LearnersPermitDtlModel learnersDtlModel : permitDtlsModel) {
						if (learnersDtlModel.getValidTo().before(new Date())) {
							isExpired = true;
						}
					}
				}
			}

		} else {
			throw new NotFoundException("LLR Number or DL Number is Not Valid");
		}
		if (!validate) {
			log.info("No Driver License entry OR Aadhaar-LLR combination found for provided details:"
					+ citizen.getApplicationNumber());
			throw new NotFoundException("No Driver License entry OR Aadhaar-LLR combination found for provided details:"
					+ citizen.getApplicationNumber());
		}
		if (isExpired) {
			log.info("You Cannot apply for DLRE, Your Learners License has Expired.");
			throw new ConflictException(
					"You Cannot apply for DLRE, Your Learners License has Expired.:" + citizen.getLlrNumber());
		}
		List<UserSessionEntity> activeSes = userSessionDAO.getAppliedSessions(aadharNumber);
		if (!ObjectsUtil.isNullOrEmpty(activeSes)) {
			for (UserSessionEntity active : activeSes) {
				if (active.getServiceCode().equals(serviceType.getCode()) && !active.getUniqueKey().equals(uniqueKey)) {
					uniqueKey = active.getUniqueKey();
				}
			}
		}
		KeyType keyType = getKeyType();
		Long timestamp = DateUtil.toCurrentUTCTimeStamp();
		LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(citizen.getUid_num(), uniqueKey,
				keyType, DateUtil.getLongDate(DateUtil.extractDateAsString(timestamp), "00:00:00"),
				DateUtil.getLongDate(DateUtil.extractDateAsString(timestamp), "23:59:59"));
		Boolean newLoggedIn = Boolean.FALSE;
		Integer loginCount = Integer.valueOf(1);
		if (!ObjectsUtil.isNull(loginHistory)) {
			loginCount = loginHistory.getLoginCount();
			if (loginCount >= maxAllowedPerDay) {
				throw new ForbiddenException("login limit exceeded");
			}
			loginCount++;
		} else {
			newLoggedIn = Boolean.TRUE;
		}
		try {
			if (!StringsUtil.isNullOrEmpty(pinCode) || !authenticateAadhar) {
				citizen.setPincode(pinCode);
				String dob = null;
				if (authenticateAadhar) {
					AadharModel aadhaarModel = aadharAuthentication(citizen);
					dob = aadhaarModel.getDob();
				} else {
					// Once we will be using aadhaar validation, We will remove
					// this.
					dob = "24-03-1986";
				}
				TokenModel tokenModel;
				UserSessionEntity activeSession = userSessionDAO.getActiveSession(aadharNumber, uniqueKey, keyType,
						serviceType);
				if (ObjectsUtil.isNull(activeSession)) {
					activeSession = new UserSessionEntity();
					activeSession.setAadharNumber(aadharNumber);
					activeSession.setCompletionStatus(Status.FRESH.getValue());
					activeSession.setCreatedBy(aadharNumber);
					activeSession.setCreatedOn(timestamp);
					activeSession.setKeyType(keyType);
					activeSession.setServiceCode(serviceType.getCode());
					activeSession.setUniqueKey(uniqueKey);
				}

				activeSession.setModifiedBy(aadharNumber);
				activeSession.setModifiedOn(timestamp);
				activeSession.setLoginTime(timestamp);
				userSessionDAO.saveOrUpdate(activeSession);
				tokenModel = generateToken(activeSession);
				if (newLoggedIn) {
					loginHistory = new LoginAttemptHistoryEntity();
					loginHistory.setAadharNumber(aadharNumber);
					loginHistory.setCreatedBy(aadharNumber);
					loginHistory.setCreatedOn(timestamp);
					loginHistory.setKeyType(keyType);
					loginHistory.setLoginTime(timestamp);
					loginHistory.setModifiedBy(aadharNumber);
					loginHistory.setModifiedOn(timestamp);
					loginHistory.setUniqueKey(uniqueKey);
				}
				loginHistory.setLoginCount(loginCount);

				// create application if not exists
				ApplicationEntity citizenApplicationEntity = applicationDAO
						.getApplicationFromSession(activeSession.getSessionId());
				if (ObjectsUtil.isNull(citizenApplicationEntity)) {
					citizenApplicationEntity = new ApplicationEntity();
					citizenApplicationEntity.setCreatedBy(activeSession.getAadharNumber());
					citizenApplicationEntity.setCreatedOn(timestamp);
					citizenApplicationEntity.setApplicationNumber(applicationService
							.generateApplicationNumber(userSessionConverter.converToModel(activeSession)));
					param.put(CitizenConstants.CREATE_NEW_PROCESS, true);
				} else {
					param.put(CitizenConstants.CREATE_NEW_PROCESS, false);
				}
				citizenApplicationEntity.setModifiedBy(activeSession.getAadharNumber());
				citizenApplicationEntity.setModifiedOn(timestamp);
				citizenApplicationEntity.setServiceCode(serviceType.getCode());
				citizenApplicationEntity.setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
				citizenApplicationEntity.setLoginHistory(activeSession);
				citizenApplicationEntity.setRtaOfficeCode(rtaOfficeCode);
				citizenApplicationEntity.setIsAuthenticated(true);
				citizenApplicationEntity.setApplicantDob(dob);
				applicationDAO.saveOrUpdate(citizenApplicationEntity);
				loginAttemptsDAO.saveOrUpdate(loginHistory);
				return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
			}
		} catch (HttpClientErrorException e) {
			log.info("error in registration service response : ", e);
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				log.info("Not found " + keyType.toString() + " " + uniqueKey);
				throw new NotFoundException("Not found " + keyType.toString() + " " + uniqueKey);
			}
		}
		throw new UnauthorizedException();
	}

	@Override
	protected String getUniqueKey(AuthenticationModel citizen) {
		String llrNumber = citizen.getLlrNumber();
		String dlNumber = citizen.getDlNumber();
		if (StringsUtil.isNullOrEmpty(llrNumber)) {
			if (StringsUtil.isNullOrEmpty(dlNumber)) {
				throw new IllegalArgumentException("LLR and DL Number are missing");
			} else if (!StringsUtil.isNullOrEmpty(dlNumber))
				return dlNumber;
		} else if (!StringsUtil.isNullOrEmpty(llrNumber)) {
			return llrNumber;
		}
		return null;
	}

	protected Boolean isValidllrNo(AuthenticationModel citizen) throws DataMismatchException {
		String llrNumber = citizen.getLlrNumber();
		if (!StringsUtil.isNullOrEmpty(llrNumber) && llrNumber.startsWith(ServiceCategory.LL_CATEGORY.getCode())) {
			return true;
		} else {
			log.info("LLR Number is Not Valid : " + citizen.getLlrNumber());
			return false;
		}

	}

	protected Boolean isValiddlNo(AuthenticationModel citizen) throws DataMismatchException {
		String dlNumber = citizen.getDlNumber();
		if (!StringsUtil.isNullOrEmpty(dlNumber) && dlNumber.startsWith("AP")
				|| dlNumber.startsWith(ServiceCategory.DL_CATEGORY.getCode())) {
			return true;
		} else {
			log.info("DL Number is Not Valid : " + citizen.getDlNumber());
			return false;
		}

	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.DLRE;
	}

}

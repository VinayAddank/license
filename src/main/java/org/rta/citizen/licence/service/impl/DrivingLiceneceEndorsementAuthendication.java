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
public class DrivingLiceneceEndorsementAuthendication extends AuthenticationService {

	private static final Logger log = Logger.getLogger(DrivingLiceneceEndorsementAuthendication.class);

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private ActivitiService activitiService;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException {

		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getUid_num())
				|| StringsUtil.isNullOrEmpty(citizen.getLlrNumber())
						&& StringsUtil.isNullOrEmpty(citizen.getDlNumber())) {
			log.info("Citizen LLR or DL or Aadhar details not provided.");
			throw new AadharNotFoundException("Citizen LLR or DL or Aadhar details not found.");
		}
		String aadharNumber = citizen.getUid_num();
		String uniqueKey = getUniqueKey(citizen);
		KeyType keyType = getKeyType();
		Boolean isValid = false;
		Boolean isRejectedApp = false;
		// TODO: This should be removed soon with more generic approach
		List<String> checkList = new ArrayList<>(Arrays.asList(ServiceType.LL_FRESH.getCode(),
				ServiceType.LL_ENDORSEMENT.getCode(), ServiceType.LL_RETEST.getCode(), ServiceType.DL_FRESH.getCode(),
				ServiceType.DL_RENEWAL.getCode(), ServiceType.DL_CHANGE_ADDRESS.getCode(),
				ServiceType.DL_RETEST.getCode(), ServiceType.DL_BADGE.getCode(), ServiceType.DL_SURRENDER.getCode(),
				ServiceType.DL_REVO_SUS.getCode(), ServiceType.DL_EXPIRED.getCode(),
				ServiceType.DL_INT_PERMIT.getCode(), ServiceType.DL_MILITRY.getCode(),
				ServiceType.DL_FOREIGN_CITIZEN.getCode(), ServiceType.DL_CHANGEADDRS_OS.getCode()));
		List<String> activitiList = new ArrayList<>(Arrays.asList("mvi", "cco", "ao_rto"));
		List<Integer> status = new ArrayList<>(Arrays.asList(Status.APPROVED.getValue(), Status.REJECTED.getValue()));
		List<String> serviceTypes = new ArrayList<>(
				Arrays.asList(ServiceType.DL_ENDORSMENT.getCode(), ServiceType.DL_RETEST.getCode()));
		List<Integer> lleApprovedStatus = new ArrayList<>(Arrays.asList(Status.APPROVED.getValue()));
		List<String> lleApprovedServiceTypes = new ArrayList<>(
				Arrays.asList(ServiceType.DL_ENDORSMENT.getCode(), ServiceType.LL_ENDORSEMENT.getCode()));
		List<UserSessionEntity> activeSessions = userSessionDAO.getAppliedSessions(aadharNumber);
		List<UserSessionEntity> approveSessions = userSessionDAO.getApprovedAppSessions(aadharNumber);
		List<UserSessionEntity> dlEndorseRejectedSessions = userSessionDAO.getRejectedAppSessions(aadharNumber,
				ServiceType.DL_ENDORSMENT.getCode());
		UserSessionEntity lastAppOrReJSession = userSessionDAO.getLastRejectedApprovedSession(aadharNumber,
				serviceTypes, status);
		UserSessionEntity lastApprovedSession = userSessionDAO.getLastRejectedApprovedSession(aadharNumber,
				lleApprovedServiceTypes, lleApprovedStatus);
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

		for (UserSessionEntity rejected : dlEndorseRejectedSessions) {
			if (!ObjectsUtil.isNullOrEmpty(dlEndorseRejectedSessions) && (rejected.getUniqueKey().equals(uniqueKey)
					|| (details.getResponseBody().getDriversPermitDetailsList().get(0).getDlNo().equals(uniqueKey)
							|| details.getResponseBody().getDriversPermitDetailsList().get(0).getLlrNo()
									.equals(uniqueKey)))) {
				if (!ObjectsUtil.isNull(lastAppOrReJSession)
						&& lastAppOrReJSession.getServiceCode().equals(ServiceType.DL_ENDORSMENT.getCode())
						&& lastAppOrReJSession.getCompletionStatus().equals(Status.REJECTED.getValue())) {
					isRejectedApp = true;
				}
			}
		}
		if (!ObjectsUtil.isNull(lastApprovedSession)
				&& lastApprovedSession.getServiceCode().equals(serviceType.getCode())) {
			log.info("User can't apply for " + serviceType + " as He has not applied for any Class of vehicle ");
			throw new NotFoundException(
					"User can't apply for " + serviceType + " as He has not applied for any Class of vehicle ");
		}
		try {
			for (UserSessionEntity approve : approveSessions) {
				if (approve.getServiceCode().equals(ServiceType.DL_FRESH.getCode())
						|| approve.getServiceCode().equals(ServiceType.DL_RETEST.getCode())
						|| approve.getServiceCode().equals(ServiceType.DL_MILITRY.getCode())
						|| approve.getServiceCode().equals(ServiceType.DL_CHANGEADDRS_OS.getCode())) {
					isValid = true;
				}
			}
		} catch (Exception errorMsg) {
			log.debug(errorMsg);
		}
		if (isRejectedApp) {
			log.info("User can't apply for DLE as Rejected Application found with DL Number: " + citizen.getDlNumber());
			throw new ConflictException(
					"User can't apply for DLE as Rejected Application found with DL Number: " + citizen.getDlNumber());
		}

		String pinCode = null;
		boolean validate = false;
		String rtaOfficeCode = null;
		Boolean isExpired = false;
		if (isValidllrNo(citizen, isValid) || isValidDlNumber(citizen)) {
			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
					.getLicenseHolderDtls(aadharNumber, null, uniqueKey);
			if (null != holderDetails) {
				LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
				if (null != holder) {
					LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
					List<LearnersPermitDtlModel> permitDtlsModel = holder.getLearnersPermitDetailsList();
					List<DriversLicenceDetailsModel> driverDtlsModel = holder.getDriversPermitDetailsList();
					if ("Y".equalsIgnoreCase(holderDetail.getIsAdharVerify())
							|| null != holderDetail.getRtaOfficeDetails()) {
						rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
						pinCode = holderDetail.getPresAddrPinCode();
						if (uniqueKey.contains(ServiceCategory.LL_CATEGORY.getCode())) {
							if (isValidllrNo(citizen, isValid) && !ObjectsUtil.isNullOrEmpty(permitDtlsModel)
									&& holderDetail.getLicenceHolderId()
											.equals(permitDtlsModel.get(0).getLicenceHolderId())) {
								validate = true;
							}
						} else {
							if (isValidDlNumber(citizen) && !ObjectsUtil.isNullOrEmpty(driverDtlsModel) && holderDetail
									.getLicenceHolderId().equals(driverDtlsModel.get(0).getLicenceHolderId())) {
								validate = true;
							}
						}
					} else {
						log.info("Aadhaar is not seeded OR RTA office code is not present:" + uniqueKey);
						throw new NotFoundException(
								"Aadhaar is not seeded OR RTA office code is not present:" + uniqueKey);
					}
					/*
					 * for (LearnersPermitDtlModel learnersDtlModel :
					 * permitDtlsModel) { if (DateUtil.addMonths(new Date(),
					 * SomeConstants.SIX).after(learnersDtlModel.getValidTo()))
					 * { isExpired = true; } }
					 */
					if (uniqueKey.startsWith("AP")) {
						for (DriversLicenceDetailsModel model : driverDtlsModel) {
							if (model.getStatusRemarks().contains("CANCEL")
									|| model.getStatusRemarks().contains("SUSPEND")) {
								log.info("You can't apply for" + serviceType
										+ " Your DL number was Cancelled/Suspended.");
								throw new ConflictException("You can't apply for" + serviceType
										+ " Your DL number was Cancelled/Suspended." + citizen.getDlNumber());
							}
						}
					} else if (uniqueKey.startsWith("LLR")) {
						for (LearnersPermitDtlModel learnersDtlModel : permitDtlsModel) {
							if (learnersDtlModel.getValidTo().before(new Date())) {
								isExpired = true;
							}
						}
					}

				}
			}
		} else {
			throw new NotFoundException("User not applied for DL Fresh or LLR or DL number is not valid");
		}

		if (!validate) {
			log.info("No Learning License entry OR Aadhaar-LLR combination found for provided details:" + uniqueKey);
			throw new NotFoundException(
					"No Learning License entry OR Aadhaar-LLR combination found for provided details:" + uniqueKey);
		}
		if (isExpired) {
			log.info("You Cannot apply for DLE, Your Learners License has Expired.");
			throw new ConflictException(
					"You Cannot apply for DLE, Your Learners License has Expired.:" + citizen.getLlrNumber());
		}
		List<UserSessionEntity> activeSes = userSessionDAO.getAppliedSessions(aadharNumber);
		if (!ObjectsUtil.isNullOrEmpty(activeSes)) {
			for (UserSessionEntity active : activeSes) {
				if (active.getServiceCode().equals(serviceType.getCode()) && !active.getUniqueKey().equals(uniqueKey)) {
					uniqueKey = active.getUniqueKey();
				}
			}
		}
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
				citizenApplicationEntity.setApplicantDob(dob);
				citizenApplicationEntity.setIsAuthenticated(true);
				applicationDAO.saveOrUpdate(citizenApplicationEntity);
				loginAttemptsDAO.saveOrUpdate(loginHistory);

				return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
			} else {
				log.info("Please provide valid Pin code:" + uniqueKey);
				throw new NotFoundException("Please provide valid Pin code:" + uniqueKey);
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

	protected Boolean isValidllrNo(AuthenticationModel citizen, Boolean isAppliedForDLF) throws DataMismatchException {
		String llrNumber = citizen.getLlrNumber();
		if (!StringsUtil.isNullOrEmpty(llrNumber) && llrNumber.startsWith(ServiceCategory.LL_CATEGORY.getCode())
				&& isAppliedForDLF) {
			return true;
		} else {
			log.info("User not applied for DL Fresh OR LLR Number is Not Valid : " + citizen.getLlrNumber());
			return false;
		}
	}

	protected Boolean isValidDlNumber(AuthenticationModel citizen) throws DataMismatchException {
		String dlNumber = citizen.getDlNumber();
		if (!StringsUtil.isNullOrEmpty(dlNumber)
				&& (dlNumber.startsWith("AP") || dlNumber.startsWith(ServiceCategory.DL_CATEGORY.getCode()))) {
			return true;
		} else {
			log.info("DL number is Not Valid");
			return false;
		}
	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.DLE;
	}
}

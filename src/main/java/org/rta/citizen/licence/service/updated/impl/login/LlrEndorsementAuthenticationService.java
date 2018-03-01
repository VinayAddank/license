package org.rta.citizen.licence.service.updated.impl.login;

import java.text.ParseException;
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

/**
 *	@Author vinay.addanki created on Feb 12, 2017.
 */

@Service
public class LlrEndorsementAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(LlrEndorsementAuthenticationService.class);

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ParseException, ConflictException {

		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getUid_num())
				|| StringsUtil.isNullOrEmpty(citizen.getLlrNumber())
						&& StringsUtil.isNullOrEmpty(citizen.getDlNumber())) {
			log.info("Citizen LLR or DL or Aadhar details not provided.");
			throw new AadharNotFoundException("Citizen LLR or DL or Aadhar details not provided.");
		}
		String aadharNumber = citizen.getUid_num(); 
		String uniqueKey = getUniqueKey(citizen);
		// TODO: This should be removed soon with more generic approach
		List<String> checkList = new ArrayList<>(Arrays.asList(ServiceType.LL_FRESH.getCode(),
				ServiceType.LL_RETEST.getCode(), ServiceType.DL_FRESH.getCode(), ServiceType.DL_RENEWAL.getCode(),
				ServiceType.DL_ENDORSMENT.getCode(), ServiceType.DL_RETEST.getCode()));
		List<String> activitiList = new ArrayList<>(Arrays.asList("mvi", "cco", "ao_rto"));
		List<UserSessionEntity> activeSessions = userSessionDAO.getAppliedSessions(aadharNumber);
		List<UserSessionEntity> approveSessions = userSessionDAO.getApprovedAppSessions(aadharNumber);
		for (UserSessionEntity active : activeSessions) {
			if (!ObjectsUtil.isNullOrEmpty(activeSessions) && checkList.contains(active.getServiceCode())) {
				log.info("User can't apply for LLE as He has already applied for " + active.getServiceCode());
				throw new ConflictException(
						"User can't apply for LLE as He has already applied for " + active.getServiceCode());
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
		for (UserSessionEntity approve : approveSessions) {
			if (!ObjectsUtil.isNullOrEmpty(approveSessions) && approve.getUniqueKey().equals(uniqueKey)) {
				if (approve.getServiceCode().equals(serviceType)
						|| approve.getServiceCode().equals(ServiceType.DL_FRESH.getCode())) {
					log.info("User can't apply for LLE as He has already having Driving License with LLR Number is: "
							+ approve.getUniqueKey());
					throw new ConflictException(
							"User can't apply for LLE as He has already having Driving License with LLR Number is: "
									+ approve.getUniqueKey());
				}
			}
		}
		String pinCode = null;
		boolean validate = false;
		String rtaOfficeCode = null;
		if (isValidllrNo(citizen) || isValidDlNumber(citizen)) {
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
							if (isValidllrNo(citizen) && !ObjectsUtil.isNullOrEmpty(permitDtlsModel) && holderDetail
									.getLicenceHolderId().equals(permitDtlsModel.get(0).getLicenceHolderId())) {
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
					if (uniqueKey.startsWith("AP")) {
						for (DriversLicenceDetailsModel model : driverDtlsModel) {
							if (model.getStatusRemarks().equals("CANCEL")
									|| model.getStatusRemarks().equals("SUSPEND")) {
								log.info("You can't apply for" + serviceType
										+ " Your DL number was Cancelled/Suspended.");
								throw new ConflictException("You can't apply for" + serviceType
										+ " Your DL number was Cancelled/Suspended." + citizen.getDlNumber());
							}
							if (driverDtlsModel.size() >= SomeConstants.FIFTEEN) {
								throw new ConflictException("You can't apply for" + serviceType
										+ " All class of vehicles are listed to your Dl number ." + citizen.getDlNumber());
							}
						}
					} else if (uniqueKey.startsWith("LLR")) {
						for (LearnersPermitDtlModel learnersDtlModel : permitDtlsModel) {
							if (learnersDtlModel.getValidTo().before(new Date())) {
								log.info("You Cannot apply for LLE, Your Learners License has Expired.");
								throw new ConflictException(
										"You Cannot apply for LLE, Your Learners License has Expired.:"
												+ citizen.getLlrNumber());

							}
						}
					}
				}
			}
		} else {
			throw new NotFoundException("LLR or DL number is not valid");
		}
		if (!validate) {
			log.info("No Learning License entry OR Aadhaar-LLR combination found for provided details:" + uniqueKey);
			throw new NotFoundException(
					"No Learning License entry OR Aadhaar-LLR combination found for provided details:" + uniqueKey);
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
			loginCount--;
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

	protected Boolean isValidllrNo(AuthenticationModel citizen) throws DataMismatchException {
		String llrNumber = citizen.getLlrNumber();
		if (!StringsUtil.isNullOrEmpty(llrNumber) && llrNumber.startsWith(ServiceCategory.LL_CATEGORY.getCode())) {
			return true;
		} else {
			log.info("LLR Number is Not Valid : " + citizen.getLlrNumber());
			return false;
		}
	}

	protected Boolean isValidDlNumber(AuthenticationModel citizen) throws DataMismatchException {
		String dlNumber = citizen.getDlNumber();
		if (!StringsUtil.isNullOrEmpty(dlNumber) && dlNumber.startsWith("AP")
				|| dlNumber.startsWith(ServiceCategory.DL_CATEGORY.getCode())) {
			return true;
		} else {
			log.info("DL number is Not Valid: " + citizen.getDlNumber());
			return false;
		}
	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.LLE;
	}
}

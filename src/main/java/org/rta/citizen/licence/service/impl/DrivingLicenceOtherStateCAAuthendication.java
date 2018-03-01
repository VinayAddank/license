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
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
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
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class DrivingLicenceOtherStateCAAuthendication extends AuthenticationService {

	private static final Logger log = Logger.getLogger(DrivingLicenceOtherStateCAAuthendication.class);

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Override
	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException, ServiceValidationException {

		// TODO: Generic approach for all License login validations required.
		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getUid_num())) {
			log.info("Citizen OR Aadhar details not provided.");
			throw new AadharNotFoundException("Citizen OR Aadhar details not found.");
		}

		String aadharNumber = citizen.getUid_num();
		// TODO: This should be removed soon with more generic validation
		// approach
		List<String> checkList = new ArrayList<>(Arrays.asList(ServiceType.LL_FRESH.getCode(),
				ServiceType.LL_ENDORSEMENT.getCode(), ServiceType.LL_RETEST.getCode(), ServiceType.DL_FRESH.getCode(),
				ServiceType.DL_ENDORSMENT.getCode(), ServiceType.DL_RENEWAL.getCode(),
				ServiceType.DL_CHANGE_ADDRESS.getCode(), ServiceType.DL_RETEST.getCode(),
				ServiceType.DL_BADGE.getCode(), ServiceType.DL_SURRENDER.getCode(), ServiceType.DL_REVO_SUS.getCode(),
				ServiceType.DL_EXPIRED.getCode(), ServiceType.DL_INT_PERMIT.getCode(), ServiceType.DL_MILITRY.getCode(),
				ServiceType.DL_FOREIGN_CITIZEN.getCode()));
		List<String> activitiList = new ArrayList<>(Arrays.asList("cco", "ao_rto"));
		List<UserSessionEntity> activeSessions = userSessionDAO.getAppliedSessions(aadharNumber);
		for (UserSessionEntity active : activeSessions) {
			if (!ObjectsUtil.isNullOrEmpty(activeSessions) && checkList.contains(active.getServiceCode())) {
				log.info("User can't apply for LLF as He has already applied for " + active.getServiceCode());
				throw new ConflictException(
						"User can't apply for LLF as He has already applied for " + active.getServiceCode());
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

		List<UserSessionEntity> approvedSessions = userSessionDAO.getApprovedAppSessions(aadharNumber);
		for (UserSessionEntity active : approvedSessions) {
			if (!ObjectsUtil.isNullOrEmpty(approvedSessions)
					&& serviceType.getCode().equalsIgnoreCase(active.getServiceCode())) {
				log.info("User can't apply for new" + serviceType.getCode()
						+ "as User is already applied and generated.");
				throw new ConflictException("User can't apply for new" + serviceType.getCode()
						+ "as He already holds Milatary License with Number." + active.getUniqueKey());
			}
		}

		String uniqueKey = getUniqueKey(citizen);
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
				log.info("LOGIN Attempt Failed, Limit exceeded");
				throw new ForbiddenException("login limit exceeded");
			}
			loginCount--;
		} else {
			newLoggedIn = Boolean.TRUE;
		}
		String pinCode = null;
		String rtaOfficeCode = null;
		Integer count1 = 0, count2 = 0, countList = 0;
		boolean validate = false, isExpired = false, isInRenewalPeriod = false;
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
				.getLicenseHolderDtlsForDriver(aadharNumber, citizen.getDlNumber());
		if (null != holderDetails) {
			LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
			if (null != holder) {
				LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
				List<DriversLicenceDetailsModel> driversLicenceDetailslist = holder.getDriversPermitDetailsList();
				rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
				pinCode = holderDetail.getPresAddrPinCode();
				try {
					// if ("Y".equalsIgnoreCase(holderDetail.getIsAdharVerify())
					// ||
					// driversLicenceDetailslist.get(0).getLicenceHolderId().equals(holderDetail.getLicenceHolderId()))
					// {
					validate = true;
					// }
				} catch (Exception e) {
					log.debug("Aadhaar is not seeded OR DL Number OR RTA office is not present: "
							+ citizen.getDlNumber());
					throw new NotFoundException("Aadhaar is not seeded OR DL Number OR RTA office is not present: "
							+ citizen.getDlNumber());
				}
				for (DriversLicenceDetailsModel model : driversLicenceDetailslist) {
					if (model.getStatusRemarks().contains("CANCEL") || model.getStatusRemarks().contains("SUSPEND")) {
						log.info("You can't apply for" + serviceType + " Your DL number was Cancelled/Suspended.");
						throw new ConflictException("You can't apply for" + serviceType
								+ " Your DL number was Cancelled/Suspended." + citizen.getDlNumber());
					} else if (SomeConstants.VALID.equalsIgnoreCase(model.getStatusCode())
							&& DateUtil.addYearToDate(model.getValidTo(), SomeConstants.FIVE).before(new Date())) {
						isExpired = true;
						count1++;
					} else if (SomeConstants.VALID.equalsIgnoreCase(model.getStatusCode())
							&& (model.getValidTo().before(new Date()))) {
						count2++;
						isInRenewalPeriod = true;
					}
					countList++;
				}
			}
		}
		if (!validate) {
			log.info("No Learning License entry OR Aadhaar-DL combination found for provided details:"
					+ citizen.getDlNumber());
			throw new NotFoundException(
					"No Learning License entry OR Aadhaar-DL combination found for provided details:"
							+ citizen.getDlNumber());
		}
		if (isExpired && countList.equals(count1)) {
			log.info("DL Expired on :" + citizen.getDlNumber());
			throw new NotFoundException(
					"Driver License Expired on:" + citizen.getDlNumber() + ", Plesae call DL Expired Service");
		}
		if (isInRenewalPeriod && countList.equals(count2)) {
			log.info("DL is in Renewal period :" + citizen.getDlNumber());
			throw new NotFoundException(
					"DL is in Renewal period:" + citizen.getDlNumber() + ", Plesae call DL Renewal Service");
		}
		try {
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
			pendingServiceExists(serviceType, activeSession);
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
				citizenApplicationEntity.setApplicantDob(dob);
				citizenApplicationEntity.setIsAuthenticated(true);
				citizenApplicationEntity.setServiceCode(serviceType.getCode());
				citizenApplicationEntity.setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
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
	protected KeyType getKeyType() {
		return KeyType.DLOS;
	}

	@Override
	protected String getUniqueKey(AuthenticationModel citizen) {
		return ServiceType.DL_CHANGEADDRS_OS.getCode();
	}
}
package org.rta.citizen.licence.service.updated.impl.login;

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
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class LlrRetestAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(LlrRetestAuthenticationService.class);

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private ActivitiService activitiService;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException, ServiceValidationException {

		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getUid_num())
				|| StringsUtil.isNullOrEmpty(citizen.getApplicationNumber())) {
			log.info("Application number OR Aadhar details not provided.");
			throw new AadharNotFoundException("Application number OR Aadhar details not provided.");
		}

		String aadharNumber = citizen.getUid_num();
		// TODO: This should be removed soon with more generic approach
		List<String> checkList = new ArrayList<>(Arrays.asList(ServiceType.LL_FRESH.getCode(),
				ServiceType.LL_ENDORSEMENT.getCode(), ServiceType.DL_FRESH.getCode(), ServiceType.DL_RENEWAL.getCode(),
				ServiceType.DL_CHANGE_ADDRESS.getCode(), ServiceType.DL_ENDORSMENT.getCode(),
				ServiceType.DL_RETEST.getCode()));
		List<String> activitiList = new ArrayList<>(Arrays.asList("mvi", "cco", "exam", "ao_rto"));
		List<UserSessionEntity> activeSessions = userSessionDAO.getAppliedSessions(aadharNumber);
		for (UserSessionEntity active : activeSessions) {
			if (!ObjectsUtil.isNullOrEmpty(activeSessions) && checkList.contains(active.getServiceCode())) {
				log.info("User can't apply for LLR as He has already applied for " + active.getServiceCode());
				throw new ConflictException(
						"User can't apply for LLR as He has already applied for " + active.getServiceCode());
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
		if (isValidAppNo(citizen)) {
			ApplicationEntity appEntity = applicationDAO.getApplication(citizen.getApplicationNumber());
			if (null == appEntity || !aadharNumber.equalsIgnoreCase(appEntity.getLoginHistory().getAadharNumber())
					|| Status.REJECTED.getValue() != appEntity.getLoginHistory().getCompletionStatus()) {
				log.info("User can't apply for LLR. No rejected LLF application found for application number: "
						+ citizen.getApplicationNumber());
				throw new ConflictException(
						"User can't apply for LLR. Not able to find any Rejected LLF application with application number:"
								+ citizen.getApplicationNumber());
			}
		} else
			throw new NotFoundException("Application Number is Not Valid");

		List<UserSessionEntity> rejectedSessions = userSessionDAO.getRejectedAppSessions(aadharNumber,
				serviceType.getCode());
		if (!ObjectsUtil.isNullOrEmpty(rejectedSessions)) {
			int daysDiff, attemptCount = rejectedSessions.size();
			Date lastDate, firstDate = DateUtil
					.getDatefromString(DateUtil.extractDateAsStringWithHyphen(rejectedSessions.get(0).getCreatedOn()));
			lastDate = DateUtil.getDatefromString(DateUtil.extractDateAsStringWithHyphen(
					rejectedSessions.get(attemptCount - SomeConstants.ONE).getCreatedOn()));
			daysDiff = (int) (Math.abs(firstDate.getTime() - lastDate.getTime())
					/ (SomeConstants.THOUSUND * SomeConstants.SIXTY * SomeConstants.SIXTY * SomeConstants.TWENTY_FOUR));
			if (attemptCount >= SomeConstants.THREE && daysDiff <= SomeConstants.SEVEN
					&& DateUtil.addMonths(lastDate, SomeConstants.TWO).after(new Date())) {
				log.info("User Can't apply for LLR as He exceeds test attempts, Please apply after 60 days");
				throw new ConflictException(
						"User Can't apply for LLR as He exceeds test attempts, Please apply after 60 days");
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
				throw new ForbiddenException("login limit exceeded");
			}
			loginCount++;
		} else {
			newLoggedIn = Boolean.TRUE;
		}

		String pinCode = null;
		boolean validate = false;
		String rtaOfficeCode = null;
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
				.getLicenseHolderDtls(aadharNumber, null, citizen.getApplicationNumber());
		if (null != holderDetails) {
			LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
			if (null != holder) {
				LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
				if ("Y".equalsIgnoreCase(holderDetail.getIsAdharVerify())
						|| null != holderDetail.getRtaOfficeDetails()) {
					rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
					pinCode = holderDetail.getPresAddrPinCode();
					validate = true;
				} else {
					log.info("Aadhaar is not seeded OR RTA office code is not present:" + citizen.getLlrNumber());
					throw new NotFoundException(
							"Aadhaar is not seeded OR RTA office code is not present:" + citizen.getLlrNumber());
				}
			}
		}

		if (!validate) {
			log.info("No Learning License entry OR Aadhaar-LLR combination found for provided details:"
					+ citizen.getLlrNumber());
			throw new NotFoundException(
					"No Learning License entry OR Aadhaar-LLR combination found for provided details:"
							+ citizen.getLlrNumber());
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
		return citizen.getApplicationNumber();
	}

	protected Boolean isValidAppNo(AuthenticationModel citizen) throws DataMismatchException {
		String applicationNumber = citizen.getApplicationNumber();
		if (!StringsUtil.isNullOrEmpty(applicationNumber)
				&& (applicationNumber.startsWith("LLF") || applicationNumber.startsWith("LLR"))) {
			return true;
		} else {
			log.info("Application Number is Not Valid : " + citizen.getApplicationNumber());
			return false;
		}
	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.LLR;
	}
}

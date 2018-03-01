package org.rta.citizen.licence.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
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
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class DrivingLicenceServiceImpl extends AuthenticationService {

	private static final Logger log = Logger.getLogger(DrivingLicenceServiceImpl.class);

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private ActivitiService activitiService;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException {

		log.info("::::::::::::::::::::::::::::: Driving License Fresh Landing Start ::::::::::::::::::::::::::::::::");

		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getUid_num())
				|| StringsUtil.isNullOrEmpty(citizen.getLlrNumber())) {
			log.info("Citizen LLR OR Aadhar details not provided.");
			throw new AadharNotFoundException("Please provide Citizen LLR and Aadhar Number.");
		}
		String aadharNumber = citizen.getUid_num();
		// TODO: This should be removed soon with more generic approach
		List<String> checkList = new ArrayList<>(Arrays.asList(ServiceType.DL_CHANGE_ADDRESS.getCode(),
				ServiceType.DL_RENEWAL.getCode(), ServiceType.DL_ENDORSMENT.getCode(), ServiceType.DL_RETEST.getCode(),
				ServiceType.LL_FRESH.getCode(), ServiceType.LL_RETEST.getCode(), ServiceType.LL_ENDORSEMENT.getCode()));
		List<String> activitiList = new ArrayList<>(Arrays.asList("mvi", "cco", "ao_rto"));
		List<UserSessionEntity> activeSessions = userSessionDAO.getAppliedSessions(aadharNumber);
		for (UserSessionEntity active : activeSessions) {
			if (!ObjectsUtil.isNullOrEmpty(activeSessions) && checkList.contains(active.getServiceCode())) {
				log.info("User can't apply for " + serviceType + "as He has already applied for "
						+ active.getServiceCode());
				throw new ConflictException("User can't apply for " + serviceType + " as He has already applied for "
						+ active.getServiceCode());
			}
			if (!ObjectsUtil.isNullOrEmpty(activeSessions)
					&& active.getServiceCode().equalsIgnoreCase(ServiceType.DL_FRESH.getCode())) {
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
				log.info("User can't apply for new DLF as His DLF has already been generated.");
				throw new ConflictException("User can't apply for new DLF as He already holds Drivers Licenese");
			}
		}

		String uniqueKey = getUniqueKey(citizen);
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

		String pinCode = null;
		boolean validate = false;
		Boolean isExpired = false;
		String rtaOfficeCode = null;
		try {
			if (isValidllrNo(citizen)) {
				RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
						.getLicenseHolderDtls(aadharNumber, null, citizen.getLlrNumber());
				if (null != holderDetails) {
					LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
					List<LearnersPermitDtlModel> learnersPermitDetails = holder.getLearnersPermitDetailsList();
					if (null != holder) {
						LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
						if ("Y".equalsIgnoreCase(holderDetail.getIsAdharVerify())
								|| null != holderDetail.getRtaOfficeDetails()) {

							// User can apply for DL Fresh after 30 days of
							// obtaining an LLR/Endorsement
							// if(!learnersPermitDetails.isEmpty() &&
							// (isAvailableForDlFresh(learnersPermitDetails.get(0).getLlrIssuedt())))
							// {
							rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
							pinCode = holderDetail.getPresAddrPinCode();
							if (!ObjectsUtil.isNullOrEmpty(learnersPermitDetails) && holderDetail.getLicenceHolderId()
									.equals(learnersPermitDetails.get(0).getLicenceHolderId()))
								validate = true;
							// }else {
							// log.info("Your License number is not complete 30
							// days
							// OR It has been expired please apply for renewal
							// OR
							// wait for completion of 30 days");
							// throw new NotFoundException("Your License number
							// is
							// not complete 30 days OR It has been expired
							// please
							// apply for renewal OR wait for completion of 30
							// days"+
							// citizen.getLlrNumber());
							// }
						} else {
							log.info("Aadhaar is not seeded OR RTA office code is not present:"
									+ citizen.getLlrNumber());
							throw new NotFoundException("Aadhaar is not seeded OR RTA office code is not present:"
									+ citizen.getLlrNumber());
						}
						for (LearnersPermitDtlModel learnersDtlModel : learnersPermitDetails) {
							if (learnersDtlModel.getValidTo().before(new Date())) {
								isExpired = true;
							}
						}

					}
				}
			} else {
				throw new IllegalArgumentException("LLR Number is Not Valid");
			}
		} catch (Exception errorMsg) {
			log.debug(errorMsg);
		}
		if (!validate) {
			log.info("No Learning License entry OR Aadhaar-LLR combination found for provided details:"
					+ citizen.getLlrNumber());
			throw new NotFoundException(
					"No Learning License entry OR Aadhaar-LLR combination found for provided details:"
							+ citizen.getLlrNumber());
		}
		if (isExpired) {
			log.info("You Cannot apply for DLF, Your Learners License has Expired.");
			throw new ConflictException(
					"You Cannot apply for DLF, Your Learners License has Expired.:" + citizen.getLlrNumber());
		}

		try {
			if (!StringsUtil.isNullOrEmpty(pinCode) || !authenticateAadhar) {
				citizen.setPincode(pinCode);
				String dob = null;
				if (authenticateAadhar) {
					dob = aadharAuthentication(citizen).getDob();
				} else {
					dob = "03-03-1986";
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
		if (StringsUtil.isNullOrEmpty(llrNumber)) {
			throw new IllegalArgumentException("LLR Number is missing");
		} else if (!StringsUtil.isNullOrEmpty(llrNumber)) {
			return llrNumber;
		}
		return null;
	}

	protected boolean isAvailableForDlFresh(Date date) {
		Calendar dateBeforeMonth = Calendar.getInstance();
		dateBeforeMonth.add(Calendar.MONTH, -1);
		Calendar dateAfter6months = Calendar.getInstance();
		dateAfter6months.setTime(date);
		dateAfter6months.add(Calendar.MONTH, 6);
		return (date.before(dateBeforeMonth.getTime()) && new Date().before(dateAfter6months.getTime())) ? true : false;
	}

	protected Boolean isValidllrNo(AuthenticationModel citizen) throws DataMismatchException {
		String llrNumber = citizen.getLlrNumber();
		if (!StringsUtil.isNullOrEmpty(llrNumber) && llrNumber.startsWith(ServiceCategory.LL_CATEGORY.getCode())) {
			return true;
		} else {
			throw new DataMismatchException("LL number is Not Valid");
		}
	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.DLF;
	}
}

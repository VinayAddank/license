
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
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class DrivingLiceneceBadgeAuthendication extends AuthenticationService {

	private static final Logger log = Logger.getLogger(DrivingLiceneceBadgeAuthendication.class);

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException {

		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getUid_num())
				|| StringsUtil.isNullOrEmpty(citizen.getDlNumber())) {
			log.info("Citizen DL number OR Aadhar details not provided.");
			throw new AadharNotFoundException("Please provide Aadhar number and DL number");
		}
		String aadharNumber = citizen.getUid_num();
		Boolean isBadgePresent = false;
		List<String> checkList = new ArrayList<>(Arrays.asList(ServiceType.LL_FRESH.getCode(),
				ServiceType.LL_ENDORSEMENT.getCode(), ServiceType.LL_RETEST.getCode(), ServiceType.DL_FRESH.getCode(),
				ServiceType.DL_ENDORSMENT.getCode(), ServiceType.DL_RENEWAL.getCode(),
				ServiceType.DL_CHANGE_ADDRESS.getCode(), ServiceType.DL_RETEST.getCode(),
				ServiceType.DL_SURRENDER.getCode(), ServiceType.DL_REVO_SUS.getCode(), ServiceType.DL_EXPIRED.getCode(),
				ServiceType.DL_INT_PERMIT.getCode(), ServiceType.DL_MILITRY.getCode(),
				ServiceType.DL_FOREIGN_CITIZEN.getCode(), ServiceType.DL_CHANGEADDRS_OS.getCode()));
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
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> detailsList = registrationLicenseService
				.getLicenseHolderDtls(aadharNumber, null, null);
		if (null != detailsList) {
			LicenseHolderPermitDetails licenseHolderPermitDtls = detailsList.getResponseBody();
			List<DriversLicenceDetailsModel> driversLicenceDtlslist = licenseHolderPermitDtls
					.getDriversPermitDetailsList();
			if (!ObjectsUtil.isNullOrEmpty(driversLicenceDtlslist)) {
				for (DriversLicenceDetailsModel driversLicenceDtlsModel : driversLicenceDtlslist) {
					if (!StringsUtil.isNullOrEmpty(driversLicenceDtlsModel.getDlBadgeNo())) {
						isBadgePresent = true;
					}
				}
			}
		}
		for (UserSessionEntity active : approvedSessions) {
			if (!ObjectsUtil.isNullOrEmpty(approvedSessions)
					&& serviceType.getCode().equalsIgnoreCase(active.getServiceCode()) || isBadgePresent) {
				log.info("User can't apply for new DLB as His DL Badge has already been generated.");
				throw new ConflictException(
						"User can't apply for new DLB as He already holds DL Badge Licenese with Number."
								+ active.getUniqueKey());
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
		Boolean isValidForDLB = false;
		Integer count1 = 0, count2 = 0, countList = 0;
		boolean validate = false, isExpired = false, isInRenewalPeriod = false;
		String rtaOfficeCode = null;
		if (isValidDlNumber(citizen)) {
			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
					.getLicenseHolderDtlsForDriver(aadharNumber, citizen.getDlNumber());
			if (null != holderDetails) {
				LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
				if (null != holder) {
					List<String> vehicleClassList = new ArrayList<>(
							Arrays.asList("ART", "HGV", "HPV", "HTV", "HZRD", "LMVT", "MGV", "MPV", "MTL", "OTH"));
					LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
					List<DriversLicenceDetailsModel> driversLicenceDetailslist = holder.getDriversPermitDetailsList();
					if ("Y".equalsIgnoreCase(holderDetail.getIsAdharVerify())
							|| null != holderDetail.getRtaOfficeDetails()) {
						rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
						pinCode = holderDetail.getPresAddrPinCode();
						if (!ObjectsUtil.isNullOrEmpty(driversLicenceDetailslist) && driversLicenceDetailslist.get(0)
								.getLicenceHolderId().equals(holderDetail.getLicenceHolderId())) {
							for (DriversLicenceDetailsModel driversLicenceDetails : driversLicenceDetailslist) {
								if (vehicleClassList.contains(driversLicenceDetails.getDlVehicleClassCode())) {
									isValidForDLB = true;
								}
								validate = true;
							}
						}
						if (!isValidForDLB) {
							log.info(
									"You are not authorized to drive transport vehicles and hence cannot obtain a badge, please obtain a transport DL and then apply for this service");
							throw new ConflictException(
									"You are not authorized to drive transport vehicles and hence cannot obtain a badge, please obtain a transport DL and then apply for this service");
						}
						for (DriversLicenceDetailsModel model : driversLicenceDetailslist) {
							if (model.getStatusRemarks().contains("CANCEL")
									|| model.getStatusRemarks().contains("SUSPEND")) {
								log.info("You can't apply for" + serviceType
										+ " Your DL number was Cancelled/Suspended.");
								throw new ConflictException("You can't apply for" + serviceType
										+ " Your DL number was Cancelled/Suspended." + citizen.getDlNumber());
							} else if (SomeConstants.VALID.equalsIgnoreCase(model.getStatusCode()) && DateUtil
									.addYearToDate(model.getValidTo(), SomeConstants.FIVE).before(new Date())) {
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
			}
		} else {
			throw new NotFoundException("DL number is not valid");
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
		String dlNo = citizen.getDlNumber();
		if (StringsUtil.isNullOrEmpty(dlNo)) {
			throw new IllegalArgumentException("DL Number is missing");
		}
		return dlNo;
	}

	protected Boolean isValidDlNumber(AuthenticationModel citizen) throws DataMismatchException {
		String dlNumber = citizen.getDlNumber();
		if (!StringsUtil.isNullOrEmpty(dlNumber) && dlNumber.startsWith("AP")
				|| dlNumber.startsWith(ServiceCategory.DL_CATEGORY.getCode())) {
			return true;
		} else {
			log.info("DL number is Not Valid");
			return false;
		}
	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.DLB;
	}
}

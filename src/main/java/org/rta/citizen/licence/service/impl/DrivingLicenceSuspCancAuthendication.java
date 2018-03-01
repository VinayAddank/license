package org.rta.citizen.licence.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
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
public class DrivingLicenceSuspCancAuthendication extends AuthenticationService {

	private static final Logger log = Logger.getLogger(DrivingLicenceSuspCancAuthendication.class);

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
		String uniqueKey = citizen.getDlNumber();
		KeyType keyType = getKeyType();
		boolean validate = false;
		String rtaOfficeCode = null;
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
				.getLicenseHolderDtls(aadharNumber, null, citizen.getDlNumber());
		if (null != holderDetails) {
			LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
			if (null != holder) {
				LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
				List<DriversLicenceDetailsModel> driversLicenceDetailslist = holder.getDriversPermitDetailsList();
				if (null != holderDetail.getRtaOfficeDetails() || "Y".equalsIgnoreCase(holderDetail.getIsActive())) {
					rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
					validate = true;
				} else {
					log.info("DL number is not active :" + citizen.getDlNumber());
					throw new NotFoundException("DL number is not active :" + citizen.getDlNumber());
				}
				for (DriversLicenceDetailsModel model : driversLicenceDetailslist) {
					if (model.getStatusRemarks().equals("CANCEL") || model.getStatusRemarks().equals("SUSPEND")) {
						log.info("Provided DL Number is already Suspended OR Cancelled. " + citizen.getDlNumber());
						throw new ConflictException(
								"Provided DL Number is already Suspended OR Cancelled. " + citizen.getDlNumber());
					}
				}
			}
		}
		if (!validate) {
			log.info("DL number is not active :" + citizen.getDlNumber());
			throw new NotFoundException("DL number is not active :" + citizen.getDlNumber());
		}
		if (userReq.equalsIgnoreCase(UserType.ROLE_CCO.getLabel())) {
			UserSessionEntity activeSessionEntity = userSessionDAO.getLatestUserSession(null, uniqueKey, null,
					ServiceType.DL_SUSU_CANC, Status.PENDING);
			if (!ObjectsUtil.isNull(activeSessionEntity)) {
				throw new ForbiddenException("There is already an application open for DL Number: " + uniqueKey);
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
			citizenApplicationEntity.setIsAuthenticated(false);
			applicationDAO.saveOrUpdate(citizenApplicationEntity);
			loginAttemptsDAO.saveOrUpdate(loginHistory);
			tokenModel.setAppNumber(citizenApplicationEntity.getApplicationNumber());
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
	protected String getUniqueKey(AuthenticationModel citizen) {
		String dlNo = citizen.getDlNumber();
		if (StringsUtil.isNullOrEmpty(dlNo)) {
			throw new IllegalArgumentException("DL Number is missing");
		}
		return dlNo;
	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.DLSC;
	}
}

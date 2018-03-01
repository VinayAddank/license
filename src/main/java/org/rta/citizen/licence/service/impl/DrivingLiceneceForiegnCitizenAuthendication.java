package org.rta.citizen.licence.service.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
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
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class DrivingLiceneceForiegnCitizenAuthendication extends AuthenticationService {

	private static final Logger log = Logger.getLogger(DrivingLiceneceForiegnCitizenAuthendication.class);

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Override
	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException, ServiceValidationException {

		// TODO: Generic approach for all License login validations required.
		if (null == citizen || StringsUtil.isNullOrEmpty(citizen.getPassportNo())) {
			log.info("Citizen OR User details not provided.");
			throw new AadharNotFoundException("Citizen OR User details not found.");
		}

		String uniqueKey = getUniqueKey(citizen);
		KeyType keyType = getKeyType();
		String aadharNumber = citizen.getPassportNo();
		Long timestamp = DateUtil.toCurrentUTCTimeStamp();
		LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(citizen.getPassportNo(), uniqueKey,
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
			pendingServiceExists(serviceType, activeSession);
			if (newLoggedIn) {
				loginHistory = new LoginAttemptHistoryEntity();
				loginHistory.setAadharNumber(aadharNumber);
				loginHistory.setCreatedBy(citizen.getPassportNo());
				loginHistory.setCreatedOn(timestamp);
				loginHistory.setKeyType(keyType);
				loginHistory.setLoginTime(timestamp);
				loginHistory.setModifiedBy(citizen.getPassportNo());
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
				citizenApplicationEntity.setApplicantDob(citizen.getDob());
				citizenApplicationEntity.setIsAuthenticated(true);
				citizenApplicationEntity.setServiceCode(serviceType.getCode());
				citizenApplicationEntity.setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
				citizenApplicationEntity.setApplicationNumber(applicationService
						.generateApplicationNumber(userSessionConverter.converToModel(activeSession)));
				param.put(CitizenConstants.CREATE_NEW_PROCESS, true);
			} else {
				param.put(CitizenConstants.CREATE_NEW_PROCESS, false);
			}
			citizenApplicationEntity.setModifiedOn(timestamp);
			citizenApplicationEntity.setServiceCode(serviceType.getCode());
			citizenApplicationEntity.setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
			citizenApplicationEntity.setLoginHistory(activeSession);
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
		return KeyType.DLFC;
	}

	@Override
	protected String getUniqueKey(AuthenticationModel citizen) {
		return citizen.getDob();
	}
}

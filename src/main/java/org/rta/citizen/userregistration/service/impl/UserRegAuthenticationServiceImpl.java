/**
 * 
 */
package org.rta.citizen.userregistration.service.impl;

import java.text.ParseException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @author arun.verma
 *
 */

@Service
public class UserRegAuthenticationServiceImpl extends AuthenticationService {

	private static final Logger log = Logger.getLogger(UserRegAuthenticationServiceImpl.class);

	@Transactional
	@Override
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException, AadharNotFoundException,
			DataMismatchException, NotFoundException, AadharAuthenticationFailedException, VehicleNotFinanced,
			FinancerNotFound, ParseException, ServiceValidationException {
		if (ObjectsUtil.isNull(citizen.getUserType())) {
			log.error("invalid usertype : " + citizen.getUserType());
			throw new IllegalArgumentException("invalid usertype");
		}

		if (ObjectsUtil.isNull(citizen.getUid_num())) {
			log.error("invalid aadhar : " + citizen.getUid_num());
			throw new IllegalArgumentException("invalid aadhar");
		}

		boolean isUserExits = false;
		RegistrationServiceResponseModel<Boolean> userExists = null;
		try {
			userExists = registrationService.isUserExistsByAadharAndType(citizen.getUid_num(), citizen.getUserType());
			if (!ObjectsUtil.isNull(userExists) && userExists.getHttpStatus() == HttpStatus.OK) {
				isUserExits = userExists.getResponseBody();
			} else {
				log.error("error when getting isUserExistsBy aadhar and userType ");
				throw new UnauthorizedException("unauthorized");
			}
		} catch (HttpClientErrorException e) {
			log.error("error when getting isUserExistsBy aadhar and userType : ", e);
			throw new UnauthorizedException("unauthorized");
		} catch (HttpServerErrorException e) {
			log.error("error when getting isUserExistsBy aadhar and userType : ", e);
			throw new UnauthorizedException("unauthorized");
		}

		if (isUserExits) {
			log.info("user with aadhar : " + citizen.getUid_num() + ", and userType : " + citizen.getUserType()
					+ " already exits");
			throw new ServiceValidationException(ServiceValidation.USER_ALREADY_EXITS.getCode(),
					ServiceValidation.USER_ALREADY_EXITS.getValue());
		}

		String uniqueKey = serviceType.getCode();
		Long timestamp = DateUtil.toCurrentUTCTimeStamp();
		LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(citizen.getUid_num(), uniqueKey,
				getKeyType(), DateUtil.getLongDate(DateUtil.extractDateAsString(timestamp), "00:00:00"),
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

		if (!ObjectsUtil.isNull(citizen.getUid_num())) {
			if (authenticateAadhar) {
				aadharAuthentication(citizen);
			}
			// TODO get the rta office code dynamicaly
			TokenModel tokenModel;
			UserSessionEntity activeSession = userSessionDAO.getActiveSession(citizen.getUid_num(), uniqueKey,
					getKeyType(), serviceType);
			if (ObjectsUtil.isNull(activeSession)) {
				activeSession = new UserSessionEntity();
				activeSession.setAadharNumber(citizen.getUid_num());
				activeSession.setCompletionStatus(Status.FRESH.getValue());
				activeSession.setCreatedBy(citizen.getUid_num());
				activeSession.setCreatedOn(timestamp);
				activeSession.setKeyType(getKeyType());
				activeSession.setServiceCode(serviceType.getCode());
				activeSession.setUniqueKey(uniqueKey);
			}
			pendingServiceExists(serviceType, activeSession);
			activeSession.setModifiedBy(citizen.getUid_num());
			activeSession.setModifiedOn(timestamp);
			activeSession.setLoginTime(timestamp);
			userSessionDAO.saveOrUpdate(activeSession);
			tokenModel = generateToken(activeSession);
			if (newLoggedIn) {
				loginHistory = new LoginAttemptHistoryEntity();
				loginHistory.setAadharNumber(citizen.getUid_num());
				loginHistory.setCreatedBy(citizen.getUid_num());
				loginHistory.setCreatedOn(timestamp);
				loginHistory.setKeyType(getKeyType());
				loginHistory.setLoginTime(timestamp);
				loginHistory.setModifiedBy(citizen.getUid_num());
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
			applicationDAO.saveOrUpdate(citizenApplicationEntity);
			loginAttemptsDAO.saveOrUpdate(loginHistory);
			return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
		} else {
			log.info("aadhar not found : " + getKeyType().toString() + " " + uniqueKey);
			throw new DataMismatchException("Aadhaar is not found for " + getKeyType().toString() + " " + uniqueKey);
		}
	}

	protected KeyType getKeyType() {
		return KeyType.USERREG;
	}
}

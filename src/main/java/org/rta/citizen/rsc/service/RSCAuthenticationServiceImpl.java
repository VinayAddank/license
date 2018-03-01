/**
 * 
 */
package org.rta.citizen.rsc.service;

import java.text.ParseException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TheftIntSusType;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author arun.verma
 *
 */
@Service
public class RSCAuthenticationServiceImpl extends AuthenticationService {

	private static final Logger log = Logger.getLogger(RSCAuthenticationServiceImpl.class);

    @Transactional
    @Override
    public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
            HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException, AadharNotFoundException,
            DataMismatchException, NotFoundException, AadharAuthenticationFailedException, VehicleNotFinanced,
            FinancerNotFound, ParseException, ServiceValidationException {
        String uniqueKey = getUniqueKey(citizen);
        Long timestamp = DateUtil.toCurrentUTCTimeStamp();
        String ccoAadhaarNumber = citizen.getUid_num();
        LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(ccoAadhaarNumber, uniqueKey,
                getKeyType(serviceType), DateUtil.getLongDate(DateUtil.extractDateAsString(timestamp), "00:00:00"),
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

        RegistrationServiceResponseModel<ApplicationModel> prDetailsResponse = null;
        try {
            prDetailsResponse = registrationService.getPRDetails(citizen.getPrNumber());
            HttpStatus statusCode = prDetailsResponse.getHttpStatus();
            ApplicationModel applicationModel = null;
            if (statusCode == HttpStatus.OK) {
                applicationModel = prDetailsResponse.getResponseBody();
                if(applicationModel.getPrStatus() == Status.LOCKED){
					log.info("RC is Locked for RC : " + uniqueKey);
					throw new ServiceValidationException(ServiceValidation.RC_LOCKED.getCode(), ServiceValidation.RC_LOCKED.getValue());
				}
                RTAOfficeModel office = applicationModel.getRtaOffice();
                String aadharNumber = applicationModel.getAadharNumber();// we don't need aadhaar number mapped to RC for this service
                if (!ObjectsUtil.isNull(ccoAadhaarNumber)) {
                    CustomerDetailsRequestModel cdrm = applicationModel.getCustomerDetails();
                    if (!ObjectsUtil.isNull(cdrm)) {
                        citizen.setPincode(cdrm.getTemp_pincode());
                        /*if (authenticateAadhar) {
                            aadharAuthentication(citizen);
                        }*/
                        TokenModel tokenModel;
                        UserSessionEntity activeSession = userSessionDAO.getActiveSession(ccoAadhaarNumber,
                                uniqueKey, getKeyType(serviceType), serviceType);
                        if (ObjectsUtil.isNull(activeSession)) {
                            activeSession = new UserSessionEntity();
                            activeSession.setAadharNumber(ccoAadhaarNumber);
                            activeSession.setCompletionStatus(Status.FRESH.getValue());
                            activeSession.setCreatedBy(ccoAadhaarNumber);
                            activeSession.setCreatedOn(timestamp);
                            activeSession.setKeyType(getKeyType(serviceType));
                            activeSession.setServiceCode(serviceType.getCode());
                            activeSession.setUniqueKey(uniqueKey);
                        }
                        pendingServiceExists(serviceType, activeSession);
                        validate(serviceType, uniqueKey, aadharNumber, getKeyType());
                        activeSession.setModifiedBy(ccoAadhaarNumber);
                        activeSession.setModifiedOn(timestamp);
                        activeSession.setLoginTime(timestamp);
                        activeSession.setVehicleRcId(applicationModel.getVehicleRcId());
                        userSessionDAO.saveOrUpdate(activeSession);
                        tokenModel = generateToken(activeSession);
                        if (newLoggedIn) {
                            loginHistory = new LoginAttemptHistoryEntity();
                            loginHistory.setAadharNumber(ccoAadhaarNumber);
                            loginHistory.setCreatedBy(ccoAadhaarNumber);
                            loginHistory.setCreatedOn(timestamp);
                            loginHistory.setKeyType(getKeyType(serviceType));
                            loginHistory.setLoginTime(timestamp);
                            loginHistory.setModifiedBy(ccoAadhaarNumber);
                            loginHistory.setModifiedOn(timestamp);
                            loginHistory.setUniqueKey(uniqueKey);
                        }
                        loginHistory.setLoginCount(loginCount);

                        // create application if not exists
                        ApplicationEntity citizenApplicationEntity =
                                applicationDAO.getApplicationFromSession(activeSession.getSessionId());
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
                        citizenApplicationEntity
                                .setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
                        citizenApplicationEntity.setLoginHistory(activeSession);
                        citizenApplicationEntity.setRtaOfficeCode(office.getCode());
                        applicationDAO.saveOrUpdate(citizenApplicationEntity);
                        loginAttemptsDAO.saveOrUpdate(loginHistory);
                        tokenModel.setAppNumber(citizenApplicationEntity.getApplicationNumber());
                        return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
                    }
                } else {
                    log.info("CCO aadhaar not found " + getKeyType(serviceType).toString() + " " + uniqueKey);
                    throw new DataMismatchException(
                            "CCO aadhaar is not found " + getKeyType(serviceType).toString() + " " + uniqueKey);
                }
            }
        } catch (HttpClientErrorException e) {
            log.info("error in registration service response : ", e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Not found " + getKeyType(serviceType).toString() + " " + uniqueKey);
                throw new NotFoundException("Your RC Record not found.");
            }
        }
        throw new UnauthorizedException();
    }

    protected String getUniqueKey(AuthenticationModel citizen) {
        if (!ObjectsUtil.isNull(citizen.getPrNumber())) {
            return citizen.getPrNumber();
        } else {
            throw new IllegalArgumentException("uniqueKey is missing");
        }
    }

    protected KeyType getKeyType(ServiceType service) {
        return KeyType.PR;
    }
    
    protected void pendingServiceExists(ServiceType serviceType, UserSessionEntity activeSession)
			throws UnauthorizedException, ServiceValidationException {
	    UserSessionEntity userSessionEntity = userSessionDAO.getUserSessions(null, activeSession.getUniqueKey(), Status.PENDING, serviceType);
		if (!ObjectsUtil.isNull(userSessionEntity)
				&& ServiceType.getServiceType(activeSession.getServiceCode()) == ServiceType.getServiceType(userSessionEntity.getServiceCode())
				&& !activeSession.getAadharNumber().equals(userSessionEntity.getAadharNumber())) {
			log.error("can't login, another session exist. sessionId : " + activeSession.getSessionId());
			throw new ServiceValidationException(ServiceValidation.SAME_SERVICE_EXIST_WITH_ANOTHER_USER.getCode(),
					"Same service, "
							+ ServiceType.getServiceType(userSessionEntity.getServiceCode()).getLabel()
							+ " with application number : "
							+ applicationDAO.getApplicationFromSession(userSessionEntity.getSessionId())
									.getApplicationNumber()
							+ " is already in process by another user.");
		}
	}
}

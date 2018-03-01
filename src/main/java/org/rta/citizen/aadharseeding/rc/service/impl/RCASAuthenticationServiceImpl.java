/**
 * 
 */
package org.rta.citizen.aadharseeding.rc.service.impl;

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
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.rsc.service.RSCAuthenticationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author arun.verma
 *
 */
@Service
public class RCASAuthenticationServiceImpl extends AuthenticationService{

	private static final Logger log = Logger.getLogger(RSCAuthenticationServiceImpl.class);
    
    @Transactional
    @Override
    public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
            HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException, AadharNotFoundException,
            DataMismatchException, NotFoundException, AadharAuthenticationFailedException, VehicleNotFinanced,
            FinancerNotFound, ParseException, ServiceValidationException {
        String uniqueKey = getUniqueKey(citizen);
        Long timestamp = DateUtil.toCurrentUTCTimeStamp();
        LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(citizen.getUid_num(), uniqueKey,
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
        
        if(StringsUtil.isNullOrEmpty(citizen.getUid_num())){
            throw new AadharNotFoundException("Please Provide Aadhar Number !!!");
        }
        if(StringsUtil.isNullOrEmpty(citizen.getChassisNumber())){
            throw new NotFoundException("Please Provide Chassis Number !!!");
        }
        RegistrationServiceResponseModel<ApplicationModel> prDetailsResponse = null;
        try {
            prDetailsResponse = registrationService.getPRDetails(citizen.getPrNumber());
            HttpStatus statusCode = prDetailsResponse.getHttpStatus();
            ApplicationModel applicationModel = null;
            if (statusCode == HttpStatus.OK) {
                applicationModel = prDetailsResponse.getResponseBody();
                
                RTAOfficeModel office = applicationModel.getRtaOffice();
                String aadharNumber = applicationModel.getAadharNumber();
                if(!StringsUtil.isNullOrEmpty(aadharNumber)){
                    if(aadharNumber.equalsIgnoreCase(citizen.getUid_num()) || applicationModel.isAadharVerified()){
                        throw new ServiceValidationException(ServiceValidation.AADHAR_ALREADY_SEEDED_WITH_RC.getCode(), ServiceValidation.AADHAR_ALREADY_SEEDED_WITH_RC.getValue());
                    }
                }
                if(!applicationModel.getVehicleModel().getChassisNumber().equalsIgnoreCase(citizen.getChassisNumber())){
                    throw new ServiceValidationException(ServiceValidation.RC_NOT_MATCHED_CHASSI.getCode(), ServiceValidation.RC_NOT_MATCHED_CHASSI.getValue());
                }
                CustomerDetailsRequestModel cdrm = applicationModel.getCustomerDetails();
                if (!ObjectsUtil.isNull(cdrm)) {
                    citizen.setPincode(cdrm.getTemp_pincode());
                    // ------ is service applicable with provided
                    // details -------------
                    boolean isApplicable = validate(serviceType, uniqueKey);
                    if (!isApplicable) {
                        throw new DataMismatchException(
                                "You can not apply this service with provided details. ");
                    }
                    // ----------------------------------------------------------------
                    if (authenticateAadhar) {
                        aadharAuthentication(citizen);
                    }
                    TokenModel tokenModel;
                    UserSessionEntity activeSession =
                            userSessionDAO.getActiveSession(citizen.getUid_num(), uniqueKey, getKeyType(serviceType), serviceType);
                    if (ObjectsUtil.isNull(activeSession)) {
                        activeSession = new UserSessionEntity();
                        activeSession.setAadharNumber(citizen.getUid_num());
                        activeSession.setCompletionStatus(Status.FRESH.getValue());
                        activeSession.setCreatedBy(citizen.getUid_num());
                        activeSession.setCreatedOn(timestamp);
                        activeSession.setKeyType(getKeyType(serviceType));
                        activeSession.setServiceCode(serviceType.getCode());
                        activeSession.setUniqueKey(uniqueKey);
                        activeSession.setVehicleRcId(applicationModel.getVehicleRcId());
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
                        loginHistory.setKeyType(getKeyType(serviceType));
                        loginHistory.setLoginTime(timestamp);
                        loginHistory.setModifiedBy(citizen.getUid_num());
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
                    citizenApplicationEntity.setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
                    citizenApplicationEntity.setLoginHistory(activeSession);
                    citizenApplicationEntity.setRtaOfficeCode(office.getCode());
                    applicationDAO.saveOrUpdate(citizenApplicationEntity);
                    loginAttemptsDAO.saveOrUpdate(loginHistory);
                    return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
                }
            }
        } catch (HttpClientErrorException e) {
            log.info("error in registration service response : ", e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error("Not found " + getKeyType(serviceType).toString() + " " + uniqueKey);
                throw new NotFoundException("Your RC Record not found.");
            }
        }
        throw new UnauthorizedException();
    }

    protected String getUniqueKey(AuthenticationModel citizen) {
        if(!ObjectsUtil.isNull(citizen.getPrNumber())){
            return citizen.getPrNumber();
        } else {
            throw new IllegalArgumentException("UniqueKey is missing");
        }
    }
    
    protected KeyType getKeyType(ServiceType service) {
        return KeyType.PR;
    }
    
    private boolean validate(ServiceType serviceType, String uniqueKey)
            throws VehicleNotFinanced, FinancerNotFound, UnauthorizedException {
        return true;
    }
}
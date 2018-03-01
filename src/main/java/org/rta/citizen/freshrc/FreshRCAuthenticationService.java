package org.rta.citizen.freshrc;

import java.text.ParseException;
import java.util.HashMap;

import javax.transaction.Transactional;

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
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FreshRCAuthenticationService extends AuthenticationService {
    
	private static final Logger log = Logger.getLogger(FreshRCAuthenticationService.class);

    @Autowired
    private ApplicationFormDataService applicationFormDataService;
    
    @Value("${activiti.citizen.iteration.max}")
    private Integer maxIterationAllowed;
    
    @Override
    @Transactional
    public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
            HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException, AadharNotFoundException,
            DataMismatchException, NotFoundException, AadharAuthenticationFailedException, VehicleNotFinanced,
            FinancerNotFound, ParseException, ServiceValidationException {
        String uniqueKey = getUniqueKey(citizen);
        KeyType keyType = citizen.getKeyType();
        Long timestamp = DateUtil.toCurrentUTCTimeStamp();
        LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(citizen.getAadhaarNumber(), uniqueKey,
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
        RegistrationServiceResponseModel<ApplicationModel> prDetailsResponse = null;
        String aadharNumber = null;
        try {
        	prDetailsResponse = registrationService.getDetails(uniqueKey, keyType);
            if (ObjectsUtil.isNull(prDetailsResponse)) {
            	log.error(serviceType + ", can't get details");
            	throw new UnauthorizedException("unauthorized");
            }
            HttpStatus statusCode = prDetailsResponse.getHttpStatus();
            ApplicationModel applicationModel = null;
            if (statusCode == HttpStatus.OK) {
                applicationModel = prDetailsResponse.getResponseBody();
                if(applicationModel.getPrStatus() == Status.LOCKED){
					log.info("RC is Locked for RC : " + uniqueKey);
					throw new ServiceValidationException(ServiceValidation.RC_LOCKED.getCode(), ServiceValidation.RC_LOCKED.getValue());
					}
                if(applicationModel.isIncompleteData()){
					log.info("Incomplete Data for RC " + uniqueKey);
					throw new ServiceValidationException(ServiceValidation.RC_INCOMPLETE_DATA.getCode(), ServiceValidation.RC_INCOMPLETE_DATA.getValue());
				}
                RTAOfficeModel office = applicationModel.getRtaOffice();
                // aadhar number of Financer
                aadharNumber = citizen.getAadhaarNumber();
                if (!StringsUtil.isNullOrEmpty(aadharNumber)) {
//                    CustomerDetailsRequestModel cdrm = applicationModel.getCustomerDetails();
//                    if (!ObjectsUtil.isNull(cdrm)) {
//                        citizen.setPincode(cdrm.getTemp_pincode());
                    // ------ is service applicable with provided
                    // details -------------
//                        validate(serviceType, uniqueKey, aadharNumber, getKeyType());
                    /*if (!isApplicable) {
                        throw new DataMismatchException(
                                "You can not apply this service with provided details. ");
                    }*/
                    // ----------------------------------------------------------------
                    /*if (authenticateAadhar) {
                        aadharAuthentication(citizen);
                    }*/
                    TokenModel tokenModel;
                    UserSessionEntity activeSession = userSessionDAO.getActiveSession(aadharNumber, uniqueKey,
                            keyType, serviceType);
                    if (ObjectsUtil.isNull(activeSession)) {
                        activeSession = new UserSessionEntity();
                        activeSession.setAadharNumber(aadharNumber);
                        activeSession.setCompletionStatus(Status.FRESH.getValue());
                        activeSession.setCreatedBy(aadharNumber);
                        activeSession.setCreatedOn(timestamp);
                        activeSession.setKeyType(keyType);
                        activeSession.setServiceCode(serviceType.getCode());
                        activeSession.setUniqueKey(uniqueKey);
                        activeSession.setVehicleRcId(applicationModel.getVehicleRcId());
                    }
                    pendingServiceExists(serviceType, activeSession);
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
                        citizenApplicationEntity.setApplicationNumber(applicationService.generateApplicationNumber(userSessionConverter.converToModel(activeSession)));
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
                    tokenModel.setAppNumber(citizenApplicationEntity.getApplicationNumber());
                    return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
                } else {
                    log.info("aadhar " + aadharNumber + " not mapped to this " + getKeyType().toString() + " " + uniqueKey);
                    throw new ServiceValidationException(ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getCode(), ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getValue());
                }
            } else {
                log.info("Some Error occured while calling registration : " + getKeyType().toString() + " " + uniqueKey);
                throw new DataMismatchException("Some Error Occured. Please try again.");
            }
        } catch (HttpClientErrorException e) {
            log.info("error in registration service response : ", e);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info("Not found " + getKeyType().toString() + " " + uniqueKey);
                throw new NotFoundException("Your RC Record not found.");
            }
        }/* catch (ServiceValidationException e) {
            log.info("error in registration service response : ", e);
            throw e;
        }*/
        throw new UnauthorizedException();
    }

    @Override
    protected void pendingServiceExists(ServiceType serviceType, UserSessionEntity activeSession)
            throws UnauthorizedException, ServiceValidationException {
        
        ServiceCategory serviceCategory = ServiceUtil.getServiceCategory(serviceType);
        
        UserSessionEntity theftSession = userSessionDAO.getTheftUserSession(activeSession.getUniqueKey(), KeyType.PR);
        if (!ObjectsUtil.isNull(theftSession)) {
            try {
                ResponseModel<ApplicationFormDataModel> res = applicationFormDataService.getApplicationFormDataBySessionId(theftSession.getSessionId(), FormCodeType.TI_FORM.getLabel());
                ObjectMapper mapper = new ObjectMapper();
                TheftIntimationRevocationModel theftModel = mapper.readValue(res.getData().getFormData(), TheftIntimationRevocationModel.class);
                if(theftModel.getTheftStatus() == TheftIntSusType.FRESH){
                    log.error("can't login, theft intimation found : " + theftSession.getSessionId());
                    throw new ServiceValidationException(ServiceValidation.THEFT_OBJECTION_FOUND.getCode(), ServiceValidation.THEFT_OBJECTION_FOUND.getValue()); 
                }
            } catch(Exception e){
                log.error("can't login, : " + theftSession.getSessionId() + " message :" + e.getMessage());
                throw new ServiceValidationException(ServiceValidation.THEFT_OBJECTION_FOUND.getCode(), ServiceValidation.THEFT_OBJECTION_FOUND.getValue()); 
            }
        }
        
        UserSessionEntity rejectedUserSessionEntity = userSessionDAO.getUserSession(activeSession.getAadharNumber(), activeSession.getUniqueKey(), activeSession.getKeyType(), ServiceType.getServiceType(activeSession.getServiceCode()), Status.REJECTED);
        if(!ObjectsUtil.isNull(rejectedUserSessionEntity) && !(serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION || serviceType == ServiceType.DIFFERENTIAL_TAX
                || serviceType == ServiceType.FC_RENEWAL || serviceType == ServiceType.FC_FRESH || serviceType == ServiceType.FC_OTHER_STATION || serviceType == ServiceType.FC_RE_INSPECTION_SB)){
            ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(rejectedUserSessionEntity.getSessionId());
            if(!ObjectsUtil.isNull(appEntity) && (ObjectsUtil.isNull(appEntity.getIteration()) ? 1 : appEntity.getIteration()) <= maxIterationAllowed){
                log.info("An other application in rejected state with same credential sessionId : " + rejectedUserSessionEntity.getSessionId());
                throw new ServiceValidationException(ServiceValidation.REJECTED_APP_EXIST.getCode(), ServiceValidation.REJECTED_APP_EXIST.getValue());
            }
        }
    }

    @Override
    protected TokenModel generateToken(UserSessionEntity activeSession) {
        // TODO Auto-generated method stub
        return super.generateToken(activeSession);
    }

    @Override
    protected String getUniqueKey(AuthenticationModel citizen) {
        return citizen.getKeyType() == KeyType.PR ? citizen.getPrNumber() : citizen.getTrNumber();
    }

    @Override
    protected KeyType getKeyType() {
        // TODO Auto-generated method stub
        return super.getKeyType();
    }

    @Override
    public String generateApplicationNumber() {
        // TODO Auto-generated method stub
        return super.generateApplicationNumber();
    }

    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        // TODO Auto-generated method stub
        return super.validate(serviceType, uniqueKey, aadharNumber, keyType);
    }

}

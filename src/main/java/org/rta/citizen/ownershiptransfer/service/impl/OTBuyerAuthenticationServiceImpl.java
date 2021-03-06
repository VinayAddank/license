/**
 * 
 */
package org.rta.citizen.ownershiptransfer.service.impl;

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
import org.rta.citizen.common.model.AadharModel;
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
import org.rta.citizen.ownershiptransfer.dao.OTTokenDAO;
import org.rta.citizen.ownershiptransfer.entity.OTTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author arun.verma
 *
 */

@Service
public class OTBuyerAuthenticationServiceImpl extends AuthenticationService {

	private static final Logger log = Logger.getLogger(OTBuyerAuthenticationServiceImpl.class);
    
    @Autowired
    private OTTokenDAO oTTokenDAO;

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
        
        if(serviceType == ServiceType.OWNERSHIP_TRANSFER_SALE){
            // authenticate buyer in case of transfer of ownership type sale
            OTTokenEntity oTTokenEntity = oTTokenDAO.getTokenEntity(uniqueKey);
            if(ObjectsUtil.isNull(oTTokenEntity)){
                throw new NotFoundException("Invalid Token Number !!!");
            }
            validate(serviceType, oTTokenEntity.getApplicationEntity().getLoginHistory().getUniqueKey());
            if (authenticateAadhar) {
                aadharAuthentication(citizen);
            }
            if(!(ObjectsUtil.isNull(oTTokenEntity.getClaimantAadhaarNumber()) || oTTokenEntity.getClaimantAadhaarNumber().trim().equals("") 
                    || oTTokenEntity.getClaimantAadhaarNumber().equals(citizen.getUid_num()))){
                log.info("This token is already claimed. old ClaimantAadhaarNumber : " + oTTokenEntity.getClaimantAadhaarNumber() + 
                        " new ClaimantAadhaarNumber: " + citizen.getUid_num() + " Token: " + uniqueKey);
                throw new DataMismatchException("This token is already claimed !!!");
            }
            if(!oTTokenEntity.getIsClaimed()){
                oTTokenEntity.setClaimantAadhaarNumber(citizen.getUid_num());
                oTTokenEntity.setClaimantIp(citizen.getClientIp());
                AadharModel aadhar = null;
                try {
                    RegistrationServiceResponseModel<AadharModel> res = registrationService.getAadharDetails(Long.parseLong(citizen.getUid_num()));
                    if(res.getHttpStatus().equals(HttpStatus.OK)){
                        aadhar = res.getResponseBody();
                        if(!ObjectsUtil.isNull(aadhar)){
                            oTTokenEntity.setClaimantName(aadhar.getName());
                        }
                    }
                } catch (NumberFormatException | UnauthorizedException e) {
                    log.error("Error while calling registration for Aadhar data...");
                }
                oTTokenEntity.setIsClaimed(true);
                oTTokenEntity.setModifiedBy(citizen.getUid_num());
                oTTokenEntity.setModifiedOn(timestamp);
                oTTokenDAO.saveOrUpdate(oTTokenEntity);
            }
            
            param.put(CitizenConstants.CREATE_NEW_PROCESS, false);
            UserSessionEntity activeSession = oTTokenEntity.getApplicationEntity().getLoginHistory();
            activeSession.setModifiedBy(citizen.getAadhaarNumber());
            activeSession.setModifiedOn(timestamp);
            activeSession.setLoginTime(timestamp);
            userSessionDAO.saveOrUpdate(activeSession);
            TokenModel tokenModel = generateToken(activeSession);
            if (newLoggedIn) {
                loginHistory = new LoginAttemptHistoryEntity();
                loginHistory.setAadharNumber(citizen.getAadhaarNumber());
                loginHistory.setCreatedBy(citizen.getAadhaarNumber());
                loginHistory.setCreatedOn(timestamp);
                loginHistory.setKeyType(getKeyType(serviceType));
                loginHistory.setLoginTime(timestamp);
                loginHistory.setModifiedBy(citizen.getAadhaarNumber());
                loginHistory.setModifiedOn(timestamp);
                loginHistory.setUniqueKey(uniqueKey);
            }
            loginHistory.setLoginCount(loginCount);
            ApplicationEntity citizenApplicationEntity = oTTokenEntity.getApplicationEntity();
            citizenApplicationEntity.setModifiedBy(citizen.getAadhaarNumber());
            citizenApplicationEntity.setModifiedOn(timestamp);
            citizenApplicationEntity.setLoginHistory(activeSession);
            applicationDAO.saveOrUpdate(citizenApplicationEntity);
            loginAttemptsDAO.saveOrUpdate(loginHistory);
            return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
        } else {
            RegistrationServiceResponseModel<ApplicationModel> prDetailsResponse = null;
            try {
                prDetailsResponse = registrationService.getPRDetails(citizen.getPrNumber());
                if(serviceType == ServiceType.OWNERSHIP_TRANSFER_DEATH && prDetailsResponse.getResponseBody().getAadharNumber().equals(citizen.getUid_num())){
                    throw new ServiceValidationException(ServiceValidation.AADHAR_CAN_NOT_SAME.getCode(),ServiceValidation.AADHAR_CAN_NOT_SAME.getValue());
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
                    String aadharNumber = applicationModel.getAadharNumber();
                    if (!StringsUtil.isNullOrEmpty(aadharNumber)) {
                        if (!(ObjectsUtil.isNull(aadharNumber) || ObjectsUtil.isNull(citizen.getUid_num()))) {
                            CustomerDetailsRequestModel cdrm = applicationModel.getCustomerDetails();
                            if (!ObjectsUtil.isNull(cdrm)) {
                                citizen.setPincode(cdrm.getTemp_pincode());
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
                                boolean isApplicable = validate(serviceType, uniqueKey);
                                if (!isApplicable) {
                                	throw new DataMismatchException(
                                			"You can not apply this service with provided details. ");
                                }
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
                        } else {
                            log.info("aadhar not mapped to this " + getKeyType(serviceType).toString() + " " + uniqueKey);
                            throw new DataMismatchException(
                                    "Aadhaar is not found for " + getKeyType(serviceType).toString() + " " + uniqueKey);
                        }
                    } else {
                        log.info("aadhar not mapped to this " + getKeyType(serviceType).toString() + " " + uniqueKey);
                        throw new ServiceValidationException(ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getCode(), ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getValue());
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
        
    }

    protected String getUniqueKey(AuthenticationModel citizen) {
        if(!ObjectsUtil.isNull(citizen.getPrNumber())){
            return citizen.getPrNumber();
        } else if(!ObjectsUtil.isNull(citizen.getTokenNumber())){
            return citizen.getTokenNumber();
        } else {
            throw new IllegalArgumentException("uniqueKey is missing");
        }
    }
    
    protected KeyType getKeyType(ServiceType service) {
        if(service == ServiceType.OWNERSHIP_TRANSFER_SALE){
            return KeyType.TOKEN;
        } else {
            return KeyType.PR;
        }
    }
    
    private boolean validate(ServiceType serviceType, String uniqueKey)
            throws VehicleNotFinanced, FinancerNotFound, UnauthorizedException, ServiceValidationException {
        if(serviceType == ServiceType.OWNERSHIP_TRANSFER_SALE){
            RegistrationServiceResponseModel<ApplicationModel> prDetailResponse = registrationService.getPRDetails(uniqueKey);
            if (!ObjectsUtil.isNull(prDetailResponse.getResponseBody()) && prDetailResponse.getResponseBody().getPrStatus().equals(Status.SUSPENDED)) {
                throw new ServiceValidationException(ServiceValidation.RC_SUSPENDED.getCode(), ServiceValidation.RC_SUSPENDED.getValue());
            }
        }
        return true;
    }
}

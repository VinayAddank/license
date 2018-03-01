package org.rta.citizen.common.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.converters.UserSessionConverter;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.LoginAttemptDAO;
import org.rta.citizen.common.dao.UserAttemptLogDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.entity.UserAttemptLogEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.PermitType;
import org.rta.citizen.common.enums.RegistrationCategoryType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TheftIntSusType;
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
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SuspendedRCNumberModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.permit.model.PermitAuthorizationCardModel;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AuthenticationService {

	private static final Logger log = Logger.getLogger(AuthenticationService.class);

	@Value("${jwt.expiration}")
	protected Long expiration;

	@Autowired
	protected UserSessionDAO userSessionDAO;

	@Autowired
	protected RegistrationService registrationService;

	@Autowired
	protected LoginAttemptDAO loginAttemptsDAO;
	
	@Autowired
	protected ApplicationService applicationService;

	@Autowired
	protected UserSessionConverter userSessionConverter;

	@Autowired
	protected ApplicationDAO applicationDAO;
	
	@Autowired
    private UserAttemptLogDAO userAttemptLog;
	
	@Autowired
	protected JwtTokenUtil jwtTokenUtil;

	@Value("${login.attempt.count.max}")
	protected Integer maxAllowedPerDay;

	@Value("${aadhar.authenticate.enabled}")
	protected Boolean authenticateAadhar;
	
	@Value("${activiti.citizen.iteration.max}")
    private Integer maxIterationAllowed;
	
	@Autowired
    private ApplicationFormDataService applicationFormDataService;
	
	protected static final Integer RC_NON_TRANSPORT_VALIDITY_YEARS = 15;
	protected static final Integer RC_TRANSPORT_VALIDITY_YEARS = 7;
	private static final Integer GVW_THRESHOLD_FOR_TRANSPORT_VEHICLES = 3000;
	private static final long DAYS = 365;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq)
			throws UnauthorizedException, ForbiddenException, AadharNotFoundException, DataMismatchException,
			NotFoundException, AadharAuthenticationFailedException, VehicleNotFinanced, FinancerNotFound, ParseException, ServiceValidationException, ConflictException {
		String uniqueKey = getUniqueKey(citizen);
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
				if(applicationModel.isIncompleteData()){
					log.info("Incomplete Data for RC " + uniqueKey);
					throw new ServiceValidationException(ServiceValidation.RC_INCOMPLETE_DATA.getCode(), ServiceValidation.RC_INCOMPLETE_DATA.getValue());
				}
				RTAOfficeModel office = applicationModel.getRtaOffice();
				String aadharNumber = applicationModel.getAadharNumber();
				if (!StringsUtil.isNullOrEmpty(aadharNumber)) {
					if (aadharNumber.equals(citizen.getUid_num())) {
						CustomerDetailsRequestModel cdrm = applicationModel.getCustomerDetails();
						if (!ObjectsUtil.isNull(cdrm)) {
							citizen.setPincode(cdrm.getTemp_pincode());
							if (authenticateAadhar && (serviceType == null || serviceType != ServiceType.PAY_TAX)) {
								aadharAuthentication(citizen);
							}
							TokenModel tokenModel;
							UserSessionEntity activeSession = userSessionDAO.getActiveSession(aadharNumber, uniqueKey,
									getKeyType(), serviceType);
							if (ObjectsUtil.isNull(activeSession)) {
								activeSession = new UserSessionEntity();
								activeSession.setAadharNumber(aadharNumber);
								activeSession.setCompletionStatus(Status.FRESH.getValue());
								activeSession.setCreatedBy(aadharNumber);
								activeSession.setCreatedOn(timestamp);
								activeSession.setKeyType(getKeyType());
								activeSession.setServiceCode(serviceType.getCode());
								activeSession.setUniqueKey(uniqueKey);
                                activeSession.setVehicleRcId(applicationModel.getVehicleRcId());
							}
							pendingServiceExists(serviceType, activeSession);
							validate(serviceType, uniqueKey, aadharNumber, getKeyType());
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
								loginHistory.setKeyType(getKeyType());
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
							if(serviceType == ServiceType.FC_RE_INSPECTION_SB){
							    //in case of fitness re-inspection assign the rta office code of rejected app of fitness fresh/renewal/renewalotherstation
							    List<String> serviceTypes = new ArrayList<>();
		                        serviceTypes.add(ServiceType.FC_FRESH.getCode());
		                        serviceTypes.add(ServiceType.FC_RENEWAL.getCode());
		                        serviceTypes.add(ServiceType.FC_RE_INSPECTION_SB.getCode());
		                        serviceTypes.add(ServiceType.FC_OTHER_STATION.getCode());
		                        List<Integer> status = new ArrayList<>();
		                        status.add(Status.REJECTED.getValue());
		                        UserSessionEntity lastRejSession = userSessionDAO.getLastRejectedApprovedSession(uniqueKey, serviceTypes, status);
		                        citizenApplicationEntity.setRtaOfficeCode(applicationDAO.getApplicationFromSession(lastRejSession.getSessionId()).getRtaOfficeCode());
							} else {
							    citizenApplicationEntity.setRtaOfficeCode(office.getCode());
							}
							
							applicationDAO.saveOrUpdate(citizenApplicationEntity);
							loginAttemptsDAO.saveOrUpdate(loginHistory);
							return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
						}
					} else {
						log.info("aadhar not mapped to this " + getKeyType().toString() + " " + uniqueKey);
						throw new ServiceValidationException(ServiceValidation.AADHAR_MISMATCH_WITH_RC.getCode(), ServiceValidation.AADHAR_MISMATCH_WITH_RC.getValue());
					}
				} else {
					log.info("aadhar not mapped to this " + getKeyType().toString() + " " + uniqueKey);
					throw new ServiceValidationException(ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getCode(), ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getValue());
				}
			}
		} catch (HttpClientErrorException e) {
		    log.error("error in registration service response : " + e.getMessage());
			log.debug("error in registration service response : ", e);
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				log.info("Not found " + getKeyType().toString() + " " + uniqueKey);
				throw new NotFoundException("Your RC Record not found.");
			}
		} catch (ServiceValidationException e) {
		    log.error("ServiceValidationException : " + e.getErrorMsg());
		    log.debug("ServiceValidationException : ", e);
            throw e;
        }
		throw new UnauthorizedException();
	}

	protected void pendingServiceExists(ServiceType serviceType, UserSessionEntity activeSession)
			throws UnauthorizedException, ServiceValidationException {
	    if(serviceType == ServiceType.PAY_TAX){
	        //-- do not check pending service etc for pay tax---------
	        return;
	    }
	    ServiceCategory serviceCategory = ServiceUtil.getServiceCategory(serviceType);
	    
	    // validation for parallel pending applications for a Unique Key and Aadhar Number
	    // validation only applicable for Permit, Fitness and Registration Services
	    
	    // as per RTA-2365, Permit and Fitness services should be allowed in parallel
	    if (serviceCategory == ServiceCategory.PERMIT_FITNESS_CATEGORY || serviceCategory == ServiceCategory.REG_CATEGORY) {
	        
	        if (serviceType != ServiceType.FC_REVOCATION_CFX && serviceType != ServiceType.THEFT_INTIMATION) {
	            try {
	                RegistrationServiceResponseModel<ApplicationModel> ap = registrationService.getPRDetails(activeSession.getUniqueKey());
                    if (ap.getHttpStatus() == HttpStatus.OK) {
                        ApplicationModel app = ap.getResponseBody();
                        if (app !=null) {
                            FcDetailsModel fdm = app.getFitnessCertificateDetails();
                            if (fdm !=null && fdm.getSuspended()) {
                                log.info("cfx issued for prnumebr :  " + activeSession.getUniqueKey());
                                throw new ServiceValidationException(ServiceValidation.CFX_ISSUED.getCode(), ServiceValidation.CFX_ISSUED.getValue());
                            }
                        }
                    }
	            } catch (Exception e) {
	                log.error("error getting cfx details for pr :  " + activeSession.getUniqueKey());
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
	        
	        if (serviceType != ServiceType.THEFT_INTIMATION) {
                UserSessionEntity theftSession = userSessionDAO.getLastTheftUserSession(activeSession.getAadharNumber(), activeSession.getUniqueKey(), KeyType.PR);
                if (!ObjectsUtil.isNull(theftSession)) {
                    try{
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
                
                if(!(serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION || serviceType == ServiceType.REGISTRATION_CANCELLATION || serviceType == ServiceType.FC_ISSUE_CFX)){
                    if (serviceType != ServiceType.NOC_ISSUE) {
                        UserSessionEntity existingSessions = userSessionDAO.getUserSession(activeSession.getAadharNumber(), activeSession.getUniqueKey(), KeyType.PR, ServiceType.NOC_ISSUE, Status.PENDING);
                        if (!ObjectsUtil.isNull(existingSessions)) {
                            log.error("can't login, Noc is issued. sessionId : " + existingSessions.getSessionId());
                            throw new ServiceValidationException(ServiceValidation.NOC_ISSUED.getCode(), ServiceValidation.NOC_ISSUED.getValue()); 
                        }
                    }
                    UserSessionEntity userSessionEntity = userSessionDAO.getUserSessions(null, activeSession.getUniqueKey(), Status.PENDING, null);
					if (!ObjectsUtil.isNull(userSessionEntity)
							&& ServiceType.getServiceType(activeSession.getServiceCode()) != ServiceType.getServiceType(userSessionEntity.getServiceCode())) {
						log.error("can't login, another session exist. sessionId : " + activeSession.getSessionId());
						throw new ServiceValidationException(ServiceValidation.ANOTHER_SERVICE_EXISTS.getCode(),
								"Another service, "
										+ ServiceType.getServiceType(userSessionEntity.getServiceCode()).getLabel()
										+ " with application number : "
										+ applicationDAO.getApplicationFromSession(userSessionEntity.getSessionId())
												.getApplicationNumber()
										+ " is already in process.");
					}
                }
            }
	    }
	        
	}

	protected TokenModel generateToken(UserSessionEntity activeSession) {
		return new TokenModel(jwtTokenUtil.generateToken(new UserSessionModel(activeSession.getSessionId(),
				activeSession.getAadharNumber(), Status.getStatus(activeSession.getCompletionStatus()), activeSession.getUniqueKey(),
				ServiceType.getServiceType(activeSession.getServiceCode()))));
	}

	/**
	 * Returns Unique Key value based on service type. Default is PR number.
	 * Must override this method, For each new implementation of authenticate
	 * method.
	 */
	protected String getUniqueKey(AuthenticationModel citizen) {
		String prNumber = citizen.getPrNumber();
		if (ObjectsUtil.isNull(prNumber)) {
			throw new IllegalArgumentException("prNumber is missing");
		}
		return prNumber;
	}

	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + expiration * 1000);
	}

	/**
	 * Returns the KeyType for the service. default is PR. Must override this
	 * method, For each new implementation of authenticate method.
	 */
	protected KeyType getKeyType() {
		return KeyType.PR;
	}

	protected AadharModel aadharAuthentication(AuthenticationModel authenticationModel)
			throws AadharAuthenticationFailedException, UnauthorizedException {
		RegistrationServiceResponseModel<AadharModel> aadharResponse = registrationService
				.aadharAuthentication(authenticationModel);
		if (ObjectsUtil.isNull(aadharResponse) || aadharResponse.getHttpStatus() != HttpStatus.OK) {
			log.error("eKYC authentication failed");
			throw new AadharAuthenticationFailedException();
		}
		AadharModel aadharModel = aadharResponse.getResponseBody();
		if (aadharModel.getAuth_status().equalsIgnoreCase("SUCCESS")) {
			return aadharModel;
		} else if (aadharModel.getAuth_status().equalsIgnoreCase("FAILED")) {
			throw new AadharAuthenticationFailedException(aadharModel);
		}
		throw new AadharAuthenticationFailedException(aadharModel);
	}

	public String generateApplicationNumber() {
		return null;
	}

	protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType) throws ServiceValidationException, UnauthorizedException {
	    if(serviceType == ServiceType.PAY_TAX){
	        // no validation for pay tax RTA-3280 --------
	        return true;
	    } else if(serviceType == ServiceType.THEFT_INTIMATION){
	      //---validate only NOC for TI--------------
            RegistrationServiceResponseModel<NocDetails> res = registrationService.getNocDetails(null, uniqueKey);
            if (res.getHttpStatus().equals(HttpStatus.OK)) {
                NocDetails noc = res.getResponseBody();
                if(!ObjectsUtil.isNull(noc) && noc.getStatus()){
                    log.error("NOC is found (found in registration) for : " + uniqueKey);
                    throw new ServiceValidationException(ServiceValidation.NOC_ISSUED.getCode(), ServiceValidation.NOC_ISSUED.getValue());
                }
            }
	    } else {
	        validatePR(uniqueKey, serviceType);
	    }
		return true;
	}
	
	protected boolean validatePR(String uniqueKey, ServiceType serviceType) throws UnauthorizedException, ServiceValidationException {
	    boolean isPRValidation = true;
	    RegistrationServiceResponseModel<ApplicationModel> prDetailsResponse = null;
	    try {
	        prDetailsResponse = registrationService.getPRDetails(uniqueKey);
	        HttpStatus statusCode = prDetailsResponse.getHttpStatus();
            ApplicationModel applicationModel = null;
            if (statusCode == HttpStatus.OK) {
                applicationModel = prDetailsResponse.getResponseBody();
                if (applicationModel.getPrStatus() != Status.STOPPAGE_TAX && serviceType == ServiceType.STOPPAGE_TAX_REVOCATION ) {
                    throw new ServiceValidationException(ServiceValidation.VEHICLE_IS_NOT_STOPPAGE_TAX.getCode(), ServiceValidation.VEHICLE_IS_NOT_STOPPAGE_TAX.getValue());
                } else if (applicationModel.getPrStatus() == Status.STOPPAGE_TAX && serviceType == ServiceType.STOPPAGE_TAX_REVOCATION ) {
                    return isPRValidation;
                }
                if(ServiceUtil.getServiceCategory(serviceType) == ServiceCategory.PERMIT_FITNESS_CATEGORY){
                    if(!applicationModel.getRegistrationCategory().getCode().equalsIgnoreCase("T")){
                        log.error("pr: " + uniqueKey + " is not transport");
                        throw new ServiceValidationException(ServiceValidation.ONLY_TRANSPORT_VEHICLE_IS_ALLOWED.getCode(),
                                ServiceValidation.ONLY_TRANSPORT_VEHICLE_IS_ALLOWED.getValue());
                    }
                    
                    if (serviceType == ServiceType.PERMIT_VARIATIONS) {
						RegistrationServiceResponseModel<List<PermitHeaderModel>> permitDetailList = registrationService
								.getPermitDetails(applicationModel.getVehicleRcId());
						if(ObjectsUtil.isNull(permitDetailList.getResponseBody()) || permitDetailList.getResponseBody().size()==0){
							throw new ServiceValidationException(
									ServiceValidation.PERMIT_NOT_FOUND.getCode(),
									ServiceValidation.PERMIT_NOT_FOUND.getValue());
						}
						for (PermitHeaderModel headerModel : permitDetailList.getResponseBody()) {
							if (!headerModel.getIsTempPermit()) {
								if (!(PermitType.EIB.getValue().equals(headerModel.getPermitType())
										|| PermitType.PSVP.getValue().equals(headerModel.getPermitType())
										|| (PermitType.AITP.getValue().equals(headerModel.getPermitType())
												&& applicationModel.getVehicleModel().getVehicleSubClass()
														.equalsIgnoreCase("TOVT"))
										|| PermitType.CCP.getValue().equals(headerModel.getPermitType())
										|| PermitType.NP.getValue().equals(headerModel.getPermitType())
										|| PermitType.GCP.getValue().equals(headerModel.getPermitType()))) {
									log.error(
											"variation for pr : " + uniqueKey + " is not application on your Permit.");
									throw new ServiceValidationException(
											ServiceValidation.PERMIT_VARIATION_NOT_APPLICABLE.getCode(),
											ServiceValidation.PERMIT_VARIATION_NOT_APPLICABLE.getValue());
								}
							}
						}
					}
                    
                    // validate permit for ambulance
                    if ((serviceType == ServiceType.PERMIT_FRESH || serviceType == ServiceType.PERMIT_RENEWAL 
                            || serviceType == ServiceType.PERMIT_SURRENDER || serviceType == ServiceType.PERMIT_RENEWAL_AUTH_CARD 
                            || serviceType == ServiceType.PERMIT_REPLACEMENT_VEHICLE || serviceType == ServiceType.PERMIT_VARIATIONS) 
                            && permitNotAllowed(applicationModel)) {
                        log.error("permit not applicable for vehicle sub class : " + applicationModel.getVehicleModel().getVehicleSubClass());
                        throw new ServiceValidationException(ServiceValidation.PERMIT_NOT_APPLICABLE.getCode(), ServiceValidation.PERMIT_NOT_APPLICABLE.getValue());
                    }
                    
                    // if fitness expired, don't allow permit services
                    if (serviceType == ServiceType.PERMIT_FRESH || serviceType == ServiceType.PERMIT_RENEWAL 
                            || serviceType == ServiceType.PERMIT_SURRENDER || serviceType == ServiceType.PERMIT_RENEWAL_AUTH_CARD 
                            || serviceType == ServiceType.PERMIT_REPLACEMENT_VEHICLE || serviceType == ServiceType.PERMIT_VARIATIONS) {
                        FcDetailsModel fitnessDetais = applicationModel.getFitnessCertificateDetails();
                        if (!ObjectsUtil.isNull(fitnessDetais)) {
                            Long expiryDate = fitnessDetais.getExpiryDate();
                            if (expiryDate < DateUtil.toCurrentUTCTimeStamp()) {
                                log.error("fitness for pr : " + uniqueKey + " is expired. Please use Fitness Renewal Service.");
                                throw new ServiceValidationException(ServiceValidation.FITNESS_EXPIRED.getCode(),
                                        ServiceValidation.FITNESS_EXPIRED.getValue());
                            }
                        }
                    }
                    
                    // validate fitness details
                    FcDetailsModel fitnessDetais = applicationModel.getFitnessCertificateDetails();
                    if (!ObjectsUtil.isNull(fitnessDetais)) {
                        if (fitnessDetais.getSuspended() && !(serviceType == ServiceType.THEFT_INTIMATION || serviceType == ServiceType.PAY_TAX
                                || serviceType == ServiceType.REGISTRATION_CANCELLATION || serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION
                                || serviceType == ServiceType.FC_REVOCATION_CFX)) {
                            log.error("fitness for pr : " + uniqueKey
                                    + " has been suspended. Please use Fitness Revocation Service.");
                            throw new ServiceValidationException(ServiceValidation.FITNESS_SUSPENDED.getCode(),
                                    ServiceValidation.FITNESS_SUSPENDED.getValue());
                        }
                    }
                    if (serviceType == ServiceType.FC_FRESH && !ObjectsUtil.isNull(fitnessDetais)) {
                        Long expiryDate = fitnessDetais.getExpiryDate();
                        if (expiryDate > DateUtil.toCurrentUTCTimeStamp()) {
                            log.error("fitness for pr : " + uniqueKey + " is already exists.");
                            throw new ServiceValidationException(ServiceValidation.FITNESS_ALREADY_EXISTS.getCode(),
                                    ServiceValidation.FITNESS_ALREADY_EXISTS.getValue());
                        } else {
                            log.error("fitness for pr : " + uniqueKey
                                    + " is expired. Please use Fitness Renewal Service.");
                            throw new ServiceValidationException(ServiceValidation.FITNESS_EXPIRED.getCode(),
                                    ServiceValidation.FITNESS_EXPIRED.getValue());
                        }
                    }
                    if (serviceType == ServiceType.FC_REVOCATION_CFX) {
                        if(ObjectsUtil.isNull(fitnessDetais)){
                            log.error("fitness for pr : " + uniqueKey + " not found.");
                            throw new ServiceValidationException(ServiceValidation.FITNESS_NOT_FOUND.getCode(),
                                    ServiceValidation.FITNESS_NOT_FOUND.getValue());
                        }
                        if (ObjectsUtil.isNull(fitnessDetais.getSuspended()) || !fitnessDetais.getSuspended()) {
                            log.error("fitness for pr : " + uniqueKey + " hasn't been suspended yet.");
                            throw new ServiceValidationException(ServiceValidation.FITNESS_NOT_SUSPENDED.getCode(),
                                    ServiceValidation.FITNESS_NOT_SUSPENDED.getValue());
                        }
                    }
                    if (serviceType == ServiceType.FC_RENEWAL
                            || serviceType == ServiceType.FC_OTHER_STATION 
                            || serviceType == ServiceType.FC_RE_INSPECTION_SB) {
                        if (!ObjectsUtil.isNull(fitnessDetais)) {
                            Long expiryDate = fitnessDetais.getExpiryDate();
                            // DateUtil.addDays(DateUtil.toCurrentUTCTimeStamp(), 30) > expiryDate
                            if (!DateUtil.isSameOrGreaterDate(DateUtil.addDays(DateUtil.toCurrentUTCTimeStamp(), 30), expiryDate)) {
                                log.error("fitness for pr : " + uniqueKey + " is already exists.");
                                throw new ServiceValidationException(ServiceValidation.FITNESS_ALREADY_EXISTS.getCode(),
                                        ServiceValidation.FITNESS_ALREADY_EXISTS.getValue());
                            }
                        } else {
                            log.error("fitness not found for pr : " + uniqueKey);
                            throw new ServiceValidationException(ServiceValidation.FITNESS_NOT_FOUND.getCode(),
                                    ServiceValidation.FITNESS_NOT_FOUND.getValue());
                        }
                    }
                    /*
                     * RTA-3314  Allow citizen to apply for fitness renewal if green tax is pending 
                     * 
                     * if(serviceType == ServiceType.FC_RENEWAL){
                        if(DateUtil.getDateDiff(DateUtil.toCurrentUTCTimeStamp(), applicationModel.getGreenTaxValidTo()) < 0){
                            log.error("green tax expired ... valid to : " + applicationModel.getGreenTaxValidTo() + "  pr " + uniqueKey);
                            throw new ServiceValidationException(ServiceValidation.GREEN_TAX_EXPIRED.getCode(),
                                    ServiceValidation.GREEN_TAX_EXPIRED.getValue());
                        }
                    }*/
                    /*
                     * Do not allow to fitness renewal  for CFRR issued RC's
                     * 
                     **/
                    if(serviceType == ServiceType.FC_FRESH || serviceType == ServiceType.FC_RENEWAL || serviceType == ServiceType.FC_OTHER_STATION
                    		|| serviceType == ServiceType.FC_RE_INSPECTION_SB){
                    	List<String> serviceTypes = new ArrayList<>();
                    	serviceTypes.add(ServiceType.FC_FRESH.getCode());
                    	serviceTypes.add(ServiceType.FC_RENEWAL.getCode());
                    	serviceTypes.add(ServiceType.FC_RE_INSPECTION_SB.getCode());
                    	serviceTypes.add(ServiceType.FC_OTHER_STATION.getCode());
                    	List<Integer> status = new ArrayList<>();
                    	status.add(Status.APPROVED.getValue());
                    	status.add(Status.REJECTED.getValue());
                    	UserSessionEntity userSessionEntity=userSessionDAO.getLastRejectedApprovedSession(uniqueKey, serviceTypes, status);
                       if(!ObjectsUtil.isNull(userSessionEntity)){
                            if(Status.getStatus(userSessionEntity.getCompletionStatus()) == Status.REJECTED && serviceType != ServiceType.FC_RE_INSPECTION_SB){
                            		throw new ServiceValidationException(ServiceValidation.FITNESS_RE_INSPECTION_REQUIRED.getCode(),
                            				ServiceValidation.FITNESS_RE_INSPECTION_REQUIRED.getValue());
                            } else if(Status.getStatus(userSessionEntity.getCompletionStatus()) == Status.APPROVED
                                    && serviceType == ServiceType.FC_RE_INSPECTION_SB){
                            	throw new ServiceValidationException(ServiceValidation.FITNESS_INSPECTION_NOT_APPLICABLE.getCode(),
                        				ServiceValidation.FITNESS_INSPECTION_NOT_APPLICABLE.getValue());
                            }
                         }
                    }
                }
                if(applicationModel.getRegistrationCategory().getCode().equalsIgnoreCase("NT")) {
                    isPRValidation = DateUtil.isSameOrGreaterDate(DateUtil.addYears(applicationModel.getPrIssueTime(), RC_NON_TRANSPORT_VALIDITY_YEARS),
                            DateUtil.toCurrentUTCTimeStamp());
                } else if(applicationModel.getRegistrationCategory().getCode().equalsIgnoreCase("T")) {
                    if (!(serviceType == ServiceType.FC_ISSUE_CFX || serviceType == ServiceType.FC_FRESH || serviceType == ServiceType.FC_OTHER_STATION 
                          || serviceType == ServiceType.FC_RE_INSPECTION_SB || serviceType == ServiceType.FC_REVOCATION_CFX ||
                          serviceType == ServiceType.FC_RENEWAL || serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION
                          || serviceType == ServiceType.SUSPENSION_REVOCATION)) {
                        RegistrationServiceResponseModel<List<PermitHeaderModel>> pRes = registrationService.getPermitDetails(applicationModel.getVehicleRcId());
                        if(pRes.getHttpStatus().equals(HttpStatus.OK) && !ObjectsUtil.isNull(pRes.getResponseBody())
                                && pRes.getResponseBody().size() > 0 && !ObjectsUtil.isNull(pRes.getResponseBody().get(0).getPermitNo())){
                            for(PermitHeaderModel permitModel: pRes.getResponseBody()){
                                if(!permitModel.getIsTempPermit() && (permitModel.getValidToDate().compareTo(DateUtil.toCurrentUTCTimeStamp()) < 0)
                                        && !(serviceType == ServiceType.PERMIT_RENEWAL || serviceType == ServiceType.NOC_ISSUE || serviceType == ServiceType.NOC_CANCELLATION || 
                                                serviceType == ServiceType.THEFT_INTIMATION || serviceType == ServiceType.REGISTRATION_CANCELLATION || serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION 
                                                || serviceType == ServiceType.OWNERSHIP_TRANSFER_AUCTION || serviceType == ServiceType.OWNERSHIP_TRANSFER_DEATH || serviceType == ServiceType.OWNERSHIP_TRANSFER_SALE
                                                || serviceType == ServiceType.ADDRESS_CHANGE || serviceType == ServiceType.DUPLICATE_REGISTRATION || serviceType == ServiceType.HPA || serviceType == ServiceType.HPT
                                                || serviceType == ServiceType.VEHICLE_ATLERATION || serviceType == ServiceType.REGISTRATION_RENEWAL || serviceType == ServiceType.PERMIT_SURRENDER)){
                                    throw new ServiceValidationException(ServiceValidation.PERMIT_EXPIRED.getCode(), ServiceValidation.PERMIT_EXPIRED.getValue());
                                }
                            }
                        } else if(!permitNotAllowed(applicationModel) && !(serviceType == ServiceType.PERMIT_FRESH || serviceType == ServiceType.NOC_ISSUE || serviceType == ServiceType.NOC_CANCELLATION || 
                                serviceType == ServiceType.THEFT_INTIMATION || serviceType == ServiceType.REGISTRATION_CANCELLATION || serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION 
                                || serviceType == ServiceType.OWNERSHIP_TRANSFER_AUCTION || serviceType == ServiceType.OWNERSHIP_TRANSFER_DEATH || serviceType == ServiceType.OWNERSHIP_TRANSFER_SALE
                                || serviceType == ServiceType.ADDRESS_CHANGE || serviceType == ServiceType.DUPLICATE_REGISTRATION || serviceType == ServiceType.HPA || serviceType == ServiceType.HPT
                                || serviceType == ServiceType.VEHICLE_ATLERATION || serviceType == ServiceType.REGISTRATION_RENEWAL) 
                                && !((isGoodsVehicle(applicationModel.getVehicleModel().getVehicleSubClass()) && applicationModel.getVehicleModel().getRlw() <= GVW_THRESHOLD_FOR_TRANSPORT_VEHICLES))){
                            // check permit only for vehicles having more that THRESHOLD GVW for goods vehicle. Non goods vehicle should have permit RTA-1793
                            // without permit can apply address change RTA-2848
                            // without permit Duplicate registration is applicable
                            // without permit HPA and HPT applicable RTA-3176
                            // without permit can apply for SUSPENSION_REVOCATION RTA-2615
                            throw new ServiceValidationException(ServiceValidation.PERMIT_NOT_FOUND.getCode(), ServiceValidation.PERMIT_NOT_FOUND.getValue());
                        } else if(isGoodsVehicle(applicationModel.getVehicleModel().getVehicleSubClass()) && applicationModel.getVehicleModel().getRlw() < GVW_THRESHOLD_FOR_TRANSPORT_VEHICLES &&
                                (serviceType == ServiceType.PERMIT_FRESH || serviceType == ServiceType.PERMIT_RENEWAL || 
                                serviceType == ServiceType.PERMIT_SURRENDER || serviceType == ServiceType.PERMIT_RENEWAL_AUTH_CARD || 
                                serviceType == ServiceType.PERMIT_REPLACEMENT_VEHICLE || serviceType == ServiceType.PERMIT_VARIATIONS)){
                            throw new ServiceValidationException(ServiceValidation.PERMIT_NOT_APPLICABLE.getCode(), ServiceValidation.PERMIT_NOT_APPLICABLE.getValue());
                        }
                        if(serviceType == ServiceType.PERMIT_RENEWAL){
                            for(PermitHeaderModel permitModel: pRes.getResponseBody()){
                                if(!permitModel.getIsTempPermit() && (DateUtil.getDateDiff(DateUtil.toCurrentUTCTimeStamp(), permitModel.getValidToDate()) > 15L)){
                                    throw new ServiceValidationException(ServiceValidation.PERMIT_NOT_EXPIRED.getCode(), ServiceValidation.PERMIT_NOT_EXPIRED.getValue());
                                }
                            }
                        }
                        
                        //Permit Auth card Validations
                        if(serviceType == ServiceType.PERMIT_RENEWAL_AUTH_CARD){
                        	String permitType = "";
                        	Long permitExpiryDate = null;
                        	if(pRes.getHttpStatus().equals(HttpStatus.OK) && !ObjectsUtil.isNull(pRes.getResponseBody()) && pRes.getResponseBody().size() > 0){
                        		for(PermitHeaderModel permitModel: pRes.getResponseBody()){
                        			if (!permitModel.getIsTempPermit()) {
                        				permitType = permitModel.getPermitType();
                        				permitExpiryDate = permitModel.getValidToDate();
                						break;
                					}
                            	}
                        	}
                        	if(!StringsUtil.isNullOrEmpty(permitType)  &&  (permitType.equalsIgnoreCase(PermitType.NP.getValue()) || permitType.equalsIgnoreCase(PermitType.AITC.getValue())
                        			|| permitType.equalsIgnoreCase(PermitType.AITP.getValue()))){
                        		if(!permitType.equalsIgnoreCase(PermitType.NP.getValue())){
                        			RegistrationServiceResponseModel<PermitAuthorizationCardModel> authCardDetail = registrationService.getPermitAuthCardDetails(applicationModel.getPrNumber());
                                    if(authCardDetail.getHttpStatus().equals(HttpStatus.OK) && ObjectsUtil.isNull(authCardDetail.getResponseBody())){
                                        throw new ServiceValidationException(ServiceValidation.PERMIT_AUTH_CARD_NOT_FOUND.getCode(), ServiceValidation.PERMIT_AUTH_CARD_NOT_FOUND.getValue());
                                    }
                                    long numberOfDays = DateUtil.getNumberOfDays(authCardDetail.getResponseBody().getPermitExpiryDate(), DateUtil.addDays(new Date().getTime()/1000, 1));
                                    if( numberOfDays <= DAYS){
                                        throw new ServiceValidationException(ServiceValidation.PRIMARY_PERMIT_AUTH_CARD.getCode(), ServiceValidation.PRIMARY_PERMIT_AUTH_CARD.getValue());
                                    }
                                    if (!DateUtil.isSameOrGreaterDate(DateUtil.addDays(DateUtil.toCurrentUTCTimeStamp(), 15), authCardDetail.getResponseBody().getAuthExpiryDate())) {
                           	    	  throw new ServiceValidationException(ServiceValidation.PERMIT_AUTH_CARD_NOT_UNDER_RENEWAL.getCode(), ServiceValidation.PERMIT_AUTH_CARD_NOT_UNDER_RENEWAL.getValue());
                           	      	}
                        		} else {
                        			long numberOfDays = DateUtil.getNumberOfDays(permitExpiryDate, DateUtil.addDays(new Date().getTime()/1000, 1));
                                    if( numberOfDays <= DAYS){
                                        throw new ServiceValidationException(ServiceValidation.PRIMARY_PERMIT_AUTH_CARD.getCode(), ServiceValidation.PRIMARY_PERMIT_AUTH_CARD.getValue());
                                    }
                        		}
                        	} else {
                        		throw new ServiceValidationException(ServiceValidation.PERMIT_RENEWAL_AUTH_CARD_NOT_APPLICABLE.getCode(), ServiceValidation.PERMIT_RENEWAL_AUTH_CARD_NOT_APPLICABLE.getValue());
                        	}
                        }
                        	
                        }
                        
                        isPRValidation = DateUtil.isSameOrGreaterDate(DateUtil.addYears(applicationModel.getPrIssueTime(), RC_TRANSPORT_VALIDITY_YEARS),
                                DateUtil.toCurrentUTCTimeStamp());
                    }
                }
                if (applicationModel.getPrStatus() == Status.CANCELLED) {
                    throw new ServiceValidationException(ServiceValidation.RC_CANCELLED.getCode(), ServiceValidation.RC_CANCELLED.getValue());
                }
                if (applicationModel.getPrStatus() == Status.STOPPAGE_TAX) {
                    throw new ServiceValidationException(ServiceValidation.VEHICLE_IS_STOPPAGE_TAX.getCode(), ServiceValidation.VEHICLE_IS_STOPPAGE_TAX.getValue());
                }
                if(serviceType == ServiceType.SUSPENSION_REVOCATION){
                    if (!(applicationModel.getPrStatus() == Status.SUSPENDED || applicationModel.getPrStatus() == Status.OBJECTION)) {
                        log.error("pr " + uniqueKey + " is not suspended/objected.");
                        throw new ServiceValidationException(ServiceValidation.RC_NOT_SUSPENDED_OBJECTED.getCode(), ServiceValidation.RC_NOT_SUSPENDED_OBJECTED.getValue());
                    }
                } else {
                    if (applicationModel.getPrStatus() == Status.SUSPENDED && !(serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION || 
                            serviceType == ServiceType.REGISTRATION_CANCELLATION)){
                        SuspendedRCNumberModel suspensionModel = applicationModel.getSuspensionDetails();
                        log.error("pr " + applicationModel.getPrStatus() + " is suspended. till " +  suspensionModel.getEndDate());
                        throw new ServiceValidationException(ServiceValidation.RC_SUSPENDED.getCode(), ServiceValidation.RC_SUSPENDED.getValue());
                    } else if (applicationModel.getPrStatus() == Status.OBJECTION && !(serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION || 
                            serviceType == ServiceType.REGISTRATION_CANCELLATION || serviceType == ServiceType.REGISTRATION_RENEWAL)){
                        log.error("pr " + applicationModel.getPrStatus());
                        throw new ServiceValidationException(ServiceValidation.RC_OBJECTED.getCode(), ServiceValidation.RC_OBJECTED.getValue());
                    }
                }
			if (serviceType != ServiceType.REGISTRATION_SUS_CANCELLATION) {
				if (serviceType != ServiceType.REGISTRATION_RENEWAL) {
					if (!applicationModel.getRegistrationCategory().getCode().equals(RegistrationCategoryType.TRANSPORT.getCode()) && DateUtil.toCurrentUTCTimeStamp().compareTo(applicationModel.getPrValidUpto()) > 0) {
						log.error("pr is expired :" + applicationModel.getPrValidUpto());
						throw new ServiceValidationException(ServiceValidation.RC_EXPIRED.getCode(),
								ServiceValidation.RC_EXPIRED.getValue());
					}
				} else {
					if (!DateUtil.isSameOrGreaterDate(DateUtil.addDays(DateUtil.toCurrentUTCTimeStamp(), 30),
							applicationModel.getPrValidUpto())) {
						log.error("pr yet not expired :" + applicationModel.getPrValidUpto());
						throw new ServiceValidationException(ServiceValidation.RC_NOT_EXPIRED.getCode(),
								ServiceValidation.RC_NOT_EXPIRED.getValue());
					}
				}
			}                
			if (serviceType == ServiceType.VEHICLE_REASSIGNMENT) {
				// check isVehicleReassignmentApplicable on RC
				RegistrationServiceResponseModel<Boolean> isApplicableResponse = registrationService
						.getIsVehicleReassignmentApplicable(uniqueKey);
				if (isApplicableResponse.getHttpStatus().equals(HttpStatus.OK)) {
					Boolean isApplicable = isApplicableResponse.getResponseBody();
					if (ObjectsUtil.isNull(isApplicable) && isApplicable == false) {
						throw new ServiceValidationException(ServiceValidation.VEHICLE_REASSIGNMENT.getCode(),
								ServiceValidation.VEHICLE_REASSIGNMENT.getValue());
					}
				} else if (isApplicableResponse.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)) {
					if (serviceType == ServiceType.VEHICLE_REASSIGNMENT) {
						log.error("Vehicle Reassignmnet for : " + uniqueKey);
						throw new ServiceValidationException(ServiceValidation.VEHICLE_REASSIGNMENT.getCode(),
								ServiceValidation.VEHICLE_REASSIGNMENT.getValue());
					}
				}
			}
                
                //---validate for NOC --------------

                RegistrationServiceResponseModel<NocDetails> res = registrationService.getNocDetails(null, uniqueKey);
                if (res.getHttpStatus().equals(HttpStatus.OK)) {
                    NocDetails noc = res.getResponseBody();
                    if(!ObjectsUtil.isNull(noc) && noc.getStatus()){
                        if(serviceType != ServiceType.NOC_CANCELLATION){
                            log.error("NOC is found (found in registration) for : " + uniqueKey);
                            throw new ServiceValidationException(ServiceValidation.NOC_ISSUED.getCode(), ServiceValidation.NOC_ISSUED.getValue());
                        }
                    } else if(serviceType == ServiceType.NOC_CANCELLATION){
                        log.error("Noc not issued for : " + uniqueKey);
                        throw new ServiceValidationException(ServiceValidation.NOC_NOT_ISSUED.getCode(), ServiceValidation.NOC_NOT_ISSUED.getValue());
                    }
                } else if(res.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)){
                    if(serviceType == ServiceType.NOC_CANCELLATION){
                        log.error("Noc not issued for : " + uniqueKey);
                        throw new ServiceValidationException(ServiceValidation.NOC_NOT_ISSUED.getCode(), ServiceValidation.NOC_NOT_ISSUED.getValue());
                    }
                }
           
        } catch(HttpStatusCodeException ex) {
	        log.error("can't validate PR, http status code : " + ex.getStatusCode());
//	        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
	            throw new UnauthorizedException("RC Number Not Found !!!");
//	        }
        }
        return isPRValidation;
	}

	private boolean permitNotAllowed(ApplicationModel applicationModel) {
        return applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("AMBT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("AABT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("TRTT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("BULT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("CRNT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("CEHHT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("CHST")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("CRNT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("DEXT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("DMPT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("EXCT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("FTRT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("SNLT")
        || applicationModel.getVehicleModel().getVehicleSubClass().equalsIgnoreCase("TRTT");
    }
	
	
    private boolean isGoodsVehicle(String cov) throws ServiceValidationException{
        if(StringsUtil.isNullOrEmpty(cov)){
            throw new ServiceValidationException(404, "Vehicle Sub Class Not Found !!!");
        }
        if(     cov.equalsIgnoreCase("ARVT") || cov.equalsIgnoreCase("AUTT") || cov.equalsIgnoreCase("BULT") || cov.equalsIgnoreCase("DMPT") ||
                cov.equalsIgnoreCase("CAVT") || cov.equalsIgnoreCase("CMVT") || cov.equalsIgnoreCase("CTHT") || cov.equalsIgnoreCase("CVHT") ||
                cov.equalsIgnoreCase("FFVT") || cov.equalsIgnoreCase("FTRT") || cov.equalsIgnoreCase("FWGT") || cov.equalsIgnoreCase("GCRT") || 
                cov.equalsIgnoreCase("HERT") || cov.equalsIgnoreCase("LIBT") || cov.equalsIgnoreCase("MACT") || cov.equalsIgnoreCase("MBWT") || 
                cov.equalsIgnoreCase("MOCT") || cov.equalsIgnoreCase("PWTT") || cov.equalsIgnoreCase("SNLT") || cov.equalsIgnoreCase("STWT") || 
                cov.equalsIgnoreCase("TFTT") || cov.equalsIgnoreCase("TGVT") || cov.equalsIgnoreCase("TRLT") || cov.equalsIgnoreCase("MVCT") ||
                cov.equalsIgnoreCase("TTTT") || cov.equalsIgnoreCase("XRYT") || cov.equalsIgnoreCase("EXCT") || cov.equalsIgnoreCase("CEHHT")|| 
                cov.equalsIgnoreCase("DEXT") || cov.equalsIgnoreCase("REVN") ){
            return true;
        }
        return false;
    }
    
    
    @Transactional
    public void logAttempt(AuthenticationModel model, ServiceType serviceType, String status, String desc, Integer code){
    	try{
    		UserAttemptLogEntity logEntity = new UserAttemptLogEntity();
        	logEntity.setServiceType(serviceType.getCode());
        	KeyType keyType = model.getKeyType();
        	if(ObjectsUtil.isNull(model.getKeyType())){
        		keyType = KeyType.PR;
        	}
        	logEntity.setKeyType(keyType.name());
        	switch(keyType){
    		case PR:
    			logEntity.setUniqueKey(model.getPrNumber());
    			break;
    		case TR:
    			logEntity.setUniqueKey(model.getTrNumber());
    			break;
    		case DLEX:
    		case DLIN:
    		case DLS:
    		case DLSC:
    		case DLB:
    		case DLCA:
    		case DLD:
    		case DLE:
    		case DLI:
    		case DLR:
    		case DLRE:
    			logEntity.setUniqueKey(model.getDlNumber());
    			break;
    		case LLE:
    			if(ObjectsUtil.isNull(model.getLlrNumber())){
    				logEntity.setUniqueKey(model.getDlNumber());
    			} else {
    				logEntity.setUniqueKey(model.getLlrNumber());
    			}
    			break;
    		case DLF:
    		case LLD:
    			logEntity.setUniqueKey(model.getLlrNumber());
    			break;
    		case DLFC:
    			logEntity.setUniqueKey(model.getDob());
    			break;
    		case LLR:
    			logEntity.setUniqueKey(model.getApplicationNumber());
    			break;
    		default:
    			logEntity.setUniqueKey(serviceType.getCode());
    			break;
        	}
        	logEntity.setAadharNumber(StringsUtil.isNullOrEmpty(model.getAadhaarNumber()) ? model.getUid_num() : model.getAadhaarNumber());
        	logEntity.setStatus(status);
        	logEntity.setDescription(desc);
        	logEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
        	logEntity.setCode(code);
        	userAttemptLog.save(logEntity);
    	} catch(Exception ex){
    		log.error("Error while creating user attempt log : " + ex.getMessage());
    	}
    }
}
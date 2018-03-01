/**
 * 
 */
package org.rta.citizen.common.service.payment.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.converters.payment.TransactionDetailConverter;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.dao.payment.TransactionHistoryDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.CitizenInvoiceEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.entity.payment.TransactionHistoryEntity;
import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.PaymentGatewayType;
import org.rta.citizen.common.enums.PermitType;
import org.rta.citizen.common.enums.RegistrationCategoryType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TaxType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.PayUResponse;
import org.rta.citizen.common.model.payment.PayUVerifyResponseTransaction;
import org.rta.citizen.common.model.payment.TaxRuleModel;
import org.rta.citizen.common.model.payment.TaxTypeModel;
import org.rta.citizen.common.model.payment.TransactionDetailModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.AttachmentService;
import org.rta.citizen.common.service.communication.CommunicationService;
import org.rta.citizen.common.service.payment.CitizenInvoiceService;
import org.rta.citizen.common.service.payment.PaymentService;
import org.rta.citizen.common.service.payment.TaxFeeService;
import org.rta.citizen.common.service.payment.TransactionDetailsService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.service.rtaapplication.RtaApplicationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.hpt.model.FinanceOtherServiceModel;
import org.rta.citizen.permit.model.PermitNewRequestModel;
import org.rta.citizen.vehiclealteration.service.VehicleAlterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger log = Logger.getLogger(PaymentServiceImpl.class);

	@Autowired
	private ApplicationDAO applicationDAO;
	@Autowired
	private CitizenInvoiceService citizenInvoiceService;
	@Autowired
	private RegistrationService registrationService;
	@Autowired
	private TransactionDetailsService transactionDetailService;
	@Value(value = "${service.registration.host}")
	private String HOST;
	@Value(value = "${service.registration.port}")
	private String PORT;
	@Value(value = "${service.registration.path}")
	private String ROOT_URL;
	@Autowired
	private TransactionHistoryDAO transactionHistoryDAO;
	@Autowired
	private TransactionDetailDAO transactionDetailDAO;
	@Autowired
	private TransactionDetailConverter transactionDetailConverter;

	@Autowired
    private ActivitiService activitiService;
	@Autowired
	private CommunicationService communicationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    protected VehicleAlterationService vehicleAlterationService;
    
    @Autowired
    private ApplicationFormDataService applicationFormDataService;
    
    @Autowired
    private RtaApplicationService rtaApplicationService;
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private UserSessionDAO userSessionDAO;
    
    @Autowired
    private TaxFeeService taxFeeService;
    
    @Autowired
    private TaxDetailDAO taxDetailDAO;
    
    @Value(value = "${activiti.citizen.code.all.payment}")
	private String paymentTask;
	
	@Override
	@Transactional
	public TransactionDetailModel createPaymentRequest(long sessionId , String appNo , PaymentGatewayType paymentGatewayType) {
		log.debug("::CITIZEN::::createPaymentRequest:::::appNo " + appNo + " sessionId "+ sessionId );
		ApplicationEntity appEntity = null;
		if(appNo != null)
		appEntity = applicationDAO.getApplication(appNo);
		else
		appEntity = applicationDAO.getApplicationFromSession(sessionId);	
		log.debug("::CITIZEN::createPaymentRequest:::: Service Type " + appEntity.getServiceCode());
		ApplicationTaxModel appTaxModel = new ApplicationTaxModel();
		if(getService(appEntity) == RegistrationCategoryType.LICENSE.getValue()){
			appTaxModel.setRegType(RegistrationCategoryType.LICENSE.getValue());
		}else if(getService(appEntity) == RegistrationCategoryType.USERREG.getValue()){
			appTaxModel.setRegType(RegistrationCategoryType.USERREG.getValue());
		}
		else{
			 appTaxModel = getApplicationTax(appEntity);	
		}	
		CitizenInvoiceEntity citizenInvoiceEntity = citizenInvoiceService.createInvoice(appEntity, appTaxModel);
		TransactionDetailModel transactionDetailModel = transactionDetailService.createBankTransaction(appEntity , citizenInvoiceEntity , appTaxModel , paymentGatewayType);
		transactionDetailModel.setServiceCode(appEntity.getServiceCode());
		transactionDetailModel = getEncryptData(transactionDetailModel, appTaxModel , appEntity);
		return transactionDetailModel;
	}
	
	public ApplicationTaxModel getApplicationTax(ApplicationEntity appEntity){
		RegistrationServiceResponseModel<ApplicationTaxModel> result = null;
		try {
			result = registrationService.getTaxDetails(appEntity.getLoginHistory().getUniqueKey());
		} catch (RestClientException e) {
			log.error("error when getting tr details : " + e);
		}	
		if (ObjectsUtil.isNull(result)) {
			log.info("tr details not found for tr number : " + appEntity.getLoginHistory().getUniqueKey());
			return null;
		}
		if (result.getHttpStatus() != HttpStatus.OK) {
			log.info("error in http request " + result.getHttpStatus());
			return null;
		}
		ApplicationTaxModel appTaxModel = result.getResponseBody();
		return appTaxModel;
	}
	
	private TransactionDetailModel getEncryptData(TransactionDetailModel transactionDetailModel , ApplicationTaxModel appTaxModel , ApplicationEntity appEntity){
		transactionDetailModel.setVehicleRcId(String.valueOf(appTaxModel.getVehicleRcId()));
		transactionDetailModel.setEncryptRequest(transactionDetailModel.getEncryptRequest());
		log.info("::PAYMENT SERVICE::::CODE::::::: " + transactionDetailModel.getServiceCode());
		transactionDetailModel =registrationService.getEncryptedSBIParameter(transactionDetailModel);
		transactionDetailModel.setVehicleRcId(null);
		return transactionDetailModel;
	}
	

	@Override
	@Transactional
	public ResponseModel<String> processPaymentResponse(TransactionDetailModel transactionDetailModel,
			long sessionId) {
		log.debug("::CITIZEN::::processPaymentResponse:::::appNo " + transactionDetailModel.getAppNo() + " sessionId "+ sessionId );
		ApplicationEntity appEntity = null;
		if(transactionDetailModel.getAppNo() != null)
		appEntity = applicationDAO.getApplication(transactionDetailModel.getAppNo());
		else
		appEntity = applicationDAO.getApplicationFromSession(sessionId);	
		log.debug(":CITIZEN::processPaymentResponse::::Service Type:: " + appEntity.getServiceCode());
		ResponseModel<String> response = new ResponseModel<>();
		transactionDetailModel = getDecryptData(transactionDetailModel);
		
		switch(PaymentGatewayType.getPaymentGatewayType(transactionDetailModel.getPgType())){
		case PAYU:
			if (transactionDetailModel.getStatus().equalsIgnoreCase("SUCCESS")
					|| transactionDetailModel.getStatus().equalsIgnoreCase("PENDING")
					|| transactionDetailModel.getStatus().equalsIgnoreCase("FAILURE")) {
				response = transactionDetailService.updatePayUPaymentProcess(transactionDetailModel, appEntity);
			} else {
				response = new ResponseModel<>(ResponseModel.FAILED, "Interrupted checksum data");
			}
			break;
		case SBI:
			if (transactionDetailModel.getStatus().equalsIgnoreCase("SUCCESS")) {
				response = transactionDetailService.updatePaymentProcess(transactionDetailModel, appEntity);
			} else {
				response = new ResponseModel<>(ResponseModel.FAILED, "Interrupted checksum data");
			}
			break;
		}
		
		return response;
	}
	
	private TransactionDetailModel getDecryptData(TransactionDetailModel transactionDetailModel){
		transactionDetailModel.setEncryptRequest(transactionDetailModel.getEncryptRequest());
		TransactionHistoryEntity transactionHistoryEntity = new TransactionHistoryEntity();
		transactionHistoryEntity.setTransactionNo(transactionDetailModel.getTransactionNo());
		transactionHistoryEntity.setStatus(Status.OPEN.getValue());
		transactionHistoryEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		transactionHistoryDAO.save(transactionHistoryEntity);
		transactionDetailModel = registrationService.decryptSBIResponse(transactionDetailModel);
		transactionDetailModel.setVehicleRcId(null);
		return transactionDetailModel;
	}
	
	@Override
    @Transactional
    public TransactionDetailModel paymentVerificationReq(long sessionId , String appNo) {
		log.info("::CITIZEN::::paymentVerificationReq:::::appNo " + appNo + " sessionId "+ sessionId );
		ApplicationEntity appEntity = null;
		if(appNo != null)
		appEntity = applicationDAO.getApplication(appNo);
		else
		appEntity = applicationDAO.getApplicationFromSession(sessionId);
        TransactionDetailEntity transactionDetailEntity = null;
        TransactionDetailModel transactionDetailModel = null;
        transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(appEntity);
        if (transactionDetailEntity == null) {
            return null;
        }
        transactionDetailModel = new TransactionDetailModel();
        transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
        transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
        transactionDetailDAO.update(transactionDetailEntity);
        transactionDetailModel = transactionDetailConverter.convertToModel(transactionDetailEntity);
        transactionDetailModel.setServiceCode(appEntity.getServiceCode());
		transactionDetailModel = registrationService.getVerificationEncrytData(transactionDetailModel);
		log.info(":::CITIZEN::paymentVerificationReq::end::::" + transactionDetailModel);
        return transactionDetailModel;
    }
        
        
        @Override
        @Transactional
        public ResponseModel<String> payUPaymentVerificationReq(long sessionId , String appNo) {
    		log.info("::CITIZEN::::paymentVerificationReq:::::appNo " + appNo + " sessionId "+ sessionId );
    		ResponseModel<String> response = null;
    		ApplicationEntity appEntity = null;
    		if(appNo != null)
    		appEntity = applicationDAO.getApplication(appNo);
    		else
    		appEntity = applicationDAO.getApplicationFromSession(sessionId);
            TransactionDetailEntity transactionDetailEntity = null;
            TransactionDetailModel transactionDetailModel = null;
            transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(appEntity);
            if (transactionDetailEntity == null) {
                return null;
            }
            transactionDetailModel = new TransactionDetailModel();
            transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
            transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
            transactionDetailDAO.update(transactionDetailEntity);
            transactionDetailModel = transactionDetailConverter.convertToModel(transactionDetailEntity);
            transactionDetailModel.setServiceCode(appEntity.getServiceCode());
            
        		transactionDetailModel = registrationService.getPayUVerificationEncrytData(transactionDetailModel);
        		if (transactionDetailModel.getPayURespopnseStatus()) {
    				log.info("PayU verification response validation is failed.");
    				//status update in case of failure
    				transactionDetailService.updatePayUFaliureResponse(transactionDetailModel.getTransactionNo() , appEntity);
    				response = new ResponseModel<>(ResponseModel.FAILED, "Interrupted checksum data");
    			}else{
    				transactionDetailModel = getDecryptData(transactionDetailModel);
    				if (transactionDetailModel.getStatus().equalsIgnoreCase("SUCCESS")
    						|| transactionDetailModel.getStatus().equalsIgnoreCase("PENDING")
    						|| transactionDetailModel.getStatus().equalsIgnoreCase("FAILURE")) {
    					response = transactionDetailService.updatePayUPaymentProcess(transactionDetailModel, appEntity);
    				} else {
    					response = new ResponseModel<>(ResponseModel.FAILED, "Interrupted checksum data");
    				}
    			}
    		return response;
        }
        
        private boolean isPayUResponseValid(Optional<PayUResponse> payUResponseOptional, String transactionNo) {
    		if (payUResponseOptional.isPresent()) {
    			String status = payUResponseOptional.get().getStatus();
    			log.info("=====response payU:" + status + "," + payUResponseOptional.get().getMessage());
    			if (status.equalsIgnoreCase("0")) {
    				List<PayUVerifyResponseTransaction> result = payUResponseOptional.get().getResult();
    				if (result == null || result.isEmpty()) {
    					return false;
    				}
    				PayUVerifyResponseTransaction pvrt = result.get(0);
    				return transactionNo.equals(pvrt.getMerchantTransactionId());
    			}
    		}
    		return false;
    	}  
    

	@Override
	@Transactional
	public ResponseModel<String> processPaymentVerifyResponse(TransactionDetailModel transactionDetailModel,
			Long sessionId) {
		log.info("::CITIZEN::::processPaymentResponse:::::appNo " + transactionDetailModel.getAppNo() + " sessionId "+ sessionId );
		ApplicationEntity appEntity = null;
		if(transactionDetailModel.getAppNo() != null)
		appEntity = applicationDAO.getApplication(transactionDetailModel.getAppNo());
		else
		appEntity = applicationDAO.getApplicationFromSession(sessionId);	
		log.info(":CITIZEN::processPaymentResponse::::Service Type:: " + appEntity.getServiceCode());
		log.info(":CITIZEN::processPaymentVerifyResponse::::Service Type:: " + appEntity.getServiceCode());
		ResponseModel<String> response = new ResponseModel<>();
		transactionDetailModel = getDecryptData(transactionDetailModel);
		if (transactionDetailModel.getStatus().equalsIgnoreCase("SUCCESS")) {
			response = transactionDetailService.updatePaymentProcess(transactionDetailModel, appEntity);
		} else {
			response = new ResponseModel<>(ResponseModel.FAILED, "Interrupted checksum data");
		}
		
		try{
		    //----------- get Active task from activiti -----------
		    ActivitiResponseModel<List<RtaTaskInfo>> actResponse = activitiService.getActiveTasks(appEntity.getExecutionId());
		    List<RtaTaskInfo> tasks = actResponse.getData();
		    response.setActivitiTasks(tasks);
		    //------------------------------------------------------
		} catch(Exception ex){
		    log.error("Exception while getting active task from activiti. ExecutionId " + appEntity.getExecutionId());
		}
		
		log.info(":::processPaymentVerifyResponse::::end::");
		return response;
	}
	
	public int getService(ApplicationEntity appEntity){
		
		int isUserOrLicense = 0;
		switch(ServiceType.getServiceType(appEntity.getServiceCode())){
		case ADDRESS_CHANGE:
			break;
		case DEFAULT:
			break;
		case DIFFERENTIAL_TAX:
			break;
		case LL_DUPLICATE:
		case LL_ENDORSEMENT:
		case LL_FRESH:
		case LL_RETEST:
		case DL_BADGE:
		case DL_CHANGE_ADDRESS:
		case DL_DLINFO:
		case DL_DUPLICATE:
		case DL_ENDORSMENT:
		case DL_EXPIRED:
		case DL_FRESH:
		case DL_INT_PERMIT:
		case DL_RENEWAL:
		case DL_RETEST:
		case DL_SURRENDER:
		case DL_REVO_SUS:
		case DL_CHANGEADDRS_OS:
		case DL_FOREIGN_CITIZEN:
		case DL_MILITRY:
		case DL_SUSU_CANC:
			isUserOrLicense = RegistrationCategoryType.LICENSE.getValue();
			break;
		case DUPLICATE_REGISTRATION:
			break;
		case FRESH_RC_FINANCIER:
			break;
		case HPA:
			break;
		case HPT:
			break;
		case NOC_CANCELLATION:
			break;
		case NOC_ISSUE:
			break;
		case OWNERSHIP_TRANSFER_AUCTION:
			break;
		case OWNERSHIP_TRANSFER_DEATH:
			break;
		case OWNERSHIP_TRANSFER_SALE:
			break;
		case REGISTRATION_CANCELLATION:
			break;
		case REGISTRATION_RENEWAL:
			break;
		case REGISTRATION_SUS_CANCELLATION:
			break;
		case SUSPENSION_REVOCATION:
			break;
		case THEFT_INTIMATION:
			break;
		case VEHICLE_ATLERATION:
			break;
		case VEHICLE_REASSIGNMENT:
			break;
		case ALTERATION_AGENCY_SIGNUP:
		case BODYBUILDER_SIGNUP:
		case DEALER_SIGNUP:
		case FINANCIER_SIGNUP:
		case PUC_USER_SIGNUP:
		case DRIVING_INSTITUTE:
		case HAZARDOUS_VEH_TRAIN_INST:
		case MEDICAL_PRACTITIONER:
			isUserOrLicense = RegistrationCategoryType.USERREG.getValue();
			break;
		}
		return isUserOrLicense;
	}
	
	@Transactional
	@Override
    public List<RtaTaskInfo> callAfterPaymentSuccess(Long sessionId, String taskDef, String userName){
        log.info("Payment is Succesfull... performing other tasks... with user : " + userName);

        Assignee assignee = new Assignee();
        assignee.setUserId(userName);
        String instanceId = applicationService.getProcessInstanceId(sessionId);
        Map<String, Object> variableMap = new HashMap<>();
        Map<String, Object> otherDataMap = new HashMap<>();
        ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
        String rtaOfficeCode = appEntity.getRtaOfficeCode();
        UserSessionEntity sessionEntity = appEntity.getLoginHistory();
        
        if(!StringsUtil.isNullOrEmpty(taskDef) && taskDef.equalsIgnoreCase(paymentTask)){
        	boolean isCallActiviti = true;
            boolean isSync = false;
            otherDataMap.put("isCallActiviti", true);
            otherDataMap.put("isSync", false);
            beforeActivitiTask(appEntity, sessionId, variableMap, otherDataMap);
            isCallActiviti = (boolean) otherDataMap.get("isCallActiviti");
            isSync = (boolean) otherDataMap.get("isSync");
            
            log.info("Call Activiti : " + isCallActiviti);
            ServiceCategory cat = ServiceUtil.getServiceCategory(ServiceType.getServiceType(sessionEntity.getServiceCode()));
            if(isCallActiviti){
                variableMap.put(ActivitiService.RTA_OFFICE_CODE, rtaOfficeCode);
                ActivitiResponseModel<List<RtaTaskInfo>> actResponse =
                        activitiService.completeTask(assignee, taskDef, instanceId, true, variableMap);
                log.info("Call activitiService.completeTask : "+assignee+" "+taskDef+" "+instanceId+" "+"true"+" "+variableMap);
                if(ObjectsUtil.isNull(actResponse) || ObjectsUtil.isNull(actResponse.getActiveTasks()) || actResponse.getActiveTasks().size() <= 0){
                    //--- application completed in bpm ----------------
                    RtaTaskInfo taskInfo = new RtaTaskInfo();
                    taskInfo.setTaskDefKey(ActivitiService.APP_COMPLETED);
                    taskInfo.setProcessDefId(appEntity.getLoginHistory().getServiceCode());
                    List<RtaTaskInfo> taskList = new ArrayList<RtaTaskInfo>();
                    taskList.add(taskInfo);
                    try{
                        rtaApplicationService.completeApp(instanceId, Status.APPROVED, sessionEntity.getAadharNumber(), UserType.ROLE_CITIZEN, true);
                        log.info("Call rtaApplicationService.completeApp : "+instanceId+" "+"true"+" "+sessionEntity.getAadharNumber()+" "+"citizen"+" "+"true");
                        if(cat == ServiceCategory.PERMIT_FITNESS_CATEGORY){
                        	rtaApplicationService.sendSMSEmail(Status.APPROVED, appEntity);
                        }
                        if(cat==ServiceCategory.REG_CATEGORY && ServiceType.getServiceType(sessionEntity.getServiceCode()) == ServiceType.FRESH_RC_FINANCIER){
    						rtaApplicationService.sendSMSEmail(Status.FRESH, appEntity);
    					}
                    } catch(Exception ex){
                        log.error("Exception while completing task ......." + ex.getMessage());
                    }
                    return taskList;
                } else if(isSync){
                    try{
                        rtaApplicationService.completeApp(instanceId, Status.APPROVED, sessionEntity.getAadharNumber(), UserType.ROLE_CITIZEN, false);
                        if(cat==ServiceCategory.PERMIT_FITNESS_CATEGORY){
    						rtaApplicationService.sendSMSEmail(Status.FRESH, appEntity);
    					}
                    } catch(Exception ex){
                        log.error("Exception while completing task ......." + ex.getMessage());
                    }
                }
                return actResponse.getActiveTasks();
            }
        } else {
        	ActivitiResponseModel<List<RtaTaskInfo>> actResponse = activitiService.completeTask(assignee, taskDef, instanceId, true, variableMap);
        	return actResponse.getActiveTasks();
        }
        return null;
    }
    
    private RegistrationServiceResponseModel<Object> saveHpaToRegistration(String appNumber, String prNumber, Long quoteAmount) throws UnauthorizedException{
        return registrationService.saveHPA(appNumber, prNumber, quoteAmount);
    }
    
    @Override
    @Transactional
    public void beforeActivitiTask(ApplicationEntity appEntity, Long sessionId, Map<String, Object> variableMap, Map<String, Object> otherDataMap){
        UserSessionEntity sessionEntity = appEntity.getLoginHistory();
        try {
            ServiceType service = ServiceType.getServiceType(appEntity.getLoginHistory().getServiceCode());
            if (service == ServiceType.ADDRESS_CHANGE || service == ServiceType.DUPLICATE_REGISTRATION
                    || service == ServiceType.NOC_CANCELLATION || service == ServiceType.HPT
                    || service == ServiceType.NOC_ISSUE || service == ServiceType.OWNERSHIP_TRANSFER_DEATH
                    || service == ServiceType.REGISTRATION_CANCELLATION|| service == ServiceType.REGISTRATION_RENEWAL
                    || service == ServiceType.VEHICLE_ATLERATION || service == ServiceType.VEHICLE_REASSIGNMENT) {
                FinanceOtherServiceModel finModel = new FinanceOtherServiceModel();
                finModel.setAppNo(appEntity.getApplicationNumber());
                finModel.setPrNumber(sessionEntity.getUniqueKey());
                finModel.setServiceCode(service.getCode());
                RegistrationServiceResponseModel<FinanceOtherServiceModel> res = registrationService.sendOtherAppToFinancier(finModel);
                log.info("Status while sendOtherAppToFinancier ::" + res.getHttpStatus());
                if(res.getHttpStatus().equals(HttpStatus.OK) || res.getHttpStatus().equals(HttpStatus.ALREADY_REPORTED)){
                    variableMap.put(ActivitiService.IS_ONLINE_FINANCED, true);
                } else {
                    variableMap.put(ActivitiService.IS_ONLINE_FINANCED, false);
                }
                otherDataMap.put("isCallActiviti",true);
            } else if(service == ServiceType.HPA){
                ResponseModel<ApplicationFormDataModel> response = applicationFormDataService
                        .getApplicationFormDataBySessionId(sessionId, FormCodeType.HPA_FORM.getLabel());
                ApplicationFormDataModel form = response.getData();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonData = mapper.readTree(form.getFormData());
                Long quoteAmount = jsonData.get("quoteAmount").asLong();
                if(quoteAmount <= 0.0){
                    log.error("Error Quote amount not found, session Id : " + sessionId);
                    throw new NotFoundException("Quote amount not found !!!");
                }
                RegistrationServiceResponseModel<Object> res = saveHpaToRegistration(appEntity.getApplicationNumber(), sessionEntity.getUniqueKey(), quoteAmount);
                if(!res.getHttpStatus().equals(HttpStatus.OK)){
                    log.error("Exception while applying HPA to registration...");
                }
                otherDataMap.put("isCallActiviti",true);
            } else if(service == ServiceType.LL_ENDORSEMENT) {
                if(SomeConstants.ONE == applicationFormDataService.getRegistrationCategory(appEntity.getApplicationId(), service )){
                    variableMap.put(ActivitiService.ISCCOSTEP, true);
                }else{
                    variableMap.put(ActivitiService.ISCCOSTEP, false);
                }
            } else if(service == ServiceType.DL_ENDORSMENT){
                if(SomeConstants.ONE == applicationFormDataService.getRegistrationCategory(appEntity.getApplicationId(), service )){
                    variableMap.put(ActivitiService.ISCCOSTEP, true);
                    variableMap.put(ActivitiService.ISAORTOSTEP, true);
                }else{
                    variableMap.put(ActivitiService.ISCCOSTEP, false);
                    variableMap.put(ActivitiService.ISAORTOSTEP, false);
                }
            }else if(service == ServiceType.DL_RENEWAL){
//                if(SomeConstants.ONE == applicationFormDataService.getRegistrationCategory(appEntity.getApplicationId(), service )
//                		|| SomeConstants.FIFTY <= DateUtil.getCurrentAge(appEntity.getApplicantDob())){
//                    variableMap.put(ActivitiService.ISCCOSTEP, true);
//                }else{
//                	variableMap.put(ActivitiService.ISCCOSTEP, false);
//                }
            	variableMap.put(ActivitiService.ISCCOSTEP, true);
            } else if(service == ServiceType.DL_EXPIRED){
            	if(DateUtil.getCurrentAge(appEntity.getApplicantDob()) >= SomeConstants.FIFTY){
            		 variableMap.put(ActivitiService.ISCCOSTEP, true);
            		 variableMap.put(ActivitiService.ISAORTOSTEP, true);
            	}else{
            		variableMap.put(ActivitiService.ISCCOSTEP, false);
                    variableMap.put(ActivitiService.ISAORTOSTEP, false);
            	}
            } else if(service == ServiceType.PERMIT_FRESH){
                boolean isSendCco = false;
                try{
                    ResponseModel<ApplicationFormDataModel> response = applicationFormDataService.getApplicationFormDataBySessionId(sessionId, FormCodeType.PCF_FORM.getLabel());
                    ApplicationFormDataModel form = response.getData();
                    ObjectMapper mapper = new ObjectMapper();
                    PermitNewRequestModel permitNewModel = mapper.readValue(form.getFormData(), PermitNewRequestModel.class);
                    log.info("permitClass: " + permitNewModel.getPermitClass() + "  permitTypeId: " + permitNewModel.getPermitType());
                    if((permitNewModel.getPermitType().equalsIgnoreCase("NP") || permitNewModel.getPermitType().equalsIgnoreCase("CSPP"))){
                        isSendCco = true;
                        otherDataMap.put("isSync",true);
                    }
                } catch(Exception ex){
                    otherDataMap.put("isCallActiviti",true);
                    log.error("Exception while getting form data for sessonId :" + sessionId + " and form code : " + FormCodeType.PCF_FORM.getLabel());
                }
                log.info("isSendCco : " + isSendCco);
                variableMap.put(ActivitiService.ISCCOSTEP, isSendCco);
            } else if(service == ServiceType.PERMIT_RENEWAL_AUTH_CARD){
            	variableMap.put(ActivitiService.ISCCOSTEP, false);
            	otherDataMap.put("isSync",true);
            	RegistrationServiceResponseModel<List<PermitHeaderModel>> permitDetailList = registrationService.getPermitDetails(sessionEntity.getVehicleRcId());
            	if(ObjectsUtil.isNull(permitDetailList.getResponseBody()) || permitDetailList.getResponseBody().size()==0){
            		log.error("Permit Details not found while renewal of auth card for : " + sessionEntity.getUniqueKey());
					throw new ServiceValidationException(ServiceValidation.PERMIT_NOT_FOUND.getCode(), ServiceValidation.PERMIT_NOT_FOUND.getValue());
				}
				for (PermitHeaderModel headerModel : permitDetailList.getResponseBody()) {
					if (!headerModel.getIsTempPermit() && PermitType.NP.getValue().equals(headerModel.getPermitType())) {
						variableMap.put(ActivitiService.ISCCOSTEP, true);
						break;
					}
				}
            }
            if (service == ServiceType.VEHICLE_ATLERATION) {
            	 ResponseModel<Boolean> resModel = vehicleAlterationService.updateDataAfterPayments(sessionId);
                 String formCode="";
                 formCode=FormCodeType.VA_FORM.getLabel();
                 try{
                     ResponseModel<ApplicationFormDataModel> response = applicationFormDataService.getApplicationFormDataBySessionId(sessionId, formCode);
                     ApplicationFormDataModel form = response.getData();
                     ObjectMapper mapper = new ObjectMapper();
                     VehicleBodyModel vehicleBodyModel = mapper.readValue(form.getFormData(), VehicleBodyModel.class);
                     log.info("alterationCategory: " + vehicleBodyModel.getAlterationCategory());
                     RegistrationServiceResponseModel<RegistrationCategoryModel> result=null;
					try {
						result = registrationService
								.getRegCategoryByRcId(sessionEntity.getVehicleRcId());
					} catch (RestClientException e) {
						log.error("error when getting tr details : " + e);
					}	
					if (ObjectsUtil.isNull(result)) {
						log.info("tr details not found for tr number : " + appEntity.getLoginHistory().getUniqueKey());
					}
					if (result.getHttpStatus() != HttpStatus.OK) {
						log.info("error in http request " + result.getHttpStatus());
					}
					UserSessionEntity userSessionEntity = userSessionDAO.getUserSession(sessionId);
					RegistrationServiceResponseModel<VehicleDetailsRequestModel> vehicleDetailResponse = null;
					try{
					vehicleDetailResponse = registrationService.getVehicleDetails(userSessionEntity.getVehicleRcId());
					}catch (RestClientException e) {
						log.error("error when getting vehicle details : " + e);
					}
					if (ObjectsUtil.isNull(vehicleDetailResponse)) {
						log.info("vehicle details not found for vehicle RC ID : " + userSessionEntity.getVehicleRcId());
					}
					if (vehicleDetailResponse.getHttpStatus() != HttpStatus.OK) {
						log.info("error in http request " + result.getHttpStatus());
					}
					//--- set default false ----------------
					variableMap.put(ActivitiService.IS_BODY_BUILDER, false);
					
					List<AlterationCategory> alterationCategorieList = vehicleBodyModel.getAlterationCategory();
					for (AlterationCategory alterationCategory : alterationCategorieList) {
						if ((alterationCategory == AlterationCategory.BODY_TYPE)
								|| alterationCategory == AlterationCategory.SEATING_CAPACITY) {
							variableMap.put(ActivitiService.IS_BODY_BUILDER, true);
						}
					}
                 } catch(Exception ex){
                     otherDataMap.put("isCallActiviti",true);
                     log.error("Exception while getting form data for sessonId :" + sessionId + " and form code : " + formCode);
                 }
                 if(resModel.equals(ResponseModel.SUCCESS)){
                     otherDataMap.put("isCallActiviti",true);
                 }
                 log.info(":::getPaymentResponse::: updateDataAfterPayments status ::: " + resModel.getStatus());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception while calling hasAppliedHPA to registration...: " + ex.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Boolean isPayDiffTaxForVA(Long sessionId){
    	
    	String formCode=FormCodeType.VA_FORM.getLabel();
        try{
            ResponseModel<ApplicationFormDataModel> response = applicationFormDataService.getApplicationFormDataBySessionId(sessionId, formCode);
            ApplicationFormDataModel form = response.getData();
            ObjectMapper mapper = new ObjectMapper();
            VehicleBodyModel vehicleBodyModel = mapper.readValue(form.getFormData(), VehicleBodyModel.class);
            log.info("alterationCategory: " + vehicleBodyModel.getAlterationCategory());
            RegistrationServiceResponseModel<RegistrationCategoryModel> result=null;
            ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
            UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
			try {
				result = registrationService.getRegCategoryByRcId(userSessionEntity.getVehicleRcId());
			} catch (RestClientException e) {
				log.error("error when getting tr details : " + e);
			}	
			if (ObjectsUtil.isNull(result)) {
				log.info("tr details not found for tr number : " + userSessionEntity.getUniqueKey());
			}
			if (result.getHttpStatus() != HttpStatus.OK) {
				log.info("error in http request " + result.getHttpStatus());
			}
			RegistrationServiceResponseModel<VehicleDetailsRequestModel> vehicleDetailResponse = null;
			try{
				vehicleDetailResponse = registrationService.getVehicleDetails(userSessionEntity.getVehicleRcId());
			}catch (RestClientException e) {
				log.error("error when getting vehicle details : " + e);
			}
			if (ObjectsUtil.isNull(vehicleDetailResponse)) {
				log.info("vehicle details not found for vehicle RC ID : " + userSessionEntity.getVehicleRcId());
			}
			if (vehicleDetailResponse.getHttpStatus() != HttpStatus.OK) {
				log.info("error in http request " + result.getHttpStatus());
			}
			
			VehicleDetailsRequestModel detailsRequestModel = vehicleDetailResponse.getResponseBody();

			List<AlterationCategory> alterationCategorieList = vehicleBodyModel.getAlterationCategory();
			
			Boolean isDiffTaxPayed = false;
			for (AlterationCategory alterationCategory : alterationCategorieList) {
				if (alterationCategory == AlterationCategory.VEHICLE_TYPE || alterationCategory == AlterationCategory.BODY_TYPE
						|| alterationCategory == AlterationCategory.SEATING_CAPACITY) {
					isDiffTaxPayed=true;
				}
			}
			boolean isPayDT=false;
			String txTypeCode = null;
			if (isDiffTaxPayed) {
				TaxType existingTaxType = TaxType.getTaxTypeByCode(detailsRequestModel.getVehicle().getTaxType().toUpperCase());
				TaxTypeModel taxTypeModel = null;
				String cov="";
				if (vehicleBodyModel.getVehicleSubClass() == null) {
					cov = detailsRequestModel.getVehicle().getVehicleSubClass();
				} else {
					cov = vehicleBodyModel.getVehicleSubClass();
				}
				RegistrationServiceResponseModel<TaxTypeModel> taxTypeResponse = registrationService
						.getTaxTypeByCov(cov);
				if (taxTypeResponse.getHttpStatus().equals(HttpStatus.OK)) {
					taxTypeModel = taxTypeResponse.getResponseBody();
				}
				txTypeCode = taxTypeModel.getTaxTypeCode();
				// Quarterly to Life Tax
				if (existingTaxType.getCode().equals(TaxType.QUARTERLY_TAX.getCode())
						&& taxTypeModel.getTaxTypeCode().equals(TaxType.LIFE_TAX.getCode())) {
					RegistrationServiceResponseModel<Boolean> lifeTaxResponse = registrationService
							.getIsLifeTaxPaid(userSessionEntity.getVehicleRcId());
					if (lifeTaxResponse.getHttpStatus().equals(HttpStatus.OK)) {
						Boolean isLifeTaxPaid = lifeTaxResponse.getResponseBody();
						log.info("QA to LT isPayDiffTaxForVA tax amt : " + isLifeTaxPaid);
						if (!isLifeTaxPaid) {
							isPayDT = true;
						}
					}
				}

				// Quarterly to Quarterly Quarterly
				if (existingTaxType.getCode().equals(TaxType.QUARTERLY_TAX.getCode())
						&& taxTypeModel.getTaxTypeCode().equals(TaxType.QUARTERLY_TAX.getCode())) {
					// check difference
					TaxRuleModel taxRuleModel = taxFeeService.getTaxCal(appEntity.getApplicationNumber());
					if (taxRuleModel != null) {
						log.info("Inside isPayDiffTaxForVA tax amt : " + taxRuleModel.getTaxAmount());
						if (taxRuleModel.getTaxAmount() > 0) {
							isPayDT = true;
						}
					}
				}
				// Life to Quarterly Tax
				if (existingTaxType.getCode().equals(TaxType.LIFE_TAX.getCode())
						&& taxTypeModel.getTaxTypeCode().equals(TaxType.QUARTERLY_TAX.getCode())) {
					isPayDT = true;
				}
			}
			log.info("Inside isPayDiffTaxForVA isPayDT : " + isPayDT);
			if(!isPayDT){
				//----------insert in tax table with 0 value ---------------------
				TaxDetailEntity taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
				if (taxDetailEntity == null) {
					taxDetailEntity = new TaxDetailEntity();
					taxDetailEntity.setApplicationId(appEntity);
					taxDetailEntity.setTaxAmt(0);
					taxDetailEntity.setTaxType(txTypeCode);
					taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
					taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
					taxDetailEntity.setPenaltyAmt(0);
					taxDetailEntity.setTotalAmt(0);
					taxDetailDAO.save(taxDetailEntity);
				}
			}
			return isPayDT;
         } catch(Exception ex){
             log.error("Exception while getting form data for sessonId :" + sessionId + " and form code : " + formCode);
         }
		return false;
        
    }
}

package org.rta.citizen.common.service.payment.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.converters.payment.TransactionDetailConverter;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.CitizenInvoiceEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.PaymentGatewayType;
import org.rta.citizen.common.enums.PaymentType;
import org.rta.citizen.common.enums.RegistrationCategoryType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.communication.CustMsgModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.TransactionDetailModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.communication.CommunicationService;
import org.rta.citizen.common.service.communication.CommunicationServiceImpl;
import org.rta.citizen.common.service.payment.TransactionDetailsService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.NumberParser;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class TransactionDetailsServiceImpl implements TransactionDetailsService{

	private static final Logger log = Logger.getLogger(TransactionDetailsServiceImpl.class);

	@Autowired
	private TransactionDetailDAO transactionDetailDAO;
	@Autowired
	private TransactionDetailConverter transactionDetailConverter;
	@Autowired
	private RegistrationService registrationService;
	@Value(value = "${service.registration.host}")
	private String HOST;
	@Value(value = "${service.registration.port}")
	private String PORT;
	@Value(value = "${service.registration.path}")
	private String ROOT_URL;
	@Autowired
	private UserSessionDAO usersessionDAO;
	@Autowired
	private ActivitiService  activitiService;
	@Autowired
	private CommunicationService communicationService;
	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;
	@Autowired
    private TaxDetailDAO taxDetailDAO;
	
	@Override
	@Transactional
	public TransactionDetailModel createBankTransaction(ApplicationEntity applicationEntity , CitizenInvoiceEntity citizenInvoiceEntity , ApplicationTaxModel appTaxModel , PaymentGatewayType paymentGatewayType) {
		TransactionDetailEntity transactionDetailEntity = null;
		TransactionDetailModel transactionDetailModel = null;
		String remmiterName = null;
		String districtCode = null;
		boolean newApplicantFlag = false;
		boolean isBuyer = false;
		boolean isUser = true;
		ApplicationFormDataEntity appFormDataEntity = null;
		ObjectMapper mapper = new ObjectMapper();
		AddressChangeModel addressChangeModel = new AddressChangeModel();
		int regType = appTaxModel.getRegType();
		boolean isAlteration =false;
		double feeAmt = 0.0d , permitFee =0.0d , serviceFee = 0.0d ;
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(applicationEntity.getExecutionId());
        switch(ServiceType.getServiceType(applicationEntity.getServiceCode())){
		case VEHICLE_ATLERATION:
			  if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			   for(RtaTaskInfo task : actRes.getActiveTasks()){
				     if (task.getTaskDefKey().equalsIgnoreCase("dt_tax"))
			            	isAlteration = true;
			   }
			  }
			break;
        }
        List<RtaTaskInfo> tasks = actRes.getActiveTasks();
        for (RtaTaskInfo task : tasks) {
            if (task.getTaskDefKey().equalsIgnoreCase("payment_buyer"))
                isBuyer = true;
        }
        if(isBuyer)
			transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(applicationEntity , PaymentType.TOW_BUYER);
        else if(isAlteration)
			transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(applicationEntity , PaymentType.ALTERATION_DT);
		else
			transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(applicationEntity , PaymentType.PAY);
		if(transactionDetailEntity != null)
		throw new IllegalArgumentException("Payment has been already done");
        
		transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(applicationEntity);
        
		
        switch (RegistrationCategoryType.getRegistrationCategoryType(regType)) {
            case NON_TRANSPORT:
            	feeAmt = NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getApplicationFee() 
                		+  (citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getSmartCardFee())
                		+ citizenInvoiceEntity.getRegFeeDtlId().getPenaltyFee());
                serviceFee = NumberParser.getRoundOff((citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getApplicationServiceCharge())
                	+	(citizenInvoiceEntity.getTaxDtlId() == null ? 0 : citizenInvoiceEntity.getTaxDtlId().getServiceFee()));
                break;
            case TRANSPORT:
            	feeAmt = NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getApplicationFee() 
                        +  (citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getSmartCardFee()) 
                        + (citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getFitnessFee())
                        + citizenInvoiceEntity.getRegFeeDtlId().getPenaltyFee());
                serviceFee = NumberParser.getRoundOff((citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getApplicationServiceCharge()) 
                        + ((citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getFitnessServiceCharge())) 
                        + ((citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getPermitServiceCharge())
                		+ (citizenInvoiceEntity.getTaxDtlId() == null ? 0 : citizenInvoiceEntity.getTaxDtlId().getServiceFee())));
                if(citizenInvoiceEntity.getRegFeeDtlId() != null)
                permitFee = citizenInvoiceEntity.getRegFeeDtlId().getPermitFee()
                        + citizenInvoiceEntity.getRegFeeDtlId().getOtherPermitFee();
                break;
            case LICENSE:
                feeAmt = NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId().getApplicationFee() +  citizenInvoiceEntity.getRegFeeDtlId().getSmartCardFee());
                serviceFee = NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId().getApplicationServiceCharge());
                newApplicantFlag =  true;
                districtCode  = "AN" ;
                try {
    				AadharModel aadharModel =  registrationService.getAadharDetails(Long.valueOf(applicationEntity.getLoginHistory().getAadharNumber())).getResponseBody();
    				remmiterName = aadharModel.getName();
    				} catch (NumberFormatException | UnauthorizedException e1) {
    					e1.printStackTrace();
    					throw new IllegalArgumentException("Internal Server Error getInvoiceDLDetails !");
    				} 
                
                break;
            case USERREG:
                feeAmt = NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId().getApplicationFee() +  citizenInvoiceEntity.getRegFeeDtlId().getSmartCardFee());
                serviceFee = NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId().getApplicationServiceCharge());
                newApplicantFlag =  true;
                appFormDataEntity = applicationFormDataDAO.getApplicationFormData(applicationEntity.getApplicationId(), getFormCode(applicationEntity));
                try {
        			if(appFormDataEntity != null){
        				isUser = false;
        			UserSignupModel userSignupModel = null;	
        			userSignupModel =  mapper.readValue(appFormDataEntity.getFormData(), UserSignupModel.class);	
        			districtCode = "VZ";
        			remmiterName = userSignupModel.getInstitutionName();
        			if(remmiterName == null)
        				remmiterName = "Medical";
        			}
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
                break;    
                
        }
        
        TaxDetailEntity taxDetail = taxDetailDAO.getByAppId(applicationEntity);
        double compoundFee = 0.0;
        if (taxDetail != null) {
            compoundFee = taxDetail.getCompoundFee();
        }
        
        
        if (transactionDetailEntity != null) {
            transactionDetailModel = new TransactionDetailModel();
            transactionDetailEntity.setFeeAmount(feeAmt);
            transactionDetailEntity.setServiceCharge(serviceFee);
            transactionDetailEntity.setPermitAmount(permitFee);
            transactionDetailEntity
                    .setPostalCharge(NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getPostalCharge()));
            if(citizenInvoiceEntity.getTaxDtlId() != null){
                transactionDetailEntity.setTaxAmount(NumberParser.getRoundOff(citizenInvoiceEntity.getTaxDtlId().getTaxAmt() 
                		+ citizenInvoiceEntity.getTaxDtlId().getPenaltyAmt() 
                		+ citizenInvoiceEntity.getTaxDtlId().getTaxAmtArrears() 
                		+ citizenInvoiceEntity.getTaxDtlId().getPenaltyAmtArrears()));
                transactionDetailEntity.setGreenTaxAmt(citizenInvoiceEntity.getTaxDtlId().getGreenTaxAmt());
                transactionDetailEntity.setCessFee(citizenInvoiceEntity.getTaxDtlId().getCessFee());
            }
            transactionDetailEntity.setPayAmount(totalPayAmount(transactionDetailEntity));
            transactionDetailEntity.setModifiedBy(applicationEntity.getLoginHistory().getAadharNumber());
            transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
            transactionDetailEntity.setCompoundAmount(compoundFee);
            transactionDetailEntity.setPgType(paymentGatewayType.getLabel());
            transactionDetailDAO.update(transactionDetailEntity);
        } else {
        	transactionDetailModel = new TransactionDetailModel();
            transactionDetailEntity = new TransactionDetailEntity();
            transactionDetailEntity.setFeeAmount(feeAmt);
            transactionDetailEntity.setServiceCharge(serviceFee);
            transactionDetailEntity.setPermitAmount(permitFee);
            transactionDetailEntity.setPostalCharge(NumberParser.getRoundOff(citizenInvoiceEntity.getRegFeeDtlId() == null ? 0:citizenInvoiceEntity.getRegFeeDtlId().getPostalCharge()));
            if(citizenInvoiceEntity.getTaxDtlId() != null){
                transactionDetailEntity.setTaxAmount(NumberParser.getRoundOff(citizenInvoiceEntity.getTaxDtlId().getTaxAmt() 
                		+ citizenInvoiceEntity.getTaxDtlId().getPenaltyAmt() 
                		+ citizenInvoiceEntity.getTaxDtlId().getTaxAmtArrears() 
                		+ citizenInvoiceEntity.getTaxDtlId().getPenaltyAmtArrears()));
                transactionDetailEntity.setGreenTaxAmt(citizenInvoiceEntity.getTaxDtlId().getGreenTaxAmt());
                transactionDetailEntity.setCessFee(citizenInvoiceEntity.getTaxDtlId().getCessFee());
            }
            transactionDetailEntity.setPayAmount(totalPayAmount(transactionDetailEntity));
            transactionDetailEntity.setCreatedBy(applicationEntity.getLoginHistory().getAadharNumber());
            transactionDetailEntity.setPaymentTime(DateUtil.toCurrentUTCTimeStamp());
            transactionDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
            transactionDetailEntity.setStatus(Status.OPEN.getValue());
            transactionDetailEntity.setBankTransacNo(registrationService.getChallanNumber());
            transactionDetailEntity.setApplicationId(applicationEntity);
            transactionDetailEntity.setServiceCode(applicationEntity.getServiceCode());
            transactionDetailEntity.setCompoundAmount(compoundFee);            
            if(isAlteration)
            	transactionDetailEntity.setPaymentType(PaymentType.ALTERATION_DT.getId());
            else if (isBuyer)
            	transactionDetailEntity.setPaymentType(PaymentType.TOW_BUYER.getId());
            else
            	transactionDetailEntity.setPaymentType(PaymentType.PAY.getId());	
            transactionDetailEntity.setPgType(paymentGatewayType.getLabel());
            transactionDetailDAO.save(transactionDetailEntity);
        }
        
        transactionDetailModel = transactionDetailConverter.convertToModel(transactionDetailEntity);
        transactionDetailModel.setDistrictCode(districtCode);
        transactionDetailModel.setRemiterName(remmiterName);
        
        if(isBuyer){
        	newApplicantFlag = true;	
        	appFormDataEntity = applicationFormDataDAO.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.OTS_FORM.getLabel());
        }
        if( transactionDetailEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_DEATH.getCode()) ){ 
        	newApplicantFlag = false;
        	appFormDataEntity = applicationFormDataDAO.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.OTD_FORM.getLabel());
        }
        if( transactionDetailEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_AUCTION.getCode())){
        	newApplicantFlag = true;
        	appFormDataEntity = applicationFormDataDAO.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.OTA_FORM.getLabel());
        }	
		
		try {
			if(appFormDataEntity != null && newApplicantFlag && isUser){
			addressChangeModel =  mapper.readValue(appFormDataEntity.getFormData(), AddressChangeModel.class);	
			transactionDetailModel.setDistrictCode(addressChangeModel.getDistrictCode());
			transactionDetailModel.setRemiterName(addressChangeModel.getDisplayName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		transactionDetailModel.setNewApplicantFlag(newApplicantFlag);
        return transactionDetailModel;
	}
        
        public double totalPayAmount(TransactionDetailEntity transactionDetailEntity) {
            double payAmt = 0.0d;
            payAmt = NumberParser.getRoundOff(transactionDetailEntity.getFeeAmount()
                    + transactionDetailEntity.getServiceCharge() + transactionDetailEntity.getPostalCharge()
                    + transactionDetailEntity.getTaxAmount() + transactionDetailEntity.getPermitAmount()+transactionDetailEntity.getGreenTaxAmt() 
                    + transactionDetailEntity.getCessFee());
            return payAmt;
        }
        
       
        public String getFormCode(ApplicationEntity applicationEntity){
        	String formCode = "";
        	if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.FINANCIER_SIGNUP.getCode())){
        		formCode = FormCodeType.FINREG_FORM.getLabel();
        	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.ALTERATION_AGENCY_SIGNUP.getCode())){
        		formCode = FormCodeType.VA_FORM.getLabel();
        	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.BODYBUILDER_SIGNUP.getCode())){
        		formCode = FormCodeType.BBREG_FORM.getLabel();
        	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.DEALER_SIGNUP.getCode())){
        		formCode = FormCodeType.DEALERREG_FORM.getLabel();
        	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.PUC_USER_SIGNUP.getCode())){
        		formCode = FormCodeType.PUCREG_FORM.getLabel();
        	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.DRIVING_INSTITUTE.getCode())){
        		formCode = FormCodeType.DRIVINGINSTREG_FORM.getLabel();
        	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.HAZARDOUS_VEH_TRAIN_INST.getCode())){
        		formCode = FormCodeType.HAZVEHTRINSTREG_FORM.getLabel();
        	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.MEDICAL_PRACTITIONER.getCode())){
        		formCode = FormCodeType.MEDPRTSNRREG_FORM.getLabel();
        	}
        	log.info("::getFormCode::::: " + formCode);
        	return formCode ;
        }
        

		@Override
		@Transactional
		public ResponseModel<String> updatePaymentProcess(TransactionDetailModel transactionDetailModel,
				ApplicationEntity appEntity) {
			ResponseModel<String> responseModel = null;
	        TransactionDetailEntity transactionDetailEntity =
	                transactionDetailDAO.getByTransNoNdAppNo(transactionDetailModel.getTransactionNo(), appEntity);
	        if (transactionDetailEntity == null) {
	            throw new IllegalArgumentException("Invalid ApplicationNo or Transaction no");
	        } else {
	            if (validateSBIResponse(transactionDetailEntity, transactionDetailModel)) {
	                transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
	                transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
	                transactionDetailEntity.setStatus(Status.CLOSED.getValue());
	                transactionDetailEntity.setSbiRefNo(transactionDetailModel.getSbiResponseMap().get("sbi_ref_no"));
	                transactionDetailEntity.setBankStatusMessage(transactionDetailModel.getSbiResponseMap().get("Status_desc"));
	                transactionDetailDAO.update(transactionDetailEntity);
	                responseModel = new ResponseModel<String>(ResponseModel.SUCCESS,
	                		transactionDetailModel.getSbiResponseMap().get("Status_desc"));
	                UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
	                userSessionEntity.setCompletionStatus(Status.PENDING.getValue());
	                usersessionDAO.update(userSessionEntity);
	                try {
						sendSMSEmail(appEntity , transactionDetailEntity);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } else {
	                if (transactionDetailModel.getSbiResponseMap().get("Status").equalsIgnoreCase("PENDING")) {
	                    transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
	                    transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
	                    transactionDetailEntity.setStatus(Status.PENDING.getValue());
	                    transactionDetailEntity.setSbiRefNo(transactionDetailModel.getSbiResponseMap().get("sbi_ref_no"));
	                    transactionDetailEntity.setBankStatusMessage(transactionDetailModel.getSbiResponseMap().get("Status_desc"));
	                    transactionDetailDAO.update(transactionDetailEntity);
	                    responseModel = new ResponseModel<String>(ResponseModel.PENDING,
	                            transactionDetailModel.getSbiResponseMap().get("Status_desc"));
	                    /*transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
		                transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
		                transactionDetailEntity.setSbiRefNo(transactionDetailModel.getSbiResponseMap().get("sbi_ref_no"));
		                transactionDetailEntity.setBankStatusMessage(transactionDetailModel.getSbiResponseMap().get("Status_desc"));
		                transactionDetailDAO.update(transactionDetailEntity);
		                responseModel = new ResponseModel<String>(ResponseModel.SUCCESS,
		                        transactionDetailModel.getSbiResponseMap().get("Status_desc"));*/
		                UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
		                userSessionEntity.setCompletionStatus(Status.PENDING.getValue());
		                usersessionDAO.update(userSessionEntity);
		                /*try {
							sendSMSEmail(appEntity , transactionDetailEntity);
						} catch (Exception e) {
							e.printStackTrace();
						}*/
		            } else {
	                    transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
	                    transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
	                    transactionDetailEntity.setStatus(Status.FAILURE.getValue());
	                    transactionDetailEntity.setSbiRefNo(transactionDetailModel.getSbiResponseMap().get("sbi_ref_no"));
	                    transactionDetailEntity
	                            .setBankStatusMessage(transactionDetailModel.getSbiResponseMap().get("Status_desc"));
	                    transactionDetailDAO.update(transactionDetailEntity);
	                    responseModel = new ResponseModel<String>(ResponseModel.FAILED,
	                            transactionDetailModel.getSbiResponseMap().get("Status_desc"));
	                    log.debug(":::CITIZEN:::updatePaymentProcess:::: failure:::::::"
	                            + transactionDetailModel.getSbiResponseMap().get("Status_desc"));
	                }
	            }
	        }
			return responseModel;
		}

		public Boolean validateSBIResponse(TransactionDetailEntity persistTransDtlEntity,
	            TransactionDetailModel transactionDetailModel) {
	        log.info(":CITIZEN:::updatePaymentProcess::::validateSBIResponse::::");
	        if (transactionDetailModel.getSbiResponseMap().get("Status").equalsIgnoreCase("SUCCESS")) {
	            log.info(":CITIZEN:::updatePaymentProcess::validateSBIResponse::::Status:::True::");
	            return true;
	        } else {
	            log.info(":CITIZEN:::updatePaymentProcess::validateSBIResponse::::False::::");
	            return false;
	        }
	    }
		
		public void sendSMSEmail(ApplicationEntity appEntity , TransactionDetailEntity transactionDetailEntity){
			log.info("::sendSMSEmail afetr payment::start ::::");
			if(transactionDetailEntity.getPaymentType() == PaymentType.TOW_BUYER.getId()){
				log.debug("::::sendSMSEmail:::TOW_BUYER::::");
		    	CustMsgModel custModel=null;
		    	boolean msgSend=false;
		    	custModel = communicationService.getCustInfo(Status.FRESH, appEntity , false , FormCodeType.OTS_FORM.getLabel());
				msgSend = communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS_EMAIL, custModel);
				log.info("::::sendSMSEmail::end:: TOW_BUYER:: " + msgSend);
			}
			
			if(appEntity.getServiceCategory().equalsIgnoreCase(ServiceCategory.PERMIT_FITNESS_CATEGORY.getCode())){
				log.info("::::sendSMSEmail:after payment::PERMIT_FITNESS_CATEGORY::");
		    	boolean msgSend=false;
		    	PermitHeaderModel pukkaPermit = null;
		    	try {
					RegistrationServiceResponseModel<List<PermitHeaderModel>> pRes = registrationService.getPermitDetails(appEntity.getLoginHistory().getVehicleRcId());
					log.info(":::11111:: " + pRes);
					if(pRes.getHttpStatus().equals(HttpStatus.OK) && !ObjectsUtil.isNull(pRes.getResponseBody())
					        && pRes.getResponseBody().size() > 0){
						log.info("::2222222222::: " + pRes.getHttpStatus());
					    for(PermitHeaderModel permit: pRes.getResponseBody()){
					    	log.info(":333333333::: " + permit.getIsTempPermit());
					        if(!permit.getIsTempPermit()){
					            pukkaPermit = permit;
					        }
					    }
					}
				} catch (UnauthorizedException e) {
					e.printStackTrace();
				}
		        log.info("::Pukka permit:::" + pukkaPermit);
		    	if(pukkaPermit!= null && pukkaPermit.getPermitTypeId() != null && pukkaPermit.getPermitTypeId().equalsIgnoreCase("NP") || pukkaPermit.getPermitTypeId().equalsIgnoreCase("CSPP")){
						CustMsgModel custModel=null;
				    	custModel = communicationService.getCustInfo(Status.FRESH, appEntity , false , null);
						msgSend = communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS_EMAIL, custModel);
					}
					log.info("::::::sendSMSEmail:after payment::PERMIT_FITNESS_CATEGORY: " + msgSend);
	        }
			
			if(!StringsUtil.isNullOrEmpty(appEntity.getServiceCode()) && appEntity.getServiceCode().equals(ServiceType.FRESH_RC_FINANCIER.getCode())){
				log.debug("::::sendSMSEmail:::Fresh rc for financier sending sms::::");
		    	CustMsgModel custModel=null;
		    	boolean msgSend=false;
		    	custModel = communicationService.getCustInfo(Status.FRESH, appEntity , false , null);
		    	custModel.setCommunicationConfig(communicationService.getCommunicationConfig());
				msgSend = communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS_EMAIL, custModel);
				log.info("::::sendSMSEmail::end:: Fresh rc for financier sending sms:: " + msgSend);
			}
			
		}

		
		@Override
		@Transactional
		public ResponseModel<String> updatePayUPaymentProcess(TransactionDetailModel transactionDetailModel,
				ApplicationEntity appEntity) {
			ResponseModel<String> responseModel = null;
	        TransactionDetailEntity transactionDetailEntity =
	                transactionDetailDAO.getByTransNoNdAppNo(transactionDetailModel.getTransactionNo(), appEntity);
	        if (transactionDetailEntity == null) {
	            throw new IllegalArgumentException("Invalid ApplicationNo or Transaction no");
	        } else {
	            if (validatePayUResponse(transactionDetailEntity, transactionDetailModel)) {
	                transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
	                transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
	                transactionDetailEntity.setStatus(Status.CLOSED.getValue());
	            	transactionDetailEntity.setSbiRefNo(transactionDetailModel.getPayUTransactionDetails().getBank_ref_num());
	    			transactionDetailEntity.setBankStatusMessage(transactionDetailModel.getPayUTransactionDetails().getStatus());
	    			transactionDetailEntity.setPgType(PaymentGatewayType.PAYU.getLabel());
	    			transactionDetailDAO.update(transactionDetailEntity);
	                responseModel = new ResponseModel<String>(ResponseModel.SUCCESS,
	                		transactionDetailModel.getPayUTransactionDetails().getStatus());
	                UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
	                userSessionEntity.setCompletionStatus(Status.PENDING.getValue());
	                usersessionDAO.update(userSessionEntity);
	                try {
						sendSMSEmail(appEntity , transactionDetailEntity);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } else {
	                if (transactionDetailModel.getStatus().equalsIgnoreCase("PENDING")) {
	                    transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
	                    transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
	                    transactionDetailEntity.setStatus(Status.PENDING.getValue());
	                    transactionDetailEntity.setSbiRefNo(transactionDetailModel.getPayUTransactionDetails().getBank_ref_num());
		    			transactionDetailEntity.setBankStatusMessage(transactionDetailModel.getPayUTransactionDetails().getStatus());
	                    transactionDetailDAO.update(transactionDetailEntity);
	                    responseModel = new ResponseModel<String>(ResponseModel.PENDING,
	                    		transactionDetailModel.getPayUTransactionDetails().getStatus());
	                    log.debug(":::CITIZEN:::updatePaymentProcess::::: PENDING:::::::"
	                            + transactionDetailModel.getPayUTransactionDetails().getStatus());
	                	transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
		                transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
		                transactionDetailEntity.setStatus(Status.CLOSED.getValue());
		                transactionDetailEntity.setSbiRefNo(transactionDetailModel.getPayUTransactionDetails().getBank_ref_num());
		    			transactionDetailEntity.setBankStatusMessage(transactionDetailModel.getPayUTransactionDetails().getStatus());
		                transactionDetailDAO.update(transactionDetailEntity);
		                responseModel = new ResponseModel<String>(ResponseModel.SUCCESS,
		                		transactionDetailModel.getPayUTransactionDetails().getStatus());
		                UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
		                userSessionEntity.setCompletionStatus(Status.PENDING.getValue());
		                usersessionDAO.update(userSessionEntity);
		                /*try {
							sendSMSEmail(appEntity , transactionDetailEntity);
						} catch (Exception e) {
							e.printStackTrace();
						}*/
		            } else {
	                    transactionDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
	                    transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
	                    transactionDetailEntity.setStatus(Status.FAILURE.getValue());
	                    transactionDetailEntity.setSbiRefNo(transactionDetailModel.getPayUTransactionDetails().getBank_ref_num());
		    			transactionDetailEntity.setBankStatusMessage(transactionDetailModel.getPayUTransactionDetails().getStatus());
	                    transactionDetailDAO.update(transactionDetailEntity);
	                    responseModel = new ResponseModel<String>(ResponseModel.FAILED,
	                    		transactionDetailModel.getPayUTransactionDetails().getStatus());
	                    log.debug(":::CITIZEN:::updatePaymentProcess:::: failure:::::::"
	                            + transactionDetailModel.getPayUTransactionDetails().getStatus());
	                }
	            }
	        }
			return responseModel;
		}
		
		public Boolean validatePayUResponse(TransactionDetailEntity persistTransDtlEntity,
				TransactionDetailModel transactionDetailModel) {
			log.info(":::::::::::validatePayUResponse:::::::::::");

			if ("success".equalsIgnoreCase(transactionDetailModel.getPayUTransactionDetails().getStatus())) {
				log.info(":::::::::::validatePayUResponse::::Status:::True::::");
				return true;
			} else {
				log.info(":::::::::::validatePayUResponse:::::::False::::");
				return false;
			}
		}

		@Override
		@Transactional
		public void updatePayUFaliureResponse(String txnNumber , ApplicationEntity applicationEntity) {
			TransactionDetailEntity transactionDetailEntity = transactionDetailDAO.getByTransNoNdAppNo(txnNumber , applicationEntity);
			if (transactionDetailEntity == null) {
				throw new IllegalArgumentException(
						"Invalid Transaction no:" + txnNumber);
			}
			transactionDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			transactionDetailEntity.setStatus(Status.FAILURE.getValue());
			transactionDetailEntity.setBankStatusMessage("Failed");
			transactionDetailEntity.setPgType(PaymentGatewayType.PAYU.getLabel());
			transactionDetailDAO.update(transactionDetailEntity);
		}

}

package org.rta.citizen.common.controller;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.enums.PaymentGatewayType;
import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.payment.TransactionDetailModel;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.payment.PaymentService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.vehiclealteration.service.VehicleAlterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayUPaymentController {

	private static final Logger log = Logger.getLogger(PaymentController.class);
	
	@Autowired
	private PaymentService paymentService;

	@Autowired
    protected RegistrationService registrationService;

    @Autowired
    protected VehicleAlterationService vehicleAlterationService;
    
    @Autowired
    private ApplicationService applicationService;
    
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@RequestMapping(path = "/payu/payment/paytaxfee", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> payUPayTaxFee(@RequestHeader("Authorization") String token , @RequestParam(name = "appNo", required = false) String appNo) {
		Long sessionId = 0l;
		if(appNo == null)
		sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.debug("::CITIZEN:::payUPayTaxFee:::: SessionId " + sessionId);
		TransactionDetailModel transactionDetailModel = new TransactionDetailModel();
		ResponseModel<TransactionDetailModel> response = null;
		try {
			transactionDetailModel = paymentService.createPaymentRequest(sessionId , appNo , PaymentGatewayType.PAYU);
			response = new ResponseModel<>(ResponseModel.SUCCESS,transactionDetailModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,transactionDetailModel);
			e.printStackTrace();
		}
		if(transactionDetailModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,transactionDetailModel);
		}
		log.debug(":CITIZEN::payUPayTaxFee::::Response " + response);
		return ResponseEntity.ok(response);
	}

	
	@RequestMapping(path = "/payu/payment/payresponse/task/{taskDef}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getPayUPaymentResponse(@RequestHeader("Authorization") String token,
			@RequestBody TransactionDetailModel transactionDetailModel, @PathVariable("taskDef") String taskDef) {
	    log.debug(" ::::getPayUPaymentResponse:::: ");
		String userName = null;
        TokenType tokenType = null; 
        try{
            tokenType  = TokenType.valueOf(jwtTokenUtil.getTokenType(token));
            if(tokenType == TokenType.CITIZEN){
                userName = CitizenConstants.CITIZEN_USERID;
            } else {
                userName = jwtTokenUtil.getUserNameFromRegToken(token);
            }
        } catch(Exception ex){
            userName = jwtTokenUtil.getUserNameFromRegToken(token);
        }
        Long sessionId = getSession(token, transactionDetailModel.getAppNo());
		log.debug(":::getPayUPaymentResponse::: SessionId " + sessionId);
		ResponseModel<String> response = null;
		try {
			response = paymentService.processPaymentResponse(transactionDetailModel, sessionId);
			log.debug("payment service response status : " + response.getStatus());
			if (response.getStatus().equals(ResponseModel.SUCCESS)) {
			    try{
			        log.debug("payment service setting activiti : " + ", sessionId : " + sessionId + ", takDef : " + taskDef + ", userName : " + userName);
			        response.setActivitiTasks(paymentService.callAfterPaymentSuccess(sessionId, taskDef, userName));
			    } catch(Exception e){
			        log.error("Exception while completing other tasks after payment...: " + e.getMessage());
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseModel<>(ResponseModel.FAILED,"CITZEN Exception while saving paymentresponse Transaction Details :: " + e.getMessage());
		}
		log.debug(":CITIZEN::getPayUPaymentResponse::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	/**
     * If appnumber is found then sessionId is returned using appNumber else using token
     * @param token
     * @param appNumber
     * @return
     */
    private Long getSession(String token, String appNumber) {
        if (!StringsUtil.isNullOrEmpty(appNumber)) {
            return applicationService.getSession(appNumber).getSessionId();
        } else {
            return jwtTokenUtil.getSessionIdFromToken(token);
        }
    }
    
    @RequestMapping(path = "/payu/payment/payverification/task/{taskDef}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> payuPayVerification(@RequestHeader("Authorization") String token , @RequestParam(name = "appNo", required = false) String appNo
			, @PathVariable("taskDef") String taskDef) {
		Long sessionId = 0l;
		if(appNo == null)
		sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info("::CITIZEN:::payuPayVerification:::: SessionId " + sessionId);
		String userName = null;
        TokenType tokenType = null; 
        try{
            tokenType  = TokenType.valueOf(jwtTokenUtil.getTokenType(token));
            if(tokenType == TokenType.CITIZEN){
                userName = CitizenConstants.CITIZEN_USERID;
            } else {
                userName = jwtTokenUtil.getUserNameFromRegToken(token);
            }
        } catch(Exception ex){
            userName = jwtTokenUtil.getUserNameFromRegToken(token);
        }
		ResponseModel<String> response = null;
		try {
			response = paymentService.payUPaymentVerificationReq(sessionId , appNo);
			log.debug("payment service response status : " + response.getStatus());
			if (response.getStatus().equals(ResponseModel.SUCCESS)) {
			    try{
			        log.debug("payment service setting activiti : " + ", sessionId : " + sessionId + ", takDef : " + taskDef + ", userName : " + userName);
			        response.setActivitiTasks(paymentService.callAfterPaymentSuccess(sessionId, taskDef, userName));
			    } catch(Exception e){
			        log.error("Exception while completing other tasks after payment...: " + e.getMessage());
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseModel<>(ResponseModel.FAILED,"CITZEN Exception while saving paymentresponse Transaction Details :: " + e.getMessage());
		}
		log.debug(":CITIZEN::getPayUPaymentResponse::::Response " + response);
		return ResponseEntity.ok(response);
	}
	}

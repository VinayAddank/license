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
public class PaymentController {

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

	@RequestMapping(path = "/payment/paytaxfee", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> payTaxFee(@RequestHeader("Authorization") String token , @RequestParam(name = "appNo", required = false) String appNo) {
		Long sessionId = 0l;
		if(appNo == null)
		sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.debug("::CITIZEN:::payTaxFee:::: SessionId " + sessionId);
		TransactionDetailModel transactionDetailModel = new TransactionDetailModel();
		ResponseModel<TransactionDetailModel> response = null;
		try {
			transactionDetailModel = paymentService.createPaymentRequest(sessionId , appNo , PaymentGatewayType.SBI);
			response = new ResponseModel<>(ResponseModel.SUCCESS,transactionDetailModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,transactionDetailModel);
			e.printStackTrace();
		}
		if(transactionDetailModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,transactionDetailModel);
		}
		//------------- LOGGING ------------------------------
		applicationService.createLog(response.getStatus() + "_BEFORE_PAYMENT", response.getMessage(), sessionId, response.getStatusCode());
		//---------------------------------------------------
		log.debug(":CITIZEN::payTaxFee::::Response " + response);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(path = "/payment/payresponse/task/{taskDef}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getPaymentResponse(@RequestHeader("Authorization") String token,
			@RequestBody TransactionDetailModel transactionDetailModel, @PathVariable("taskDef") String taskDef) {
	    log.debug(" ::::getPaymentResponse:::: ");
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
		log.debug(":::getPaymentResponse::: SessionId " + sessionId);
		ResponseModel<String> response = null;
		try {
			transactionDetailModel.setPgType(PaymentGatewayType.SBI.getLabel());
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
		log.debug(":CITIZEN::getPaymentResponse::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(path = "/payment/payverification", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> payVerification(@RequestHeader("Authorization") String token , @RequestParam(name = "appNo", required = false) String appNo) {
		Long sessionId = 0l;
		if(appNo == null)
		sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info("::CITIZEN:::payVerification:::: SessionId " + sessionId);
		TransactionDetailModel transactionDetailModel = new TransactionDetailModel();
		ResponseModel<TransactionDetailModel> response = null;
		try {
			transactionDetailModel = paymentService.paymentVerificationReq(sessionId , appNo);
			response = new ResponseModel<>(ResponseModel.SUCCESS,transactionDetailModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,transactionDetailModel);
			e.printStackTrace();
		}
		if(transactionDetailModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,transactionDetailModel);
		}
		log.info(":CITIZEN::payVerification::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(path = "/payment/payverifyresponse/task/{taskDef}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getPaymentVerifyResponse(@RequestHeader("Authorization") String token,
			@RequestBody TransactionDetailModel transactionDetailModel, @PathVariable("taskDef") String taskDef) {
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
        Long sessionId = 0l;
		sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info(":::getPaymentVerifyResponse::: SessionId " + sessionId);
		ResponseModel<String> response = null;
		try {
			transactionDetailModel.setPgType(PaymentGatewayType.SBI.getLabel());
			response = paymentService.processPaymentVerifyResponse(transactionDetailModel, sessionId);
			if (response.getStatus().equals(ResponseModel.SUCCESS)) {
			    try{
			        response.setActivitiTasks(paymentService.callAfterPaymentSuccess(sessionId, taskDef, userName));
			    } catch(Exception e){
			        log.error("Exception while completing other tasks after payment...: " + e.getMessage());
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseModel<>(ResponseModel.FAILED,"CITZEN Exception while saving paymentresponse Transaction Details :: " + e.getMessage());
		}
		log.info(":CITIZEN::getPaymentResponse::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	//#TODO delete it later
	@RequestMapping(path = "/application/test/{sessionId}/{taskDef}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> testAfterPayment(@RequestHeader("Authorization") String token, @PathVariable("sessionId") Long sessionId, @PathVariable("taskDef") String taskDef){
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
	    paymentService.callAfterPaymentSuccess(sessionId, taskDef, userName);
	    return ResponseEntity.ok().build();
	}
	
	//#TODO delete it later
		@RequestMapping(path = "/application/test/dttax/{sessionId}", method = RequestMethod.GET, produces = {
	            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
		public ResponseEntity<?> testisDtTax(@RequestHeader("Authorization") String token, @PathVariable("sessionId") Long sessionId){
		    Boolean isDT = paymentService.isPayDiffTaxForVA(sessionId);
		    ResponseModel<Boolean> response = new ResponseModel<>(ResponseModel.SUCCESS, isDT);
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
}

/**
 * 
 */
package org.rta.citizen.ownershiptransfer.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ApplicationNotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.GeneralDetails;
import org.rta.citizen.common.model.ReceiptModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SellerAuthModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.DetailsService;
import org.rta.citizen.common.service.DetailsServiceFactory;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.ownershiptransfer.model.OtPermitModel;
import org.rta.citizen.ownershiptransfer.service.OTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author arun.verma
 *
 */

@RestController
public class OTController {

	private static final Logger log = Logger.getLogger(OTController.class);
    
    @Autowired
    private OTService oTService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private ActivitiService activitiService;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private DetailsServiceFactory detailsServiceFactory;
    
    @Value("${activiti.citizen.ot.code.generatetoken}")
    private String generateTokenTaskDef;
   
    @RequestMapping(path = "/application/generatetoken", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> generateToken(@RequestHeader("Authorization") String token, @RequestHeader("clientIp") String ip) {
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        log.info("::generateToken::::::start:::: ");
        ResponseModel<ReceiptModel> response = new ResponseModel<ReceiptModel>(ResponseModel.SUCCESS);
        ReceiptModel receipt = null;
        try {
            receipt = oTService.generateToken(sessionId, ip);
            response.setData(receipt);
            if(!ObjectsUtil.isNull(receipt)){
                //------complete activiti task -------------
            	log.info(":::GOING TO COMPLETE TASK::");
                Assignee assignee = new Assignee();
                assignee.setUserId(CitizenConstants.CITIZEN_USERID);
                String instanceId = applicationService.getProcessInstanceId(sessionId);
                ActivitiResponseModel<List<RtaTaskInfo>> actResponse = activitiService.completeTask(assignee, generateTokenTaskDef, instanceId, true, null);
                response.setActivitiTasks(actResponse.getActiveTasks());
            }
        } catch (ApplicationNotFoundException e) {
            log.error("Application not found");
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    
    @RequestMapping(path = "/application/ottoken/{appnumber}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> generateToken(@PathVariable("appnumber") String appNumber) {
        Long sessionId = getSession(null, appNumber);
        if (ObjectsUtil.isNull(sessionId)) {
            log.error("invalid session id : " + sessionId);
            throw new IllegalArgumentException("no session found for this application number");
        }
        ResponseModel<ReceiptModel> response = new ResponseModel<ReceiptModel>(ResponseModel.SUCCESS);
        ReceiptModel receipt = null;
        try {
            receipt = oTService.getTokenReceipt(sessionId);
            response.setData(receipt);
        } catch (ApplicationNotFoundException e) {
            log.error("Application not found");
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    
    @RequestMapping(path = "/ots/buyer/generaldetails", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getGeneralDetailsByToken(@RequestHeader("token") String token,
            @RequestHeader("Authorization") String authToken) throws UnauthorizedException{
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        CitizenApplicationModel citizenApp = oTService.getCitizenAppByOTSToken(token);
        if (ObjectsUtil.isNull(citizenApp)) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Application Not Found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (ObjectsUtil.isNull(citizenApp.getServiceType())) {
            return ResponseEntity.notFound().build();
        }
        String aadharNumber = "";
        GeneralDetails generalDetails;
        try {
            DetailsService detailsService = detailsServiceFactory.getDetailsService(citizenApp.getServiceType());
            if (ObjectsUtil.isNull(detailsService)) {
                log.debug("No DetailsService found for service type  : " + citizenApp.getServiceType());
                return ResponseEntity.badRequest().build();
            }
            generalDetails = detailsService.getDetails(aadharNumber, citizenApp.getUniqueKey());
            if (ObjectsUtil.isNull(generalDetails)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<GeneralDetails>(ResponseModel.FAILED, null, "not found"));
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<GeneralDetails>(ResponseModel.FAILED, null, "bad request"));
        }
        return ResponseEntity.ok(new ResponseModel<GeneralDetails>(ResponseModel.SUCCESS, generalDetails));
    }
    
    @RequestMapping(value = "/ots/approve/buyer/app/{app_no}/action/{status}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> approveBuyer(@RequestBody SellerAuthModel applicant, @PathVariable("app_no") String appNo,
    		@PathVariable("status") String status1) throws UnauthorizedException{
    	
    	
        ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>();
        if(ObjectsUtil.isNull(applicant) || ObjectsUtil.isNull(applicant.getAadharNumber())){
            response = new ResponseModel<>(ResponseModel.FAILED, null, "Invalid Buyer Details !!!");
            return ResponseEntity.ok(response);
        }
        Status status = Status.getStatus(status1);
        if(ObjectsUtil.isNull(status) || !(status == Status.APPROVED || status == Status.REJECTED)){
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Invalid Status : " + status1);
            return ResponseEntity.ok(response);
        }
        CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNo);
        if(ObjectsUtil.isNull(citizenApp)){
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("Application Not Found");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response = oTService.approveBuyer(appNo, applicant, status);
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
    
    /**
     * @param appNumber
     * @param authToken
     * @return  permit should be transfer/surrender
     */
    
    @RequestMapping(path = "/application/ot/permitoption/{mandalCode}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPermitOption(@RequestParam(name = "appNumber", required = false) String appNumber,
    		@RequestHeader("Authorization") String authToken, @PathVariable("mandalCode")Integer mandalCode) {
        
    	Long sessionId = getSession(authToken, appNumber);
        if (ObjectsUtil.isNull(sessionId)) {
            log.error("invalid session id : " + sessionId);
            throw new IllegalArgumentException("no session found for this application number");
        }
        ResponseModel<OtPermitModel> response = new ResponseModel<OtPermitModel>(ResponseModel.SUCCESS);
        try {
        	response.setData(oTService.getPermitOptions(sessionId, mandalCode));
        }catch(Exception e) {
            log.error("Application not found");
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * @param appNumber
     * @param authToken
     * @return  permit should be transfer/surrender in case of OTD
     */
    @RequestMapping(path = "/application/ot/permitoption", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPermitOption(@RequestParam(name = "appNumber", required = false) String appNumber,
            @RequestHeader("Authorization") String authToken) {
        
        Long sessionId = getSession(authToken, appNumber);
        if (ObjectsUtil.isNull(sessionId)) {
            log.error("invalid session id : " + sessionId);
            throw new IllegalArgumentException("no session found for this application number");
        }
        ResponseModel<OtPermitModel> response = new ResponseModel<OtPermitModel>(ResponseModel.SUCCESS);
        try {
            response.setData(oTService.getPermitOptions(sessionId, null));
        }catch(Exception e) {
            log.error("Application not found");
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
}

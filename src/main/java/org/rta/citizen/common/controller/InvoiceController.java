package org.rta.citizen.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.model.InvoiceModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.InvoiceService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InvoiceController {

	private static final Logger log = Logger.getLogger(InvoiceController.class);
	
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private ActivitiService activitiService;
	
	@Autowired
	ApplicationDAO applicationDAO;
	
	

	@RequestMapping(path = "/invoice", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getInvoice(@RequestHeader(name = "Authorization", required = false) String token , @RequestParam(name = "appNo", required = false) String appNo) {
		Long sessionId = 0l;
		if(appNo == null)
		sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info(":CITIZEN::getInvoice:: sessionId: " + sessionId +" appNo " + appNo);
		InvoiceModel invoiceModel = new InvoiceModel();
		ResponseModel<InvoiceModel> response = null;
		if(ObjectsUtil.isNull(sessionId) && ObjectsUtil.isNull(appNo)){
			log.error("Either appNumber or sessioId required !!!");
			response = new ResponseModel<>(ResponseModel.FAILED, null, "Either application number or valid authorization token required !!!");
			return ResponseEntity.ok(response);
		}
		try {
			invoiceModel = invoiceService.getInvoiceDetails(sessionId , appNo);
			response = new ResponseModel<>(ResponseModel.SUCCESS,invoiceModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel, "Unknown error occured !!!");
			e.printStackTrace();
		}
		if(invoiceModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel, "No Data Found !!!");
		}
		if(response.getStatus().equals(ResponseModel.SUCCESS)){
		    try{
		        ApplicationEntity appEntity = null;
		        if(appNo == null){
		            appEntity = applicationDAO.getApplicationFromSession(sessionId);
		        } else {
		            appEntity = applicationDAO.getApplication(appNo);
		        }
		        Map<String, Object> variableMap = new HashMap<>();
		        variableMap.put(ActivitiService.ITERATION, ObjectsUtil.isNull(appEntity.getIteration()) ? 0 : appEntity.getIteration());
		        if(!ObjectsUtil.isNull(appEntity.getRtaOfficeCode())){
		            variableMap.put(ActivitiService.RTA_OFFICE_CODE, appEntity.getRtaOfficeCode());
		        }
		        activitiService.setProcessVariables(appEntity.getExecutionId(), variableMap);
		    } catch(Exception ex){
		        log.error("Exception while setting variable in activiti................");
		    } 
		}
		log.info(":CITIZEN::getFeeTaxCalculation::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(path = "/receipt/{appNo}", method = RequestMethod.GET, produces = {
	        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getReceipt(@PathVariable("appNo") String appNo) {
	    log.info(":CITIZEN::getReceipt:: appNo " + appNo);
	    InvoiceModel invoiceModel = new InvoiceModel();
	    ResponseModel<InvoiceModel> response = null;
	    try {
	        invoiceModel = invoiceService.getInvoiceDetails(0L , appNo);
	        response = new ResponseModel<>(ResponseModel.SUCCESS,invoiceModel);
	    } catch (Exception e) {
	        response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel);
	        e.printStackTrace();
	    }
	    if(invoiceModel == null){
	        response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel);
	    }
	    log.info(":CITIZEN::getReceipt::::Response " + response);
	    return ResponseEntity.ok(response);
	}
	
	
	
    @RequestMapping(path = "/dl/receipt/details", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getDLInvoice(@RequestHeader("Authorization") String token,
            @RequestParam(name = "appnumber", required = false) String appNumber) {
        InvoiceModel invoiceModel = new InvoiceModel();
        ResponseModel<InvoiceModel> response = null;
        try {
            if (!StringsUtil.isNullOrEmpty(appNumber)) {
                log.info(":CITIZEN::getDLInvoice:: appNumber: " + appNumber);
                invoiceModel = invoiceService.getInvoiceDLDetails(0L, appNumber);
            } else {
                Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
                log.info(":CITIZEN::getDLInvoice:: sessionId: " + sessionId);
                invoiceModel = invoiceService.getInvoiceDLDetails(sessionId, null);
            }
            response = new ResponseModel<>(ResponseModel.SUCCESS, invoiceModel);
        } catch (Exception e) {
            response = new ResponseModel<>(ResponseModel.FAILED, invoiceModel);
            e.printStackTrace();
        }
        if (invoiceModel == null) {
            response = new ResponseModel<>(ResponseModel.FAILED, invoiceModel);
        }
        log.info(":CITIZEN::getDLInvoice::::Response " + response);
        return ResponseEntity.ok(response);
    }
	
	@RequestMapping(path = "/invoice/users", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getUsersInvoice(@RequestHeader("Authorization") String token) {
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info(":CITIZEN::getUsersInvoice:: sessionId: " + sessionId);
		InvoiceModel invoiceModel = new InvoiceModel();
		ResponseModel<InvoiceModel> response = null;
		try {
			invoiceModel = invoiceService.getInvoiceUsersDetails(sessionId);
			response = new ResponseModel<>(ResponseModel.SUCCESS,invoiceModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel);
			e.printStackTrace();
		}
		if(invoiceModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel);
		}
		log.info(":CITIZEN::getUsersInvoice::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(path = "/receipt/users/{appNo}", method = RequestMethod.GET, produces = {
	        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getReceiptForUsers(@PathVariable("appNo") String appNo) {
	    log.info(":CITIZEN::getReceiptForUsers:: appNo " + appNo);
	    InvoiceModel invoiceModel = new InvoiceModel();
	    ResponseModel<InvoiceModel> response = null;
	    try {
	        invoiceModel = invoiceService.getReceiptUsersDetails(appNo);
	        response = new ResponseModel<>(ResponseModel.SUCCESS,invoiceModel);
	    } catch (Exception e) {
	        response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel);
	        e.printStackTrace();
	    }
	    if(invoiceModel == null){
	        response = new ResponseModel<>(ResponseModel.FAILED,invoiceModel);
	    }
	    log.info(":CITIZEN::getReceiptForUsers::::Response " + response);
	    return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/attachmentfornotify", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> attachmentsForNotify(@RequestHeader("Authorization") String token , @RequestParam(name = "appNo", required = false) String appNo, @RequestBody String attachment) {
		log.info("::attachmentsForNotify::::start:::::");
		Boolean response = invoiceService.attachments4Communication(appNo, attachment);
		log.info("::attachmentsForNotify::::end:::::");
		return ResponseEntity.ok(response);
	}
}

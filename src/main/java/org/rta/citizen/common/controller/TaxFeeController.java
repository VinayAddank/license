package org.rta.citizen.common.controller;

import org.apache.log4j.Logger;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.payment.DifferentialTaxFeeModel;
import org.rta.citizen.common.model.payment.TaxFeeModel;
import org.rta.citizen.common.service.payment.TaxFeeService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaxFeeController {

	private static final Logger log = Logger.getLogger(TaxFeeController.class);

	@Autowired
	private TaxFeeService taxFeeService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	

	@RequestMapping(path = "/taxfeecal/{isdispatch}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getFeeTaxCalculation(@RequestHeader("Authorization") String token , @PathVariable("isdispatch") Boolean isdispatch 
			, @RequestParam(name = "appNo", required = false) String appNo , @RequestParam(name = "quartelyType", required = false) Integer quartelyType) {
		Long sessionId = 0l;
		if(quartelyType == null)
		quartelyType = 0;
		if(appNo == null)
		sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.debug("::getFeeTaxCalculation:: sessionId: " + sessionId + " appNo " + appNo + " quartelyType " + quartelyType);
		TaxFeeModel feeTaxModel = new TaxFeeModel();
		ResponseModel<TaxFeeModel> response = null;
		try {
			feeTaxModel = taxFeeService.taxFeeCal(sessionId , isdispatch , appNo , quartelyType);
			response = new ResponseModel<>(ResponseModel.SUCCESS,feeTaxModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,feeTaxModel);
			e.printStackTrace();
		}
		if(feeTaxModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,feeTaxModel);
		}
		log.debug(":CITIZEN::getFeeTaxCalculation::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(path = "/license/taxfeecal", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getLicenseFeeTax(@RequestHeader("Authorization") String token) {
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.debug(":CITIZEN::getLicenseFeeTax: sessionId: " + sessionId);
		TaxFeeModel feeTaxModel = new TaxFeeModel();
		ResponseModel<TaxFeeModel> response = null;
		try {
			feeTaxModel = taxFeeService.licenseTaxFeeCal(sessionId);
			
			response = new ResponseModel<>(ResponseModel.SUCCESS,feeTaxModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,feeTaxModel);
			e.printStackTrace();
		}
		if(feeTaxModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,feeTaxModel);
		}
		log.debug(":CITIZEN::getLicenseFeeTax::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(path = "/user/taxfeecal", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getUserFeeTax(@RequestHeader("Authorization") String token) {
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info(":CITIZEN::getUserFeeTax:: sessionId: " + sessionId);
		TaxFeeModel feeTaxModel = new TaxFeeModel();
		ResponseModel<TaxFeeModel> response = null;
		try {
			feeTaxModel = taxFeeService.userTaxFeeCal(sessionId);
			
			response = new ResponseModel<>(ResponseModel.SUCCESS,feeTaxModel);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,feeTaxModel);
			e.printStackTrace();
		}
		if(feeTaxModel == null){
			response = new ResponseModel<>(ResponseModel.FAILED,feeTaxModel);
		}
		log.info(":CITIZEN::getUserFeeTax::::Response " + response);
		return ResponseEntity.ok(response);
	}
	
/*test mail and sms*/	
	@RequestMapping(path = "/testmail/{status}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> sendTestMail(@RequestHeader("Authorization") String token ,  @PathVariable("status") String status) {
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info(":CITIZEN::sendTestMail:: sessionId: " + sessionId);
		try {
			taxFeeService.testMailNdSMS(sessionId , status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(":CITIZEN::sendTestMail::::Response ");
		return ResponseEntity.ok("success");
	}
	
	@RequestMapping(path = "/differntialtax/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getLicense(@RequestParam(value="trnumber", required=true) String trNumber) {
		
		DifferentialTaxFeeModel model = taxFeeService.saveOrUpdateDifferentialTaxFee(trNumber, null);
		if(ObjectsUtil.isNull(model)){
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseModel<>(ResponseModel.FAILED));
		}
		return ResponseEntity.ok(model);
	}
	
	
}

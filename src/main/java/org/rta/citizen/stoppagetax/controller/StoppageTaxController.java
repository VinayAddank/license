package org.rta.citizen.stoppagetax.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxReportModel;
import org.rta.citizen.stoppagetax.service.StoppageTaxService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 
 * @author sohan.maurya created on Jul 5, 2017
 *
 */

@RestController
public class StoppageTaxController {
	
	private static final Logger logger = Logger.getLogger(StoppageTaxController.class);
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private StoppageTaxService stoppageTaxService;
	
	@Autowired
	private RegistrationService registrationService;
	
	//TODO this is use only testing purpose, after testing remove this code
	@RequestMapping(value = "/test/stoppage/tax", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> saveForm(@RequestParam(name = "applicationid", required = true) Long applicationId,
			@RequestParam(name = "prnumber", required = true) String prNumber) {
		
		ResponseModel<String> response = stoppageTaxService.saveOrUpdateStoppageTax(prNumber, applicationId);
		
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/stoppagetax/report/details", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> stoppageTaxReport(@RequestBody StoppageTaxReportModel model,
			@RequestHeader("Authorization") String token) {
		ResponseModel<String> response = null;
		 if(ObjectsUtil.isNull(model) || StringsUtil.isNullOrEmpty(model.getApplicationNo())){
	            response = new ResponseModel<>(ResponseModel.FAILED, null, "Invalid Stoppage Tax Report Details !!!");
	            return ResponseEntity.ok(response);
	     }
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		if(StringsUtil.isNullOrEmpty(userName)){
			 response = new ResponseModel<>(ResponseModel.FAILED, null, "Something is happened wrong");
	         return ResponseEntity.ok(response);
		}
		response = stoppageTaxService.stoppageTaxReportSync(model, userName, userId);
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/stoppagetax/report/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getStoppageTaxReport(@RequestParam(name="applicationno", required=true) String applicationNo) {
		if(StringsUtil.isNullOrEmpty(applicationNo)){
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<String>(ResponseModel.FAILED,"Application number is missing...!!" ));
	    }
		List<StoppageTaxReportModel> models = stoppageTaxService.getStoppageTaxReport(applicationNo);
		if(!ObjectsUtil.isNull(models)){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("count",  models.size());
			map.put("reports", models);
			return ResponseEntity.ok(new ResponseModel<Map<String, Object>>(ResponseModel.SUCCESS, map));
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseModel<String>(ResponseModel.FAILED,"Getting Something Wrong...!!" ));
	}
	
	@RequestMapping(value = "/stoppagetax/single/report/details/{reportid}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getStoppageTaxSingleReport(@PathVariable(value="reportid") Long reportId) {
		if(ObjectsUtil.isNull(reportId)){
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<String>(ResponseModel.FAILED,"Report Id is missing...!!" ));
	    }
		try{
			RegistrationServiceResponseModel<StoppageTaxReportModel> responseModel = registrationService.getStoppageTaxSingleReportDetails(reportId);
			if(responseModel.getHttpStatus() == HttpStatus.OK){
				return ResponseEntity.ok(new ResponseModel<StoppageTaxReportModel>(ResponseModel.SUCCESS, responseModel.getResponseBody()));
			}
		}catch (Exception e) {
			logger.error("Getting error in ");
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseModel<String>(ResponseModel.FAILED,"Getting Something Wrong...!!" ));
	}
	
	@RequestMapping(value = "/stoppagetax/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getStoppageTaxDetails(@RequestParam(name="applicationno", required=true) String applicationNo) {
		if(StringsUtil.isNullOrEmpty(applicationNo)){
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<String>(ResponseModel.FAILED,"Application number is missing...!!" ));
	    }
		StoppageTaxDetailsModel model = stoppageTaxService.getStoppageTaxDetails(applicationNo);
		if(!ObjectsUtil.isNull(model)){
			return  ResponseEntity.ok(new ResponseModel<StoppageTaxDetailsModel>(ResponseModel.SUCCESS, model));
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseModel<String>(ResponseModel.FAILED,"Getting Something Wrong...!!" ));
	}
}

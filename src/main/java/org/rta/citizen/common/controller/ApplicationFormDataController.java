/**
 * To handle data of all forms of all services
 */
package org.rta.citizen.common.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
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

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author arun.verma
 *
 */

@RestController
@RequestMapping(value = "/form")
public class ApplicationFormDataController {

	private static final Logger log = Logger.getLogger(ApplicationFormDataController.class);

	@Autowired
	private ApplicationFormDataService applicationFormDataService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private ApplicationService applicationService;

	@RequestMapping(value = "/{task_def}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> saveForm(@RequestHeader("Authorization") String token,
			@RequestBody ApplicationFormDataModel applicationFormDataModel, @PathVariable("task_def") String taskDef,
			@RequestParam(name = "appnumber", required = false) String appNumber) throws UnauthorizedException {
		log.info(":::saveForm::::::::: data " + applicationFormDataModel);
		String userName = null;
		TokenType tokenType = null;
		Long userId = null;
		try {
			userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenType(token));
			tokenType = TokenType.valueOf(jwtTokenUtil.getTokenType(token));
			if (tokenType == TokenType.CITIZEN) {
				userName = CitizenConstants.CITIZEN_USERID;
			} else {
				userName = jwtTokenUtil.getUserNameFromRegToken(token);
			}
		} catch (Exception ex) {
			userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenType(token));
			userName = jwtTokenUtil.getUserNameFromRegToken(token);
		}
		Long sessionId = getSession(token, appNumber);
		log.info("saving form data for sessionId : " + sessionId);
		ResponseModel<ApplicationFormDataModel> response = new ResponseModel<>();
		if (ObjectsUtil.isNull(sessionId)) {
			response.setStatus(ResponseModel.FAILED);
			response.setMessage("could not save form data");
			response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			response.setStatus(HttpStatus.UNAUTHORIZED.toString());
			return ResponseEntity.ok(response);
		}
		try {
			List<ApplicationFormDataModel> forms = new ArrayList<>();
			forms.add(applicationFormDataModel);
			response = applicationFormDataService.saveForm(forms, sessionId, userId);
		} catch (ServiceValidationException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setMessage(e.getErrorMsg());
			response.setStatusCode(e.getErrorCode());
			return ResponseEntity.ok(response);
		} catch (IOException | DataMismatchException | NotFoundException e) {
			e.printStackTrace();
			response.setStatus(ResponseModel.FAILED);
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		} catch (ConflictException e) {
			e.printStackTrace();
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(ResponseModel.FAILED);
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		}
		if(!applicationFormDataService.aadhaarSeedingAvailability(sessionId)){
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(ServiceValidation.AADHAAR_MATCH_WITH_RC.getCode());
			response.setMessage(ServiceValidation.AADHAAR_MATCH_WITH_RC.getValue());
			return ResponseEntity.ok(response);
		}
		// ---- for activiti --------------------
		if (response.getStatus().equalsIgnoreCase(ResponseModel.SUCCESS)
				&& !FormCodeType.FCF_MVI_FORM.getLabel().equalsIgnoreCase(taskDef)) {
			log.info(":form data activity start::::::");
			response.setActivitiTasks(completeFormActiviti(sessionId, taskDef, userName));
			log.info(":form data activity end::::::");
		}
		// ----------------------------------------
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/list/{task_def}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> saveFormList(@RequestHeader("Authorization") String token,
			@RequestBody List<ApplicationFormDataModel> applicationFormDataModelList,
			@PathVariable("task_def") String taskDef) throws UnauthorizedException {
		String userName = null;
		TokenType tokenType = null;
		Long userId = null;
		try {
			tokenType = TokenType.valueOf(jwtTokenUtil.getTokenType(token));
			userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenType(token));
			if (tokenType == TokenType.CITIZEN) {
				userName = CitizenConstants.CITIZEN_USERID;
			} else {
				userName = jwtTokenUtil.getUserNameFromRegToken(token);
			}
		} catch (Exception ex) {
			userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenType(token));
			userName = jwtTokenUtil.getUserNameFromRegToken(token);
		}
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		ResponseModel<ApplicationFormDataModel> response = new ResponseModel<>();
		try {
			response = applicationFormDataService.saveForm(applicationFormDataModelList, sessionId , userId);
		} catch (IOException | DataMismatchException | NotFoundException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		} catch (ConflictException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setStatus(ResponseModel.FAILED);
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		}
		if(!applicationFormDataService.aadhaarSeedingAvailability(sessionId)){
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(ServiceValidation.AADHAAR_MATCH_WITH_RC.getCode());
			response.setMessage(ServiceValidation.AADHAAR_MATCH_WITH_RC.getValue());
			return ResponseEntity.ok(response);
		}
		// ---- for activiti --------------------
		if (response.getStatus().equalsIgnoreCase(ResponseModel.SUCCESS)) {
			response.setActivitiTasks(completeFormActiviti(sessionId, taskDef, userName));
		}
		// ----------------------------------------
		return ResponseEntity.ok(response);
	}

	private List<RtaTaskInfo> completeFormActiviti(Long sessionId, String taskDef, String userName)
			throws UnauthorizedException {
		List<RtaTaskInfo> tasks = applicationFormDataService.completeFormDataActiviti(sessionId, taskDef, userName);
		return tasks;
	}

	@RequestMapping(value = "/{form_code}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getForm(@RequestHeader("Authorization") String token,
			@PathVariable("form_code") String formCode,
			@RequestParam(name = "applicationno", required = false) String applicationNumber)
			throws UnauthorizedException {
		ResponseModel<ApplicationFormDataModel> response = null;
		try {
			if (!StringsUtil.isNullOrEmpty(applicationNumber)) {
				response = applicationFormDataService.getApplicationFormData(applicationNumber, formCode);
			} else {
				response = applicationFormDataService
						.getApplicationFormDataBySessionId(jwtTokenUtil.getSessionIdFromToken(token), formCode);
			}
		} catch (IOException e) {
			response = new ResponseModel<ApplicationFormDataModel>(ResponseModel.FAILED);
			response.setMessage("Invalid Form Data !!!");
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/app/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getFormByAppNo(@RequestHeader("Authorization") String token,
			@PathVariable("app_no") String appNo) throws UnauthorizedException {
		ResponseModel<Map<String, Object>> response = null;
		try {
			response = applicationFormDataService.getAllForms(appNo);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseModel<Map<String, Object>>(ResponseModel.FAILED);
			response.setMessage("Invalid Form Data Found!!!");
		}
		return ResponseEntity.ok(response);
	}

	/**
	 * If appnumber is found then sessionId is returned using appNumber else
	 * using token
	 * 
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

	@RequestMapping(value = "/details/update", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> saveForm(@RequestBody ApplicationFormDataModel applicationFormDataModel,
			@RequestParam(name = "applicationno", required = true) String applicationNumber) {
		log.info(":::saveForm::::::::: data " + applicationFormDataModel);
		ResponseModel<ApplicationFormDataModel> response = new ResponseModel<>();
		if (StringsUtil.isNullOrEmpty(applicationNumber)) {
			response.setStatus(ResponseModel.FAILED);
			response.setMessage("Application Number can not null or empty..!!");
			return ResponseEntity.ok(response);
		}
		try {
			response = applicationFormDataService.saveUpdateForm(applicationFormDataModel, applicationNumber);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			response.setStatus(ResponseModel.FAILED);
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(ResponseModel.FAILED);
			response.setMessage(e.getMessage());
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.ok(response);
	}
	
    @RequestMapping(value = "/{form_code}/otd", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getFormDataForOTD(@RequestHeader("Authorization") String token, 
            @PathVariable("form_code") String formCode, @RequestParam(name = "applicationno", required = false) String applicationNumber) throws UnauthorizedException {
        ResponseModel<ApplicationFormDataModel> response = null;
        try {
            if (!StringsUtil.isNullOrEmpty(applicationNumber)) {
                response = applicationFormDataService.getApplicationFormData(applicationNumber, formCode);
            } else {
                response = applicationFormDataService.getApplicationFormDataBySessionId(jwtTokenUtil.getSessionIdFromToken(token), formCode);
            }
        } catch (IOException e) {
            response = new ResponseModel<ApplicationFormDataModel>(ResponseModel.FAILED);
            response.setMessage("Invalid Form Data !!!");
        }
        return ResponseEntity.ok(response.getData());
    }

}

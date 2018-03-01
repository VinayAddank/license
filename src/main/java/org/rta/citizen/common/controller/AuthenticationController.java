package org.rta.citizen.common.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.UserType;
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
import org.rta.citizen.common.model.AadhaarTCSDetailsRequestModel;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaProcessInfo;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.service.AuthenticationServiceFactory;
import org.rta.citizen.common.service.ServiceMasterService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

	private static final Logger log = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AuthenticationServiceFactory serviceFactory;

	@Value("${response.status.code.aadharnotfound}")
	public Integer CODE_AADHAR_NOT_FOUND = 101;

	@Value("${response.status.code.citizennotmapped}")
	public Integer CODE_CITIZEN_VEHICLE_MAPPING_ERROR = 102;

	@Value("${response.status.code.notfound}")
	public Integer CODE_NOT_FOUND = 103;

	@Value("${response.status.code.aadhaarauthfailed}")
	public Integer AADHAAR_AUTH_FAILED = 104;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ServiceMasterService serviceMasterService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private RegistrationService registrationService;

	@RequestMapping(value = "/{servicetype}/login", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> authenticate(@RequestBody AuthenticationModel model,
			@PathVariable(value = "servicetype") String st,
			@RequestParam(name = "userReq", required = false) String userReq)
			throws UnauthorizedException, ParseException, ConflictException {
		ServiceType serviceType = ServiceType.getServiceType(st);
		AuthenticationService service;
		if (ObjectsUtil.isNull(serviceType)) {
			return ResponseEntity.notFound().build();
		}

		String applicatonNumber = model.getApplicationNumber();
		if (!StringsUtil.isNullOrEmpty(applicatonNumber)) {
			model.setApplicationId(applicatonNumber);
		}

		service = serviceFactory.getAuthenticationService(serviceType, model.getUserType());
		if (ObjectsUtil.isNull(service)) {
			return ResponseEntity.notFound().build();
		}
		ResponseModel<TokenModel> responseModel;
		HashMap<String, Boolean> param = new HashMap<>();
		try {
			responseModel = service.authenticate(model, serviceType, param, userReq);

			Assignee assignee = new Assignee();
			assignee.setUserId(CitizenConstants.CITIZEN_USERID);
			String processId = serviceMasterService.getProcessId(serviceType);
			Long sessionId = jwtTokenUtil.getSessionIdFromToken(responseModel.getData().getToken());
			List<RtaTaskInfo> tasks = null;
			String instanceId = null;
			try{
			    instanceId = applicationService.getProcessInstanceId(sessionId);
			}catch(Exception ex){
			    log.error("Exception while getting execution id from application ......");
			}
			
			if (ObjectsUtil.isNull(instanceId)) {
				log.info("creating new process for ServiceType : " + serviceType);
				// ----create process---------
				ActivitiResponseModel<RtaProcessInfo> actResponse = activitiService.startProcess(assignee, processId,
						serviceType);
				tasks = actResponse.getActiveTasks();
				if(ObjectsUtil.isNull(tasks) || ObjectsUtil.isNull(tasks.get(0)) || ObjectsUtil.isNull(tasks.get(0).getProcessInstanceId())){
				    log.error("Error Unable to create task in activiti for service " + serviceType);
				}
				// ---- save executionId--------------
				applicationService.saveUpdateExecutionId(sessionId, tasks.get(0).getProcessInstanceId());
			} else {
				log.info("getting exiting process for ServiceType : " + serviceType);
				// --- get old process instance--------
				ActivitiResponseModel<List<RtaTaskInfo>> actResponse = null;
				if (serviceType == ServiceType.OWNERSHIP_TRANSFER_SALE && model.getUserType() == UserType.ROLE_BUYER) {
					String taskDef = "claim_token";
					actResponse = activitiService.completeTask(assignee, taskDef, instanceId, true, null);
					tasks = actResponse.getActiveTasks();
				}
				actResponse = activitiService.getActiveTasks(instanceId);
				tasks = actResponse.getData();
			}
			responseModel.setActivitiTasks(tasks);
			log.debug("done with activiti task for ServiceType: " + serviceType);
		} catch (ForbiddenException e) {
			log.error("limit exceeded");
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage("login limit exceeded");
			responseModel.setStatusCode(HttpStatus.FORBIDDEN.value());
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", "login limit exceeded");
			headers.add("code", HttpStatus.FORBIDDEN.value() + "");
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.FORBIDDEN);
		} catch (UnauthorizedException e) {
			log.error("unauthorized");
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", HttpStatus.UNAUTHORIZED.value() + "");
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.UNAUTHORIZED);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", HttpStatus.UNAUTHORIZED.value() + "");
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.BAD_REQUEST);
		} catch (AadharNotFoundException e) {
			log.error(e);
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(CODE_AADHAR_NOT_FOUND);
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", String.valueOf(CODE_AADHAR_NOT_FOUND));
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.ACCEPTED);
		} catch (DataMismatchException e) {
			log.error(e);
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(CODE_CITIZEN_VEHICLE_MAPPING_ERROR);
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", String.valueOf(CODE_CITIZEN_VEHICLE_MAPPING_ERROR));
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.ACCEPTED);
		} catch (NotFoundException e) {
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(CODE_NOT_FOUND);
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", String.valueOf(CODE_NOT_FOUND));
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.ACCEPTED);
		} catch (AadharAuthenticationFailedException e) {
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			HttpHeaders headers = new HttpHeaders();
			if (!ObjectsUtil.isNull(e.getAadharModel())) {
				Map<String, String> errors = new HashMap<>();
				errors.put("auth_status", e.getAadharModel().getAuth_status());
				errors.put("auth_err_code", e.getAadharModel().getAuth_err_code());
				responseModel.setErrors(errors);
				responseModel.setMessage(e.getAadharModel().getAuth_err_code());
				headers.add("message", e.getAadharModel().getAuth_err_code());
			}
			responseModel.setStatusCode(AADHAAR_AUTH_FAILED);
			headers.add("code", AADHAAR_AUTH_FAILED + "");
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.ACCEPTED);
		} catch (VehicleNotFinanced | FinancerNotFound e) {
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.FORBIDDEN.value());
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", HttpStatus.FORBIDDEN.value() + "");
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.FORBIDDEN);
		} catch (ServiceValidationException e) {
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getErrorMsg());
			responseModel.setStatusCode(e.getErrorCode());
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", String.valueOf(e.getErrorCode()));
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.ACCEPTED);
		} catch (ConflictException e) {
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.value());
			HttpHeaders headers = new HttpHeaders();
			headers.add("message", e.getMessage());
			headers.add("code", HttpStatus.CONFLICT.value() + "");
			service.logAttempt(model, serviceType, responseModel.getStatus(), responseModel.getMessage(), responseModel.getStatusCode());
			return new ResponseEntity<>(responseModel, headers, HttpStatus.CONFLICT);
		}
		service.logAttempt(model, serviceType, ResponseModel.SUCCESS, ResponseModel.SUCCESS, HttpStatus.OK.value());
		return ResponseEntity.ok(responseModel);
	}

	@RequestMapping(value = "/authenticate/aadhar", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> authenticateAadhar(@RequestBody AadhaarTCSDetailsRequestModel model)
			throws UnauthorizedException, ParseException, ConflictException {
		RegistrationServiceResponseModel<AadharModel> res = registrationService.aadharAuthentication(model);
		ResponseModel<AadharModel> response = null;
		AadharModel adharModel = res.getResponseBody();
		if (res.getHttpStatus().equals(HttpStatus.OK)) {
			response = new ResponseModel<>(ResponseModel.SUCCESS, adharModel);
		} else {
			response = new ResponseModel<>(ResponseModel.FAILED, adharModel);
			response.setStatusCode(res.getHttpStatus().value());
		}
		return ResponseEntity.ok(response);
	}
}

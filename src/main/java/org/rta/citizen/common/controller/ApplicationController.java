package org.rta.citizen.common.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.UserSessionService;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.DealerModel;
import org.rta.citizen.common.model.GeneralDetails;
import org.rta.citizen.common.model.OwnerConscent;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.ServiceMasterModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.DetailsService;
import org.rta.citizen.common.service.DetailsServiceFactory;
import org.rta.citizen.common.service.ServiceMasterService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.freshrc.FreshRCPRDetailService;
import org.rta.citizen.freshrc.FreshRCTRDetailService;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.exception.SlotUnavailableException;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.SlotService;
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
import org.springframework.web.client.RestClientException;

import javassist.NotFoundException;

@RestController
public class ApplicationController {

	private static final Logger log = Logger.getLogger(ApplicationController.class);

	@Autowired
	private DetailsServiceFactory detailsServiceFactory;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ServiceMasterService serviceMasterService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private FreshRCTRDetailService freshRCTRDetailService;

	@Autowired
	private FreshRCPRDetailService freshRCPRDetailService;

	@Autowired
	private UserSessionService userSessionService;

	@Autowired
	private SlotService slotService;

	@Value("${activiti.citizen.task.code.bodybuilder}")
	private String taskBodyBuilder;

	@Value("${activiti.citizen.task.code.ownerconscent}")
	private String taskOwnerConscent;
	
	public static final String CANCEL_REQ = "CANCEL";

	@RequestMapping(value = "/{servicetype}/application/{uniquekey}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getApplicationDetails(@PathVariable(value = "uniquekey") String uniqueKey,
			@PathVariable(value = "servicetype") String st, @RequestHeader("Authorization") String token)
			throws UnauthorizedException {
		ServiceType serviceType = ServiceType.getServiceType(st);
		if (ObjectsUtil.isNull(serviceType)) {
			return ResponseEntity.notFound().build();
		}
		String aadharNumber = getAadharNumber(token);
		GeneralDetails generalDetails;
		try {
			DetailsService detailsService = detailsServiceFactory.getDetailsService(serviceType);
			if (ObjectsUtil.isNull(detailsService)) {
				log.debug("No DetailsService found for service type  : " + serviceType);
				return ResponseEntity.badRequest().build();
			}
			generalDetails = detailsService.getDetails(aadharNumber, uniqueKey);
			if (ObjectsUtil.isNull(generalDetails)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ResponseModel<GeneralDetails>(ResponseModel.FAILED, null, "not found"));
			}
		} /*
			 * catch (ForbiddenException e) { log.info("limit exceeded"); return
			 * ResponseEntity.status(HttpStatus.FORBIDDEN).build(); } catch
			 * (UnauthorizedException e) { log.info("unauthorized exceeded");
			 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
			 */ catch (IllegalArgumentException e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseModel<GeneralDetails>(ResponseModel.FAILED, null, "bad request"));
		}
		ResponseModel<GeneralDetails> response = new ResponseModel<GeneralDetails>(ResponseModel.SUCCESS,
				generalDetails);
		return ResponseEntity.ok(response);
	}

	private String getAadharNumber(String token) {
		return "";
	}

	@RequestMapping(value = "/application/generaldetailsnext/task/{task_def}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> addToPaymentCart(@RequestHeader("Authorization") String token,
			@PathVariable("task_def") String taskDef,
			@RequestParam(name = "appnumber", required = false) String appNumber) {
		Long sessionId = getSession(token, appNumber);
		String userName = null;
		TokenType tokenType = null;
		try {
			tokenType = TokenType.valueOf(jwtTokenUtil.getTokenType(token));
			if (tokenType == TokenType.CITIZEN) {
				userName = CitizenConstants.CITIZEN_USERID;
			} else {
				userName = jwtTokenUtil.getUserNameFromRegToken(token);
			}
		} catch (Exception ex) {
			userName = jwtTokenUtil.getUserNameFromRegToken(token);
		}
		ResponseModel<Object> response = applicationService.generalDetailsNext(sessionId, taskDef, userName);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/search/data/app/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> searchApplication(@PathVariable("app_no") String appNo)
			throws UnauthorizedException, VehicleNotFinanced {
		ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
		CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNo);
		if (ObjectsUtil.isNull(citizenApp)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			response.setMessage("Application Not Found");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		AppSearchService appService = detailsServiceFactory.getAppSearchService(citizenApp.getServiceType());
		if (ObjectsUtil.isNull(appService)) {
			log.debug("Invalid Service Code  : " + citizenApp.getServiceType());
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Invalid Service Code");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		response = appService.getSearchApplication(appNo, citizenApp.getSessionId());
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/status/app/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getApplicationStatus(@PathVariable("app_no") String appNo)
			throws UnauthorizedException, IOException, VehicleNotFinanced {
		ResponseModel<ApplicationStatusModel> response = new ResponseModel<ApplicationStatusModel>(
				ResponseModel.FAILED);
		CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNo);
		if (ObjectsUtil.isNull(citizenApp)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			response.setMessage("Application Not Found");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		AppSearchService appService = detailsServiceFactory.getAppSearchService(citizenApp.getServiceType());
		if (ObjectsUtil.isNull(appService)) {
			log.debug("Invalid Service Code  : " + citizenApp.getServiceType());
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Invalid Service Code");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		try {
			response = appService.getApplicationStatus(appNo, citizenApp.getSessionId());

		} catch (NotFoundException e) {
			log.debug("registration number not found  : " + appNo);
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setMessage("registration number not found");
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/services", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getServiceList(
			@RequestParam(name = "service_category", required = false) String serviceCategory1) {
		ResponseModel<List<ServiceMasterModel>> response;
		ServiceCategory serviceCategory = null;
		if (!ObjectsUtil.isNull(serviceCategory1)) {
			serviceCategory = ServiceCategory.getServiceTypeCat(serviceCategory1.toUpperCase());
			if (ObjectsUtil.isNull(serviceCategory)) {
				response = new ResponseModel<List<ServiceMasterModel>>(ResponseModel.FAILED);
				response.setMessage("Invalid ServiceCategory : " + serviceCategory1);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			}
		}
		List<ServiceMasterModel> list = (List<ServiceMasterModel>) serviceMasterService.getAll(serviceCategory);
		response = new ResponseModel<List<ServiceMasterModel>>(ResponseModel.SUCCESS, list);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/details/{applicationno}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getApplicationDetails(@PathVariable(value = "applicationno") String applicationNo,
			@RequestParam(value = "isvehicledetails", required = false) boolean isVehicleDetails,
			@RequestParam(value = "onlyappcovs", required = false) boolean isOnlyAppicationCovs,
			@RequestHeader("Authorization") String token) throws UnauthorizedException {
		ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
		CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(applicationNo);
		if (ObjectsUtil.isNull(citizenApp)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			response.setMessage("Application Not Found");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		if (ObjectsUtil.isNull(citizenApp.getServiceType())) {
			return ResponseEntity.notFound().build();
		}

		UserType userType = null;
		try {
			String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
			if (!StringsUtil.isNullOrEmpty(userRole)) {
				userType = UserType.valueOf(userRole);
			}
		} catch (Exception e) {
		}
		if (userType == UserType.ROLE_BODY_BUILDER) {
			boolean taskFound = false;
			ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService
					.getActiveTasks(applicationService.getProcessInstanceId(applicationNo));
			List<RtaTaskInfo> tasks = actRes.getActiveTasks();
			for (RtaTaskInfo task : tasks) {
				if (task.getTaskDefKey().equalsIgnoreCase(taskBodyBuilder)) {
					taskFound = true;
				}
			}
			if (!taskFound) {
				log.error("Application Id " + applicationNo + " not belong to task : " + taskBodyBuilder);
				response.setStatus(ResponseModel.FAILED);
				response.setMessage("Invalid Action, Application not found at Body Builder !!!");
				response.setStatusCode(HttpStatus.FORBIDDEN.value());
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		}
		GeneralDetails generalDetails;
		try {
			DetailsService detailsService;
			if (citizenApp.getServiceType() == ServiceType.FRESH_RC_FINANCIER) {
				if (citizenApp.getKeyType() == KeyType.TR) {
					detailsService = freshRCTRDetailService;
				} else {
					detailsService = freshRCPRDetailService;
				}
			} else {
				detailsService = detailsServiceFactory.getDetailsService(citizenApp.getServiceType());
			}
			if (ObjectsUtil.isNull(detailsService)) {
				log.debug("No DetailsService found for service type  : " + citizenApp.getServiceType());
				return ResponseEntity.badRequest().build();
			}
			String uniqueKey = citizenApp.getUniqueKey();
			
			if (ServiceCategory.DL_CATEGORY.getCode().equalsIgnoreCase(citizenApp.getServiceCategoryCode())
					|| ServiceCategory.LL_CATEGORY.getCode().equalsIgnoreCase(citizenApp.getServiceCategoryCode())) {
				uniqueKey = null;
				if (isOnlyAppicationCovs) {
					uniqueKey = citizenApp.getApplicationNumber();
				}
				generalDetails = detailsService.getDetails(citizenApp.getAadharNumber(), uniqueKey);
			} else {
				generalDetails = detailsService.getDetails(citizenApp.getAadharNumber(), uniqueKey);
			}
			
			if (ObjectsUtil.isNull(generalDetails)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ResponseModel<GeneralDetails>(ResponseModel.FAILED, null, "not found"));
			}
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseModel<GeneralDetails>(ResponseModel.FAILED, null, "bad request"));
		}
		if (isVehicleDetails) {
			return ResponseEntity.ok(new ResponseModel<VehicleDetailsRequestModel>(ResponseModel.SUCCESS,
					generalDetails.getVehicleDetails()));
		}
		return ResponseEntity.ok(new ResponseModel<GeneralDetails>(ResponseModel.SUCCESS, generalDetails));
	}

	@RequestMapping(value = "/{servicetype}/applications/pending", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getSlotApplications(@RequestHeader("Authorization") String token,
			@PathVariable(value = "servicetype") String st, @RequestParam(value = "timestamp") Long v,
			@RequestParam(value = "slotservicetype", required = true) String slotType) {
		ServiceType serviceType = ServiceType.getServiceType(st);
		if (ObjectsUtil.isNull(serviceType)) {
			return ResponseEntity.notFound().build();
		}
		SlotServiceType slotServiceType = SlotServiceType.getSlotType(slotType);
		if (ObjectsUtil.isNull(slotServiceType) || ObjectsUtil.isNull(slotServiceType)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new ResponseModel<>(ResponseModel.FAILED, "invalid slottype", HttpStatus.BAD_REQUEST.value()));
		}
		UserModel user = null;
		try {
			user = applicationService.getRTAUserByToken(token);
		} catch (UnauthorizedException e) {
			log.info("rta office not found");
			return ResponseEntity.ok(new ResponseModel<>(HttpStatus.UNAUTHORIZED.toString(), "invalid token",
					HttpStatus.UNAUTHORIZED.value()));
		}
		if (ObjectsUtil.isNull(user)) {
			return ResponseEntity.ok(new ResponseModel<>(HttpStatus.UNAUTHORIZED.toString(), "invalid token",
					HttpStatus.UNAUTHORIZED.value()));
		}
		String rtaOfficeCode = user.getRtaOfficeCode();
		if (ObjectsUtil.isNull(rtaOfficeCode)) {
			return ResponseEntity.ok(new ResponseModel<>(HttpStatus.UNAUTHORIZED.toString(), "invalid token",
					HttpStatus.UNAUTHORIZED.value()));
		}
		List<CitizenApplicationModel> list = (List<CitizenApplicationModel>) applicationService
				.getSlotPendingApplications(v, serviceType, slotServiceType, rtaOfficeCode, user.getUserName());
		ResponseModel<List<CitizenApplicationModel>> response = new ResponseModel<List<CitizenApplicationModel>>(
				ResponseModel.SUCCESS, list);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/user/details/{username}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getUserDetails(@PathVariable(value = "username") String userName) {
		ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
		RegistrationServiceResponseModel<DealerModel> dealerModel = null;
		try {
			dealerModel = registrationService.getUserDetails(userName);
		} catch (RestClientException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("getting Error");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} catch (UnauthorizedException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			response.setMessage("User Not Found");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
		return ResponseEntity.ok(new ResponseModel<DealerModel>(ResponseModel.SUCCESS, dealerModel.getResponseBody()));
	}

	@RequestMapping(value = "/application/alerts", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getAlerts(@RequestHeader("Authorization") String token,
			@RequestParam(name = "appnumber", required = false) String appNumber) {
		log.info(":::getAlerts:::init::start :");
		Long sessionId = getSession(token, appNumber);

		ResponseModel<Object> response = new ResponseModel<>();
		try {
			response = applicationService.getAlerts(sessionId);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseModel<Object>(ResponseModel.FAILED);
		}
		log.info(":::getAlerts:::init::end :");
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/status", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getStatus(@RequestParam(value = "prnumber", required = true) String prNumber) {
		ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		if (StringsUtil.isNullOrEmpty(prNumber)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("PR number can't be Null OR Empty.");
			return ResponseEntity.status(httpStatus).body(response);
		}
		try {
			RegistrationServiceResponseModel<ApplicationModel> result = null;
			result = registrationService.getPRDetails(prNumber);
			if (ObjectsUtil.isNull(result) || result.getHttpStatus() != HttpStatus.OK) {
				log.info("pr details not found for pr number : " + prNumber);
				response.setStatus(ResponseModel.FAILED);
				response.setStatusCode(HttpStatus.NO_CONTENT.value());
				response.setMessage("Error when getting pr details.");
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
			}
			return ResponseEntity
					.ok(new ResponseModel<ApplicationModel>(ResponseModel.SUCCESS, result.getResponseBody()));
		} catch (RestClientException e) {
			log.error("error when getting pr details : " + e.getLocalizedMessage());
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Rest client Error when getting pr details.");
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		} catch (UnauthorizedException e) {
			log.error("unauthorized");
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			response.setMessage("UNAUTHORIZED Request.");
			httpStatus = HttpStatus.UNAUTHORIZED;
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

	/**
	 * Financier action on app (other than HPA)
	 * 
	 * @param token
	 * @param appNo
	 * @param status1
	 * @return
	 */
	@RequestMapping(value = "/application/financier/app/{app_no}/action/{status}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> approveFinancierTaskStep(@RequestHeader("Authorization") String token,
			@PathVariable("app_no") String appNo, @PathVariable("status") String status1,
			@RequestBody CommentModel comment) {
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
		ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>();
		Status status = Status.getStatus(status1);
		if (ObjectsUtil.isNull(status) || !(status == Status.APPROVED || status == Status.REJECTED)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Invalid Status : " + status);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		response = applicationService.financierAction(appNo, userName, userId, userRole, status, comment);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/terminate/activiti/task/{task_def}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> terminateActivitiTask(@RequestHeader("Authorization") String token,
			@PathVariable("task_def") String taskDef,
			@RequestParam(name = "appnumber", required = false) String appNumber) {
		Long sessionId = getSession(token, appNumber);
		String instanceId = applicationService.getProcessInstanceId(sessionId);

		String userName = null;
		TokenType tokenType = null;
		try {
			tokenType = TokenType.valueOf(jwtTokenUtil.getTokenType(token));
			if (tokenType == TokenType.CITIZEN) {
				userName = CitizenConstants.CITIZEN_USERID;
			} else {
				userName = jwtTokenUtil.getUserNameFromRegToken(token);
			}
		} catch (Exception ex) {
			userName = jwtTokenUtil.getUserNameFromRegToken(token);
		}

		ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>();
		Assignee assignee = new Assignee();
		assignee.setUserId(userName);
		ActivitiResponseModel<List<RtaTaskInfo>> actResponse = activitiService.completeTask(assignee, taskDef,
				instanceId, true, null);
		response.setActivitiTasks(actResponse.getActiveTasks());
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

	@RequestMapping(value = "/slotbooking/details/{applicationNumner}", method = RequestMethod.GET)
	public ResponseEntity<?> getSlotBookingDetails(@PathVariable("applicationNumner") String applicationNumner) {
		if (StringsUtil.isNullOrEmpty(applicationNumner)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request, user name is not valid"));
		}
		ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
		List<SlotModel> responseModel = null;
		try {
			responseModel = slotService.getSlotBookingDetails(applicationNumner);
		} catch (SlotUnavailableException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("getting Error");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} catch (Exception e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
			response.setMessage("getting Error");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		return ResponseEntity.ok(new ResponseModel<Object>(ResponseModel.SUCCESS, responseModel));
	}

	@RequestMapping(path = "/customer/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getCustomerDetails(@RequestHeader("Authorization") String token,
			@RequestParam(name = "appnumber", required = false) String appNumber) throws UnauthorizedException {
		log.info("getCustomerDetails Start ....");
		Long sessionId = getSession(token, appNumber);
		CustomerDetailsRequestModel customer = applicationService.getCustomerInfoBySession(sessionId);
		return ResponseEntity.ok(new ResponseModel<CustomerDetailsRequestModel>(ResponseModel.SUCCESS, customer));
	}

	@RequestMapping(value = "/uid/{appNumber}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getUidFromAppNumber(@PathVariable("appNumber") String appNumber) {
		UserSessionModel response = applicationService.getSession(appNumber);
		return ResponseEntity.ok(new ResponseModel<String>(ResponseModel.SUCCESS, response.getAadharNumber(), null,
				HttpStatus.OK.value()));
	}

	@RequestMapping(value = "/application/reiterate/app/{appNumber}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> reIterateApp(@PathVariable("appNumber") String appNumber)
			throws UnauthorizedException, ServiceValidationException {
		ResponseModel<String> resp = applicationService.reIterateApp(appNumber);
		return ResponseEntity.ok(resp);
	}

	@RequestMapping(value = "/application/citizenapp/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<CitizenApplicationModel> getCitizenAppDetails(@RequestHeader("Authorization") String token,
			@RequestParam(name = "appnumber", required = false) String appNumber) throws UnauthorizedException {
		Long sessionId = getSession(token, appNumber);
		CitizenApplicationModel citizenApp = applicationService.getCitizenAppDeatails(sessionId);
		return ResponseEntity.ok(citizenApp);
	}

	/**
	 * @author Gautam.kumar
	 * @description Get mvi last comment
	 * @param appNumber
	 * @return
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/getLastComment/{appNumber}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getLastComment(@RequestHeader("Authorization") String token,
			@PathVariable("appNumber") String appNumber) throws UnauthorizedException {
		Long sessionId = getSession(null, appNumber);
		String appNo = applicationService.getLastApplicationForMviInspectionComment(sessionId);
		ResponseModel<String> resp = applicationService.getLastMviInspectionComment(appNo);
		return ResponseEntity.ok(resp);
	}

	@RequestMapping(path = "/application/authentication/{appNumber}", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
	public ResponseEntity<?> authenticateOwnerForConscent(@PathVariable("appNumber") String appNumber,
			@RequestBody AuthenticationModel model, @RequestParam(value= "req_type", required = false) String reqType) throws UnauthorizedException, ServiceValidationException {
		String token = null;
		CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNumber);
		if (!ObjectsUtil.isNull(citizenApp)) {
			Long sessionId = citizenApp.getSessionId();
			ResponseModel<TokenModel> response = new ResponseModel<>(ResponseModel.SUCCESS);
			UserSessionModel userDetailsModel = userSessionService.getSession(sessionId);
			if (!ObjectsUtil.isNull(userDetailsModel)) {
				String ownAadharNumber = null;
				if(!StringsUtil.isNullOrEmpty(reqType) && reqType.equalsIgnoreCase(CANCEL_REQ)){
					ownAadharNumber = userDetailsModel.getAadharNumber();
				} else {
					RegistrationServiceResponseModel<ApplicationModel> prDetails = registrationService
							.getPRDetails(userDetailsModel.getUniqueKey());
					ApplicationModel appModel = prDetails.getResponseBody();
					if(!ObjectsUtil.isNull(appModel)){
						ownAadharNumber = appModel.getAadharNumber();
					}
				}
				
				if (!ObjectsUtil.isNull(ownAadharNumber) && ownAadharNumber.equals(model.getUid_num())) {
					RegistrationServiceResponseModel<AadharModel> res = registrationService.aadharAuthentication(model);
					if (res.getHttpStatus().equals(HttpStatus.OK)) {
						token = jwtTokenUtil.generateToken(userDetailsModel);
						TokenModel tokenModel = new TokenModel();
						tokenModel.setToken(token);
						response.setData(tokenModel);
						response.setStatus(ResponseModel.SUCCESS);
						response.setMessage("SUCCESS");
						return ResponseEntity.ok(response);
					} else {
						response.setStatus(ResponseModel.FAILED);
						response.setMessage("FAILURE");
						return ResponseEntity.ok(response);
					}
				} else {
					response.setStatus(ResponseModel.FAILED);
					response.setMessage("FAILURE");
					return ResponseEntity.ok(response);
				}
			}
		}

		return null;
	}

	@RequestMapping(value = "/application/submitownerconscent/{appNumber}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> submitownerconscent(@RequestHeader("Authorization") String token,
			@RequestBody OwnerConscent ownerConscent, @PathVariable("appNumber") String appNumber)
			throws UnauthorizedException {
		ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>(ResponseModel.SUCCESS);
		RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService
				.submitOwnerConscent(ownerConscent, appNumber);
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		if (regResponse.getHttpStatus() == HttpStatus.OK) {
			String instanceId = applicationService.getProcessInstanceId(sessionId);
			Assignee assignee = new Assignee();
			assignee.setUserId(CitizenConstants.CITIZEN_USERID);
			Map<String, Object> variables = new HashMap<String, Object>();
			if (ownerConscent.getOwnerConscent()) {
				variables.put(taskOwnerConscent, Status.APPROVED);
			} else {
				variables.put(taskOwnerConscent, Status.REJECTED);
			}
			ActivitiResponseModel<List<RtaTaskInfo>> actResponse = activitiService.completeTask(assignee,
					taskOwnerConscent, instanceId, true, variables);
			response.setActivitiTasks(actResponse.getActiveTasks());
			response.setStatus(ResponseModel.SUCCESS);
			response.setMessage("SUCCESS");
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/search/data/authentication/app/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> searchAuthenticatedApplication(@PathVariable("app_no") String appNo,
			@RequestHeader("Authorization") String token) throws UnauthorizedException, VehicleNotFinanced {
		ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
		CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNo);
		if (ObjectsUtil.isNull(citizenApp)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			response.setMessage("Application Not Found");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		AppSearchService appService = detailsServiceFactory.getAppSearchService(citizenApp.getServiceType());
		if (ObjectsUtil.isNull(appService)) {
			log.debug("Invalid Service Code  : " + citizenApp.getServiceType());
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Invalid Service Code");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		response = appService.getSearchApplication(appNo, citizenApp.getSessionId());
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Cancel a citizen application.
	 * 
	 * @param appNo
	 * @return
	 */
	@RequestMapping(value = "/application/cancel/app/{app_no}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> cancellApplication(@PathVariable("app_no") String appNo){
		log.info("Going to cancelling application number : " + appNo);
		ResponseModel<String> response = applicationService.cancelApplication(appNo);
		return ResponseEntity.ok(response);
	}
}

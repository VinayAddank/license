/**
 * 
 */
package org.rta.citizen.common.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.PRType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.TaskNotFound;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AppActionModel;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.service.rtaapplication.RtaApplicationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.freshrc.ShowcaseNoticeInfoModel;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author arun.verma
 *
 */
@RestController
public class RtaApplicationController {
	
	private static final Logger log = Logger.getLogger(RtaApplicationController.class);

	@Autowired
	private RtaApplicationService rtaApplicationService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
    private ApplicationFormDataService applicationFormDataService;
	
	@Autowired
	private StoppageTaxService stoppageTaxService;

	@RequestMapping(value = "/{usertype}/applications/status/{status}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getRTAApplication(@RequestHeader("Authorization") String token,
			@PathVariable("usertype") String userType, @RequestParam(name = "count", required = false) boolean count,
			@PathVariable("status") String status, @RequestParam(name = "from", required = false) Long from,
			@RequestParam(name = "to", required = false) Long to,
			@RequestParam(name = "per_page_records", required = false) Integer perPageRecords,
			@RequestParam(name = "page_number", required = false) Integer pageNumber,
			@RequestParam(name = "service_type", required = false) String serviceType,
			@RequestParam(name = "slot_applicable", required = false) boolean slotApplicable,
			@RequestParam(name = "service_category", required = false) String serviceCategory1,
			@RequestParam(name = "q", required = false) String applicationNumber) throws UnauthorizedException {
		log.info("Inside getRTAApplication.....");
		ResponseModel<?> response = null;
		Status status1 = Status.getStatus(status);
		if (ObjectsUtil.isNull(status1)) {
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
			response.setMessage("Invalid status !!!");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}
		ServiceCategory serviceCategory = null;
		if (!ObjectsUtil.isNull(serviceCategory1)) {
			serviceCategory = ServiceCategory.getServiceTypeCat(serviceCategory1.toUpperCase());
			if (ObjectsUtil.isNull(serviceCategory)) {
				response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
				response.setMessage("Invalid ServiceCategory : " + serviceCategory1);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			}
		}
		ServiceType sType = ServiceType.getServiceType(serviceType);
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
		log.info("userName: " + userName + " userId: " + userId + " userRole:" + userRole);

		UserModel um = registrationService.getRtaUserByToken(token);
		log.info("RTA office :: " + um.getRtaOffice() + " RTA Office Code" + um.getRtaOfficeCode());

		HashMap<String, Object> variables = new HashMap<>();
		if (serviceType != null) {
			variables.put(ActivitiService.SERVICE_CODE_KEY, sType.getCode());
		}
		if (!ObjectsUtil.isNull(serviceCategory)) {
			variables.put(ActivitiService.SERVICE_CATEGORY_KEY, serviceCategory.getCode());
		}
		// TODO uncomment latter
		variables.put(ActivitiService.RTA_OFFICE_CODE, um.getRtaOfficeCode());

		if (status1 == Status.PENDING) {
			List<CitizenApplicationModel> appList = new ArrayList<>();
			HashMap<String, Object> mapObject = new HashMap<String, Object>();
			Integer totalRecords = 0;
			if (count) {
				Map<String, Object> map = new HashMap<>();
				map.put("status", status1.getLabel());
				map.put("count", rtaApplicationService.getPendingApplicationCount(userId, userName, userRole, from, to,
						perPageRecords, pageNumber, variables));
				response = new ResponseModel<Map<String, Object>>(ResponseModel.SUCCESS, map);
				return ResponseEntity.ok(response);
			} else {
				appList = rtaApplicationService.getPendingApplications(userId, userName, userRole, from, to,
						perPageRecords, pageNumber, slotApplicable, variables, applicationNumber);
				totalRecords = appList.size();
			}
			List<CitizenApplicationModel> subAppList = new ArrayList<CitizenApplicationModel>();
			  if (ObjectsUtil.isNull(appList) || appList.size() == 0) {
				subAppList = appList;
			}else if (ObjectsUtil.isNull(pageNumber) || ObjectsUtil.isNull(perPageRecords)) {
				subAppList = appList;
			} else if ((pageNumber - 1) * perPageRecords > appList.size()) {
				subAppList = null;
			} else if (pageNumber * perPageRecords <= appList.size() && pageNumber * perPageRecords <= appList.size()) {
				subAppList = appList.subList((pageNumber - 1) * perPageRecords, pageNumber * perPageRecords);
			} else {
				subAppList = appList.subList((pageNumber - 1) * perPageRecords, appList.size());
			}

			mapObject = new HashMap<String, Object>();
			mapObject.put("applicationModels", subAppList);
			mapObject.put("totalRecords", totalRecords);

			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.SUCCESS, mapObject);
			return ResponseEntity.ok(response);
		} else if (status1 == Status.APPROVED || status1 == Status.REJECTED) {
			List<CitizenApplicationModel> appList = new ArrayList<CitizenApplicationModel>();
			HashMap<String, Object> mapObject = new HashMap<String, Object>();
			Integer totalRecords = 0;
			if (count) {
				Map<String, Object> map = new HashMap<>();
				map.put("status", status1.getLabel());
				map.put("count", rtaApplicationService.getApplicationCount(status1, userId, userName, userRole, sType,
						from, to, perPageRecords, pageNumber, serviceCategory));
				response = new ResponseModel<Map<String, Object>>(ResponseModel.SUCCESS, map);
				return ResponseEntity.ok(response);
			} else {
				appList = rtaApplicationService.getApplications(status1, userId, userName, userRole, sType, from, to,
						perPageRecords, pageNumber, serviceCategory, applicationNumber);
				totalRecords = appList.size();
			}
			List<CitizenApplicationModel> subAppList = new ArrayList<CitizenApplicationModel>();
			
			 if (ObjectsUtil.isNull(appList) || appList.size() == 0) {
				subAppList = appList;
			} else if (ObjectsUtil.isNull(pageNumber) || ObjectsUtil.isNull(perPageRecords)) {
				subAppList = appList;
			}else if ((pageNumber - 1) * perPageRecords > appList.size()) {
				subAppList = null;
			} else if ((pageNumber - 1) * perPageRecords <= appList.size()
					&& pageNumber * perPageRecords <= appList.size()) {
				subAppList = appList.subList((pageNumber - 1) * perPageRecords, pageNumber * perPageRecords);
			} else {
				subAppList = appList.subList((pageNumber - 1) * perPageRecords, appList.size());
			}

			mapObject = new HashMap<String, Object>();
			mapObject.put("applicationModels", subAppList);
			mapObject.put("totalRecords", totalRecords);
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.SUCCESS, mapObject);
			return ResponseEntity.ok(response);
		}
		response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
		response.setMessage("Invalid status !!!");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@RequestMapping(value = "/inspection/applications/status/{status}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getInspectionApplications(@RequestHeader("Authorization") String token,
			@RequestParam(name = "count", required = false) boolean count,
			@PathVariable("status") String status, @RequestParam(name = "from", required = false) Long from,
			@RequestParam(name = "to", required = false) Long to,
			@RequestParam(name = "per_page_records", required = false) Integer perPageRecords,
			@RequestParam(name = "page_number", required = false) Integer pageNumber,
			@RequestParam(name = "q", required = false) String applicationNumber) throws UnauthorizedException {
		log.info("Inside getRTAApplication.....");
		ResponseModel<?> response = null;
		Status status1 = Status.getStatus(status);
		if (ObjectsUtil.isNull(status1)) {
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
			response.setMessage("Invalid status !!!");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		UserType userType = UserType.valueOf(jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0));
		log.info("userId: " + userId + " userTpe:" + userType);

		if (status1 == Status.OPEN) {
			List<CitizenApplicationModel> appList = new ArrayList<>();
			HashMap<String, Object> mapObject = new HashMap<String, Object>();
			Integer totalRecords = 0;
			if (count) {
				Map<String, Object> map = new HashMap<>();
				map.put("status", status1.getLabel());
				map.put("count", stoppageTaxService.getInspectionOpenApplicationCount(userId));
				response = new ResponseModel<Map<String, Object>>(ResponseModel.SUCCESS, map);
				return ResponseEntity.ok(response);
			} else {
				appList = stoppageTaxService.getInspectionOpenApplications(userId, applicationNumber);
				totalRecords = appList.size();
			}
			List<CitizenApplicationModel> subAppList = new ArrayList<CitizenApplicationModel>();
			  if (ObjectsUtil.isNull(appList) || appList.size() == 0) {
				subAppList = appList;
			}else if (ObjectsUtil.isNull(pageNumber) || ObjectsUtil.isNull(perPageRecords)) {
				subAppList = appList;
			} else if ((pageNumber - 1) * perPageRecords > appList.size()) {
				subAppList = null;
			} else if (pageNumber * perPageRecords <= appList.size() && pageNumber * perPageRecords <= appList.size()) {
				subAppList = appList.subList((pageNumber - 1) * perPageRecords, pageNumber * perPageRecords);
			} else {
				subAppList = appList.subList((pageNumber - 1) * perPageRecords, appList.size());
			}

			mapObject = new HashMap<String, Object>();
			mapObject.put("applicationModels", subAppList);
			mapObject.put("totalRecords", totalRecords);

			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.SUCCESS, mapObject);
			return ResponseEntity.ok(response);
		} 
		response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
		response.setMessage("Invalid status !!!");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@RequestMapping(value = "/{usertype}/application/{app_no}/action/{status}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> applicationAction(@RequestHeader("Authorization") String token,
			@RequestHeader("clientip") String ip, @PathVariable("status") String status,
			@PathVariable("app_no") String appNumber, @RequestBody CommentModel commentModel,
			@RequestParam(name = "slotid", required = false) String slotId, @RequestParam(required = false) String prNumber) throws JsonProcessingException, IOException, UnauthorizedException, DataMismatchException, NotFoundException, ConflictException, ServiceValidationException {
		log.info("Inside applicationAction.....");
		ResponseModel<?> response = null;
		Status status1 = Status.getStatus(status);
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
		log.info("userName: " + userName + " userId: " + userId + " userRole:" + userRole);
		// ----------open application -----------------------------
		if (status1 == Status.OPEN) {
			try {
				rtaApplicationService.openApplication(status1, appNumber, userId, userName, userRole);
			} catch (TaskNotFound e) {
				response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED, null, e.getMessage());
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			} catch (NotFoundException e) {
				response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED, null, e.getMessage());
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.SUCCESS);
			return ResponseEntity.ok(response);
		}
		// ---------------------reject/approve-----------------------
		if (ObjectsUtil.isNull(status1) || !(status1 == Status.APPROVED || status1 == Status.REJECTED)) {
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
			response.setMessage("Invalid status : " + status);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (status1 == Status.REJECTED && ObjectsUtil.isNull(commentModel.getComment())) {
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
			response.setMessage("Comment Requiered !!!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		//---- validate for service vehicle alteration for special number case only for AO/RTO-----
		if(status1 == Status.APPROVED && (userRole.equalsIgnoreCase(UserType.ROLE_AO.name()) || userRole.equalsIgnoreCase(UserType.ROLE_RTO.name()))){
		    CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNumber);
		    if(citizenApp.getServiceType() == ServiceType.VEHICLE_ATLERATION){
		        ResponseModel<ApplicationFormDataModel> formData = applicationFormDataService.getApplicationFormDataBySessionId(citizenApp.getSessionId(), FormCodeType.VA_FORM.getLabel());
		        ApplicationFormDataModel form = formData.getData();
		        ObjectMapper mapper = new ObjectMapper();
		        VehicleBodyModel vehicleBodyModel = mapper.readValue(form.getFormData(), VehicleBodyModel.class);
		        if(vehicleBodyModel.getPrType() == PRType.SPECIAL){
		        	if(null == prNumber || prNumber.trim().equals("")){
			            response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
			            response.setMessage("Special PR Number Required !!!");
			            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			        } else {
			            vehicleBodyModel.setSpecialPrNo(prNumber.trim());
			            String jsonInString = mapper.writeValueAsString(vehicleBodyModel);
			            ApplicationFormDataModel appForm = new ApplicationFormDataModel();
			            appForm.setFormData(jsonInString);
			            appForm.setFormCode(FormCodeType.VA_FORM.getLabel());
			            List<ApplicationFormDataModel> forms = new ArrayList<>();
			            forms.add(appForm);
			            ResponseModel<ApplicationFormDataModel> formResp = applicationFormDataService.saveForm(forms, citizenApp.getSessionId(), userId);
			            if(!formResp.getStatus().equalsIgnoreCase(ResponseModel.SUCCESS)){
			                response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
		                    response.setMessage("Some Error Occured While Saving Data !!!");
		                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			            }
			        }
		        }
		    }
		}
		//--------------------------------------------------------------------------
		
		List<RtaTaskInfo> rtaTaskList = null;
		try {
			rtaTaskList = rtaApplicationService.actionOnApp(status1, appNumber, userId, userName, userRole,
					commentModel, slotId);
		} catch (TaskNotFound e) {
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED, null, e.getMessage());
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} catch (NotFoundException e) {
			response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED, null, e.getMessage());
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		response = new ResponseModel<HashMap<String, Object>>(ResponseModel.SUCCESS);
		response.setActivitiTasks(rtaTaskList);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/rta/application/status/app/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getApplicationStatus(@RequestHeader("Authorization") String token,
			@PathVariable("app_no") String appNo) {
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
		ResponseModel<ApplicationStatusModel> response = new ResponseModel<ApplicationStatusModel>(
				ResponseModel.SUCCESS);
		ApplicationStatusModel appStatus = rtaApplicationService.getAppStatus(appNo);
		if (ObjectsUtil.isNull(appStatus)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
		response.setData(appStatus);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/checklist/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getApplicationCheckList(@PathVariable("app_no") String applicationNumber, @RequestHeader("Authorization") String token) {
		ResponseModel<Map<String, Object>> response = new ResponseModel<Map<String, Object>>(ResponseModel.SUCCESS);
		if (StringsUtil.isNullOrEmpty(applicationNumber)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		Map<String, Object> appInfo = null;
		try {
			appInfo = rtaApplicationService.getInfo(applicationNumber, token);
		} catch (NotFoundException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		} catch (Exception e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		if (ObjectsUtil.isNull(appInfo)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
		response.setData(appInfo);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/customer/invoice/{appnumber}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getCustomerInvoice(@PathVariable("appnumber") String appNumber,
			@RequestParam(name = "regtype", required = false) String invoiceType) {
		ResponseModel<String> response = new ResponseModel<String>(ResponseModel.SUCCESS);
		String customerInvoice = rtaApplicationService.getCustomerInvoice(appNumber, invoiceType);
		if (StringsUtil.isNullOrEmpty(customerInvoice)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
		response.setData(customerInvoice);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/rta/signature/{appnumber}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getSignature(@PathVariable("appnumber") String appNumber) {
    	log.info("::::getSignature:::::start::::: " + appNumber);
		ResponseModel<String> response = new ResponseModel<String>(ResponseModel.SUCCESS);
		String signatureDetails = rtaApplicationService.getSignature(appNumber);
		if (StringsUtil.isNullOrEmpty(signatureDetails)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
		response.setData(signatureDetails);
        log.info("::::getSignature::::end::::: " + response.getData());
		return ResponseEntity.ok(response);
	}

	/*
	 * @RequestMapping(value = "/application/action/mvi/app/{app_no}", method =
	 * RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	 * public ResponseEntity<?>
	 * mviCompleteAppWithAadhar(@RequestHeader("Authorization") String
	 * token, @PathVariable("app_no") String appNumber,
	 * 
	 * @RequestBody AadhaarTCSDetailsRequestModel
	 * aadharAuthModel, @RequestParam(name = "slotid", required = false) String
	 * slotId) throws AadharAuthenticationFailedException, UnauthorizedException
	 * { log.info("Inside mviCompleteAppWithAadhar....."); ResponseModel<?>
	 * response = null; String userName =
	 * jwtTokenUtil.getUserNameFromRegToken(token); Long userId =
	 * jwtTokenUtil.getUserIdFromRegToken(token); String userRole =
	 * jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
	 * log.info("userName: " + userName + " userId: " + userId + " userRole:" +
	 * userRole);
	 * 
	 * RegistrationServiceResponseModel<AadharModel> aadharResponse =
	 * registrationService .aadharAuthentication(aadharAuthModel); if
	 * (ObjectsUtil.isNull(aadharResponse) || aadharResponse.getHttpStatus() !=
	 * HttpStatus.OK) { log.error("eKYC authentication failed"); throw new
	 * AadharAuthenticationFailedException(); } AadharModel aadharModel =
	 * aadharResponse.getResponseBody(); if
	 * (aadharModel.getAuth_status().equalsIgnoreCase("SUCCESS")) {
	 * List<RtaTaskInfo> rtaTaskList = null; try { rtaTaskList =
	 * rtaApplicationService.actionOnApp(Status.APPROVED, appNumber, userId,
	 * userName, userRole, null, slotId); } catch (TaskNotFound e) { response =
	 * new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED, null,
	 * e.getMessage()); response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); }
	 * catch(NotFoundException e){ response = new ResponseModel<HashMap<String,
	 * Object>>(ResponseModel.FAILED, null, e.getMessage());
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return
	 * ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); } response =
	 * new ResponseModel<HashMap<String, Object>>(ResponseModel.SUCCESS);
	 * response.setActivitiTasks(rtaTaskList); return
	 * ResponseEntity.ok(response); } response = new
	 * ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
	 * response.setMessage("Aadhaar authentication failed !!!"); return
	 * ResponseEntity.ok(response); }
	 */

	@RequestMapping(value = "/application/action", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> appActionWithDocs(@RequestHeader("Authorization") String token,
			@RequestBody AppActionModel appActionModel) throws TaskNotFound, NotFoundException {
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
		ResponseModel<?> response = rtaApplicationService.actionOnApp(userName, userId, userRole, appActionModel);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/application/showcaseinfo", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getShowcaseinfo(@RequestHeader("Authorization") String token,
			@RequestParam(name = "appnumber", required = true) String appNumber)
			throws TaskNotFound, NotFoundException {
		if (StringsUtil.isNullOrEmpty(appNumber)) {
			log.error("invalid app number : " + appNumber);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseModel<>(ResponseModel.FAILED, null, HttpStatus.BAD_REQUEST.value()));
		}
		Long sessionId = getSession(token, appNumber);
		ResponseModel<ShowcaseNoticeInfoModel> response;
		try {
			response = rtaApplicationService.getShowcaseInfo(sessionId);
			return ResponseEntity.ok(response);
		} catch (UnauthorizedException e) {
			log.error("unauthorized : when getting showcase info for session id : " + sessionId);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseModel<>(ResponseModel.FAILED, null, HttpStatus.UNAUTHORIZED.value()));
		}
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

	/**
	 * TODO delete it later
	 * 
	 * test Sync
	 * 
	 * @param token
	 * @param executionId
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "/rta/test/sync/{execution_id}/action/{status}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> testSync(@RequestHeader("Authorization") String token,
			@PathVariable("execution_id") String executionId, @PathVariable("status") Status status) {
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
		ResponseModel<?> res = new ResponseModel<>();
		rtaApplicationService.completeApp(executionId, status, userName, UserType.ROLE_ADMIN, false);
		res.setStatus(ResponseModel.SUCCESS);
		return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value = "/rta/mystatus/current/app/{app_no}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getAppStatusForCurrentStatus(@RequestHeader("Authorization") String token, @PathVariable("app_no") String appNo) {
	    String userName = jwtTokenUtil.getUserNameFromRegToken(token);
        Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
        String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
	    ResponseModel<?> res = rtaApplicationService.getAppStatusForCurrentStatus(userName, userId, appNo);
        return ResponseEntity.ok(res);
	}

	@RequestMapping(value = "/application/prnumbertype/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getPRNumberType(@PathVariable("app_no") String applicationNumber) {
		ResponseModel<Map<String, Object>> response = new ResponseModel<Map<String, Object>>(ResponseModel.SUCCESS);
		if (StringsUtil.isNullOrEmpty(applicationNumber)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		Map<String, Object> appInfo = null;
		try {
			appInfo = rtaApplicationService.getPRNumberType(applicationNumber);
		} catch (NotFoundException e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		} catch (Exception e) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		if (ObjectsUtil.isNull(appInfo)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
		response.setData(appInfo);
		return ResponseEntity.ok(response);
	}
}

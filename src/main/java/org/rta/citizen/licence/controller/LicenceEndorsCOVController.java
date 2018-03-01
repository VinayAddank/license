package org.rta.citizen.licence.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.UserSessionService;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.model.EndorseCOVModel;
import org.rta.citizen.licence.model.updated.CovDetailsModel;
import org.rta.citizen.licence.service.LicenceEndorsCOVService;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LicenceEndorsCOVController {

	private static final Logger log = Logger.getLogger(LicenceEndorsCOVController.class);

	@Autowired
	private LicenceEndorsCOVService licenceEndorsCOVService;
	@Value("${jwt.header}")
	private String tokenHeader;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private ActivitiService activitiService;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private RegistrationService registrationService;

	@RequestMapping(path = "{servicetype}/saveCovDetails/task/{task_def}", produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, method = RequestMethod.POST)
	public ResponseEntity<?> saveCovDetails(@Valid @RequestBody EndorseCOVModel requestModel,
			HttpServletRequest request, @PathVariable("task_def") String taskDef) {
		ResponseModel<SaveUpdateResponse> response = new ResponseModel<SaveUpdateResponse>(ResponseModel.SUCCESS);
		if (requestModel.getLlrVehicleClassCode() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
		}
		ActivitiResponseModel<List<RtaTaskInfo>> actResponse = null;
		String token = request.getHeader(tokenHeader);
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		log.info("sessionId" + sessionId);
		UserSessionModel userModel = userSessionService.getSession(sessionId);
		CitizenApplicationModel applicant = applicationService.getApplicationModel(sessionId);

		try {
			// Validate Guardian
			if (ServiceType.LL_FRESH == userModel.getServiceType() && DateUtil.getCurrentAge(applicant.getDob()) < 18) {
				String applicantAadhaar = userModel.getAadharNumber();
				Long guardianAadhaar = requestModel.getAadharNumber();
				if (null == guardianAadhaar || applicantAadhaar.equalsIgnoreCase(guardianAadhaar.toString())) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseModel<Object>(
							ResponseModel.FAILED, null,
							"Guardian's aadhaar no. is null OR Applicant's and Guardian's aadhaar no. are same."));
				} else {
					AadharModel model = registrationService.getAadharDetails(guardianAadhaar).getResponseBody();
					String dob = model.getDob();
					if (StringsUtil.isNullOrEmpty(dob) || DateUtil.getCurrentAge(dob) < 18) {
						return ResponseEntity.status(HttpStatus.FORBIDDEN)
								.body(new ResponseModel<Object>(ResponseModel.FAILED, null,
										"Guardian's age from aadhaar is coming null/empty OR its less than 18 years."));
					}
				}
			}
		} catch (Exception e) {
			log.info("Error while authenticate applicant's guardian aadhaar no. Application will move further."
					+ e.getMessage());
		}

		SaveUpdateResponse response1 = null;
		try {
			response1 = licenceEndorsCOVService.saveCovDetails(requestModel, sessionId, null, null);
			response.setData(response1);
			if (!ObjectsUtil.isNull(response1) && response1.getStatus().equals(SaveUpdateResponse.SUCCESS)) {
				// ------complete activiti task -------------
				Assignee assignee = new Assignee();
				assignee.setUserId(CitizenConstants.CITIZEN_USERID);
				String instanceId = applicationService.getProcessInstanceId(sessionId);
				actResponse = activitiService.getActiveTasks(instanceId);
				actResponse = activitiService.completeTask(assignee, taskDef, instanceId, true, null);
				response.setActivitiTasks(actResponse.getActiveTasks());
			}
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (ObjectsUtil.isNull(response1)) {
			log.error("there is not save Details");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@RequestMapping(path = "/covs/update/details/{applicationNo}", method = RequestMethod.GET)
	public ResponseEntity<?> getSelectedCOV(@PathVariable("applicationNo") String applicationNo,
			HttpServletRequest request) {
		// TODO Validations/Checks are remaining
		String tokn = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(request.getHeader(tokenHeader)).get(0);
		UserType userType = UserType.valueOf(tokn);
		CovDetailsModel lLRCOVModel = null;
		try {
			CitizenApplicationModel application = applicationService.getCitizenApplication(applicationNo);
			lLRCOVModel = licenceEndorsCOVService.getCovDetails(application.getSessionId(), userType);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok(new ResponseModel<CovDetailsModel>(ResponseModel.SUCCESS, lLRCOVModel));
	}

	@RequestMapping(path = "/covDetails", method = RequestMethod.GET)
	public ResponseEntity<?> getClassOfVehicle(HttpServletRequest request) {

		String token = request.getHeader(tokenHeader);
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);

		CovDetailsModel lLRCOVModel = null;
		try {
			lLRCOVModel = licenceEndorsCOVService.getCovDetails(sessionId, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok(new ResponseModel<CovDetailsModel>(ResponseModel.SUCCESS, lLRCOVModel));
	}

	@RequestMapping(path = "/tests/applicable", method = RequestMethod.POST)
	public ResponseEntity<?> getTests(HttpServletRequest request, @RequestBody List<String> vehicleClassList) {

		// String token = request.getHeader(tokenHeader);
		// Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);

		ResponseModel<Map<SlotServiceType, List<String>>> testsForCOVs = licenceEndorsCOVService
				.getTestsForVehicleClass(vehicleClassList);
		if (testsForCOVs.getStatusCode() == HttpStatus.NO_CONTENT.value()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(testsForCOVs);
	}

	@RequestMapping(path = "/covDetailsList", method = RequestMethod.GET)
	public ResponseEntity<?> getClassOfVehicleInfo(HttpServletRequest request) {
		String token = request.getHeader(tokenHeader);
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		CovDetailsModel lLRCOVModel = null;
		try {
			lLRCOVModel = licenceEndorsCOVService.getClassOfVehicleInfo(sessionId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok(new ResponseModel<CovDetailsModel>(ResponseModel.SUCCESS, lLRCOVModel));
	}

	
	//@PreAuthorize("hasRole('ROLE_CCO','ROLE_AO','ROLE_RTO')")
	@RequestMapping(path = "/covs/update/details/{applicationno}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE }, method = RequestMethod.PUT)
	public ResponseEntity<?> updateCovDetails(@Valid @RequestBody EndorseCOVModel requestModel,
			HttpServletRequest request, @PathVariable("applicationno") String applicationNo) {
		ResponseModel<SaveUpdateResponse> response = new ResponseModel<SaveUpdateResponse>(ResponseModel.SUCCESS);
		if (requestModel.getLlrVehicleClassCode() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
		}
		Long userId = jwtTokenUtil.getUserIdFromToken(request.getHeader(tokenHeader));
		log.info("userId " + userId);
		UserType userType = UserType.valueOf(jwtTokenUtil.getUserRoleFromRegistrationTokenToken(request.getHeader(tokenHeader)).get(0));
		log.info("USER TYPE " + userType);
		CitizenApplicationModel applicant = applicationService.getCitizenApplication(applicationNo);
		SaveUpdateResponse response1 = null;
		try {
			response1 = licenceEndorsCOVService.saveCovDetails(requestModel, applicant.getSessionId(), userId, userType);
			response.setData(response1);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (ObjectsUtil.isNull(response1)) {
			log.error("there is not save Details");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}

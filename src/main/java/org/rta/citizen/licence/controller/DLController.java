package org.rta.citizen.licence.controller;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.enums.TokenType;
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
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.ServiceMasterModel;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.service.LicenceService;
import org.rta.citizen.licence.service.updated.LicenseSyncingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DLController {

	private static final Logger logger = Logger.getLogger(DLController.class);

	@Autowired
	private LicenceService licenceService;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private LicenseSyncingService licenseSyncingService;

	@Autowired
	protected UserSessionDAO userSessionDAO;

	@Autowired
	private ApplicationService applicationService;

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@RequestMapping(value = "/license/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getDLDetails(HttpServletRequest request,
			@RequestParam(value = "uniquekey", required = false) String uniqueKey,
			@RequestParam(value = "isall", required = false) boolean isAll) {
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(request.getHeader(tokenHeader));
		LicenseHolderPermitDetails model = null;
		try {
			if (!StringsUtil.isNullOrEmpty(uniqueKey)) {
				model = registrationLicenseService.getLicenseHolderDtlsForDriver(uniqueKey).getResponseBody();
				model.setApplicationNo(applicationService.getCitizenAppDeatails(sessionId).getAadharNumber());
			} else {
				model = licenceService.getLicenseDetails(sessionId, isAll);
			}
		} catch (Exception e) {
			logger.error("getting error " + e.getMessage());
		}
		if (ObjectsUtil.isNull(model)) {
			logger.error("::::LicenseHolderPermitDetails::::Response Null");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
					.body(new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.FAILED, null,
							"::::LicenseHolderPermitDetails::::Response Null"));
		}
		logger.info("getting driver License details successfully ");
		return ResponseEntity.ok(new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.SUCCESS, model));
	}

	@RequestMapping(value = "/rta/details", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getRtaDetails(HttpServletRequest request) {

		List<RTAOfficeModel> model = null;

		try {
			model = registrationLicenseService.getRtaOfficeList().getResponseBody();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ResponseEntity.ok(new ResponseModel<List<RTAOfficeModel>>(ResponseModel.SUCCESS, model));

	}

	@RequestMapping(path = "/license/details/task/{taskDef}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getDetailsResponse(@RequestHeader("Authorization") String token,
			@PathVariable("taskDef") String taskDef) {
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
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		logger.info(":::getDetailsResponse::: SessionId " + sessionId);
		ResponseModel<String> response = new ResponseModel<>();
		try {
			logger.info("details service setting activiti : " + ", sessionId : " + sessionId + ", takDef : " + taskDef
					+ ", userName : " + userName);
			response.setActivitiTasks(licenceService.callAfterGettingDetails(sessionId, taskDef, userName));
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseModel<>(ResponseModel.FAILED,
					"Exception while saving detailsResponse Details :: " + e.getMessage());
		}
		logger.info(":getDetailsResponse::::Response " + response);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(path = "/license/holder/details", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> updateLicenceHoderDetails(@RequestHeader("Authorization") String token,
			@RequestBody LLRegistrationModel model) {

		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		logger.info(":::getDetailsResponse::: SessionId " + sessionId);
		ResponseModel<String> response = new ResponseModel<>();
		try {
			response = licenseSyncingService.updateInLicenseHolderDetails(sessionId, model);
		} catch (Exception e) {
			response = new ResponseModel<>(ResponseModel.FAILED,
					"Exception while saving updateLicenceHoderDetails :: " + e.getMessage());
		}
		logger.info(":updateLicenceHoderDetails::::Response " + response);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/license/details/{applicationno}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getDLDetails(@PathVariable("applicationno") String applicationNo) {
		LicenseHolderPermitDetails model = null;
		try {
			if (!StringsUtil.isNullOrEmpty(applicationNo)) {
				model = licenceService.getLicenceDetails(applicationNo);
			}
		} catch (Exception e) {
			logger.error("getting error " + e.getMessage());
		}
		if (ObjectsUtil.isNull(model)) {
			logger.error("getting some thing is missing");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.FAILED, null, "Some things missing"));
		}
		logger.info("getting driver License details successfully ");
		return ResponseEntity.ok(new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.SUCCESS, model));
	}

	@PreAuthorize("hasAnyRole('ROLE_CCO','ROLE_AO')")
	@RequestMapping(value = "/license/suspcanel/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getSuspCancelDLDetails(@RequestParam(value = "dlnumber", required = false) String dLNumber,
			HttpServletRequest request) {
		LicenseHolderPermitDetails model = null;
		ResponseModel<LicenseHolderPermitDetails> response;
		if (StringsUtil.isNullOrEmpty(dLNumber) || !dLNumber.startsWith(SomeConstants.AP)) {
			logger.error("This is not valid DL Number, please enter valid DL number !!!..." + dLNumber);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.FAILED, null,
							"This is not valid DL Number, please enter valid DL number !!!..." + dLNumber));
		}
		String token = request.getHeader(tokenHeader);
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		UserType userType = UserType.valueOf(jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0));
		try {
			model = licenceService.getSuspCancelDriverLicence(dLNumber, userName, userType);
		} catch (UnauthorizedException | ForbiddenException | AadharNotFoundException | DataMismatchException
				| NotFoundException | AadharAuthenticationFailedException | VehicleNotFinanced | FinancerNotFound
				| ParseException | ServiceValidationException | ConflictException e) {
			response = new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.SUCCESS);
			response.setMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
		}
		if (ObjectsUtil.isNull(model)) {
			logger.error("getting some thing is missing");
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.FAILED, null, "Some things missing"));
		}
		logger.info("getting driver License details successfully ");
		return ResponseEntity.ok(new ResponseModel<LicenseHolderPermitDetails>(ResponseModel.SUCCESS, model));
	}
}

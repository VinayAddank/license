package org.rta.citizen.licence.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.communication.CustMsgModel;
import org.rta.citizen.common.service.communication.CommunicationService;
import org.rta.citizen.common.service.communication.CommunicationServiceImpl;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.service.LicenceService;
import org.rta.citizen.licence.service.updated.LicenseSyncingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LLRController {

	private static final Logger log = Logger.getLogger(LLRController.class);

	@Autowired
	private LicenceService licenceService;

	@Autowired
	private LicenseSyncingService licenseSyncingService;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${exam.api.url}")
	private String examApiUrl;

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private CommunicationService communicationService;

	@RequestMapping(value = "/llf/attempt/count/app/{app_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getLLFAttemptCount(@PathVariable("app_no") String appNo) {
		HashMap<String, Integer> map = new HashMap<>();
		Integer count = licenceService.getLLFAttempts(appNo);
		map.put("attempts", count);
		ResponseModel<HashMap<String, Integer>> response = new ResponseModel<HashMap<String, Integer>>(
				ResponseModel.SUCCESS, map);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(path = "/forgot/{licencetype}/{aadharnumber}", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public ResponseEntity<?> forgotLicence(@PathVariable("licencetype") String licenceType,
			@PathVariable("aadharnumber") String aadharNumber,
			@RequestParam(value = "dob", required = true) String dob) {
		ResponseModel<Object> responseModel = new ResponseModel<Object>(ResponseModel.FAILED);
		Map<String, Object> map = new HashMap<>();
		map.put("licencetype", licenceType);
		try {
			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> responseBody = registrationLicenseService
					.getForgotLicenceNumber(dob, aadharNumber);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				map.put("licenceDetails", responseBody.getResponseBody());
				map.put("message", "");
				responseModel = new ResponseModel<Object>(ResponseModel.SUCCESS, map);
			} else if (responseBody.getHttpStatus() == HttpStatus.NOT_FOUND) {
				map.put("licenceDetails", null);
				map.put("message", "No detail found for this aadhaar number and date of birth");
				responseModel = new ResponseModel<Object>(ResponseModel.FAILED, map);
			} else if (responseBody.getHttpStatus() == HttpStatus.EXPECTATION_FAILED) {
				map.put("licenceDetails", null);
				map.put("message", "Some thing went wrong, Please try again");
				responseModel = new ResponseModel<Object>(ResponseModel.FAILED, map);
			}
		} catch (Exception e) {
			log.error("Getting error with getForgotLicence " + e.getMessage());
		}

		return ResponseEntity.ok(responseModel);
	}

	// TODO: This are for unit test purpose, We can remove after thats done.
	@RequestMapping(path = "/syncing/test/{applicationid}/{aadharnumber}", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public ResponseEntity<?> syncLL(@PathVariable("applicationid") Long applicationId,
			@PathVariable("aadharnumber") String aadharNumber) {
		ResponseModel<String> response = null;
		try {
			response = licenceService.saveUpdateLicenseHolderDtls(applicationId, aadharNumber);
		} catch (Exception e) {
			log.error("ERROR in exam post api:" + e.getMessage(), e);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}

	// TODO: This are for unit test purpose, We can remove after thats done.
	@RequestMapping(path = "/sync/dl/{applicationid}/{aadharnumber}", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public ResponseEntity<?> syncDL(@PathVariable("applicationid") Long applicationId,
			@PathVariable("aadharnumber") String aadharNumber) {
		ResponseModel<String> response = null;
		try {
			response = licenceService.saveUpdateDriversFreshPermitDtls(Status.APPROVED, applicationId, aadharNumber,
					null);
		} catch (Exception e) {
			log.error("ERROR in exam post api:" + e.getMessage(), e);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}

	// TODO: This are for unit test purpose, We can remove after thats done.
	@RequestMapping(path = "/sync/dlre/{applicationno}/{aadharnumber}", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public ResponseEntity<?> syncDLRE(@PathVariable("applicationno") String applicationNo,
			@PathVariable("aadharnumber") String aadharNumber, @RequestParam("uniquekey") String uniqueKey) {
		ResponseModel<String> response = null;
		try {
			response = licenseSyncingService.saveUpdateDriversPermitDtlsForDLRE(Status.APPROVED, applicationNo,
					aadharNumber, uniqueKey);
		} catch (Exception e) {
			log.error("ERROR in exam post api:" + e.getMessage(), e);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}

	// TODO: This are for unit test purpose, We can remove after thats done.
	@RequestMapping(path = "/syncing/test/{applicationid}/{aadharnumber}/{uk}", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public ResponseEntity<?> syncLLR(@PathVariable("applicationid") Long applicationId,
			@PathVariable("aadharnumber") String aadharNumber, @PathVariable("uk") String uniqueKey) {
		ResponseModel<String> response = null;
		try {
			response = licenseSyncingService.saveLearnerPermitDetailsForLLR(applicationId, aadharNumber, uniqueKey,
					Status.APPROVED);
		} catch (Exception e) {
			log.error("ERROR in exam post api:" + e.getMessage(), e);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}

	@RequestMapping(path = "/ispaymentcompleted", method = RequestMethod.GET)
	public ResponseEntity<?> isPaymentCompleted(HttpServletRequest request) {
		String token = request.getHeader(tokenHeader);
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);

		Boolean isPaymentCompleted;
		try {
			isPaymentCompleted = licenceService.isPaymentCompleted(sessionId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok(new ResponseModel<>(ResponseModel.SUCCESS, isPaymentCompleted));
	}

	// TODO: This are for unit test purpose, We can remove after thats done.
	@RequestMapping(path = "/communication/test/{applicationNumber}", method = RequestMethod.GET)
	public ResponseEntity<?> communicationTest(HttpServletRequest request,
			@PathVariable("applicationNumber") String applicationNumber) {
		CustMsgModel custMsgModel = communicationService.checkCommunication(applicationNumber);
		return ResponseEntity.ok(new ResponseModel<>(ResponseModel.SUCCESS, custMsgModel));
	}

	// TODO: This are for unit test purpose, We can remove after thats done.
	@RequestMapping(path = "/suspension/test/{applicationid}", method = RequestMethod.GET)
	public ResponseEntity<?> suspensionTest(HttpServletRequest request,
			@PathVariable("applicationid") Long applicationId) {
		ResponseModel<String> responseModel = licenseSyncingService.suspendCancelLicense(applicationId, null);
		return ResponseEntity.ok(new ResponseModel<>(ResponseModel.SUCCESS, responseModel));
	}

}

package org.rta.citizen.freshrc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.service.impl.ACApplicationServiceImpl;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.OwnerConscent;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FreshRCServiceImpl implements FreshRCService {

	private static final Logger logger = Logger.getLogger(ACApplicationServiceImpl.class);
	private static final String OWNER_NO_RESPONSE = "No response from owner";
	// @Autowired
	// private RegistrationService registrationService;
	//
	// @Autowired
	// private UserSessionDAO userSessionDAO;
	//
	// @Autowired
	// private ApplicationFormDataDAO applicationFormDataDAO;

	@Value("${activiti.citizen.task.code.ownerconscent}")
	private String taskOwnerConscent;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Value(value = "${service.registration.port}")
	private String PORT;

	@Value(value = "${service.registration.host}")
	private String HOST;

	@Value(value = "${service.registration.path}")
	private String ROOT_URL;

	@Override
	@Transactional
	public ResponseModel<String> saveOrUpdateFreshRC(Long vehicleRcId, Long applicationId, String prNumber) {
		/*
		 * try { ApplicationFormDataEntity entity =
		 * applicationFormDataDAO.getApplicationFormData(applicationId,
		 * FormCodeType.AC_FORM.getLabel()); ObjectMapper mapper = new
		 * ObjectMapper(); AddressChangeModel model =
		 * mapper.readValue(entity.getFormData(), AddressChangeModel.class); if
		 * (!ObjectsUtil.isNull(vehicleRcId)) {
		 * model.setVehicleRcId(vehicleRcId); } else {
		 * RegistrationServiceResponseModel<ApplicationModel>
		 * registrationServiceResponseModel=
		 * registrationService.getPRDetails(prNumber);
		 * model.setVehicleRcId(registrationServiceResponseModel.getResponseBody
		 * ().getVehicleRcId()); }
		 * model.setServiceType(ServiceType.ADDRESS_CHANGE);
		 * RegistrationServiceResponseModel<SaveUpdateResponse> regResponse =
		 * registrationService.saveOrUpdateForCustomerDetails(model); if
		 * (regResponse.getHttpStatus() == HttpStatus.OK) { return new
		 * ResponseModel<String>(ResponseModel.SUCCESS); } } catch (Exception
		 * ex) { logger.
		 * error("Getting error in update Or save in Change of Address details"
		 * ); }
		 */
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	private StringBuilder getRootURL() {
		StringBuilder url = new StringBuilder("http://").append(HOST);
		if (!StringsUtil.isNullOrEmpty(PORT)) {
			url.append(":").append(PORT);
		}
		url.append("/").append(ROOT_URL);
		return url;
	}

	private String getUsername() {
		return CitizenConstants.CITIZEN_USERID;
	}

	private String getPassword() {
		return "admin";
	}

	// Scheduler For owner Conscent
	@Override
	@Scheduled(cron = "0 0 0 * * ?")
	public void changeOwnerConsent() throws UnauthorizedException {

		ActivitiResponseModel<List<RtaTaskInfo>> activeInstances = activitiService
				.getActiveInstances(taskOwnerConscent);
		List<RtaTaskInfo> tasks = activeInstances.getActiveTasks();
		List<String> exectionIds = new ArrayList<String>();
		for (RtaTaskInfo task : tasks) {
			exectionIds.add(task.getProcessInstanceId());
		}

		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<OwnerConscent> httpEntity = null;
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		OwnerConscent consent = null;
		Map<String, String> appList = applicationService.getApplicationFromExecId(exectionIds);
		if (!appList.isEmpty()) {
			for (Map.Entry<String, String> entry : appList.entrySet()) {
				consent = new OwnerConscent();
				consent.setOwnerConscent(false);
				consent.setOwnerComment(OWNER_NO_RESPONSE);
				httpEntity = new HttpEntity<OwnerConscent>(consent, headers);
				ResponseEntity<FreshRcModel> response = restTemplate.exchange(
						getRootURL().append("/application/updateownerconsent/").append(entry.getValue()).toString(),
						HttpMethod.POST, httpEntity, FreshRcModel.class);
				String instanceId = entry.getKey();
				if (response.getStatusCode() == HttpStatus.OK) {
					Assignee assignee = new Assignee();
					assignee.setUserId(CitizenConstants.CITIZEN_USERID);
					Map<String, Object> variables = new HashMap<String, Object>();
					if (response.getBody().getOwnerConsent()) {
						variables.put(taskOwnerConscent, Status.APPROVED);
					} else {
						variables.put(taskOwnerConscent, Status.REJECTED);
					}
					activitiService.completeTask(assignee, taskOwnerConscent, instanceId, true, variables);
				}
			}
		}
	}
}

package org.rta.citizen.common.service.registration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.model.LoginModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.model.updated.LicenseIDPDtlsModel;
import org.rta.citizen.licence.model.updated.SupensionCancellationModel;
import org.rta.citizen.licence.model.updated.SuspensionRevocationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class RegistrationLicenseServiceImpl implements RegistrationLicenseService {

	private static final Logger logger = Logger.getLogger(RegistrationLicenseServiceImpl.class);

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private RestTemplate restTemplate;

	@Value(value = "${service.registration.host}")
	private String HOST;

	@Value(value = "${service.registration.port}")
	private String PORT;

	@Value(value = "${service.registration.path}")
	private String ROOT_URL;

	@Value(value = "${citizen.jwt.expiration}")
	private Long tokenExpiryTimeInSeconds;

	private String getPassword() {
		return "admin";
	}

	private String getUsername() {
		return CitizenConstants.CITIZEN_USERID;
	}

	private StringBuilder getRootURL() {
		StringBuilder url = new StringBuilder("http://").append(HOST);
		if (!StringsUtil.isNullOrEmpty(PORT)) {
			url.append(":").append(PORT);
		}
		url.append("/").append(ROOT_URL);
		return url;
	}

	@Override
	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getLicenseHolderDtls(String aadharNo,
			Long licenceHolderId, String uniqueKey) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<LicenseHolderPermitDetails> response = null;
		HttpStatus httpStatus = null;
		StringBuilder addUrl = new StringBuilder();
		boolean addEnd = false;
		if (!StringsUtil.isNullOrEmpty(aadharNo)) {
			addUrl.append("?aadharno=" + aadharNo);
			addEnd = true;
		}
		if (!ObjectsUtil.isNull(licenceHolderId)) {
			addUrl = (addEnd) ? addUrl.append("&") : addUrl.append("?");
			addUrl.append("licenceholderid=" + licenceHolderId);
			addEnd = true;
		}
		if (!StringsUtil.isNullOrEmpty(uniqueKey)) {
			addUrl = (addEnd) ? addUrl.append("&") : addUrl.append("?");
			addUrl.append("uniquekey=" + uniqueKey);
		}
		String url = null;
		// TODO : need to work.. Sohan
		if (!StringsUtil.isNullOrEmpty(uniqueKey)) {
			if (uniqueKey.contains("LL")) {
				url = getRootURL().append("/learner/license/details").append(addUrl).toString();
			} else if (uniqueKey.contains("DL") || uniqueKey.contains("AP")) {
				url = getRootURL().append("/driver/license/details").append(addUrl).toString();
			}
		} else {
			url = getRootURL().append("/learner/license/details").append(addUrl).toString();
		}
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, LicenseHolderPermitDetails.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		LicenseHolderPermitDetails licenseHolderDtlsModel = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				licenseHolderDtlsModel = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<LicenseHolderPermitDetails>(httpStatus, licenseHolderDtlsModel);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLicenseHolderDtls(LicenseHolderDtlsModel model)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LicenseHolderDtlsModel> httpEntity = new HttpEntity<LicenseHolderDtlsModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/learner/license/details").toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		SaveUpdateResponse saveUpdateResponse = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				saveUpdateResponse = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, saveUpdateResponse);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLicenseHolderDtls(LicenseHolderPermitDetails models)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LicenseHolderPermitDetails> httpEntity = new HttpEntity<LicenseHolderPermitDetails>(models, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/learner/license/permit/details").toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		SaveUpdateResponse saveUpdateResponse = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				saveUpdateResponse = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, saveUpdateResponse);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLearnerPermitDetails(
			List<LearnersPermitDtlModel> models, String aadharNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<LearnersPermitDtlModel>> httpEntity = new HttpEntity<List<LearnersPermitDtlModel>>(models,
				headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/learner/permit/details").append("?aadharno=" + aadharNumber).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse saveUpdateResponse = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				saveUpdateResponse = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, saveUpdateResponse);
	}

	@Override
	public RegLicenseServiceResponseModel<List<LearnersPermitDtlModel>> getLearnerPermitDtls(Long licenceHolderId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<LearnersPermitDtlModel[]> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate
					.exchange(
							getRootURL().append("/learner/license/details")
									.append("?licenceholderid=" + licenceHolderId).toString(),
							HttpMethod.GET, httpEntity, LearnersPermitDtlModel[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		List<LearnersPermitDtlModel> permitList = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				permitList = Arrays.asList(response.getBody());
			}
		}
		return new RegLicenseServiceResponseModel<List<LearnersPermitDtlModel>>(httpStatus, permitList);
	}

	@Override
	public RegLicenseServiceResponseModel<List<String>> getVehicleClasses(String aadharNo, String showVehicleClass)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<String[]> response = null;
		HttpStatus httpStatus = null;
		String url = null;
		if (StringsUtil.isNullOrEmpty(showVehicleClass)) {
			url = getRootURL().append("/license/vehicleclass/list/").append(aadharNo).toString();
		} else {
			url = getRootURL().append("/license/vehicleclass/list/").append(aadharNo)
					.append("?show=" + showVehicleClass).toString();
		}
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		List<String> vehicleClasses = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				vehicleClasses = Arrays.asList(response.getBody());
			}
		}
		return new RegLicenseServiceResponseModel<List<String>>(httpStatus, vehicleClasses);
	}

	@Override
	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getLicenseHolderDtlsForDriver(String aadharNo,
			String uniqueKey) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<LicenseHolderPermitDetails> response = null;
		HttpStatus httpStatus = null;
		StringBuilder addUrl = new StringBuilder();
		boolean addEnd = false;
		if (!StringsUtil.isNullOrEmpty(aadharNo)) {
			addUrl.append("?aadharno=" + aadharNo);
			addEnd = true;
		}
		if (!StringsUtil.isNullOrEmpty(uniqueKey)) {
			addUrl = (addEnd) ? addUrl.append("&") : addUrl.append("?");
			addUrl.append("uniquekey=" + uniqueKey);
		}
		String url = getRootURL().append("/driver/license/details").append(addUrl).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, LicenseHolderPermitDetails.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		LicenseHolderPermitDetails driverPermitDetailsList = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				driverPermitDetailsList = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<LicenseHolderPermitDetails>(httpStatus, driverPermitDetailsList);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLicenseHolderDtlsForDriver(
			LicenseHolderPermitDetails model, String aadharNo) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LicenseHolderPermitDetails> httpEntity = new HttpEntity<LicenseHolderPermitDetails>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/driver/license/details").append("?aadharno=" + aadharNo).toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> updateLicenseHolderDetails(LicenseHolderDtlsModel model)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LicenseHolderDtlsModel> httpEntity = new HttpEntity<LicenseHolderDtlsModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/license/holder/details").toString(), HttpMethod.PUT,
					httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in updateLicenseHolderDetails service " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> addEndorsmentsInDriverPermitDetails(
			LicenseHolderPermitDetails model) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LicenseHolderPermitDetails> httpEntity = new HttpEntity<LicenseHolderPermitDetails>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/driver/license/endorsment/details").toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in addEndorsmentsInDriverPermitDetails service " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> addDriverPermitDetailsForDLRE(
			List<DriversLicenceDetailsModel> models, String aadharNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<DriversLicenceDetailsModel>> httpEntity = new HttpEntity<List<DriversLicenceDetailsModel>>(
				models, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/driver/license/retest/details").append("?aadharno=" + aadharNumber)
				.toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in addDriverPermitDetailsForDLRE service " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<List<RTAOfficeModel>> getRtaOfficeList() throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(headers);
		ResponseEntity<RTAOfficeModel[]> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/rtaoffice/details").toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, RTAOfficeModel[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getRtaOfficeList service " + ex.getMessage());
		}
		List<RTAOfficeModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegLicenseServiceResponseModel<List<RTAOfficeModel>>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getLicenseHolderDtlsForDriver(String uniqueKey)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<LicenseHolderPermitDetails> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/driver/license/details/").append(uniqueKey).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, LicenseHolderPermitDetails.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		LicenseHolderPermitDetails driverPermitDetailsList = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				driverPermitDetailsList = response.getBody();
				List<DriversLicenceDetailsModel> validCovs = driverPermitDetailsList.getDriversPermitDetailsList();
				List<DriversLicenceDetailsModel> expiredCOV = new ArrayList<>();
				for (DriversLicenceDetailsModel model : validCovs) {
					if (model.getValidFlg().equalsIgnoreCase("Y") && model.getValidTo() != null
							&& model.getValidTo().before(new Date())) {
						expiredCOV.add(model);
					}
				}
				if (expiredCOV != null && expiredCOV.size() > 0) {
					validCovs.removeAll(expiredCOV);
				}
				driverPermitDetailsList.setDriversPermitDetailsList(validCovs);
			}
		}
		return new RegLicenseServiceResponseModel<LicenseHolderPermitDetails>(httpStatus, driverPermitDetailsList);
	}

	@Override
	public RegLicenseServiceResponseModel<LicenseHolderDtlsModel> getLicenseHolderDtls(String aadhaarNumber,
			String passportNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		StringBuilder addUrl = new StringBuilder();
		if (!StringsUtil.isNullOrEmpty(aadhaarNumber)) {
			addUrl.append("?aadharno=" + aadhaarNumber);
		} else {
			addUrl.append("?passportnumber=" + passportNumber);
		}
		String url = getRootURL().append("/license/holder/details").append(addUrl).toString();
		;
		ResponseEntity<LicenseHolderDtlsModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, LicenseHolderDtlsModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseHolderDtls service " + ex.getMessage());
		}
		LicenseHolderDtlsModel licenseHolderDtlsModel = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				licenseHolderDtlsModel = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<LicenseHolderDtlsModel>(httpStatus, licenseHolderDtlsModel);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> driverLicenceCommonService(String aadharNumber,
			List<DriversLicenceDetailsModel> models) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<DriversLicenceDetailsModel>> httpEntity = new HttpEntity<List<DriversLicenceDetailsModel>>(
				models, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/dl/common/service/details").append("?aadharno=" + aadharNumber).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in driverLicenceCommonService " + models.get(0).getDlType() + " " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<String> updateInLicenseHolderDetails(String serviceCode,
			LicenseHolderDtlsModel model) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LicenseHolderDtlsModel> httpEntity = new HttpEntity<LicenseHolderDtlsModel>(model, headers);
		ResponseEntity<String> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/licence/holder/details").append("?servicecode=" + serviceCode).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in updateMobileEmailId " + serviceCode + " " + ex.getMessage());
		}
		String responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<String>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> updateLicenceAttachmentsDetails(
			List<AttachmentModel> models, String aadhaarNo) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<AttachmentModel>> httpEntity = new HttpEntity<List<AttachmentModel>>(models, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/multipledocs/licence/").append(aadhaarNo).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in updateLicenceAttachmentsDetails " + aadhaarNo + " " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getForgotLicenceNumber(String dob,
			String aadharNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(headers);
		ResponseEntity<LicenseHolderPermitDetails> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/forgot/licence/").append(dob).append("/").append(aadharNumber).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, LicenseHolderPermitDetails.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getForgotLicenceNumber " + aadharNumber + " " + ex.getMessage());
		}
		LicenseHolderPermitDetails responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<LicenseHolderPermitDetails>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveUpdateIntrnationalLicenseDtls(
			LicenseIDPDtlsModel model, String aadharNo) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LicenseIDPDtlsModel> httpEntity = new HttpEntity<LicenseIDPDtlsModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/driver/license/idpdetails").append("?aadharno=" + aadharNo).toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in getLicenseIDPDtls service " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> suspendCancelLicense(String dLNumber,
			SupensionCancellationModel model) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<SupensionCancellationModel> httpEntity = new HttpEntity<SupensionCancellationModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/driver/license/suscancel").append("?dlnumber=" + dLNumber).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in suspendCancelLicense service " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegLicenseServiceResponseModel<SaveUpdateResponse> licenseRevokeSuspension(String dLNumber,
			SuspensionRevocationModel model) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<SuspensionRevocationModel> httpEntity = new HttpEntity<SuspensionRevocationModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		String url = getRootURL().append("/driver/license/revokesusp").append("?dlnumber=" + dLNumber).toString();
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			logger.error("Getting in licenseRevokeSuspension service " + ex.getMessage());
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegLicenseServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<UserModel> getUserDetails(String userName) throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = registrationService.loginIfRequired(getUsername(),
				getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<UserModel> response = restTemplate.exchange(
				getRootURL().append("/users/").append(userName).toString(), HttpMethod.GET, httpEntity,
				UserModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		UserModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<UserModel>(httpStatus, responseBody);
	}

}

package org.rta.citizen.common.service.registration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.MessageConfig;
import org.rta.citizen.aadharseeding.rc.model.RCAadharSeedModel;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.PermitDetailsType;
import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadhaarTCSDetailsRequestModel;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.model.ChallanDetailsModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CrimeDetailsListModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.DealerModel;
import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.FinancerModel;
import org.rta.citizen.common.model.FitnessDetailsModel;
import org.rta.citizen.common.model.HPAHPTSyncModel;
import org.rta.citizen.common.model.InsuranceDetailsModel;
import org.rta.citizen.common.model.LoginModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.OwnerConscent;
import org.rta.citizen.common.model.PermitDetailsModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.PermitTypeModel;
import org.rta.citizen.common.model.PermitTypeVehicleClassModel;
import org.rta.citizen.common.model.PucDetailsModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.SuspendedRCNumberModel;
import org.rta.citizen.common.model.SyncDataModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.VehicleAlterationUpdateModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.VehicleClassDescModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.communication.CustMsgModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.model.payment.TaxTypeModel;
import org.rta.citizen.common.model.payment.TransactionDetailModel;
import org.rta.citizen.common.model.vcr.VcrModel;
import org.rta.citizen.common.service.communication.CommunicationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.duplicateregistration.model.DuplicateRegistrationModel;
import org.rta.citizen.fitness.cfx.model.CFXModel;
import org.rta.citizen.fitness.cfx.model.CFXNoticeModel;
import org.rta.citizen.freshrc.FinancerFreshContactDetailsModel;
import org.rta.citizen.freshrc.FreshRcModel;
import org.rta.citizen.freshrc.ShowcaseInfoRequestModel;
import org.rta.citizen.freshrc.ShowcaseNoticeInfoModel;
import org.rta.citizen.hpt.model.FinanceOtherServiceModel;
import org.rta.citizen.permit.model.PermitAuthorizationCardModel;
import org.rta.citizen.permit.model.PermitCodeDescModel;
import org.rta.citizen.permit.model.PermitTempPermitModel;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxReportModel;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.rta.citizen.vehiclereassignment.model.VehicleReassignmentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
public class RegistrationServiceImpl implements RegistrationService {

	private static final Logger log = Logger.getLogger(RegistrationServiceImpl.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CommunicationService communicationService;
	
	@Value(value = "${service.registration.host}")
	private String HOST;

	@Value(value = "${service.registration.port}")
	private String PORT;

	@Value(value = "${service.registration.path}")
	private String ROOT_URL;

	@Value(value = "${cctns.host}")
	private String CCTNS_HOST_URL;

	@Value(value = "${cctns.port}")
	private String CCTNS_PORT;

	@Value(value = "${cctns.root}")
	private String CCTNS_ROOT;

	@Value(value = "${cctns.token}")
	private String CCTNS_TOKEN;

	@Value(value = "${citizen.jwt.expiration}")
	private Long tokenExpiryTimeInSeconds;

	private TokenModel sharedToken;
	private Long expiryTime;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public final static short SEND_SMS_EMAIL = 0;
	public final static short SEND_SMS = 1;
	public final static short SEND_EMAIL = 2;
	/*
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 * 
	 * @Value(value = "${service.registration.login.url}") private String login;
	 */

	@Override
	public RegistrationServiceResponseModel<TokenModel> loginIfRequired(String username, String password)
			throws UnauthorizedException {
		Long currentTime = DateUtil.toCurrentUTCTimeStamp();
		TokenModel tokenModel = getToken(username, password);
		if (ObjectsUtil.isNull(tokenModel) || currentTime > expiryTime) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			LoginModel loginModel = new LoginModel(username, password);
			HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(loginModel);
			ResponseEntity<TokenModel> response = restTemplate.exchange(getRootURL().append("/login").toString(),
					HttpMethod.POST, httpEntity, TokenModel.class);
			HttpStatus httpStatus = response.getStatusCode();
			if (httpStatus == HttpStatus.OK) {
				if (response.hasBody()) {
					tokenModel = response.getBody();
					setToken(tokenModel);
					this.expiryTime = currentTime + tokenExpiryTimeInSeconds;
					return new RegistrationServiceResponseModel<TokenModel>(httpStatus, getToken(username, password));
				}
			}
			log.info("login failed on registration service for username = " + getUsername());
			throw new UnauthorizedException("unauthorized");
		}
		return new RegistrationServiceResponseModel<TokenModel>(HttpStatus.OK, tokenModel);
	}

	@Override
	public RegistrationServiceResponseModel<ApplicationModel> getPRDetails(String prNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<ApplicationModel> response = null;
		if (!ObjectsUtil.isNull(loginResponse.getResponseBody())) {
			response = restTemplate.exchange(getRootURL().append("/pr/").append(prNumber).toString(), HttpMethod.GET,
					httpEntity, ApplicationModel.class);
		}
		HttpStatus httpStatus = response.getStatusCode();
		ApplicationModel applicationModel = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				applicationModel = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<ApplicationModel>(httpStatus, applicationModel);
	}

	private String getPassword() {
		return "admin";
	}

	private String getUsername() {
		return CitizenConstants.CITIZEN_USERID;
	}

	@Override
	public RegistrationServiceResponseModel<ApplicationModel> getTRDetails(String trNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<ApplicationModel> response = null;
		if (!ObjectsUtil.isNull(loginResponse.getResponseBody())) {
			response = restTemplate.exchange(getRootURL().append("/tr/").append(trNumber).toString(), HttpMethod.GET,
					httpEntity, ApplicationModel.class);
		}
		HttpStatus httpStatus = response.getStatusCode();
		ApplicationModel applicationModel = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				applicationModel = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<ApplicationModel>(httpStatus, applicationModel);
	}

	@Override
	public RegistrationServiceResponseModel<AadharModel> aadharAuthentication(AadhaarTCSDetailsRequestModel model)
			throws UnauthorizedException {
        log.info("############################## " + model.toString());
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<AadhaarTCSDetailsRequestModel> httpEntity = new HttpEntity<AadhaarTCSDetailsRequestModel>(model,
				headers);
		ResponseEntity<AadharModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/PIDBlockTCS").toString(), HttpMethod.POST,
					httpEntity, AadharModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		AadharModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<AadharModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<VehicleDetailsRequestModel> getVehicleDetails(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<VehicleDetailsRequestModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/vehicle/").append(vehicleRcId).toString(),
					HttpMethod.GET, httpEntity, VehicleDetailsRequestModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		VehicleDetailsRequestModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<VehicleDetailsRequestModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<CustomerDetailsRequestModel> getCustomerDetails(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<CustomerDetailsRequestModel> response = restTemplate.exchange(
				getRootURL().append("/customer/customerInfoStepOne?vehiclercid=").append(vehicleRcId).toString(),
				HttpMethod.GET, httpEntity, CustomerDetailsRequestModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		CustomerDetailsRequestModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<CustomerDetailsRequestModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<InsuranceDetailsModel> getInsuranceDetails(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<InsuranceDetailsModel> response = restTemplate.exchange(
				getRootURL().append("/iib/insurance/details/").append(vehicleRcId).toString(), HttpMethod.GET,
				httpEntity, InsuranceDetailsModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		InsuranceDetailsModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<InsuranceDetailsModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<FinanceModel> getFinancierDetails(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<FinanceModel> response = restTemplate.exchange(
				getRootURL().append("/financerdetails/").append(vehicleRcId).toString(), HttpMethod.GET, httpEntity,
				FinanceModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		FinanceModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FinanceModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<CrimeDetailsListModel> getCrimeDetails(String prNo)
			throws UnauthorizedException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", CCTNS_TOKEN);
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<CrimeDetailsListModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getCCTNSRootURL().append("?rcNumber=").append(prNo).toString(),
					HttpMethod.GET, httpEntity, CrimeDetailsListModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		} catch (Exception ex) {
			httpStatus = httpStatus.NOT_FOUND;
		}
		CrimeDetailsListModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<CrimeDetailsListModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<PucDetailsModel> getPucDetails(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<PucDetailsModel> response = restTemplate.exchange(
				getRootURL().append("/pucdetails/").append(vehicleRcId).toString(), HttpMethod.GET, httpEntity,
				PucDetailsModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		PucDetailsModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<PucDetailsModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<NocDetails> getNocDetails(Long vehicleRcId, String prNumber)
			throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		String queryP = "?";
		if (!ObjectsUtil.isNull(vehicleRcId)) {
			queryP += "vehiclercid=" + vehicleRcId;
		} else if (!StringsUtil.isNullOrEmpty(prNumber)) {
			queryP += "prnumber=" + prNumber;
		}
		ResponseEntity<NocDetails> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/noc/details").append(queryP).toString(),
					HttpMethod.GET, httpEntity, NocDetails.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		NocDetails responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<NocDetails>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<ChallanDetailsModel>> getChallanDetails(Long vehicleRcId) {
		return new RegistrationServiceResponseModel<List<ChallanDetailsModel>>(HttpStatus.OK, new ArrayList<>());
	}

	@Override
	public RegistrationServiceResponseModel<TaxModel> getTaxDetails(Long vehicleRcId) throws UnauthorizedException {
		
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<TaxModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/tax/details/vehiclerc/").append(vehicleRcId).toString(),
					HttpMethod.GET, httpEntity, TaxModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		TaxModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<TaxModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<PermitHeaderModel>> getPermitDetails(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<List<PermitHeaderModel>> response = restTemplate.exchange(
				getRootURL().append("/permitdetails/").append(vehicleRcId).toString(), HttpMethod.GET, httpEntity,
				new ParameterizedTypeReference<List<PermitHeaderModel>>() {
				});
		HttpStatus httpStatus = response.getStatusCode();
		List<PermitHeaderModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<List<PermitHeaderModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<FitnessDetailsModel> getFitnessDetails(Long vehicleRcId, Long mviUserId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<FitnessDetailsModel> response = restTemplate.exchange(getRootURL().append("/certificate/")
				.append(vehicleRcId).append("/FC").append("?approver_mvi=").append(mviUserId).toString(),
				HttpMethod.GET, httpEntity, FitnessDetailsModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		FitnessDetailsModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FitnessDetailsModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<RTAOfficeModel> getRTAOfficeByMandal(Integer code, Long vehicleRcId) {
		StringBuilder sb = getRootURL().append("/rta/mandal/").append(code).append("?vehiclercid=");
		StringsUtil.appendIfNotNull(sb, vehicleRcId);
		ResponseEntity<RTAOfficeModel> response = restTemplate.exchange(sb.toString(), HttpMethod.GET, null,
				RTAOfficeModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		RTAOfficeModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<RTAOfficeModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<RTAOfficeModel>> getRTAOfficeByState(String stateCode) {
		StringBuilder sb = getRootURL().append("/rtaoffices?state=").append(stateCode);
		ResponseEntity<List<RTAOfficeModel>> response = restTemplate.exchange(sb.toString(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<RTAOfficeModel>>() {
				});
		HttpStatus httpStatus = response.getStatusCode();
		List<RTAOfficeModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<List<RTAOfficeModel>>(httpStatus, responseBody);
	}

	private StringBuilder getRootURL() {
		StringBuilder url = new StringBuilder("http://").append(HOST);
		if (!StringsUtil.isNullOrEmpty(PORT)) {
			url.append(":").append(PORT);
		}
		url.append("/").append(ROOT_URL);
		return url;
	}

	private StringBuilder getCCTNSRootURL() {
		StringBuilder url = new StringBuilder("http://").append(CCTNS_HOST_URL);
		if (!StringsUtil.isNullOrEmpty(CCTNS_PORT)) {
			url.append(":").append(CCTNS_PORT);
		}
		url.append("/").append(CCTNS_ROOT);
		return url;
	}

	private TokenModel getToken(String username, String password) {
		return this.sharedToken;
	}

	private void setToken(TokenModel token) {
		this.sharedToken = token;
	}

	@Override
	public RegistrationServiceResponseModel<FinanceOtherServiceModel> getFinancier(String prNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<FinanceOtherServiceModel> httpEntity = new HttpEntity<FinanceOtherServiceModel>(headers);

		ResponseEntity<FinanceOtherServiceModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/hpt/status/").append(prNumber).toString(),
					HttpMethod.GET, httpEntity, FinanceOtherServiceModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		FinanceOtherServiceModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FinanceOtherServiceModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<FinanceOtherServiceModel> sendOtherAppToFinancier(
			FinanceOtherServiceModel form) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<FinanceOtherServiceModel> httpEntity = new HttpEntity<FinanceOtherServiceModel>(form, headers);
		ResponseEntity<FinanceOtherServiceModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/finance/otherservice/apply").toString(),
					HttpMethod.POST, httpEntity, FinanceOtherServiceModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			log.error("Exception Status : " + httpStatus);
		}
		FinanceOtherServiceModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FinanceOtherServiceModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<Object> saveHPA(String appNo, String prNumber, Long quoteAmount)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<Object> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/hpa/apply").append("/app_number/").append(appNo).append("/pr/")
							.append(prNumber).append("?quotation_price=").append(quoteAmount).toString(),
					HttpMethod.POST, httpEntity, Object.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		Object responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Object>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<HashMap<String, Boolean>> isOnlineFinanced(String prNum)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		@SuppressWarnings("rawtypes")
		ResponseEntity<HashMap> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/onlinefinanced/").append(prNum).toString(),
					HttpMethod.GET, httpEntity, HashMap.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		HashMap<String, Boolean> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<HashMap<String, Boolean>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<FinancerModel>> getFinancerList(String applicationNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<List<FinancerModel>> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/citizen/financerlist/").append(applicationNumber).toString(), HttpMethod.GET,
					httpEntity, new ParameterizedTypeReference<List<FinancerModel>>() {
					});
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		List<FinancerModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<List<FinancerModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<Object> approveFinancier(String appNo, FinancerModel financerModel)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<Object> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/citizen/financerselection/").append(appNo).append("/")
							.append(financerModel.getFinancerId()).toString(),
					HttpMethod.POST, httpEntity, Object.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		Object responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Object>(httpStatus, responseBody);
	}

	@Override
	public UserModel getRtaUserByToken(String rtaToken) throws UnauthorizedException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", rtaToken);
		HttpEntity<UserModel> httpEntity = new HttpEntity<UserModel>(headers);
		ResponseEntity<UserModel> response = restTemplate.exchange(getRootURL().append("/rta/user").toString(),
				HttpMethod.GET, httpEntity, UserModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		UserModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}

		return responseBody;
	}

	@Override
	public RegistrationServiceResponseModel<RTAOfficeModel> getRtaDetails(String code) throws UnauthorizedException {
		ResponseEntity<RTAOfficeModel> response = restTemplate.exchange(
				getRootURL().append("/rtaoffices/").append(code).toString(), HttpMethod.GET, null,
				RTAOfficeModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		RTAOfficeModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<RTAOfficeModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<UserModel> getRTAUserFromToken(String rtaToken)
			throws UnauthorizedException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", rtaToken);
		HttpEntity<UserModel> httpEntity = new HttpEntity<UserModel>(headers);
		ResponseEntity<UserModel> response = restTemplate.exchange(getRootURL().append("/rta/user").toString(),
				HttpMethod.GET, httpEntity, UserModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		UserModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<UserModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<ApplicationTaxModel> getTaxDetails(String prOrtrNumber) {
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<ApplicationTaxModel> httpEntity = new HttpEntity<ApplicationTaxModel>(headers);
		ResponseEntity<ApplicationTaxModel> response = restTemplate.exchange(
				getRootURL().append("/alltaxdetails/").append(prOrtrNumber).toString(), HttpMethod.GET, httpEntity,
				new ParameterizedTypeReference<ApplicationTaxModel>() {
				});
		HttpStatus httpStatus = response.getStatusCode();
		ApplicationTaxModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<ApplicationTaxModel>(httpStatus, responseBody);
	}

	public String getChallanNumber() {
		log.info("::::::::getChallanNumber::From Registration API's::start:::::");
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(getRootURL().append("/challanno/").toString(),
				HttpMethod.GET, httpEntity, String.class);
		HttpStatus httpStatus = response.getStatusCode();
		String responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		log.info("::::::::getChallanNumber::From Registration API's::end::::: " + responseBody);
		return responseBody;
	}

	public TransactionDetailModel getEncryptedSBIParameter(TransactionDetailModel transactionDetailModel) {
		log.info(":::CITIZEN:::getEncryptedSBIParameter::::::::start::::::");
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TransactionDetailModel> httpEntity = new HttpEntity<TransactionDetailModel>(transactionDetailModel,
				headers);
		ResponseEntity<TransactionDetailModel> response = restTemplate.exchange(
				getRootURL().append("/paymentparameter/").toString(), HttpMethod.POST, httpEntity,
				TransactionDetailModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		TransactionDetailModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		log.info(":::CITIZEN:::getEncryptedSBIParameter::::::::end::::::");
		return responseBody;
	}

	@Override
	public TransactionDetailModel decryptSBIResponse(TransactionDetailModel transactionDetailModel) {
		log.info(":::CITIZEN:::decryptSBIResponse::::::::start::::::");
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TransactionDetailModel> httpEntity = new HttpEntity<TransactionDetailModel>(transactionDetailModel,
				headers);
		ResponseEntity<TransactionDetailModel> response = restTemplate.exchange(
				getRootURL().append("/payment/decryptVerification/").toString(), HttpMethod.POST, httpEntity,
				TransactionDetailModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		TransactionDetailModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		log.info(":CITIZEN::decryptSBIResponse::::end::");
		return responseBody;
	}

	@Override
	public TransactionDetailModel getVerificationEncrytData(TransactionDetailModel transactionDetailModel) {
		log.info(":::CITIZEN:::getVerificationEncrytData::::::::start:::::: " + transactionDetailModel);
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TransactionDetailModel> httpEntity = new HttpEntity<TransactionDetailModel>(transactionDetailModel,
				headers);
		ResponseEntity<TransactionDetailModel> response = restTemplate.exchange(
				getRootURL().append("/paymentverifyparameter/").toString(), HttpMethod.POST, httpEntity,
				TransactionDetailModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		TransactionDetailModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		log.info(":CITIZEN::getVerificationEncrytData::::end:: " + responseBody);
		return responseBody;
	}
	
	
	@Override
	public TransactionDetailModel getPayUVerificationEncrytData(TransactionDetailModel transactionDetailModel) {
		log.info(":::CITIZEN:::getPayUVerificationEncrytData::::::::start:::::: " + transactionDetailModel);
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TransactionDetailModel> httpEntity = new HttpEntity<TransactionDetailModel>(transactionDetailModel,
				headers);
		ResponseEntity<TransactionDetailModel> response = restTemplate.exchange(
				getRootURL().append("/payu/paymentverifyparameter/").toString(), HttpMethod.POST, httpEntity,
				TransactionDetailModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		TransactionDetailModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		log.info(":CITIZEN::getPayUVerificationEncrytData::::end:: " + responseBody);
		return responseBody;
	}

	@Override
	public RegistrationServiceResponseModel<ApplicationModel> getDetails(String uniqueKey, KeyType keyType)
			throws UnauthorizedException {
		switch (keyType) {
		case PR:
			return getPRDetails(uniqueKey);
		case TR:
			return getTRDetails(uniqueKey);
		default:
			break;
		}
		return null;
	}

	@Override
	public RegistrationServiceResponseModel<List<NocDetails>> getNocAddressList(String districtCode)
			throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<List<NocDetails>> response = restTemplate.exchange(
				getRootURL().append("/noc/address").append("?districtcode=" + districtCode).toString(), HttpMethod.GET,
				httpEntity, new ParameterizedTypeReference<List<NocDetails>>() {
				});
		HttpStatus httpStatus = response.getStatusCode();
		List<NocDetails> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<List<NocDetails>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<NocDetails> getNocAddressDetails(String nocAddressCode)
			throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<NocDetails> response = restTemplate.exchange(
				getRootURL().append("/noc/address").append("?nocaddresscode=" + nocAddressCode).toString(),
				HttpMethod.GET, httpEntity, NocDetails.class);
		HttpStatus httpStatus = response.getStatusCode();
		NocDetails responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<NocDetails>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<AadharModel> getAadharDetails(Long aadharNumber)
			throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<AadharModel> response = restTemplate.exchange(
				getRootURL().append("/aadhar/details/").append(aadharNumber).toString(), HttpMethod.GET, httpEntity,
				AadharModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		AadharModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<AadharModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<DealerModel> getUserDetails(String userName) throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<DealerModel> response = restTemplate.exchange(
				getRootURL().append("/users/").append(userName).toString(), HttpMethod.GET, httpEntity,
				DealerModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		DealerModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<DealerModel>(httpStatus, responseBody);

	}

	@Override
	public RegistrationServiceResponseModel<UserModel> getUser(String userName) throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
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

	@Override
	public RegistrationServiceResponseModel<Boolean> isUserExistsByAadharAndType(String aadharNumber, UserType userType)
			throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<Boolean> response = restTemplate.exchange(getRootURL().append("/users/aadhar?uid=")
				.append(aadharNumber).append("&usertype=").append(userType.getLabel()).toString(), HttpMethod.GET,
				httpEntity, Boolean.class);
		HttpStatus httpStatus = response.getStatusCode();
		Boolean responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Boolean>(httpStatus, responseBody);

	}

	@Override
	public RegistrationServiceResponseModel<FinanceModel> getFinanceInfo(String prNum) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<FinanceModel> response = restTemplate.exchange(
				getRootURL().append("/finance/info/").append("?prnumber=").append(prNum).toString(), HttpMethod.GET,
				httpEntity, FinanceModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		FinanceModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FinanceModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<Boolean> hasAppliedHPA(String prNum) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<Boolean> response = restTemplate.exchange(
				getRootURL().append("/hpaapplied/").append(prNum).toString(), HttpMethod.GET, httpEntity,
				Boolean.class);
		HttpStatus httpStatus = response.getStatusCode();
		Boolean responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Boolean>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<Boolean> hasAppliedHPA(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<Boolean> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/hpaapplied/vehiclerc/").append(vehicleRcId).toString(), HttpMethod.GET,
					httpEntity, Boolean.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		Boolean responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Boolean>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveSlots(CitizenApplicationModel citizenApplication)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<CitizenApplicationModel> httpEntity = new HttpEntity<CitizenApplicationModel>(citizenApplication,
				headers);
		ResponseEntity<SaveUpdateResponse> response = restTemplate.exchange(getRootURL().append("/slots").toString(),
				HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
		HttpStatus httpStatus = response.getStatusCode();
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<PermitTypeVehicleClassModel> getPermitTypeByTr(String trPrNumber)
			throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<PermitTypeVehicleClassModel> httpEntity = new HttpEntity<PermitTypeVehicleClassModel>(headers);
		ResponseEntity<PermitTypeVehicleClassModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/permittypelist?trnumber=").append(trPrNumber).toString(), HttpMethod.GET,
					httpEntity, PermitTypeVehicleClassModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			log.error("Exception While calling getPermitType : " + trPrNumber + " status : " + httpStatus);
		}

		PermitTypeVehicleClassModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<PermitTypeVehicleClassModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<?> getUserAttachmentDetails(String userName, Integer docId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		if (ObjectsUtil.isNull(docId)) {
			ResponseEntity<List<AttachmentModel>> response = restTemplate.exchange(
					getRootURL().append("/docs/user/").append(userName).toString(), HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<List<AttachmentModel>>() {
					});
			HttpStatus httpStatus = response.getStatusCode();
			List<AttachmentModel> responseBody = null;
			if (httpStatus == HttpStatus.OK) {
				if (response.hasBody()) {
					responseBody = response.getBody();
				}
			}
			return new RegistrationServiceResponseModel<List<AttachmentModel>>(httpStatus, responseBody);
		}
		ResponseEntity<AttachmentModel> response = restTemplate.exchange(
				getRootURL().append("/docs/user/").append(userName).append("?docid=").append(docId).toString(),
				HttpMethod.GET, httpEntity, AttachmentModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		AttachmentModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<AttachmentModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForUser(AttachmentModel model)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<AttachmentModel> httpEntity = new HttpEntity<AttachmentModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = restTemplate.exchange(
				getRootURL().append("/docs/user").toString(), HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
		HttpStatus httpStatus = response.getStatusCode();
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> multipleSaveOrUpdateForUser(
			List<AttachmentModel> models) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<List<AttachmentModel>> httpEntity = new HttpEntity<List<AttachmentModel>>(models, headers);
		ResponseEntity<SaveUpdateResponse> response = restTemplate.exchange(
				getRootURL().append("/multipledocs/user").toString(), HttpMethod.POST, httpEntity,
				SaveUpdateResponse.class);
		HttpStatus httpStatus = response.getStatusCode();
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> updateVehicleAlterationForBodyBuilder(
			VehicleBodyModel model) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<VehicleBodyModel> httpEntity = new HttpEntity<VehicleBodyModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/vehicle/bodybuilder").append("?isoldvehicle=").append(true).toString(),
					HttpMethod.PUT, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForNocDetails(NocDetails model)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<NocDetails> httpEntity = new HttpEntity<NocDetails>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/noc/details").toString(), HttpMethod.POST,
					httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForCustomerDetails(AddressChangeModel model)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<AddressChangeModel> httpEntity = new HttpEntity<AddressChangeModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/customer/details").toString(), HttpMethod.PUT,
					httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForVehicleAlterationDetails(
			VehicleAlterationUpdateModel model) throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<VehicleAlterationUpdateModel> httpEntity = new HttpEntity<VehicleAlterationUpdateModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/vehicle/alteration/details").toString(),
					HttpMethod.PUT, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	//
	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateDealer(UserModel model)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<UserModel> httpEntity = new HttpEntity<UserModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/user/register/other").toString(), HttpMethod.POST,
					httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<UserSignupModel> saveOrUpdateUser(UserSignupModel model)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<UserSignupModel> httpEntity = new HttpEntity<UserSignupModel>(model, headers);
		ResponseEntity<UserSignupModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/user/new").toString(), HttpMethod.POST, httpEntity,
					UserSignupModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		UserSignupModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<UserSignupModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForDuplicateRegistrationDetails(
			DuplicateRegistrationModel model) throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<DuplicateRegistrationModel> httpEntity = new HttpEntity<DuplicateRegistrationModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/vehicle/duplicateregistration/details").toString(),
					HttpMethod.PUT, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> updateDataHPAHPT(HPAHPTSyncModel hPAHPTSyncModel)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<HPAHPTSyncModel> httpEntity = new HttpEntity<HPAHPTSyncModel>(hPAHPTSyncModel, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/rtoresponseonservice/hpahpt").append("/approved").toString(), HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<MessageConfig> getCommunicationConfig() throws UnauthorizedException {
		log.info(":::getCommunicationConfig::::start:::::");
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<MessageConfig> httpEntity = new HttpEntity<MessageConfig>(headers);
		ResponseEntity<MessageConfig> response = restTemplate.exchange(getRootURL().append("/msgconfig").toString(),
				HttpMethod.GET, httpEntity, new ParameterizedTypeReference<MessageConfig>() {
				});
		HttpStatus httpStatus = response.getStatusCode();
		MessageConfig responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		log.info(":::getCommunicationConfig:::end::::");
		return new RegistrationServiceResponseModel<MessageConfig>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<String> getCustomerInvoice(Long vehicleRcId, String regType, boolean isNoc)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<String> response = restTemplate
				.exchange(
						getRootURL().append("/getcustomerinvoice/").append(vehicleRcId).append("?regtype=")
								.append(regType).append("&isnoc=").append(isNoc).toString(),
						HttpMethod.GET, httpEntity, String.class);
		HttpStatus httpStatus = response.getStatusCode();
		String responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<String>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<String> getSignature(Long vehicleRcId) throws UnauthorizedException {
		log.info("::getSignature:::Service::::start:::: " + vehicleRcId);
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				getRootURL().append("/getsignature/").append(vehicleRcId).toString(), HttpMethod.GET, httpEntity,
				String.class);
		HttpStatus httpStatus = response.getStatusCode();
		String responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<String>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<VehicleClassDescModel>> getVehicleClassDesc(String regCategoryCode,
			Integer alterationCategory) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<VehicleClassDescModel[]> response = null;
		HttpStatus httpStatus = null;
		try {
			if (ObjectsUtil.isNull(alterationCategory)) {
				response = restTemplate.exchange(
						getRootURL().append("/vehicleclassdetails/").append(regCategoryCode).toString(), HttpMethod.GET,
						httpEntity, VehicleClassDescModel[].class);
			} else {
				response = restTemplate.exchange(
						getRootURL().append("/vehicleclassdetails/").append(regCategoryCode).append("?alteration_cat=")
								.append(alterationCategory).toString(),
						HttpMethod.GET, httpEntity, VehicleClassDescModel[].class);
			}
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		List<VehicleClassDescModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegistrationServiceResponseModel<List<VehicleClassDescModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<VehicleBodyModel> getVehicleAlterationDetails(Long vehicleRcId, String authToken)
			throws UnauthorizedException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if(ObjectsUtil.isNull(authToken)){
			RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
			headers.add("Authorization", loginResponse.getResponseBody().getToken());
		} else {
			try {
				TokenType tokenType = TokenType.valueOf(jwtTokenUtil.getTokenType(authToken));
				if (tokenType == TokenType.CITIZEN) {
					RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(),
							getPassword());
					headers.add("Authorization", loginResponse.getResponseBody().getToken());
				} else {
					headers.add("Authorization", authToken);
				}
			} catch (Exception ex) {
				headers.add("Authorization", authToken);
			}
		}
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<VehicleBodyModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/vehicle/").append(vehicleRcId)
					.append("?isvehiclealterdetails=").append(true).toString(), HttpMethod.GET, httpEntity,
					VehicleBodyModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		VehicleBodyModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<VehicleBodyModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForCitizenCommonSerives(
			CommonServiceModel model) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<CommonServiceModel> httpEntity = new HttpEntity<CommonServiceModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/vehicle/commoncitizenservice/details").toString(),
					HttpMethod.PUT, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForVehicleReassignmentSerives(
			VehicleReassignmentModel model) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<VehicleReassignmentModel> httpEntity = new HttpEntity<VehicleReassignmentModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/application/vehiclereassignment").toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> syncTheft(TheftIntimationRevocationModel theftModel,
			String prNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TheftIntimationRevocationModel> httpEntity = new HttpEntity<TheftIntimationRevocationModel>(
				theftModel, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/reg/sync/theft/").append(prNumber).toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			log.error("Exception Status : " + httpStatus);
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<AttachmentModel>> getAttachmentsDetails(String chassisNumber,
			Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<AttachmentModel[]> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/docsurls/").append(chassisNumber).append("/" + vehicleRcId)
							.append("?isoldvehicle=" + true).toString(),
					HttpMethod.GET, httpEntity, AttachmentModel[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		List<AttachmentModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegistrationServiceResponseModel<List<AttachmentModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<ShowcaseNoticeInfoModel> getShowcaseInfo(ShowcaseInfoRequestModel request)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<ShowcaseInfoRequestModel> httpEntity = new HttpEntity<ShowcaseInfoRequestModel>(request, headers);
		ResponseEntity<ShowcaseNoticeInfoModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/getshowcaseinfo/").toString(), HttpMethod.POST,
					httpEntity, ShowcaseNoticeInfoModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		ShowcaseNoticeInfoModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<ShowcaseNoticeInfoModel>(httpStatus, responseBody);
	}

	
	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveCustomerInfoForDataEntry(String token,
			CustomerDetailsRequestModel customerRequest) throws UnauthorizedException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", token);
		HttpEntity<CustomerDetailsRequestModel> httpEntity = new HttpEntity<CustomerDetailsRequestModel>(
				customerRequest, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/customer/1").toString(), HttpMethod.POST, httpEntity,
					SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<VehicleClassDescModel> getCovDetails(String prNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<VehicleClassDescModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/vehicleclassdetails/pr/").append(prNumber).toString(), HttpMethod.GET,
					httpEntity, VehicleClassDescModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		VehicleClassDescModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<VehicleClassDescModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<PermitTypeModel>> getPermitType(String cov)
			throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<PermitTypeModel[]> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/permittypes/cov/").append(cov).toString(),
					HttpMethod.GET, httpEntity, PermitTypeModel[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			log.error("Exception While calling getPermitType class code : " + cov + " status : " + httpStatus);
		}

		List<PermitTypeModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegistrationServiceResponseModel<List<PermitTypeModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> syncData(SyncDataModel syncDataModel)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<SyncDataModel> httpEntity = new HttpEntity<SyncDataModel>(syncDataModel, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/syncdata").toString(), HttpMethod.POST, httpEntity,
					SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
			log.error("Exception Status : " + httpStatus);
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<PermitCodeDescModel>> getGoodsRouteCondnsForPrimaryPermit(
			PermitDetailsType permitDetailsType, String cov, String permitType) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<PermitCodeDescModel[]> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/type/").append(permitDetailsType.getLabel()).append("/cov/").append(cov)
							.append("/permit/").append(permitType).toString(),
					HttpMethod.GET, httpEntity, PermitCodeDescModel[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		List<PermitCodeDescModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegistrationServiceResponseModel<List<PermitCodeDescModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<PermitTempPermitModel> getPermitTempPermits(String prNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<PermitTempPermitModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/permit/temporary/pr/").append(prNumber).toString(),
					HttpMethod.GET, httpEntity, PermitTempPermitModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		PermitTempPermitModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<PermitTempPermitModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<Map<String, Object>> getSelectedPukkaTempPermit(String prNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<Map> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/pukkatemp/permittype/").append(prNumber).toString(),
					HttpMethod.GET, httpEntity, Map.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		Map<String, Object> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Map<String, Object>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<PermitDetailsModel> getPermitCertificate(Long vehicleRcId,
			String certificateType, Long mviUserId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<PermitDetailsModel> response = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/certificate/").append(vehicleRcId).append("/").append(certificateType)
							.append("?approver_mvi=").append(mviUserId).toString(),
					HttpMethod.GET, httpEntity, PermitDetailsModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		PermitDetailsModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<PermitDetailsModel>(httpStatus, responseBody);
	}

	/// permit/authcard/{pr_number}
	@Override
	public RegistrationServiceResponseModel<PermitAuthorizationCardModel> getPermitAuthCardDetails(String prNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<PermitAuthorizationCardModel> response = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/permit/authcard/").append(prNumber).toString(),
					HttpMethod.GET, httpEntity, PermitAuthorizationCardModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		PermitAuthorizationCardModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<PermitAuthorizationCardModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> aadhaarSeedingWithSystem(
			RCAadharSeedModel rcAadharSeedModel) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<RCAadharSeedModel> httpEntity = new HttpEntity<RCAadharSeedModel>(rcAadharSeedModel, headers);
		HttpStatus httpStatus = null;
		ResponseEntity<SaveUpdateResponse> response = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/rc/aadhaarseeding").toString(), HttpMethod.PUT,
					httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> reIterateAppForFinance(String appNo)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<SaveUpdateResponse> response = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/reiterate/finance/app/").append(appNo).toString(),
					HttpMethod.POST, httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<PermitCodeDescModel>> getRouteGoodsConditionsForTempPermit(
			PermitDetailsType detailsType, String primaryPermit, String temporaryPermit) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<PermitCodeDescModel[]> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/temppermit/").append(detailsType).append("/permit/").append(primaryPermit)
							.append("/temp/").append(temporaryPermit).toString(),
					HttpMethod.GET, httpEntity, PermitCodeDescModel[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		List<PermitCodeDescModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegistrationServiceResponseModel<List<PermitCodeDescModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<FcDetailsModel> getFitnessCertificate(Long vehicleRcId, Long mviUserId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<FcDetailsModel> response = null;
		try {
			response = restTemplate
					.exchange(
							getRootURL().append("/certificate/").append(vehicleRcId).append("/FC")
									.append("?approver_mvi=").append(mviUserId).toString(),
							HttpMethod.GET, httpEntity, FcDetailsModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		FcDetailsModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FcDetailsModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SuspendedRCNumberModel> getSuspensionDetails(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<SuspendedRCNumberModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/application/suspensiondetails/vehicle/").append(vehicleRcId).toString(),
					HttpMethod.GET, httpEntity, SuspendedRCNumberModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SuspendedRCNumberModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SuspendedRCNumberModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveFCFXNote(CFXModel cfxModel)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<CFXModel> httpEntity = new HttpEntity<CFXModel>(cfxModel, headers);
		HttpStatus httpStatus = null;
		ResponseEntity<SaveUpdateResponse> response = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/fcfx").toString(), HttpMethod.POST, httpEntity,
					SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> syncPayTaxData(TaxModel taxModel)
			throws UnauthorizedException {
		log.info(":syncPayTaxData::call registration:");
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TaxModel> httpEntity = new HttpEntity<TaxModel>(taxModel, headers);
		HttpStatus httpStatus = null;
		ResponseEntity<SaveUpdateResponse> response = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/sync/paytax").toString(), HttpMethod.POST,
					httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<CFXNoticeModel> getFCFXNote(String prNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<CFXNoticeModel> httpEntity = new HttpEntity<CFXNoticeModel>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<CFXNoticeModel> response = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/fcfx/pr/").append(prNumber).toString(),
					HttpMethod.GET, httpEntity, CFXNoticeModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		CFXNoticeModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<CFXNoticeModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<RegistrationCategoryModel> getRegCategoryByRcId(Long vehicleRcId)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<RegistrationCategoryModel> response = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/vehicle/regcategory/").append(vehicleRcId).toString(), HttpMethod.GET,
					httpEntity, RegistrationCategoryModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		RegistrationCategoryModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<RegistrationCategoryModel>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<Boolean> getIsVehicleReassignmentApplicable(String prNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<Boolean> response = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/isvehiclereassignment/applicable/").append(prNumber).toString(),
					HttpMethod.GET, httpEntity, Boolean.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		Boolean responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Boolean>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<VehicleClassDescModel>> getAlterationCovList(String prNumber,
			String regCatCode) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<VehicleClassDescModel[]> response = null;
		if (!ObjectsUtil.isNull(loginResponse.getResponseBody())) {
			response = restTemplate
					.exchange(
							getRootURL().append("/vehicle/getAlterationCovList/").append(prNumber).append("/")
									.append(regCatCode).toString(),
							HttpMethod.GET, httpEntity, VehicleClassDescModel[].class);
		}
		HttpStatus httpStatus = response.getStatusCode();
		List<VehicleClassDescModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegistrationServiceResponseModel<List<VehicleClassDescModel>>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<TaxTypeModel> getTaxTypeByCov(String cov) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = null;
		try {
			loginResponse = loginIfRequired(getUsername(), getPassword());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TaxTypeModel> httpEntity = new HttpEntity<TaxTypeModel>(headers);
		ResponseEntity<TaxTypeModel> response = restTemplate.exchange(
				getRootURL().append("/taxtypeforvehiclesubclass/").append(cov).toString(), HttpMethod.GET, httpEntity,
				new ParameterizedTypeReference<TaxTypeModel>() {
				});
		HttpStatus httpStatus = response.getStatusCode();
		TaxTypeModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<TaxTypeModel>(httpStatus, responseBody);
	}
	
	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveStoppageTaxDetails(List<ApplicationFormDataModel> models, String prNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<List<ApplicationFormDataModel>> httpEntity = new HttpEntity<List<ApplicationFormDataModel>>(models, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try{
			response = restTemplate.exchange(getRootURL().append("/vehicle/stoppagetax/details/").append(prNumber).toString(), HttpMethod.PUT, httpEntity,
					new ParameterizedTypeReference<SaveUpdateResponse>() {});
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}
	
	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> saveStoppageTaxReportDetails(StoppageTaxReportModel model, String prNumber, String userName) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<StoppageTaxReportModel> httpEntity = new HttpEntity<StoppageTaxReportModel>(model, headers);
		ResponseEntity<SaveUpdateResponse> response = null;
		HttpStatus httpStatus = null;
		try{
			response = restTemplate.exchange(getRootURL().append("/vehicle/stoppagetax/report/details/").append(prNumber).append("?username=").append(userName).toString(), 
					HttpMethod.POST, httpEntity, new ParameterizedTypeReference<SaveUpdateResponse>() {});
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}
	
	@Override
	public RegistrationServiceResponseModel<StoppageTaxDetailsModel> getStoppageTaxDetails(String prNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<StoppageTaxDetailsModel> response = null;
		HttpStatus httpStatus = null;
		try{
			response = restTemplate.exchange(getRootURL().append("/vehicle/stoppagetax/details/").append(prNumber).toString(), HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<StoppageTaxDetailsModel>() {});
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		StoppageTaxDetailsModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<StoppageTaxDetailsModel>(httpStatus, responseBody);
	}
	
	@Override
	public RegistrationServiceResponseModel<List<StoppageTaxReportModel>> getStoppageTaxReportDetails(String applicationNo) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<List<StoppageTaxReportModel>> response = null;
		HttpStatus httpStatus = null;
		try{
			response = restTemplate.exchange(getRootURL().append("/vehicle/stoppagetax/report/details/").append(applicationNo).toString(), 
					HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<StoppageTaxReportModel>>() {});
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		List<StoppageTaxReportModel> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<List<StoppageTaxReportModel>>(httpStatus, responseBody);
	}
	
	@Override
	public RegistrationServiceResponseModel<StoppageTaxReportModel> getStoppageTaxSingleReportDetails(Long stoppageTaxReportId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		ResponseEntity<StoppageTaxReportModel> response = null;
		HttpStatus httpStatus = null;
		try{
			response = restTemplate.exchange(getRootURL().append("/vehicle/stoppagetax/single/report/details/").append(stoppageTaxReportId).toString(), 
					HttpMethod.GET, httpEntity, new ParameterizedTypeReference<StoppageTaxReportModel>() {});
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		StoppageTaxReportModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<StoppageTaxReportModel>(httpStatus, responseBody);
	}

	// get financer fresh Rc
	@Override
	public RegistrationServiceResponseModel<FinancerFreshContactDetailsModel> getFinancerFreshContactDetails(
			Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<FinancerFreshContactDetailsModel> response = null;
		response = restTemplate.exchange(getRootURL().append("/financierfreshdetails/").append(vehicleRcId).toString(),
				HttpMethod.GET, httpEntity, FinancerFreshContactDetailsModel.class);
		HttpStatus httpStatus = response.getStatusCode();
		FinancerFreshContactDetailsModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FinancerFreshContactDetailsModel>(httpStatus, responseBody);
	}

//	@Override
//	public FreshRcAppStatusDetailsModel getCustomerAppStatusDetails(Long vehicleRcId)
//			throws UnauthorizedException {
//		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.add("Authorization", loginResponse.getResponseBody().getToken());
//		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
//
//		ResponseEntity<FreshRcAppStatusDetailsModel> response = restTemplate.exchange(getRootURL().append("/applicationdetails/status/")
//				.append(vehicleRcId).toString(), HttpMethod.GET, httpEntity, FreshRcAppStatusDetailsModel.class);
//		HttpStatus httpStatus = response.getStatusCode();
//		FreshRcAppStatusDetailsModel responseBody =null; 
//		if (httpStatus == HttpStatus.OK) {
//			if (response.hasBody()) {
//				responseBody = response.getBody();
//			}
//		}
//		return responseBody;
//	}

	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> submitOwnerConscent(OwnerConscent ownerConscent,
			String appNumber) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<OwnerConscent> httpEntity = new HttpEntity<OwnerConscent>(ownerConscent, headers);

		ResponseEntity<SaveUpdateResponse> response = restTemplate.exchange(
				getRootURL().append("/application/submitownerconscent/").append(appNumber).toString(), HttpMethod.POST,
				httpEntity, SaveUpdateResponse.class);

		SaveUpdateResponse responseBody = null;
		HttpStatus httpStatus = response.getStatusCode();
		if (httpStatus == HttpStatus.OK) {
			String vehicleRcId = response.getBody().getVehicleRcId();
			ResponseEntity<FinanceModel> financerDetails = restTemplate.exchange(
					getRootURL().append("/financerdetails/").append(vehicleRcId).toString(), HttpMethod.GET, httpEntity,
					FinanceModel.class);
			RegistrationServiceResponseModel<FreshRcModel> freshrc = getFreshRcDataByApplicationNumber(appNumber);
			
			if (!ObjectsUtil.isNull(financerDetails.getBody()) && !ObjectsUtil.isNull(freshrc)) {
				if (!StringsUtil.isNullOrEmpty(financerDetails.getBody().getFinancerOfficialEmailId())) {
					CustMsgModel msgModel = new CustMsgModel();
					msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
					msgModel.setTo(financerDetails.getBody().getFinancerOfficialEmailId());
					msgModel.setSubject("AP_RTD: Vehicle owner consent for Fresh RC for financier");
					msgModel.setMailContent("Dear "+financerDetails.getBody().getApproverName() +" ,<br>"+
							"For your application "+freshrc.getResponseBody().getApplicationNumber()+" of Fresh RC for Financier.<br>"+
							"The vehicle owner has responded as "+(freshrc.getResponseBody().getOwnerConsent() ? "Yes":"No") +".<br>"+
							"<br>Thanks and Regards,<br>"
							+ "A.P. Transport Department"
							);
					communicationService.sendMsg(SEND_EMAIL, msgModel);
				}

				if (!StringsUtil.isNullOrEmpty(financerDetails.getBody().getFinancerContactNumber()) &&  !ObjectsUtil.isNull(freshrc)) {
					CustMsgModel msgModel = new CustMsgModel();
					msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
					msgModel.setMobileNo(financerDetails.getBody().getFinancerContactNumber());
					msgModel.setSmsMsg("Dear "+financerDetails.getBody().getApproverName() +" ,\n"+
							"For your application "+freshrc.getResponseBody().getApplicationNumber()+" of Fresh RC for Financier.\n"+
							"The vehicle owner has responded as "+(freshrc.getResponseBody().getOwnerConsent() ? "Yes":"No") +".\n"+
							"\nThanks and Regards,\n"
							+ "A.P. Transport Department"
							);
					communicationService.sendMsg(SEND_SMS, msgModel);
				}

			}

			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<FreshRcModel> getFreshRcDataByAadharAndVehicleRcId(Long vehicleRcId,
			String aadharNumber) throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<FreshRcModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/freshrcbyaadhar/").append(vehicleRcId)
					.append("?aadharnumber=").append(aadharNumber).toString(), HttpMethod.GET, httpEntity,
					FreshRcModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		FreshRcModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FreshRcModel>(httpStatus, responseBody);
	
	}
	
	
	@Override
	public RegistrationServiceResponseModel<FreshRcModel> getFreshRcDataByApplicationNumber(String applicationNumber) throws UnauthorizedException {

		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<TokenModel> httpEntity = new HttpEntity<TokenModel>(headers);
		ResponseEntity<FreshRcModel> response = null;
		HttpStatus httpStatus = null;
		try {
			response = restTemplate.exchange(getRootURL().append("/freshrcbyappno/").append(applicationNumber).toString(), HttpMethod.GET, httpEntity,
					FreshRcModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		FreshRcModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<FreshRcModel>(httpStatus, responseBody);
	
	}

	@Override
	public RegistrationServiceResponseModel<Boolean> getIsLifeTaxPaid(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		HttpStatus httpStatus = null;
		ResponseEntity<Boolean> response = null;
		try {
			response = restTemplate.exchange(
					getRootURL().append("/getLifeTaxDetails/").append(vehicleRcId).toString(),
					HttpMethod.GET, httpEntity, Boolean.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		Boolean responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<Boolean>(httpStatus, responseBody);
}	
	@Override
	public VcrModel getVCRTax(String docType, String docNumber) throws UnauthorizedException {
		 RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", loginResponse.getResponseBody().getToken());
			HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		 ResponseEntity<VcrModel> response = restTemplate.exchange(
	                getRootURL().append("/vcr/getReport/" + docType + "?docnumber=").append(docNumber).toString(), HttpMethod.GET,
	                httpEntity, new ParameterizedTypeReference<VcrModel>() {});
		 
		 log.info(" :::::::::VCR request fee and tax :::::::::::::");
	        HttpStatus httpStatus = response.getStatusCode();
	        if (httpStatus == HttpStatus.OK) {
	            if (response.hasBody()) {
	                return response.getBody();
	            }            
	        }
	        return null;
	    }
	
	
	@Override
	public RegistrationServiceResponseModel<SaveUpdateResponse> reIterateAppForFreshRc(OwnerConscent ownerConscent,
			String appNumber)
			throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		HttpEntity<OwnerConscent> httpEntity = new HttpEntity<OwnerConscent>(ownerConscent, headers);
		HttpStatus httpStatus = null;  
		ResponseEntity<SaveUpdateResponse> response = null;
		try {
		response = restTemplate.exchange(
				getRootURL().append("/application/submitownerconscent/").append(appNumber).toString(), HttpMethod.POST,
				httpEntity, SaveUpdateResponse.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		SaveUpdateResponse responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<SaveUpdateResponse>(httpStatus, responseBody);
	}

	@Override
	public RegistrationServiceResponseModel<List<String>> getBodyTypeList() throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		HttpStatus httpStatus = null;  
		ResponseEntity<String[]> response = null; 
		try {
			response = restTemplate.exchange(getRootURL().append("/vehicle").append("/bodytypelist").toString(), HttpMethod.GET,
					httpEntity, String[].class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		List<String> responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = Arrays.asList(response.getBody());
			}
		}
		return new RegistrationServiceResponseModel<List<String>>(httpStatus, responseBody);
	}
	
	@Override
	public RegistrationServiceResponseModel<UserModel> getUserDetails(Long userId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TokenModel> loginResponse = loginIfRequired(getUsername(), getPassword());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", loginResponse.getResponseBody().getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginModel> httpEntity = new HttpEntity<LoginModel>(headers);
		HttpStatus httpStatus = null;  
		ResponseEntity<UserModel> response = null; 
		try {
			response = restTemplate.exchange(getRootURL().append("/user/details/").append(userId).toString(), HttpMethod.GET,
					httpEntity, UserModel.class);
			httpStatus = response.getStatusCode();
		} catch (HttpStatusCodeException ex) {
			httpStatus = ex.getStatusCode();
		}
		UserModel responseBody = null;
		if (httpStatus == HttpStatus.OK) {
			if (response.hasBody()) {
				responseBody = response.getBody();
			}
		}
		return new RegistrationServiceResponseModel<UserModel>(httpStatus, responseBody);
	}
}

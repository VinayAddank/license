package org.rta.citizen.common.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.aadharseeding.rc.model.RCAadharSeedModel;
import org.rta.citizen.aadharseeding.rc.service.RCASService;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.converters.AddressConverter;
import org.rta.citizen.common.dao.AddressDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.AddressEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.AddressType;
import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.PermitClassType;
import org.rta.citizen.common.enums.PermitOptionType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TheftIntSusType;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.InvalidDataExcpetion;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.AddressModel;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.LoginModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.communication.CustMsgModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.communication.CommunicationService;
import org.rta.citizen.common.service.communication.CommunicationServiceImpl;
import org.rta.citizen.common.service.payment.PaymentService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.fitness.cfx.model.CFXModel;
import org.rta.citizen.hpt.model.FinanceOtherServiceModel;
import org.rta.citizen.licence.dao.LicenceEndorsCOVDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.model.updated.DLMilataryDetailsModel;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.SupensionCancellationModel;
import org.rta.citizen.licence.service.updated.LicenseSyncingService;
import org.rta.citizen.permit.model.PermitNewRequestModel;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.rta.citizen.userregistration.dao.PendingUsernameDAO;
import org.rta.citizen.userregistration.entity.PendingUsernameEntity;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author arun.verma
 *
 */
@Service
public class ApplicationFormDataServiceImpl implements ApplicationFormDataService {

	private static final Logger logger = Logger.getLogger(ApplicationFormDataServiceImpl.class);

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private AddressDAO addressDAO;

	@Autowired
	private AddressConverter addressConverter;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private PendingUsernameDAO pendingUsernameDAO;

	@Autowired
	private RCASService rcasServvice;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private CommunicationService communicationService;

	@Autowired
	private LicensePermitDetailsDAO licencePermitDetailsDAO;

	@Autowired
	private LicenceEndorsCOVDAO licenceEndorsCOVDAO;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private LicenseSyncingService licenseSyncingService;

	@Override
	@Transactional
	public ResponseModel<ApplicationFormDataModel> getApplicationFormDataBySessionId(Long sessionId, String formCode)
			throws JsonProcessingException, IOException, UnauthorizedException {
		ResponseModel<ApplicationFormDataModel> response = new ResponseModel<>();
		UserSessionEntity sessionEntity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		if (ObjectsUtil.isNull(appEntity)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Application Not Exist !!!");
			return response;
		}
		ServiceType serviceType = ServiceType.getServiceType(sessionEntity.getServiceCode());
		ObjectMapper mapper = new ObjectMapper();
		ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
				formCode);
		if (!ObjectsUtil.isNull(entity)) {
			ApplicationFormDataModel form = new ApplicationFormDataModel();
			form.setFormCode(entity.getFormCode());
			form.setFormData(entity.getFormData());
			response.setData(form);
		} else {
			if (serviceType == ServiceType.HPT) {
				RegistrationServiceResponseModel<FinanceOtherServiceModel> res = getHptDataFromRegistration(
						appEntity.getApplicationNumber(), sessionEntity.getKeyType().toString(),
						sessionEntity.getUniqueKey());
				if (!res.getHttpStatus().equals(HttpStatus.OK)) {
					response.setStatusCode(res.getHttpStatus().value());
					response.setStatus(ResponseModel.FAILED);
					if (res.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
						response.setMessage("Invalid PR Number !!!");
					} else if (res.getHttpStatus().equals(HttpStatus.FORBIDDEN)) {
						response.setMessage("Vehicle is not Financed !!!");
					} else if (res.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)) {
						response.setMessage("Financer not found for given PR Number!!!");
					}
					return response;
				}
				ApplicationFormDataModel form = new ApplicationFormDataModel();
				/*
				 * HPTFormModel hptForm = new HPTFormModel();
				 * hptForm.setFinancierName("ABC finanicer");
				 * hptForm.setFinancierId("122");
				 * hptForm.setAgreementDate(1481811140L);
				 */
				form.setFormData(mapper.writeValueAsString(res.getResponseBody()));
				response.setData(form);
			}
		}
		response.setStatus(ResponseModel.SUCCESS);
		return response;
	}

	@Override
	@Transactional
	public ResponseModel<Map<String, Object>> getAllForms(String appNo)
			throws JsonProcessingException, IOException, NumberFormatException, UnauthorizedException {
		ResponseModel<Map<String, Object>> res = new ResponseModel<>(ResponseModel.SUCCESS);
		List<ApplicationFormDataModel> forms = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		if (ObjectsUtil.isNull(appEntity)) {
			return res;
		}
		List<ApplicationFormDataEntity> formsEntity = applicationFormDataDAO
				.getAllApplicationFormData(appEntity.getApplicationId());
		ServiceType serviceType = ServiceType.getServiceType(appEntity.getServiceCode());
		switch (serviceType) {

		case DEFAULT:
			break;
		case DL_BADGE:
		case DL_CHANGEADDRS_OS:
		case DL_CHANGE_ADDRESS:
		case DL_DLINFO:
			break;
		case DL_DUPLICATE:
			break;
		case DL_ENDORSMENT:
			break;
		case DL_EXPIRED:
			break;
		case DL_FOREIGN_CITIZEN:
			break;
		case DL_INT_PERMIT:
			break;
		case DL_MILITRY:
			break;
		case DL_RENEWAL:
			break;
		case DL_RETEST:
			break;
		case DL_SURRENDER:
			break;
		case DL_REVO_SUS:
			break;
		case DL_FRESH:
		case DL_SUSU_CANC:
		case LL_FRESH:
		case LL_ENDORSEMENT:
		case LL_DUPLICATE:
		case LL_RETEST: {
			for (ApplicationFormDataEntity ent : formsEntity) {
				ApplicationFormDataModel form = new ApplicationFormDataModel();
				form.setFormCode(ent.getFormCode());
				form.setFormData(ent.getFormData());
				forms.add(form);
			}
		}
			break;
		case DIFFERENTIAL_TAX:
		case ADDRESS_CHANGE:
		case ALTERATION_AGENCY_SIGNUP:
		case BODYBUILDER_SIGNUP:
		case DEALER_SIGNUP:
		case DUPLICATE_REGISTRATION:
		case FINANCIER_SIGNUP:
		case FRESH_RC_FINANCIER:
		case HPA:
		case HPT:
		case NOC_CANCELLATION:
		case NOC_ISSUE:
		case OWNERSHIP_TRANSFER_AUCTION:
		case OWNERSHIP_TRANSFER_DEATH:
		case OWNERSHIP_TRANSFER_SALE:
		case PUC_USER_SIGNUP:
		case REGISTRATION_CANCELLATION:
		case REGISTRATION_RENEWAL:
		case REGISTRATION_SUS_CANCELLATION:
		case SUSPENSION_REVOCATION:
		case THEFT_INTIMATION:
		case VEHICLE_ATLERATION:
		case VEHICLE_REASSIGNMENT: {
			for (ApplicationFormDataEntity ent : formsEntity) {
				ApplicationFormDataModel form = new ApplicationFormDataModel();
				form.setFormCode(ent.getFormCode());
				form.setFormData(ent.getFormData());
				forms.add(form);
			}
			RegistrationServiceResponseModel<AadharModel> adharRes = registrationService
					.getAadharDetails(Long.parseLong(appEntity.getLoginHistory().getAadharNumber()));
			if (adharRes.getHttpStatus().equals(HttpStatus.OK)) {
				map.put("aadhar", adharRes.getResponseBody());
			} else {
				logger.error("Error while calling Aadhar details data. Status code : " + adharRes.getHttpStatus());
			}
		}
			break;
		default:
			break;
		}
		map.put("forms", forms);
		map.put("serviceCode", appEntity.getServiceCode());
		res.setData(map);
		return res;
	}

	@Override
	public ResponseModel<ApplicationFormDataModel> getApplicationFormData(Long applicationId, String formId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationFormDataModel> getApplicationFormData(String applicationNumber, String formCode)
			throws JsonProcessingException, IOException, UnauthorizedException {
		ResponseModel<ApplicationFormDataModel> response = new ResponseModel<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
		if (ObjectsUtil.isNull(appEntity)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Application Not Exist !!!");
			return response;
		}

		UserSessionEntity sessionEntity = appEntity.getLoginHistory();
		// TO Sync suspension data
		// licenseSyncingService.suspendCancelLicenseByAO(appEntity.getApplicationId(),
		// sessionEntity.getAadharNumber());

		ServiceType serviceType = ServiceType.getServiceType(sessionEntity.getServiceCode());
		ObjectMapper mapper = new ObjectMapper();
		ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
				formCode);
		if (!ObjectsUtil.isNull(entity)) {
			ApplicationFormDataModel form = new ApplicationFormDataModel();
			form.setFormCode(entity.getFormCode());
			form.setFormData(entity.getFormData());
			response.setData(form);
		} else {
			if (serviceType == ServiceType.HPT) {
				RegistrationServiceResponseModel<FinanceOtherServiceModel> res = getHptDataFromRegistration(
						appEntity.getApplicationNumber(), sessionEntity.getKeyType().toString(),
						sessionEntity.getUniqueKey());
				if (!res.getHttpStatus().equals(HttpStatus.OK)) {
					response.setStatusCode(res.getHttpStatus().value());
					response.setStatus(ResponseModel.FAILED);
					if (res.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
						response.setMessage("Invalid PR Number !!!");
					} else if (res.getHttpStatus().equals(HttpStatus.FORBIDDEN)) {
						response.setMessage("Vehicle is not Financed !!!");
					} else if (res.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)) {
						response.setMessage("Financer not found for given PR Number!!!");
					}
					return response;
				}
				ApplicationFormDataModel form = new ApplicationFormDataModel();
				form.setFormData(mapper.writeValueAsString(res.getResponseBody()));
				response.setData(form);
			}
		}
		response.setStatus(ResponseModel.SUCCESS);
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ResponseModel<ApplicationFormDataModel> saveForm(List<ApplicationFormDataModel> forms, Long sessionId,
			Long userId) throws UnauthorizedException, JsonParseException, JsonMappingException, IOException,
			DataMismatchException, NotFoundException, ConflictException, ServiceValidationException {
		ResponseModel<ApplicationFormDataModel> response = new ResponseModel<>();
		UserSessionEntity sessionEntity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
		if (ObjectsUtil.isNull(sessionEntity)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Application Not Exist !!!");
			return response;
		}
		ServiceType serviceType = ServiceType.getServiceType(sessionEntity.getServiceCode());
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		if (ObjectsUtil.isNull(appEntity)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Invalid Application !!!");
			return response;
		}
		Long time = DateUtil.toCurrentUTCTimeStamp();
		// ---- service specific tasks ---------------
		for (ApplicationFormDataModel form : forms) {
			ObjectMapper mapper = new ObjectMapper();
			if (serviceType == ServiceType.HPA) {
				JsonNode jsonData = mapper.readTree(form.getFormData());
				Long quoteAmount = jsonData.get("quoteAmount").asLong();
				if (quoteAmount <= 0.0) {
					throw new NotFoundException("Quote amount not found !!!");
				}
			} else if (serviceType == ServiceType.VEHICLE_ATLERATION) {
				VehicleBodyModel vehicleBodyModel = mapper.readValue(form.getFormData(), VehicleBodyModel.class);
				List<AlterationCategory> alterationCategorieList = vehicleBodyModel.getAlterationCategory();
				logger.info("Save Form VehicleAlteration.......");
				RegistrationServiceResponseModel<VehicleDetailsRequestModel> vehicleDetailResponse = registrationService
						.getVehicleDetails(sessionEntity.getVehicleRcId());
				if (vehicleDetailResponse.getHttpStatus() != HttpStatus.OK) {
					String msg = "Some Error Occured while saving form data for regCatCode in alteration of vehicle.";
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.CHECKPOINT.value());
					response.setMessage(msg);
					return response;
				}
				VehicleDetailsRequestModel vehicleDetailsModel = vehicleDetailResponse.getResponseBody();
				for (AlterationCategory alterationCategory : alterationCategorieList) {
					logger.info("Type : " + alterationCategory.getLabel());
					if (alterationCategory.getLabel().equalsIgnoreCase(AlterationCategory.VEHICLE_TYPE.getLabel())) {
						// --- save old reg category in form data -----------
						vehicleBodyModel
								.setOldRegistrationCategoryCode(vehicleDetailsModel.getRegCategoryDetails().getCode());
						String jsonInString = mapper.writeValueAsString(vehicleBodyModel);
						form.setFormData(jsonInString);
						String regCatCode = null;
						regCatCode = vehicleBodyModel.getRegistrationCategoryCode();
						if (regCatCode.equals("") || regCatCode == null) {
							String msg = "Registration Category Not Found !!!.";
							response.setStatus(ResponseModel.FAILED);
							response.setStatusCode(HttpStatus.CHECKPOINT.value());
							response.setMessage(msg);
							return response;
						}
					}
				}

				boolean isValidSeatingCapacityByCov = isCovAlterationValidation(vehicleBodyModel, vehicleDetailsModel);
				logger.info("validate seating with cov:" + isValidSeatingCapacityByCov);
				if (!isValidSeatingCapacityByCov) {
					String msg = "Seating capacity with this class of vehicle can not be altered.";
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.CHECKPOINT.value());
					response.setMessage(msg);
					return response;
				}

			} else if (serviceType == ServiceType.THEFT_INTIMATION) {
				try {
					validateTheftService(mapper.readValue(form.getFormData(), TheftIntimationRevocationModel.class),
							appEntity);
				} catch (DataMismatchException ex) {
					logger.error("Error : " + ex.getMessage());
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.CHECKPOINT.value());
					response.setMessage(ex.getMessage());
					return response;
				}
			} else if (serviceType == ServiceType.HPT) {
				mapper.readValue(form.getFormData(), FinanceOtherServiceModel.class);
			} else if ((serviceType == ServiceType.OWNERSHIP_TRANSFER_SALE
					&& form.getFormCode().equalsIgnoreCase(FormCodeType.OTS_FORM.getLabel()))
					|| (serviceType == ServiceType.OWNERSHIP_TRANSFER_AUCTION
							&& form.getFormCode().equalsIgnoreCase(FormCodeType.OTA_FORM.getLabel()))
					|| (serviceType == ServiceType.OWNERSHIP_TRANSFER_DEATH
							&& form.getFormCode().equalsIgnoreCase(FormCodeType.OTD_FORM.getLabel()))) {

				AddressChangeModel addressChangeModel = mapper.readValue(form.getFormData(), AddressChangeModel.class);
				updateRtaOfficeForOT(addressChangeModel, appEntity);

			} else if (serviceType == ServiceType.PERMIT_VARIATIONS) {
				// just validate the input data by mapper
				mapper.readValue(form.getFormData(), PermitNewRequestModel.class);
			} else if (serviceType == ServiceType.PERMIT_FRESH) {
				PermitNewRequestModel reqMdl = mapper.readValue(form.getFormData(), PermitNewRequestModel.class);
				try {
					validatePermitNew(reqMdl, appEntity);
				} catch (InvalidDataExcpetion ex) {
					logger.info("Validation Failed to submit new permit data  !!!" + ex.getMessage());
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
					response.setMessage("Invalid data. " + ex.getMessage());
					return response;
				} catch (ServiceValidationException ex) {
					logger.info("Validation Failed to submit new permit data  !!!" + ex.getMessage());
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(ex.getErrorCode());
					response.setMessage(ex.getErrorMsg());
					return response;
				}
			} else if (serviceType == ServiceType.ADDRESS_CHANGE) {
				saveOrUpdateCurrentAddress(appEntity, mapper.readValue(form.getFormData(), AddressChangeModel.class),
						sessionEntity.getAadharNumber());
			} else if (serviceType == ServiceType.LL_FRESH
					&& FormCodeType.LLF_DETAIL_FORM.getLabel().equalsIgnoreCase(form.getFormCode())) {
				LLRegistrationModel model = mapper.readValue(form.getFormData(), LLRegistrationModel.class);
				Map<String, Object> map = licenceHolderDetailsValidation(model, appEntity.getApplicationNumber(),
						response);
				response = (ResponseModel<ApplicationFormDataModel>) map.get("response");
				if (!ResponseModel.SUCCESS.equalsIgnoreCase(response.getStatus())) {
					return response;
				} else {
					String rtaOffice = (String) map.get("rtaOfficeCode");
					appEntity.setRtaOfficeCode(rtaOffice);
				}
				saveOrUpdateCurrentAddress(appEntity, model, sessionEntity.getAadharNumber());

			} else if (serviceType == ServiceType.DL_CHANGE_ADDRESS) {
				saveOrUpdateCurrentAddress(appEntity, mapper.readValue(form.getFormData(), LLRegistrationModel.class),
						sessionEntity.getAadharNumber());
			} else if (serviceType == ServiceType.DL_ENDORSMENT
					&& FormCodeType.DLE_FORM.getLabel().equalsIgnoreCase(form.getFormCode())) {
				ResponseModel<String> responseModel = licenseSyncingService.updateInLicenseHolderDetails(sessionId,
						mapper.readValue(form.getFormData(), LLRegistrationModel.class));
				logger.info("mobile number and emailId updated: " + responseModel.getStatus() + " for "
						+ appEntity.getApplicationNumber());
			} else if (serviceType == ServiceType.DL_FRESH
					&& FormCodeType.DLF_FORM.getLabel().equalsIgnoreCase(form.getFormCode())) {
				ResponseModel<String> responseModel = licenseSyncingService.updateInLicenseHolderDetails(sessionId,
						mapper.readValue(form.getFormData(), LLRegistrationModel.class));
				logger.info("mobile number and emailId updated: " + responseModel.getStatus() + " for "
						+ appEntity.getApplicationNumber());
			} else if (serviceType == ServiceType.DL_RETEST
					&& FormCodeType.DLRE_FORM.getLabel().equalsIgnoreCase(form.getFormCode())) {
				ResponseModel<String> responseModel = licenseSyncingService.updateInLicenseHolderDetails(sessionId,
						mapper.readValue(form.getFormData(), LLRegistrationModel.class));
				logger.info("mobile number and emailId updated: " + responseModel.getStatus() + " for "
						+ appEntity.getApplicationNumber());
			} else if (serviceType == ServiceType.DL_MILITRY
					&& FormCodeType.DLM_DETAIL_FORM.getLabel().equalsIgnoreCase(form.getFormCode())) {
				saveOrUpdateCurrentAddress(appEntity,
						mapper.readValue(form.getFormData(), DLMilataryDetailsModel.class),
						sessionEntity.getAadharNumber());
			} else if (serviceType == ServiceType.DL_SUSU_CANC) {
				ResponseModel<String> responseModel = licenseSyncingService.suspendCancelLicenseByAO(
						mapper.readValue(form.getFormData(), SupensionCancellationModel.class), sessionEntity);
				logger.info("Susp and Canc Sync Updated : " + responseModel.getStatus() + " for "
						+ appEntity.getApplicationNumber());
			} else if (serviceType == ServiceType.FINANCIER_SIGNUP || serviceType == ServiceType.BODYBUILDER_SIGNUP
					|| serviceType == ServiceType.DEALER_SIGNUP || serviceType == ServiceType.PUC_USER_SIGNUP
					|| serviceType == ServiceType.ALTERATION_AGENCY_SIGNUP
					|| serviceType == ServiceType.HAZARDOUS_VEH_TRAIN_INST
					|| serviceType == ServiceType.DRIVING_INSTITUTE
					|| serviceType == ServiceType.MEDICAL_PRACTITIONER) {
				UserSignupModel userModel = mapper.readValue(form.getFormData(), UserSignupModel.class);
				LoginModel loginDetails = userModel.getLoginDetails();
				if (loginDetails == null) {
					logger.error("invalid login details for application number : " + appEntity.getApplicationNumber());
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());
					response.setMessage("invalid login details !!!");
					return response;
				}
				String username = loginDetails.getUsername();
				if (ObjectsUtil.isNull(username)) {
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());
					response.setMessage("invalid username !!!");
					return response;
				}
				logger.info("username is = " + username);
				username = username.toUpperCase();
				loginDetails.setUsername(username);
				form.setFormData(mapper.writeValueAsString(userModel));
				logger.info("username to upper case is = " + username);
				ObjectReader reader = mapper.reader();
				JsonNode jsonNode = reader.readTree(form.getFormData());
				ObjectNode loginDetailsObjectNode = (ObjectNode) jsonNode.get("loginDetails");
				loginDetailsObjectNode.put("username", username.toUpperCase());
				logger.info("objectnode is = " + loginDetailsObjectNode.toString());
				PendingUsernameEntity pendingUsernameEntity = pendingUsernameDAO.getByUsernameAndStatus(username,
						Status.PENDING);
				if (!ObjectsUtil.isNull(pendingUsernameEntity)
						&& pendingUsernameEntity.getApplication().getApplicationId() != appEntity.getApplicationId()) {
					logger.debug("username found in pending username table : " + username);
					throw new ConflictException("user already exists!!");
				}

				pendingUsernameEntity = pendingUsernameDAO.getByApplication(appEntity.getApplicationId(),
						Status.PENDING);

				// username validation, already exists or not
				RegistrationServiceResponseModel<UserModel> res = registrationService.getUser(username);
				if (res.getHttpStatus() == HttpStatus.OK) {
					logger.debug("username found in registration users table : " + username);
					throw new ConflictException("user already exists!!");
				}

				long currentTime = DateUtil.toCurrentUTCTimeStamp();
				if (!ObjectsUtil.isNull(pendingUsernameEntity)) {
					pendingUsernameEntity.setModifiedBy(sessionEntity.getAadharNumber());
					pendingUsernameEntity.setModifiedOn(currentTime);
				} else {
					pendingUsernameEntity = new PendingUsernameEntity();
					pendingUsernameEntity.setStatus(Status.PENDING.getValue());
					pendingUsernameEntity.setApplication(appEntity);
					pendingUsernameEntity.setCreatedBy(sessionEntity.getAadharNumber());
					pendingUsernameEntity.setCreatedOn(currentTime);
					pendingUsernameEntity.setModifiedBy(sessionEntity.getAadharNumber());
					pendingUsernameEntity.setModifiedOn(currentTime);
				}
				pendingUsernameEntity.setUsername(username);
				pendingUsernameDAO.saveOrUpdate(pendingUsernameEntity);
				// assign RTA office to application
				CustomerDetailsRequestModel customerDetailsNode = userModel.getCustomerDetails();
				String stateCode = customerDetailsNode.getTemp_statecode();
				if (stateCode == null) {
					logger.error("invalid state code" + stateCode + " for application number : "
							+ appEntity.getApplicationNumber());
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());
					response.setMessage("invalid mandalcode !!!");
					return response;
				}
				if (stateCode.equalsIgnoreCase("AP") || stateCode.equalsIgnoreCase("Andhra Pradesh")) {
					Integer mandalCode;
					try {
						String tempMandalCodeNode = customerDetailsNode.getTemp_mandal_code();
						mandalCode = Integer.parseInt(tempMandalCodeNode);
					} catch (NumberFormatException e) {
						mandalCode = null;
					}
					if (ObjectsUtil.isNull(mandalCode)) {
						logger.error("invalid mandal code" + mandalCode + " for application number : "
								+ appEntity.getApplicationNumber());
						response.setStatus(ResponseModel.FAILED);
						response.setStatusCode(HttpStatus.BAD_REQUEST.value());
						response.setMessage("invalid mandalcode !!!");
						return response;
					}
					try {
						RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponse = registrationService
								.getRTAOfficeByMandal(mandalCode, appEntity.getLoginHistory().getVehicleRcId());
						if (rtaOfficeResponse.getHttpStatus() == HttpStatus.OK) {
							RTAOfficeModel rtaOffice = rtaOfficeResponse.getResponseBody();
							appEntity.setRtaOfficeCode(rtaOffice.getCode());
						} else {
							logger.info(
									"can't find rta office for applicationNumber : " + appEntity.getApplicationNumber()
											+ ", http status code : " + rtaOfficeResponse.getHttpStatus());
							response.setStatus(ResponseModel.FAILED);
							response.setStatusCode(HttpStatus.BAD_REQUEST.value());
							response.setMessage("can't find rta office !!!");
							return response;
						}
					} catch (HttpStatusCodeException e) {
						logger.error("can't find rta office for applicationNumber : " + appEntity.getApplicationNumber()
								+ ", http status code : " + e.getStatusCode());
						response.setStatus(ResponseModel.FAILED);
						response.setStatusCode(HttpStatus.BAD_REQUEST.value());
						response.setMessage("can't find rta office !!!");
						return response;
					}
				} else {
					String rtaOfficeCodeNode = userModel.getRtaOfficeCode();
					if (rtaOfficeCodeNode == null) {
						logger.error("rta office code is not present for application number : "
								+ appEntity.getApplicationNumber());
						response.setStatus(ResponseModel.FAILED);
						response.setStatusCode(HttpStatus.BAD_REQUEST.value());
						response.setMessage("invalid mandalcode !!!");
						return response;
					}
					appEntity.setRtaOfficeCode(rtaOfficeCodeNode);
				}
			} else if (serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION) {
				CommonServiceModel csm = mapper.readValue(form.getFormData(), CommonServiceModel.class);
				if (csm == null || ObjectsUtil.isNull(csm.getStartTime())) {
					logger.error("startTime for RSC service" + csm.getStartTime());
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());
					response.setMessage("invalid startTime !!!");
					return response;
				}
				try {
					validateRSC(csm, appEntity);
				} catch (ServiceValidationException e) {
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(e.getErrorCode());
					response.setMessage(e.getErrorMsg());
					return response;
				}
			} else if (serviceType == ServiceType.AADHAR_SEED_RC) {
				mapper.readValue(form.getFormData(), RCAadharSeedModel.class);
			}
			logger.info("saving form data : " + form.getFormData());
			ApplicationFormDataEntity entity = applicationFormDataDAO
					.getApplicationFormData(appEntity.getApplicationId(), form.getFormCode());
			if (ObjectsUtil.isNull(entity)) {
				entity = new ApplicationFormDataEntity();
				entity.setApplicationEntity(appEntity);
				entity.setFormData(form.getFormData());
				entity.setFormCode(form.getFormCode());

				entity.setCreatedBy(sessionEntity.getAadharNumber());
				entity.setCreatedOn(time);
				response.setMessage("Saved Successfully.");
			} else {
				entity.setFormData(form.getFormData());
				entity.setFormCode(form.getFormCode());

				entity.setModifiedBy(sessionEntity.getAadharNumber());
				entity.setModifiedOn(time);
				response.setMessage("Updated Successfully.");
			}

			applicationFormDataDAO.saveOrUpdate(entity);

			if (serviceType == ServiceType.FC_ISSUE_CFX) {
				String applicantName = null;
				String applicantMobileNumber = null;
				CFXModel cfxModel = mapper.readValue(form.getFormData(), CFXModel.class);
				cfxModel.setApplicationNumber(appEntity.getApplicationNumber());
				cfxModel.setTimeOfChecking(DateUtil.toCurrentUTCTimeStamp());
				if (null != userId) {
					cfxModel.setUserId(userId);
				}
				registrationService.saveFCFXNote(cfxModel);
				// send mail to applicant
				RegistrationServiceResponseModel<CustomerDetailsRequestModel> custModel = registrationService
						.getCustomerDetails(appEntity.getLoginHistory().getVehicleRcId());
				if (!ObjectsUtil.isNull(custModel) && custModel.getHttpStatus() == HttpStatus.OK) {
					CustomerDetailsRequestModel cdrm = custModel.getResponseBody();
					applicantName = cdrm.getFirst_name();
					applicantMobileNumber = cdrm.getMobileNumber();
					String emailId = cdrm.getEmailid();
					if (!StringsUtil.isNullOrEmpty(emailId)) {
						CustMsgModel msgModel = new CustMsgModel();
						msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
						msgModel.setTo(cdrm.getEmailid());
						msgModel.setSubject(
								"CFX is issued for vehicle RC : " + appEntity.getLoginHistory().getUniqueKey());
						msgModel.setMailContent("Dear " + applicantName + ", CFX note has been issued for vehicle RC : "
								+ appEntity.getLoginHistory().getUniqueKey());
						communicationService.sendMsg(CommunicationServiceImpl.SEND_EMAIL, msgModel);
					}

					if (!StringsUtil.isNullOrEmpty(applicantMobileNumber)) {
						CustMsgModel msgModel = new CustMsgModel();
						msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
						msgModel.setMobileNo(applicantMobileNumber);
						msgModel.setSmsMsg("Dear " + applicantName + ", CFX note has been issued for vehicle RC : "
								+ appEntity.getLoginHistory().getUniqueKey());
						communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS, msgModel);
					}
				}

				// send mail to financer
				RegistrationServiceResponseModel<HashMap<String, Boolean>> onlineFinanceResponse = registrationService
						.isOnlineFinanced(sessionEntity.getUniqueKey());
				Boolean isOnlineFinanced = false;
				if (onlineFinanceResponse.getHttpStatus() == HttpStatus.OK) {
					HashMap<String, Boolean> map = onlineFinanceResponse.getResponseBody();
					isOnlineFinanced = map.get("isOnlineFinanced");
				}
				if (isOnlineFinanced) {
					RegistrationServiceResponseModel<FinanceModel> financeDetailsResponse = registrationService
							.getFinancierDetails(sessionEntity.getVehicleRcId());
					if (!ObjectsUtil.isNull(financeDetailsResponse)) {
						if (financeDetailsResponse.getHttpStatus() == HttpStatus.OK) {
							try {
								FinanceModel financeModel = financeDetailsResponse.getResponseBody();
								String email = financeModel.getFinancerOfficialEmailId();
								if (!StringsUtil.isNullOrEmpty(email)) {
									CustMsgModel msgModel = new CustMsgModel();
									msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
									msgModel.setTo(email);
									msgModel.setSubject("CFX has been issued for vehicle RC : "
											+ appEntity.getLoginHistory().getUniqueKey());
									msgModel.setMailContent("CFX has been issued for vehicle RC : "
											+ appEntity.getLoginHistory().getUniqueKey());
									communicationService.sendMsg(CommunicationServiceImpl.SEND_EMAIL, msgModel);
								}
								String contactNumber = financeModel.getFinancerContactNumber();
								if (!StringsUtil.isNullOrEmpty(contactNumber)) {
									CustMsgModel msgModel = new CustMsgModel();
									msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
									msgModel.setMobileNo(contactNumber);
									msgModel.setSmsMsg("CFX has been issue for vehicle RC : "
											+ appEntity.getLoginHistory().getUniqueKey());
									communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS, msgModel);
								}
							} catch (HttpClientErrorException e) {
								logger.error("Error while sending notification for application number : "
										+ appEntity.getApplicationNumber());
							}
						}
					}
				}
			}

			if (serviceType == ServiceType.THEFT_INTIMATION) {
				try {
					String applicantName = null;
					String applicantMobileNumber = null;
					TheftIntimationRevocationModel theftModel = mapper.readValue(form.getFormData(),
							TheftIntimationRevocationModel.class);
					TheftIntSusType status = theftModel.getTheftStatus();

					// send mail to applicant
					RegistrationServiceResponseModel<CustomerDetailsRequestModel> custModel = registrationService
							.getCustomerDetails(appEntity.getLoginHistory().getVehicleRcId());
					if (!ObjectsUtil.isNull(custModel) && custModel.getHttpStatus() == HttpStatus.OK) {
						CustomerDetailsRequestModel cdrm = custModel.getResponseBody();
						applicantName = cdrm.getFirst_name();
						applicantMobileNumber = cdrm.getMobileNumber();
						String emailId = cdrm.getEmailid();
						if (!StringsUtil.isNullOrEmpty(emailId)) {
							CustMsgModel msgModel = new CustMsgModel();
							msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
							msgModel.setTo(cdrm.getEmailid());
							msgModel.setSubject(theftModel.getTheftStatus() == TheftIntSusType.FRESH
									? "You have applied for Theft Intimation"
									: "You have applied for Theft Revocation");
							msgModel.setMailContent(getTheftAppliedApplicantMailContent(applicantName, appEntity,
									theftModel.getTheftStatus()));
							communicationService.sendMsg(CommunicationServiceImpl.SEND_EMAIL, msgModel);
						}

						if (!StringsUtil.isNullOrEmpty(applicantMobileNumber)) {
							CustMsgModel msgModel = new CustMsgModel();
							msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
							msgModel.setMobileNo(applicantMobileNumber);
							msgModel.setSmsMsg(getTheftAppliedApplicantMessageContent(applicantName, appEntity,
									theftModel.getTheftStatus()));
							communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS, msgModel);
						}
					}

					// send mail to financer
					RegistrationServiceResponseModel<HashMap<String, Boolean>> onlineFinanceResponse = registrationService
							.isOnlineFinanced(sessionEntity.getUniqueKey());
					Boolean isOnlineFinanced = false;
					if (onlineFinanceResponse.getHttpStatus() == HttpStatus.OK) {
						HashMap<String, Boolean> map = onlineFinanceResponse.getResponseBody();
						isOnlineFinanced = map.get("isOnlineFinanced");
					}
					if (isOnlineFinanced) {
						RegistrationServiceResponseModel<FinanceModel> financeDetailsResponse = registrationService
								.getFinancierDetails(sessionEntity.getVehicleRcId());
						if (!ObjectsUtil.isNull(financeDetailsResponse)) {
							if (financeDetailsResponse.getHttpStatus() == HttpStatus.OK) {
								try {
									String subject = (status == TheftIntSusType.FRESH) ? "Theft Intimation is Done"
											: "Theft Revocation is Done";
									FinanceModel financeModel = financeDetailsResponse.getResponseBody();
									String email = financeModel.getFinancerOfficialEmailId();
									if (!StringsUtil.isNullOrEmpty(email)) {
										CustMsgModel msgModel = new CustMsgModel();
										msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
										msgModel.setTo(email);
										msgModel.setSubject(subject);
										msgModel.setMailContent(getTheftAppliedFinancerMailContent(applicantName,
												financeModel.getName(), appEntity, theftModel.getTheftStatus()));
										communicationService.sendMsg(CommunicationServiceImpl.SEND_EMAIL, msgModel);
									}
									String contactNumber = financeModel.getFinancerContactNumber();
									if (!StringsUtil.isNullOrEmpty(contactNumber)) {
										CustMsgModel msgModel = new CustMsgModel();
										msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
										msgModel.setMobileNo(contactNumber);
										msgModel.setSmsMsg(getTheftAppliedFinancerMessageContent(applicantName,
												financeModel.getName(), appEntity, theftModel.getTheftStatus()));
										communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS, msgModel);
									}
								} catch (HttpClientErrorException e) {
									logger.error("Error while sending notification for application number : "
											+ appEntity.getApplicationNumber());
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("Error while sending notification for application number : "
							+ appEntity.getApplicationNumber(), e);
				}
			}
		}
		// --------------------------------------------
		response.setStatus(ResponseModel.SUCCESS);
		return response;
	}

	private String getTheftAppliedApplicantMailContent(String citizenName, ApplicationEntity appEntity,
			TheftIntSusType status) {
		StringBuilder mailContent = new StringBuilder();
		switch (status) {
		case FRESH:
			mailContent.append("<table><tr><td>Dear ").append(citizenName).append(",</td></tr> ")
					.append("<tr></tr><tr><td>Your application with application number ")
					.append(appEntity.getApplicationNumber()).append(" with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey())
					.append(" has been received by the RTA. You will receive a confirmation message once your application is processed. Please use application status to know the status of your application. Till then you won't be able to use any registration service.</td></tr>")
					.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
			break;
		case REVOCATION:
			mailContent.append("<table><tr><td>Dear ").append(citizenName).append(",</td></tr> ")
					.append("<tr></tr><tr><td>Your application with application number ")
					.append(appEntity.getApplicationNumber()).append(" with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey())
					.append(" has been received by the RTA. You will receive a confirmation message once your application is processed. Please use application status to know the status of your application.</td></tr>")
					.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
			break;
		}
		return mailContent.toString();
	}

	private String getCFXAppliedApplicantMailContent(String citizenName, ApplicationEntity appEntity,
			TheftIntSusType status) {
		StringBuilder mailContent = new StringBuilder();
		switch (status) {
		case FRESH:
			mailContent.append("<table><tr><td>Dear ").append(citizenName).append(",</td></tr> ")
					.append("<tr></tr><tr><td>CFX has been issued to your vehicle with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey())
					.append(" Till then you won't be able to use any registration service.</td></tr>")
					.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
			break;
		case REVOCATION:
			mailContent.append("<table><tr><td>Dear ").append(citizenName).append(",</td></tr> ")
					.append("<tr></tr><tr><td>Your application with application number ")
					.append(appEntity.getApplicationNumber()).append(" with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey())
					.append(" has been received by the RTA. You will receive a confirmation message once your application is processed. Please use application status to know the status of your application.</td></tr>")
					.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
			break;
		}
		return mailContent.toString();
	}

	private String getTheftAppliedApplicantMessageContent(String citizenName, ApplicationEntity appEntity,
			TheftIntSusType status) {
		StringBuilder mailContent = new StringBuilder();
		switch (status) {
		case FRESH:
			mailContent.append("Dear ").append(citizenName).append(", ")
					.append("Your application with application number ").append(appEntity.getApplicationNumber())
					.append(" with RC Number ").append(appEntity.getLoginHistory().getUniqueKey())
					.append(" has been received by the RTA. You will receive a confirmation message once your application is processed. Please use application status to know the status of your application. Till then you won't be able to use any registration service.")
					.append(" Thank You, AP_Road Transport.");
			break;
		case REVOCATION:
			mailContent.append("Dear ").append(citizenName).append(", ")
					.append("Your application with application number ").append(appEntity.getApplicationNumber())
					.append(" with RC Number ").append(appEntity.getLoginHistory().getUniqueKey())
					.append(" has been received by the RTA. You will receive a confirmation message once your application is processed. Please use application status to know the status of your application. ")
					.append(" Thank You, AP_Road Transport.");
			break;
		}
		return mailContent.toString();
	}

	private String getTheftAppliedFinancerMailContent(String citizenName, String financerName,
			ApplicationEntity appEntity, TheftIntSusType status) {
		StringBuilder mailContent = new StringBuilder();
		switch (status) {
		case FRESH:
			mailContent.append("<table><tr><td>Dear ").append(financerName).append(",</td></tr> ")
					.append("<tr></tr><tr><td>The vehicle owner (").append(citizenName)
					.append(") has applied for the Theft Intimation on vehicle with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey()).append(".</td></tr>")
					.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
			break;
		case REVOCATION:
			mailContent.append("<table><tr><td>Dear ").append(financerName).append(",</td></tr> ")
					.append("<tr></tr><tr><td>The vehicle owner (").append(citizenName)
					.append(") has applied for the Theft Revocation on vehicle with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey()).append(".</td></tr>")
					.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
			break;
		}
		return mailContent.toString();
	}

	private String getTheftAppliedFinancerMessageContent(String citizenName, String financerName,
			ApplicationEntity appEntity, TheftIntSusType status) {
		StringBuilder mailContent = new StringBuilder();
		switch (status) {
		case FRESH:
			mailContent.append("Dear ").append(financerName).append(", ").append("The vehicle owner (")
					.append(citizenName).append(") has applied for the Theft Intimation on vehicle with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey()).append(".")
					.append("Thank You, AP_Road Transport");
			break;
		case REVOCATION:
			mailContent.append("Dear ").append(financerName).append(", ").append("The vehicle owner (")
					.append(citizenName).append(") has applied for the Theft Revocation on vehicle with RC Number ")
					.append(appEntity.getLoginHistory().getUniqueKey()).append(".")
					.append("Thank You, AP_Road Transport");
			break;
		}
		return mailContent.toString();
	}

	private RegistrationServiceResponseModel<FinanceOtherServiceModel> getHptDataFromRegistration(String appNumber,
			String keyType, String key) throws UnauthorizedException {
		return registrationService.getFinancier(key);
	}

	private void saveOrUpdateCurrentAddress(ApplicationEntity applicationEntity, AddressModel addressModel,
			String userName) throws DataMismatchException {
		if (ObjectsUtil.isNull(addressModel.getMandalCode()) || addressModel.getMandalCode() < 1) {
			logger.error("MandalCode is Missing in form data for Change of Address. ApplicationNumber : "
					+ applicationEntity.getApplicationNumber());
			throw new DataMismatchException("Mandal is Missing !!!");
		} else {
			// set new rta office code in application table ...
			RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeRes = registrationService.getRTAOfficeByMandal(
					addressModel.getMandalCode(), applicationEntity.getLoginHistory().getVehicleRcId());
			if (rtaOfficeRes.getHttpStatus().equals(HttpStatus.OK)) {
				RTAOfficeModel rtaOffice = rtaOfficeRes.getResponseBody();
				if (!ObjectsUtil.isNull(rtaOffice)) {
					applicationEntity.setRtaOfficeCode(rtaOffice.getCode());
					applicationDAO.saveOrUpdate(applicationEntity);
				} else {
					logger.error("RTA office is null for mandalCode : " + addressModel.getMandalCode());
					throw new DataMismatchException("RTA office not found for given mandal. Please Try again !!!");
				}
			} else {
				logger.error("Exception while calling registration for rta office by mandal code : "
						+ addressModel.getMandalCode() + " Status : " + rtaOfficeRes.getHttpStatus());
				throw new DataMismatchException(
						"Some error occured while getting RTA Office for given mandal. Please Try again !!!");
			}
		}
		AddressEntity entity = addressDAO.getAddressDetails(applicationEntity.getApplicationId(),
				AddressType.CURRENT_ADDRESS);
		if (ObjectsUtil.isNull(entity)) {
			entity = addressConverter.convertToEntity(addressModel);
			entity.setCreatedBy(userName);
			entity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			entity.setApplicationId(applicationEntity);
			entity.setAddressType(AddressType.CURRENT_ADDRESS.getValue());
		} else {
			entity.setDoorNo(addressModel.getDoorNo());
			entity.setStreetName(addressModel.getStreet());
			entity.setTownName(addressModel.getCity());
			entity.setMandalCode(addressModel.getMandalCode());
			entity.setDistrictCode(addressModel.getDistrictCode());
			entity.setStateCode(addressModel.getStateCode());
			entity.setCountryCode(addressModel.getCountryCode());
			entity.setPincode(String.valueOf(addressModel.getPostOffice()));
			entity.setIsSameAadhar(addressModel.getIsSameAadhar());
		}
		entity.setModifiedBy(userName);
		entity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());

		addressDAO.saveOrUpdate(entity);
	}

	@Override
	@Transactional
	public List<RtaTaskInfo> completeFormDataActiviti(Long sessionId, String taskDef, String userName)
			throws UnauthorizedException {
		logger.info("Calling after save application form data sessionId : " + sessionId);
		Assignee assignee = new Assignee();
		assignee.setUserId(userName);
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		UserSessionEntity sessionEntity = appEntity.getLoginHistory();
		ServiceType service = ServiceType.getServiceType(sessionEntity.getServiceCode());
		Map<String, Object> variableMap = new HashMap<>();
		Map<String, Object> otherDataMap = new HashMap<>();
		// ---- set the iteration in activiti ---------
		variableMap.put(ActivitiService.ITERATION,
				ObjectsUtil.isNull(appEntity.getIteration()) ? 1 : appEntity.getIteration());
		if (!ObjectsUtil.isNull(appEntity.getRtaOfficeCode())) {
			variableMap.put(ActivitiService.RTA_OFFICE_CODE, appEntity.getRtaOfficeCode());
		}
		// ----- make application status to pending whoes payment is not
		// required ----
		if (service == ServiceType.REGISTRATION_SUS_CANCELLATION || service == ServiceType.THEFT_INTIMATION
				|| service == ServiceType.REGISTRATION_CANCELLATION || service == ServiceType.DL_SUSU_CANC) {
			sessionEntity.setCompletionStatus(Status.PENDING.getValue());
			userSessionDAO.saveOrUpdate(sessionEntity);
		}
		// ------ send application to financer whoes payment is not requiered
		// ------
		if (service == ServiceType.REGISTRATION_CANCELLATION) {
			FinanceOtherServiceModel finModel = new FinanceOtherServiceModel();
			finModel.setAppNo(appEntity.getApplicationNumber());
			finModel.setPrNumber(sessionEntity.getUniqueKey());
			finModel.setServiceCode(service.getCode());
			RegistrationServiceResponseModel<FinanceOtherServiceModel> res = registrationService
					.sendOtherAppToFinancier(finModel);
			logger.info("Status while sendOtherAppToFinancier ::" + res.getHttpStatus());
			if (res.getHttpStatus().equals(HttpStatus.OK) || res.getHttpStatus().equals(HttpStatus.ALREADY_REPORTED)) {
				variableMap.put(ActivitiService.IS_ONLINE_FINANCED, true);
			} else {
				variableMap.put(ActivitiService.IS_ONLINE_FINANCED, false);
			}
		}
		// ---- set auto approval for RC Aadhar seeding service
		if (service == ServiceType.AADHAR_SEED_RC) {
			Map<String, Object> map = rcasServvice.getMatchDataBwAadhaarAndRC(appEntity.getApplicationNumber());
			Integer matchingPercentage = (Integer) map.get("overAllPercentage");
			logger.info("RC aadhaar Seeding percentage  ::: " + matchingPercentage);
			if (SomeConstants.SEVENTY <= matchingPercentage) {
				variableMap.put(ActivitiService.AUTOAPPROVED_ACTIVITI, true);
				UserSessionEntity entity = appEntity.getLoginHistory();
				ResponseModel<String> result = rcasServvice.aadhaarSeedingWithSystem(appEntity.getApplicationId(),
						entity.getUniqueKey(), entity.getAadharNumber());
				logger.info("RC aadhaar Seeding status ::: " + result.getStatus());
			} else {
				variableMap.put(ActivitiService.AUTOAPPROVED_ACTIVITI, false);
			}
		}

		if (!ObjectsUtil.isNull(appEntity.getIteration()) && appEntity.getIteration() > 1) {
			// -----do the all task of after payment success as for re-iteration
			// payment is skipped-----------
			logger.info("Doing all the task of after payment as Payment will be skipped ........");
			boolean isCallActiviti = true;
			boolean isSync = false;
			otherDataMap.put("isCallActiviti", true);
			otherDataMap.put("isSync", false);
			paymentService.beforeActivitiTask(appEntity, sessionId, variableMap, otherDataMap);
			isCallActiviti = (boolean) otherDataMap.get("isCallActiviti");
			isSync = (boolean) otherDataMap.get("isSync");
		}

		ActivitiResponseModel<List<RtaTaskInfo>> actResponse = activitiService.completeTask(assignee, taskDef,
				appEntity.getExecutionId(), true, variableMap);
		if (ObjectsUtil.isNull(actResponse) || ObjectsUtil.isNull(actResponse.getActiveTasks())
				|| actResponse.getActiveTasks().size() <= 0) {
			// --- application completed in bpm ----------------
			RtaTaskInfo taskInfo = new RtaTaskInfo();
			taskInfo.setTaskDefKey(ActivitiService.APP_COMPLETED);
			taskInfo.setProcessDefId(appEntity.getLoginHistory().getServiceCode());
			List<RtaTaskInfo> taskList = new ArrayList<RtaTaskInfo>();
			taskList.add(taskInfo);
			if (ServiceType.getServiceType(sessionEntity.getServiceCode()) == ServiceType.FC_ISSUE_CFX) {
				sessionEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
				sessionEntity.setModifiedBy(userName);
				sessionEntity.setCompletionStatus(Status.APPROVED.getValue());
				userSessionDAO.saveOrUpdate(sessionEntity);
			}
			return taskList;
		}
		return actResponse.getActiveTasks();
	}

	private void validatePermitNew(PermitNewRequestModel permitModel, ApplicationEntity appEntity)
			throws InvalidDataExcpetion, UnauthorizedException, ServiceValidationException {
		if (permitModel.getPermitClass().equals(PermitClassType.PUKKA.getLabel())
				&& (StringsUtil.isNullOrEmpty(permitModel.getPermitType())
						|| StringsUtil.isNullOrEmpty(permitModel.getPermitTypeName()))) {
			logger.error("Permit Type Required !!!");
			throw new InvalidDataExcpetion("Permit Type Required !!!");
		}
		if (permitModel.getPermitClass().equals(PermitClassType.TEMPORARY.getLabel())
				&& (StringsUtil.isNullOrEmpty(permitModel.getTempPermitType())
						|| StringsUtil.isNullOrEmpty(permitModel.getTempPermitTypeName()))) {
			logger.error("Temporary Permit Type Required !!!");
			throw new InvalidDataExcpetion("Temporary Permit Type Required !!!");
		}
		RegistrationServiceResponseModel<List<PermitHeaderModel>> pRes = registrationService
				.getPermitDetails(appEntity.getLoginHistory().getVehicleRcId());
		if (pRes.getHttpStatus().equals(HttpStatus.OK) && !ObjectsUtil.isNull(pRes.getResponseBody())
				&& pRes.getResponseBody().size() > 0) {
			logger.info("Validating permit new ..........");
			PermitHeaderModel pukkaPermit = null;
			PermitHeaderModel tempPermit = null;
			for (PermitHeaderModel permit : pRes.getResponseBody()) {
				if (!permit.getIsTempPermit()) {
					pukkaPermit = permit;
					logger.debug("days remaining for pukka permit => "
							+ DateUtil.getDateDiff(DateUtil.toCurrentUTCTimeStamp(), pukkaPermit.getValidToDate()));
				} else {
					tempPermit = permit;
					logger.debug("days remaining for temp permit => "
							+ DateUtil.getDateDiff(DateUtil.toCurrentUTCTimeStamp(), tempPermit.getValidToDate()));
				}
			}
			if (!ObjectsUtil.isNull(pukkaPermit)
					&& (permitModel.getPermitClass().equals(PermitClassType.PUKKA.getLabel()))
					&& (DateUtil.getDateDiff(DateUtil.toCurrentUTCTimeStamp(), pukkaPermit.getValidToDate()) > 15)) {
				logger.error("Permit validation : " + ServiceValidation.PERMIT_NOT_EXPIRED.getValue());
				throw new ServiceValidationException(ServiceValidation.PERMIT_NOT_EXPIRED.getCode(),
						ServiceValidation.PERMIT_NOT_EXPIRED.getValue());
			}
			if (!ObjectsUtil.isNull(tempPermit)
					&& (permitModel.getPermitClass().equals(PermitClassType.TEMPORARY.getLabel()))
					&& (DateUtil.getDateDiff(DateUtil.toCurrentUTCTimeStamp(), tempPermit.getValidToDate()) > 7)) {
				logger.error("Permit validation : " + ServiceValidation.PERMIT_TEMP_NOT_EXPIRED.getValue());
				throw new ServiceValidationException(ServiceValidation.PERMIT_TEMP_NOT_EXPIRED.getCode(),
						ServiceValidation.PERMIT_TEMP_NOT_EXPIRED.getValue());
			}
		}
	}

	private void validateTheftService(TheftIntimationRevocationModel theftModel, ApplicationEntity appEntity)
			throws DataMismatchException, JsonProcessingException, IOException, UnauthorizedException {
		logger.info("Applying theft intimation/suspension :" + theftModel.getTheftStatus() + " for pr : "
				+ appEntity.getLoginHistory().getUniqueKey());
		UserSessionEntity currentSession = appEntity.getLoginHistory();
		UserSessionEntity lastApprovedSession = userSessionDAO.getLatestUserSession(currentSession.getAadharNumber(),
				currentSession.getUniqueKey(), KeyType.PR, ServiceType.THEFT_INTIMATION, Status.APPROVED);
		ObjectMapper mapper = new ObjectMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
		TheftIntimationRevocationModel lastTheftModel = null;
		if (!ObjectsUtil.isNull(lastApprovedSession)) {
			ApplicationEntity lastApplicationEntity = applicationDAO
					.getApplicationFromSession(lastApprovedSession.getSessionId());
			ApplicationFormDataEntity afde = applicationFormDataDAO
					.getApplicationFormData(lastApplicationEntity.getApplicationId(), FormCodeType.TI_FORM.getLabel());
			if (!ObjectsUtil.isNull(afde)) {
				lastTheftModel = mapper.readValue(afde.getFormData(), TheftIntimationRevocationModel.class);
				TheftIntSusType lastTheftStatus = lastTheftModel.getTheftStatus();
				if (lastTheftStatus == theftModel.getTheftStatus()) {
					logger.error(
							"can't reapply for theft intimation : again applying with same action... for applicationNumber : "
									+ appEntity.getApplicationNumber());
					throw new DataMismatchException("Invalid action: Theft Intimaion Already Applied !!!");
				}
			}
		} else {
			if (theftModel.getTheftStatus() == TheftIntSusType.REVOCATION) {
				logger.error("can't revocate if not intimated... for applicationNumber : "
						+ appEntity.getApplicationNumber());
				throw new DataMismatchException("Invalid action: Theft Intimaion Not Found !!!");
			}
		}
		// ---- validate complaintDate -------------
		try {
			if (theftModel.getTheftStatus() == TheftIntSusType.REVOCATION
					&& !ObjectsUtil.isNull(theftModel.getComplaintDate())) {
				try {
					Date d1 = sdf.parse(sdf.format(theftModel.getComplaintDate() * 1000L));
					Date d2 = sdf.parse(sdf.format(lastTheftModel.getComplaintDate() * 1000L));
					if (d1.before(d2)) {
						logger.debug("Theft Revocation :: Revocation complain date : " + d1
								+ " Intimation complain date :" + d2);
						throw new DataMismatchException(
								"Theft Revocation Complain Date Can't Be Before Than Theft Intimation Complain Date !!!");
					}
				} catch (ParseException e) {
					logger.error("Parsing exception in validateTheftService111");
				}
			} else {
				RegistrationServiceResponseModel<ApplicationModel> prDetailsResponse = registrationService
						.getPRDetails(currentSession.getUniqueKey());
				HttpStatus statusCode = prDetailsResponse.getHttpStatus();
				ApplicationModel applicationModel = null;
				if (statusCode == HttpStatus.OK) {
					applicationModel = prDetailsResponse.getResponseBody();
					if (ObjectsUtil.isNull(applicationModel.getPrIssueTime())
							|| ObjectsUtil.isNull(theftModel.getComplaintDate())) {
						logger.debug(" validateTheftService:  pr issue time : " + applicationModel.getPrIssueTime()
								+ " complain date :" + theftModel.getComplaintDate());
						throw new DataMismatchException("Either Complain date or RC issue date is null !!!");
					} else {
						try {
							Date d1 = sdf.parse(sdf.format(theftModel.getComplaintDate() * 1000L));
							Date d2 = sdf.parse(sdf.format(applicationModel.getPrIssueTime() * 1000L));
							if (d1.before(d2)) {
								logger.debug("Theft Intimation :: pr issue time: " + d2 + " complain date :" + d1);
								throw new DataMismatchException("Complain date can't be before RC issue date !!!");
							}
						} catch (ParseException e) {
							logger.error("Parsing exception in validateTheftService");
						}
					}
				}
			}
		} catch (HttpClientErrorException e) {
			logger.error("HttpClientErrorException while calling registration status : " + e.getStatusText());
		}
	}

	private void updateRtaOfficeForOT(AddressChangeModel addressModel, ApplicationEntity appEntity)
			throws NotFoundException, DataMismatchException, ServiceValidationException {
		// -----validate for state --------------------------------------
		CustomerDetailsRequestModel cdrm = null;
		try {
			cdrm = applicationService.getCustomerDetails(appEntity.getLoginHistory().getVehicleRcId());
		} catch (RestClientException e) {
			logger.error("error in getting customer details form registration" + e);
		} catch (Exception e) {
			logger.error("error in getting customer details form registration");
			logger.debug("Exception : " + e);
		}
		if (!(appEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_DEATH.getCode()))) {
			if (ObjectsUtil.isNull(cdrm)) {
				logger.error("Customer details not found vehicleRcId: " + appEntity.getLoginHistory().getVehicleRcId());
				throw new DataMismatchException("Customer Details Not Found !!!");
			} else if (ObjectsUtil.isNull(addressModel)
					|| !addressModel.getStateCode().equalsIgnoreCase(cdrm.getTemp_statecode())) {
				logger.error("Applying for other state in Ownership transfer case.");
				throw new ServiceValidationException(ServiceValidation.NOC_REQUIRED.getCode(),
						"Ownership Transfer Service is not applicable to other state. Please apply NOC Service for the same !!!");
			}
			// -------------validate for mandal code -----------------------
			if (ObjectsUtil.isNull(addressModel.getMandalCode())) {
				logger.error("Mandal Not Found for ot form submit app No : " + appEntity.getApplicationNumber());
				throw new NotFoundException("Mandal Not Found !!!");
			}
			RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponse = registrationService
					.getRTAOfficeByMandal(addressModel.getMandalCode(), appEntity.getLoginHistory().getVehicleRcId());
			if (rtaOfficeResponse.getHttpStatus() == HttpStatus.OK) {
				RTAOfficeModel rtaOffice = rtaOfficeResponse.getResponseBody();
				logger.info("For OTS Updating RTA Office Code to : " + rtaOffice.getCode());
				appEntity.setRtaOfficeCode(rtaOffice.getCode());
				applicationDAO.saveOrUpdate(appEntity);
			} else {
				logger.error("can't find rta office for applicationNumber : " + appEntity.getApplicationNumber()
						+ ", http status code : " + rtaOfficeResponse.getHttpStatus());
				throw new NotFoundException("RTA Office Not Found !!!");
			}
		}
		if (addressModel.getPermitTransferType() != null && !addressModel.getPermitTransferType().isEmpty()) {
			if (!(addressModel.getPermitTransferType().equalsIgnoreCase(PermitOptionType.TRANSFER.getLabel())
					|| addressModel.getPermitTransferType().equalsIgnoreCase(PermitOptionType.SURRENDER.getLabel()))) {
				logger.error("Permit Transfer Type Not matched vehicleRcId: "
						+ appEntity.getLoginHistory().getVehicleRcId());
				throw new DataMismatchException("Permit Transfer Type Not matched !!!");
			}
		}
	}

	private void validateRSC(CommonServiceModel csm, ApplicationEntity appEntity) throws ServiceValidationException {
		RegistrationServiceResponseModel<ApplicationModel> prDetailsResponse = null;
		try {
			prDetailsResponse = registrationService.getPRDetails(appEntity.getLoginHistory().getUniqueKey());
			HttpStatus statusCode = prDetailsResponse.getHttpStatus();
			ApplicationModel applicationModel = null;
			if (statusCode == HttpStatus.OK) {
				applicationModel = prDetailsResponse.getResponseBody();
				if (applicationModel.getPrStatus() == Status.SUSPENDED && (csm.getSuspensionType() == Status.SUSPENDED
						|| csm.getSuspensionType() == Status.OBJECTION)) {
					logger.error("RC is suspended for : " + appEntity.getLoginHistory().getUniqueKey());
					throw new ServiceValidationException(ServiceValidation.RC_SUSPENDED.getCode(),
							ServiceValidation.RC_SUSPENDED.getValue());
				} else if (applicationModel.getPrStatus() == Status.CANCELLED
						&& csm.getSuspensionType() == Status.CANCELLED) {
					logger.error("RC is cancelled for : " + appEntity.getLoginHistory().getUniqueKey());
					throw new ServiceValidationException(ServiceValidation.RC_CANCELLED.getCode(),
							ServiceValidation.RC_CANCELLED.getValue());
				} else if (applicationModel.getPrStatus() == Status.OBJECTION
						&& (csm.getSuspensionType() == Status.OBJECTION
								|| csm.getSuspensionType() == Status.SUSPENDED)) {
					logger.error("RC is RC_OBJECTED for : " + appEntity.getLoginHistory().getUniqueKey());
					throw new ServiceValidationException(ServiceValidation.RC_OBJECTED.getCode(),
							ServiceValidation.RC_OBJECTED.getValue());
				}
			}
		} catch (ServiceValidationException se) {
			throw se;
		} catch (Exception ex) {
			logger.error("Exception while communicating to registration. validateRSC : " + ex.getMessage());
		}
	}

	private Map<String, Object> licenceHolderDetailsValidation(LLRegistrationModel model, String applicationNo,
			ResponseModel<ApplicationFormDataModel> response) {

		Map<String, Object> map = new HashMap<>();
		response.setStatus(ResponseModel.SUCCESS);
		String rtaOfficeCode = null;
		if (StringsUtil.isNullOrEmpty(model.getBloodGroup())) {
			logger.info("blood group is null for applicationNumber : " + applicationNo);
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("blood group is null  !!!");
		} else if (ObjectsUtil.isNull(model.getQualificationCode())) {
			logger.info("Qualification is null for applicationNumber : " + applicationNo);
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Qualification is null  !!!");
		} else if (ObjectsUtil.isNull(model.getMandalCode())) {
			logger.info("can't find mandal code for applicationNumber : " + applicationNo);
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("can't find model code  !!!");
		} else if (!ObjectsUtil.isNull(model.getMandalCode())) {
			try {
				RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponse = registrationService
						.getRTAOfficeByMandal(model.getMandalCode(), null);
				if (rtaOfficeResponse.getHttpStatus() == HttpStatus.OK) {
					RTAOfficeModel rtaOffice = rtaOfficeResponse.getResponseBody();
					rtaOfficeCode = rtaOffice.getCode();
				} else {
					logger.info("can't find rta office for applicationNumber : " + applicationNo
							+ ", http status code : " + rtaOfficeResponse.getHttpStatus());
					response.setStatus(ResponseModel.FAILED);
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());
					response.setMessage("can't find rta office !!!");
				}
			} catch (HttpStatusCodeException e) {
				logger.error("can't find rta office for applicationNumber : " + applicationNo + ", http status code : "
						+ e.getStatusCode());
				response.setStatus(ResponseModel.FAILED);
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setMessage("can't find rta office !!!");
			}
		}
		map.put("response", response);
		map.put("rtaOfficeCode", rtaOfficeCode);
		return map;
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationFormDataModel> saveUpdateForm(ApplicationFormDataModel form,
			String applicationNumber) throws JsonMappingException, IOException {
		ResponseModel<ApplicationFormDataModel> response = new ResponseModel<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
		if (ObjectsUtil.isNull(appEntity)) {
			response.setStatus(ResponseModel.FAILED);
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Invalid Application !!!");
			return response;
		}
		ServiceType serviceType = ServiceType.getServiceType(appEntity.getServiceCode());
		Long time = DateUtil.toCurrentUTCTimeStamp();
		ObjectMapper mapper = new ObjectMapper();
		if (serviceType == ServiceType.LL_FRESH) {
			mapper.readValue(form.getFormData(), LLRegistrationModel.class);
		} else if (serviceType == ServiceType.DL_MILITRY) {
			mapper.readValue(form.getFormData(), DLMilataryDetailsModel.class);
		}
		logger.info("update form data : " + form.getFormData());
		ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
				form.getFormCode());
		if (ObjectsUtil.isNull(entity)) {
			entity = new ApplicationFormDataEntity();
			entity.setApplicationEntity(appEntity);
			entity.setFormData(form.getFormData());
			entity.setFormCode(form.getFormCode());
			entity.setCreatedOn(time);
			response.setMessage("Saved Successfully.");
		} else {
			entity.setFormData(form.getFormData());
			entity.setFormCode(form.getFormCode());
			entity.setModifiedOn(time);
			response.setMessage("Updated Successfully.");
		}
		applicationFormDataDAO.saveOrUpdate(entity);
		response.setStatus(ResponseModel.SUCCESS);
		return response;
	}

	@Override
	@Transactional
	public Integer getRegistrationCategory(Long applicationId, ServiceType serviceType) {
		boolean flag = false;
		List<LicensePermitDetailsEntity> entities = licencePermitDetailsDAO.getLicensePermitDetails(applicationId);
		Set<String> vehicleCodeSet = new HashSet<String>();
		for (LicensePermitDetailsEntity entity : entities) {
			vehicleCodeSet.add(entity.getVehicleClassCode());
		}
		if (serviceType == ServiceType.DL_ENDORSMENT) {
			if (vehicleCodeSet.contains(SomeConstants.HGV) || vehicleCodeSet.contains(SomeConstants.HZRD)) {
				return SomeConstants.ONE;
			}
			return SomeConstants.TWO;
		}
		List<LlrVehicleClassMasterEntity> vehicleClassList = licenceEndorsCOVDAO.getVehicleDescription(vehicleCodeSet);
		for (LlrVehicleClassMasterEntity entity : vehicleClassList) {
			if (SomeConstants.TRANSPORT.equalsIgnoreCase(entity.getVehicleTransportType())) {
				flag = true;
				break;
			}
		}
		return flag ? SomeConstants.ONE : SomeConstants.TWO;
	}

	// this method is used to validate the seating capacity with cov
	// this method is also available in citizen end.
	private boolean isCovAlterationValidation(VehicleBodyModel bodyModel,
			VehicleDetailsRequestModel vehicleDetailsModel) {
		boolean isValid = true;
		List<AlterationCategory> alterationCategorieList = bodyModel.getAlterationCategory();
		if (alterationCategorieList.contains(AlterationCategory.SEATING_CAPACITY)
				|| alterationCategorieList.contains(AlterationCategory.VEHICLE_TYPE)) {
			if (!ObjectsUtil.isNull(bodyModel.getSeatingCapacity())) {
				if (!ObjectsUtil.isNull(bodyModel.getVehicleSubClass())) {
					isValid = isSeatingCapacityExits(bodyModel.getVehicleSubClass(), bodyModel.getSeatingCapacity());
				} else {
					isValid = isSeatingCapacityExits(vehicleDetailsModel.getVehicle().getVehicleSubClass(),
							bodyModel.getSeatingCapacity());
				}
			} else {
				if (!ObjectsUtil.isNull(bodyModel.getVehicleSubClass())) {
					isValid = isSeatingCapacityExits(bodyModel.getVehicleSubClass(),
							vehicleDetailsModel.getVehicle().getSeatingCapacity());
				}
			}
		}
		return isValid;
	}

	private boolean isSeatingCapacityExits(String cov, int seatingCapacity) {
		switch (cov) {
		case "MAXT":
			if (seatingCapacity >= 8 && seatingCapacity <= 13)
				return true;
			break;
		case "OBPN":
			if (seatingCapacity >= 8 && seatingCapacity <= 13)
				return true;
			break;
		case "MCRN":
			if (seatingCapacity >= 2 && seatingCapacity <= 7)
				return true;
			break;
		case "MTLT":
			if (seatingCapacity >= 4 && seatingCapacity <= 7)
				return true;
			break;
		case "LTCT":
			if (seatingCapacity >= 4 && seatingCapacity <= 7)
				return true;
			break;
		case "PSVT":
			if (seatingCapacity >= 8 && seatingCapacity <= 99)
				return true;
			break;
		case "OBT":
			if (seatingCapacity >= 14 && seatingCapacity <= 99)
				return true;
			break;
		case "COCT":
			if (seatingCapacity >= 14 && seatingCapacity <= 99)
				return true;
			break;
		case "SCRT":
			if (seatingCapacity >= 14 && seatingCapacity <= 99)
				return true;
			break;
		case "TOVT":
			if (seatingCapacity >= 14 && seatingCapacity <= 99)
				return true;
			break;
		case "STCT":
			if (seatingCapacity >= 4 && seatingCapacity <= 7)
				return true;
			break;
		default:
			return true;
		}
		return false;
	}
	
	@Override
	@Transactional
	public Boolean aadhaarSeedingAvailability(Long sessionId){
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);	
		if (ServiceType.AADHAR_SEED_RC.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
			Map<String, Object> map = rcasServvice.getMatchDataBwAadhaarAndRC(appEntity.getApplicationNumber());
			Integer matchingPercentage = (Integer) map.get("overAllPercentage");
			logger.info("RC aadhaar Seeding percentage  ::: " + matchingPercentage);
			if (SomeConstants.ZERO == matchingPercentage) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
}

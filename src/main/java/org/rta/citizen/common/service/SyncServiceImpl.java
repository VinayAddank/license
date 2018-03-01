package org.rta.citizen.common.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.aadharseeding.rc.service.RCASService;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.addresschange.service.ACApplicationService;
import org.rta.citizen.common.converters.UserSessionConverter;
import org.rta.citizen.common.converters.payment.FeeDetailConverter;
import org.rta.citizen.common.converters.payment.TaxDetailConverter;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.payment.FeeDetailDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.FeeDetailEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.HPAHPTSyncModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.SyncDataModel;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.duplicateregistration.service.DRApplicationService;
import org.rta.citizen.licence.service.LicenceService;
import org.rta.citizen.licence.service.updated.LicenseSyncingService;
import org.rta.citizen.noc.service.NocService;
import org.rta.citizen.ownershiptransfer.service.OTService;
import org.rta.citizen.paytax.service.PayTaxService;
import org.rta.citizen.registrationcancellation.service.RCApplicationService;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.rta.citizen.registrationrenewal.service.RegistrationRenewalService;
import org.rta.citizen.rsc.service.RSCService;
import org.rta.citizen.stoppagetax.service.StoppageTaxService;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.rta.citizen.userregistration.dao.PendingUsernameDAO;
import org.rta.citizen.userregistration.entity.PendingUsernameEntity;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.rta.citizen.userregistration.service.UserService;
import org.rta.citizen.vehiclealteration.service.VehicleAlterationService;
import org.rta.citizen.vehiclereassignment.service.VRApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SyncServiceImpl implements SyncService {

	private static final Logger log = Logger.getLogger(SyncServiceImpl.class);

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private NocService nocService;

	@Autowired
	private ACApplicationService acApplicationService;

	@Autowired
	private VehicleAlterationService vehicleAlterationService;

	@Autowired
	private UserService userService;

	@Autowired
	private RegistrationRenewalService registrationRenewalService;

	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;

	@Autowired
	private UserSessionConverter userSessionConverter;

	@Autowired
	private OTService otService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private DRApplicationService drApplicationService;

	@Autowired
	private RCApplicationService rcApplicationService;

	@Autowired
	private VRApplicationService vrApplicationService;

	@Autowired
	private PendingUsernameDAO pendingUsernameDAO;

	@Autowired
	private RSCService rscService;

	@Autowired
	private LicenceService licenceService;

	@Autowired
	private LicenseSyncingService licenseSyncingService;

	@Autowired
	private FeeDetailDAO feeDetailDAO;

	@Autowired
	private FeeDetailConverter feeDetailConverter;

	@Autowired
	private RCASService rcasService;

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private PayTaxService payTaxService;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private TaxDetailDAO taxDetailDAO;

	@Autowired
	private TaxDetailConverter taxDetailConverter;

	@Autowired
	private StoppageTaxService stoppageTaxService;

	@Override
	@Transactional
	public void syncApprovedApplications(Status status, ApplicationEntity appEntity, UserSessionEntity userSession,
			String approverName) {
		log.debug("Going to sync app : " + appEntity.getApplicationNumber());
		ResponseModel<String> responseModel = null;
		String formCode;
		boolean isRegistrationService = false;
		if (status == Status.APPROVED || appEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_FRESH.getCode())
				|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_RETEST.getCode())
				|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_FRESH.getCode())
				|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_RETEST.getCode())
				|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_ENDORSMENT.getCode())) {

			if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.NOC_ISSUE.getCode())) {

				responseModel = nocService.saveOrUpdateNocDetails(userSession.getVehicleRcId(),
						appEntity.getApplicationId(), userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.NOC_CANCELLATION.getCode())) {

				responseModel = nocService.saveOrUpdateCancellationNocDetails(userSession.getVehicleRcId(),
						appEntity.getApplicationId(), userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.ADDRESS_CHANGE.getCode())) {

				responseModel = acApplicationService.saveOrUpdateAddressChange(userSession.getVehicleRcId(),
						appEntity.getApplicationId(), userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.VEHICLE_ATLERATION.getCode())) {

				responseModel = vehicleAlterationService.saveOrUpdateVehicleAlteration(userSession.getUniqueKey(),
						appEntity.getApplicationId());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.REGISTRATION_RENEWAL.getCode())) {

				responseModel = registrationRenewalService.saveOrUpdateRegistrationRenewal(userSession.getUniqueKey(),
						appEntity.getApplicationId());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_AUCTION.getCode())
					|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_DEATH.getCode())
					|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_SALE.getCode())) {

				responseModel = otService.saveOrUpdateOwnershipTransfer(userSession.getVehicleRcId(),
						appEntity.getApplicationId(), userSession.getUniqueKey(),
						ServiceType.getServiceType(appEntity.getServiceCode()), userSession.getAadharNumber());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DUPLICATE_REGISTRATION.getCode())) {

				responseModel = drApplicationService.saveOrUpdateDuplicateRegistration(appEntity.getApplicationId(),
						userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.REGISTRATION_CANCELLATION.getCode())) {

				responseModel = rcApplicationService.saveOrUpdateRegistrationCanellation(userSession.getUniqueKey(),
						appEntity.getApplicationId());
			} else if (appEntity.getServiceCode()
					.equalsIgnoreCase(ServiceType.REGISTRATION_SUS_CANCELLATION.getCode())) {

				responseModel = rscService.saveOrUpdateSuspensionOrCancellationOfRC(userSession.getUniqueKey(),
						appEntity.getApplicationId());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.VEHICLE_REASSIGNMENT.getCode())) {

				responseModel = vrApplicationService.saveOrUpdateVehicleReassignment(appEntity.getApplicationId(),
						userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DEALER_SIGNUP.getCode())) {
				formCode = FormCodeType.DEALERREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.ALTERATION_AGENCY_SIGNUP.getCode())) {
				formCode = FormCodeType.AAREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.BODYBUILDER_SIGNUP.getCode())) {
				formCode = FormCodeType.BBREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.FINANCIER_SIGNUP.getCode())) {
				formCode = FormCodeType.FINREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.PUC_USER_SIGNUP.getCode())) {
				formCode = FormCodeType.PUCREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.HPA.getCode())
					|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.HPT.getCode())) {
				responseModel = syncForHPAHPT(appEntity.getApplicationNumber());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.HAZARDOUS_VEH_TRAIN_INST.getCode())) {
				formCode = FormCodeType.HAZVEHTRINSTREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DRIVING_INSTITUTE.getCode())) {
				formCode = FormCodeType.DRIVINGINSTREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.MEDICAL_PRACTITIONER.getCode())) {
				formCode = FormCodeType.MEDPRTSNRREG_FORM.getLabel();
				responseModel = saveUser(appEntity, userSession, formCode);
				isRegistrationService = true;
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.FRESH_RC_FINANCIER.getCode())) {
				AddressChangeModel addressModel = new AddressChangeModel();
				addressModel.setAadharNumber(appEntity.getLoginHistory().getAadharNumber());
				addressModel.setServiceType(ServiceType.FRESH_RC_FINANCIER);
				addressModel.setUserType(UserType.ROLE_ONLINE_FINANCER.getLabel());
				addressModel.setVehicleRcId(appEntity.getLoginHistory().getVehicleRcId());
				addressModel.setApplicationNumber(appEntity.getApplicationNumber());
				
				List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
				List<UserActionModel> actionModelList = new ArrayList<>();
				for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
					UserActionModel actionModel = new UserActionModel();
					actionModel.setUserId(String.valueOf(history.getRtaUserId()));
					actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
					actionModel.setUserAction(Status.getLabel(history.getStatus()));
					actionModelList.add(actionModel);
				}
				addressModel.setActionModelList(actionModelList);
				
				log.info("FreshRC sync for application number : " + appEntity.getApplicationNumber());
				try {
					RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService
							.saveOrUpdateForCustomerDetails(addressModel);
					if (regResponse != null && HttpStatus.OK == regResponse.getHttpStatus()) {
						log.info("Fresh RC for application number " + appEntity.getApplicationNumber() + " success");
						responseModel = new ResponseModel<>(ResponseModel.SUCCESS);
					} else {
						log.info("Fresh RC for application number " + appEntity.getApplicationNumber() + " failed");
						responseModel = new ResponseModel<>(ResponseModel.FAILED);
					}
				} catch (UnauthorizedException e) {
					log.error("Fresh RC sync error ", e);
					responseModel = new ResponseModel<>(ResponseModel.FAILED);
				} catch (Exception e) {
					log.error("Fresh RC sync error ", e);
					responseModel = new ResponseModel<>(ResponseModel.FAILED);
				}
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.THEFT_INTIMATION.getCode())) {
				try {
					responseModel = syncTI(appEntity, userSession, FormCodeType.TI_FORM);
				} catch (IOException e) {
					e.printStackTrace();
					responseModel = new ResponseModel<>(ResponseModel.FAILED);
				}
			}
			// License Data Sync with RTA 1.2 DB
			else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_FRESH.getCode())
					|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_MILITRY.getCode())) {

				responseModel = licenceService.saveUpdateLicenseHolderDtls(appEntity.getApplicationId(),
						userSession.getAadharNumber());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_RETEST.getCode())) {

				responseModel = licenseSyncingService.saveLearnerPermitDetailsForLLR(appEntity.getApplicationId(),
						userSession.getAadharNumber(), userSession.getUniqueKey(), status);
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_ENDORSEMENT.getCode())) {

				responseModel = licenseSyncingService.saveUpdateLearnerPermitDtls(appEntity.getApplicationId(),
						userSession.getAadharNumber());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_FRESH.getCode())) {

				responseModel = licenceService.saveUpdateDriversFreshPermitDtls(status, appEntity.getApplicationId(),
						userSession.getAadharNumber(), userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_RETEST.getCode())) {

				responseModel = licenseSyncingService.saveUpdateDriversPermitDtlsForDLRE(status,
						appEntity.getApplicationNumber(), userSession.getAadharNumber(), userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_ENDORSMENT.getCode())) {

				responseModel = licenseSyncingService.saveUpdateDriversEndorsmentsDtls(status,
						appEntity.getApplicationId(), userSession.getAadharNumber(), userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_CHANGE_ADDRESS.getCode())) {

				responseModel = licenseSyncingService.updateLicenseHolderDetails(appEntity.getApplicationId(),
						userSession.getAadharNumber());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_BADGE.getCode())
					|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_RENEWAL.getCode())
					|| appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_EXPIRED.getCode())) {

				responseModel = licenseSyncingService.driverLicenceCommonService(appEntity.getApplicationId(),
						userSession.getAadharNumber(), appEntity.getServiceCode());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_INT_PERMIT.getCode())) {

				responseModel = licenceService.saveUpdateIntrnationalLicenseDtls(status, appEntity.getApplicationId(),
						userSession.getAadharNumber(), userSession.getUniqueKey());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_SUSU_CANC.getCode())) {

				responseModel = licenseSyncingService.suspendCancelLicense(appEntity.getApplicationId(),
						appEntity.getServiceCode());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_REVO_SUS.getCode())) {

				responseModel = licenseSyncingService.licenseRevokeSuspension(appEntity.getApplicationId(),
						appEntity.getServiceCode());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.SUSPENSION_REVOCATION.getCode())) {
				try {
					ApplicationFormDataEntity enFormDataEntity = applicationFormDataDAO
							.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.SR_FORM.getLabel());
					ObjectMapper mapper = new ObjectMapper();
					CommonServiceModel model = mapper.readValue(enFormDataEntity.getFormData(),
							CommonServiceModel.class);
					model.setServiceType(ServiceType.SUSPENSION_REVOCATION);
					model.setPrNumber(userSession.getUniqueKey());
					
					List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO						.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
					List<UserActionModel> actionModelList = new ArrayList<>();
					for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
						UserActionModel actionModel = new UserActionModel();
						actionModel.setUserId(String.valueOf(history.getRtaUserId()));
						actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
						actionModel.setUserAction(Status.getLabel(history.getStatus()));
						actionModelList.add(actionModel);
					}
					model.setActionModelList(actionModelList);
					
					RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService
							.saveOrUpdateForCitizenCommonSerives(model);
					if (regResponse.getHttpStatus() == HttpStatus.OK) {
						log.info("Suspension Revocation Sync : done successfully for application number : "
								+ appEntity.getApplicationNumber());
						responseModel = new ResponseModel<>(ResponseModel.SUCCESS);
					} else {
						log.error("Suspension Revocation Sync : failed for application number : "
								+ appEntity.getApplicationNumber());
						responseModel = new ResponseModel<>(ResponseModel.FAILED);
					}
				} catch (Exception ex) {
					log.error("Getting error in update Or save in SuspensionOrCancellationOfRC ");
					responseModel = new ResponseModel<>(ResponseModel.FAILED);
				}
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.AADHAR_SEED_RC.getCode())) {

				responseModel = rcasService.aadhaarSeedingWithSystem(appEntity.getApplicationId(),
						userSession.getUniqueKey(), userSession.getAadharNumber());
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.PAY_TAX.getCode())) {
				log.info("::PayTax Api::sync::start:: ");
				responseModel = payTaxService.syncPayTaxData(appEntity, userSession);
				log.info("::PayTax Api::sync::end:: ");
			} else if (appEntity.getServiceCode().equalsIgnoreCase(ServiceType.STOPPAGE_TAX.getCode())) {

				responseModel = stoppageTaxService.saveOrUpdateStoppageTax(userSession.getUniqueKey(),
						appEntity.getApplicationId());
			}
			if (ServiceCategory.DL_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
					|| ServiceCategory.LL_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())) {
				responseModel = attachmentService.saveOrUpdateAttachments(appEntity.getApplicationId(),
						userSession.getAadharNumber());
			}
			if (responseModel.getStatus().equalsIgnoreCase(ResponseModel.SUCCESS)) {
				appEntity.setSyncWithMaster(Boolean.TRUE);
				// in case of user registration services delete the occupied
				// username from the pending username table
				if (isRegistrationService) {
					PendingUsernameEntity en = pendingUsernameDAO.getByApplication(appEntity.getApplicationId());
					if (ObjectsUtil.isNull(en)) {
						log.debug("unable to delete username on completion of application : "
								+ appEntity.getApplicationNumber());
					} else {
						pendingUsernameDAO.delete(en);
					}
				}
			}
		}
	}

	private ResponseModel<String> saveUser(ApplicationEntity appEntity, UserSessionEntity userSession,
			String formCode) {
		ResponseModel<String> responseModel;
		ApplicationFormDataEntity afde = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
				formCode);
		ObjectMapper mapper = new ObjectMapper();
		UserSignupModel user;
		try {
			user = mapper.readValue(afde.getFormData(), UserSignupModel.class);
		} catch (IOException e) {
			log.error("unable to read usersignupmodel for applicationNumber : " + appEntity.getApplicationNumber());
			user = null;
		}
		if (!ObjectsUtil.isNull(user)) {
			try {
				ResponseModel<UserSignupModel> res = userService
						.saveOrUpdateUser(userSessionConverter.converToModel(userSession), user);
				if (res != null) {
					responseModel = new ResponseModel<>(ResponseModel.SUCCESS);
				} else {
					responseModel = new ResponseModel<>(ResponseModel.FAILED);
				}
			} catch (UnauthorizedException e) {
				log.error("unable to sync data with registration service for application number : "
						+ appEntity.getApplicationNumber());
				log.error(e.getMessage());
				responseModel = new ResponseModel<>(ResponseModel.FAILED);
			}
		} else {
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
		}
		return responseModel;
	}

	private ResponseModel<String> syncForHPAHPT(String appNo) {
		ResponseModel<String> responseModel;
		try {
			HPAHPTSyncModel hpahptSyncMdl = new HPAHPTSyncModel();
			hpahptSyncMdl.setAppNumber(appNo);
			
			ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
			List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
			List<UserActionModel> actionModelList = new ArrayList<>();
			for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
				UserActionModel actionModel = new UserActionModel();
				actionModel.setUserId(String.valueOf(history.getRtaUserId()));
				actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
				actionModel.setUserAction(Status.getLabel(history.getStatus()));
				actionModelList.add(actionModel);
			}
			hpahptSyncMdl.setActionModelList(actionModelList);
			
			RegistrationServiceResponseModel<SaveUpdateResponse> res = registrationService.updateDataHPAHPT(hpahptSyncMdl);
			if (res.getHttpStatus().equals(HttpStatus.OK)) {
				responseModel = new ResponseModel<>(ResponseModel.SUCCESS);
			} else {
				responseModel = new ResponseModel<>(ResponseModel.FAILED);
			}
		} catch (UnauthorizedException e) {
			log.error("unable to sync data with registration service for application number : " + appNo);
			log.error(e.getMessage());
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
		}
		return responseModel;
	}

	private ResponseModel<String> syncTI(ApplicationEntity appEntity, UserSessionEntity userSession,
			FormCodeType formCode) throws JsonParseException, JsonMappingException, IOException {
		ResponseModel<String> responseModel;
		try {
			ApplicationFormDataEntity entity = applicationFormDataDAO
					.getApplicationFormData(appEntity.getApplicationId(), formCode.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			TheftIntimationRevocationModel theftModel = mapper.readValue(entity.getFormData(),
					TheftIntimationRevocationModel.class);
			
			List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
			List<UserActionModel> actionModelList = new ArrayList<>();
			for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
				UserActionModel actionModel = new UserActionModel();
				actionModel.setUserId(String.valueOf(history.getRtaUserId()));
				actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
				actionModel.setUserAction(Status.getLabel(history.getStatus()));
				actionModelList.add(actionModel);
			}
			theftModel.setActionModelList(actionModelList);
			
			RegistrationServiceResponseModel<SaveUpdateResponse> res = registrationService.syncTheft(theftModel,
					userSession.getUniqueKey());
			if (res.getHttpStatus().equals(HttpStatus.OK)) {
				responseModel = new ResponseModel<>(ResponseModel.SUCCESS);
			} else {
				responseModel = new ResponseModel<>(ResponseModel.FAILED);
			}
		} catch (UnauthorizedException e) {
			log.error("unable to sync data with registration service for application number : "
					+ appEntity.getApplicationNumber());
			log.error(e.getMessage());
			responseModel = new ResponseModel<>(ResponseModel.FAILED);
		}
		return responseModel;
	}

	@Override
	public void syncData(Status status, ApplicationEntity appEntity, UserSessionEntity userSession,
			String approverName) {
		if (status == Status.APPROVED) {
			log.info(":Citizen:syncData::Service::start:::::: " + appEntity.getServiceCode());
			SyncDataModel syncDataModel = new SyncDataModel();
			FeeDetailEntity feeDetailEntity = null;
			TaxDetailEntity taxDetailEntity = null;
			FeeModel feeModel = new FeeModel();
			TaxModel taxModel = new TaxModel();
			try {
				List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO
						.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
				List<ApplicationFormDataEntity> formListEntity = applicationFormDataDAO
						.getAllApplicationFormData(appEntity.getApplicationId());
				List<ApplicationFormDataModel> formList = new ArrayList<>();
				for (ApplicationFormDataEntity formEntity : formListEntity) {
					ApplicationFormDataModel model = new ApplicationFormDataModel();
					model.setFormCode(formEntity.getFormCode());
					model.setFormData(formEntity.getFormData());
					formList.add(model);
				}
				List<UserActionModel> actionModelList = new ArrayList<>();
				for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
					UserActionModel actionModel = new UserActionModel();
					actionModel.setUserId(String.valueOf(history.getRtaUserId()));
					actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
					actionModel.setUserAction(Status.getLabel(history.getStatus()));
					actionModelList.add(actionModel);
				}
				syncDataModel.setActionModelList(actionModelList);
				if (!ObjectsUtil.isNull(formList) && formList.size() > 0) {
					syncDataModel.setFormList(formList);
				}
				feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
				if (feeDetailEntity != null)
					feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
				taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
				if (taxDetailEntity != null)
					taxModel = taxDetailConverter.convertToModel(taxDetailEntity);
				if (taxModel != null) {
					log.info(":::Sync Permit new:Service::tax: " + taxModel.getTaxAmt());
					syncDataModel.setTaxModel(taxModel);
				}
				syncDataModel.setFeeModel(feeModel);
				syncDataModel.setVehicleRcId(userSession.getVehicleRcId());
				syncDataModel.setPrNumber(userSession.getUniqueKey());
				syncDataModel.setServiceCategory(appEntity.getServiceCategory());
				syncDataModel.setServiceType(appEntity.getServiceCode());
				syncDataModel.setApplicationNumber(appEntity.getApplicationNumber());
				log.info("::Sync Permit Fitness::Service::::::: " + syncDataModel);
				RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService
						.syncData(syncDataModel);
				if (regResponse.getHttpStatus() == HttpStatus.OK) {
					appEntity.setSyncWithMaster(Boolean.TRUE);
					applicationDAO.saveOrUpdate(appEntity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info(":Citizen:syncData::Service::end::::::");
		}
	}

}

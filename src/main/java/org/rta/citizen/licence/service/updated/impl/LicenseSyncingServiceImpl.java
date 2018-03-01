package org.rta.citizen.licence.service.updated.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.AttachmentDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.AttachmentEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.DistrictModel;
import org.rta.citizen.common.model.MandalModel;
import org.rta.citizen.common.model.QualificationModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.StateModel;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.dao.VehicleClassTestsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.enums.LicenceVehicleClass;
import org.rta.citizen.licence.enums.MedicalFitnessType;
import org.rta.citizen.licence.model.updated.CourseCertificateModel;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.model.updated.MedicalDetailsModel;
import org.rta.citizen.licence.model.updated.SupensionCancellationModel;
import org.rta.citizen.licence.model.updated.SuspensionRevocationModel;
import org.rta.citizen.licence.service.LicenceService;
import org.rta.citizen.licence.service.updated.DrivingLicenseService;
import org.rta.citizen.licence.service.updated.LicenseSyncingService;
import org.rta.citizen.licence.utils.ApplicationUtil;
import org.rta.citizen.slotbooking.dao.SlotDAO;
import org.rta.citizen.slotbooking.entity.SlotApplicationsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LicenseSyncingServiceImpl implements LicenseSyncingService {

	private static final Logger logger = Logger.getLogger(LicenseSyncingServiceImpl.class);

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private AttachmentDAO attachmentDAO;

	@Autowired
	private SlotDAO slotDAO;

	@Autowired
	private VehicleClassTestsDAO vehicleClassTestsDAO;

	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private LicensePermitDetailsDAO licensePermitDetailsDAO;

	@Autowired
	private DrivingLicenseService drivingLicenseService;

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private LicenceService licenceService;

	@Override
	@Transactional
	public ResponseModel<String> updateLicenseHolderDetails(Long applicationId, String aadharNumber) {
		LLRegistrationModel llModel = new LLRegistrationModel();
		ApplicationFormDataEntity formEntity = applicationFormDataDAO.getApplicationFormData(applicationId,
				FormCodeType.DLCA_FORM.getLabel());
		ObjectMapper mapper = new ObjectMapper();
		try {
			llModel = mapper.readValue(formEntity.getFormData(), LLRegistrationModel.class);

			LicenseHolderDtlsModel licenseHolderDtlsModel = getLicenseHolderDtlsModel(llModel, aadharNumber,
					formEntity.getApplicationEntity().getRtaOfficeCode());
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.updateLicenseHolderDetails(licenseHolderDtlsModel);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with saveUpdateLicenseHolderDtls " + e.getMessage());
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	@Override
	@Transactional
	public ResponseModel<String> saveUpdateDriversEndorsmentsDtls(Status status, Long applicationId,
			String aadharNumber, String uniqueKey) {
		LLRegistrationModel llDetailsModel = new LLRegistrationModel();
		CourseCertificateModel certificateModel = new CourseCertificateModel();
		try {
			List<ApplicationFormDataEntity> formsEntity = applicationFormDataDAO
					.getAllApplicationFormData(applicationId);
			ObjectMapper mapper = new ObjectMapper();
			for (ApplicationFormDataEntity formEntity : formsEntity) {
				if (FormCodeType.DLE_FORM.getLabel().equalsIgnoreCase(formEntity.getFormCode())) {
					llDetailsModel = mapper.readValue(formEntity.getFormData(), LLRegistrationModel.class);
				} else if (FormCodeType.DLE_COURSE_FORM.getLabel().equalsIgnoreCase(formEntity.getFormCode())) {
					certificateModel = mapper.readValue(formEntity.getFormData(), CourseCertificateModel.class);
				}
			}
			LicenseHolderPermitDetails holderPermitDetails = new LicenseHolderPermitDetails();
			LicenseHolderDtlsModel licenseHolderDtlsModel = new LicenseHolderDtlsModel();
			licenseHolderDtlsModel.setAadhaarNo(aadharNumber);
			if (!ObjectsUtil.isNull(llDetailsModel)) {
				licenseHolderDtlsModel.setMobileNo(llDetailsModel.getMobileNo());
				licenseHolderDtlsModel.setEmail(llDetailsModel.getEmailId());
			}
			List<DriversLicenceDetailsModel> driversPermitDtlModelList = getDriversEndorsmentsDtlModels(status,
					applicationId, uniqueKey, certificateModel);
			holderPermitDetails.setLicenseHolderDetails(licenseHolderDtlsModel);
			holderPermitDetails.setDriversPermitDetailsList(driversPermitDtlModelList);
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.addEndorsmentsInDriverPermitDetails(holderPermitDetails);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with saveUpdateDriversEndorsmentsDtls " + e.getMessage());
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	@Override
	@Transactional
	public ResponseModel<String> saveUpdateLearnerPermitDtls(Long applicationId, String aadharNumber) {
		MedicalDetailsModel mDetailsModel = new MedicalDetailsModel();
		try {
			ApplicationFormDataEntity formEntity = applicationFormDataDAO.getApplicationFormData(applicationId,
					FormCodeType.LLE_MEDICAL_FORM.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			if (!ObjectsUtil.isNull(formEntity)) {
				mDetailsModel = mapper.readValue(formEntity.getFormData(), MedicalDetailsModel.class);
			}
			List<LearnersPermitDtlModel> learnersPermitDtlModelList = getLearnersPermitDtlModels(mDetailsModel,
					ServiceType.LL_ENDORSEMENT, applicationId);
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.saveLearnerPermitDetails(learnersPermitDtlModelList, aadharNumber);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {

				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with saveUpdateLearnerPermitDtls " + e.getMessage());
		}
		return new ResponseModel<String>(SaveUpdateResponse.FAILURE);
	}

	@Override
	@Transactional
	public ResponseModel<String> saveLearnerPermitDetailsForLLR(Long applicationId, String aadharNumber,
			String uniqueKey, Status status) {

		MedicalDetailsModel mDetailsModel = new MedicalDetailsModel();
		ApplicationEntity llrEntity = applicationDAO.getEntity(ApplicationEntity.class, applicationId);
		ApplicationEntity llfEntity = applicationDAO.getApplication(uniqueKey);

		try {
			ApplicationFormDataEntity formEntity = applicationFormDataDAO
					.getApplicationFormData(llfEntity.getApplicationId(), FormCodeType.LLF_MEDICAL_FORM.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			if (!ObjectsUtil.isNull(formEntity)) {
				mDetailsModel = mapper.readValue(formEntity.getFormData(), MedicalDetailsModel.class);
			}
			List<LearnersPermitDtlModel> learnersPermitDtlModelList = getLearnersPermitDtlModels(mDetailsModel,
					ServiceType.LL_RETEST, llfEntity.getApplicationId(), llrEntity.getApplicationNumber(), status);
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.saveLearnerPermitDetails(learnersPermitDtlModelList, aadharNumber);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {

				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with saveUpdateLearnerPermitDtls " + e.getMessage());
		}
		return new ResponseModel<String>(SaveUpdateResponse.FAILURE);
	}

	@Override
	@Transactional
	public ResponseModel<String> saveUpdateDriversPermitDtlsForDLRE(Status status, String applicationNo,
			String aadharNumber, String uniqueKey) {

		boolean flag = false;
		try {
			ApplicationEntity applicationEntity = null;
			List<UserSessionEntity> dlFreshRejectedSessions = userSessionDAO.getRejectedAppSessions(aadharNumber,
					ServiceType.DL_FRESH.getCode());
			for (UserSessionEntity rejected : dlFreshRejectedSessions) {
				if (!ObjectsUtil.isNullOrEmpty(dlFreshRejectedSessions) && rejected.getUniqueKey().equals(uniqueKey)) {
					flag = true;
					applicationEntity = applicationDAO.getApplicationFromSession(rejected.getSessionId());
					break;
				}
			}
			if (!flag) {
				List<UserSessionEntity> dlEndorseRejectedSessions = userSessionDAO.getRejectedAppSessions(aadharNumber,
						ServiceType.DL_ENDORSMENT.getCode());
				List<UserSessionEntity> dlEndorseAppSession = userSessionDAO.getAppliedSessions(aadharNumber,
						ServiceType.LL_ENDORSEMENT.getCode());
				for (UserSessionEntity rejected : dlEndorseRejectedSessions) {
					if (!ObjectsUtil.isNullOrEmpty(dlEndorseRejectedSessions)) {
						if (rejected.getUniqueKey().equals(uniqueKey)) {
							applicationEntity = applicationDAO.getApplicationFromSession(rejected.getSessionId());
							break;
						} else if (!ObjectsUtil.isNullOrEmpty(dlEndorseAppSession)) {
							for (UserSessionEntity appSesions : dlEndorseAppSession) {
								if (uniqueKey.equals(appSesions.getUniqueKey())) {
									applicationEntity = applicationDAO
											.getApplicationFromSession(appSesions.getSessionId());
									break;
								}
							}
						}
					}
				}
			}
			CourseCertificateModel certificateModel = null;
			ServiceType serviceType = ServiceType.getServiceType(applicationEntity.getServiceCode());
			if (ServiceType.DL_ENDORSMENT == serviceType) {
				ApplicationFormDataEntity formEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLE_COURSE_FORM.getLabel());
				ObjectMapper mapper = new ObjectMapper();
				if (!ObjectsUtil.isNull(formEntity)) {
					certificateModel = mapper.readValue(formEntity.getFormData(), CourseCertificateModel.class);
				}
			}
			LicenseHolderPermitDetails model = registrationLicenseService.getLicenseHolderDtls(aadharNumber, null, "")
					.getResponseBody();

			List<DriversLicenceDetailsModel> drDetailsModels = getDriversPermitDtlModelsForDLRE(status,
					applicationEntity.getApplicationId(), uniqueKey, applicationNo, certificateModel, serviceType,
					model.getDriversPermitDetailsList().get(0).getDlNo());
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.addDriverPermitDetailsForDLRE(drDetailsModels, aadharNumber);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with saveUpdateDriversPermitDtlsForDLRE " + e.getMessage());
		}
		return new ResponseModel<String>(SaveUpdateResponse.FAILURE);
	}

	@Override
	@Transactional
	public ResponseModel<String> driverLicenceCommonService(Long applicationId, String aadharNumber,
			String serviceCode) {

		try {
			List<DriversLicenceDetailsModel> models = new ArrayList<DriversLicenceDetailsModel>();

			if (ServiceType.DL_BADGE.getCode().equalsIgnoreCase(serviceCode)) {
				DriversLicenceDetailsModel model = new DriversLicenceDetailsModel();
				ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
				RTAOfficeModel rtaOffModel = new RTAOfficeModel();
				rtaOffModel.setCode(appEntity.getRtaOfficeCode());
				model.setDlType(serviceCode);
				models.add(model);
			} else {
				// this is DL Renewal/ Expired block
				models = getDriversPermitDtlModelsForDLRenewal(applicationId);
			}
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.driverLicenceCommonService(aadharNumber, models);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with driverLicenceCommonService service code = " + serviceCode + "  "
					+ e.getMessage());
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	private List<DriversLicenceDetailsModel> getDriversEndorsmentsDtlModels(Status status, Long applicationId,
			String uniqueKey, CourseCertificateModel certificateModel) {
		ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
		List<LicensePermitDetailsEntity> permitDLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		List<DriversLicenceDetailsModel> permitDLModel = new ArrayList<>();
		List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO
				.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(), appEntity.getIteration());
		DriversLicenceDetailsModel model = null;
		String examResult = "F";
		if (Status.APPROVED == status) {
			examResult = "P";
		}
		for (LicensePermitDetailsEntity entity : permitDLEntities) {
			model = new DriversLicenceDetailsModel();
			model.setAppId(entity.getApplicationId().getApplicationNumber());
			model.setApplicationId(entity.getApplicationId().getApplicationNumber());
			model.setBadgeIssuedDate(null);
			model.setBadgeRtaOfficeCode(null);
			model.setDateOfFirstIssue(new Date());
			model.setDlIssuedDate(new Date());
			model.setDlType(ServiceType.DL_ENDORSMENT.getCode());
			model.setDlVehicleClassCode(entity.getVehicleClassCode());
			if (!ObjectsUtil.isNull(certificateModel)) {
				model.setDrivingSchoolLicenseNo(certificateModel.getCetificateNumber());
				model.setIsTrained("Y");
			}
			if (!ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
				for (SlotApplicationsEntity slotAppEntity : slotApplicationEntityList) {
					if (slotAppEntity.getSlotServiceType()
							.equals(vehicleClassTestsDAO.getTest(entity.getVehicleClassCode()).getTestType())) {
						model.setTestDate(DateUtil.getDatefromString(
								DateUtil.extractDateAsStringWithHyphen(slotAppEntity.getSlot().getScheduledDate())));
					}
				}
			}
			model.setLlrNo(uniqueKey);
			model.setModuleCd(null);
			model.setObservation(null);
			model.setPhotoAttachmentId(null);
			model.setPlannedValidFrom(new Date());
			model.setPlannedValidTo(DateUtil.addMonths(new Date(), SomeConstants.YEAR_20));
			model.setReferenceId(null);
			model.setRenewalFlag("N");
			model.setRetestFlag("N");
			model.setRetestReason(null);
			model.setSignAttachmentId(null);
			model.setStatusCode(SomeConstants.VALID);
			model.setStatusDate(new Date());
			model.setStatusRemarks("NA");
			model.setStatusValidFrom(new Date());
			model.setStatusUpdatedBy(null);
			model.setStatusValidTo(new Date());
			model.setTestExempted(String.valueOf(entity.getTestExempted()));
			model.setTestExemptedReason(entity.getTestExemptedReason());
			model.setTestId("NA");
			model.setTestResult(examResult);
			model.setTicketDetails(null);
			model.setValidFlg("Y");
			model.setValidFrom(new Date());
			// model.setValidTo(licenceService.getDLValidity(entity.getApplicationId().getApplicantDob()));
			model.setRtaOfficeDetails(new RTAOfficeModel());
			model.getRtaOfficeDetails().setCode(entity.getApplicationId().getRtaOfficeCode());
			permitDLModel.add(model);
		}
		return permitDLModel;
	}

	private LicenseHolderDtlsModel getLicenseHolderDtlsModel(LLRegistrationModel llModel, String aadharNumber,
			String rtaOfficeCode) {
		LicenseHolderDtlsModel lDtlsModel = new LicenseHolderDtlsModel();
		if (!ObjectsUtil.isNull(llModel)) {
			lDtlsModel.setAadhaarNo(aadharNumber);
			lDtlsModel.setEmail(llModel.getEmailId());
			lDtlsModel.setMobileNo(llModel.getMobileNo());
			lDtlsModel.setPresAddrCountryId(1l);
			lDtlsModel.setDistrictDetails(new DistrictModel());
			lDtlsModel.getDistrictDetails().setCode(llModel.getDistrictCode());
			lDtlsModel.setPresAddrDoorNo(llModel.getDoorNo());
			lDtlsModel.setMandalDetails(new MandalModel());
			lDtlsModel.getMandalDetails().setCode(llModel.getMandalCode());
			lDtlsModel.setPresAddrPinCode(String.valueOf(llModel.getPostOffice()));
			lDtlsModel.setStateDetails(new StateModel());
			lDtlsModel.getStateDetails().setCode(llModel.getStateCode());
			lDtlsModel.setRtaOfficeDetails(new RTAOfficeModel());
			lDtlsModel.getRtaOfficeDetails().setCode(rtaOfficeCode);
			lDtlsModel.setPresAddrStreet(llModel.getStreet());
			lDtlsModel.setPresAddrTown(llModel.getCity());
			lDtlsModel.setIsSameAsAadhaar(llModel.getIsSameAadhar());
			lDtlsModel.setQualificationDetails(new QualificationModel());
			lDtlsModel.getQualificationDetails().setCode(Integer.valueOf(llModel.getQualification()));
		}
		return lDtlsModel;
	}

	private List<LearnersPermitDtlModel> getLearnersPermitDtlModels(MedicalDetailsModel mDetailsModel,
			ServiceType serviceType, Long applicationId) {
		List<LicensePermitDetailsEntity> permitLLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		List<LearnersPermitDtlModel> permitLLModel = new ArrayList<>();
		LearnersPermitDtlModel model = null;
		String learnersLicence = ApplicationUtil.getLearnersLicenceFormat(permitLLEntities.get(0).getApplicationId());
		for (LicensePermitDetailsEntity entity : permitLLEntities) {
			model = new LearnersPermitDtlModel();
			model.setApplicationId(entity.getApplicationId().getApplicationNumber());
			model.setLlrVehicleClassCode(entity.getVehicleClassCode());
			// applicationOrigination,referenceId,signAttachmentId,statusDate,statusRemarks,ticketDetails
			// ,approvedAo,approvedMvi :: need to be added still
			model.setLlrNo(learnersLicence);
			model.setLlrNoType(serviceType.getCode());
			model.setTestDate(entity.getTestDate());
			model.setTestExempted(entity.getTestExempted());
			model.setTestExemptedReason(entity.getTestExemptedReason());
			model.setTestResult(entity.getTestResult());
			model.setTestId(String.valueOf(entity.getTestNoOfAttemp()));
			model.setValidFrom(new Date());
			model.setValidTo(DateUtil.addMonths(new Date(), SomeConstants.SIX));
			model.setRtaOfficeDetails(new RTAOfficeModel());
			model.getRtaOfficeDetails().setCode(entity.getApplicationId().getRtaOfficeCode());
			model.setPhotoAttachmentId(getAttachemts(applicationId));
			model.setApplicationOrigination("1.3X");
			model.setDateOfFirstIssue(null);
			model.setLlrIssuedt(new Date());
			model.setRetestFlag("N");
			model.setSignAttachmentId(null);
			model.setStatusDate(new Date());
			model.setReferenceId(null);
			model.setParentConsentAadhaarNo(entity.getParentConsentAadhaarNo());
			if (!ObjectsUtil.isNull(mDetailsModel)) {
				model.setMedicalFitnessType(MedicalFitnessType.DOCTOR_CERTIFICATE.getLabel());
				model.setMedicalPractionerCode(mDetailsModel.getRegistrationNumber());
			}
			permitLLModel.add(model);
		}
		return permitLLModel;
	}

	private List<LearnersPermitDtlModel> getLearnersPermitDtlModels(MedicalDetailsModel mDetailsModel,
			ServiceType serviceType, Long applicationId, String applicationNumber, Status status) {
		List<LicensePermitDetailsEntity> permitLLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		List<LearnersPermitDtlModel> permitLLModel = new ArrayList<>();
		LearnersPermitDtlModel model = null;
		String learnersLicence = "";
		if (SomeConstants.PASS.equalsIgnoreCase(permitLLEntities.get(0).getTestResult()) || Status.APPROVED == status) {
			learnersLicence = ApplicationUtil.getLearnersLicenceFormat(permitLLEntities.get(0).getApplicationId());
		}
		for (LicensePermitDetailsEntity entity : permitLLEntities) {
			model = new LearnersPermitDtlModel();
			model.setApplicationId(applicationNumber);
			model.setLlrVehicleClassCode(entity.getVehicleClassCode());
			// applicationOrigination,referenceId,signAttachmentId,statusDate,statusRemarks,ticketDetails
			// ,approvedAo,approvedMvi :: need to be added still
			model.setLlrNo(learnersLicence);
			model.setLlrNoType(serviceType.getCode());
			model.setTestDate(entity.getTestDate());
			model.setTestExempted(entity.getTestExempted());
			model.setTestExemptedReason(entity.getTestExemptedReason());
			model.setTestResult(entity.getTestResult());
			model.setTestId(String.valueOf(entity.getTestNoOfAttemp()));
			model.setValidFrom(new Date());
			model.setValidTo(DateUtil.addMonths(new Date(), SomeConstants.SIX));
			model.setPhotoAttachmentId(getAttachemts(applicationId));
			model.setApplicationOrigination("1.3X");
			model.setDateOfFirstIssue(null);
			model.setLlrIssuedt(new Date());
			model.setRetestFlag("N");
			model.setSignAttachmentId(null);
			model.setStatusDate(new Date());
			model.setReferenceId(null);
			model.setParentConsentAadhaarNo(entity.getParentConsentAadhaarNo());
			if (!ObjectsUtil.isNull(mDetailsModel)) {
				model.setMedicalFitnessType(MedicalFitnessType.DOCTOR_CERTIFICATE.getLabel());
				model.setMedicalPractionerCode(mDetailsModel.getRegistrationNumber());
			}
			model.setRtaOfficeDetails(new RTAOfficeModel());
			model.getRtaOfficeDetails().setCode(entity.getApplicationId().getRtaOfficeCode());
			permitLLModel.add(model);
		}
		return permitLLModel;
	}

	private List<DriversLicenceDetailsModel> getDriversPermitDtlModelsForDLRE(Status status, Long applicationId,
			String uniqueKey, String applicationNumver, CourseCertificateModel certificateModel,
			ServiceType serviceType, String dlNumber) {

		List<LicensePermitDetailsEntity> permitDLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		List<DriversLicenceDetailsModel> permitDLModel = new ArrayList<>();
		DriversLicenceDetailsModel model = null;
		String licenceNumber = "";
		String examResult = "F";
		ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
		List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO
				.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(), appEntity.getIteration());
		if (Status.APPROVED == status) {
			examResult = "P";
			if (StringsUtil.isNullOrEmpty(dlNumber)) {
				licenceNumber = drivingLicenseService.getAndUpdateDrivingLicenseSeries(
						permitDLEntities.get(0).getApplicationId().getRtaOfficeCode());
			}
		}
		for (LicensePermitDetailsEntity entity : permitDLEntities) {
			entity.setTestResult(examResult);
			licensePermitDetailsDAO.saveOrUpdate(entity);

			model = new DriversLicenceDetailsModel();
			model.setAppId("1.3X");
			model.setApplicationId(applicationNumver);
			model.setBadgeIssuedDate(null);
			model.setBadgeRtaOfficeCode(null);
			model.setDateOfFirstIssue(new Date());
			model.setDlIssuedDate(new Date());
			model.setDlNo(licenceNumber);
			model.setDlType(appEntity.getServiceCode());
			model.setDlVehicleClassCode(entity.getVehicleClassCode());
			if (!ObjectsUtil.isNull(certificateModel)) {
				model.setDrivingSchoolLicenseNo(certificateModel.getCetificateNumber());
				model.setIsTrained("Y");
			}
			if (!ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
				for (SlotApplicationsEntity slotAppEntity : slotApplicationEntityList) {
					if (slotAppEntity.getSlotServiceType()
							.equals(vehicleClassTestsDAO.getTest(entity.getVehicleClassCode()).getTestType())) {
						model.setTestDate(DateUtil.getDatefromString(
								DateUtil.extractDateAsStringWithHyphen(slotAppEntity.getSlot().getScheduledDate())));
					}
				}
			}
			model.setLlrNo(uniqueKey);
			model.setModuleCd(null);
			model.setObservation(null);
			model.setPhotoAttachmentId(null);
			model.setPlannedValidFrom(new Date());
			model.setPlannedValidTo(new Date());
			model.setReferenceId(null);
			model.setRenewalFlag("N");
			model.setRetestFlag("N");
			model.setRetestReason(null);
			model.setSignAttachmentId(null);
			model.setStatusCode(SomeConstants.VALID);
			model.setStatusDate(new Date());
			model.setStatusRemarks("NA");
			model.setStatusValidFrom(new Date());
			model.setStatusUpdatedBy(null);
			model.setStatusValidTo(new Date());
			model.setTestExempted(String.valueOf(entity.getTestExempted()));
			model.setTestExemptedReason(entity.getTestExemptedReason());
			model.setTestId("NA");
			model.setTestResult(entity.getTestResult());
			model.setTicketDetails(null);
			model.setValidFlg("Y");
			model.setValidFrom(new Date());
			model.setValidTo(licenceService.getDLValidity(entity.getApplicationId().getApplicantDob()));
			Map<String, Object> map = new HashMap<String, Object>();
			Long userId = (Long) map.get("userId");
			model.setMviUserDetails(new UserModel());
			model.getMviUserDetails().setUserId(userId);
			model.setRtaOfficeDetails(new RTAOfficeModel());
			model.getRtaOfficeDetails().setCode(entity.getApplicationId().getRtaOfficeCode());
			permitDLModel.add(model);
		}
		return permitDLModel;
	}

	private List<DriversLicenceDetailsModel> getDriversPermitDtlModelsForDLRenewal(Long applicationId) {

		List<LicensePermitDetailsEntity> permitDLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		List<DriversLicenceDetailsModel> permitDLModel = new ArrayList<>();
		DriversLicenceDetailsModel model = null;
		for (LicensePermitDetailsEntity entity : permitDLEntities) {
			model = new DriversLicenceDetailsModel();
			model.setApplicationId(entity.getApplicationId().getApplicationNumber());
			model.setDlType(entity.getApplicationId().getServiceCode());
			model.setDlVehicleClassCode(entity.getVehicleClassCode());
			model.setRenewalFlag("Y");
			model.setRetestFlag("N");
			model.setValidFlg("Y");
			model.setValidFrom(new Date());
			LicenceVehicleClass vehicleClassType = LicenceVehicleClass
					.getLicenceVehicleClassByCode(entity.getVehicleClassCode().trim());
			if (!ObjectsUtil.isNull(vehicleClassType)) {
				model.setTransportType(vehicleClassType.getTransportType());
				if (LicenceVehicleClass.HAZARDDOUS_GOODS_CARRIAGE.getCode()
						.equalsIgnoreCase(entity.getVehicleClassCode())) {
					model.setValidTo(DateUtil.addYears(new Date(), SomeConstants.ONE));
				} else if (SomeConstants.TRANSPORT.equalsIgnoreCase(vehicleClassType.getTransportType())) {
					model.setValidTo(DateUtil.addYears(new Date(), SomeConstants.THREE));
				} else {
					model.setValidTo(DateUtil.addYears(new Date(), SomeConstants.FIVE));

				}
			}
			permitDLModel.add(model);
		}
		return permitDLModel;
	}

	private Long getAttachemts(Long applicationId) {
		AttachmentEntity attachmentEntity = null;
		try {
			attachmentEntity = attachmentDAO.getAttachmentDetails(SomeConstants.ONE, applicationId);
		} catch (Exception e) {
		}
		return ObjectsUtil.isNull(attachmentEntity) ? null : attachmentEntity.getAttachmentDlId();
	}

	@Override
	@Transactional
	public ResponseModel<String> updateInLicenseHolderDetails(Long sessionId, LLRegistrationModel model) {

		ResponseModel<String> responseModel = new ResponseModel<String>(ResponseModel.SUCCESS);
		UserSessionEntity userSessionEntity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
		try {
			LicenseHolderDtlsModel licenseHolderDtlsModel = new LicenseHolderDtlsModel();
			licenseHolderDtlsModel.setAadhaarNo(userSessionEntity.getAadharNumber());
			if (!StringsUtil.isNullOrEmpty(model.getMobileNo())) {
				licenseHolderDtlsModel.setMobileNo(model.getMobileNo());
			}
			if (!StringsUtil.isNullOrEmpty(model.getEmailId())) {
				licenseHolderDtlsModel.setEmail(model.getEmailId());
			}
			if (!StringsUtil.isNullOrEmpty(model.getBloodGroup())) {
				licenseHolderDtlsModel.setBloodGrp(model.getBloodGroup());
			}
			if (!ObjectsUtil.isNull(model.getQualification())) {
				licenseHolderDtlsModel.setQualificationDetails(new QualificationModel());
				licenseHolderDtlsModel.getQualificationDetails().setCode(Integer.valueOf(model.getQualification()));
			}
			RegLicenseServiceResponseModel<String> responseBody = registrationLicenseService
					.updateInLicenseHolderDetails(userSessionEntity.getServiceCode(), licenseHolderDtlsModel);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return responseModel;
			}
		} catch (Exception e) {
			responseModel.setStatus(ResponseModel.FAILED);
		}
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<String> suspendCancelLicense(Long applicationId, String aadharNumber) {
		SupensionCancellationModel supensionCancellationModel = new SupensionCancellationModel();
		ApplicationFormDataEntity formEntity = applicationFormDataDAO.getApplicationFormData(applicationId,
				FormCodeType.DLSC_FORM.getLabel());
		ObjectMapper mapper = new ObjectMapper();
		try {
			supensionCancellationModel = mapper.readValue(formEntity.getFormData(), SupensionCancellationModel.class);

			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.suspendCancelLicense(formEntity.getApplicationEntity().getLoginHistory().getUniqueKey(),
							supensionCancellationModel);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with suspendCancelLicense " + e.getMessage());
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	/**
	 * @author neeraj.maletia
	 * @description DL suspension by AO
	 * 
	 */
	@Override
	@Transactional
	public ResponseModel<String> suspendCancelLicenseByAO(SupensionCancellationModel supensionCancellationModel,
			UserSessionEntity usEntity) {
		try {
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.suspendCancelLicense(usEntity.getUniqueKey(), supensionCancellationModel);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				/*UserSessionEntity uSessionEntity = userSessionDAO.getUserSession(usEntity.getSessionId());
				uSessionEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
				uSessionEntity.setCompletionStatus(Status.APPROVED.getValue());
				userSessionDAO.saveOrUpdate(uSessionEntity);*/
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with suspendCancelLicense " + e.getMessage());
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	@Override
	@Transactional
	public ResponseModel<String> licenseRevokeSuspension(Long applicationId, String aadharNumber) {
		SuspensionRevocationModel suspensionRevocationModel = new SuspensionRevocationModel();
		ApplicationFormDataEntity formEntity = applicationFormDataDAO.getApplicationFormData(applicationId,
				FormCodeType.DLC_FORM.getLabel());
		ObjectMapper mapper = new ObjectMapper();
		try {
			suspensionRevocationModel = mapper.readValue(formEntity.getFormData(), SuspensionRevocationModel.class);

			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.licenseRevokeSuspension(formEntity.getApplicationEntity().getLoginHistory().getUniqueKey(),
							suspensionRevocationModel);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("Getting error with licenseRevokeSuspension " + e.getMessage());
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	// TODO sohan
	@Override
	@Transactional
	public Map<String, Object> getEmployeeUserId(Long applicationId, ServiceType serviceType) {
		List<String> userRoles = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
		switch (serviceType) {

		case DL_BADGE:
			break;
		case DL_CHANGEADDRS_OS:
			break;
		case DL_CHANGE_ADDRESS:
			break;
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
		case DL_FRESH:
			break;
		case DL_INT_PERMIT: {
			userRoles.add(UserType.ROLE_RTO.toString());
			userRoles.add(UserType.ROLE_AO.toString());
		}
			break;
		case DL_MILITRY: {
			userRoles.add(UserType.ROLE_RTO.toString());
			userRoles.add(UserType.ROLE_AO.toString());
		}
			break;
		case DL_RENEWAL:
			break;
		case DL_RETEST:
			break;
		case DL_SURRENDER:
			break;
		case DL_REVO_SUS:
			break;
		case LL_DUPLICATE:
			break;
		case LL_ENDORSEMENT:
			break;
		case LL_FRESH: {
			userRoles.add(UserType.ROLE_RTO.toString());
			userRoles.add(UserType.ROLE_AO.toString());
		}
			break;
		case LL_RETEST: {
			userRoles.add(UserType.ROLE_MVI.toString());
		}
			break;
		default:
			break;
		}
		List<ApplicationApprovalHistoryEntity> approvalHistoryEntities = applicationApprovalHistoryDAO
				.getApprovalHistories(applicationId, Status.APPROVED, userRoles);
		for (ApplicationApprovalHistoryEntity appApproval : approvalHistoryEntities) {
			map.put("userId", appApproval.getRtaUserId());
			map.put("userRole", appApproval.getRtaUserRole());
			map.put("comment", appApproval.getComments());
			break;
		}
		return map;
	}

}

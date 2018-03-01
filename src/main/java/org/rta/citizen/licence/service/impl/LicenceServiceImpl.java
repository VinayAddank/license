package org.rta.citizen.licence.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;

import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.converters.AadharLicenseHolderConverter;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.AttachmentDAO;
import org.rta.citizen.common.dao.RtaOfficeIPAddressDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.AttachmentEntity;
import org.rta.citizen.common.entity.RtaOfficeIPAddressEntity;
import org.rta.citizen.common.entity.StringJsonUserType;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.PaymentType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.ExaminerFoundException;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.SlotBookingException;
import org.rta.citizen.common.exception.TaskNotFound;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.DealerModel;
import org.rta.citizen.common.model.DistrictModel;
import org.rta.citizen.common.model.MandalModel;
import org.rta.citizen.common.model.QualificationModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.StateModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.service.AuthenticationServiceFactory;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.service.rtaapplication.RtaApplicationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.converters.AgeGroupConverter;
import org.rta.citizen.licence.converters.OptionsConverter;
import org.rta.citizen.licence.converters.QuestionConverter;
import org.rta.citizen.licence.converters.QuestionnaireFeedbackConverter;
import org.rta.citizen.licence.dao.LicenceDAO;
import org.rta.citizen.licence.dao.QuestionnaireDAO;
import org.rta.citizen.licence.dao.VehicleClassTestsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsHistoryDAO;
import org.rta.citizen.licence.dao.updated.QuestionnaireFeedbackDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.LlrVehicleClassRefEntity;
import org.rta.citizen.licence.entity.tests.OptionsEntity;
import org.rta.citizen.licence.entity.tests.QuestionEntity;
import org.rta.citizen.licence.entity.tests.QuestionnaireFeedbackEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsHistoryEntity;
import org.rta.citizen.licence.enums.LicenceVehicleClass;
import org.rta.citizen.licence.enums.LicenseType;
import org.rta.citizen.licence.enums.MedicalFitnessType;
import org.rta.citizen.licence.model.ClassofVechicleModel;
import org.rta.citizen.licence.model.LLRAgeGroupModel;
import org.rta.citizen.licence.model.LicenceDetailsModel;
import org.rta.citizen.licence.model.LlrRetestDetailsRequestModel;
import org.rta.citizen.licence.model.tests.OptionModel;
import org.rta.citizen.licence.model.tests.QuestionModel;
import org.rta.citizen.licence.model.updated.DLMilataryDetailsModel;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.ExamResultModel;
import org.rta.citizen.licence.model.updated.InternationalPermitModel;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.model.updated.LicenseIDPDtlsModel;
import org.rta.citizen.licence.model.updated.MedicalDetailsModel;
import org.rta.citizen.licence.model.updated.QuestionnaireFeedbackModel;
import org.rta.citizen.licence.service.LicenceService;
import org.rta.citizen.licence.service.updated.DrivingLicenseService;
import org.rta.citizen.licence.service.updated.LicenseSyncingService;
import org.rta.citizen.licence.utils.ApplicationUtil;
import org.rta.citizen.slotbooking.dao.SlotDAO;
import org.rta.citizen.slotbooking.entity.SlotApplicationsEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LicenceServiceImpl implements LicenceService {

	private static final Logger log = Logger.getLogger(LicenceServiceImpl.class);

	@Autowired
	private LicenceDAO licenceDAO;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Autowired
	private AttachmentDAO attachmentDAO;

	@Autowired
	private AgeGroupConverter ageGroupConverter;

	@Autowired
	private SlotDAO slotDAO;

	@Autowired
	private AuthenticationServiceFactory serviceFactory;

	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private LicensePermitDetailsDAO licensePermitDetailsDAO;

	@Value("${activiti.citizen.task.code.exam}")
	private String examTask;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${base.php.url}")
	private String basePhpUrl;

	@Autowired
	private QuestionnaireDAO questionnaireDAO;

	@Autowired
	private QuestionConverter questionConverter;

	@Autowired
	private OptionsConverter optionsConverter;

	@Autowired
	private VehicleClassTestsDAO vehicleClassTestsDAO;

	@Autowired
	private RtaApplicationService rtaApplicationService;

	@Autowired
	private QuestionnaireFeedbackDAO questionnaireFeedbackDAO;

	@Autowired
	private QuestionnaireFeedbackConverter questionnaireFeedbackConverter;

	@Autowired
	private DrivingLicenseService drivingLicenseService;

	@Autowired
	private LicenseSyncingService licenseSyncingService;

	@Autowired
	private RtaOfficeIPAddressDAO rtaOfficeIPAddressDAO;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private TransactionDetailDAO transactionDetailDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private AadharLicenseHolderConverter aadharLicenseHolderConverter;

	@Autowired
	private LicensePermitDetailsHistoryDAO licensePermitDetailsHistoryDAO;

	@Override
	public LLRAgeGroupModel getAgeGroup(Long sessionId) throws Exception {
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		return ageGroupConverter
				.convertToModel(licenceDAO.getAgeGroup(DateUtil.getCurrentAge(appEntity.getApplicantDob())));
	}

	@Override
	public List<ClassofVechicleModel> getCOV(String ageGroup, String serviceCode) {

		log.info("::::::::COV Service Start ::::::::::");
		List<ClassofVechicleModel> llrclassofvechicledetailsmodel = new ArrayList<ClassofVechicleModel>();
		ClassofVechicleModel llrclassofvechicledetails = new ClassofVechicleModel();
		List<LlrVehicleClassRefEntity> list = new ArrayList<>();
		LlrVehicleClassMasterEntity llrclassofvechiclemodel = null;
		try {
			list = licenceDAO.getCOV(ageGroup);
			for (LlrVehicleClassRefEntity llrmodel : list) {
				if (ServiceType.LL_FRESH.getCode().equalsIgnoreCase(serviceCode)
						&& (LicenceVehicleClass.HEAVY_GOODS_VEHICLE.getCode()
								.equalsIgnoreCase(llrmodel.getVehicle_class())
								|| LicenceVehicleClass.HEAVY_PASSENGER_VEHICLE.getCode()
										.equalsIgnoreCase(llrmodel.getVehicle_class())
								|| LicenceVehicleClass.HEAVY_TRANSPORT_VEHICLE.getCode()
										.equalsIgnoreCase(llrmodel.getVehicle_class())
								|| LicenceVehicleClass.MEDIUM_GOODS_VEHICLE.getCode()
										.equalsIgnoreCase(llrmodel.getVehicle_class())
								|| LicenceVehicleClass.MEDIUM_PASSENGER_VEHICLE.getCode()
										.equalsIgnoreCase(llrmodel.getVehicle_class())
								|| LicenceVehicleClass.HAZARDDOUS_GOODS_CARRIAGE.getCode()
										.equalsIgnoreCase(llrmodel.getVehicle_class()))) {
					llrclassofvechiclemodel = null;
				} else {
					llrclassofvechiclemodel = licenceDAO.getDetailCOV(llrmodel.getVehicle_class());
				}
				if (null != llrclassofvechiclemodel) {
					llrclassofvechicledetails = new ClassofVechicleModel();
					llrclassofvechicledetails.setCovDescription(llrclassofvechiclemodel.getVehicleClassDescription());
					llrclassofvechicledetails.setCovCode(llrclassofvechiclemodel.getVehicleClass());
					llrclassofvechicledetails.setAgeGroup(llrmodel.getAge_group_cd());
					llrclassofvechicledetails.setVehicleClassType(llrclassofvechiclemodel.getVehicleTransportType());
					llrclassofvechicledetailsmodel.add(llrclassofvechicledetails);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return llrclassofvechicledetailsmodel;
	}

	@Override
	@Transactional
	public List<LicenceDetailsModel> getLlrDetails(Long sessionId, String serviceType) {
		List<LicenceDetailsModel> licenceDetailsModellist = new ArrayList<LicenceDetailsModel>();
		LicenceDetailsModel LicenceDetailsModel = new LicenceDetailsModel();
		UserSessionEntity usEntity = userSessionDAO.getUserSession(sessionId);
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails;
		try {
			holderDetails = registrationLicenseService.getLicenseHolderDtls(usEntity.getAadharNumber(), null,
					usEntity.getUniqueKey());
			if (null != holderDetails) {
				LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
				if (null != holder) {
					LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
					List<LearnersPermitDtlModel> permitDetails = holder.getLearnersPermitDetailsList();
					LearnersPermitDtlModel permitDetail = permitDetails.get(0);
					if (serviceType.equalsIgnoreCase(ServiceType.LL_DUPLICATE.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.LL_ENDORSEMENT.getCode())) {
						ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
						LicenceDetailsModel.setApplication_id(appEntity.getApplicationNumber());

						LicenceDetailsModel.setName(holderDetail.getDisplayName());
						LicenceDetailsModel.setCoName(holderDetail.getGuardianName());
						LicenceDetailsModel.setBloodGroup(holderDetail.getBloodGrp());
						LicenceDetailsModel.setNationality(holderDetail.getNationality());
						LicenceDetailsModel.setPres_addr_country_id(holderDetail.getPermAddrCountry());
						LicenceDetailsModel.setPres_addr_district_id(holderDetail.getDistrictDetails().getId());
						LicenceDetailsModel.setPres_addr_mandal_id(holderDetail.getMandalDetails().getId());
						LicenceDetailsModel.setPres_addr_state_id(holderDetail.getStateDetails().getId());
						LicenceDetailsModel.setDob(DateUtil.getDateInString(holderDetail.getDateOfBirth()));
						LicenceDetailsModel.setPres_addr_street(holderDetail.getPresAddrStreet());
						LicenceDetailsModel.setPres_addr_town(holderDetail.getPresAddrTown());
						LicenceDetailsModel.setPres_addr_door_no(holderDetail.getPresAddrDoorNo());
						LicenceDetailsModel.setCovValidity(permitDetail.getValidTo());
						LicenceDetailsModel.setIssuedDate(permitDetail.getLlrIssuedt());
						LicenceDetailsModel.setIssuedBy(permitDetail.getRtaOfficeDetails().getCode());
						LicenceDetailsModel.setRefLA(permitDetail.getRtaOfficeDetails().getCode());
						LicenceDetailsModel.setLlr_no(permitDetail.getLlrNo());
						LicenceDetailsModel.setRefNo(null);
						LicenceDetailsModel.setChallanDetailsList(null);
						LicenceDetailsModel.setCrimeDetailsList(null);
						log.info("::::::::: LLR Details End ::::::::::");
						licenceDetailsModellist.add(LicenceDetailsModel);
					}
				}
			}
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		return licenceDetailsModellist;
	}

	@Override
	@Transactional
	public List<LlrRetestDetailsRequestModel> llRetestDetails(Long sessionId) {
		List<LlrRetestDetailsRequestModel> llrretestdetailsrequestmodelList = new ArrayList<LlrRetestDetailsRequestModel>();
		LlrRetestDetailsRequestModel llrretestdetailsrequestmodel = new LlrRetestDetailsRequestModel();
		try {
			UserSessionEntity usEntity = userSessionDAO.getUserSession(sessionId);
			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
					.getLicenseHolderDtls(usEntity.getAadharNumber(), null, usEntity.getUniqueKey());
			if (null != holderDetails) {
				LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
				if (null != holder) {
					LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
					List<LearnersPermitDtlModel> permitDetails = holder.getLearnersPermitDetailsList();
					LearnersPermitDtlModel permitDetail = permitDetails.get(0);

					llrretestdetailsrequestmodel.setDisplayName(holderDetail.getDisplayName());
					llrretestdetailsrequestmodel.setPreviousTestDate(permitDetail.getTestDate().toString());

					llrretestdetailsrequestmodel.setBloodGroup(holderDetail.getBloodGrp());
					llrretestdetailsrequestmodel.setMobileNumber(holderDetail.getMobileNo());
					llrretestdetailsrequestmodel.setEmail(holderDetail.getEmail());
					llrretestdetailsrequestmodel
							.setQualification(String.valueOf(holderDetail.getQualificationDetails().getName()));
					llrretestdetailsrequestmodel.setDoorNo(holderDetail.getPermAddrDoorNo());
					llrretestdetailsrequestmodel.setStreet(holderDetail.getPermAddrStreet());
					llrretestdetailsrequestmodel.setTownCity(holderDetail.getPermAddrTown());
					llrretestdetailsrequestmodel.setMandalName(holderDetail.getMandalDetails().getName());
					llrretestdetailsrequestmodel.setDistrict(holderDetail.getDistrictDetails().getCode());
					llrretestdetailsrequestmodel.setState(holderDetail.getStateDetails().getCode());
					llrretestdetailsrequestmodel.setCountry(holderDetail.getPermAddrCountry());
					llrretestdetailsrequestmodel.setPincode(holderDetail.getPermAddrPinCode());

					llrretestdetailsrequestmodelList.add(llrretestdetailsrequestmodel);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return llrretestdetailsrequestmodelList;
	}

	@Override
	@Transactional
	public List<LicenceDetailsModel> getDLDetails(Long sessionId, String serviceType) {
		List<LicenceDetailsModel> licenceDetailsModellist = new ArrayList<LicenceDetailsModel>();
		LicenceDetailsModel LicenceDetailsModel = new LicenceDetailsModel();
		UserSessionEntity usEntity = userSessionDAO.getUserSession(sessionId);
		UserSessionEntity sessionEntity = userSessionDAO.getActiveSession(usEntity.getAadharNumber(),
				usEntity.getUniqueKey(), usEntity.getKeyType());
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails;
		try {
			holderDetails = registrationLicenseService.getLicenseHolderDtls(sessionEntity.getAadharNumber(), null,
					sessionEntity.getUniqueKey());
			if (serviceType.equalsIgnoreCase(ServiceType.DL_CHANGE_ADDRESS.getCode())
					|| serviceType.equalsIgnoreCase(ServiceType.DL_BADGE.getCode())
					|| serviceType.equalsIgnoreCase(ServiceType.DL_DUPLICATE.getCode())
					|| serviceType.equalsIgnoreCase(ServiceType.DL_EXPIRED.getCode())
					|| serviceType.equalsIgnoreCase(ServiceType.DL_MILITRY.getCode())
					|| serviceType.equalsIgnoreCase(ServiceType.DL_CHANGEADDRS_OS.getCode())
					|| serviceType.equalsIgnoreCase(ServiceType.DL_SURRENDER.getCode())) {
				holderDetails = registrationLicenseService
						.getLicenseHolderDtlsForDriver(sessionEntity.getAadharNumber(), sessionEntity.getUniqueKey());
			}
			if (serviceType.equalsIgnoreCase(ServiceType.DL_REVO_SUS.getCode())) {
				holderDetails = registrationLicenseService
						.getLicenseHolderDtlsForDriver(sessionEntity.getAadharNumber(), sessionEntity.getUniqueKey());
			}
			if (null != holderDetails) {
				LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
				if (null != holder) {
					LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
					List<LearnersPermitDtlModel> permitDetails = holder.getLearnersPermitDetailsList();
					LearnersPermitDtlModel permitDetail = permitDetails.get(0);
					if (serviceType.equalsIgnoreCase(ServiceType.DL_FRESH.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_RETEST.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_ENDORSMENT.getCode())) {
						LicenceDetailsModel.setCovValidity(permitDetail.getValidTo());
						LicenceDetailsModel.setIssuedDate(permitDetail.getLlrIssuedt());
						LicenceDetailsModel.setIssuedBy(permitDetail.getRtaOfficeDetails().getCode());
						LicenceDetailsModel.setRefLA(permitDetail.getRtaOfficeDetails().getCode());
						LicenceDetailsModel.setLlr_no(permitDetail.getLlrNo());

					}
					if (serviceType.equalsIgnoreCase(ServiceType.DL_CHANGE_ADDRESS.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_BADGE.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_DLINFO.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_DUPLICATE.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_INT_PERMIT.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_RENEWAL.getCode())
							|| serviceType.equalsIgnoreCase(ServiceType.DL_EXPIRED.getCode())) {

					}
					ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
					LicenceDetailsModel.setCovValidity(permitDetail.getValidTo());
					LicenceDetailsModel.setIssuedDate(permitDetail.getLlrIssuedt());
					LicenceDetailsModel.setIssuedBy(permitDetail.getRtaOfficeDetails().getCode());
					LicenceDetailsModel.setRefLA(permitDetail.getRtaOfficeDetails().getCode());
					LicenceDetailsModel.setDl_no(sessionEntity.getUniqueKey());
					LicenceDetailsModel.setApplication_id(appEntity.getApplicationNumber());
					LicenceDetailsModel.setName(holderDetail.getDisplayName());
					LicenceDetailsModel.setCoName(holderDetail.getGuardianName());
					LicenceDetailsModel.setBloodGroup(holderDetail.getBloodGrp());
					LicenceDetailsModel.setNationality(holderDetail.getNationality());
					LicenceDetailsModel.setPres_addr_country_id(holderDetail.getPermAddrCountry());
					LicenceDetailsModel.setPres_addr_district_id(holderDetail.getDistrictDetails().getId());
					LicenceDetailsModel.setPres_addr_mandal_id(holderDetail.getMandalDetails().getId());
					LicenceDetailsModel.setPres_addr_state_id(holderDetail.getStateDetails().getId());
					LicenceDetailsModel.setDob(DateUtil.getDateInString(holderDetail.getDateOfBirth()));
					LicenceDetailsModel.setPres_addr_street(holderDetail.getPresAddrStreet());
					LicenceDetailsModel.setPres_addr_town(holderDetail.getPresAddrTown());
					LicenceDetailsModel.setPres_addr_door_no(holderDetail.getPresAddrDoorNo());
					LicenceDetailsModel.setRefNo(null);
					LicenceDetailsModel.setChallanDetailsList(null);
					LicenceDetailsModel.setCrimeDetailsList(null);
					log.info("::::::::: LLR Details End ::::::::::");
					licenceDetailsModellist.add(LicenceDetailsModel);
				}
			}
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		return licenceDetailsModellist;
	}

	@Override
	@Transactional
	public LicenseHolderPermitDetails getLicenseDetails(Long sessionId, boolean isAll) {

		ApplicationEntity entity = applicationDAO.getApplicationFromSession(sessionId);
		List<String> covs = new ArrayList<>();
		UserSessionEntity usEntity = entity.getLoginHistory();
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails;
		LicenseHolderPermitDetails licenseHolderPermitDetails = null;
		try {
			if (usEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_MILITRY.getCode())) {
				RegistrationServiceResponseModel<AadharModel> response = registrationService
						.getAadharDetails(Long.parseLong(entity.getCreatedBy()));
				LicenseHolderPermitDetails licenseHldrPrmtDtls = new LicenseHolderPermitDetails();
				licenseHldrPrmtDtls.setApplicationNo(entity.getApplicationNumber());
				if (response.getHttpStatus() == HttpStatus.OK) {
					AadharModel aadharModel = response.getResponseBody();
					LicenseHolderDtlsModel HolderDtlsModel = aadharLicenseHolderConverter
							.convertToLicenseHolderDtlsModel(aadharModel);
					licenseHldrPrmtDtls.setLicenseHolderDetails(HolderDtlsModel);
				}
				return licenseHldrPrmtDtls;
			} else if (usEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_ENDORSEMENT.getCode())) {
				holderDetails = registrationLicenseService.getLicenseHolderDtls(usEntity.getAadharNumber(), null,
						usEntity.getUniqueKey());
				List<DriversLicenceDetailsModel> driverDetailsList = holderDetails.getResponseBody()
						.getDriversPermitDetailsList();
				if (!ObjectsUtil.isNullOrEmpty(driverDetailsList)) {
					Iterator<DriversLicenceDetailsModel> dlIterator = driverDetailsList.iterator();
					while (dlIterator.hasNext()) {
						DriversLicenceDetailsModel driverDetailsModel = dlIterator.next();
						if (StringsUtil.isNullOrEmpty(driverDetailsModel.getDlNo())) {
							dlIterator.remove();
						}
					}
				}
			} else {
				holderDetails = registrationLicenseService.getLicenseHolderDtlsForDriver(usEntity.getAadharNumber(),
						"");
				if (usEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_ENDORSMENT.getCode())) {
					UserSessionEntity userSesEntity = userSessionDAO.getLatestUserSession(usEntity.getAadharNumber(),
							null, null, ServiceType.LL_ENDORSEMENT, Status.APPROVED);
					Long appId = applicationDAO.getApplicationFromSession(userSesEntity.getSessionId())
							.getApplicationId();
					List<LicensePermitDetailsEntity> licensePermitDetailsEntities = licensePermitDetailsDAO
							.getLicensePermitDetails(appId);
					for (LicensePermitDetailsEntity lEntity : licensePermitDetailsEntities) {
						covs.add(lEntity.getVehicleClassCode());
					}
					List<LearnersPermitDtlModel> learnersPermitDetailsList = holderDetails.getResponseBody()
							.getLearnersPermitDetailsList();
					Iterator<LearnersPermitDtlModel> llrIterator = learnersPermitDetailsList.iterator();
					while (llrIterator.hasNext()) {
						LearnersPermitDtlModel learnersPermitDtlModel = llrIterator.next();
						if (!covs.contains(learnersPermitDtlModel.getLlrVehicleClassCode())) {
							llrIterator.remove();
						}
					}
				} else if (usEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_RETEST.getCode())) {
					List<DriversLicenceDetailsModel> driverDetailsList = holderDetails.getResponseBody()
							.getDriversPermitDetailsList();
					Iterator<DriversLicenceDetailsModel> dlIterator = driverDetailsList.iterator();
					while (dlIterator.hasNext()) {
						DriversLicenceDetailsModel driverDetailsModel = dlIterator.next();
						if (StringsUtil.isNullOrEmpty(driverDetailsModel.getDlNo())) {
							dlIterator.remove();
						}
					}
				} else if (usEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_EXPIRED.getCode())) {
					List<DriversLicenceDetailsModel> driverDetailsList = holderDetails.getResponseBody()
							.getDriversPermitDetailsList();
					List<LicensePermitDetailsEntity> licensePermitDetailsEntities = licensePermitDetailsDAO
							.getLicensePermitDetails(entity.getApplicationId());
					for (LicensePermitDetailsEntity lEntity : licensePermitDetailsEntities) {
						covs.add(lEntity.getVehicleClassCode());
					}
					Iterator<DriversLicenceDetailsModel> dlIterator = driverDetailsList.iterator();
					while (dlIterator.hasNext()) {
						DriversLicenceDetailsModel driverDetailsModel = dlIterator.next();
						if (!covs.contains(driverDetailsModel.getDlVehicleClassCode())) {
							dlIterator.remove();
						}
					}
				}
			}
			if (holderDetails.getHttpStatus() == HttpStatus.OK) {
				licenseHolderPermitDetails = holderDetails.getResponseBody();
				licenseHolderPermitDetails.setApplicationNo(entity.getApplicationNumber());
				List<DriversLicenceDetailsModel> validCovs = licenseHolderPermitDetails.getDriversPermitDetailsList();
				List<DriversLicenceDetailsModel> expiredCOV = new ArrayList<>();
				if (!ObjectsUtil.isNullOrEmpty(validCovs)
						&& (!usEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_RENEWAL.getCode())
								|| !usEntity.getServiceCode().equalsIgnoreCase(ServiceType.DL_EXPIRED.getCode()))) {
					for (DriversLicenceDetailsModel model : validCovs) {
						if (model.getValidFlg().equalsIgnoreCase("Y") && model.getValidTo() != null
								&& model.getValidTo().before(new Date())) {
							expiredCOV.add(model);
						}
					}
					if (expiredCOV != null && expiredCOV.size() > 0) {
						validCovs.removeAll(expiredCOV);
					}
					licenseHolderPermitDetails.setDriversPermitDetailsList(validCovs);
				}
				return licenseHolderPermitDetails;
			}
		} catch (Exception e) {
			log.error("getting some things missing");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional
	public Integer getLLFAttempts(String appNo) {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		UserSessionEntity session = appEntity.getLoginHistory();
		List<UserSessionEntity> sessions = userSessionDAO.getAppliedSessions(session.getAadharNumber(),
				session.getServiceCode());
		return sessions.size();
	}

	@Override
	@Transactional
	public Boolean saveExamResults(ExamResultModel examResultModel, CitizenApplicationModel appModel) {
		try {
			if (null != appModel) {
				Long appId = appModel.getAppId();

				if (ServiceType.LL_RETEST == appModel.getServiceType()) {
					UserSessionEntity userSession = userSessionDAO.getUserSession(appModel.getSessionId());
					if (null != userSession) {
						ApplicationEntity appEntity = applicationDAO.getApplication(userSession.getUniqueKey());
						appId = appEntity.getApplicationId();
					} else {
						log.info("Exception in Updating Exam Results. Can get RETEST user session for exam:"
								+ examResultModel.getApplicationNo());
						return false;
					}
				}
				List<LicensePermitDetailsEntity> licensePermitDetails = licensePermitDetailsDAO
						.getLicensePermitDetails(appId);
				for (LicensePermitDetailsEntity licensePermitDetail : licensePermitDetails) {
					licensePermitDetail
							.setTestDate(new SimpleDateFormat("dd/MM/yyyy").parse(examResultModel.getTestDate()));
					licensePermitDetail.setTestResult(examResultModel.getResult());
					// licensePermitDetail.setTestExemptedReason();
					// licensePermitDetail.setTestExempted();
					licensePermitDetail.setTestMarks(String.valueOf(examResultModel.getMarks()));
					licensePermitDetailsDAO.saveOrUpdate(licensePermitDetail);
				}
				return true;
			} else {
				log.info("Exception in Updating Exam Results. Application Model passed is null for exam:"
						+ examResultModel.getApplicationNo());
				return false;
			}
		} catch (Exception e) {
			log.error("Exception in Updating Exam Results in LicensePermitDetailsEntity:" + e.getMessage());
			return false;
		}
	}

	private String getPhoto(String sources) {
		disableSSLVerification();
		log.info(":::::::::getOwnerPhoto::::::::::start::::::for source:" + sources);
		String imageDataString = null;
		try {
			javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession sslSession) {
					if (basePhpUrl.contains(hostname)) {
						log.info(":::::::::VERIFY HOST for SSL ::::::::::::::");
						return true;
					}
					return false;
				}
			});
			sources = basePhpUrl + sources;
			log.info(":::::::::Generate Base64 from complete image URL:" + sources);
			if (sources.endsWith(".pdf")) {
				log.info(
						":::::::::PDF found instead of image. Now changing it for +flow . PHP should change image source logic. Now URL is:"
								+ sources);
				sources.replace(".pdf", ".jpg");
			}
			URL url = new URL(sources);
			BufferedImage bufferedImage = ImageIO.read(url);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpg", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			imageDataString = Base64.encodeBase64String(imageInByte);
		} catch (Exception ex) {
			log.info(":::::::::getOwnerPhoto::::::::::Exception::::::" + ex.getMessage());
			ex.printStackTrace();
		}
		log.info(":::::::::getOwnerPhoto::::::::::end::::::");
		return imageDataString;
	}

	@Override
	@Transactional
	public ResponseModel<String> saveUpdateLicenseHolderDtls(Long applicationId, String aadharNumber) {
		MedicalDetailsModel mDetailsModel = new MedicalDetailsModel();
		List<LearnersPermitDtlModel> learnersPermitDtlModelList = null;
		LicenseHolderDtlsModel licenseHolderDtlsModel = null;
		List<DriversLicenceDetailsModel> driversLicenceDetailsModels = null;
		ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
		List<ApplicationFormDataEntity> formsEntity = applicationFormDataDAO.getAllApplicationFormData(applicationId);
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (ServiceType.LL_FRESH.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
				LLRegistrationModel llModel = null;
				for (ApplicationFormDataEntity formEntity : formsEntity) {
					if (FormCodeType.LLF_DETAIL_FORM.getLabel().equalsIgnoreCase(formEntity.getFormCode())) {
						llModel = mapper.readValue(formEntity.getFormData(), LLRegistrationModel.class);
					} else if (FormCodeType.LLF_MEDICAL_FORM.getLabel().equalsIgnoreCase(formEntity.getFormCode())) {
						mDetailsModel = mapper.readValue(formEntity.getFormData(), MedicalDetailsModel.class);
					}
				}
				learnersPermitDtlModelList = getLearnersPermitDtlModels(llModel, mDetailsModel, applicationId,
						ServiceType.LL_FRESH.getCode());
				licenseHolderDtlsModel = getLicenseHolderDtlsModel(llModel, mDetailsModel, aadharNumber,
						formsEntity.get(0).getApplicationEntity().getApplicantDob(),
						formsEntity.get(0).getApplicationEntity().getRtaOfficeCode());
			} else if (ServiceType.DL_MILITRY.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
				DLMilataryDetailsModel mdModel = null;
				for (ApplicationFormDataEntity formEntity : formsEntity) {
					if (FormCodeType.DLM_DETAIL_FORM.getLabel().equalsIgnoreCase(formEntity.getFormCode())) {
						mdModel = mapper.readValue(formEntity.getFormData(), DLMilataryDetailsModel.class);
					} else if (FormCodeType.DLM_MEDICAL_FORM.getLabel().equalsIgnoreCase(formEntity.getFormCode())) {
						mDetailsModel = mapper.readValue(formEntity.getFormData(), MedicalDetailsModel.class);
					}
				}
				licenseHolderDtlsModel = getLicenseHolderDtlsModelForDLM(mdModel, mDetailsModel, aadharNumber,
						formsEntity.get(0).getApplicationEntity().getApplicantDob(),
						formsEntity.get(0).getApplicationEntity().getRtaOfficeCode());
				driversLicenceDetailsModels = getDriversMilitaryPermitDtlModels(Status.APPROVED, applicationId,
						appEntity.getServiceCode());

			}
			LicenseHolderPermitDetails licenseHolderPermitDetails = new LicenseHolderPermitDetails();
			licenseHolderPermitDetails.setLicenseHolderDetails(licenseHolderDtlsModel);
			licenseHolderPermitDetails.setLearnersPermitDetailsList(learnersPermitDtlModelList);
			licenseHolderPermitDetails.setDriversPermitDetailsList(driversLicenceDetailsModels);
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.saveLicenseHolderDtls(licenseHolderPermitDetails);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {

				UserSessionEntity entity = appEntity.getLoginHistory();
				entity.setUniqueKey(appEntity.getApplicationNumber());
				userSessionDAO.saveOrUpdate(entity);
				return new ResponseModel<String>(responseBody.getResponseBody().getStatus());
			}
		} catch (Exception e) {
			log.error("Getting error with saveUpdateLicenseHolderDtls " + e.getMessage());
		}
		return new ResponseModel<String>(SaveUpdateResponse.FAILURE);
	}

	@Override
	@Transactional
	public ResponseModel<String> saveUpdateDriversFreshPermitDtls(Status status, Long applicationId,
			String aadharNumber, String uniqueKey) {
		LLRegistrationModel llDetailsModel = new LLRegistrationModel();
		try {
			ApplicationFormDataEntity formEntity = applicationFormDataDAO.getApplicationFormData(applicationId,
					FormCodeType.DLF_FORM.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			if (!ObjectsUtil.isNull(formEntity)) {
				llDetailsModel = mapper.readValue(formEntity.getFormData(), LLRegistrationModel.class);
			}
			LicenseHolderPermitDetails driversDtlModel = getDriversFreshPermitDtlModels(status, applicationId,
					uniqueKey, llDetailsModel, ServiceType.DL_FRESH.getCode());
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.saveLicenseHolderDtlsForDriver(driversDtlModel, aadharNumber);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {

				return new ResponseModel<String>(responseBody.getResponseBody().getStatus());
			}
		} catch (Exception e) {
			log.error("Getting error with saveUpdateLearnerPermitDtls " + e.getMessage());
		}
		return new ResponseModel<String>(SaveUpdateResponse.FAILURE);
	}

	private LicenseHolderPermitDetails getDriversFreshPermitDtlModels(Status status, Long applicationId,
			String uniqueKey, LLRegistrationModel llDetailsModel, String serviceCode) {
		LicenseHolderPermitDetails licenseHolderPermitDetails = new LicenseHolderPermitDetails();
		List<LicensePermitDetailsEntity> permitDLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
		List<DriversLicenceDetailsModel> permitDLModel = new ArrayList<>();
		DriversLicenceDetailsModel model = null;
		String licenceNumber = "";
		List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO
				.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(), appEntity.getIteration());
		boolean flag = true;
		for (LicensePermitDetailsEntity entity : permitDLEntities) {
			String examResult = "F";
			model = new DriversLicenceDetailsModel();
			if (Status.APPROVED == status) {
				if (flag) {
					licenceNumber = drivingLicenseService.getAndUpdateDrivingLicenseSeries(
							permitDLEntities.get(0).getApplicationId().getRtaOfficeCode());
					flag = false;
				}
				examResult = "P";
				if (entity.isBadge()) {
					if (LicenceVehicleClass.AUTORICKSHAW_TRANSPORT.getCode()
							.equalsIgnoreCase(entity.getVehicleClassCode())
							|| LicenceVehicleClass.LIGHT_MOTOR_VEHICLE_TRANSPORT.getCode()
									.equalsIgnoreCase(entity.getVehicleClassCode())
							|| LicenceVehicleClass.MEDIUM_PASSENGER_VEHICLE.getCode()
									.equalsIgnoreCase(entity.getVehicleClassCode())
							|| LicenceVehicleClass.MOTOR_CAB.getCode().equalsIgnoreCase(entity.getVehicleClassCode())) {
						model.setBadgeIssuedDate(new Date());
						model.setBadgeRtaOfficeCode(entity.getApplicationId().getRtaOfficeCode());
					}
				} else if (LicenceVehicleClass.AUTORICKSHAW_TRANSPORT.getCode()
						.equalsIgnoreCase(entity.getVehicleClassCode())
						|| LicenceVehicleClass.MOTOR_CAB.getCode().equalsIgnoreCase(entity.getVehicleClassCode())) {
					model.setBadgeIssuedDate(new Date());
					model.setBadgeRtaOfficeCode(entity.getApplicationId().getRtaOfficeCode());
				} else {
					model.setBadgeIssuedDate(null);
					model.setBadgeRtaOfficeCode(null);
				}
				model.setDlIssuedDate(new Date());
				model.setDlNo(licenceNumber);
				model.setValidFrom(new Date());
				model.setValidTo(getDLValidity(entity.getApplicationId().getApplicantDob()));
				model.setDateOfFirstIssue(new Date());
				model.setStatusCode(SomeConstants.VALID);
				model.setStatusDate(new Date());
				entity.setLicenseNumber(licenceNumber);
			} else if (Status.APPROVED.getValue() == entity.getStatus()) {
				if (flag) {
					licenceNumber = drivingLicenseService.getAndUpdateDrivingLicenseSeries(
							permitDLEntities.get(0).getApplicationId().getRtaOfficeCode());
					flag = false;
				}
				examResult = "P";
				if (entity.isBadge()) {
					if (LicenceVehicleClass.AUTORICKSHAW_TRANSPORT.getCode()
							.equalsIgnoreCase(entity.getVehicleClassCode())
							|| LicenceVehicleClass.LIGHT_MOTOR_VEHICLE_TRANSPORT.getCode()
									.equalsIgnoreCase(entity.getVehicleClassCode())
							|| LicenceVehicleClass.MEDIUM_PASSENGER_VEHICLE.getCode()
									.equalsIgnoreCase(entity.getVehicleClassCode())
							|| LicenceVehicleClass.MOTOR_CAB.getCode().equalsIgnoreCase(entity.getVehicleClassCode())) {
						model.setBadgeIssuedDate(new Date());
						model.setBadgeRtaOfficeCode(entity.getApplicationId().getRtaOfficeCode());
					}
				} else if (LicenceVehicleClass.AUTORICKSHAW_TRANSPORT.getCode()
						.equalsIgnoreCase(entity.getVehicleClassCode())
						|| LicenceVehicleClass.MOTOR_CAB.getCode().equalsIgnoreCase(entity.getVehicleClassCode())) {
					model.setBadgeIssuedDate(new Date());
					model.setBadgeRtaOfficeCode(entity.getApplicationId().getRtaOfficeCode());
				} else {
					model.setBadgeIssuedDate(null);
					model.setBadgeRtaOfficeCode(null);
				}
				model.setDlIssuedDate(new Date());
				model.setDlNo(licenceNumber);
				model.setValidFrom(new Date());
				model.setValidTo(getDLValidity(entity.getApplicationId().getApplicantDob()));
				model.setDateOfFirstIssue(new Date());
				model.setStatusCode(SomeConstants.VALID);
				model.setStatusDate(new Date());
				entity.setLicenseNumber(licenceNumber);
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
			model.setAppId(entity.getApplicationId().getApplicationNumber());
			model.setApplicationId(entity.getApplicationId().getApplicationNumber());
			model.setDlType(serviceCode);
			model.setDlVehicleClassCode(entity.getVehicleClassCode());
			model.setDrivingSchoolLicenseNo(null);
			model.setIsTrained("N");
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
			model.setRtaOfficeDetails(new RTAOfficeModel());
			model.getRtaOfficeDetails().setCode(entity.getApplicationId().getRtaOfficeCode());
			permitDLModel.add(model);

			entity.setTestResult(examResult);
			licensePermitDetailsDAO.saveOrUpdate(entity);
		}
		licenseHolderPermitDetails.setDriversPermitDetailsList(permitDLModel);
		if (!ObjectsUtil.isNull(llDetailsModel)) {
			LicenseHolderDtlsModel licenseHolderDtlsModel = new LicenseHolderDtlsModel();
			licenseHolderDtlsModel.setEmail(llDetailsModel.getEmailId());
			licenseHolderDtlsModel.setMobileNo(llDetailsModel.getMobileNo());
		}
		return licenseHolderPermitDetails;
	}

	private List<DriversLicenceDetailsModel> getDriversMilitaryPermitDtlModels(Status status, Long applicationId,
			String serviceCode) {
		boolean flag = true;
		String licenceNumber = "";
		DriversLicenceDetailsModel model = null;
		List<String> rejectedCovs = new ArrayList<String>();
		List<DriversLicenceDetailsModel> permitDLModel = new ArrayList<>();
		List<LicensePermitDetailsEntity> permitDLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		List<LicensePermitDetailsHistoryEntity> licensePrmtDtlsHistoryList = licensePermitDetailsHistoryDAO
				.getCOVDetails(applicationId, Status.REJECTED.getValue());
		for (LicensePermitDetailsHistoryEntity historyEntity : licensePrmtDtlsHistoryList) {
			rejectedCovs.add(historyEntity.getVehicleClassCode());
		}
		for (LicensePermitDetailsEntity entity : permitDLEntities) {
			if (!ObjectsUtil.isNull(entity) && rejectedCovs.contains(entity.getVehicleClassCode())) {
				continue;
			} else {
				String examResult = "NA";
				model = new DriversLicenceDetailsModel();
				if (Status.APPROVED == status) {
					if (flag) {
						licenceNumber = drivingLicenseService.getAndUpdateDrivingLicenseSeries(
								permitDLEntities.get(0).getApplicationId().getRtaOfficeCode());
						flag = false;
					}
					model.setDlIssuedDate(new Date());
					model.setDlNo(licenceNumber);
					model.setValidFrom(new Date());
					model.setValidTo(getDLValidity(entity.getApplicationId().getApplicantDob()));
					model.setDateOfFirstIssue(new Date());
					model.setStatusCode(SomeConstants.VALID);
					model.setStatusDate(new Date());
					entity.setLicenseNumber(licenceNumber);
				}
				model.setAppId(entity.getApplicationId().getApplicationNumber());
				model.setApplicationId(entity.getApplicationId().getApplicationNumber());
				// TODO
				// model.setApprovedAo(null);
				// model.setApprovedMvi(null);
				model.setDlType(serviceCode);
				model.setDlVehicleClassCode(entity.getVehicleClassCode());
				model.setDrivingSchoolLicenseNo(null);
				model.setIsTrained("N");
				model.setLlrNo(null);
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
				model.setStatusRemarks("NA");
				model.setStatusValidFrom(new Date());
				model.setStatusUpdatedBy(null);
				model.setStatusValidTo(new Date());
				model.setTestDate(entity.getTestDate());
				model.setTestExempted(String.valueOf(entity.getTestExempted()));
				model.setTestExemptedReason(entity.getTestExemptedReason());
				model.setTestId("NA");
				model.setTestResult(entity.getTestResult());
				model.setTicketDetails(null);
				model.setValidFlg("Y");
				model.setRtaOfficeDetails(new RTAOfficeModel());
				model.getRtaOfficeDetails().setCode(entity.getApplicationId().getRtaOfficeCode());
				model.setAoUserDetails(new UserModel());
				Long userId = (Long) licenseSyncingService.getEmployeeUserId(applicationId, ServiceType.DL_MILITRY)
						.get("userId");
				model.getAoUserDetails().setUserId(userId);
				permitDLModel.add(model);

				entity.setTestResult(examResult);
				licensePermitDetailsDAO.saveOrUpdate(entity);
			}
		}
		return permitDLModel;
	}

	private List<LearnersPermitDtlModel> getLearnersPermitDtlModels(LLRegistrationModel llModel,
			MedicalDetailsModel mDetailsModel, Long applicationId, String serviceCode) {
		List<LicensePermitDetailsEntity> permitLLEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(applicationId);
		List<LearnersPermitDtlModel> permitLLModel = new ArrayList<>();
		LearnersPermitDtlModel model = null;
		String learnersLicence = "";
		Long userId = null;
		if (SomeConstants.PASS.equalsIgnoreCase(permitLLEntities.get(0).getTestResult())) {
			learnersLicence = ApplicationUtil.getLearnersLicenceFormat(permitLLEntities.get(0).getApplicationId());
			Map<String, Object> map = licenseSyncingService.getEmployeeUserId(applicationId, ServiceType.LL_FRESH);
			userId = (Long) map.get("userId");
		}
		for (LicensePermitDetailsEntity entity : permitLLEntities) {
			model = new LearnersPermitDtlModel();
			model.setApplicationId(entity.getApplicationId().getApplicationNumber());
			model.setLlrVehicleClassCode(entity.getVehicleClassCode());
			// applicationOrigination,referenceId,retestFlag,,signAttachmentId,statusDate,statusRemarks,ticketDetails
			// ,approvedAo,approvedMvi :: need to be added still
			model.setLlrNo(learnersLicence);
			model.setLlrNoType(serviceCode);
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
			model.setApplicationOrigination("1.3.X");
			model.setDateOfFirstIssue(new Date());
			model.setLlrIssuedt(new Date());
			model.setRetestFlag("N");
			model.setSignAttachmentId(null);
			model.setStatusDate(new Date());
			model.setReferenceId(null);
			model.setParentConsentAadhaarNo(entity.getParentConsentAadhaarNo());
			if (llModel.getSelfDecalartion()) {
				model.setMedicalFitnessType(MedicalFitnessType.SELF_CERTIFICATE.getLabel());
				model.setMedicalPractionerCode(null);
			} else {
				model.setMedicalFitnessType(MedicalFitnessType.DOCTOR_CERTIFICATE.getLabel());
				model.setMedicalPractionerCode(mDetailsModel.getRegistrationNumber());
			}
			/// TODO
			model.setAoUserId(userId);
			model.setAoUserDetails(new UserModel());
			model.getAoUserDetails().setUserId(userId);

			permitLLModel.add(model);
		}
		return permitLLModel;
	}

	private LicenseHolderDtlsModel getLicenseHolderDtlsModel(LLRegistrationModel llModel,
			MedicalDetailsModel mDetailsModel, String aadharNumber, String applicantDob, String rtaOfficeCode) {
		LicenseHolderDtlsModel lDtlsModel = new LicenseHolderDtlsModel();
		if (!ObjectsUtil.isNull(llModel)) {
			lDtlsModel.setAadhaarNo(aadharNumber);
			lDtlsModel.setBloodDonor(null);
			lDtlsModel.setBloodGrp(llModel.getBloodGroup());
			lDtlsModel.setCfstApplicantId(null);
			lDtlsModel.setDisplayName(llModel.getDisplayName());
			lDtlsModel.setElectoralNumber(null);
			lDtlsModel.setEmail(llModel.getEmailId());
			if (!StringsUtil.isNullOrEmpty(llModel.getFullName())) {
				Map<String, String> map = getSplitFirstLastName(llModel.getFullName());
				lDtlsModel.setFirstName(map.get("firstName"));
			}
			lDtlsModel.setLastName(llModel.getLastName());
			lDtlsModel.setFirstaidcertified(null);
			lDtlsModel.setForeignMilitary(null);
			lDtlsModel.setGender(null);
			lDtlsModel.setGuardianName(llModel.getGuardianName());
			lDtlsModel.setHandicapDtls(null);
			lDtlsModel.setIsActive("Y");
			lDtlsModel.setIsAdharVerify("Y");
			lDtlsModel.setIsHandicapped("N");
			lDtlsModel.setMobileNo(llModel.getMobileNo());
			lDtlsModel.setNationality("Indian");
			lDtlsModel.setOrganDonor(null);
			lDtlsModel.setOtherstateCd(null);
			lDtlsModel.setPanNo(null);
			lDtlsModel.setPresAddrCountryId(1l);
			lDtlsModel.setDistrictDetails(new DistrictModel());
			lDtlsModel.getDistrictDetails().setCode(llModel.getDistrictCode());
			lDtlsModel.setPresAddrDoorNo(llModel.getDoorNo());
			lDtlsModel.setMandalDetails(new MandalModel());
			lDtlsModel.getMandalDetails().setCode(llModel.getMandalCode());
			lDtlsModel.setPresAddrPinCode(String.valueOf(llModel.getPostOffice()));
			lDtlsModel.setStateDetails(new StateModel());
			lDtlsModel.getStateDetails().setCode(llModel.getStateCode());
			lDtlsModel.setPresAddrStreet(llModel.getStreet());
			lDtlsModel.setPresAddrTown(llModel.getCity());
			lDtlsModel.setQualificationDetails(new QualificationModel());
			lDtlsModel.getQualificationDetails().setCode(llModel.getQualificationCode());
			lDtlsModel.setRtaOfficeDetails(new RTAOfficeModel());
			lDtlsModel.getRtaOfficeDetails().setCode(rtaOfficeCode);
			lDtlsModel.setTicketDetails(null);
			lDtlsModel.setTwotire(null);
			lDtlsModel.setPermAddrCountry("India");
			lDtlsModel.setDateOfBirth(DateUtil.getDatefromString(applicantDob));
			lDtlsModel.setIsSameAsAadhaar(llModel.getIsSameAadhar());
			if (llModel.getIsSameAadhar()) {
				lDtlsModel.setPermAddrDoorNo(llModel.getDoorNo());
				lDtlsModel.setPermAddrPinCode(String.valueOf(llModel.getPostOffice()));
				lDtlsModel.setPermAddrStreet(llModel.getStreet());
				lDtlsModel.setPermAddrTown(llModel.getCity());
				lDtlsModel.setPermAddrMandal(llModel.getMandalName());
				lDtlsModel.setPermAddrDistrict(llModel.getDistrictName());
				lDtlsModel.setPermAddrState(llModel.getStateName());
			}
		}
		return lDtlsModel;
	}

	private LicenseHolderDtlsModel getLicenseHolderDtlsModelForDLM(DLMilataryDetailsModel llModel,
			MedicalDetailsModel mDetailsModel, String aadharNumber, String applicantDob, String rtaOfficeCode) {
		LicenseHolderDtlsModel lDtlsModel = new LicenseHolderDtlsModel();
		if (!ObjectsUtil.isNull(llModel)) {
			lDtlsModel.setAadhaarNo(aadharNumber);
			lDtlsModel.setBloodDonor(null);
			lDtlsModel.setBloodGrp(llModel.getBloodGroup());
			lDtlsModel.setCfstApplicantId(null);
			lDtlsModel.setDisplayName(llModel.getDisplayName());
			lDtlsModel.setElectoralNumber(null);
			lDtlsModel.setEmail(llModel.getEmailId());
			if (!StringsUtil.isNullOrEmpty(llModel.getFullName())) {
				Map<String, String> map = getSplitFirstLastName(llModel.getFullName());
				lDtlsModel.setFirstName(map.get("firstName"));
			}
			lDtlsModel.setLastName(llModel.getLastName());
			lDtlsModel.setFirstaidcertified(null);
			lDtlsModel.setForeignMilitary(null);
			lDtlsModel.setGender(null);
			lDtlsModel.setGuardianName(llModel.getGuardianName());
			lDtlsModel.setHandicapDtls(null);
			lDtlsModel.setIsActive("Y");
			lDtlsModel.setIsAdharVerify("Y");
			lDtlsModel.setIsHandicapped("N");
			lDtlsModel.setMobileNo(llModel.getMobileNo());
			lDtlsModel.setNationality("Indian");
			lDtlsModel.setOrganDonor(null);
			lDtlsModel.setOtherstateCd(null);
			lDtlsModel.setPanNo(null);
			lDtlsModel.setPresAddrCountryId(1l);
			lDtlsModel.setDistrictDetails(new DistrictModel());
			lDtlsModel.getDistrictDetails().setCode(llModel.getDistrictCode());
			lDtlsModel.setPresAddrDoorNo(llModel.getDoorNo());
			lDtlsModel.setMandalDetails(new MandalModel());
			lDtlsModel.getMandalDetails().setCode(llModel.getMandalCode());
			lDtlsModel.setPresAddrPinCode(String.valueOf(llModel.getPostOffice()));
			lDtlsModel.setStateDetails(new StateModel());
			lDtlsModel.getStateDetails().setCode(llModel.getStateCode());
			lDtlsModel.setPresAddrStreet(llModel.getStreet());
			lDtlsModel.setPresAddrTown(llModel.getCity());
			lDtlsModel.setQualificationDetails(new QualificationModel());
			lDtlsModel.getQualificationDetails().setCode(llModel.getQualificationCode());
			lDtlsModel.setRtaOfficeDetails(new RTAOfficeModel());
			lDtlsModel.getRtaOfficeDetails().setCode(rtaOfficeCode);
			lDtlsModel.setTicketDetails(null);
			lDtlsModel.setTwotire(null);
			lDtlsModel.setPermAddrCountry("India");
			lDtlsModel.setDateOfBirth(DateUtil.getDatefromString(applicantDob));
			lDtlsModel.setIsSameAsAadhaar(llModel.getIsSameAadhar());
			if (llModel.getIsSameAadhar()) {
				lDtlsModel.setPermAddrDoorNo(llModel.getDoorNo());
				lDtlsModel.setPermAddrPinCode(String.valueOf(llModel.getPostOffice()));
				lDtlsModel.setPermAddrStreet(llModel.getStreet());
				lDtlsModel.setPermAddrTown(llModel.getCity());
				lDtlsModel.setPermAddrMandal(llModel.getMandalName());
				lDtlsModel.setPermAddrDistrict(llModel.getDistrictName());
				lDtlsModel.setPermAddrState(llModel.getStateName());
			}
		}
		return lDtlsModel;
	}

	private Long getAttachemts(Long applicationId) {
		AttachmentEntity attachmentEntity = null;
		try {
			attachmentEntity = attachmentDAO.getAttachmentDetails(SomeConstants.ONE, applicationId);
		} catch (Exception e) {
		}
		return ObjectsUtil.isNull(attachmentEntity) ? null : attachmentEntity.getAttachmentDlId();
	}

	// Method used for bypassing SSL verification
	public static void disableSSLVerification() {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

		} };

		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HostnameVerifier allHostsValid = new HostnameVerifier() {

			public boolean verify(String hostname, SSLSession session) {
				return true;
			}

		};
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	private List<LearnersPermitDtlModel> testSyncinggetLearnersPermitDtlModels(LLRegistrationModel llModel,
			MedicalDetailsModel mDetailsModel, Long applicationId) {
		List<LicensePermitDetailsEntity> permitLLEntities = null;// licensePermitDetailsDAO.getLicensePermitDetails(applicationId);
		permitLLEntities = new ArrayList<>();
		LicensePermitDetailsEntity entitytest = new LicensePermitDetailsEntity();
		entitytest.setApplicationId(applicationDAO.getEntity(ApplicationEntity.class, applicationId));

		entitytest.setLicenseNumber(
				ApplicationUtil.getApplicationFormat(applicationDAO.getEntity(ApplicationEntity.class, applicationId)));
		entitytest.setLicenseType(LicenseType.LL.getLabel());
		entitytest.setVehicleClassCode("LMNT");
		entitytest.setTestDate(new Date());
		entitytest.setTestExempted('Y');
		entitytest.setTestExemptedReason(null);
		entitytest.setTestNoOfAttemp(1);
		entitytest.setTestResult("PASS");
		entitytest.setTestMarks("99");
		permitLLEntities.add(entitytest);
		List<LearnersPermitDtlModel> permitLLModel = new ArrayList<>();
		LearnersPermitDtlModel model = null;
		for (LicensePermitDetailsEntity entity : permitLLEntities) {
			model = new LearnersPermitDtlModel();
			model.setApplicationId(entity.getApplicationId().getApplicationNumber());
			model.setLlrVehicleClassCode(entity.getVehicleClassCode());
			// applicationOrigination,referenceId,retestFlag,,signAttachmentId,statusDate,statusRemarks,ticketDetails
			// ,approvedAo,approvedMvi :: need to be added still

			model.setLlrNo(entity.getLicenseNumber());
			model.setTestDate(entity.getTestDate());
			model.setTestExempted(entity.getTestExempted());
			model.setTestExemptedReason(entity.getTestExemptedReason());
			model.setTestResult(entity.getTestResult());
			model.setTestId(String.valueOf(entity.getTestNoOfAttemp()));
			model.setValidFrom(new Date());
			model.setValidTo(null);
			model.setRtaOfficeDetails(new RTAOfficeModel());
			model.getRtaOfficeDetails().setCode(entity.getApplicationId().getRtaOfficeCode());
			model.setPhotoAttachmentId(1l);
			// model.setMedicalFitnessType(llModel.getSelfDecalartion());
			model.setMedicalPractionerCode(mDetailsModel.getRegistrationNumber());
			model.setApplicationOrigination(null);
			model.setDateOfFirstIssue(null);
			model.setLlrIssuedt(null);
			model.setRetestFlag(null);
			model.setSignAttachmentId(null);
			model.setStatusDate(null);
			model.setReferenceId(null);
			model.setParentConsentAadhaarNo(entity.getParentConsentAadhaarNo());
			permitLLModel.add(model);
		}
		return permitLLModel;
	}

	@Override
	@Transactional
	public List<RtaTaskInfo> completeExamTask(String token, String appNo, Status status) {
		log.info("Going to complete Exam task for App : " + appNo);
		String userName = jwtTokenUtil.getUserNameFromRegToken(token);
		Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
		String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
		ApplicationEntity entity = applicationDAO.getApplication(appNo);
		saveToApplicationHistory(status, userName, userId, userRole, entity);
		String instanceId = entity.getExecutionId();
		Assignee assignee = new Assignee();
		assignee.setUserId(userName);
		log.info("userName: " + userName + " userId: " + userId + " userRole:" + userRole);
		ActivitiResponseModel<List<RtaTaskInfo>> cRes = activitiService.completeTaskWithAction(assignee, examTask,
				status.getLabel(), instanceId, true);
		UserType userType = UserType.valueOf(userRole.toUpperCase());
		if (Status.REJECTED == status) {
			rtaApplicationService.completeApp(instanceId, status, userName, userType, true);
			// sendSMSEmail(status, entity);
		}
		return cRes.getActiveTasks();
	}

	private void saveToApplicationHistory(Status status, String userName, Long userId, String userRole,
			ApplicationEntity entity) {
		Long time = DateUtil.toCurrentUTCTimeStamp();
		ApplicationApprovalHistoryEntity entityHistory = new ApplicationApprovalHistoryEntity();
		entityHistory.setApplicationEntity(entity);
		entityHistory.setRtaUserId(userId);
		entityHistory.setRtaUserRole(userRole);
		entityHistory.setStatus(status.getValue());
		entityHistory.setCreatedBy(userName);
		entityHistory.setCreatedOn(time);
		entityHistory.setIteration(entity.getIteration());
		applicationApprovalHistoryDAO.save(entityHistory);
	}

	@Override
	@Transactional
	public List<QuestionModel> getQuestions(SlotServiceType type) {
		List<QuestionEntity> questions = questionnaireDAO.getQuestions(type, Status.APPROVED);
		List<QuestionModel> questionsModelList = null;
		if (!ObjectsUtil.isNull(questions)) {
			questionsModelList = new ArrayList<>();
			for (QuestionEntity question : questions) {
				List<OptionsEntity> optionsEntityList = questionnaireDAO.getAnswers(question.getQuestionId(),
						Status.APPROVED);
				if (!ObjectsUtil.isNull(optionsEntityList)) {
					QuestionModel questionModel = questionConverter.convertToModel(question);
					questionModel.setOptions(optionsConverter.convertToModelList(optionsEntityList));
					questionsModelList.add(questionModel);
				}
			}
		}
		return questionsModelList;
	}

	@Override
	@Transactional
	public ResponseModel<List<QuestionnaireFeedbackEntity>> saveQuestionsFeedbackEntities(String applicationNumber,
			List<QuestionModel> questions, SlotServiceType testType, String username) {
		ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNumber);
		if (ObjectsUtil.isNull(applicationEntity)) {
			log.error("questionnaire not saved.. " + applicationNumber);
			return new ResponseModel<>(ResponseModel.FAILED, "invalid application number",
					HttpStatus.BAD_REQUEST.value());
		}
		if (ObjectsUtil.isNullOrEmpty(questions)) {
			log.error("questionnaire not saved.. " + applicationNumber);
			return new ResponseModel<>(ResponseModel.FAILED, "invalid questions feedback",
					HttpStatus.BAD_REQUEST.value());
		}
		if (ObjectsUtil.isNull(testType)) {
			log.error("testtype not saved.. invalid testType : " + testType + " for applicationNumber : "
					+ applicationNumber);
			return new ResponseModel<>(ResponseModel.FAILED, "invalid testType", HttpStatus.BAD_REQUEST.value());
		}
		long currentTime = DateUtil.toCurrentUTCTimeStamp();
		List<QuestionnaireFeedbackEntity> list = new ArrayList<>();
		for (QuestionModel question : questions) {
			OptionModel selectedOption = question.getSelectedOption();
			if (!ObjectsUtil.isNull(selectedOption)) {
				QuestionEntity questionEntity = questionnaireDAO.getEntity(QuestionEntity.class,
						question.getQuestionId());
				if (!ObjectsUtil.isNull(questionEntity)) {
					OptionsEntity selectedOptionEntity = questionnaireDAO.getOption(selectedOption.getOptionId(),
							Status.APPROVED);
					if (!ObjectsUtil.isNull(selectedOptionEntity)) {
						QuestionnaireFeedbackEntity qfe = new QuestionnaireFeedbackEntity();
						qfe.setAnswer(selectedOptionEntity.getOption());
						qfe.setApplication(applicationEntity);
						qfe.setCreatedBy(username);
						qfe.setCreatedOn(currentTime);
						qfe.setIsCorrect(selectedOptionEntity.getIsCorrect());
						qfe.setModifiedBy(username);
						qfe.setModifiedOn(currentTime);
						qfe.setQuestion(questionEntity.getQuestion());
						qfe.setStatus(Status.APPROVED.getValue());
						qfe.setTestType(testType);
						questionnaireFeedbackDAO.saveOrUpdate(qfe);
					}
				}
			}
		}
		return new ResponseModel<>(ResponseModel.SUCCESS, list);
	}

	@Override
	@Transactional
	public ResponseModel<List<QuestionnaireFeedbackModel>> getQuestionsFeedbackEntities(String applicationNumber,
			SlotServiceType testType, String username) {
		ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNumber);
		if (ObjectsUtil.isNull(applicationEntity)) {
			log.error("questionnaire not saved.. " + applicationNumber);
			return new ResponseModel<>(ResponseModel.FAILED, "invalid application number",
					HttpStatus.BAD_REQUEST.value());
		}
		if (ObjectsUtil.isNull(testType)) {
			log.error("testtype not saved.. invalid testType : " + testType + " for applicationNumber : "
					+ applicationNumber);
			return new ResponseModel<>(ResponseModel.FAILED, "invalid testType", HttpStatus.BAD_REQUEST.value());
		}
		List<QuestionnaireFeedbackModel> listOfmodels = null;
		List<QuestionnaireFeedbackEntity> list = questionnaireFeedbackDAO.getQuestionnaire(testType,
				applicationEntity.getApplicationId());
		if (!ObjectsUtil.isNullOrEmpty(list)) {
			listOfmodels = questionnaireFeedbackConverter.convertToModelList(list);
		}
		return new ResponseModel<>(ResponseModel.SUCCESS, listOfmodels);
	}

	@Override
	public void saveQuestionsFeedback(List<QuestionnaireFeedbackEntity> entities) {
		questionnaireFeedbackDAO.saveInBulk(entities);
	}

	@Override
	@Transactional
	public List<RtaTaskInfo> callAfterGettingDetails(Long sessionId, String taskDef, String userName) {
		log.info("Details are Getting Succesfully... performing other tasks... with user : " + userName);
		Assignee assignee = new Assignee();
		assignee.setUserId(userName);
		String instanceId = applicationService.getProcessInstanceId(sessionId);
		Map<String, Object> variableMap = new HashMap<>();
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		UserSessionEntity sessionEntity = appEntity.getLoginHistory();
		activitiService.completeTask(assignee, taskDef, instanceId, true, variableMap);
		// --- application completed in bpm ----------------
		RtaTaskInfo taskInfo = new RtaTaskInfo();
		taskInfo.setTaskDefKey(ActivitiService.APP_COMPLETED);
		taskInfo.setProcessDefId(appEntity.getLoginHistory().getServiceCode());
		List<RtaTaskInfo> taskList = new ArrayList<RtaTaskInfo>();
		taskList.add(taskInfo);
		try {
			rtaApplicationService.completeApp(instanceId, Status.APPROVED, sessionEntity.getAadharNumber(),
					UserType.ROLE_CITIZEN, true);
		} catch (Exception ex) {
			log.error("Exception while completing task ......." + ex.getMessage());
		}
		return taskList;
	}

	private static Map<String, String> getSplitFirstLastName(String fullName) {
		Map<String, String> map = new HashMap<String, String>();
		String[] name = fullName.split(" ", 2);
		if (name.length == 2) {
			map.put("firstName", name[0]);
			map.put("lastName", name[1]);
		} else {
			map.put("firstName", name[0]);
		}
		return map;
	}

	@Override
	public List<ClassofVechicleModel> getCovList(List<String> vehicleClass) {
		List<LlrVehicleClassMasterEntity> list = null;
		List<ClassofVechicleModel> llrclassofvechicledetailsmodel = new ArrayList<ClassofVechicleModel>();
		for (String out : vehicleClass) {
			list = licenceDAO.getCovList(out);
			for (LlrVehicleClassMasterEntity llrmodel : list) {
				if (null != llrmodel) {
					ClassofVechicleModel model = new ClassofVechicleModel();
					model.setCovCode(llrmodel.getVehicleClass());
					model.setCovDescription(llrmodel.getVehicleClassDescription());
					model.setVehicleClassType(llrmodel.getVehicleTransportType());
					llrclassofvechicledetailsmodel.add(model);
				}
			}
		}
		return llrclassofvechicledetailsmodel;
	}

	@Override
	public Date getDLValidity(String dateOfBirth) {
		try {
			Integer age = DateUtil.getCurrentAge(dateOfBirth);
			age = age + SomeConstants.TWENTY;
			if (age < SomeConstants.FIFTY) {
				return DateUtil.addYears(new Date(), SomeConstants.TWENTY);
			} else {
				// TODO Optimize this below
				/*
				 * TimeZone timeZone = TimeZone.getTimeZone("UTC"); Integer ageInDays =
				 * DateUtil.getCurrentAgeInDays(dateOfBirth); Date after50Years =
				 * DateUtil.addYears(new Date(), SomeConstants.FIFTY); Calendar calendar =
				 * Calendar.getInstance(timeZone);
				 * calendar.setTimeInMillis(DateUtil.toCurrentUTCTimeStamp());
				 * calendar.add(Calendar.DATE, ageInDays - 1); long diff =
				 * after50Years.getTime() - calendar.getTime().getTime(); Integer days = (int)
				 * TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS); Calendar c1 =
				 * Calendar.getInstance(timeZone);
				 * c1.setTimeInMillis(DateUtil.toCurrentUTCTimeStamp()); c1.add(Calendar.DATE,
				 * days - 30); return c1.getTime();
				 */

				// Get Date of birth object
				Date date = DateUtil.getDatefromString(dateOfBirth);
				// Add 50 years
				Date after50YearsDOB = DateUtil.addYears(date, SomeConstants.FIFTY);

				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				Calendar cal = Calendar.getInstance(timeZone);
				cal.setTime(after50YearsDOB);
				Date dateBefore1Days = cal.getTime();
				return dateBefore1Days;
			}
		} catch (Exception e) {
		}
		return DateUtil.addMonths(new Date(), SomeConstants.TWENTY);
	}

	@Override
	@Transactional
	public LicenseHolderPermitDetails getLicenceDetails(String applicationNo) {

		String uniqueKey = "";
		ApplicationEntity application = applicationDAO.getApplication(applicationNo);
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = null;
		LicenseHolderPermitDetails model = null;
		try {
			if (ServiceType.LL_ENDORSEMENT.getCode().equals(application.getServiceCode())) {
				uniqueKey = ApplicationUtil.getLearnersLicenceFormat(application);
			}
			holderDetails = registrationLicenseService
					.getLicenseHolderDtls(application.getLoginHistory().getAadharNumber(), null, uniqueKey);
			if (holderDetails.getHttpStatus() == HttpStatus.OK) {
				model = holderDetails.getResponseBody();
			}
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		return model;
	}

	@Transactional
	@Override
	public Boolean isPaymentCompleted(Long sessionId) {
		try {
			ApplicationEntity application = applicationDAO.getApplicationFromSession(sessionId);
			if (!ObjectsUtil.isNull(
					transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(application, PaymentType.PAY))) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	private LicenseIDPDtlsModel getLicenseIDPDtlsModel(Status status, Long applicationId, String uniqueKey,
			InternationalPermitModel intPermitModel, String serviceCode, String aadharNumber) {
		LicenseIDPDtlsModel licenseIDPDtlsModel = new LicenseIDPDtlsModel();
		UserSessionEntity userSession = userSessionDAO.getAppliedSessionsForLLR(aadharNumber);
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(userSession.getSessionId());
		String idpLicenceNo = drivingLicenseService.getIDPDrivingLicenseSeries(appEntity.getRtaOfficeCode());
		Map<String, Object> map = licenseSyncingService.getEmployeeUserId(applicationId, ServiceType.DL_INT_PERMIT);
		Date date = new Date();
		licenseIDPDtlsModel.setApplicationNo(appEntity.getApplicationNumber());
		licenseIDPDtlsModel.setApprovedBy(map.get("userId").toString());
		licenseIDPDtlsModel.setIdpLicenseNo(idpLicenceNo);
		// licenseIDPDtlsModel.setIdpVehicleClassCode(idpVehicleClassCode);
		licenseIDPDtlsModel.setIssueDate(new Date(intPermitModel.getIssusedDate() * 1000));
		licenseIDPDtlsModel.setPassportNo(intPermitModel.getPassportNumber());
		licenseIDPDtlsModel.setPassportValidTo(new Date(intPermitModel.getExpiryDate() * 1000));
		licenseIDPDtlsModel.setRtaOfficeDetails(new RTAOfficeModel());
		licenseIDPDtlsModel.getRtaOfficeDetails().setCode(appEntity.getRtaOfficeCode());
		licenseIDPDtlsModel.setStatus(status.getValue());
		licenseIDPDtlsModel.setStatusRemarks(map.get("comment").toString());
		licenseIDPDtlsModel.setValidFrom(date);
		licenseIDPDtlsModel.setValidTo(DateUtil.addYears(date, SomeConstants.ONE));
		return licenseIDPDtlsModel;
	}

	@Override
	@Transactional
	public ResponseModel<String> saveUpdateIntrnationalLicenseDtls(Status status, Long applicationId,
			String aadharNumber, String uniqueKey) {
		InternationalPermitModel intPermitModel = new InternationalPermitModel();
		try {
			ApplicationFormDataEntity formEntity = applicationFormDataDAO.getApplicationFormData(applicationId,
					FormCodeType.DLIN_FORM.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			if (!ObjectsUtil.isNull(formEntity)) {
				intPermitModel = mapper.readValue(formEntity.getFormData(), InternationalPermitModel.class);
			}
			LicenseIDPDtlsModel licenseIDPDtlModel = getLicenseIDPDtlsModel(status, applicationId, uniqueKey,
					intPermitModel, ServiceType.DL_INT_PERMIT.getCode(), aadharNumber);
			RegLicenseServiceResponseModel<SaveUpdateResponse> responseBody = registrationLicenseService
					.saveUpdateIntrnationalLicenseDtls(licenseIDPDtlModel, aadharNumber);
			if (responseBody.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(responseBody.getResponseBody().getStatus());
			}
		} catch (Exception e) {
			log.error("Getting error with saveUpdateIntrnationalLicenseDtls " + e.getMessage());
		}
		return new ResponseModel<String>(SaveUpdateResponse.FAILURE);
	}

	@Override
	public LicenseHolderPermitDetails getSuspCancelDriverLicence(String dLNumber, String userName, UserType userType)
			throws UnauthorizedException, ForbiddenException, AadharNotFoundException, DataMismatchException,
			NotFoundException, AadharAuthenticationFailedException, VehicleNotFinanced, FinancerNotFound,
			ParseException, ServiceValidationException, ConflictException {

		AuthenticationService service;
		ResponseModel<TokenModel> responseModel = null;
		HashMap<String, Boolean> param = new HashMap<>();
		RegLicenseServiceResponseModel<LicenseHolderPermitDetails> response = registrationLicenseService
				.getLicenseHolderDtlsForDriver(dLNumber);
		RegistrationServiceResponseModel<UserModel> usResModel = registrationLicenseService.getUserDetails(userName);
		if (response.getHttpStatus() == HttpStatus.OK && usResModel.getHttpStatus() == HttpStatus.OK) {
			LicenseHolderDtlsModel lieHldrDtls = response.getResponseBody().getLicenseHolderDetails();
			if (!usResModel.getResponseBody().getRtaOfficeCode().equals(lieHldrDtls.getRtaOfficeDetails().getCode())) {
				log.info("Provided DL Number is not matched with User RTA Office " + dLNumber);
				throw new ConflictException("Provided DL Number is not matched with User RTA Office " + dLNumber);

			}
		}
		if (UserType.ROLE_CCO == userType || UserType.ROLE_AO == userType) {
			service = serviceFactory.getAuthenticationService(ServiceType.DL_SUSU_CANC, userType);
			if (!ObjectsUtil.isNull(service)) {
				AuthenticationModel model = new AuthenticationModel();
				String aadharNo = usResModel.getResponseBody().getAadharNumber();
				if (!StringsUtil.isNullOrEmpty(aadharNo)) {
					model.setUid_num(aadharNo);
					model.setDlNumber(dLNumber);
					model.setKeyType(KeyType.DLC);
				}
				responseModel = service.authenticate(model, ServiceType.DL_SUSU_CANC, param, userType.getLabel());
			}
			if (!responseModel.getStatus().equals(ResponseModel.SUCCESS)) {
				return null;
			}
		}
		UserSessionEntity entity = userSessionDAO.getUserSessionByUniqueKey(dLNumber, ServiceType.DL_SUSU_CANC);
		if (ObjectsUtil.isNull(entity)) {
			return null;
		}
		if (response.getHttpStatus() == HttpStatus.OK) {
			LicenseHolderPermitDetails model = response.getResponseBody();
			if (!ObjectsUtil.isNull(entity)) {
				model.setApplicationNo(
						applicationDAO.getApplicationFromSession(entity.getSessionId()).getApplicationNumber());
			}
			return model;
		}
		return null;
	}
}

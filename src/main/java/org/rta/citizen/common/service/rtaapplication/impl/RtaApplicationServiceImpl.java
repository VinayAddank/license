/**
 * 
 */
package org.rta.citizen.common.service.rtaapplication.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.converters.AddressConverter;
import org.rta.citizen.common.converters.AddressOutsideAPConverter;
import org.rta.citizen.common.dao.AddressDAO;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.ServiceMasterDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.ServiceMasterEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.OwnershipType;
import org.rta.citizen.common.enums.Qualification;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.TaskNotFound;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.AddressModel;
import org.rta.citizen.common.model.AppActionModel;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.DocActionModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.PermitTypeModel;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.communication.CustMsgModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.SyncService;
import org.rta.citizen.common.service.communication.CommunicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.service.rtaapplication.RtaApplicationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.duplicateregistration.model.DuplicateRegistrationModel;
import org.rta.citizen.freshrc.FinanceYardModel;
import org.rta.citizen.freshrc.FreshRcModel;
import org.rta.citizen.freshrc.ShowcaseInfoRequestModel;
import org.rta.citizen.freshrc.ShowcaseNoticeInfoModel;
import org.rta.citizen.hpt.model.FinanceOtherServiceModel;
import org.rta.citizen.licence.dao.updated.LicenseHolderApprovedDetailsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.updated.LicenseHolderApprovedDetailsEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.model.updated.CourseCertificateModel;
import org.rta.citizen.licence.model.updated.DLForiegnCitizenDetailsModel;
import org.rta.citizen.licence.model.updated.DLMilataryDetailsModel;
import org.rta.citizen.licence.model.updated.DLOSChecklistDetailsModel;
import org.rta.citizen.licence.model.updated.DLSurrenderDetailsModel;
import org.rta.citizen.licence.model.updated.InternationalPermitModel;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.MedicalDetailsModel;
import org.rta.citizen.licence.model.updated.SupensionCancellationModel;
import org.rta.citizen.licence.model.updated.SuspensionRevocationModel;
import org.rta.citizen.licence.service.updated.LicensePermitDetailsService;
import org.rta.citizen.permit.model.PermitNewRequestModel;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.rta.citizen.slotbooking.converters.SlotConverter;
import org.rta.citizen.slotbooking.dao.SlotDAO;
import org.rta.citizen.slotbooking.entity.SlotApplicationsEntity;
import org.rta.citizen.slotbooking.entity.SlotEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.SlotService;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.rta.citizen.vehiclereassignment.model.VehicleReassignmentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author arun.verma
 *
 */
@Service
public class RtaApplicationServiceImpl implements RtaApplicationService {

	private static final Logger log = Logger.getLogger(RtaApplicationServiceImpl.class);

	public final static short SEND_SMS_EMAIL = 0;
	public final static short SEND_SMS = 1;
	public final static short SEND_EMAIL = 2;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ServiceMasterDAO serviceMasterDAO;

	@Autowired
	private SlotDAO slotDAO;

	@Autowired
	private SlotConverter slotConverter;

	@Autowired
	private AddressOutsideAPConverter addressOutsideAPConverter;

	@Autowired
	private AddressConverter addressConverter;

	@Autowired
	private CommunicationService communicationService;

	@Autowired
	private ApplicationFormDataService applicationFormDataService;

	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;

	@Autowired
	private AddressDAO addressDAO;

	@Autowired
	private SlotService slotService;

	@Autowired
	private LicensePermitDetailsDAO licensePermitDetailsDAO;

	@Autowired
	private LicensePermitDetailsService licensePermitDetailsService;

	@Autowired
	private LicenseHolderApprovedDetailsDAO licenseHolderApprovedDetailsDAO;
	
	@Autowired
	private TaxDetailDAO taxDetailDAO;

	@Value("${slot.scheduling.enabled}")
	private Boolean isSchedulingEnabled;

	@Override
	@Transactional
	public void openApplication(Status status, String appNo, Long userId, String userName, String userRole)
			throws TaskNotFound, NotFoundException {
		log.info("open application : appno: " + appNo + " userId: " + userId + " userName: " + userName + " userRole"
				+ userRole);
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		if (ObjectsUtil.isNull(appEntity)) {
			log.error("Application Not Found " + appNo);
			throw new NotFoundException("Application Not Found");
		}
		String instanceId = applicationService.getProcessInstanceId(appEntity.getLoginHistory().getSessionId());
		if (ObjectsUtil.isNull(instanceId)) {
			log.error("ExecutionId Not Found for App : " + appNo);
			throw new NotFoundException("Application Not Found");
		}
		Assignee assignee = new Assignee();
		assignee.setUserId(userName);
		ActivitiResponseModel<List<RtaTaskInfo>> res = activitiService.getTasks(userName, instanceId);
		if (res.getStatus().equals(ActivitiResponseModel.SUCCESS) && res.getActiveTasks().size() > 0) {
			RtaTaskInfo tasks = res.getActiveTasks().get(0);
			activitiService.claimTask(assignee, instanceId, tasks.getTaskDefKey());
		} else {
			log.error("Task not found : " + appNo);
			throw new TaskNotFound();
		}
		Long time = DateUtil.toCurrentUTCTimeStamp();
		List<ApplicationApprovalHistoryEntity> entityList = applicationApprovalHistoryDAO
				.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), Status.OPEN, userId);
		if (ObjectsUtil.isNull(entityList) || entityList.size() <= 0) {
			ApplicationApprovalHistoryEntity entity = new ApplicationApprovalHistoryEntity();
			entity.setApplicationEntity(appEntity);
			entity.setRtaUserId(userId);
			entity.setRtaUserRole(userRole);
			entity.setStatus(status.getValue());
			entity.setCreatedBy(userName);
			entity.setCreatedOn(time);
			entity.setIteration(appEntity.getIteration());
			applicationApprovalHistoryDAO.save(entity);
		}
	}

	@Override
	@Transactional
	public List<RtaTaskInfo> actionOnApp(Status status, String appNo, Long userId, String userName, String userRole,
			CommentModel commentModel, String slotId) throws TaskNotFound, NotFoundException {
		log.info("action on app : appno: " + appNo + " userId: " + userId + " userName: " + userName + " userRole"
				+ userRole + " status: " + status);
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		UserType userType = UserType.valueOf(userRole.toUpperCase());
		boolean slotProcessed = true;
		if (ObjectsUtil.isNull(appEntity)) {
			log.error("Application Not Found " + appNo);
			throw new NotFoundException("Application Not Found");
		}
		String instanceId = applicationService.getProcessInstanceId(appEntity.getLoginHistory().getSessionId());
		if (ObjectsUtil.isNull(instanceId)) {
			log.error("ExecutionId Not Found for App : " + appNo);
			throw new NotFoundException("Application Not Found");
		}
		if (UserType.ROLE_MVI.toString().equalsIgnoreCase(userRole)) {
			Long slotIdAsLong;
			try {
				slotIdAsLong = Long.parseLong(slotId);
			} catch (NumberFormatException e) {
				log.info("slotId not provided for applicationNumber : " + appNo);
				slotIdAsLong = 0L;
			}
			validateSlotIdIfRequired(appEntity, slotIdAsLong);
			slotProcessed = allSlotsProcessed(appEntity, slotIdAsLong, status);
		}
		Assignee assignee = new Assignee();
		assignee.setUserId(userName);
		// ---------opened apps -------------------------
		HashMap<String, Object> variables = new HashMap<>();
		if (appEntity.getServiceCode() != null) {
			variables.put(ActivitiService.SERVICE_CODE_KEY, appEntity.getServiceCode());
		}
		ActivitiResponseModel<List<RtaTaskInfo>> resOpened = activitiService.getAssignedTasks(userName, variables);
		List<String> openedExecutionIdList = new ArrayList<>();
		if (resOpened.getStatus().equals(ActivitiResponseModel.SUCCESS)) {
			List<RtaTaskInfo> tasks = resOpened.getActiveTasks();
			if (!ObjectsUtil.isNull(tasks)) {
				tasks.forEach(task -> {
					openedExecutionIdList.add(task.getProcessInstanceId());
				});
			}
		}
		if (ObjectsUtil.isNull(openedExecutionIdList) || !openedExecutionIdList.contains(instanceId)) {
			log.error("Task not found in open list,  app: " + appNo);
			throw new TaskNotFound("Task not found in open list.");
		}
		List<RtaTaskInfo> rtTaskList = null;
		ActivitiResponseModel<List<RtaTaskInfo>> res = activitiService.getTasks(userName, instanceId);
		if (res.getStatus().equals(ActivitiResponseModel.SUCCESS) && res.getActiveTasks().size() > 0 && slotProcessed) {
			RtaTaskInfo tasks = res.getActiveTasks().get(0);
			ActivitiResponseModel<List<RtaTaskInfo>> cRes = activitiService.completeTaskWithAction(assignee,
					tasks.getTaskDefKey(), status.getLabel(), instanceId, false);
			rtTaskList = cRes.getActiveTasks();
		} else {
			log.error("Task not found : " + appNo);
			throw new TaskNotFound("Task Not Found !!!");
		}
		Long time = DateUtil.toCurrentUTCTimeStamp();
		ApplicationApprovalHistoryEntity entity = new ApplicationApprovalHistoryEntity();
		entity.setApplicationEntity(appEntity);
		entity.setRtaUserId(userId);
		entity.setRtaUserRole(userRole);
		entity.setStatus(status.getValue());
		entity.setCreatedBy(userName);
		entity.setCreatedOn(time);
		entity.setIteration(appEntity.getIteration());
		if (!ObjectsUtil.isNull(commentModel)) {
			entity.setComments(commentModel.getComment());
		}
		applicationApprovalHistoryDAO.save(entity);

		if (ServiceCategory.LL_CATEGORY.getCode().equalsIgnoreCase((appEntity.getServiceCategory()))
				|| ServiceCategory.DL_CATEGORY.getCode().equalsIgnoreCase((appEntity.getServiceCategory()))) {
			List<LicensePermitDetailsEntity> licensePermitDetailsEntityList = licensePermitDetailsDAO
					.getLicensePermitDetails(appEntity.getApplicationId());
			LicenseHolderApprovedDetailsEntity holderEntity = licenseHolderApprovedDetailsDAO
					.getHolderApprovedDetails(appEntity.getApplicationId());
			LicenseHolderApprovedDetailsEntity approvedDtlsEntity = new LicenseHolderApprovedDetailsEntity();
			for (LicensePermitDetailsEntity licensePermitDetailsEntity : licensePermitDetailsEntityList) {
				licensePermitDetailsEntity.setModifiedBy(userName);
				licensePermitDetailsEntity.setModifiedOn(DateUtil.getDatefromString(DateUtil.getDateAsString(time)));
				licensePermitDetailsDAO.saveOrUpdate(licensePermitDetailsEntity);
			}
			if (ObjectsUtil.isNull(holderEntity)) {
				try {
					RegistrationServiceResponseModel<AadharModel> aadharRes = registrationService
							.getAadharDetails(Long.parseLong(appEntity.getCreatedBy()));
					String[] nameArr = aadharRes.getResponseBody().getName().trim().split(" ", 2);
					approvedDtlsEntity.setCreatedBy(appEntity.getCreatedBy());
					approvedDtlsEntity.setCreatedOn(appEntity.getCreatedOn());
					approvedDtlsEntity.setModifiedBy(userName);
					approvedDtlsEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
					approvedDtlsEntity.setApplicationEntity(appEntity);
					approvedDtlsEntity.setGuardianName(aadharRes.getResponseBody().getCo());
					approvedDtlsEntity.setFirstName(nameArr[0]);
					approvedDtlsEntity.setLastName(nameArr[1]);
					licenseHolderApprovedDetailsDAO.save(approvedDtlsEntity);
				} catch (NumberFormatException | UnauthorizedException e) {
					e.printStackTrace();
				}
			}
		}
		if (ObjectsUtil.isNull(rtTaskList) || rtTaskList.size() <= 0) {
			// -------- process completed -----------------------
			try {
				completeApp(instanceId, status, userName, userType, true);
				try {
					log.info(":::Start mail Process::");
					UserType user = UserType.valueOf(userRole);
					switch (user) {
					case ROLE_AO:
					case ROLE_RTO:
						sendSMSEmail(status, appEntity);
						break;
					}
				} catch (Exception e) {
					log.error(":::sendSMSEmail::Excepion :" + instanceId);
					e.printStackTrace();
				}
				log.info(":::end mail Process::");

			} catch (Exception ex) {
				log.error("Exception occured while completing the app, executionId : " + instanceId);
				ex.printStackTrace();
			}
		}
		return rtTaskList;
	}

	private void validateSlotIdIfRequired(ApplicationEntity appEntity, Long slotId) throws NotFoundException {
		log.info("validating if slot is required for this application");
		boolean isValidSlotId = true;
		List<SlotServiceType> slotServiceTypeList = slotService
				.getApplicableSlots(ServiceType.getServiceType(appEntity.getServiceCode()));
		if (!ObjectsUtil.isNullOrEmpty(slotServiceTypeList)) {
			log.info("application " + appEntity.getApplicationNumber() + " is found applicable for slots");
			isValidSlotId = false;
			List<SlotApplicationsEntity> slotAppsList = slotDAO
					.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(), appEntity.getIteration());
			if (!ObjectsUtil.isNullOrEmpty(slotAppsList)) {
				for (SlotApplicationsEntity slotEntity : slotAppsList) {
					if (!ObjectsUtil.isNull(slotId)
							&& slotEntity.getSlot().getSlotId().longValue() == slotId.longValue()) {
						isValidSlotId = true;
						break;
					}
				}
			}
		}
		if (!isValidSlotId) {
			log.error("slotId required but found invalid slotId : " + slotId + " for applicationNumber : "
					+ appEntity.getApplicationNumber() + ", unable to approve");
			throw new NotFoundException("invalid slotId");
		}
	}

	private boolean allSlotsProcessed(ApplicationEntity appEntity, Long slotId, Status status) {
		log.info("checking if all slots are approved for application : " + appEntity.getApplicationNumber());
		List<SlotApplicationsEntity> slotApplicationsEntityList = slotDAO
				.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(), appEntity.getIteration());
		if (ObjectsUtil.isNullOrEmpty(slotApplicationsEntityList)) {
			return true;
		}
		for (SlotApplicationsEntity slot : slotApplicationsEntityList) {
			if (Status.getStatus(slot.getApprovalStatus()) == Status.PENDING
					&& slotId.longValue() == slot.getSlot().getSlotId().longValue()) {
				slot.setApprovalStatus(status.getValue());
				if (ServiceType.DL_FRESH.getCode().equalsIgnoreCase(appEntity.getServiceCode())
						|| ServiceType.DL_ENDORSMENT.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
					String result = licensePermitDetailsService.update(appEntity.getApplicationId(),
							slot.getSlotServiceType(), status);
					log.info("getting update status in License permit details :::: " + result);
				}
				slotDAO.saveOrUpdateSlotApplications(slot);
				break;
			}
		}
		for (SlotApplicationsEntity slot : slotApplicationsEntityList) {
			if (Status.getStatus(slot.getApprovalStatus()) == Status.PENDING) {
				return false;
			}
		}
		return true;
	}

	@Override
	@Transactional
	public Integer getPendingApplicationCount(Long userId, String userName, String userRole, Long from, Long to,
			Integer perPageRecords, Integer pageNumber, HashMap<String, Object> variables) {
		log.info("getPendingApplicationCount " + " userId: " + userId + " userName: " + userName + " userRole"
				+ userRole);
		ActivitiResponseModel<List<RtaTaskInfo>> res = activitiService.getTasks(userName, variables);
		List<String> executionIdList = new ArrayList<>();
		List<ApplicationEntity> appEntities = new ArrayList<>();
		if (res.getStatus().equals(ActivitiResponseModel.SUCCESS)) {
			List<RtaTaskInfo> tasks = res.getActiveTasks();
			if (!ObjectsUtil.isNull(tasks)) {
				tasks.forEach(task -> {
					log.info("app execution : " + task.getProcessInstanceId());
					executionIdList.add(task.getProcessInstanceId());
				});
			}
			// get application list by executionId
			if (!(ObjectsUtil.isNull(executionIdList) || executionIdList.size() <= 0)) {
				appEntities = applicationDAO.getApplications(executionIdList);
			}
		}
		return appEntities.size();
	}

	@Override
	@Transactional
	public List<CitizenApplicationModel> getPendingApplications(Long userId, String userName, String userRole,
			Long from, Long to, Integer perPageRecords, Integer pageNumber, boolean slotApplicable,
			HashMap<String, Object> variables, String applicationNumber) {
		log.info(
				"getPendingApplications : " + " userId: " + userId + " userName: " + userName + " userRole" + userRole);

		// TODO: Remove/Replace this with actual current date WHEN TEST is done.
		long currentTime;
		if (ObjectsUtil.isNull(isSchedulingEnabled) || isSchedulingEnabled) {
			currentTime = DateUtil.toCurrentUTCTimeStamp();
		} else {
			currentTime = DateUtil.toCurrentUTCTimeStamp() + 1296000;// adding
																		// 15
																		// days
																		// to
																		// current
																		// date
		}
		// ---------opened apps -------------------------
		ActivitiResponseModel<List<RtaTaskInfo>> resOpened = activitiService.getAssignedTasks(userName, variables);
		List<String> openedExecutionIdList = new ArrayList<>();
		if (resOpened.getStatus().equals(ActivitiResponseModel.SUCCESS)) {
			List<RtaTaskInfo> tasks = resOpened.getActiveTasks();
			if (!ObjectsUtil.isNull(tasks)) {
				tasks.forEach(task -> {
					log.info("opened execution : " + task.getProcessInstanceId());
					openedExecutionIdList.add(task.getProcessInstanceId());
				});
			}
		}
		List<String> openedExecutionIdListNew = openedExecutionIdList;
		List<ServiceMasterEntity> serviceMasterEntityList;
		if (userRole.equalsIgnoreCase(UserType.ROLE_MVI.toString())) {
			serviceMasterEntityList = serviceMasterDAO.getServices(slotApplicable);
			if (!ObjectsUtil.isNullOrEmpty(serviceMasterEntityList)
					&& !ObjectsUtil.isNullOrEmpty(openedExecutionIdListNew)) {
				List<String> applicableServices = serviceMasterEntityList.stream().map(service -> service.getCode())
						.collect(Collectors.toList());
				openedExecutionIdListNew = applicationDAO.getApplications(openedExecutionIdList, applicableServices);
			}
		} else {
			serviceMasterEntityList = serviceMasterDAO.getAll();
		}
		ActivitiResponseModel<List<RtaTaskInfo>> res = activitiService.getTasks(userName, variables);
		List<String> executionIdList = new ArrayList<>();
		List<CitizenApplicationModel> openedAppList = new ArrayList<>();
		List<CitizenApplicationModel> appList = new ArrayList<>();
		if (res.getStatus().equals(ActivitiResponseModel.SUCCESS)) {
			List<RtaTaskInfo> tasks = res.getActiveTasks();
			if (!ObjectsUtil.isNull(tasks)) {
				tasks.forEach(task -> {
					log.info("pending execution : " + task.getProcessInstanceId());
					executionIdList.add(task.getProcessInstanceId());
				});
			}

			// get application list by executionId
			List<String> executionIdListNew = executionIdList;
			List<ApplicationEntity> appEntities = new ArrayList<>();
			if (!ObjectsUtil.isNull(executionIdListNew) && executionIdListNew.size() > 0) {
				if (userRole.equalsIgnoreCase(UserType.ROLE_MVI.toString())) {
					serviceMasterEntityList = serviceMasterDAO.getServices(slotApplicable);
					if (!ObjectsUtil.isNullOrEmpty(serviceMasterEntityList)) {
						List<String> applicableServices = serviceMasterEntityList.stream()
								.map(service -> service.getCode()).collect(Collectors.toList());
						executionIdListNew = applicationDAO.getApplications(executionIdList, applicableServices);
					} else {
						log.error("no service found");
						return new ArrayList<>();
					}
					if (ObjectsUtil.isNullOrEmpty(executionIdListNew)) {
						log.error("no applications found");
						return new ArrayList<>();
					}
					if (slotApplicable) {
						appEntities = applicationDAO.getApplications(executionIdListNew, currentTime);
					} else {
						appEntities = applicationDAO.getApplications(executionIdListNew);
					}
				} else {
					appEntities = applicationDAO.getApplications(executionIdListNew);
				}
			} else {
				log.error("no applications found");
				return new ArrayList<>();
			}
			for (ApplicationEntity appEntity : appEntities) {
				if (!ObjectsUtil.isNull(applicationNumber) && !applicationNumber.isEmpty())
					if (!appEntity.getApplicationNumber().equals(applicationNumber)) {
						continue;
					}
				CitizenApplicationModel app = new CitizenApplicationModel();
				app.setIteration(appEntity.getIteration());
				app.setApplicationNumber(appEntity.getApplicationNumber());
				UserSessionEntity sessionEntity = appEntity.getLoginHistory();
				ServiceType service = ServiceType.getServiceType(sessionEntity.getServiceCode());
				app.setServiceType(service);
				app.setServiceTypeText(service.getLabel());
				if (slotApplicable && userRole.equalsIgnoreCase(UserType.ROLE_MVI.toString())) {
					List<SlotApplicationsEntity> slotApplicationsEntityList = slotDAO
							.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(),
									appEntity.getIteration());
					List<SlotModel> slotsList = new ArrayList<>();
					slotApplicationsEntityList.stream().forEach(slotApp -> {
						if (Status.getStatus(slotApp.getApprovalStatus()) == Status.PENDING) {
							SlotEntity slotEntity = slotApp.getSlot();
							SlotModel slotModel = new SlotModel();
							slotModel.setDuration(slotEntity.getDuration());
							slotModel.setEndTime(slotEntity.getEndTime());
							slotModel.setScheduledDate(slotEntity.getScheduledDate());
							slotModel.setScheduledTime(slotEntity.getScheduledTime());
							slotModel.setSlotId(slotEntity.getSlotId());
							slotModel.setSlotStatus(slotApp.getSlotStatus());
							slotModel.setStartTime(slotEntity.getStartTime());
							slotModel.setType(slotApp.getSlotServiceType());
							slotModel.setStatus(Status.getStatus(slotApp.getApprovalStatus()));
							slotsList.add(slotModel);
						}
					});
					app.setSlot(slotsList);
				}
				if (openedExecutionIdListNew.contains(appEntity.getExecutionId())) {
					app.setAppStatus(Status.OPEN);
					openedAppList.add(app);
				} else {
					app.setAppStatus(Status.PENDING);
					appList.add(app);
				}
			}
		}
		if (openedAppList.size() > 0) {
			appList.addAll(0, openedAppList);
		}
		return appList;
	}

	@Override
	@Transactional
	public Integer getApplicationCount(Status status, Long userId, String userName, String userRole,
			ServiceType serviceType, Long from, Long to, Integer perPageRecords, Integer pageNumber,
			ServiceCategory serviceCategory) {
		log.info("getApplicationCount : " + " userId: " + userId + " userName: " + userName + " userRole" + userRole
				+ " status: " + status);
		List<ApplicationApprovalHistoryEntity> historyList = applicationApprovalHistoryDAO.getApprovalHistories(userId,
				status, null, serviceType, serviceCategory);
		Set<String> appSet = new HashSet<>();
		for (ApplicationApprovalHistoryEntity history : historyList) {
			ApplicationEntity appEntity = history.getApplicationEntity();
			if (ObjectsUtil.isNull(appEntity)) {
				continue;
			}
			appSet.add(appEntity.getApplicationNumber());
		}
		return appSet.size();
	}

	@Override
	@Transactional
	public List<CitizenApplicationModel> getApplications(Status status, Long userId, String userName, String userRole,
			ServiceType serviceType, Long from, Long to, Integer perPageRecords, Integer pageNumber,
			ServiceCategory serviceCategory, String applicationNumber) {
		log.info("getApplications : " + " userId: " + userId + " userName: " + userName + " userRole" + userRole
				+ " status: " + status);
		List<ApplicationApprovalHistoryEntity> historyList = applicationApprovalHistoryDAO.getApprovalHistories(userId,
				status, null, serviceType, serviceCategory);
		List<CitizenApplicationModel> appList = new ArrayList<>();
		List<String> addedApp = new ArrayList<>();

		for (ApplicationApprovalHistoryEntity history : historyList) {

			CitizenApplicationModel app = new CitizenApplicationModel();
			ApplicationEntity appEntity = history.getApplicationEntity();
			UserSessionEntity userSession = appEntity.getLoginHistory();
			if (!StringsUtil.isNullOrEmpty(applicationNumber))
				if (!appEntity.getApplicationNumber().equals(applicationNumber)) {
					continue;
				}
			if (ObjectsUtil.isNull(appEntity) || addedApp.contains(appEntity.getApplicationNumber())) {
				continue;
			}
			app.setApplicationNumber(appEntity.getApplicationNumber());
			addedApp.add(appEntity.getApplicationNumber());
			app.setServiceType(ServiceType.getServiceType(userSession.getServiceCode()));
			app.setServiceTypeText(ServiceType.getServiceType(userSession.getServiceCode()).getLabel());

			// added slots in approved/rejected lists for MVI
			if (userRole.equalsIgnoreCase(UserType.ROLE_MVI.toString())) {
				List<SlotModel> slots;
				List<SlotApplicationsEntity> slotApplicationsEntityList = slotDAO
						.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(),
								appEntity.getIteration());
				if (!ObjectsUtil.isNullOrEmpty(slotApplicationsEntityList)) {
					slots = new ArrayList<>();
					slotApplicationsEntityList.stream().forEach(slotApp -> {
						if (Status.getStatus(slotApp.getApprovalStatus()) == status) {
							SlotEntity slotEntity = slotApp.getSlot();
							SlotModel slotModel = new SlotModel();
							slotModel.setDuration(slotEntity.getDuration());
							slotModel.setEndTime(slotEntity.getEndTime());
							slotModel.setScheduledDate(slotEntity.getScheduledDate());
							slotModel.setScheduledTime(slotEntity.getScheduledTime());
							slotModel.setSlotId(slotEntity.getSlotId());
							slotModel.setSlotStatus(slotApp.getSlotStatus());
							slotModel.setStartTime(slotEntity.getStartTime());
							slotModel.setType(slotApp.getSlotServiceType());
							slotModel.setStatus(Status.getStatus(slotApp.getApprovalStatus()));
							slots.add(slotModel);
						}
					});
					app.setSlot(slots);
				}
			}
			if (ServiceType.getServiceType(
					history.getApplicationEntity().getServiceCode()) == ServiceType.REGISTRATION_SUS_CANCELLATION) {
				try {
					RegistrationServiceResponseModel<CustomerDetailsRequestModel> res = registrationService
							.getCustomerDetails(appEntity.getLoginHistory().getVehicleRcId());
					CustomerDetailsRequestModel appModel = res.getResponseBody();
					if (!ObjectsUtil.isNull(appModel)) {
						OwnershipType ownershipType = OwnershipType.getOwnershipType(appModel.getOwnershipType());
						switch (ownershipType) {
						case POLICE:
						case STU_VEHICLES:
						case COMPANY:
							app.setFirstName(appModel.getFirst_name());
							app.setSurName(appModel.getFirst_name());
							break;
						case DIPLOMATIC_OFFICER:
							break;
						case GOVERNMENT:
							break;
						case INDIVIDUAL:
							app.setFirstName(appModel.getFirst_name());
							app.setSurName(appModel.getSurName());
							app.setCareOff(appModel.getFather_name());
							break;
						case ORGANIZATION:
							break;
						}
						app.setAppStatus(status);
						appList.add(app);
					}
				} catch (Exception e) {
					log.error("Exception While getting data for vehicle : " + userSession.getUniqueKey() + ", appno : "
							+ appEntity.getApplicationNumber());
				}
			} else {
				try {
					if (serviceCategory.equals(ServiceCategory.LL_CATEGORY)
							|| serviceCategory.equals(ServiceCategory.DL_CATEGORY)) {
						LicenseHolderApprovedDetailsEntity holderEntity = licenseHolderApprovedDetailsDAO
								.getHolderApprovedDetails(appEntity.getApplicationId());
						if (!ObjectsUtil.isNull(holderEntity)) {
							app.setFirstName(holderEntity.getFirstName());
							app.setSurName(holderEntity.getLastName());
							app.setCareOff(holderEntity.getGuardianName());
						} else {
							log.info("Getting response null for licenseHolderDetails with aadharNo "
									+ userSession.getAadharNumber());
						}
					} else {
						RegistrationServiceResponseModel<AadharModel> res = registrationService
								.getAadharDetails(Long.parseLong(userSession.getAadharNumber()));
						if (res.getHttpStatus().equals(HttpStatus.OK)) {
							AadharModel aadhar = res.getResponseBody();
							String[] nameArr = aadhar.getName().trim().split(" ", 2);
							app.setFirstName(nameArr[0]);
							if (nameArr.length > 1) {
								app.setSurName(nameArr[1]);
							} else {
								app.setSurName("");
							}
							app.setCareOff(aadhar.getCo());
						} else {
							log.error("Response code while calling aadhar data from Registration : "
									+ res.getHttpStatus().value());
						}
					}
				} catch (Exception ex) {
					log.error("Exception While getting data from Aadhar table (registration) for aadhar: "
							+ userSession.getAadharNumber() + " appno : " + appEntity.getApplicationNumber());
				}
				app.setAppStatus(status);
				appList.add(app);
			}
		}
		return appList;
	}

	@Override
	@Transactional
	public ApplicationStatusModel getAppStatus(String appNo) {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		RegistrationServiceResponseModel<FreshRcModel> freshRcResponse = null;
		try {
			freshRcResponse = registrationService.getFreshRcDataByApplicationNumber(appNo);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		mdl.setServiceCode(appEntity.getServiceCode());
		mdl.setOverAllStatus(Status.getStatus(appEntity.getLoginHistory().getCompletionStatus()));
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO
				.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for (ApplicationApprovalHistoryEntity history : histpryEntityList) {
			UserType userType = Enum.valueOf(UserType.class, history.getRtaUserRole());
			switch (userType) {
			case ROLE_SELLER:
				mdl.setSellerStatus(Status.getStatus(history.getStatus()));
				mdl.setSellerActionDate(history.getCreatedOn());
				mdl.setSellerRemark(history.getComments());
				break;
			case ROLE_BUYER:
				mdl.setBuyerStatus(Status.getStatus(history.getStatus()));
				mdl.setBuyerActionDate(history.getCreatedOn());
				mdl.setBuyerRemark(history.getComments());
				break;
			case ROLE_ONLINE_FINANCER:
				mdl.setFinancierStatus(Status.getStatus(history.getStatus()));
				mdl.setFinancierActionDate(history.getCreatedOn());
				mdl.setFinancierRemark(history.getComments());
				break;
			case ROLE_CCO:
				mdl.setCcoStatus(Status.getStatus(history.getStatus()));
				mdl.setCcoActionDate(history.getCreatedOn());
				mdl.setCcoRemark(history.getComments());
				break;
			case ROLE_MVI:
				mdl.setMviStatus(Status.getStatus(history.getStatus()));
				mdl.setMviActionDate(history.getCreatedOn());
				mdl.setMviRemark(history.getComments());
				break;
			case ROLE_AO:
				mdl.setAoStatus(Status.getStatus(history.getStatus()));
				mdl.setAoActionDate(history.getCreatedOn());
				mdl.setAoRemark(history.getComments());
				break;
			case ROLE_RTO:
				mdl.setRtoStatus(Status.getStatus(history.getStatus()));
				mdl.setRtoActionDate(history.getCreatedOn());
				mdl.setRtoRemark(history.getComments());
				break;
			case ROLE_DTC:
				mdl.setDtcStatus(Status.getStatus(history.getStatus()));
				mdl.setDtcActionDate(history.getCreatedOn());
				mdl.setDtcRemark(history.getComments());
				break;
			case ROLE_EXAMINER:
				mdl.setExamStatus(Status.getStatus(history.getStatus()));
				mdl.setExamActionDate(history.getCreatedOn());
				mdl.setExamRemark(history.getComments());
				break;
			default:
				break;
			}
		}
		FreshRcModel freshRc = null;
		String citizenStatus = null;
		if(freshRcResponse.getHttpStatus() == HttpStatus.OK){
			if(freshRcResponse.getResponseBody().getOwnerConsent() == null){
				citizenStatus = "PENDING";
			} else if(freshRcResponse.getResponseBody().getOwnerConsent()){
				citizenStatus = "APPROVED";
			} else if(!freshRcResponse.getResponseBody().getOwnerConsent()){
				citizenStatus = "REJECTED";
			}
			freshRc = freshRcResponse.getResponseBody();
				mdl.setCitizenStatus(Status.getStatus(citizenStatus));
				mdl.setCitizenActionDate(freshRcResponse.getResponseBody().getOwnerConscentDate());
				mdl.setCitizenRemark(freshRcResponse.getResponseBody().getOwnerComment());
		}
		return mdl;
	}

	@Override
	@Transactional
	public void completeApp(String executionId, Status status, String approverName, UserType userType,
			Boolean isAppCompleted) {
		log.info("Going to Complete App and sync with executionId : " + executionId);
		ApplicationEntity appEntity = applicationDAO.getApplicationByExecutionId(executionId);
		UserSessionEntity userSession = appEntity.getLoginHistory();
		if (!ObjectsUtil.isNull(isAppCompleted) && isAppCompleted) {
			userSession.setCompletionStatus(status.getValue());
			userSessionDAO.saveOrUpdate(userSession);
		}

		userSessionDAO.getSession().flush();

		if (ServiceCategory.getServiceTypeCat(appEntity.getServiceCategory())
				.equals(ServiceCategory.PERMIT_FITNESS_CATEGORY)) {
			syncService.syncData(status, appEntity, userSession, approverName);
			log.info(":Citizen::syncService::::end:::: ");
		} else {
			syncService.syncApprovedApplications(status, appEntity, userSession, approverName);
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getInfo(String applicationNumber, String authToken) throws NotFoundException {

		ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNumber);
		if (ObjectsUtil.isNull(applicationEntity)) {
			log.error("application not found for application number : " + applicationNumber);
			throw new NotFoundException("application not found");
		}
		UserSessionEntity sessionEntity = applicationEntity.getLoginHistory();
		ServiceType serviceType = ServiceType.getServiceType(sessionEntity.getServiceCode());
		Map<String, Object> mapObject = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		switch (serviceType) {
		case ADDRESS_CHANGE:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.AC_FORM.getLabel());
				AddressChangeModel model = new ObjectMapper().readValue(entity.getFormData(), AddressChangeModel.class);
				map = getObjectMap(map, model);

			} catch (Exception ex) {
				log.error("Getting error in Change of Address");
			}
			break;
		case ALTERATION_AGENCY_SIGNUP:
			break;
		case BODYBUILDER_SIGNUP:
			break;
		case DEALER_SIGNUP:
			break;
		case DEFAULT:
			break;
		case DIFFERENTIAL_TAX:
			try {
				ApplicationFormDataEntity appFormDataEntity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), "dt_form");
				ObjectMapper mapper = new ObjectMapper();
				PermitTypeModel permitTypeModel = new PermitTypeModel();

				permitTypeModel = mapper.readValue(appFormDataEntity.getFormData(), PermitTypeModel.class);
				map.put("permitType", permitTypeModel.getCode());
				map.put("permitTypeName", permitTypeModel.getName());
			} catch (Exception e) {
				log.error("error in getting permit details for application number : " + applicationNumber);
			}
			break;
		case DL_BADGE:
			try {
				ApplicationFormDataEntity courseDetailsEntity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.DLB_FORM.getLabel());
				if (!ObjectsUtil.isNull(courseDetailsEntity)) {
					Map<String, Object> courseDetailsnMap = new HashMap<>();
					CourseCertificateModel courseDetailsModel = new ObjectMapper()
							.readValue(courseDetailsEntity.getFormData(), CourseCertificateModel.class);
					courseDetailsnMap.put("certificateNumber", courseDetailsModel.getCetificateNumber());
					courseDetailsnMap.put("inistituteName", courseDetailsModel.getInstituteName());
					courseDetailsnMap.put("certificateIssueDate", courseDetailsModel.getIssueCertificateDate());
					courseDetailsnMap.put("trainingFrom", courseDetailsModel.getTrainingFrom());
					courseDetailsnMap.put("trainingTo", courseDetailsModel.getTrainingTo());
					map.put(FormCodeType.DLB_FORM.getLabel(), courseDetailsnMap);
				}
			} catch (Exception ex) {
				log.error("Getting error in DL Badge Details");
			}
			break;
		case DL_CHANGEADDRS_OS:
			try {
				ApplicationFormDataEntity dlosEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLOS_DETAIL_FORM.getLabel());
				DLOSChecklistDetailsModel dlosModel = new ObjectMapper().readValue(dlosEntity.getFormData(),
						DLOSChecklistDetailsModel.class);
				Map<String, Object> dlosRegistrationMap = new HashMap<>();
				dlosRegistrationMap.put("isSameAadhar", dlosModel.getIsSameAadhar());
				dlosRegistrationMap.put("doorNo", dlosModel.getDoorNo());
				dlosRegistrationMap.put("street", dlosModel.getStreet());
				dlosRegistrationMap.put("city", dlosModel.getCity());
				dlosRegistrationMap.put("districtCode", dlosModel.getDistrictCode());
				dlosRegistrationMap.put("stateCode", dlosModel.getStateCode());
				dlosRegistrationMap.put("countryCode", dlosModel.getCountryCode());
				dlosRegistrationMap.put("mandalName", dlosModel.getMandalName());
				dlosRegistrationMap.put("districtName", dlosModel.getDistrictName());
				dlosRegistrationMap.put("stateName", dlosModel.getStateName());
				dlosRegistrationMap.put("countryName", dlosModel.getCountryName());
				dlosRegistrationMap.put("mandalCode", dlosModel.getMandalCode());
				dlosRegistrationMap.put("postOffice", dlosModel.getPostOffice());
				dlosRegistrationMap.put("mobileNo", dlosModel.getMobileNo());
				dlosRegistrationMap.put("bloodGroup", dlosModel.getBloodGroup());
				dlosRegistrationMap.put("emailId", dlosModel.getEmailId());
				dlosRegistrationMap.put("displayName", dlosModel.getDisplayName());
				dlosRegistrationMap.put("fullName", dlosModel.getFullName());
				dlosRegistrationMap.put("qualification", dlosModel.getQualification());
				dlosRegistrationMap.put("selfDecalartion", dlosModel.getSelfDecalartion());
				dlosRegistrationMap.put("dlValidityFrom", dlosModel.getDlValidityFrom());
				dlosRegistrationMap.put("dlValidityTo", dlosModel.getDlValidityTo());
				map.put(FormCodeType.DLOS_DETAIL_FORM.getLabel(), dlosRegistrationMap);
				ApplicationFormDataEntity medicalDetailsEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLOS_MEDICAL_FORM.getLabel());
				if (!ObjectsUtil.isNull(medicalDetailsEntity)) {
					Map<String, Object> medicalDetailsnMap = new HashMap<>();
					MedicalDetailsModel medicalDetailsModel = new ObjectMapper()
							.readValue(medicalDetailsEntity.getFormData(), MedicalDetailsModel.class);
					medicalDetailsnMap.put("medicalType", medicalDetailsModel.getMedicalType());
					medicalDetailsnMap.put("certificateIssueDate", medicalDetailsModel.getCertificateIssueDate());
					medicalDetailsnMap.put("doctorName", medicalDetailsModel.getDoctorName());
					medicalDetailsnMap.put("registrationNumber", medicalDetailsModel.getRegistrationNumber());
					map.put(FormCodeType.DLOS_MEDICAL_FORM.getLabel(), medicalDetailsnMap);
				}
			} catch (Exception ex) {
				log.error("Getting error in update Or save in DL Other State Details");
			}
			break;
		case DL_CHANGE_ADDRESS:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLCA_FORM.getLabel());
				LLRegistrationModel dlcaRegistrationModel = new ObjectMapper().readValue(entity.getFormData(),
						LLRegistrationModel.class);
				Map<String, Object> dlcaRegistrationMap = new HashMap<>();
				dlcaRegistrationMap.put("isSameAadhar", dlcaRegistrationModel.getIsSameAadhar());
				dlcaRegistrationMap.put("doorNo", dlcaRegistrationModel.getDoorNo());
				dlcaRegistrationMap.put("street", dlcaRegistrationModel.getStreet());
				dlcaRegistrationMap.put("city", dlcaRegistrationModel.getCity());
				dlcaRegistrationMap.put("districtCode", dlcaRegistrationModel.getDistrictCode());
				dlcaRegistrationMap.put("stateCode", dlcaRegistrationModel.getStateCode());
				dlcaRegistrationMap.put("countryCode", dlcaRegistrationModel.getCountryCode());
				dlcaRegistrationMap.put("mandalName", dlcaRegistrationModel.getMandalName());
				dlcaRegistrationMap.put("districtName", dlcaRegistrationModel.getDistrictName());
				dlcaRegistrationMap.put("stateName", dlcaRegistrationModel.getStateName());
				dlcaRegistrationMap.put("countryName", dlcaRegistrationModel.getCountryName());
				dlcaRegistrationMap.put("mandalCode", dlcaRegistrationModel.getMandalCode());
				dlcaRegistrationMap.put("postOffice", dlcaRegistrationModel.getPostOffice());
				dlcaRegistrationMap.put("mobileNo", dlcaRegistrationModel.getMobileNo());
				dlcaRegistrationMap.put("bloodGroup", dlcaRegistrationModel.getBloodGroup());
				dlcaRegistrationMap.put("emailId", dlcaRegistrationModel.getEmailId());
				dlcaRegistrationMap.put("selfDecalartion", dlcaRegistrationModel.getSelfDecalartion());
				map.put(FormCodeType.DLCA_FORM.getLabel(), dlcaRegistrationMap);
			} catch (Exception ex) {
				log.error("Getting error in Change of Address" + ex);
			}
			break;
		case DL_DLINFO:
			break;
		case DL_DUPLICATE:
			break;
		case DL_ENDORSMENT:
			try {
				ApplicationFormDataEntity courseDetailsEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLE_COURSE_FORM.getLabel());
				if (!ObjectsUtil.isNull(courseDetailsEntity)) {
					Map<String, Object> courseDetailsnMap = new HashMap<>();
					CourseCertificateModel courseDetailsModel = new ObjectMapper()
							.readValue(courseDetailsEntity.getFormData(), CourseCertificateModel.class);
					courseDetailsnMap.put("certificateNumber", courseDetailsModel.getCetificateNumber());
					courseDetailsnMap.put("inistituteName", courseDetailsModel.getInstituteName());
					courseDetailsnMap.put("certificateIssueDate", courseDetailsModel.getIssueCertificateDate());
					courseDetailsnMap.put("trainingFrom", courseDetailsModel.getTrainingFrom());
					courseDetailsnMap.put("trainingTo", courseDetailsModel.getTrainingTo());
					map.put(FormCodeType.DLE_COURSE_FORM.getLabel(), courseDetailsnMap);
				}
			} catch (Exception ex) {
				log.error("Getting error in update Or save in DL Endorsement Details");
			}
			break;
		case DL_EXPIRED:
			break;
		case DL_FOREIGN_CITIZEN:
			try {
				ApplicationFormDataEntity dlfcRegistrationEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLFC_DETAIL_FORM.getLabel());
				DLForiegnCitizenDetailsModel dlfcRegistrationModel = new ObjectMapper()
						.readValue(dlfcRegistrationEntity.getFormData(), DLForiegnCitizenDetailsModel.class);
				Map<String, Object> dlfcRegistrationMap = new HashMap<>();
				dlfcRegistrationMap.put("isSameAadhar", dlfcRegistrationModel.getIsSameAadhar());
				dlfcRegistrationMap.put("doorNo", dlfcRegistrationModel.getDoorNo());
				dlfcRegistrationMap.put("street", dlfcRegistrationModel.getStreet());
				dlfcRegistrationMap.put("city", dlfcRegistrationModel.getCity());
				dlfcRegistrationMap.put("districtCode", dlfcRegistrationModel.getDistrictCode());
				dlfcRegistrationMap.put("stateCode", dlfcRegistrationModel.getStateCode());
				dlfcRegistrationMap.put("countryCode", dlfcRegistrationModel.getCountryCode());
				dlfcRegistrationMap.put("mandalName", dlfcRegistrationModel.getMandalName());
				dlfcRegistrationMap.put("districtName", dlfcRegistrationModel.getDistrictName());
				dlfcRegistrationMap.put("stateName", dlfcRegistrationModel.getStateName());
				dlfcRegistrationMap.put("countryName", dlfcRegistrationModel.getCountryName());
				dlfcRegistrationMap.put("mandalCode", dlfcRegistrationModel.getMandalCode());
				dlfcRegistrationMap.put("postOffice", dlfcRegistrationModel.getPostOffice());
				dlfcRegistrationMap.put("mobileNo", dlfcRegistrationModel.getMobileNo());
				dlfcRegistrationMap.put("emailId", dlfcRegistrationModel.getEmailId());
				dlfcRegistrationMap.put("displayName", dlfcRegistrationModel.getDisplayName());
				dlfcRegistrationMap.put("fullName", dlfcRegistrationModel.getFullName());
				dlfcRegistrationMap.put("qualification", dlfcRegistrationModel.getQualification());
				dlfcRegistrationMap.put("bloodGroup", dlfcRegistrationModel.getBloodGroup());
				dlfcRegistrationMap.put("selfDecalartion", dlfcRegistrationModel.getSelfDecalartion());
				dlfcRegistrationMap.put("passportNumber", dlfcRegistrationModel.getPassportNumber());
				dlfcRegistrationMap.put("visaValidityFrom", dlfcRegistrationModel.getVisaValidityFrom());
				dlfcRegistrationMap.put("visaValidityTo", dlfcRegistrationModel.getVisaValidityTo());
				dlfcRegistrationMap.put("foreignFrom", dlfcRegistrationModel.getForeignFrom());
				dlfcRegistrationMap.put("foreignTo", dlfcRegistrationModel.getForeignTo());
				dlfcRegistrationMap.put("passportFrom", dlfcRegistrationModel.getPassportFrom());
				dlfcRegistrationMap.put("passportTo", dlfcRegistrationModel.getPassportTo());
				map.put(FormCodeType.DLFC_DETAIL_FORM.getLabel(), dlfcRegistrationMap);
				ApplicationFormDataEntity medicalDetailsEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLFC_MEDICAL_FORM.getLabel());
				if (!ObjectsUtil.isNull(medicalDetailsEntity)) {
					Map<String, Object> medicalDetailsnMap = new HashMap<>();
					MedicalDetailsModel medicalDetailsModel = new ObjectMapper()
							.readValue(medicalDetailsEntity.getFormData(), MedicalDetailsModel.class);
					medicalDetailsnMap.put("medicalType", medicalDetailsModel.getMedicalType());
					medicalDetailsnMap.put("certificateIssueDate", medicalDetailsModel.getCertificateIssueDate());
					medicalDetailsnMap.put("doctorName", medicalDetailsModel.getDoctorName());
					medicalDetailsnMap.put("registrationNumber", medicalDetailsModel.getRegistrationNumber());
					map.put(FormCodeType.DLFC_MEDICAL_FORM.getLabel(), medicalDetailsnMap);
				}

			} catch (Exception ex) {
				log.error("Getting error in update Or save in DL Foreign Details");
			}
			break;
		case DL_FRESH:
			break;
		case DL_INT_PERMIT:
			try {
				ApplicationFormDataEntity afe = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLIN_FORM.getLabel());
				if (!ObjectsUtil.isNull(afe)) {
					Map<String, Object> iPermitDetailsnMap = new HashMap<>();
					InternationalPermitModel iPermitModel = new ObjectMapper().readValue(afe.getFormData(),
							InternationalPermitModel.class);
					iPermitDetailsnMap.put("passportNumber", iPermitModel.getPassportNumber());
					iPermitDetailsnMap.put("issuedBy", iPermitModel.getIssuedBy());
					iPermitDetailsnMap.put("issusedDate", iPermitModel.getIssusedDate());
					iPermitDetailsnMap.put("expiryDate", iPermitModel.getExpiryDate());
					iPermitDetailsnMap.put("countryCode", iPermitModel.getCountryCode());
					iPermitDetailsnMap.put("countryName", iPermitModel.getCountryName());
					iPermitDetailsnMap.put("stayPeriod", iPermitModel.getStayPeriod());
					map.put(FormCodeType.DLIN_FORM.getLabel(), iPermitDetailsnMap);
				}
			} catch (Exception ex) {
				log.error("Getting error in update Or save in DL Endorsement Details");
			}
			break;
		case DL_MILITRY:
			try {
				ApplicationFormDataEntity dlmRegistrationEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLM_DETAIL_FORM.getLabel());
				DLMilataryDetailsModel dlmRegistrationModel = new ObjectMapper()
						.readValue(dlmRegistrationEntity.getFormData(), DLMilataryDetailsModel.class);
				Map<String, Object> dlmRegistrationMap = new HashMap<>();
				dlmRegistrationMap.put("isSameAadhar", dlmRegistrationModel.getIsSameAadhar());
				dlmRegistrationMap.put("doorNo", dlmRegistrationModel.getDoorNo());
				dlmRegistrationMap.put("street", dlmRegistrationModel.getStreet());
				dlmRegistrationMap.put("city", dlmRegistrationModel.getCity());
				dlmRegistrationMap.put("districtCode", dlmRegistrationModel.getDistrictCode());
				dlmRegistrationMap.put("stateCode", dlmRegistrationModel.getStateCode());
				dlmRegistrationMap.put("countryCode", dlmRegistrationModel.getCountryCode());
				dlmRegistrationMap.put("mandalName", dlmRegistrationModel.getMandalName());
				dlmRegistrationMap.put("districtName", dlmRegistrationModel.getDistrictName());
				dlmRegistrationMap.put("stateName", dlmRegistrationModel.getStateName());
				dlmRegistrationMap.put("countryName", dlmRegistrationModel.getCountryName());
				dlmRegistrationMap.put("mandalCode", dlmRegistrationModel.getMandalCode());
				dlmRegistrationMap.put("postOffice", dlmRegistrationModel.getPostOffice());
				dlmRegistrationMap.put("mobileNo", dlmRegistrationModel.getMobileNo());
				dlmRegistrationMap.put("emailId", dlmRegistrationModel.getEmailId());
				dlmRegistrationMap.put("bloodGroup", dlmRegistrationModel.getBloodGroup());
				dlmRegistrationMap.put("displayName", dlmRegistrationModel.getDisplayName());
				dlmRegistrationMap.put("fullName", dlmRegistrationModel.getFullName());
				dlmRegistrationMap.put("qualification", dlmRegistrationModel.getQualification());
				dlmRegistrationMap.put("selfDecalartion", dlmRegistrationModel.getSelfDecalartion());
				dlmRegistrationMap.put("dlValidityFrom", dlmRegistrationModel.getDlValidityFrom());
				dlmRegistrationMap.put("dlValidityTo", dlmRegistrationModel.getDlValidityTo());
				dlmRegistrationMap.put("rankunit", dlmRegistrationModel.getRankunit());
				dlmRegistrationMap.put("dlIssueAuthority", dlmRegistrationModel.getDlIssueAuthority());
				map.put(FormCodeType.DLM_DETAIL_FORM.getLabel(), dlmRegistrationMap);
				ApplicationFormDataEntity medicalDetailsEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLM_MEDICAL_FORM.getLabel());
				if (!ObjectsUtil.isNull(medicalDetailsEntity)) {
					Map<String, Object> medicalDetailsnMap = new HashMap<>();
					MedicalDetailsModel medicalDetailsModel = new ObjectMapper()
							.readValue(medicalDetailsEntity.getFormData(), MedicalDetailsModel.class);
					medicalDetailsnMap.put("medicalType", medicalDetailsModel.getMedicalType());
					medicalDetailsnMap.put("certificateIssueDate", medicalDetailsModel.getCertificateIssueDate());
					medicalDetailsnMap.put("doctorName", medicalDetailsModel.getDoctorName());
					medicalDetailsnMap.put("registrationNumber", medicalDetailsModel.getRegistrationNumber());
					map.put(FormCodeType.DLM_MEDICAL_FORM.getLabel(), medicalDetailsnMap);
				}

			} catch (Exception ex) {
				log.error("Getting error in update Or save in DL Forigen Details");
			}
			break;
		case DL_RENEWAL:
			break;
		case DL_RETEST:
			break;
		case DL_SURRENDER:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.DLS_FORM.getLabel());
				DLSurrenderDetailsModel model = new ObjectMapper().readValue(entity.getFormData(),
						DLSurrenderDetailsModel.class);
				Map<String, Object> modelMap = new HashMap<>();
				modelMap.put("comment", model.getComment());
				modelMap.put("dlsReason", model.getDlsReason());
				modelMap.put("dlNumber", model.getDlNumber());
				modelMap.put("otherDlNumber", model.getOtherDlNumber());
				modelMap.put("mergeStatus", model.getMergeStatus());
				map.put(FormCodeType.DLS_FORM.getLabel(), modelMap);
			} catch (Exception ex) {
				log.error("error in getting DL Surrender details for application number : " + applicationNumber);
			}
			break;
		case DL_REVO_SUS:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.DLC_FORM.getLabel());
				SuspensionRevocationModel model = new ObjectMapper().readValue(entity.getFormData(),
						SuspensionRevocationModel.class);
				map.put("remarks", model.getRemarks());
			} catch (Exception ex) {
				log.error("error in getting DL Revocation of suspesion details for application number : "
						+ applicationNumber);
			}
			break;
		case DL_SUSU_CANC:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.DLSC_FORM.getLabel());
				SupensionCancellationModel model = new ObjectMapper().readValue(entity.getFormData(),
						SupensionCancellationModel.class);
				Map<String, Object> modelMap = new HashMap<>();
				modelMap.put("reason", model.getReason());
				modelMap.put("comment", model.getComment());
				modelMap.put("suspensionType", model.getSuspensionType());
				modelMap.put("vehicleRc", model.getVehicleRc());
				modelMap.put("sectionName", model.getSectionName());
				modelMap.put("vehicleReason", model.getVehicleReason());
				modelMap.put("cov", model.getCov());
				modelMap.put("vcrNo", model.getVcrNo());
				modelMap.put("mviName", model.getMviName());
				modelMap.put("vcrDate", model.getVcrDate());
				map.put(FormCodeType.DLSC_FORM.getLabel(), modelMap);
			} catch (Exception ex) {
				log.error("error in getting DL SuspesionCancellation details for application number : "
						+ applicationNumber);
			}
			break;
		case DUPLICATE_REGISTRATION:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.DR_FORM.getLabel());
				DuplicateRegistrationModel model = new ObjectMapper().readValue(entity.getFormData(),
						DuplicateRegistrationModel.class);
				map.put("reason", model.getReason());
				map.put("comment", model.getComment());
			} catch (Exception ex) {
				log.error("error in getting duplication registration details for application number : "
						+ applicationNumber);
			}
			break;
		case FINANCIER_SIGNUP:
			break;
		case FRESH_RC_FINANCIER:
			Long vehicleRcId = sessionEntity.getVehicleRcId();
			map.put("vehicleRcId", vehicleRcId);
			FreshRcModel freshRcModel = null;
			try {
				RegistrationServiceResponseModel<FreshRcModel> response = registrationService
						.getFreshRcDataByAadharAndVehicleRcId(vehicleRcId, sessionEntity.getAadharNumber());
				if (response.getHttpStatus() == HttpStatus.OK) {
					freshRcModel = response.getResponseBody();
					map.put("vehicleUnderPossession", freshRcModel.getVehicleUnderPossession());
					map.put("ownerConsent", freshRcModel.getOwnerConsent());
					FinanceYardModel yard = freshRcModel.getYard();
					if (yard != null) {
						map.put("yardId", yard.getYardId());
						map.put("yardName", yard.getYardName());
					}
					map.put("amountDue", freshRcModel.getAmountDue());
					map.put("overDueSince", freshRcModel.getOverDueSince());
					map.put("ownerComment", freshRcModel.getOwnerComment());
					map.put("defaultedAmount", freshRcModel.getDefaultedAmount());
					map.put("noOfEmi", freshRcModel.getNoOfEmi());
				}
			} catch (UnauthorizedException e1) {
				log.error("unauthorized when getting fresh rc data");
			}
			break;
		case HPA:
			ApplicationFormDataEntity hpaFormDataEntity = applicationFormDataDAO
					.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.HPA_FORM.getLabel());
			if (!ObjectsUtil.isNull(hpaFormDataEntity)) {
				String formData = hpaFormDataEntity.getFormData();
				try {
					JsonNode jsonData = new ObjectMapper().readTree(formData);
					JsonNode quoteAmountNode = jsonData.get("quoteAmount");
					if (quoteAmountNode != null) {
						map.put("quoteAmount", quoteAmountNode.asLong());
						map.put("isDeclared", Boolean.TRUE);
					} else {
						map.put("isDeclared", Boolean.FALSE);
					}
				} catch (IOException e) {
					log.error("error in getting hpa data for application number :  " + applicationNumber);
				}
			}
			break;
		case HPT:
			ApplicationFormDataEntity hptFormDataEntity = applicationFormDataDAO
					.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.HPT_FORM.getLabel());
			try {
				FinanceOtherServiceModel financeOtherServiceModel = new ObjectMapper()
						.readValue(hptFormDataEntity.getFormData(), FinanceOtherServiceModel.class);
				map.put("financierName", financeOtherServiceModel.getFinancierName());
				map.put("financierId", financeOtherServiceModel.getFinancierId());
				map.put("agreementDate", financeOtherServiceModel.getAgreementDate());
				map.put("isDeclared", financeOtherServiceModel.getIsDeclared());
			} catch (IOException e) {
				log.error("error in getting hpt data for application number :  " + applicationNumber);
			}
			break;
		case LL_DUPLICATE:
			break;
		case LL_ENDORSEMENT:
			try {
				ApplicationFormDataEntity medicalDetailsEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.LLE_MEDICAL_FORM.getLabel());
				if (!ObjectsUtil.isNull(medicalDetailsEntity)) {
					Map<String, Object> medicalDetailsnMap = new HashMap<>();
					MedicalDetailsModel medicalDetailsModel = new ObjectMapper()
							.readValue(medicalDetailsEntity.getFormData(), MedicalDetailsModel.class);
					medicalDetailsnMap.put("medicalType", medicalDetailsModel.getMedicalType());
					medicalDetailsnMap.put("certificateIssueDate", medicalDetailsModel.getCertificateIssueDate());
					medicalDetailsnMap.put("doctorName", medicalDetailsModel.getDoctorName());
					medicalDetailsnMap.put("registrationNumber", medicalDetailsModel.getRegistrationNumber());
					map.put(FormCodeType.LLE_MEDICAL_FORM.getLabel(), medicalDetailsnMap);
				}

			} catch (Exception ex) {
				log.error("Getting error in update Or save in LL Endorsement Details");
			}
			break;
		case LL_FRESH:
			try {
				ApplicationFormDataEntity llRegistrationEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.LLF_DETAIL_FORM.getLabel());
				LLRegistrationModel llRegistrationModel = new ObjectMapper()
						.readValue(llRegistrationEntity.getFormData(), LLRegistrationModel.class);
				Map<String, Object> llRegistrationMap = new HashMap<>();
				llRegistrationMap.put("isSameAadhar", llRegistrationModel.getIsSameAadhar());
				llRegistrationMap.put("doorNo", llRegistrationModel.getDoorNo());
				llRegistrationMap.put("street", llRegistrationModel.getStreet());
				llRegistrationMap.put("city", llRegistrationModel.getCity());
				llRegistrationMap.put("districtCode", llRegistrationModel.getDistrictCode());
				llRegistrationMap.put("stateCode", llRegistrationModel.getStateCode());
				llRegistrationMap.put("countryCode", llRegistrationModel.getCountryCode());
				llRegistrationMap.put("mandalName", llRegistrationModel.getMandalName());
				llRegistrationMap.put("districtName", llRegistrationModel.getDistrictName());
				llRegistrationMap.put("stateName", llRegistrationModel.getStateName());
				llRegistrationMap.put("countryName", llRegistrationModel.getCountryName());
				llRegistrationMap.put("mandalCode", llRegistrationModel.getMandalCode());
				llRegistrationMap.put("postOffice", llRegistrationModel.getPostOffice());
				llRegistrationMap.put("mobileNo", llRegistrationModel.getMobileNo());
				llRegistrationMap.put("emailId", llRegistrationModel.getEmailId());
				llRegistrationMap.put("displayName", llRegistrationModel.getDisplayName());
				llRegistrationMap.put("fullName", llRegistrationModel.getFullName());
				llRegistrationMap.put("lastName", llRegistrationModel.getLastName());
				llRegistrationMap.put("qualification", llRegistrationModel.getQualification());
				llRegistrationMap.put("qualificationCode", llRegistrationModel.getQualificationCode());
				if (!ObjectsUtil.isNull(llRegistrationModel.getQualificationCode())) {
					llRegistrationMap.put("qualificationName",
							Qualification.getQualification(llRegistrationModel.getQualification()).getLabel());
				}
				llRegistrationMap.put("selfDecalartion", llRegistrationModel.getSelfDecalartion());
				llRegistrationMap.put("bloodGroup", llRegistrationModel.getBloodGroup());
				map.put(FormCodeType.LLF_DETAIL_FORM.getLabel(), llRegistrationMap);
				ApplicationFormDataEntity medicalDetailsEntity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.LLF_MEDICAL_FORM.getLabel());
				if (!ObjectsUtil.isNull(medicalDetailsEntity)) {
					Map<String, Object> medicalDetailsnMap = new HashMap<>();
					MedicalDetailsModel medicalDetailsModel = new ObjectMapper()
							.readValue(medicalDetailsEntity.getFormData(), MedicalDetailsModel.class);
					medicalDetailsnMap.put("medicalType", medicalDetailsModel.getMedicalType());
					medicalDetailsnMap.put("certificateIssueDate", medicalDetailsModel.getCertificateIssueDate());
					medicalDetailsnMap.put("doctorName", medicalDetailsModel.getDoctorName());
					medicalDetailsnMap.put("registrationNumber", medicalDetailsModel.getRegistrationNumber());
					map.put(FormCodeType.LLF_MEDICAL_FORM.getLabel(), medicalDetailsnMap);
				}

			} catch (Exception ex) {
				log.error("Getting error in update Or save in LL Fresh Details");
			}
			break;
		case LL_RETEST:
			break;
		case NOC_CANCELLATION:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.CC_FORM.getLabel());
				NocDetails model = new ObjectMapper().readValue(entity.getFormData(), NocDetails.class);
				map.put("status", model.getStatus());
			} catch (Exception ex) {
				log.error("Getting error in update Or save in cancellation of Noc Details");
			}
			break;
		case NOC_ISSUE:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.NOC_FORM.getLabel());
				NocDetails model = new ObjectMapper().readValue(entity.getFormData(), NocDetails.class);
				map.put("status", model.getStatus());
				RegistrationServiceResponseModel<NocDetails> response = registrationService
						.getNocAddressDetails(model.getNocAddressCode());
				NocDetails nocDetails;
				if (response.getHttpStatus() == HttpStatus.OK) {
					nocDetails = response.getResponseBody();
					AddressModel address = nocDetails.getAddress();
					map.put("rtaOfficeCode", nocDetails.getRtaOffice().getCode());
					map.put("rtaOfficeName", nocDetails.getRtaOffice().getName());
					map.put("city", address.getCity());
					map.put("districtName", address.getDistrictName());
					map.put("stateName", address.getStateName());
					map.put("countryName", address.getCountryName());
				} else {
					map.put("rtaOfficeCode", "");
					map.put("rtaOfficeName", "");
				}
			} catch (Exception ex) {
				log.error("Getting error in update Or save in cancellation of Noc Details");
			}
			break;
		case OWNERSHIP_TRANSFER_AUCTION:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.OTA_FORM.getLabel());
				AddressChangeModel model = new ObjectMapper().readValue(entity.getFormData(), AddressChangeModel.class);
				map = getObjectMap(map, model);
				map.put("displayName", model.getDisplayName());

			} catch (Exception ex) {
				log.error("Getting error in Transfer of Ownership for Auction");
			}
			break;
		case OWNERSHIP_TRANSFER_DEATH:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.OTD_FORM.getLabel());
				AddressChangeModel model = new ObjectMapper().readValue(entity.getFormData(), AddressChangeModel.class);
				map = getObjectMap(map, model);
				map.put("displayName", model.getDisplayName());

			} catch (Exception ex) {
				log.error("Getting error in Transfer of Ownership for Dead");
			}
			break;
		case OWNERSHIP_TRANSFER_SALE:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.OTS_FORM.getLabel());
				AddressChangeModel model = new ObjectMapper().readValue(entity.getFormData(), AddressChangeModel.class);
				map = getObjectMap(map, model);
				map.put("hpaStatus", model.getHpaStatus());
				map.put("displayName", model.getDisplayName());
				if (!ObjectsUtil.isNull(model.getPermitTransferType())) {
					map.put("permitOption", model.getPermitTransferType());
				}
			} catch (Exception ex) {
				log.error("Getting error in Transfer of Ownership for Buyer");
			}
			break;
		case PUC_USER_SIGNUP:
			break;
		case REGISTRATION_CANCELLATION:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.RC_FORM.getLabel());
				CommonServiceModel model = new ObjectMapper().readValue(entity.getFormData(), CommonServiceModel.class);
				map.put("status", model.getStatus());
				map.put("comment", model.getComment());
				map.put("reason", model.getReason());
			} catch (Exception ex) {
				log.error("Getting error in Cancellation of Registration");
			}
			break;
		case REGISTRATION_RENEWAL:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.RR_FORM.getLabel());
				CommonServiceModel model = new ObjectMapper().readValue(entity.getFormData(), CommonServiceModel.class);
				map.put("status", model.getStatus());
			} catch (Exception ex) {
				log.error("Getting error in Renweal of Registration");
			}
			break;
		case REGISTRATION_SUS_CANCELLATION:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.RSC_FORM.getLabel());
				CommonServiceModel model = new ObjectMapper().readValue(entity.getFormData(), CommonServiceModel.class);
				map.put("prNumber", model.getPrNumber());
				map.put("status", model.getStatus());
				map.put("approvedDate", model.getApprovedDate());
				map.put("serviceType", model.getServiceType());
				map.put("comment", model.getComment());
				map.put("suspensionType", model.getSuspensionType());
				map.put("suspensionTime", model.getSuspensionTime());
				map.put("startTime", model.getStartTime());
				map.put("endTime", model.getEndTime());
				map.put("reason", model.getReason());
				map.put("raisedBy", model.getRaisedBy());
			} catch (Exception ex) {
				log.error("Getting error in REGISTRATION_SUS_CANCELLATION while reading form data....");
			}
			break;
		case SUSPENSION_REVOCATION:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.SR_FORM.getLabel());
				CommonServiceModel model = new ObjectMapper().readValue(entity.getFormData(), CommonServiceModel.class);
				map.put("status", model.getStatus());
				map.put("reason", model.getReason());
			} catch (Exception ex) {
				log.error("Getting error in Revocation of Suspension");
			}
			break;
		case THEFT_INTIMATION:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.TI_FORM.getLabel());
				System.out.println(entity.getFormData());
				TheftIntimationRevocationModel model = new ObjectMapper().readValue(entity.getFormData(),
						TheftIntimationRevocationModel.class);
				map.put("theftType", model.getTheftType());
				map.put("theftStatus", model.getTheftStatus());
				map.put("firNo", model.getFirNo());
				map.put("policeStationName", model.getPoliceStationName());
				map.put("complaintDate", model.getComplaintDate());
				map.put("remarks", model.getRemarks());
				map.put("otherState", model.isOtherState());
				map.put("firYear", model.getFirYear());
				map.put("district", model.getDistrict());
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error("Getting error in Theft Intimation");
			}
			break;
		case VEHICLE_ATLERATION:
			try {
				ResponseModel<ApplicationFormDataModel> response = applicationFormDataService
						.getApplicationFormDataBySessionId(sessionEntity.getSessionId(),
								FormCodeType.VA_FORM.getLabel());
				ApplicationFormDataModel form = response.getData();
				VehicleBodyModel model = new ObjectMapper().readValue(form.getFormData(), VehicleBodyModel.class);
				map.put("alterationCategory", model.getAlterationCategory());
				map.put("oldRegistrationCategoryCode", model.getOldRegistrationCategoryCode());
				if (model.getAlterationCategory().contains(AlterationCategory.SEATING_CAPACITY)
						|| model.getAlterationCategory().contains(AlterationCategory.BODY_TYPE)) {
					map = getVehicleAlterationDetails(map, sessionEntity.getVehicleRcId(), authToken);
				}
				if (model.getAlterationCategory().contains(AlterationCategory.FUEL_TYPE)) {
					map.put("gasKitNumber", model.getGasKitNumber());
					map.put("fuelType", model.getFuelType());
				}
				if (model.getAlterationCategory().contains(AlterationCategory.VEHICLE_TYPE)) {
					map.put("vehicleType", model.getVehicleSubClass());
					map.put("vehicleSubClass", model.getVehicleSubClass());
					map.put("vehicleSubClassDecs", model.getVehicleSubClassDecs());
					map.put("registrationCategoryCode", model.getRegistrationCategoryCode());
					map.put("prType", model.getPrType());
				}
				if (model.getAlterationCategory().contains(AlterationCategory.ENGINE_ALTERATION)) {
					map.put("engineNo", model.getEngineNo());
				}
				TaxDetailEntity taxDetailEntity = taxDetailDAO.getByAppId(applicationEntity);
				if (taxDetailEntity != null) {
					map.put("taxAmount", taxDetailEntity.getTaxAmt());
					map.put("taxType", taxDetailEntity.getTaxType());
				}
			} catch (Exception ex) {
				log.error("error in getting data for application number : " + applicationNumber, ex);
			}
			break;
		case VEHICLE_REASSIGNMENT:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO
						.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.VR_FORM.getLabel());
				RegistrationCategoryModel model = new ObjectMapper().readValue(entity.getFormData(),
						RegistrationCategoryModel.class);
				map.put("name", model.getName());
				map.put("code", model.getCode());
			} catch (Exception ex) {
				log.error("Getting error in Reassigmnet of Vehicle ");
			}
			break;
		case STOPPAGE_TAX:
			try {
				ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(
						applicationEntity.getApplicationId(), FormCodeType.ST_FORM.getLabel());
				StoppageTaxDetailsModel stoppageTaxDetailsModel = new ObjectMapper().readValue(entity.getFormData(),
						StoppageTaxDetailsModel.class);
				Map<String, Object> stoppageTaxDetailsMap = new HashMap<>();
				stoppageTaxDetailsMap.put("isSameAadhar", stoppageTaxDetailsModel.getIsSameAadhar());
				stoppageTaxDetailsMap.put("doorNo", stoppageTaxDetailsModel.getDoorNo());
				stoppageTaxDetailsMap.put("street", stoppageTaxDetailsModel.getStreet());
				stoppageTaxDetailsMap.put("city", stoppageTaxDetailsModel.getCity());
				stoppageTaxDetailsMap.put("districtCode", stoppageTaxDetailsModel.getDistrictCode());
				stoppageTaxDetailsMap.put("stateCode", stoppageTaxDetailsModel.getStateCode());
				stoppageTaxDetailsMap.put("countryCode", stoppageTaxDetailsModel.getCountryCode());
				stoppageTaxDetailsMap.put("mandalName", stoppageTaxDetailsModel.getMandalName());
				stoppageTaxDetailsMap.put("districtName", stoppageTaxDetailsModel.getDistrictName());
				stoppageTaxDetailsMap.put("stateName", stoppageTaxDetailsModel.getStateName());
				stoppageTaxDetailsMap.put("countryName", stoppageTaxDetailsModel.getCountryName());
				stoppageTaxDetailsMap.put("mandalCode", stoppageTaxDetailsModel.getMandalCode());
				stoppageTaxDetailsMap.put("postOffice", stoppageTaxDetailsModel.getPostOffice());
				stoppageTaxDetailsMap.put("stoppageReason", stoppageTaxDetailsModel.getStoppageReason());
				stoppageTaxDetailsMap.put("stoppageDate", stoppageTaxDetailsModel.getStoppageDate());
				map.put(FormCodeType.ST_FORM.getLabel(), stoppageTaxDetailsMap);
			} catch (Exception ex) {
				log.error("Getting error in Change of Address" + ex);
			}
			break;
		case PERMIT_FRESH:
			PermitNewRequestModel permitNewModel = null;
			try {
				ResponseModel<ApplicationFormDataModel> formData = applicationFormDataService
						.getApplicationFormDataBySessionId(applicationEntity.getLoginHistory().getSessionId(),
								FormCodeType.PCF_FORM.getLabel());
				if (!ObjectsUtil.isNull(formData) && !ObjectsUtil.isNull(formData.getData())) {
					ObjectMapper mapper = new ObjectMapper();
					permitNewModel = mapper.readValue(formData.getData().getFormData(), PermitNewRequestModel.class);
					log.info("Permit type code : " + permitNewModel.getPermitType());
				}
			} catch (JsonProcessingException e) {
				log.error("Error in reading form JsonProcessingException : " + e.getMessage());
			} catch (Exception e) {
				log.error("Error in form reading IOException : " + e.getMessage());
			}
			map.put("permitNew", permitNewModel);
		default:
			break;

		}
		mapObject.put("serviceCode", serviceType.getCode());
		mapObject.put("serviceName", serviceType.getLabel());
		mapObject.put("applicationInfo", map);
		return mapObject;
	}

	private Map<String, Object> getVehicleAlterationDetails(Map<String, Object> map, Long vehicleRcId, String authToken) {

		if (ObjectsUtil.isNull(vehicleRcId)) {
			return map;
		}
		VehicleBodyModel model = new VehicleBodyModel();
		RegistrationServiceResponseModel<VehicleBodyModel> response = null;
		try {
			response = registrationService.getVehicleAlterationDetails(vehicleRcId, authToken);
		} catch (UnauthorizedException e) {
			log.error("Getting error in getVehicleAlterationDetails vehicle Rc Id " + vehicleRcId);
		}
		if (response.getHttpStatus() == HttpStatus.OK) {
			model = response.getResponseBody();
		}
  		for(AlterationCategory alterationCategory:model.getAlterationCategory()){
		if (AlterationCategory.BODY_TYPE == alterationCategory) {
			map.put("bodyTypeUpdated", model.getBodyTypeUpdated());
		} else {
			map.put("seatingCapacity", model.getSeatingCapacity());
		}
		map.put("lengthUpdated", model.getLengthUpdated());
		map.put("heightUpdated", model.getHeightUpdated());
		map.put("widthUpdated", model.getWidthUpdated());
		map.put("completionDate", model.getCompletionDate());
		map.put("vehicleSubClass", model.getVehicleSubClass());
		map.put("vehicleSubClassDecs", model.getVehicleSubClassDecs());
  		}
		return map;
	}

	private Map<String, Object> getObjectMap(Map<String, Object> map, AddressChangeModel model) {

		map.put("doorNo", model.getDoorNo());
		map.put("street", model.getStreet());
		map.put("city", model.getCity());
		map.put("districtCode", model.getDistrictCode());
		map.put("mandalCode", model.getMandalCode());
		map.put("stateCode", model.getStateCode());
		map.put("countryCode", model.getCountryCode());
		map.put("isSameAadhar", model.getIsSameAadhar());
		map.put("postOffice", model.getPostOffice());
		map.put("mobileNo", model.getMobileNo());
		map.put("emailId", model.getEmailId());
		map.put("withEffectFrom", model.getWithEffectFrom());
		map.put("mandalName", model.getMandalName());
		map.put("districtName", model.getDistrictName());
		map.put("stateName", model.getStateName());
		map.put("countryName", model.getCountryName());
        if (!ObjectsUtil.isNull(model.getPanNumber())) {
            map.put("panNumber", model.getPanNumber());
        }
        if (!ObjectsUtil.isNull(model.getBloodGroup())) {
            map.put("bloodGroup", model.getBloodGroup());
        }
        if (!ObjectsUtil.isNull(model.getAlternateMobileNumber())) {
            map.put("alternateMobileNumber", model.getAlternateMobileNumber());
        }
        if (!ObjectsUtil.isNull(model.getQualification())) {
            map.put("qualification", Qualification.getQualification(Integer.parseInt(model.getQualification())));
        }
        if (!ObjectsUtil.isNull(model.getIsDifferentlyAbled())) {
            map.put("isDifferentlyAbled", model.getIsDifferentlyAbled());
        }
        if(!ObjectsUtil.isNull(model.getLegalHierDetails())){
            map.put("legalHierDetails", model.getLegalHierDetails());
        }
        if(!ObjectsUtil.isNull(model.getAadharNumber())){
            map.put("aadharNumber",model.getAadharNumber());
        }
        if(!ObjectsUtil.isNull(model.getNoOtherLegalHier())){
            map.put("noOtherLegalHier", model.getNoOtherLegalHier());
        }
        if(!ObjectsUtil.isNull(model.getPermitTransferType())){
            map.put("permitTransferType", model.getPermitTransferType());
        }
		return map;
	}

	@Override
	public void sendSMSEmail(Status status, ApplicationEntity appEntity) {
		log.info("::::sendSMSEmail::start:::::");
		CustMsgModel custModel = null;
		RegistrationServiceResponseModel<FinanceModel> financerModel = null;
		RegistrationServiceResponseModel<Boolean> isAppliedHpa = null;
		String serviceCode = "";
		boolean msgSend = false;

		custModel = communicationService.getCustInfo(status, appEntity, false, null);
		serviceCode = appEntity.getServiceCode();
		if (serviceCode.equals(ServiceType.THEFT_INTIMATION.getCode())) {
			try {
				isAppliedHpa = registrationService.hasAppliedHPA(appEntity.getLoginHistory().getUniqueKey());
				if (isAppliedHpa.getResponseBody()) {
					financerModel = registrationService
							.getFinancierDetails(appEntity.getLoginHistory().getVehicleRcId());
				}
			} catch (UnauthorizedException e) {
				log.error("UnauthorizedException exception in calling registration for hpa : " + e.getMessage());
			}
		}
		if (financerModel != null) {
			msgSend = communicationService.sendMsg(SEND_SMS_EMAIL, custModel, financerModel.getResponseBody(),
					isAppliedHpa.getResponseBody(), serviceCode);
		} else {
			msgSend = communicationService.sendMsg(SEND_SMS_EMAIL, custModel, null, null, serviceCode);
		}
		log.info("::::sendSMSEmail::end:::: " + msgSend);
	}

	@Override
	@Transactional
	public String getCustomerInvoice(String applicationNumber, String regType) {
		log.info(":::getCustomerInvoice Details:::::");
		ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
		if (null != appEntity) {
			UserSessionEntity userSession = appEntity.getLoginHistory();
			if (null != userSession) {
				Long vehicleRcId = userSession.getVehicleRcId();
				if (!ObjectsUtil.isNull(vehicleRcId)) {
					boolean isNoc = false;
					if (ServiceType.NOC_ISSUE.getCode().equalsIgnoreCase(appEntity.getServiceCode())
							|| ServiceType.NOC_CANCELLATION.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
						isNoc = true;
					}
					try {
						RegistrationServiceResponseModel<String> res = registrationService
								.getCustomerInvoice(vehicleRcId, regType, isNoc);
						if (null != res && res.getHttpStatus().equals(HttpStatus.OK)) {
							return res.getResponseBody();
						}
					} catch (UnauthorizedException e) {
						log.info(":::Error getting CustomerInvoice Details:::::" + e);
					}
				}
			}
		}
		return null;
	}

	@Override
	@Transactional
	public String getSignature(String applicationNumber) {
		log.info(":::getSignature Details:::::");
		ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
		if (null != appEntity) {
			UserSessionEntity userSession = appEntity.getLoginHistory();
			if (null != userSession) {
				Long vehicleRcId = userSession.getVehicleRcId();
				if (!ObjectsUtil.isNull(vehicleRcId)) {
					try {
						RegistrationServiceResponseModel<String> res = registrationService.getSignature(vehicleRcId);
						if (null != res && res.getHttpStatus().equals(HttpStatus.OK)) {
							return res.getResponseBody();
						}
					} catch (UnauthorizedException e) {
						log.info(":::Error getting CustomerInvoice Details:::::" + e);
					}
				}
			}
		}
		return null;
	}

	@Override
	public ResponseModel<?> actionOnApp(String userName, Long userId, String userRole, AppActionModel appActionModel)
			throws TaskNotFound, NotFoundException {
		for (DocActionModel doc : appActionModel.getDocActions()) {

		}
		List<RtaTaskInfo> rtaTaskList = actionOnApp(appActionModel.getAppStatus(),
				appActionModel.getApplicationNumber(), userId, userName, userRole, appActionModel.getAppComment(),
				null);
		ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.SUCCESS);
		response.setActivitiTasks(rtaTaskList);
		return response;
	}

	@Override
	@Transactional
	public ResponseModel<ShowcaseNoticeInfoModel> getShowcaseInfo(Long sessionId) throws UnauthorizedException {
		ResponseModel<ShowcaseNoticeInfoModel> response = null;
		UserSessionEntity userSession = userSessionDAO.getUserSession(sessionId);
		if (userSession.getAadharNumber() == null || userSession.getVehicleRcId() == null) {
			log.error("error: aadhar number or vehicleRcId not found for sessionId : " + sessionId);
		}
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		ShowcaseInfoRequestModel request = new ShowcaseInfoRequestModel();
		request.setAadharNumber(userSession.getAadharNumber());
		request.setVehicleRcId(userSession.getVehicleRcId());
		ShowcaseNoticeInfoModel showCaseNotice = null;
		RegistrationServiceResponseModel<ShowcaseNoticeInfoModel> showcaseInfo = registrationService
				.getShowcaseInfo(request);
		if (showcaseInfo.getHttpStatus() == HttpStatus.OK) {
			showCaseNotice = showcaseInfo.getResponseBody();
			ApplicationApprovalHistoryEntity approvalHistory = applicationApprovalHistoryDAO.getLastActionOfApplication(
					UserType.ROLE_CCO.toString(), appEntity.getApplicationId(), Status.APPROVED.getValue(),
					appEntity.getIteration());
			//showCaseNotice.setGenerationDate(approvalHistory.getCreatedOn());// createdOn
																				// field
																				// is
																				// considered
																				// as
																				// cco
																				// action
																				// taken
																				// time
			showCaseNotice.setFrfNumber(appEntity.getApplicationNumber());
			response = new ResponseModel<>(ResponseModel.SUCCESS, showCaseNotice);
		} else {
			log.error("error when getting showcasenotice info from registration");
			response = new ResponseModel<>(ResponseModel.FAILED);
		}
		return response;
	}

	@Override
	@Transactional
	public ResponseModel<Map<String, Status>> getAppStatusForCurrentStatus(String userName, Long userId, String appNo) {
		ResponseModel<Map<String, Status>> response = new ResponseModel<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		if (ObjectsUtil.isNull(appEntity)) {
			response.setStatus(ResponseModel.FAILED);
			response.setMessage("Application Not Found !!!");
			return response;
		}
		Map<String, Status> map = new HashMap<>();
		ApplicationApprovalHistoryEntity approvalHistory = applicationApprovalHistoryDAO.getMyLastAction(userId,
				appEntity.getApplicationId(),
				ObjectsUtil.isNull(appEntity.getIteration()) ? 0 : appEntity.getIteration());
		if (ObjectsUtil.isNull(approvalHistory)) {
			map.put("status", Status.PENDING);
		} else {
			map.put("status", Status.getStatus(approvalHistory.getStatus()));
		}
		response.setData(map);
		response.setStatus(ResponseModel.SUCCESS);
		return response;
	}

	@Override
	@Transactional
	public Map<String, Object> getPRNumberType(String applicationNumber) throws NotFoundException {
		ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNumber);
		Map<String, Object> map = new HashMap<>();
		if (ObjectsUtil.isNull(applicationEntity)) {
			log.error("application not found for application number : " + applicationNumber);
			throw new NotFoundException("application not found");
		}
		ServiceType service = ServiceType.getServiceType(applicationEntity.getServiceCode());
		if (service == ServiceType.VEHICLE_REASSIGNMENT) {
			ApplicationFormDataEntity entity = applicationFormDataDAO
					.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.VR_FORM.getLabel());
			try {
				VehicleReassignmentModel model = new ObjectMapper().readValue(entity.getFormData(),
						VehicleReassignmentModel.class);
				if (!ObjectsUtil.isNull(model)) {
					map.put("prType", model.getPrType());
				}
			} catch (Exception e) {
				log.error("Getting error in PR Number type");
			}

		} else if (service == ServiceType.VEHICLE_ATLERATION) {
			// #TODO read from alteration form data and return prtype."for
			// Gautam"
			ApplicationFormDataEntity entity = applicationFormDataDAO
					.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.VA_FORM.getLabel());
		}
		return map;
	}

	@Override
	@Transactional
	public void completeAppOnly(String executionId, Status status, String approverName, UserType userType) {
		log.info("Complete App only executionId : " + executionId);
		ApplicationEntity appEntity = applicationDAO.getApplicationByExecutionId(executionId);
		UserSessionEntity userSession = appEntity.getLoginHistory();
		userSession.setCompletionStatus(status.getValue());
		userSessionDAO.saveOrUpdate(userSession);
	}
}

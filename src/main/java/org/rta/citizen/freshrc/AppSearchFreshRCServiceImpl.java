package org.rta.citizen.freshrc;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

@Service
@Qualifier("appSearchFreshRCServiceImpl")
public class AppSearchFreshRCServiceImpl extends AbstractAppSearchService implements AppSearchService {

	private static final Logger log = Logger.getLogger(AppSearchFreshRCServiceImpl.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Value("${activiti.citizen.task.code.ownerconscent}")
	private String taskOwnerConscent;

	@Override
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId)
			throws UnauthorizedException, VehicleNotFinanced {
		ResponseModel<Object> res = new ResponseModel<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);

		if (ServiceType.getServiceType(appEntity.getServiceCode()) != ServiceType.FRESH_RC_FINANCIER) {
			log.error("Application Service code is not FRF. app : " + appNo);
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Invalid Application Number !!!");
			res.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return res;
		}

		String instanceId = applicationService.getProcessInstanceId(sessionId);
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
		List<RtaTaskInfo> list = actRes.getActiveTasks();
		Map<String, Object> customerFinananceDetails = null;
		for (RtaTaskInfo rtaTaskInfo : list) {
			if (rtaTaskInfo.getTaskDefKey().equalsIgnoreCase(taskOwnerConscent)) {
				try {
					customerFinananceDetails = applicationService.getCustomerAppStatusDetails(appNo);
				} catch (UnauthorizedException e) {
					throw new UnauthorizedException();
				} catch (VehicleNotFinanced vehicleNotFinanced) {
					throw new VehicleNotFinanced();
				}
			}
		}
		res.setStatus(ResponseModel.SUCCESS);
		res.setData(customerFinananceDetails);
		return res;
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
			throws UnauthorizedException, NotFoundException, VehicleNotFinanced {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(ServiceType.FRESH_RC_FINANCIER.getLabel());
		mdl.setServiceCode(ServiceType.FRESH_RC_FINANCIER.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		getRegistrationCategory(appEntity, mdl);
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO
				.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for (ApplicationApprovalHistoryEntity history : histpryEntityList) {
			if (Status.getStatus(history.getStatus()) == Status.APPROVED
					|| Status.getStatus(history.getStatus()) == Status.REJECTED) {
				if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_CCO) {
					mdl.setCcoStatus(Status.getStatus(history.getStatus()));
					mdl.setCcoActionDate(history.getCreatedOn());
					mdl.setCcoRemark(history.getComments());
				} else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_DTC) {
					mdl.setDtcStatus(Status.getStatus(history.getStatus()));
					mdl.setDtcActionDate(history.getCreatedOn());
					mdl.setDtcRemark(history.getComments());
				}  else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_AO) {
					mdl.setAoStatus(Status.getStatus(history.getStatus()));
					mdl.setAoActionDate(history.getCreatedOn());
					mdl.setAoRemark(history.getComments());
				}  else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_RTO) {
					mdl.setRtoStatus(Status.getStatus(history.getStatus()));
					mdl.setRtoActionDate(history.getCreatedOn());
					mdl.setRtoRemark(history.getComments());
				}
			}
		}
		
		FreshRcModel freshRc = null;
		String citizenStatus = null;
		RegistrationServiceResponseModel<FreshRcModel> freshRcResponse = registrationService.getFreshRcDataByApplicationNumber(appNo);
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
		
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		if (overAllStatus == Status.PENDING || overAllStatus == Status.FRESH) {
			if (ObjectsUtil.isNull(mdl.getCcoStatus())) {
				mdl.setCcoStatus(Status.PENDING);
			}
			if (ObjectsUtil.isNull(mdl.getDtcStatus()) && (ObjectsUtil.isNull(freshRcResponse.getResponseBody().getOwnerConsent()) ||
					!freshRcResponse.getResponseBody().getOwnerConsent())) {
				mdl.setDtcStatus(Status.PENDING);
			}
			if (ObjectsUtil.isNull(mdl.getAoStatus())) {
				mdl.setAoStatus(Status.PENDING);
			}
			if (ObjectsUtil.isNull(mdl.getRtoStatus())) {
				mdl.setRtoStatus(Status.PENDING);
			}
		}
		mdl.setOverAllStatus(overAllStatus);
		String instanceId = applicationService.getProcessInstanceId(sessionId);
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
		if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			mdl.setActivitiTasks(actRes.getData());
		}
		return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
	}

}

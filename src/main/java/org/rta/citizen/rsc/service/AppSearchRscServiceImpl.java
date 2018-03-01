package org.rta.citizen.rsc.service;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.noc.service.impl.AppSearchNocServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.NotFoundException;

@Service
@Qualifier("appSearchRscServiceImpl")
public class AppSearchRscServiceImpl extends AbstractAppSearchService implements AppSearchService{

	private static final Logger log = Logger.getLogger(AppSearchNocServiceImpl.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;
	
	@Autowired
	private ApplicationFormDataService applicationFormDataService; 

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Override
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId) throws UnauthorizedException {
		return new ResponseModel<Object>(ResponseModel.SUCCESS, null);
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
			throws UnauthorizedException, NotFoundException, JsonProcessingException, IOException {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		ResponseModel<ApplicationFormDataModel> applicationFormDataModel = applicationFormDataService.getApplicationFormData(appNo, FormCodeType.RSC_FORM.getLabel());
		try {
			ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonData = mapper.readTree(applicationFormDataModel.getData().getFormData());
            String suspensionType = jsonData.get("suspensionType").asText();
            if(suspensionType.equals(Status.SUSPENDED.getLabel())){
            	suspensionType = "Suspension of Registration";
            }else if(suspensionType.equals(Status.CANCELLED.getLabel())){
            	suspensionType = "Cancellation of Registration";
            }else if(suspensionType.equals(Status.OBJECTION.getLabel())){
            	suspensionType = "Objection of Registration";
            }
			mdl.setApplicationType(suspensionType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mdl.setServiceCode(ServiceType.REGISTRATION_SUS_CANCELLATION.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		try{
			getRegistrationCategory(appEntity, mdl);
		} catch(Exception ex){
		}
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for(ApplicationApprovalHistoryEntity history : histpryEntityList){
			if(!(Status.getStatus(history.getStatus()) == Status.APPROVED || Status.getStatus(history.getStatus()) == Status.REJECTED)){
				continue;
			}
			if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_AO){
				mdl.setAoStatus(Status.getStatus(history.getStatus()));
				mdl.setAoActionDate(history.getCreatedOn());
				mdl.setAoRemark(history.getComments());
			} else if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_RTO){
				mdl.setRtoStatus(Status.getStatus(history.getStatus()));
				mdl.setRtoActionDate(history.getCreatedOn());
				mdl.setRtoRemark(history.getComments());
			}
		}
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		if(overAllStatus == Status.PENDING || overAllStatus == Status.FRESH){
			if(ObjectsUtil.isNull(mdl.getAoStatus()) && ObjectsUtil.isNull(mdl.getRtoStatus())){
				mdl.setAoStatus(Status.PENDING);
				mdl.setRtoStatus(Status.PENDING);
			}	
		}        
		mdl.setOverAllStatus(Status.getStatus(appEntity.getLoginHistory().getCompletionStatus()));
		String instanceId = applicationService.getProcessInstanceId(sessionId);
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
		if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			mdl.setActivitiTasks(actRes.getData());
		}
		return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
	}

}

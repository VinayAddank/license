/**
 * 
 */
package org.rta.citizen.fitness;

import java.util.List;

import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationStatusModel;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javassist.NotFoundException;

/**
 * @author arun.verma
 *
 */

@Service
@Qualifier("appSearchFitnessServiceImpl")
public class AppSearchFitnessServiceImpl extends AbstractAppSearchService implements AppSearchService{

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Override
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId) throws UnauthorizedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
			throws UnauthorizedException, NotFoundException {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(ServiceType.getServiceType(appEntity.getServiceCode()).getLabel());
		mdl.setServiceCode(ServiceType.getServiceType(appEntity.getServiceCode()).getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		getRegistrationCategory(appEntity, mdl);
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for(ApplicationApprovalHistoryEntity history : histpryEntityList){
			if(Status.getStatus(history.getStatus()) == Status.APPROVED || Status.getStatus(history.getStatus()) == Status.REJECTED) {
				if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_MVI){
					mdl.setMviStatus(Status.getStatus(history.getStatus()));
					mdl.setMviActionDate(history.getCreatedOn());
					mdl.setMviRemark(history.getComments());
				}
			}
		}
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		if(overAllStatus == Status.PENDING || overAllStatus == Status.FRESH){
			if(ObjectsUtil.isNull(mdl.getMviStatus())){
				mdl.setMviStatus(Status.PENDING);
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

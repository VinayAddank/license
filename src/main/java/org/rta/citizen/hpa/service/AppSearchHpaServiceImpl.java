/**
 * 
 */
package org.rta.citizen.hpa.service;

import java.util.List;

import javax.transaction.Transactional;

import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.FinancerModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

/**
 * @author arun.verma
 *
 */
@Service
@Qualifier("appSearchHpaServiceImpl")
public class AppSearchHpaServiceImpl extends AbstractAppSearchService implements AppSearchService{

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Value("${activiti.citizen.hpa.code.approvefinancier}")
	private String approveFinancierTaskDef;

	@Value("${activiti.citizen.hpa.code.financedetails}")
	private String financedetailsTaskDef;

	@Override
	@Transactional
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId) throws UnauthorizedException {
		RegistrationServiceResponseModel<List<FinancerModel>> res = registrationService.getFinancerList(appNo);
		if (res.getHttpStatus().equals(HttpStatus.OK)) {
			return new ResponseModel<Object>(ResponseModel.SUCCESS, res.getResponseBody());
		} else {
			return new ResponseModel<Object>(ResponseModel.FAILED, null, "Unable to get Financiers.");
		}
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId) throws UnauthorizedException, NotFoundException {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(ServiceType.HPA.getLabel());
		mdl.setServiceCode(ServiceType.HPA.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		getRegistrationCategory(appEntity, mdl);
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for(ApplicationApprovalHistoryEntity history : histpryEntityList){
			if(!(Status.getStatus(history.getStatus()) == Status.APPROVED || Status.getStatus(history.getStatus()) == Status.REJECTED)){
				continue;
			}
			if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_ONLINE_FINANCER){
				mdl.setFinancierStatus(Status.getStatus(history.getStatus()));
				mdl.setFinancierActionDate(history.getCreatedOn());
				mdl.setFinancierRemark(history.getComments());
			} else if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_CCO){
				mdl.setCcoStatus(Status.getStatus(history.getStatus()));
				mdl.setCcoActionDate(history.getCreatedOn());
				mdl.setCcoRemark(history.getComments());
			} else if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_AO){
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
			if(ObjectsUtil.isNull(mdl.getFinancierStatus())){
				mdl.setFinancierStatus(Status.PENDING);
			}
			if(ObjectsUtil.isNull(mdl.getCcoStatus())){
				mdl.setCcoStatus(Status.PENDING);
			}
			if(ObjectsUtil.isNull(mdl.getAoStatus()) && ObjectsUtil.isNull(mdl.getRtoStatus())){
				mdl.setAoStatus(Status.PENDING);
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

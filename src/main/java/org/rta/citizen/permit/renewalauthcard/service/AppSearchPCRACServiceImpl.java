package org.rta.citizen.permit.renewalauthcard.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.PermitType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.PermitHeaderModel;
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
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

@Service
@Qualifier("appSearchPermitRenewalAuthCardServiceImpl")
public class AppSearchPCRACServiceImpl extends AbstractAppSearchService implements AppSearchService {

	private static final Logger log = Logger.getLogger(AppSearchPCRACServiceImpl.class);

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
        return new ResponseModel<Object>(ResponseModel.SUCCESS, null);
    }

    @Override
    @Transactional
    public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
            throws UnauthorizedException, NotFoundException {
        ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
        ApplicationStatusModel mdl = new ApplicationStatusModel();
        mdl.setApplicationType(ServiceType.PERMIT_RENEWAL_AUTH_CARD.getLabel());
        mdl.setServiceCode(ServiceType.PERMIT_RENEWAL_AUTH_CARD.getCode());
        mdl.setSubmittedOn(appEntity.getCreatedOn());
        getRegistrationCategory(appEntity, mdl);
        mdl.setOverAllStatus(Status.getStatus(appEntity.getLoginHistory().getCompletionStatus()));
        
        RegistrationServiceResponseModel<List<PermitHeaderModel>> permitDetailList = registrationService.getPermitDetails(appEntity.getLoginHistory().getVehicleRcId());
        String permitType = "";
        if(!ObjectsUtil.isNull(permitDetailList.getResponseBody()) || permitDetailList.getResponseBody().size()>0){
        	for (PermitHeaderModel headerModel : permitDetailList.getResponseBody()) {
				if (!headerModel.getIsTempPermit()) {
					permitType = headerModel.getPermitType();
					break;
				}
			}
        }
		if(permitType.equalsIgnoreCase(PermitType.NP.getValue())){
			List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
			for(ApplicationApprovalHistoryEntity history : histpryEntityList){
				if(!(Status.getStatus(history.getStatus()) == Status.APPROVED || Status.getStatus(history.getStatus()) == Status.REJECTED)){
					continue;
				}
				if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_CCO){
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
				if(ObjectsUtil.isNull(mdl.getCcoStatus())){
					mdl.setCcoStatus(Status.PENDING);
				}
				if(ObjectsUtil.isNull(mdl.getAoStatus()) && ObjectsUtil.isNull(mdl.getRtoStatus())){
					mdl.setAoStatus(Status.PENDING);
					mdl.setRtoStatus(Status.PENDING);
				}
			}          
		}
        
        String instanceId = applicationService.getProcessInstanceId(sessionId);
        ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
        if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
            mdl.setActivitiTasks(actRes.getData());
        }
        return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
    }

}

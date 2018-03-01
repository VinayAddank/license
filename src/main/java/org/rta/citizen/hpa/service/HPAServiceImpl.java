/**
 * 
 */
package org.rta.citizen.hpa.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ApplicationNotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.FinancerModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author arun.verma
 *
 */
@Service
public class HPAServiceImpl implements HPAService {

	private static final Logger log = Logger.getLogger(HPAServiceImpl.class);

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
    @Autowired
    private UserSessionDAO userSessionDAO;
    
    @Value("${activiti.citizen.hpa.code.approvefinancier}")
    private String approveFinancierTaskDef;

    @Value("${activiti.citizen.hpa.code.financedetails}")
    private String financedetailsTaskDef;
    
    @Autowired
    private ApplicationDAO appDAO;

    @Override
    public ResponseModel<List<RtaTaskInfo>> approveFinancier(String appNo, Long sessionId, FinancerModel financier)
            throws UnauthorizedException {
        ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>(ResponseModel.SUCCESS);
        String instanceId = applicationService.getProcessInstanceId(sessionId);
        boolean taskFound = false;
        ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
        List<RtaTaskInfo> tasks = actRes.getActiveTasks();
        for (RtaTaskInfo task : tasks) {
            if (task.getTaskDefKey().equalsIgnoreCase(approveFinancierTaskDef)) {
                taskFound = true;
            }
        }
        if (!taskFound) {
            log.error("InstanceId " + instanceId + " not belong to task : " + approveFinancierTaskDef);
            response.setStatus(ResponseModel.FAILED);
            response.setMessage("Invalid Action !!!");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        RegistrationServiceResponseModel<Object> res = registrationService.approveFinancier(appNo, financier);
        if (res.getHttpStatus().equals(HttpStatus.ALREADY_REPORTED)) {
            response.setStatus(ResponseModel.FAILED);
            response.setMessage("Financier Already Selected !!!");
        } else if (res.getHttpStatus().equals(HttpStatus.OK)) {
            // ---- for activiti ----------------------
            Assignee assignee = new Assignee();
            assignee.setUserId(CitizenConstants.CITIZEN_USERID);
            ActivitiResponseModel<List<RtaTaskInfo>> actResponse =
                    activitiService.completeTask(assignee, approveFinancierTaskDef, instanceId, true, null);
            response.setActivitiTasks(actResponse.getActiveTasks());
            
            // ----------------------------------------
        } else {
            log.error("response code from registration : " + res.getHttpStatus());
            response.setStatus(ResponseModel.FAILED);
            response.setMessage("Unable to Approve Financier !!!");
        }
        response.setStatusCode(res.getHttpStatus().value());
        return response;
    }

    @Override
    @Transactional
    public ResponseModel<List<RtaTaskInfo>> submitFinanceDetails(String appNo, Long sessionId, String userName, Long appId, Long userId, String userRole, Status status, Integer iteration, CommentModel comment) throws ApplicationNotFoundException {
        ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>(ResponseModel.SUCCESS);
        String instanceId = applicationService.getProcessInstanceId(sessionId);
        log.info("calling activiti to complete finance details by online financer: " + userName + " app: " + appNo);
        //---------save in history----------
        ApplicationApprovalHistoryEntity history = new ApplicationApprovalHistoryEntity();
        Long time = DateUtil.toCurrentUTCTimeStamp();
        ApplicationEntity appEntity = appDAO.getApplicationFromSession(sessionId);
        if(appEntity==null){
        	throw new ApplicationNotFoundException("Application Number not found while Financier Approval");
        }
        if(status == Status.REJECTED){
            UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
            userSessionEntity.setCompletionStatus(Status.REJECTED.getValue());
            userSessionDAO.saveOrUpdate(userSessionEntity);
        }
        history.setApplicationEntity(appEntity);
        history.setCreatedBy(userName);
        history.setCreatedOn(time);
        history.setRtaUserId(userId);
        history.setRtaUserRole(userRole);
        history.setStatus(status.getValue());
        history.setIteration(iteration);
        if(!ObjectsUtil.isNull(comment)){
            history.setComments(comment.getComment());
        }
        applicationApprovalHistoryDAO.saveOrUpdate(history);
        // ---- for activiti ----------------------
        Assignee assignee = new Assignee();
        assignee.setUserId(userName);
        ActivitiResponseModel<List<RtaTaskInfo>> actResponse =
                activitiService.completeTaskWithAction(assignee, financedetailsTaskDef, status.getLabel(), instanceId, true);
        response.setActivitiTasks(actResponse.getActiveTasks());
        // ----------------------------------------
        return response;
    }

}

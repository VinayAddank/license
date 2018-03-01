/**
 * 
 */
package org.rta.citizen.common.service.rtaapplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.TaskNotFound;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AppActionModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.freshrc.ShowcaseNoticeInfoModel;

/**
 * @author arun.verma
 *
 */

public interface RtaApplicationService {
    
    public Integer getPendingApplicationCount(Long userId, String userName, String userRole, Long from,
            Long to, Integer perPageRecords, Integer pageNumber, HashMap<String, Object> variables);
    
    public List<CitizenApplicationModel> getPendingApplications(Long userId, String userName, String userRole, Long from,
            Long to, Integer perPageRecords, Integer pageNumber, boolean slotApplicable, HashMap<String, Object> variables,String applicationNumber);
    
    public Integer getApplicationCount(Status status, Long userId, String userName, String userRole, ServiceType serviceType, Long from,
            Long to, Integer perPageRecords, Integer pageNumber, ServiceCategory serviceCategory);
    
    public List<CitizenApplicationModel> getApplications(Status status, Long userId, String userName, String userRole, ServiceType serviceType, Long from,
            Long to, Integer perPageRecords, Integer pageNumber, ServiceCategory serviceCategory,String applicationNumber);
    
    public void openApplication(Status status, String appNo, Long userId, String userName, String userRole) throws TaskNotFound, NotFoundException;
    
    public List<RtaTaskInfo> actionOnApp(Status status, String appNo, Long userId, String userName, String userRole, CommentModel commentModel, String slotId) throws TaskNotFound, NotFoundException;
    
    public ApplicationStatusModel getAppStatus(String appNo);

    public void completeApp(String executionId, Status status, String approverName, UserType userType, Boolean isAppCompleted);
    

    public Map<String, Object> getInfo(String applicationNumber, String authToken) throws NotFoundException;
    
    public Map<String, Object> getPRNumberType(String applicationNumber) throws NotFoundException;
    
    public void sendSMSEmail(Status status , ApplicationEntity appEntity);
    
    public String getCustomerInvoice(String applicationNumber, String regType);
    
    public String getSignature(String applicationNumber);
    
    public ResponseModel<?> actionOnApp(String userName, Long userId, String userRole, AppActionModel appActionModel) throws TaskNotFound, NotFoundException;

    public ResponseModel<ShowcaseNoticeInfoModel> getShowcaseInfo(Long sessionId) throws UnauthorizedException;

    public ResponseModel<Map<String, Status>> getAppStatusForCurrentStatus(String userName, Long userId, String appNo);

    public void completeAppOnly(String executionId, Status status, String approverName, UserType userType);
    
}

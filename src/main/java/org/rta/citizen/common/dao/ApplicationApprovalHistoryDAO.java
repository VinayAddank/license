/**
 * 
 */
package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;

/**
 * @author arun.verma
 *
 */
public interface ApplicationApprovalHistoryDAO extends GenericDAO<ApplicationApprovalHistoryEntity>{
    
    public List<ApplicationApprovalHistoryEntity> getApprovalHistories(Long appId, Integer iteration, Status status, Long userId);
    
    public List<ApplicationApprovalHistoryEntity> getApprovalHistories(Long userId, Status status, String rtaOfficeCode, ServiceType serviceType, ServiceCategory serviceCategory);

    public ApplicationApprovalHistoryEntity getLastActionOfApplication(String userRole, Long appId, Integer status,
            Integer iteration);
    
    public List<ApplicationApprovalHistoryEntity> getApprovalHistories(Long appId, Status status, List<String> userRoles);

    public ApplicationApprovalHistoryEntity getMyLastAction(Long userId, Long appId, Integer iteration);
    
    public ApplicationApprovalHistoryEntity getRoleLastAction(String userType, Long appId, Integer iteration);
    
}
package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.entity.ApplicationEntity;

public interface ApplicationDAO extends GenericDAO<ApplicationEntity> {

    public ApplicationEntity getApplicationFromSession(Long sessionId);

    public ApplicationEntity getApplication(String applicationNumber);
    
    public List<ApplicationEntity> getApplications(List<String> executionIds);

    List<String> getApplications(List<String> executionId, List<String> serviceCodesList);

    List<ApplicationEntity> getApplications(List<String> executionId, Long currentTime);
    public ApplicationEntity getApplicationByExecutionId(String executionId);

    public ApplicationEntity findByApplicationId(Long id);
    
    public ApplicationEntity getLastApplicationForMviInspectionComment(Long sessionId,String serviceCode);

	public List<ApplicationEntity> getApplicationFromExecId(List<String> exectionIds);
}

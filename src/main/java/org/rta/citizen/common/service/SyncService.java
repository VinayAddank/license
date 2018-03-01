package org.rta.citizen.common.service;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.Status;

public interface SyncService {
    
    public void syncApprovedApplications(Status status, ApplicationEntity appEntity, UserSessionEntity userSession, String approverName);
    
    public void syncData(Status status, ApplicationEntity appEntity, UserSessionEntity userSession, String approverName);

}

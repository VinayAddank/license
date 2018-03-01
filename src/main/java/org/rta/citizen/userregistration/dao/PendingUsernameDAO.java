package org.rta.citizen.userregistration.dao;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.userregistration.entity.PendingUsernameEntity;

public interface PendingUsernameDAO extends GenericDAO<PendingUsernameEntity>  {

    PendingUsernameEntity getByUsernameAndStatus(String username, Status status);

    PendingUsernameEntity getByApplication(Long applicationId);

    PendingUsernameEntity getByApplication(Long applicationId, Status status);

}

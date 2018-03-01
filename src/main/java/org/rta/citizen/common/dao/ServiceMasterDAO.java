package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.entity.ServiceMasterEntity;
import org.rta.citizen.common.enums.ServiceCategory;

public interface ServiceMasterDAO extends GenericDAO<ServiceMasterEntity>{

    public ServiceMasterEntity getEntity(String code);

    public List<ServiceMasterEntity> getServices(boolean havingSlot);

    public List<ServiceMasterEntity> getServices(ServiceCategory serviceCategory);

}

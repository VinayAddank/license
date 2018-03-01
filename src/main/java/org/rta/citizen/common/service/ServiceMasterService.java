package org.rta.citizen.common.service;

import java.util.Collection;

import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.model.ServiceMasterModel;

public interface ServiceMasterService {

    public Collection<ServiceMasterModel> getAll(ServiceCategory serviceCategory);

    public ServiceMasterModel get(String code);

    public String getProcessId(ServiceType serviceType);
}

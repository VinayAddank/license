package org.rta.citizen.common.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.rta.citizen.common.dao.ServiceMasterDAO;
import org.rta.citizen.common.entity.ServiceMasterEntity;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.model.ServiceMasterModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceMasterServiceImpl implements ServiceMasterService {

	@Autowired
	private ServiceMasterDAO serviceMasterDAO;

	@Override
	@Transactional
	public Collection<ServiceMasterModel> getAll(ServiceCategory serviceCategory) {
	    List<ServiceMasterEntity> serviceListEntity = serviceMasterDAO.getServices(serviceCategory);
	    List<ServiceMasterModel> serviceList = new ArrayList<>();
	    if(!ObjectsUtil.isNull(serviceListEntity)){
	        serviceListEntity.forEach(entiy->{
	            ServiceMasterModel mdl = new ServiceMasterModel();
	            mdl.setCode(entiy.getCode());
	            mdl.setName(entiy.getName());
	            serviceList.add(mdl);
	        });
	    }
	    return serviceList;
	}

	@Override
	@Transactional
	public ServiceMasterModel get(String code) {
		ServiceMasterEntity serviceMasterEntity = serviceMasterDAO.getEntity(code);
		if (ObjectsUtil.isNull(serviceMasterEntity)) {
			return null;
		}
		ServiceMasterModel model = new ServiceMasterModel();
		model.setCode(serviceMasterEntity.getCode());
		model.setCreatedBy(serviceMasterEntity.getCreatedBy());
		model.setCreatedOn(serviceMasterEntity.getCreatedOn());
		model.setModifiedBy(serviceMasterEntity.getModifiedBy());
		model.setModifiedOn(serviceMasterEntity.getModifiedOn());
		model.setName(serviceMasterEntity.getName());
		model.setServiceId(serviceMasterEntity.getServiceId());
		return model;
	}

    @Override
    public String getProcessId(ServiceType serviceType) {
        return serviceType.getCode().toUpperCase();
    }

}

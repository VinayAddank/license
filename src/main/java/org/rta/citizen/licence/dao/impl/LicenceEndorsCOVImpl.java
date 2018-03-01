package org.rta.citizen.licence.dao.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.LicenceEndorsCOVDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LicenceEndorsCOVImpl extends BaseDAO<LlrVehicleClassMasterEntity> implements LicenceEndorsCOVDAO {

	public LicenceEndorsCOVImpl() {
		super(LlrVehicleClassMasterEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassMasterEntity> getEndorsDetails() {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		return (List<LlrVehicleClassMasterEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassMasterEntity> getVehicleDescription(String vehicleCode) {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		criteria.add(Restrictions.eq("vehicleClass", vehicleCode));
		return (List<LlrVehicleClassMasterEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassMasterEntity> getVehicleDescription(Set<String> vehicleCode) {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		criteria.add(Restrictions.in("vehicleClass", vehicleCode));
		return (List<LlrVehicleClassMasterEntity>) criteria.list();
	}
}

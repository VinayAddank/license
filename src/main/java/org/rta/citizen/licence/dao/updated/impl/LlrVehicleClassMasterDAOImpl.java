package org.rta.citizen.licence.dao.updated.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.updated.LlrVehicleClassMasterDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LlrVehicleClassMasterDAOImpl extends BaseDAO<LlrVehicleClassMasterEntity>
		implements LlrVehicleClassMasterDAO {

	public LlrVehicleClassMasterDAOImpl() {
		super(LlrVehicleClassMasterEntity.class);
	}

	@Override
	@Transactional
	public LlrVehicleClassMasterEntity getVehicleClassDetails(String covCode) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("vehicleClass", covCode));
		return (LlrVehicleClassMasterEntity) criteria.uniqueResult();
	}
}

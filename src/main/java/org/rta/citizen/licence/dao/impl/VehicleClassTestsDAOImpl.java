package org.rta.citizen.licence.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.VehicleClassTestsDAO;
import org.rta.citizen.licence.entity.tests.VehicleClassTestsEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class VehicleClassTestsDAOImpl extends BaseDAO<VehicleClassTestsEntity> implements VehicleClassTestsDAO {

	public VehicleClassTestsDAOImpl() {
		super(VehicleClassTestsEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VehicleClassTestsEntity> getTests(List<String> covs) {
		Criteria criteria = getSession().createCriteria(VehicleClassTestsEntity.class);
		criteria.add(Restrictions.in("vehicleClass", covs));
		return (List<VehicleClassTestsEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VehicleClassTestsEntity> getTests(SlotServiceType slotServiceType) {
		Criteria criteria = getSession().createCriteria(VehicleClassTestsEntity.class);
		criteria.add(Restrictions.eq("testType", slotServiceType));
		return (List<VehicleClassTestsEntity>) criteria.list();
	}

	@Override
	@Transactional
	public VehicleClassTestsEntity getTest(String cov) {
		Criteria criteria = getSession().createCriteria(VehicleClassTestsEntity.class);
		criteria.add(Restrictions.eq("vehicleClass", cov));
		return (VehicleClassTestsEntity) criteria.uniqueResult();
	}

}

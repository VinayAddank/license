package org.rta.citizen.licence.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.LicenceDAO;
import org.rta.citizen.licence.entity.LlrAgeGroupRefEntity;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.LlrVehicleClassRefEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LicenceDAOImpl extends BaseDAO<LlrAgeGroupRefEntity> implements LicenceDAO {

	public LicenceDAOImpl() {
		super(LlrAgeGroupRefEntity.class);
	}

	@Override
	@Transactional
	public LlrAgeGroupRefEntity getAgeGroup(Integer age) {
		Criteria criteria = getSession().createCriteria(LlrAgeGroupRefEntity.class);
		if (age > SomeConstants.AGE_EIGHTEEN) {
			criteria.add(Restrictions.gt("ageEnd", age));
		} else {
			criteria.add(Restrictions.le("ageStart", SomeConstants.AGE_SIXTEEN));
			criteria.add(Restrictions.ge("ageEnd", age));
		}
		return (LlrAgeGroupRefEntity) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public LlrVehicleClassMasterEntity getDetailCOV(String covCode) {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		criteria.add(Restrictions.eq("vehicleClass", covCode));
		criteria.add(Restrictions.eq("isActive", "Y"));
		return (LlrVehicleClassMasterEntity) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassRefEntity> getCOV(String ageGroup) {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassRefEntity.class);
			criteria.add(Restrictions.eq("ageGroupCode", ageGroup));
		return (List<LlrVehicleClassRefEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassMasterEntity> getAllDetailCOV() {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		criteria.add(Restrictions.eq("isActive", "Y"));
		// criteria.add(Restrictions.eq("vechile_code", covCode));
		return (List<LlrVehicleClassMasterEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassMasterEntity> getAllCOV() {
		Criteria criteria = null;
		criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		return (List<LlrVehicleClassMasterEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassMasterEntity> getCovList(String covCode) {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		criteria.add(Restrictions.eq("vehicleClass", covCode));
		return (List<LlrVehicleClassMasterEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LlrVehicleClassMasterEntity> getCovListIn(List<String> covCodes) {
		Criteria criteria = getSession().createCriteria(LlrVehicleClassMasterEntity.class);
		criteria.add(Restrictions.in("vehicleClass", covCodes));
		return (List<LlrVehicleClassMasterEntity>) criteria.list();
	}
}

package org.rta.citizen.licence.dao.updated.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsHistoryDAO;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsHistoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public class LicensePermitDetailsHistoryDAOImpl extends BaseDAO<LicensePermitDetailsHistoryEntity>
		implements LicensePermitDetailsHistoryDAO {

	public LicensePermitDetailsHistoryDAOImpl() {
		super(LicensePermitDetailsHistoryEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LicensePermitDetailsHistoryEntity> getCOVDetails(Long appId, Integer status) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("applicationId.applicationId", appId));
		criteria.add(Restrictions.eq("status", status));
		return (List<LicensePermitDetailsHistoryEntity>) criteria.list();
	}

	@Override
	public LicensePermitDetailsHistoryEntity getCOVDetails(Long appId, String covName) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("applicationId.applicationId", appId));
		criteria.add(Restrictions.eq("vehicleClassCode", covName));
		return  (LicensePermitDetailsHistoryEntity) criteria.uniqueResult();
	}
}

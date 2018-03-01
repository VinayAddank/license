package org.rta.citizen.licence.dao.updated.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LicensePermitDetailsDAOImpl extends BaseDAO<LicensePermitDetailsEntity>
		implements LicensePermitDetailsDAO {

	public LicensePermitDetailsDAOImpl() {
		super(LicensePermitDetailsEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LicensePermitDetailsEntity> getLicensePermitDetails(Long applicationId) {
		Criteria criteria = getSession().createCriteria(getPersistentClass())
				.add(Restrictions.eq("applicationId.applicationId", applicationId));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<LicensePermitDetailsEntity> getSelectedCOV(Long sessionId) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("applicationId.loginHistory.sessionId", sessionId));
		return (List<LicensePermitDetailsEntity>) criteria.list();
	}

}

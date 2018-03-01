package org.rta.citizen.licence.dao.updated.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.updated.LicenseHolderApprovedDetailsDAO;
import org.rta.citizen.licence.entity.updated.LicenseHolderApprovedDetailsEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LicenseHolderApprovedDetailsDAOImpl extends BaseDAO<LicenseHolderApprovedDetailsEntity>
		implements LicenseHolderApprovedDetailsDAO {

	public LicenseHolderApprovedDetailsDAOImpl() {
		super(LicenseHolderApprovedDetailsEntity.class);
	}

	@Override
	public LicenseHolderApprovedDetailsEntity getHolderApprovedDetails(Long applicationId) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("applicationEntity.applicationId", applicationId));
		return (LicenseHolderApprovedDetailsEntity) criteria.uniqueResult();
	}

}

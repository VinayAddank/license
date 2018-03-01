package org.rta.citizen.common.dao.payment.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TaxDetailDAOImpl extends BaseDAO<TaxDetailEntity> implements TaxDetailDAO {

	public TaxDetailDAOImpl() {
		super(TaxDetailEntity.class);
	}

	@Override
	public TaxDetailEntity getByAppId(ApplicationEntity applicationEntity) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("applicationId", applicationEntity));
        return (TaxDetailEntity) criteria.uniqueResult();
	}
}

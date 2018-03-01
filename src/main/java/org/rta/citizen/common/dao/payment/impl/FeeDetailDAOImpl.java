package org.rta.citizen.common.dao.payment.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.payment.FeeDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.FeeDetailEntity;
import org.springframework.stereotype.Repository;

@Repository
public class FeeDetailDAOImpl extends BaseDAO<FeeDetailEntity> implements FeeDetailDAO {

	public FeeDetailDAOImpl() {
		super(FeeDetailEntity.class);
	}

	@Override
	public FeeDetailEntity getByAppId(ApplicationEntity applicationEntity) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("applicationId", applicationEntity));
        return (FeeDetailEntity) criteria.uniqueResult();
	}

}

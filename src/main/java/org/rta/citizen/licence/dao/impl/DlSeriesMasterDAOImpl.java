package org.rta.citizen.licence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.DlSeriesMasterDAO;
import org.rta.citizen.licence.entity.DlSeriesMasterEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DlSeriesMasterDAOImpl extends BaseDAO<DlSeriesMasterEntity> implements DlSeriesMasterDAO {

	public DlSeriesMasterDAOImpl() {
		super(DlSeriesMasterEntity.class);
	}

	@Override
	public DlSeriesMasterEntity getByYear(Integer year) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("year", year));
		return (DlSeriesMasterEntity) criteria.uniqueResult();
	}

}

package org.rta.citizen.common.dao.payment.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.payment.CitizenInvoiceDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.CitizenInvoiceEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CitizenInvoiceDAOImpl extends BaseDAO<CitizenInvoiceEntity> implements CitizenInvoiceDAO {

	public CitizenInvoiceDAOImpl() {
		super(CitizenInvoiceEntity.class);
	}

	@Override
	public CitizenInvoiceEntity getByAppNdServiceType(ApplicationEntity applicationEntity) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("applicationId", applicationEntity));
        return (CitizenInvoiceEntity) criteria.uniqueResult();
	}

}

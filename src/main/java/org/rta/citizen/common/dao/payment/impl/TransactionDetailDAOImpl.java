package org.rta.citizen.common.dao.payment.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.enums.PaymentType;
import org.rta.citizen.common.enums.Status;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDetailDAOImpl extends BaseDAO<TransactionDetailEntity> implements TransactionDetailDAO {

	public TransactionDetailDAOImpl() {
		super(TransactionDetailEntity.class);
	}

	@Override
	public TransactionDetailEntity getByAppNdServiceType(ApplicationEntity applicationEntity) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("applicationId.applicationId", applicationEntity.getApplicationId()));
        criteria.add(Restrictions.eq("serviceCode", applicationEntity.getServiceCode()));
		criteria.add(Restrictions.disjunction().add(Restrictions.eq("status", Status.OPEN.getValue()))
				.add(Restrictions.eq("status", Status.PENDING.getValue())));
		return (TransactionDetailEntity) criteria.uniqueResult();
	}
	
	@Override
	public TransactionDetailEntity getByAppNdServiceTypeNdPaymentTypeNdStatus(ApplicationEntity applicationEntity ,PaymentType paymentType) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("applicationId.applicationId", applicationEntity.getApplicationId()));
        criteria.add(Restrictions.eq("serviceCode", applicationEntity.getServiceCode()));
		criteria.add(Restrictions.eq("status", Status.CLOSED.getValue()));
		criteria.add(Restrictions.eq("paymentType", paymentType.getId()));
		return (TransactionDetailEntity) criteria.uniqueResult();
	}

	@Override
	public TransactionDetailEntity getByTransNoNdAppNo(String transactionNo, ApplicationEntity appEntity) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("bankTransacNo", transactionNo));
		criteria.add(Restrictions.eq("applicationId.applicationId", appEntity.getApplicationId()));
		criteria.add(Restrictions.eq("serviceCode", appEntity.getServiceCode()));
		return (TransactionDetailEntity) criteria.uniqueResult();
	}
}

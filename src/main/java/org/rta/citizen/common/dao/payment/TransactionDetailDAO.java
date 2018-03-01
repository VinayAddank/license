package org.rta.citizen.common.dao.payment;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.enums.PaymentType;

public interface TransactionDetailDAO extends GenericDAO<TransactionDetailEntity> {

	public TransactionDetailEntity getByAppNdServiceType(ApplicationEntity applicationEntity);
	
	public TransactionDetailEntity getByTransNoNdAppNo(String transactionNo, ApplicationEntity appEntity);
	
	public TransactionDetailEntity getByAppNdServiceTypeNdPaymentTypeNdStatus(ApplicationEntity applicationEntity ,PaymentType paymentType );
}

package org.rta.citizen.common.dao.payment.impl;

import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.payment.TransactionHistoryDAO;
import org.rta.citizen.common.entity.payment.TransactionHistoryEntity;
import org.springframework.stereotype.Repository;
@Repository
public class TransactionHistoryDAOImpl extends BaseDAO<TransactionHistoryEntity> implements TransactionHistoryDAO {

	public TransactionHistoryDAOImpl() {
		super(TransactionHistoryEntity.class);
	}

}

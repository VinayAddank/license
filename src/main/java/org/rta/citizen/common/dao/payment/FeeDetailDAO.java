package org.rta.citizen.common.dao.payment;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.FeeDetailEntity;

public interface FeeDetailDAO extends GenericDAO<FeeDetailEntity> {

	public FeeDetailEntity getByAppId(ApplicationEntity applicationEntity);
}

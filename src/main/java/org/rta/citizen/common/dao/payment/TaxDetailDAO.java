package org.rta.citizen.common.dao.payment;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;

public interface TaxDetailDAO extends GenericDAO<TaxDetailEntity> {

	public TaxDetailEntity getByAppId(ApplicationEntity applicationEntity);
	
}

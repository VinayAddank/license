package org.rta.citizen.common.dao.payment;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.CitizenInvoiceEntity;

public interface CitizenInvoiceDAO extends GenericDAO<CitizenInvoiceEntity> {

	public CitizenInvoiceEntity getByAppNdServiceType(ApplicationEntity applicationEntity);
}

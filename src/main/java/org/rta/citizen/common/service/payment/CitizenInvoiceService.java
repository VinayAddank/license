package org.rta.citizen.common.service.payment;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.CitizenInvoiceEntity;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;

public interface CitizenInvoiceService {

	public CitizenInvoiceEntity createInvoice(ApplicationEntity applicationEntity , ApplicationTaxModel applicationTaxModel);
}

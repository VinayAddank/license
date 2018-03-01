package org.rta.citizen.licence.service.updated;

import java.util.List;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.licence.model.updated.LicensePermitDetailsModel;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface LicensePermitDetailsService {

	public List<LicensePermitDetailsModel> getLicenseDetails(Long applicationId);

	public String update(Long applicationId, SlotServiceType slotServiceType, Status status);
}

package org.rta.citizen.licence.service.updated.impl.details;

import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;

public interface LicenseSelectedDetailsService {

	public LicenseHolderPermitDetails getSelectedDetailsList(String aadharNumber, String uniqueKey);

}

package org.rta.citizen.freshrc;

import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ResponseModel;

public interface FreshRCService {

    ResponseModel<String> saveOrUpdateFreshRC(Long vehicleRcId, Long applicationId, String prNumber);

	void changeOwnerConsent() throws UnauthorizedException;

}

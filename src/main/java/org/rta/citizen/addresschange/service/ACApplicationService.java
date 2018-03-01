package org.rta.citizen.addresschange.service;

import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ResponseModel;

/**
 *	@Author sohan.maurya created on Dec 20, 2016.
 */
public interface ACApplicationService {

    public AadharModel getAadharDetails(Long sessionId, String applicationNumber) throws NotFoundException, Exception;

    public ResponseModel<String> saveOrUpdateAddressChange(Long vehicle, Long applicationId, String prNumber);
}

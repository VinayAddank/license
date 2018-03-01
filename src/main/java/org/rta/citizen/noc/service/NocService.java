package org.rta.citizen.noc.service;

import org.rta.citizen.common.model.ResponseModel;

/**
 *	@Author sohan.maurya created on Jan 4, 2017.
 */
public interface NocService {

    public ResponseModel<String> saveOrUpdateNocDetails(Long vehicleRcId, Long applicationId,String prNumber);

    public ResponseModel<String> saveOrUpdateCancellationNocDetails(Long vehicleRcId, Long applicationId,String prNumber);
}

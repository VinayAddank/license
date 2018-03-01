package org.rta.citizen.vehiclereassignment.service;

import org.rta.citizen.common.model.ResponseModel;

public interface VRApplicationService {
	
	public ResponseModel<String> saveOrUpdateVehicleReassignment(Long applicationId, String prNumber);
}

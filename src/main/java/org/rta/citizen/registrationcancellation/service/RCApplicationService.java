package org.rta.citizen.registrationcancellation.service;

import org.rta.citizen.common.model.ResponseModel;

public interface RCApplicationService {
	
	public ResponseModel<String> saveOrUpdateRegistrationCanellation(String prNumber, Long applicationId); 
}

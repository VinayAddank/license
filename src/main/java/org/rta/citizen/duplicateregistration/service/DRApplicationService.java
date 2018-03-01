package org.rta.citizen.duplicateregistration.service;

import org.rta.citizen.common.model.ResponseModel;

/**
 *	@Author sohan.maurya created on Dec 20, 2016.
 */
public interface DRApplicationService {
	
	public ResponseModel<String> saveOrUpdateDuplicateRegistration(Long applicationId, String prNumber);
}

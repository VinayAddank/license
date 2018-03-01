package org.rta.citizen.registrationrenewal.service;

import org.rta.citizen.common.model.ResponseModel;

/**
 *	@Author sohan.maurya created on Jan 9, 2017.
 */
public interface RegistrationRenewalService {

    public ResponseModel<String> saveOrUpdateRegistrationRenewal(String prNumber, Long applicationId);
}

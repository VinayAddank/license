package org.rta.citizen.rsc.service;

import org.rta.citizen.common.model.ResponseModel;

public interface RSCService {

    ResponseModel<String> saveOrUpdateSuspensionOrCancellationOfRC(String prNumber, Long applicationId);

}

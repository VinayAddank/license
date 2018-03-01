package org.rta.citizen.paytax.service;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.model.ResponseModel;

public interface PayTaxService {

	
	public ResponseModel<String> syncPayTaxData(ApplicationEntity appEntity, UserSessionEntity usersession);
}

package org.rta.citizen.permit.pcv.service;

import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.springframework.stereotype.Service;

@Service
public class PCVAuthenticationService extends AuthenticationService {

    @Override
    protected String getUniqueKey(AuthenticationModel citizen) {
        return citizen.getPrNumber();
    }

    @Override
    protected KeyType getKeyType() {
        return KeyType.PR;
    }
    
    
}

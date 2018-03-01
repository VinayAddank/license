package org.rta.citizen;

import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.model.UserSessionModel;

public interface BaseService {

    public KeyType getKeyType();
    public String getUniqueKey(UserSessionModel session);
    
}

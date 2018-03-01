package org.rta.citizen.common;

import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.model.UserSessionModel;

public interface UserSessionService {

    public UserSessionModel getSession(Long sessionId);
    
    public UserSessionModel getActiveSession(String aadharNumber, String uniqueKey, KeyType keyType);

}

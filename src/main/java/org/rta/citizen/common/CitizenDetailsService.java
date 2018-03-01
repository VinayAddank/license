package org.rta.citizen.common;

import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.UserSessionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CitizenDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserSessionModel loadUserByUsername(String sessionId) throws UsernameNotFoundException {
        UserSessionEntity userDetails = userRepo.findBySessionId(Long.getLong(sessionId));

        if (userDetails == null) {
            throw new IllegalAccessError("No session found.");
        } else {
            return new UserSessionModel(userDetails.getSessionId(), userDetails.getAadharNumber(),
                    Status.getStatus(userDetails.getCompletionStatus()), userDetails.getUniqueKey(),
                    ServiceType.getServiceType(userDetails.getServiceCode()));
        }
    }
}

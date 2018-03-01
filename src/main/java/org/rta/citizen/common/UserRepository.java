package org.rta.citizen.common;

import org.rta.citizen.common.entity.UserSessionEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserSessionEntity, Long> {

    UserSessionEntity findBySessionId(Long id);
}

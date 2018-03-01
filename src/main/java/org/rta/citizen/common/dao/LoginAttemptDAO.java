package org.rta.citizen.common.dao;

import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.enums.KeyType;

public interface LoginAttemptDAO extends GenericDAO<LoginAttemptHistoryEntity> {

	public LoginAttemptHistoryEntity getLoginAttempts(String aadharNumber, String uniqueKey, KeyType keyType,
			Long fromTimestamp, Long toTimestamp);

}

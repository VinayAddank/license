package org.rta.citizen.common;

import javax.transaction.Transactional;

import org.rta.citizen.common.converters.UserSessionConverter;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.model.UserSessionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    @Autowired
    private UserSessionDAO userSessionDAO;

    @Autowired
    private UserSessionConverter userSessionConverter;

    @Override
    @Transactional
    public UserSessionModel getSession(Long sessionId) {
        return userSessionConverter.converToModel(userSessionDAO.getEntity(UserSessionEntity.class, sessionId));
    }

	@Override
	@Transactional
	public UserSessionModel getActiveSession(String aadharNumber, String uniqueKey, KeyType keyType) {
		return userSessionConverter.converToModel(userSessionDAO.getActiveSession(aadharNumber, uniqueKey, keyType));
	}

}

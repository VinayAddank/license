package org.rta.citizen.common.converters;

import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

@Component
public class UserSessionConverter {

    public UserSessionModel converToModel(UserSessionEntity entity) {
        if (ObjectsUtil.isNull(entity)) {
            return null;
        }
        UserSessionModel model = new UserSessionModel();
        model.setAadharNumber(entity.getAadharNumber());
        model.setCompletionStatus(Status.getStatus(entity.getCompletionStatus()));
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedOn(entity.getCreatedOn());
        model.setKeyType(entity.getKeyType());
        model.setLoginTime(entity.getLoginTime());
        model.setModifiedBy(entity.getModifiedBy());
        model.setModifiedOn(entity.getModifiedOn());
        model.setServiceType(ServiceType.getServiceType(entity.getServiceCode()));
        model.setSessionId(entity.getSessionId());
        model.setUniqueKey(entity.getUniqueKey());
        return model;
    }
    
    public UserSessionEntity converToEntity(UserSessionModel model) {
        if (ObjectsUtil.isNull(model)) {
            return null;
        }
        UserSessionEntity entity = new UserSessionEntity();
        entity.setAadharNumber(entity.getAadharNumber());
        entity.setCompletionStatus(entity.getCompletionStatus());
        entity.setCreatedBy(entity.getCreatedBy());
        entity.setCreatedOn(entity.getCreatedOn());
        entity.setKeyType(entity.getKeyType());
        entity.setLoginTime(entity.getLoginTime());
        entity.setModifiedBy(entity.getModifiedBy());
        entity.setModifiedOn(entity.getModifiedOn());
        entity.setServiceCode(model.getServiceType().getCode());
        entity.setSessionId(entity.getSessionId());
        entity.setUniqueKey(entity.getUniqueKey());
        return entity;
    }
    
}

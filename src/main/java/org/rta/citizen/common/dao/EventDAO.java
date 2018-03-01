package org.rta.citizen.common.dao;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.EventEntity;

public interface EventDAO extends GenericDAO<EventEntity> {

	public EventEntity getByApp(ApplicationEntity appEntity);
}

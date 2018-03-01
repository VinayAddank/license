package org.rta.citizen.common.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.EventEntity;
import org.springframework.stereotype.Repository;

@Repository
public class EventDAOImpl extends BaseDAO<EventEntity> implements EventDAO {

	public EventDAOImpl() {
        super(EventEntity.class);
    }

	@Override
	public EventEntity getByApp(ApplicationEntity appEntity) {
		Criteria criteria = getSession().createCriteria(ApplicationEntity.class);
        criteria.add(Restrictions.eq("applicationId", appEntity.getApplicationId()));
        return (EventEntity) criteria.uniqueResult();
	}
}

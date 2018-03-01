package org.rta.citizen.userregistration.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.userregistration.dao.PendingUsernameDAO;
import org.rta.citizen.userregistration.entity.PendingUsernameEntity;
import org.springframework.stereotype.Repository;

@Repository
public class PendingUsernameDAOImpl extends BaseDAO<PendingUsernameEntity> implements PendingUsernameDAO {

    public PendingUsernameDAOImpl() {
        super(PendingUsernameEntity.class);
    }
    
    @Override
    public PendingUsernameEntity getByUsernameAndStatus(String username, Status status) {
        Criteria criteria = getSession().createCriteria(PendingUsernameEntity.class);
        criteria.add(Restrictions.eq("username", username));
        criteria.add(Restrictions.eq("status", status.getValue()));
        return (PendingUsernameEntity) criteria.uniqueResult();
    }
    
    @Override
    public PendingUsernameEntity getByApplication(Long applicationId) {
        Criteria criteria = getSession().createCriteria(PendingUsernameEntity.class);
        criteria.add(Restrictions.eq("application.applicationId", applicationId));
        return (PendingUsernameEntity) criteria.uniqueResult();
    }
    
    @Override
    public PendingUsernameEntity getByApplication(Long applicationId, Status status) {
        Criteria criteria = getSession().createCriteria(PendingUsernameEntity.class);
        criteria.add(Restrictions.eq("application.applicationId", applicationId));
        criteria.add(Restrictions.eq("status", status.getValue()));
        return (PendingUsernameEntity) criteria.uniqueResult();
    }

}

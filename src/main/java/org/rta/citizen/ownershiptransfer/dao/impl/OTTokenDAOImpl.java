/**
 * 
 */
package org.rta.citizen.ownershiptransfer.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.ownershiptransfer.dao.OTTokenDAO;
import org.rta.citizen.ownershiptransfer.entity.OTTokenEntity;
import org.springframework.stereotype.Repository;

/**
 * @author arun.verma
 *
 */

@Repository
public class OTTokenDAOImpl extends BaseDAO<OTTokenEntity> implements OTTokenDAO{

    public OTTokenDAOImpl() {
        super(OTTokenEntity.class);
    }

    @Override
    public OTTokenEntity getTokenEntity(Long applicationId) {
        Criteria criteria = getSession().createCriteria(OTTokenEntity.class);
        criteria.add(Restrictions.eq("applicationEntity.applicationId", applicationId));
        return (OTTokenEntity) criteria.uniqueResult();
    }

    @Override
    public OTTokenEntity getTokenEntity(String tokenNumber) {
        Criteria criteria = getSession().createCriteria(OTTokenEntity.class);
        criteria.add(Restrictions.eq("tokenNumber", tokenNumber));
        return (OTTokenEntity) criteria.uniqueResult();
    }
}

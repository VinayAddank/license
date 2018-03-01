package org.rta.citizen.noc.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.noc.dao.NocDAO;
import org.rta.citizen.noc.entity.NocEntity;
import org.springframework.stereotype.Repository;

/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */
@Repository("nocDAO")
public class NocDAOImpl extends BaseDAO<NocEntity> implements NocDAO {

    public NocDAOImpl() {
        super(NocEntity.class);
    }

    @Override
    public NocEntity getNocDetails(Long applicationId) {
        Criteria criteria =
                getSession().createCriteria(getPersistentClass()).add(Restrictions.eq("applicationId", applicationId));
        return (NocEntity) criteria.uniqueResult();
    }


}

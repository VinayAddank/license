package org.rta.citizen.common.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.AddressDAO;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.entity.AddressEntity;
import org.rta.citizen.common.enums.AddressType;
import org.springframework.stereotype.Repository;

/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */

@Repository("addressDAO")
public class AddressDAOImpl extends BaseDAO<AddressEntity> implements AddressDAO {

    public AddressDAOImpl() {
        super(AddressEntity.class);
    }

    @Override
    public AddressEntity getAddressDetails(Long applicationId, AddressType addressType) {

        Criteria criteria = getSession().createCriteria(AddressEntity.class);
        criteria.add(Restrictions.eq("applicationId.applicationId", applicationId))
                .add(Restrictions.eq("addressType", addressType.getValue()));
        return (AddressEntity) criteria.uniqueResult();
    }

}

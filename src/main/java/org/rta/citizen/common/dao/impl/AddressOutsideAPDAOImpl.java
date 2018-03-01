package org.rta.citizen.common.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.AddressOutsideAPDAO;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.entity.AddressOutsideAPEntity;
import org.rta.citizen.common.enums.AddressType;
import org.springframework.stereotype.Repository;

/**
 *	@Author sohan.maurya created on Dec 16, 2016.
 */

@Repository("addressOutsideAPDAO")
public class AddressOutsideAPDAOImpl extends BaseDAO<AddressOutsideAPEntity> implements AddressOutsideAPDAO {

    public AddressOutsideAPDAOImpl() {
        super(AddressOutsideAPEntity.class);
    }

    @Override
    public AddressOutsideAPEntity getAddressOutsideAPDetails(Long applicationId, AddressType addressType) {

        Criteria criteria = getSession().createCriteria(AddressOutsideAPEntity.class);
        criteria.add(Restrictions.eq("ApplicationId.applicationId", applicationId))
                .add(Restrictions.eq("addressType", addressType.getValue()));
        return (AddressOutsideAPEntity) criteria.uniqueResult();
    }

}

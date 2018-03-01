package org.rta.citizen.common.dao;

import org.rta.citizen.common.entity.AddressEntity;
import org.rta.citizen.common.enums.AddressType;

/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */
public interface AddressDAO extends GenericDAO<AddressEntity> {

    public AddressEntity getAddressDetails(Long applicationId, AddressType addressType);
}

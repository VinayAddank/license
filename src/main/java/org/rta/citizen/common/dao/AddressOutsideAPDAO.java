package org.rta.citizen.common.dao;

import org.rta.citizen.common.entity.AddressOutsideAPEntity;
import org.rta.citizen.common.enums.AddressType;

/**
 *	@Author sohan.maurya created on Dec 16, 2016.
 */

public interface AddressOutsideAPDAO extends GenericDAO<AddressOutsideAPEntity> {

    public AddressOutsideAPEntity getAddressOutsideAPDetails(Long applicationId, AddressType addressType);
}

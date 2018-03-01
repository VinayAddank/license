package org.rta.citizen.common.dao.impl;

import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.RTAOfficeTestConfigDAO;
import org.rta.citizen.slotbooking.entity.RTAOfficeTestConfigEntity;
import org.springframework.stereotype.Repository;

@Repository
public class RTAOfficeTestConfigDAOImpl extends BaseDAO<RTAOfficeTestConfigEntity> implements RTAOfficeTestConfigDAO {

    public RTAOfficeTestConfigDAOImpl() {
        super(RTAOfficeTestConfigEntity.class);
    }

}

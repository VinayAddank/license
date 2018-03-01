package org.rta.citizen.noc.dao;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.noc.entity.NocEntity;

/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */
public interface NocDAO extends GenericDAO<NocEntity> {

    public NocEntity getNocDetails(Long applicationId);
}

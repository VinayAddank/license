/**
 * 
 */
package org.rta.citizen.common.dao.impl;

import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.RejectedAppRemovedExeIdHistoryDAO;
import org.rta.citizen.common.entity.RejectedAppRemovedExeIdHistoryEntity;
import org.springframework.stereotype.Repository;

/**
 * @author arun.verma
 *
 */

@Repository
public class RejectedAppRemovedExeIdHistoryDAOImpl extends BaseDAO<RejectedAppRemovedExeIdHistoryEntity> implements RejectedAppRemovedExeIdHistoryDAO{
    
    public RejectedAppRemovedExeIdHistoryDAOImpl() {
        super(RejectedAppRemovedExeIdHistoryEntity.class);
    }
}

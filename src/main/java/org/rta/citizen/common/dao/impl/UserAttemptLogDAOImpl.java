/**
 * 
 */
package org.rta.citizen.common.dao.impl;

import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.UserAttemptLogDAO;
import org.rta.citizen.common.entity.UserAttemptLogEntity;
import org.springframework.stereotype.Repository;

/**
 * @author arun.verma
 *
 */
@Repository
public class UserAttemptLogDAOImpl extends BaseDAO<UserAttemptLogEntity> implements UserAttemptLogDAO {

	public UserAttemptLogDAOImpl() {
        super(UserAttemptLogEntity.class);
    }
}

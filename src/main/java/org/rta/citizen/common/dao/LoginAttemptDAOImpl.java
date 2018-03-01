package org.rta.citizen.common.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.enums.KeyType;
import org.springframework.stereotype.Repository;

@Repository
public class LoginAttemptDAOImpl extends BaseDAO<LoginAttemptHistoryEntity> implements LoginAttemptDAO {

	public LoginAttemptDAOImpl() {
		super(LoginAttemptHistoryEntity.class);
	}

	@Override
	public LoginAttemptHistoryEntity getLoginAttempts(String aadharNumber, String uniqueKey, KeyType keyType,
			Long fromTimestamp, Long toTimestamp) {
		Criteria criteria = getSession().createCriteria(LoginAttemptHistoryEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNumber));
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("keyType", keyType));
		criteria.add(Restrictions.between("loginTime", fromTimestamp, toTimestamp));
		return (LoginAttemptHistoryEntity) criteria.uniqueResult();
	}

}

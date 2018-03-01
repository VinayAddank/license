package org.rta.citizen.common.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserSessionDAOImpl extends BaseDAO<UserSessionEntity> implements UserSessionDAO {

	public UserSessionDAOImpl() {
		super(UserSessionEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserSessionEntity> getLoginHistory(String aadharNumber, Long fromTimestamp, Long toTimestamp,
			String uniqueKey, KeyType keyType) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNumber));
		criteria.add(Restrictions.between("loginTime", fromTimestamp, toTimestamp));
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("keyType", keyType));
		return (List<UserSessionEntity>) criteria.list();
	}

	@Override
	public UserSessionEntity getActiveSession(String aadharNumber, String uniqueKey, KeyType keyType) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNumber));
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("keyType", keyType));
		criteria.add(Restrictions.or(Restrictions.eq("completionStatus", Status.FRESH.getValue())));
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public UserSessionEntity getActiveSession(String aadharNumber, String uniqueKey, KeyType keyType,
			ServiceType serviceType) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNumber));
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("keyType", keyType));
		criteria.add(Restrictions.or(Restrictions.eq("completionStatus", Status.FRESH.getValue()),
				Restrictions.eq("completionStatus", Status.PENDING.getValue())));
		criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public UserSessionEntity getUserSession(String aadharNumber, String uniqueKey, KeyType keyType,
			ServiceType serviceType, Status status) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNumber));
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("keyType", keyType));
		criteria.add(Restrictions.or(Restrictions.eq("completionStatus", status.getValue())));
		criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
		criteria.addOrder(Order.desc("sessionId"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public UserSessionEntity getLastTheftUserSession(String aadharNumber, String uniqueKey, KeyType keyType) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNumber));
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("keyType", keyType));
		criteria.add(Restrictions.or(/*
										 * Restrictions.eq("completionStatus",
										 * Status.PENDING.getValue()),
										 */Restrictions.eq("completionStatus", Status.APPROVED.getValue())));
		criteria.add(Restrictions.eq("serviceCode", ServiceType.THEFT_INTIMATION.getCode()));
		criteria.addOrder(Order.desc("sessionId"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public UserSessionEntity getTheftUserSession(String uniqueKey, KeyType keyType) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("keyType", keyType));
		criteria.add(Restrictions.or(/*
										 * Restrictions.eq("completionStatus",
										 * Status.PENDING.getValue()),
										 */Restrictions.eq("completionStatus", Status.APPROVED.getValue())));
		criteria.add(Restrictions.eq("serviceCode", ServiceType.THEFT_INTIMATION.getCode()));
		criteria.addOrder(Order.desc("sessionId"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public UserSessionEntity getLatestUserSession(String aadharNumber, String uniqueKey, KeyType keyType,
			ServiceType serviceType, Status status) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		if (aadharNumber != null) {
			criteria.add(Restrictions.eq("aadharNumber", aadharNumber));
		}
		if (uniqueKey != null) {
			criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		}
		if (keyType != null) {
			criteria.add(Restrictions.eq("keyType", keyType));
		}
		if (serviceType != null) {
			criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
		}
		if (status != null) {
			criteria.add(Restrictions.or(Restrictions.eq("completionStatus", status.getValue())));
		}
		criteria.addOrder(Order.desc("sessionId"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public UserSessionEntity getUserSessionByUniqueKey(String uniqueKey, ServiceType serviceType) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		if (uniqueKey != null) {
			criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		}
		if (serviceType != null) {
			criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
		}
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(Status.FRESH.getValue());
		statusList.add(Status.PENDING.getValue());
		criteria.add(Restrictions.in("completionStatus", statusList));
		criteria.addOrder(Order.desc("sessionId"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public UserSessionEntity getUserSession(Long sessionId) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("sessionId", sessionId));
		// criteria.add(Restrictions.eq("keyType", serviceType));
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public List<UserSessionEntity> getAppliedSessions(String aadharNo, String serviceCode) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNo));
		criteria.add(Restrictions.eq("serviceCode", serviceCode).ignoreCase());
		return (List<UserSessionEntity>) criteria.list();
	}

	@Override
	public List<UserSessionEntity> getAppliedSessions(String aadharNo) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNo));
		criteria.add(Restrictions.or(Restrictions.eq("completionStatus", Status.FRESH.getValue()),
				Restrictions.eq("completionStatus", Status.PENDING.getValue())));
		return (List<UserSessionEntity>) criteria.list();
	}

	@Override
	public UserSessionEntity getAppliedSessionsForLLR(String aadharNo) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNo));
		criteria.add(Restrictions.or(Restrictions.eq("completionStatus", Status.FRESH.getValue()),
				Restrictions.eq("completionStatus", Status.PENDING.getValue())));
		criteria.add(Restrictions.or(Restrictions.eq("serviceCode", ServiceType.LL_RETEST.getCode()),
				Restrictions.eq("serviceCode", ServiceType.LL_FRESH.getCode())));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public List<UserSessionEntity> getApprovedAppSessions(String aadharNo) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNo));
		criteria.add(Restrictions.or(Restrictions.eq("completionStatus", Status.APPROVED.getValue()),
				Restrictions.eq("completionStatus", Status.APP_COMPLETED.getValue())));
		return (List<UserSessionEntity>) criteria.list();
	}

	@Override
	public UserSessionEntity getUserSessions(String aadharNo, String uniqueKey, Status status,
			ServiceType serviceType) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		if (!ObjectsUtil.isNull(aadharNo)) {
			criteria.add(Restrictions.eq("aadharNumber", aadharNo));
		}
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		if (!ObjectsUtil.isNull(serviceType)) {
			criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()).ignoreCase());
		}
		criteria.add(Restrictions.eq("completionStatus", status.getValue()));
		criteria.addOrder(Order.desc("sessionId"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public List<UserSessionEntity> getRejectedAppSessions(String aadharNo, String serviceCode) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("aadharNumber", aadharNo));
		criteria.add(Restrictions.eq("completionStatus", Status.REJECTED.getValue()));
		criteria.add(Restrictions.eq("serviceCode", serviceCode).ignoreCase());
		criteria.addOrder(Order.desc("createdOn"));
		return (List<UserSessionEntity>) criteria.list();
	}

	@Override
	public UserSessionEntity getLastAppSessionByUniqueKey(String uniqueKey) {
		/**
		 * get user last session by unique key
		 */
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.addOrder(Order.desc("createdOn"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	public UserSessionEntity getLastSessionForFitnessReInspection(String uniqueKey) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		criteria.add(Restrictions.eq("completionStatus", Status.REJECTED.getValue()));
		List<String> serviceCodes = new ArrayList<String>();
		serviceCodes.add(ServiceType.FC_RENEWAL.getCode());
		serviceCodes.add(ServiceType.FC_RE_INSPECTION_SB.getCode());
		serviceCodes.add(ServiceType.FC_OTHER_STATION.getCode());
		serviceCodes.add(ServiceType.FC_FRESH.getCode());
		criteria.add(Restrictions.in("serviceCode", serviceCodes));
		criteria.addOrder(Order.desc("createdOn"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public UserSessionEntity getLastRejectedApprovedSession(String uniqueKey, List<String> serviceTypeList,
			List<Integer> statusList) {
		Criteria criteria = getSession().createCriteria(UserSessionEntity.class);
		if (SomeConstants.TWELVE.equals(uniqueKey.length())) {
			criteria.add(Restrictions.eq("aadharNumber", uniqueKey));
		} else {
			criteria.add(Restrictions.eq("uniqueKey", uniqueKey));
		}
		if (!ObjectsUtil.isNull(statusList) && statusList.size() > 0) {
			criteria.add(Restrictions.in("completionStatus", statusList));
		}
		if (!ObjectsUtil.isNull(serviceTypeList) && serviceTypeList.size() > 0) {
			criteria.add(Restrictions.in("serviceCode", serviceTypeList));
		}
		criteria.addOrder(Order.desc("createdOn"));
		criteria.setMaxResults(1);
		return (UserSessionEntity) criteria.uniqueResult();
	}
}

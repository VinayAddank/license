package org.rta.citizen.common.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.slotbooking.enums.SlotStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ApplicationDAOImpl extends BaseDAO<ApplicationEntity> implements ApplicationDAO {

	public ApplicationDAOImpl() {
		super(ApplicationEntity.class);
	}

	@Override
	@Transactional
	public ApplicationEntity findByApplicationId(Long id) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq("applicationId", id));
		return (ApplicationEntity) criteria.uniqueResult();
	}

	@Override
	@Transactional
	public ApplicationEntity getApplicationFromSession(Long sessionId) {
		Criteria criteria = getSession().createCriteria(ApplicationEntity.class);
		criteria.add(Restrictions.eq("loginHistory.sessionId", sessionId));
		return (ApplicationEntity) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ApplicationEntity> getApplications(List<String> executionId) {
		Criteria criteria = getSession().createCriteria(ApplicationEntity.class);
		criteria.add(Restrictions.in("executionId", executionId));
		return (List<ApplicationEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ApplicationEntity> getApplications(List<String> executionIdList, Long currentTime) {
		StringBuilder queryString = new StringBuilder("SELECT app.* FROM application AS app")
				.append(" INNER JOIN slot_applications AS sl ON app.application_id=sl.application_id ")
				.append(" INNER JOIN slots as s ON sl.slot_id=s.slot_id")
				.append(" WHERE app.execution_id IN (:executionIds) and s.scheduled_time<:currentTime and slot_status=:slot_status and approval_status=:approvalStatus");
		SQLQuery query = getSession().createSQLQuery(queryString.toString());
		query.setParameterList("executionIds", executionIdList);
		query.setParameter("currentTime", currentTime);
		query.setParameter("slot_status", SlotStatus.BOOKED.toString());
		query.setParameter("approvalStatus", Status.PENDING.getValue());
		query.addEntity(ApplicationEntity.class);
		return (List<ApplicationEntity>) query.list();
	}

	// select * from application where execution_id in ('35001','37535') and
	// service_code in ('TNR');
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getApplications(List<String> executionId, List<String> serviceCodesList) {
		Criteria criteria = getSession().createCriteria(ApplicationEntity.class);
		criteria.add(Restrictions.in("executionId", executionId));
		criteria.add(Restrictions.in("serviceCode", serviceCodesList));
		criteria.setProjection(Projections.property("executionId"));
		return (List<String>) criteria.list();
	}

	@Override
	@Transactional
	public ApplicationEntity getApplication(String applicationNumber) {
		Criteria criteria = getSession().createCriteria(ApplicationEntity.class);
		criteria.add(Restrictions.eq("applicationNumber", applicationNumber));
		return (ApplicationEntity) criteria.uniqueResult();
	}

	@Override
	public ApplicationEntity getApplicationByExecutionId(String executionId) {
		Criteria criteria = getSession().createCriteria(ApplicationEntity.class);
		criteria.add(Restrictions.eq("executionId", executionId));
		return (ApplicationEntity) criteria.uniqueResult();
	}

	@Override
	public ApplicationEntity getLastApplicationForMviInspectionComment(Long sessionId, String serviceCode) {
		Criteria criteria = getSession().createCriteria(ApplicationEntity.class);
		criteria.add(Restrictions.eq("loginHistory.sessionId", sessionId));
		criteria.add(Restrictions.or(Restrictions.eq("serviceCode", ServiceType.FC_RENEWAL.getCode()),
				Restrictions.eq("serviceCode", ServiceType.FC_RE_INSPECTION_SB.getCode())));
		criteria.addOrder(Order.desc("createdOn"));
		criteria.setMaxResults(1);
		return (ApplicationEntity) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ApplicationEntity> getApplicationFromExecId(List<String> exectionIds) {
		StringBuilder queryString = new StringBuilder(
				"SELECT app.* FROM application app INNER JOIN application_bank_transaction_detail app_trans ON app.application_id = app_trans.application_id WHERE app_trans.status =:status AND (app_trans.payment_time  > :consentDate + :diffmilliseconds ) AND app.execution_id IN (:executionIds)");
		Long consentDate = DateUtil.toCurrentUTCTimeStamp();
		SQLQuery query = getSession().createSQLQuery(queryString.toString());
		System.out.println(exectionIds);
		 query.setParameterList("executionIds", exectionIds);
		 query.setParameter("status", Status.CLOSED.getValue());
		 query.setParameter("consentDate", consentDate);
		 query.addEntity(ApplicationEntity.class);
		 query.setParameter("diffmilliseconds",DateUtil.convertDayToMilliseconds());
		 return (List<ApplicationEntity>) query.list();
	}
}

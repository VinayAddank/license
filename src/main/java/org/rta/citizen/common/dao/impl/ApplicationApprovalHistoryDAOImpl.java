/**
 * 
 */
package org.rta.citizen.common.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Repository;

/**
 * @author arun.verma
 *
 */
@Repository
public class ApplicationApprovalHistoryDAOImpl extends BaseDAO<ApplicationApprovalHistoryEntity> implements ApplicationApprovalHistoryDAO{

    public ApplicationApprovalHistoryDAOImpl(){
        super(ApplicationApprovalHistoryEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ApplicationApprovalHistoryEntity> getApprovalHistories(Long appId, Integer iteration, Status status, Long userId) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("applicationEntity.applicationId", appId));
        if(!ObjectsUtil.isNull(iteration)){
            criteria.add(Restrictions.eq("iteration", iteration));
        }
        if(!ObjectsUtil.isNull(userId)){
            criteria.add(Restrictions.eq("rtaUserId", userId));
        }
        if(!ObjectsUtil.isNull(status)){
            criteria.add(Restrictions.eq("status", status.getValue()));
        } else {
            Criterion c = Restrictions.or(Restrictions.eq("status", Status.APPROVED.getValue()), Restrictions.eq("status", Status.REJECTED.getValue()));
            criteria.add(c);
        }
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ApplicationApprovalHistoryEntity> getApprovalHistories(Long userId, Status status,
            String rtaOfficeCode, ServiceType serviceType, ServiceCategory serviceCategory) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        if(!ObjectsUtil.isNull(userId)){
            criteria.add(Restrictions.eq("rtaUserId", userId));
        }
        if(!ObjectsUtil.isNull(status)){
            criteria.add(Restrictions.eq("status", status.getValue()));
        }
        criteria.createAlias("applicationEntity", "app");
        if(!ObjectsUtil.isNull(rtaOfficeCode)){
            criteria.add(Restrictions.eq("app.rtaOfficeCode", rtaOfficeCode));
        }
        if(!ObjectsUtil.isNull(serviceCategory)){
            criteria.add(Restrictions.eq("app.serviceCategory", serviceCategory.getCode()));
        }
        if(!ObjectsUtil.isNull(serviceType)){
            criteria.add(Restrictions.eq("app.serviceCode", serviceType.getCode()));
        }
        criteria.addOrder(Order.desc("createdOn"));
        return criteria.list();
    }
    
    @Override
    public ApplicationApprovalHistoryEntity getLastActionOfApplication(String userRole, Long appId, Integer status, Integer iteration) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("rtaUserRole", userRole));
        criteria.add(Restrictions.eq("applicationEntity.applicationId", appId));
        criteria.add(Restrictions.eq("status", status));
        criteria.add(Restrictions.eq("iteration", iteration));
        return (ApplicationApprovalHistoryEntity)criteria.uniqueResult();
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<ApplicationApprovalHistoryEntity> getApprovalHistories(Long appId, Status status, List<String> userRoles) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.in("rtaUserRole", userRoles));
        criteria.add(Restrictions.eq("applicationEntity.applicationId", appId));
        criteria.add(Restrictions.eq("status", status.getValue()));
        return criteria.list();
	}
	
	@Override
    public ApplicationApprovalHistoryEntity getMyLastAction(Long userId, Long appId, Integer iteration) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("rtaUserId", userId));
        criteria.add(Restrictions.eq("applicationEntity.applicationId", appId));
        if(!ObjectsUtil.isNull(iteration)){
            criteria.add(Restrictions.eq("iteration", iteration));
        }
        criteria.addOrder(Order.desc("createdOn"));
        criteria.setMaxResults(1);
        return (ApplicationApprovalHistoryEntity)criteria.uniqueResult();
    }

    @Override
    public ApplicationApprovalHistoryEntity getRoleLastAction(String userType, Long appId, Integer iteration) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("rtaUserRole", userType));
        criteria.add(Restrictions.eq("applicationEntity.applicationId", appId));
        if(!ObjectsUtil.isNull(iteration)){
            criteria.add(Restrictions.eq("iteration", iteration));
        }
        criteria.addOrder(Order.desc("createdOn"));
        criteria.setMaxResults(1);
        return (ApplicationApprovalHistoryEntity)criteria.uniqueResult();
    }
}

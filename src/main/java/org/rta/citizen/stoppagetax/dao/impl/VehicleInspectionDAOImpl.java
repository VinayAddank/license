/**
 * 
 */
package org.rta.citizen.stoppagetax.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.stoppagetax.dao.VehicleInspectionDAO;
import org.rta.citizen.stoppagetax.entity.VehicleInspectionEntity;
import org.springframework.stereotype.Repository;

/**
 * @author sohan.maurya
 *
 */

@Repository
public class VehicleInspectionDAOImpl extends BaseDAO<VehicleInspectionEntity> implements VehicleInspectionDAO{

	public VehicleInspectionDAOImpl() {
		super(VehicleInspectionEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VehicleInspectionEntity> getVehicleInspectionList(Long userId, Long scheduleInspectionDate) {
		
		 Criteria criteria = getSession().createCriteria(VehicleInspectionEntity.class);
	     criteria.add(Restrictions.le("scheduleInspectionDate", scheduleInspectionDate));
	     criteria.add(Restrictions.eq("inspectionStatus", Status.OPEN.getValue()));
	     criteria.add(Restrictions.eq("revocationStatus", Status.PENDING.getValue()));
	     criteria.add(Restrictions.eq("userId", userId));
	     criteria.addOrder(Order.asc("scheduleInspectionDate"));
	     //criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("applicationId")));
	     return criteria.list();
	}

	@Override
	public VehicleInspectionEntity getVehicleInspection(Long applicationId, Long scheduleInspectionDate) {
		 Criteria criteria = getSession().createCriteria(VehicleInspectionEntity.class);
		 if(!ObjectsUtil.isNull(scheduleInspectionDate)){
			 criteria.add(Restrictions.le("scheduleInspectionDate", scheduleInspectionDate));
			 criteria.add(Restrictions.eq("inspectionStatus", Status.OPEN.getValue()));
		 }
	     criteria.add(Restrictions.eq("applicationId.applicationId", applicationId));
	     criteria.addOrder(Order.asc("scheduleInspectionDate"));
	     criteria.setMaxResults(1);
		return (VehicleInspectionEntity) criteria.uniqueResult();
	}
	
	@Override
	public Integer getVehicleInspectionCount(Long applicationId) {
		Criteria criteria = getSession().createCriteria(VehicleInspectionEntity.class);
	    criteria.add(Restrictions.eq("applicationId.applicationId", applicationId));
		return criteria.list().size();
	}

}

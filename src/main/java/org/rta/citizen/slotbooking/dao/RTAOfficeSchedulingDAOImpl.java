package org.rta.citizen.slotbooking.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.slotbooking.entity.RTAOfficeScheduleEntity;
import org.rta.citizen.slotbooking.entity.RTAOfficeTestConfigEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.stereotype.Repository;

@Repository
public class RTAOfficeSchedulingDAOImpl extends BaseDAO<RTAOfficeScheduleEntity> implements RTAOfficeSchedulingDAO {

    public RTAOfficeSchedulingDAOImpl() {
        super(RTAOfficeScheduleEntity.class);
    }

    @Override
    public RTAOfficeScheduleEntity getSchedule(String code) {
        Criteria criteria = getSession().createCriteria(RTAOfficeScheduleEntity.class);
        criteria.add(Restrictions.eq("rtaOfficeCode", code));
        criteria.add(Restrictions.eq("isEnabled", Boolean.TRUE));
        return (RTAOfficeScheduleEntity) criteria.uniqueResult();
    }
    
    @Override
    public RTAOfficeScheduleEntity getSchedule(String code, SlotServiceType slotServiceType, ServiceCategory serviceCategory) {
        Criteria criteria = getSession().createCriteria(RTAOfficeScheduleEntity.class);
        criteria.add(Restrictions.eq("rtaOfficeCode", code));
        criteria.add(Restrictions.eq("isEnabled", Boolean.TRUE));
        criteria.add(Restrictions.eq("serviceCategory", serviceCategory.getCode()));
        criteria.add(Restrictions.eq("slotServiceType", slotServiceType));
        return (RTAOfficeScheduleEntity) criteria.uniqueResult();
    }
    
    @Override
    public RTAOfficeTestConfigEntity getConfig(String rtaOfficeCode, SlotServiceType slotServiceType, SlotCategory serviceCategory) {
        Criteria criteria = getSession().createCriteria(RTAOfficeTestConfigEntity.class);
        criteria.createAlias("rtaOfficeSchedule", "s");
        criteria.add(Restrictions.eq("s.rtaOfficeCode", rtaOfficeCode));
        criteria.add(Restrictions.eq("isEnabled", Boolean.TRUE));
        criteria.add(Restrictions.eq("s.serviceCategory", serviceCategory.getCode()));
        criteria.add(Restrictions.eq("slotServiceType", slotServiceType));
        return (RTAOfficeTestConfigEntity) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getRTAOfficeScheduleCodes() {
        Criteria criteria = getSession().createCriteria(RTAOfficeScheduleEntity.class);
        criteria.setProjection(Projections.property("rtaOfficeCode"));
        return (List<String>) criteria.list();
    }

}

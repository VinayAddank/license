package org.rta.citizen.slotbooking.dao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.slotbooking.entity.SlotApplicationsEntity;
import org.rta.citizen.slotbooking.entity.SlotEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.enums.SlotStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SlotDAOImpl extends BaseDAO<SlotEntity> implements SlotDAO {

    public SlotDAOImpl() {
        super(SlotEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    public Set<Long> getOccupiedOrBookedSlotsOfRTAOffice(String rtaOfficeCode, Set<Long> occupiedSlots,
            SlotServiceType slotType, Long currentTime, Long scheduledDate, ServiceType serviceType) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.add(Restrictions.eq("rtaOfficeCode", rtaOfficeCode));
        criteria.add(Restrictions.in("startTime", occupiedSlots));
        criteria.add(Restrictions.eq("scheduledDate", scheduledDate));
        criteria.add(Restrictions.eq("slotType", slotType));
        criteria.add(Restrictions.or(Restrictions.eq("slotStatus", SlotStatus.BOOKED.getValue()), Restrictions.and(Restrictions.ge("expiryTime", currentTime), Restrictions.eq("slotStatus", SlotStatus.OCCUPIED.getValue()))));
        criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
        criteria.setProjection(Projections.property("startTime"));
        return new LinkedHashSet<>(criteria.list());
    }

    @Override
    public SlotApplicationsEntity getActiveSlotByApplicationId(Long applicationId, Long currentTime, Long slotId, Integer iteration) {
        
        SQLQuery sqlQuery = getSession().createSQLQuery(
                new StringBuilder("select app.* from slots s inner join slot_applications")
                .append(" as app on app.slot_id=s.slot_id ")
                .append("where (app.application_id=:applicationId and app.slot_id=:slotId and app.slot_status=:occupiedSlotStatus and app.expiry_time>:currentTime and iteration=:iteration) ").toString());
        
        sqlQuery.setParameter("applicationId", applicationId);
        sqlQuery.setParameter("occupiedSlotStatus", SlotStatus.OCCUPIED.toString());
        sqlQuery.setParameter("currentTime", currentTime);
        sqlQuery.setParameter("slotId", slotId);
        sqlQuery.setParameter("iteration", iteration);
        sqlQuery.addEntity(SlotApplicationsEntity.class);
        return (SlotApplicationsEntity) sqlQuery.uniqueResult();
    }
    
    @Override
    public SlotApplicationsEntity getActiveSlotByApplicationId(Long applicationId, Long currentTime) {
        
        SQLQuery sqlQuery = getSession().createSQLQuery(
                new StringBuilder("select app.* from slots s inner join slot_applications")
                .append(" as app on app.slot_id=s.slot_id ")
                .append("where (app.application_id=:applicationId and app.slot_status=:occupiedSlotStatus and app.expiry_time>:currentTime) ").toString());
        
        sqlQuery.setParameter("applicationId", applicationId);
        sqlQuery.setParameter("occupiedSlotStatus", SlotStatus.OCCUPIED.toString());
        sqlQuery.setParameter("currentTime", currentTime);
        sqlQuery.addEntity(SlotApplicationsEntity.class);
        return (SlotApplicationsEntity) sqlQuery.uniqueResult();
    }
    
    @Override
    public SlotApplicationsEntity getActiveSlotByApplicationIdAndDate(Long applicationId, Long currentTime, Long date, Integer iteration) {
        SQLQuery sqlQuery = getSession().createSQLQuery(
                new StringBuilder("select app.* from slots s inner join slot_applications")
                .append(" as app on app.slot_id=s.slot_id ")
                .append("where (app.application_id=:applicationId and app.slot_status=:occupiedSlotStatus and app.expiry_time>:currentTime and s.scheduled_date=:scheduledDate and iteration=:iteration) ").toString());
        
        sqlQuery.setParameter("applicationId", applicationId);
        sqlQuery.setParameter("occupiedSlotStatus", SlotStatus.OCCUPIED.toString());
        sqlQuery.setParameter("currentTime", currentTime);
        sqlQuery.setParameter("scheduledDate", date);
        sqlQuery.setParameter("iteration", iteration);
        sqlQuery.addEntity(SlotApplicationsEntity.class);
        return (SlotApplicationsEntity) sqlQuery.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SlotApplicationsEntity> getActiveSlotByApplicationIdDateAndType(Long applicationId, Long currentTime, Long date, Integer iteration, List<SlotServiceType> slotServiceTypeList) {
        SQLQuery sqlQuery = getSession().createSQLQuery(
                new StringBuilder("select app.* from slots s inner join slot_applications")
                .append(" as app on app.slot_id=s.slot_id ")
                .append("where (app.application_id=:applicationId and app.slot_status=:occupiedSlotStatus and app.expiry_time>:currentTime and s.scheduled_date=:scheduledDate and iteration=:iteration and app.slot_service_type in (:slotServiceType)) ").toString());
        
        sqlQuery.setParameter("applicationId", applicationId);
        sqlQuery.setParameter("occupiedSlotStatus", SlotStatus.OCCUPIED.toString());
        sqlQuery.setParameter("currentTime", currentTime);
        sqlQuery.setParameter("scheduledDate", date);;
        sqlQuery.setParameter("iteration", iteration);
        sqlQuery.setParameterList("slotServiceType", slotServiceTypeList.stream().map(sst->sst.getLabel()).collect(Collectors.toList()));
        sqlQuery.addEntity(SlotApplicationsEntity.class);
        return (List<SlotApplicationsEntity>) sqlQuery.list();
    }
    
    @Override
    @Transactional
    public List<SlotApplicationsEntity> getBookedSlotByApplicationIdAndIteration(Long applicationId, Integer iteration) {
        SQLQuery sqlQuery = getSession().createSQLQuery(
                new StringBuilder("select app.* from slots s inner join slot_applications")
                .append(" as app on app.slot_id=s.slot_id ")
                .append("where (app.application_id=:applicationId and app.slot_status=:bookedSlotStatus and iteration=:iteration) ").toString());
        
        sqlQuery.setParameter("applicationId", applicationId);
        sqlQuery.setParameter("bookedSlotStatus", SlotStatus.BOOKED.toString());
        sqlQuery.setParameter("iteration", iteration);
        sqlQuery.addEntity(SlotApplicationsEntity.class);
        return (List<SlotApplicationsEntity>) sqlQuery.list();
    }
    
//    @Override
//    public List<SlotApplicationsEntity> getBookedSlotByApplicationIdAndIteration(Long applicationId, Integer iteration, List<SlotServiceType> slotServiceType) {
//        SQLQuery sqlQuery = getSession().createSQLQuery(
//                new StringBuilder("select app.* from slots s inner join slot_applications")
//                .append(" as app on app.slot_id=s.slot_id ")
//                .append("where (app.application_id=:applicationId and app.slot_status=:bookedSlotStatus and iteration=:iteration and slot_service_type in (:slotServiceType)) ").toString());
//        
//        sqlQuery.setParameter("applicationId", applicationId);
//        sqlQuery.setParameter("bookedSlotStatus", SlotStatus.BOOKED.toString());
//        sqlQuery.setParameter("iteration", iteration);
//        sqlQuery.setParameterList("slotServiceType", slotServiceType);
//        sqlQuery.addEntity(SlotApplicationsEntity.class);
//        return (List<SlotApplicationsEntity>) sqlQuery.list();
//    }
    
    @Override
    public List<SlotApplicationsEntity> getBookedSlotAndNotProcessedByApplicationIdAndIteration(Long applicationId, Integer iteration) {
        SQLQuery sqlQuery = getSession().createSQLQuery(
                new StringBuilder("select app.* from slots s inner join slot_applications")
                .append(" as app on app.slot_id=s.slot_id ")
                .append("where (app.application_id=:applicationId and app.slot_status=:bookedSlotStatus and iteration=:iteration and app.approval_status=:approvalStatus) ").toString());
        
        sqlQuery.setParameter("applicationId", applicationId);
        sqlQuery.setParameter("bookedSlotStatus", SlotStatus.BOOKED.toString());
        sqlQuery.setParameter("approvalStatus", Status.PENDING.getValue());
        sqlQuery.setParameter("iteration", iteration);
        sqlQuery.addEntity(SlotApplicationsEntity.class);
        return (List<SlotApplicationsEntity>) sqlQuery.list();
    }

    @Override
    @Deprecated
    public SlotEntity getExpired(String rtaOfficeCode, Long scheduledDate, Long scheduledTime, SlotServiceType slotType, ServiceType serviceType) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.add(Restrictions.eq("scheduledDate", scheduledDate));
        criteria.add(Restrictions.eq("startTime", scheduledTime));
        criteria.add(Restrictions.eq("rtaOfficeCode", rtaOfficeCode));
        criteria.add(Restrictions.eq("slotType", slotType));
        criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
        return (SlotEntity) criteria.uniqueResult();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<SlotEntity> getSlots(Long appId, Long timestamp, SlotServiceType slotServiceType, ServiceType serviceType) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.add(Restrictions.eq("applicationId.applicationId", appId));
        criteria.add(Restrictions.eq("slotType", slotServiceType));
        criteria.add(Restrictions.lt("scheduledTime", timestamp));
        criteria.add(Restrictions.eq("slotStatus", SlotStatus.BOOKED));
        criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
        return (List<SlotEntity>) criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SlotEntity> getSlotsByApplicationId(Long applicationId) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.add(Restrictions.eq("applicationId.applicationId", applicationId));
        return (List<SlotEntity>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> getOccupiedOrBookedSlotsOfRTAOffice(String rtaOfficeCode, Set<Long> allSlots,
            SlotServiceType slotType, Long currentTime, Long scheduledDate, ServiceType serviceType, Integer numberOfSimultaneousSlots) {
        Criteria criteria = getSession().createCriteria(SlotApplicationsEntity.class);
        criteria.add(Restrictions.eq("slot.rtaOfficeCode", rtaOfficeCode));
        criteria.add(Restrictions.in("slot.startTime", allSlots));
        criteria.add(Restrictions.eq("slot.scheduledDate", scheduledDate));
        criteria.add(Restrictions.eq("slotType", slotType));
        criteria.add(Restrictions.or(Restrictions.eq("slotStatus", SlotStatus.BOOKED.getValue()), Restrictions.and(Restrictions.ge("expiryTime", currentTime), Restrictions.eq("slotStatus", SlotStatus.OCCUPIED.getValue()))));
        criteria.add(Restrictions.eq("serviceCode", serviceType.getCode()));
        criteria.setProjection(Projections.property("startTime"));
        return new LinkedHashSet<>(criteria.list());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getBookedSlots(String rtaOfficeCode, Long date) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.setProjection(Projections.property("startTime"));
        criteria.add(Restrictions.eq("scheduledDate", date));
        criteria.add(Restrictions.eq("isCompleted", Boolean.TRUE));
        return (List<Long>)criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getBookedSlots(String rtaOfficeCode, Long date, SlotServiceType slotServiceType, SlotCategory serviceCategory) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.setProjection(Projections.property("startTime"));
        criteria.add(Restrictions.eq("scheduledDate", date));
        criteria.add(Restrictions.eq("isCompleted", Boolean.TRUE));
        criteria.add(Restrictions.eq("serviceCategory", serviceCategory.getCode()).ignoreCase());
        criteria.add(Restrictions.eq("slotServiceType", slotServiceType));
        return (List<Long>)criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SlotApplicationsEntity> getExpiredSlots(String rtaOfficeCode, Long currentTime, Long day) {
        SQLQuery sqlQuery = getSession().createSQLQuery(
                new StringBuilder("select app.* from slots s inner join slot_applications")
                .append(" as app on app.slot_id=s.slot_id ")
                .append("where app.slot_status=:slotStatus and app.expiry_time<:expiryTime and s.rta_office_code=:rtaOfficeCode").toString());
        sqlQuery.setParameter("slotStatus", SlotStatus.OCCUPIED.toString());
        sqlQuery.setParameter("expiryTime", currentTime);
        sqlQuery.setParameter("rtaOfficeCode", rtaOfficeCode);
        sqlQuery.addEntity(SlotApplicationsEntity.class);
        List<SlotApplicationsEntity> results = (List<SlotApplicationsEntity>)sqlQuery.list();
        return results;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SlotEntity> getSlotsOfDay(String rtaOfficeCode, Long day) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.add(Restrictions.eq("scheduledDate", day));
        criteria.add(Restrictions.eq("rtaOfficeCode", rtaOfficeCode));
        return (List<SlotEntity>)criteria.list();
    }
    
    @Override
    public SlotEntity getSlot(String rtaOfficeCode, Long scheduledDate, Long startTime) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.add(Restrictions.eq("startTime", startTime));
        criteria.add(Restrictions.eq("scheduledDate", scheduledDate));
        criteria.add(Restrictions.eq("rtaOfficeCode", rtaOfficeCode));
        return (SlotEntity)criteria.uniqueResult();
    }
    
    @Override
    public SlotEntity getSlot(String rtaOfficeCode, Long scheduledDate, Long startTime, SlotServiceType slotServiceType, SlotCategory serviceCategory) {
        Criteria criteria = getSession().createCriteria(SlotEntity.class);
        criteria.add(Restrictions.eq("startTime", startTime));
        criteria.add(Restrictions.eq("scheduledDate", scheduledDate));
        criteria.add(Restrictions.eq("rtaOfficeCode", rtaOfficeCode));
        criteria.add(Restrictions.eq("serviceCategory", serviceCategory.getCode()));
        criteria.add(Restrictions.eq("slotServiceType", slotServiceType));
        return (SlotEntity)criteria.uniqueResult();
    }
    
    @Override
    public void saveOrUpdateSlotApplications(SlotApplicationsEntity entity) {

        getSession().saveOrUpdate(entity);

    }
    
    /**
     * return slot application attached to slotId if occupied and not expired
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SlotApplicationsEntity> getSlotApplication(Long applicationId, Long slotId, Long currentTime) {
        Criteria criteria = getSession().createCriteria(SlotApplicationsEntity.class);
        criteria.add(Restrictions.eq("application.applicationId", applicationId));
        criteria.add(Restrictions.eq("slot.slotId", slotId));
        criteria.add(Restrictions.and(Restrictions.eq("slotStatus", SlotStatus.OCCUPIED), Restrictions.gt("expiryTime", currentTime)));
        return (List<SlotApplicationsEntity>)criteria.list();
    }
    
    /**
     * return slot application attached to slotId if occupied and not expired
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SlotApplicationsEntity> getSlotApplication(Long applicationId, List<Long> slotIds, Long currentTime) {
        Criteria criteria = getSession().createCriteria(SlotApplicationsEntity.class);
        criteria.add(Restrictions.eq("application.applicationId", applicationId));
        criteria.add(Restrictions.in("slot.slotId", slotIds));
        criteria.add(Restrictions.and(Restrictions.eq("slotStatus", SlotStatus.OCCUPIED), Restrictions.gt("expiryTime", currentTime)));
        return (List<SlotApplicationsEntity>)criteria.list();
    }
    
    /**
     * return slots that is booked(or confirmed)
     * @param applicationId
     * @param slotId
     * @return SlotApplicationsEntity
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SlotApplicationsEntity> getBookedSlot(Long applicationId, Integer iteration) {
        Criteria criteria = getSession().createCriteria(SlotApplicationsEntity.class);
        criteria.add(Restrictions.eq("application.applicationId", applicationId));
        criteria.add(Restrictions.eq("slotStatus", SlotStatus.BOOKED));
        criteria.add(Restrictions.eq("iteration", iteration));
        return (List<SlotApplicationsEntity>)criteria.list();
    }

}

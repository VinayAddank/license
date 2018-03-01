package org.rta.citizen.slotbooking.dao;

import java.util.List;
import java.util.Set;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.slotbooking.entity.SlotApplicationsEntity;
import org.rta.citizen.slotbooking.entity.SlotEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface SlotDAO extends GenericDAO<SlotEntity> {

    Set<Long> getOccupiedOrBookedSlotsOfRTAOffice(String rtaOfficeCode, Set<Long> occupiedSlots,
            SlotServiceType slotType, Long currentTime, Long scheduledDate, ServiceType serviceType);

    SlotEntity getExpired(String rtaOfficeCode, Long scheduledDate, Long scheduledTime, SlotServiceType slotType,
            ServiceType serviceType);

    List<SlotEntity> getSlots(Long appIds, Long timestamp, SlotServiceType slotServiceType, ServiceType serviceType);

    List<SlotEntity> getSlotsByApplicationId(Long applicationId);

    Set<Long> getOccupiedOrBookedSlotsOfRTAOffice(String rtaOfficeCode, Set<Long> allSlots,
            SlotServiceType slotType, Long currentTime, Long scheduledDate, ServiceType serviceType,
            Integer numberOfSimultaneousSlots);

    List<Long> getBookedSlots(String rtaOfficeCode, Long date);

    SlotEntity getSlot(String rtaOfficeCode, Long scheduledDate, Long startTime);

    void saveOrUpdateSlotApplications(SlotApplicationsEntity entity);

    SlotApplicationsEntity getActiveSlotByApplicationId(Long applicationId, Long currentTime, Long scheduledDate, Integer iteration);

    List<SlotApplicationsEntity> getSlotApplication(Long applicationId, Long slotId, Long currentTime);

    List<SlotApplicationsEntity> getBookedSlot(Long applicationId, Integer iteration);

    List<SlotEntity> getSlotsOfDay(String rtaOfficeCode, Long day);

    List<SlotApplicationsEntity> getExpiredSlots(String rtaOfficeCode, Long currentTime, Long day);

    SlotApplicationsEntity getActiveSlotByApplicationId(Long applicationId, Long currentTime);

    SlotApplicationsEntity getActiveSlotByApplicationIdAndDate(Long applicationId, Long currentTime, Long date, Integer iteration);

    List<SlotApplicationsEntity> getBookedSlotByApplicationIdAndIteration(Long applicationId, Integer iteration);

//    List<SlotApplicationsEntity> getActiveSlotByApplicationIdDateAndType(Long applicationId, Long currentTime,
//            Long date, Integer iteration);

    List<SlotApplicationsEntity> getSlotApplication(Long applicationId, List<Long> slotId, Long currentTime);

    List<SlotApplicationsEntity> getActiveSlotByApplicationIdDateAndType(Long applicationId, Long currentTime,
            Long date, Integer iteration, List<SlotServiceType> slotServiceTypeList);

    List<SlotApplicationsEntity> getBookedSlotAndNotProcessedByApplicationIdAndIteration(Long applicationId,
            Integer iteration);

    List<Long> getBookedSlots(String rtaOfficeCode, Long date, SlotServiceType slotServiceType,
            SlotCategory serviceCategory);

    SlotEntity getSlot(String rtaOfficeCode, Long scheduledDate, Long startTime, SlotServiceType slotServiceType,
            SlotCategory serviceCategory);

//    List<SlotApplicationsEntity> getBookedSlotByApplicationIdAndIteration(Long applicationId, Integer iteration,
//            List<SlotServiceType> slotServiceType);
    
}

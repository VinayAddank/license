package org.rta.citizen.slotbooking.dao;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.slotbooking.entity.RTAOfficeScheduleEntity;
import org.rta.citizen.slotbooking.entity.RTAOfficeTestConfigEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface RTAOfficeSchedulingDAO extends GenericDAO<RTAOfficeScheduleEntity>{

    public RTAOfficeScheduleEntity getSchedule(String code);

    RTAOfficeScheduleEntity getSchedule(String code, SlotServiceType slotServiceType, ServiceCategory serviceCategory);

    List<String> getRTAOfficeScheduleCodes();

    RTAOfficeTestConfigEntity getConfig(String rtaOfficeCode, SlotServiceType slotServiceType,
            SlotCategory serviceCategory);
    
}

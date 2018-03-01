package org.rta.citizen.slotbooking.service;

import java.util.List;
import java.util.Set;

import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.model.RTAOfficeScheduleModel;
import org.rta.citizen.slotbooking.model.SlotModel;

public interface RTAOfficeSchedulingService {

    public RTAOfficeScheduleModel getSchedule(String code);

    public Set<Long> getTimeSlots(String code);

    List<SlotModel> getTimeSlots(Long startTime, Long endTime, Long duration);

    RTAOfficeScheduleModel getSchedule(String code, SlotServiceType slotServiceType, SlotCategory serviceCategory);

    Set<Long> getTimeSlots(String code, SlotServiceType slotServiceType, SlotCategory serviceCategory);

    void insertIntoDB(SlotCategory serviceCategory);

}

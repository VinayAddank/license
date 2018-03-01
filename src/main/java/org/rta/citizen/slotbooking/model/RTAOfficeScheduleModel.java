package org.rta.citizen.slotbooking.model;

import org.rta.citizen.slotbooking.enums.SlotServiceType;

public class RTAOfficeScheduleModel {
    
    private Long rtaOfficeScheduleId;

    private String rtaOfficeCode;

    private Long startTime;

    private Long endTime;

    private Boolean isEnabled;

    private Long duration;
    
    private Integer numberOfSimultaneousSlots;
    
    private SlotServiceType slotServiceType;

    public Long getRtaOfficeScheduleId() {
        return rtaOfficeScheduleId;
    }

    public void setRtaOfficeScheduleId(Long rtaOfficeScheduleId) {
        this.rtaOfficeScheduleId = rtaOfficeScheduleId;
    }

    public String getRtaOfficeCode() {
        return rtaOfficeCode;
    }

    public void setRtaOfficeCode(String rtaOfficeCode) {
        this.rtaOfficeCode = rtaOfficeCode;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getNumberOfSimultaneousSlots() {
        return numberOfSimultaneousSlots;
    }

    public void setNumberOfSimultaneousSlots(Integer numberOfSimultaneousSlots) {
        this.numberOfSimultaneousSlots = numberOfSimultaneousSlots;
    }

    public SlotServiceType getSlotServiceType() {
        return slotServiceType;
    }

    public void setSlotServiceType(SlotServiceType slotServiceType) {
        this.slotServiceType = slotServiceType;
    }


}

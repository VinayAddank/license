package org.rta.citizen.slotbooking.model;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.enums.SlotStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class SlotModel implements Comparable<SlotModel> {

    private Long applicationId;
    private Long slotId;
    private Long startTime;
    private Long endTime;
    private Long duration;
    private Long scheduledTime;
    private Long scheduledDate;
    private SlotStatus slotStatus;
    private RTAOfficeModel rtaOfficeModel;
    private SlotServiceType type;
    private Status status;
    private String serviceCategory;
    
    public SlotModel() {
        // TODO Auto-generated constructor stub
    }

    public SlotModel(Long startTime, SlotServiceType type, String serviceCategory) {
        this.startTime = startTime;
        this.type = type;
        this.serviceCategory = serviceCategory;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Long getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Long scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public SlotStatus getSlotStatus() {
        return slotStatus;
    }

    public void setSlotStatus(SlotStatus slotStatus) {
        this.slotStatus = slotStatus;
    }

    public RTAOfficeModel getRtaOfficeModel() {
        return rtaOfficeModel;
    }

    public void setRtaOfficeModel(RTAOfficeModel rtaOfficeModel) {
        this.rtaOfficeModel = rtaOfficeModel;
    }

    @JsonProperty("slotServiceType")
    public SlotServiceType getType() {
        return type;
    }

    public void setType(SlotServiceType type) {
        this.type = type;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    @Override
    public String toString() {
        return "SlotModel [startTime=" + startTime + ", type=" + type + ", serviceCategory=" + serviceCategory + "]";
    }

    @Override
    public int compareTo(SlotModel o) {
        if (o.getStartTime() == this.getStartTime()) {
            return 0;
        }
        return this.getStartTime() > o.getStartTime() ? 1 : -1;
    }



}

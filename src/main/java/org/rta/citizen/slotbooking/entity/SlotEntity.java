package org.rta.citizen.slotbooking.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.rta.citizen.common.entity.BaseEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

@Entity
@Table(name = "slots", uniqueConstraints={
        @UniqueConstraint(columnNames = {"start_time", "scheduled_date", "rta_office_code", "slot_service_type", "service_category"})
    })
public class SlotEntity extends BaseEntity {

    private static final long serialVersionUID = -587922855862257548L;

    @Id
    @Column(name = "slot_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slots_seq")
    @SequenceGenerator(name = "slots_seq", sequenceName = "slots_seq", allocationSize = 1)
    private Long slotId;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "scheduled_date")
    private Long scheduledDate;

    @Column(name = "scheduled_time")
    private Long scheduledTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "rta_office_code")
    private String rtaOfficeCode;

    @Column(name = "service_code")
    private String serviceCode;
    
    @Column(name = "is_completed")
    private Boolean isCompleted;
    
    @Column(name = "application_count")
    private Integer applicationCount;
    
    @Column(name = "slot_service_type")
    @Enumerated(EnumType.STRING)
    private SlotServiceType slotServiceType;
    
    @Column(name = "service_category", length = 20)
    private String serviceCategory;

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

    public Long getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Long scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getRtaOfficeCode() {
        return rtaOfficeCode;
    }

    public void setRtaOfficeCode(String rtaOfficeCode) {
        this.rtaOfficeCode = rtaOfficeCode;
    }
    
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public SlotServiceType getSlotServiceType() {
        return slotServiceType;
    }

    public void setSlotServiceType(SlotServiceType slotServiceType) {
        this.slotServiceType = slotServiceType;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

}

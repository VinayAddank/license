package org.rta.citizen.slotbooking.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;

@Entity
@Table(name = "rta_office_schedule")
public class RTAOfficeScheduleEntity extends BaseEntity {

    private static final long serialVersionUID = -222979470183408324L;

    @Id
    @Column(name = "rta_office_schedule_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rta_office_schedule_seq")
    @SequenceGenerator(name = "rta_office_schedule_seq", sequenceName = "rta_office_schedule_seq", allocationSize = 1)
    private Long rtaOfficeScheduleId;

    @Column(name = "rta_office_code")
    private String rtaOfficeCode;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "is_enabled")
    private Boolean isEnabled;
    
    @Column(name = "no_of_simul_slots")
    private Integer numberOfSimultaneousSlots;
    
    @Column(name = "service_category", length = 20)
    private String serviceCategory;
    
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

    public Integer getNumberOfSimultaneousSlots() {
        return numberOfSimultaneousSlots;
    }

    public void setNumberOfSimultaneousSlots(Integer numberOfSimultaneousSlots) {
        this.numberOfSimultaneousSlots = numberOfSimultaneousSlots;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

}

package org.rta.citizen.slotbooking.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

@Entity
@Table(name = "rta_office_test_config")
public class RTAOfficeTestConfigEntity extends BaseEntity {

    private static final long serialVersionUID = -222979470183408324L;

    @Id
    @Column(name = "rta_office_test_config_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rta_office_test_config_seq")
    @SequenceGenerator(name = "rta_office_test_config_seq", sequenceName = "rta_office_test_config_seq", allocationSize = 1)
    private Long rtaOfficeTestConfigId;
    
    @Column(name = "slot_service_type")
    @Enumerated(EnumType.STRING)
    private SlotServiceType slotServiceType;
    
    @Column(name = "simul_app_count")
    private Integer simulApplicationCount;
    
    @Column(name = "duration")
    private Long duration;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "rta_office_schedule_id")
    private RTAOfficeScheduleEntity rtaOfficeSchedule;
    
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    public Long getRtaOfficeTestConfigId() {
        return rtaOfficeTestConfigId;
    }

    public void setRtaOfficeTestConfigId(Long rtaOfficeTestConfigId) {
        this.rtaOfficeTestConfigId = rtaOfficeTestConfigId;
    }

    public SlotServiceType getSlotServiceType() {
        return slotServiceType;
    }

    public void setSlotServiceType(SlotServiceType slotServiceType) {
        this.slotServiceType = slotServiceType;
    }

    public Integer getSimulApplicationCount() {
        return simulApplicationCount;
    }

    public void setSimulApplicationCount(Integer simulApplicationCount) {
        this.simulApplicationCount = simulApplicationCount;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public RTAOfficeScheduleEntity getRtaOfficeSchedule() {
        return rtaOfficeSchedule;
    }

    public void setRtaOfficeSchedule(RTAOfficeScheduleEntity rtaOfficeSchedule) {
        this.rtaOfficeSchedule = rtaOfficeSchedule;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
}

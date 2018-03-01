package org.rta.citizen.slotbooking.entity;

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

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.BaseEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.enums.SlotStatus;

@Entity
@Table(name = "slot_applications")
public class SlotApplicationsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "slot_applications_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slot_applications_seq")
    @SequenceGenerator(name = "slot_applications_seq", sequenceName = "slot_applications_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationEntity application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "slot_id")
    private SlotEntity slot;

    @Column(name = "slot_status")
    @Enumerated(value = EnumType.STRING)
    private SlotStatus slotStatus;

    @Column(name = "expiry_time")
    private Long expiryTime;
    
    @Column(name = "service_code")
    private String serviceCode;
    
    @Column(name = "slot_service_type")
    @Enumerated(EnumType.STRING)
    private SlotServiceType slotServiceType;
    
    @Column(name = "iteration", columnDefinition = "int default 1")
    private Integer iteration;
    
    @Column(name = "approval_status")
    private Integer approvalStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }

    public SlotEntity getSlot() {
        return slot;
    }

    public void setSlot(SlotEntity slot) {
        this.slot = slot;
    }

    public SlotStatus getSlotStatus() {
        return slotStatus;
    }

    public void setSlotStatus(SlotStatus slotStatus) {
        this.slotStatus = slotStatus;
    }

    public Long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public SlotServiceType getSlotServiceType() {
        return slotServiceType;
    }

    public void setSlotServiceType(SlotServiceType slotServiceType) {
        this.slotServiceType = slotServiceType;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Integer getIteration() {
        return iteration;
    }

    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }


}

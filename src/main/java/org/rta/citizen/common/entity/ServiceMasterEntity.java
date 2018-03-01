package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "service_master")
public class ServiceMasterEntity extends BaseMasterEntity {

    private static final long serialVersionUID = -8138249176019155207L;

    @Id
    @Column(name = "service_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_seq")
    @SequenceGenerator(name = "service_seq", sequenceName = "service_seq", allocationSize = 1)
    private Long serviceId;
    
    @Column(name = "slot_applicable")
    private Boolean slotApplicable;

    @Column(name = "service_category", length = 20)
    private String serviceCategory;
    
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Boolean getSlotApplicable() {
        return slotApplicable;
    }

    public void setSlotApplicable(Boolean slotApplicable) {
        this.slotApplicable = slotApplicable;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

}

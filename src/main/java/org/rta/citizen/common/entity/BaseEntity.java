package org.rta.citizen.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -1555152942774514424L;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_on", length = 30)
    private Long createdOn;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_on", length = 30)
    private Long modifiedOn;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }



}

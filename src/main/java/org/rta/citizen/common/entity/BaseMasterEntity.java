package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @Author sohan.maurya created on Jul 11, 2016.
 */
@MappedSuperclass
public abstract class BaseMasterEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "code")
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}

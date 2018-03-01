package org.rta.citizen.userregistration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.BaseEntity;

@Entity
@Table(name = "pending_username")
public class PendingUsernameEntity extends BaseEntity {

    private static final long serialVersionUID = -4308516310022033259L;
    
    @Id
    @Column(name = "pending_username_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pending_username_seq")
    @SequenceGenerator(name = "pending_username_seq", sequenceName = "pending_username_seq", allocationSize = 1)
    private Long pendingUsernameId;
    
    @Column(name = "username", unique = true)
    private String username;
    
    @Column(name = "status")
    private Integer status;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id")
    private ApplicationEntity application;

    public Long getPendingUsernameId() {
        return pendingUsernameId;
    }

    public void setPendingUsernameId(Long pendingUsernameId) {
        this.pendingUsernameId = pendingUsernameId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }
    
}

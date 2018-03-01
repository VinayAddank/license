/**
 * 
 */
package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author arun.verma
 *
 */
@Entity
@Table(name = "application_approval_history")
public class ApplicationApprovalHistoryEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 319082705426306319L;

    @Id
    @Column(name = "app_history_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_history_seq")
    @SequenceGenerator(name = "app_history_seq", sequenceName = "app_history_seq", allocationSize = 1)
    private Long appHistoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="application_id")
    private ApplicationEntity applicationEntity;

    @Column(name = "rta_user_id")
    private Long rtaUserId;

    @Column(name = "rta_user_role")
    private String rtaUserRole;

    @Column(name = "comments")
    private String comments;

    @Column(name = "status")
    private Integer status;
    
    @Column(name = "iteration")
    private Integer iteration;

    public Long getAppHistoryId() {
        return appHistoryId;
    }

    public void setAppHistoryId(Long appHistoryId) {
        this.appHistoryId = appHistoryId;
    }

    public ApplicationEntity getApplicationEntity() {
        return applicationEntity;
    }

    public void setApplicationEntity(ApplicationEntity applicationEntity) {
        this.applicationEntity = applicationEntity;
    }

    public Long getRtaUserId() {
        return rtaUserId;
    }

    public void setRtaUserId(Long rtaUserId) {
        this.rtaUserId = rtaUserId;
    }

    public String getRtaUserRole() {
        return rtaUserRole;
    }

    public void setRtaUserRole(String rtaUserRole) {
        this.rtaUserRole = rtaUserRole;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIteration() {
        return iteration;
    }

    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

}

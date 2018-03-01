package org.rta.citizen.noc.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.rta.citizen.common.entity.BaseEntity;

/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */

// @Entity
// @Table(name = "noc_details")
public class NocEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "noc_details_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "noc_details_seq")
    @SequenceGenerator(name = "noc_details_seq", sequenceName = "noc_details_seq", allocationSize = 1)
    private Long nocDetailsId;

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "citizen_id")
    private Long citizenId;

    @Column(name = "issue_date")
    private Long issueDate;

    @Column(name = "applied_date")
    private Long appliedDate;

    @Column(name = "cancellation_date")
    private Long cancellationDate;

    @Column(name = "status")
    private Boolean status;

    public Long getNocDetailsId() {
        return nocDetailsId;
    }

    public void setNocDetailsId(Long nocDetailsId) {
        this.nocDetailsId = nocDetailsId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public Long getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Long cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Long appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

}

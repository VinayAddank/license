package org.rta.citizen.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SuspendedRCNumberModel extends BaseModel {

    private static final long serialVersionUID = 507627797481913444L;

    private Long suspendedRcNumbersId;

    private String prNumber;

    private Boolean isRevoked;

    private Long startDate;

    private Long endDate;

    /**
     * used to identify status : suspended/objection
     */
    private Integer status;

    private String comment;
    
	private String reason;

	private String raisedBy;
	
    public Long getSuspendedRcNumbersId() {
        return suspendedRcNumbersId;
    }

    public void setSuspendedRcNumbersId(Long suspendedRcNumbersId) {
        this.suspendedRcNumbersId = suspendedRcNumbersId;
    }

    public String getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }

    public Boolean getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(Boolean isRevoked) {
        this.isRevoked = isRevoked;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

}

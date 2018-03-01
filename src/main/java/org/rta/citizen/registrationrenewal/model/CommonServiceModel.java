package org.rta.citizen.registrationrenewal.model;

import java.util.List;

import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.model.payment.TaxModel;

/**
 * @Author sohan.maurya created on Jan 10, 2017.
 */
public class CommonServiceModel extends RegistrationCategoryModel {

	private String prNumber;
	private Boolean status;
	private Long approvedDate;
	private ServiceType serviceType;
	private String comment;
	private Status suspensionType;
	private Integer suspensionTime;
	private Long startTime;
	private Long endTime;
	private String reason;
	private TaxModel taxModel;
	private String raisedBy;
	private List<UserActionModel> actionModelList;
	
	public String getPrNumber() {
		return prNumber;
	}
	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Long getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(Long approvedDate) {
		this.approvedDate = approvedDate;
	}
	public ServiceType getServiceType() {
		return serviceType;
	}
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Status getSuspensionType() {
		return suspensionType;
	}
	public void setSuspensionType(Status suspensionType) {
		this.suspensionType = suspensionType;
	}
	public Integer getSuspensionTime() {
		return suspensionTime;
	}
	public void setSuspensionTime(Integer suspensionTime) {
		this.suspensionTime = suspensionTime;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public TaxModel getTaxModel() {
		return taxModel;
	}
	public void setTaxModel(TaxModel taxModel) {
		this.taxModel = taxModel;
	}
    public String getRaisedBy() {
        return raisedBy;
    }
    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }
	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}
	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}
}

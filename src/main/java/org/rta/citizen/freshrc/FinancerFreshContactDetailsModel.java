package org.rta.citizen.freshrc;

public class FinancerFreshContactDetailsModel {

	private Long id;
	private Long customerUserId;
	private Long vehicleRcId;
	private String mobileNumber;
	private String email;
	private Boolean isUpdated;
	private String createdBy;
	private Long createdOn;
	private String modifiedBy;
	private Long modifiedOn;
	private Long financierId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerUserId() {
		return customerUserId;
	}

	public void setCustomerUserId(Long customerUserId) {
		this.customerUserId = customerUserId;
	}

	public Long getVehicleRcId() {
		return vehicleRcId;
	}

	public void setVehicleRcId(Long vehicleRcId) {
		this.vehicleRcId = vehicleRcId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getIsUpdated() {
		return isUpdated;
	}

	public void setIsUpdated(Boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

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

	public Long getFinancierId() {
		return financierId;
	}

	public void setFinancierId(Long financierId) {
		this.financierId = financierId;
	}
	
}

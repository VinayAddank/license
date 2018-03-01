package org.rta.citizen.licence.model.updated;

import java.util.List;

import org.rta.citizen.licence.model.ClassofVechicleModel;

public class CovDetailsModel {

	private String aadharNumber;
	private String llrNumber;
	private String applicationNumber;
	private List<ClassofVechicleModel> covDetails;
	private Boolean isBadge;
	private Boolean isPaymentCompleted;
	private Boolean isBadgeAllowed;

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getLlrNumber() {
		return llrNumber;
	}

	public void setLlrNumber(String llrNumber) {
		this.llrNumber = llrNumber;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public List<ClassofVechicleModel> getCovDetails() {
		return covDetails;
	}

	public void setCovDetails(List<ClassofVechicleModel> covDetails) {
		this.covDetails = covDetails;
	}

	public Boolean getIsBadge() {
		return isBadge;
	}

	public void setIsBadge(Boolean isBadge) {
		this.isBadge = isBadge;
	}

	public Boolean getIsPaymentCompleted() {
		return isPaymentCompleted;
	}

	public void setIsPaymentCompleted(Boolean isPaymentCompleted) {
		this.isPaymentCompleted = isPaymentCompleted;
	}

	public Boolean getIsBadgeAllowed() {
		return isBadgeAllowed;
	}

	public void setIsBadgeAllowed(Boolean isBadgeAllowed) {
		this.isBadgeAllowed = isBadgeAllowed;
	}

}

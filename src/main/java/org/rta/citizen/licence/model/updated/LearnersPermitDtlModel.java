package org.rta.citizen.licence.model.updated;

import java.util.Date;

import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.model.RTAOfficeModel;

public class LearnersPermitDtlModel {

	private Long licenceHolderId;
	private String llrVehicleClassCode;
	private String transportType;
	private String applicationId;
	private String applicationOrigination;
	private Date dateOfFirstIssue;
	private Date llrIssuedt;
	private String llrNo;
	private String llrNoType;
	private String parentConsentAadhaarNo;
	private Long photoAttachmentId;
	private String referenceId;
	private String retestFlag;
	private Long signAttachmentId;
	private String statusCode;
	private Date statusDate;
	private String statusRemarks;
	private String statusUpdatedBy;
	private Date testDate;
	private char testExempted;
	private String testExemptedReason;
	private String testId;
	private String testResult;
	private String ticketDetails;
	private Date validFrom;
	private Date validTo;
	private String medicalFitnessType;
	private String medicalPractionerCode;
	private RTAOfficeModel rtaOfficeDetails;
	private Long aoUserId;
	private UserModel aoUserDetails;
	private UserModel mviUserDetails;
	private String covName;

	public String getCovName() {
		return covName;
	}

	public void setCovName(String covName) {
		this.covName = covName;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationOrigination() {
		return applicationOrigination;
	}

	public void setApplicationOrigination(String applicationOrigination) {
		this.applicationOrigination = applicationOrigination;
	}

	public Date getDateOfFirstIssue() {
		return dateOfFirstIssue;
	}

	public void setDateOfFirstIssue(Date dateOfFirstIssue) {
		this.dateOfFirstIssue = dateOfFirstIssue;
	}

	public Date getLlrIssuedt() {
		return llrIssuedt;
	}

	public void setLlrIssuedt(Date llrIssuedt) {
		this.llrIssuedt = llrIssuedt;
	}

	public String getLlrNo() {
		return llrNo;
	}

	public void setLlrNo(String llrNo) {
		this.llrNo = llrNo;
	}

	public String getLlrNoType() {
		return llrNoType;
	}

	public void setLlrNoType(String llrNoType) {
		this.llrNoType = llrNoType;
	}

	public String getParentConsentAadhaarNo() {
		return parentConsentAadhaarNo;
	}

	public void setParentConsentAadhaarNo(String parentConsentAadhaarNo) {
		this.parentConsentAadhaarNo = parentConsentAadhaarNo;
	}

	public Long getPhotoAttachmentId() {
		return photoAttachmentId;
	}

	public void setPhotoAttachmentId(Long photoAttachmentId) {
		this.photoAttachmentId = photoAttachmentId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getRetestFlag() {
		return retestFlag;
	}

	public void setRetestFlag(String retestFlag) {
		this.retestFlag = retestFlag;
	}

	public RTAOfficeModel getRtaOfficeDetails() {
		return rtaOfficeDetails;
	}

	public void setRtaOfficeDetails(RTAOfficeModel rtaOfficeDetails) {
		this.rtaOfficeDetails = rtaOfficeDetails;
	}

	public Long getSignAttachmentId() {
		return signAttachmentId;
	}

	public void setSignAttachmentId(Long signAttachmentId) {
		this.signAttachmentId = signAttachmentId;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getStatusRemarks() {
		return statusRemarks;
	}

	public void setStatusRemarks(String statusRemarks) {
		this.statusRemarks = statusRemarks;
	}

	public String getStatusUpdatedBy() {
		return statusUpdatedBy;
	}

	public void setStatusUpdatedBy(String statusUpdatedBy) {
		this.statusUpdatedBy = statusUpdatedBy;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public char getTestExempted() {
		return testExempted;
	}

	public void setTestExempted(char testExempted) {
		this.testExempted = testExempted;
	}

	public String getTestExemptedReason() {
		return testExemptedReason;
	}

	public void setTestExemptedReason(String testExemptedReason) {
		this.testExemptedReason = testExemptedReason;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}

	public String getTicketDetails() {
		return ticketDetails;
	}

	public void setTicketDetails(String ticketDetails) {
		this.ticketDetails = ticketDetails;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public String getMedicalFitnessType() {
		return medicalFitnessType;
	}

	public void setMedicalFitnessType(String medicalFitnessType) {
		this.medicalFitnessType = medicalFitnessType;
	}

	public String getMedicalPractionerCode() {
		return medicalPractionerCode;
	}

	public void setMedicalPractionerCode(String medicalPractionerCode) {
		this.medicalPractionerCode = medicalPractionerCode;
	}

	public String getLlrVehicleClassCode() {
		return llrVehicleClassCode;
	}

	public void setLlrVehicleClassCode(String llrVehicleClassCode) {
		this.llrVehicleClassCode = llrVehicleClassCode;
	}

	public UserModel getAoUserDetails() {
		return aoUserDetails;
	}

	public void setAoUserDetails(UserModel aoUserDetails) {
		this.aoUserDetails = aoUserDetails;
	}

	public UserModel getMviUserDetails() {
		return mviUserDetails;
	}

	public void setMviUserDetails(UserModel mviUserDetails) {
		this.mviUserDetails = mviUserDetails;
	}

	public Long getAoUserId() {
		return aoUserId;
	}

	public void setAoUserId(Long aoUserId) {
		this.aoUserId = aoUserId;
	}

	public Long getLicenceHolderId() {
		return licenceHolderId;
	}

	public void setLicenceHolderId(Long licenceHolderId) {
		this.licenceHolderId = licenceHolderId;
	}

}

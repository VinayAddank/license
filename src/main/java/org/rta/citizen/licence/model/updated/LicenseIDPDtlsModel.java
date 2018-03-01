package org.rta.citizen.licence.model.updated;

import java.util.Date;

import org.rta.citizen.common.model.RTAOfficeModel;

public class LicenseIDPDtlsModel {

	private String applicationNo;
	private String approvedBy;
	private String idpLicenseNo;
	private String idpVehicleClassCode;
	private Date issueDate;
	private String passportNo;
	private Date passportValidTo;
	private Long photoAttachmentId;
	private RTAOfficeModel rtaOfficeDetails;
	private Integer status;
	private String statusRemarks;
	private String ticketDetails;
	private Date validFrom;
	private Date validTo;

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getIdpLicenseNo() {
		return idpLicenseNo;
	}

	public void setIdpLicenseNo(String idpLicenseNo) {
		this.idpLicenseNo = idpLicenseNo;
	}

	public String getIdpVehicleClassCode() {
		return idpVehicleClassCode;
	}

	public void setIdpVehicleClassCode(String idpVehicleClassCode) {
		this.idpVehicleClassCode = idpVehicleClassCode;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public Date getPassportValidTo() {
		return passportValidTo;
	}

	public void setPassportValidTo(Date passportValidTo) {
		this.passportValidTo = passportValidTo;
	}

	public Long getPhotoAttachmentId() {
		return photoAttachmentId;
	}

	public void setPhotoAttachmentId(Long photoAttachmentId) {
		this.photoAttachmentId = photoAttachmentId;
	}

	public RTAOfficeModel getRtaOfficeDetails() {
		return rtaOfficeDetails;
	}

	public void setRtaOfficeDetails(RTAOfficeModel rtaOfficeDetails) {
		this.rtaOfficeDetails = rtaOfficeDetails;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusRemarks() {
		return statusRemarks;
	}

	public void setStatusRemarks(String statusRemarks) {
		this.statusRemarks = statusRemarks;
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
	
}
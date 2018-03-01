package org.rta.citizen.licence.model.updated;

import java.util.Date;

public class LicensePermitDetailsModel {

	private Long permitDetailsId;
	private Long applicationId;
	private String licenseNumber;
	private String licenseType;
	private String vehicleClassCode;
	private Date testDate;
	private char testExempted;
	private String testExemptedReason;
	private Integer testNoOfAttemp;
	private String testResult;
	private String testMarks;

	public Long getPermitDetailsId() {
		return permitDetailsId;
	}

	public void setPermitDetailsId(Long permitDetailsId) {
		this.permitDetailsId = permitDetailsId;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
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

	public Integer getTestNoOfAttemp() {
		return testNoOfAttemp;
	}

	public void setTestNoOfAttemp(Integer testNoOfAttemp) {
		this.testNoOfAttemp = testNoOfAttemp;
	}

	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}

	public String getTestMarks() {
		return testMarks;
	}

	public void setTestMarks(String testMarks) {
		this.testMarks = testMarks;
	}

	public String getVehicleClassCode() {
		return vehicleClassCode;
	}

	public void setVehicleClassCode(String vehicleClassCode) {
		this.vehicleClassCode = vehicleClassCode;
	}

}

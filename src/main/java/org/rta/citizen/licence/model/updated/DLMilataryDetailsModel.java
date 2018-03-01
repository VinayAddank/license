package org.rta.citizen.licence.model.updated;

import java.util.Date;

public class DLMilataryDetailsModel extends LLRegistrationModel {

	private static final long serialVersionUID = 1L;

	private Date dlValidityFrom;
	private Date dlValidityTo;
	private String rankunit;
	private String dlIssueAuthority;
	private String medicalType;
	private Long certificateIssueDate;
	private String doctorName;
	private String registrationNumber;

	public Date getDlValidityFrom() {
		return dlValidityFrom;
	}

	public void setDlValidityFrom(Date dlValidityFrom) {
		this.dlValidityFrom = dlValidityFrom;
	}

	public Date getDlValidityTo() {
		return dlValidityTo;
	}

	public void setDlValidityTo(Date dlValidityTo) {
		this.dlValidityTo = dlValidityTo;
	}

	public String getRankunit() {
		return rankunit;
	}

	public void setRankunit(String rankunit) {
		this.rankunit = rankunit;
	}

	public String getDlIssueAuthority() {
		return dlIssueAuthority;
	}

	public void setDlIssueAuthority(String dlIssueAuthority) {
		this.dlIssueAuthority = dlIssueAuthority;
	}

	public String getMedicalType() {
		return medicalType;
	}

	public void setMedicalType(String medicalType) {
		this.medicalType = medicalType;
	}

	public Long getCertificateIssueDate() {
		return certificateIssueDate;
	}

	public void setCertificateIssueDate(Long certificateIssueDate) {
		this.certificateIssueDate = certificateIssueDate;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

}

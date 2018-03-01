package org.rta.citizen.licence.model.updated;

public class MedicalDetailsModel {

	private String medicalType;
	private Long certificateIssueDate;
	private String doctorName;
	private String registrationNumber;

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

package org.rta.citizen.licence.model.updated;

public class CourseCertificateModel {

	private Long issueCertificateDate;
	private Long trainingFrom;
	private Long trainingTo;
	private String instituteName;
	private String cetificateNumber;

	public Long getIssueCertificateDate() {
		return issueCertificateDate;
	}

	public void setIssueCertificateDate(Long issueCertificateDate) {
		this.issueCertificateDate = issueCertificateDate;
	}

	public Long getTrainingFrom() {
		return trainingFrom;
	}

	public void setTrainingFrom(Long trainingFrom) {
		this.trainingFrom = trainingFrom;
	}

	public Long getTrainingTo() {
		return trainingTo;
	}

	public void setTrainingTo(Long trainingTo) {
		this.trainingTo = trainingTo;
	}

	public String getInstituteName() {
		return instituteName;
	}

	public void setInstituteName(String instituteName) {
		this.instituteName = instituteName;
	}

	public String getCetificateNumber() {
		return cetificateNumber;
	}

	public void setCetificateNumber(String cetificateNumber) {
		this.cetificateNumber = cetificateNumber;
	}

}

package org.rta.citizen.licence.model.updated;

public class DLForiegnCitizenDetailsModel extends LLRegistrationModel {

	private static final long serialVersionUID = 1L;

	private String passportNumber;
	private String visaValidityFrom;
	private String visaValidityTo;
	private String foreignFrom;
	private String foreignTo;
	private String passportFrom;
	private String passportTo;

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getVisaValidityFrom() {
		return visaValidityFrom;
	}

	public void setVisaValidityFrom(String visaValidityFrom) {
		this.visaValidityFrom = visaValidityFrom;
	}

	public String getVisaValidityTo() {
		return visaValidityTo;
	}

	public void setVisaValidityTo(String visaValidityTo) {
		this.visaValidityTo = visaValidityTo;
	}

	public String getForeignFrom() {
		return foreignFrom;
	}

	public void setForeignFrom(String foreignFrom) {
		this.foreignFrom = foreignFrom;
	}

	public String getForeignTo() {
		return foreignTo;
	}

	public void setForeignTo(String foreignTo) {
		this.foreignTo = foreignTo;
	}

	public String getPassportFrom() {
		return passportFrom;
	}

	public void setPassportFrom(String passportFrom) {
		this.passportFrom = passportFrom;
	}

	public String getPassportTo() {
		return passportTo;
	}

	public void setPassportTo(String passportTo) {
		this.passportTo = passportTo;
	}

}
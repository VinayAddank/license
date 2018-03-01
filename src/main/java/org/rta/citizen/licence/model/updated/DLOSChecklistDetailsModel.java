package org.rta.citizen.licence.model.updated;

public class DLOSChecklistDetailsModel extends LLRegistrationModel {

	private static final long serialVersionUID = 1L;

	private String dlNumber;
	private String dlValidityTo;
	private String dlValidityFrom;

	public String getDlNumber() {
		return dlNumber;
	}

	public void setDlNumber(String dlNumber) {
		this.dlNumber = dlNumber;
	}

	public String getDlValidityTo() {
		return dlValidityTo;
	}

	public void setDlValidityTo(String dlValidityTo) {
		this.dlValidityTo = dlValidityTo;
	}

	public String getDlValidityFrom() {
		return dlValidityFrom;
	}

	public void setDlValidityFrom(String dlValidityFrom) {
		this.dlValidityFrom = dlValidityFrom;
	}

}

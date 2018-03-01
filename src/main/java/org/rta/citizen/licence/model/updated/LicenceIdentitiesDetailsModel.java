package org.rta.citizen.licence.model.updated;

import java.util.Date;

import org.rta.citizen.common.model.AddressModel;
import org.rta.citizen.common.model.RTAOfficeModel;

public class LicenceIdentitiesDetailsModel {

	private String passportNumber;
	private Date passportValidFrom;
	private Date passportValidTo;
	private Date visaValidFrom;
	private Date visaValidTo;
	private String dlNumber;
	private Date dlValidFrom;
	private Date dlValidTo;
	private AddressModel rtaOfficeAddressDetails;
	private RTAOfficeModel rtaOfficeDetails;
	private String rtaOfficeAddressCode;

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public Date getPassportValidFrom() {
		return passportValidFrom;
	}

	public void setPassportValidFrom(Date passportValidFrom) {
		this.passportValidFrom = passportValidFrom;
	}

	public Date getPassportValidTo() {
		return passportValidTo;
	}

	public void setPassportValidTo(Date passportValidTo) {
		this.passportValidTo = passportValidTo;
	}

	public Date getVisaValidFrom() {
		return visaValidFrom;
	}

	public void setVisaValidFrom(Date visaValidFrom) {
		this.visaValidFrom = visaValidFrom;
	}

	public Date getVisaValidTo() {
		return visaValidTo;
	}

	public void setVisaValidTo(Date visaValidTo) {
		this.visaValidTo = visaValidTo;
	}

	public String getDlNumber() {
		return dlNumber;
	}

	public void setDlNumber(String dlNumber) {
		this.dlNumber = dlNumber;
	}

	public Date getDlValidFrom() {
		return dlValidFrom;
	}

	public void setDlValidFrom(Date dlValidFrom) {
		this.dlValidFrom = dlValidFrom;
	}

	public Date getDlValidTo() {
		return dlValidTo;
	}

	public void setDlValidTo(Date dlValidTo) {
		this.dlValidTo = dlValidTo;
	}

	public AddressModel getRtaOfficeAddressDetails() {
		return rtaOfficeAddressDetails;
	}

	public void setRtaOfficeAddressDetails(AddressModel rtaOfficeAddressDetails) {
		this.rtaOfficeAddressDetails = rtaOfficeAddressDetails;
	}

	public RTAOfficeModel getRtaOfficeDetails() {
		return rtaOfficeDetails;
	}

	public void setRtaOfficeDetails(RTAOfficeModel rtaOfficeDetails) {
		this.rtaOfficeDetails = rtaOfficeDetails;
	}

	public String getRtaOfficeAddressCode() {
		return rtaOfficeAddressCode;
	}

	public void setRtaOfficeAddressCode(String rtaOfficeAddressCode) {
		this.rtaOfficeAddressCode = rtaOfficeAddressCode;
	}
}

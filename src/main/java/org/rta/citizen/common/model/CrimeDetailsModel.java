package org.rta.citizen.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CrimeDetailsModel {
	private String aadharNumbers;
	private String driverAddress;
	private String driverFault;
	private String driverName;
	private String firNumber;
	private String licenseNumber;
	private String causeOfAccident;
	private String dateofAccident;
	private String districtId;
	private String fIRNumber;
	private String hourID;
	private String minuteId;
	private String nearestVillage;
	private String noofLicenses;
	private String noofPersonsKilled;
	private String noofVehiclesInvolved;
	private String noofpersonsInjured;
	private String placeofAccident;
	private String policeStationCode;
	private String typeofRoad;
	private String underSectionId;
	private String firRegNumber;

	public String getFirNumber() {
		return firNumber;
	}

	public void setFirNumber(String firNumber) {
		this.firNumber = firNumber;
	}

	public String getAadharNumbers() {
		return aadharNumbers;
	}

	public void setAadharNumbers(String aadharNumbers) {
		this.aadharNumbers = aadharNumbers;
	}

	public String getDriverAddress() {
		return driverAddress;
	}

	public void setDriverAddress(String driverAddress) {
		this.driverAddress = driverAddress;
	}

	public String getDriverFault() {
		return driverFault;
	}

	public void setDriverFault(String driverFault) {
		this.driverFault = driverFault;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getCauseOfAccident() {
		return causeOfAccident;
	}

	public void setCauseOfAccident(String causeOfAccident) {
		this.causeOfAccident = causeOfAccident;
	}

	public String getDateofAccident() {
		return dateofAccident;
	}

	public void setDateofAccident(String dateofAccident) {
		this.dateofAccident = dateofAccident;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getfIRNumber() {
		return fIRNumber;
	}

	public void setfIRNumber(String fIRNumber) {
		this.fIRNumber = fIRNumber;
	}

	public String getHourID() {
		return hourID;
	}

	public void setHourID(String hourID) {
		this.hourID = hourID;
	}

	public String getMinuteId() {
		return minuteId;
	}

	public void setMinuteId(String minuteId) {
		this.minuteId = minuteId;
	}

	public String getNearestVillage() {
		return nearestVillage;
	}

	public void setNearestVillage(String nearestVillage) {
		this.nearestVillage = nearestVillage;
	}

	public String getNoofLicenses() {
		return noofLicenses;
	}

	public void setNoofLicenses(String noofLicenses) {
		this.noofLicenses = noofLicenses;
	}

	public String getNoofPersonsKilled() {
		return noofPersonsKilled;
	}

	public void setNoofPersonsKilled(String noofPersonsKilled) {
		this.noofPersonsKilled = noofPersonsKilled;
	}

	public String getNoofVehiclesInvolved() {
		return noofVehiclesInvolved;
	}

	public void setNoofVehiclesInvolved(String noofVehiclesInvolved) {
		this.noofVehiclesInvolved = noofVehiclesInvolved;
	}

	public String getNoofpersonsInjured() {
		return noofpersonsInjured;
	}

	public void setNoofpersonsInjured(String noofpersonsInjured) {
		this.noofpersonsInjured = noofpersonsInjured;
	}

	public String getPlaceofAccident() {
		return placeofAccident;
	}

	public void setPlaceofAccident(String placeofAccident) {
		this.placeofAccident = placeofAccident;
	}

	public String getPoliceStationCode() {
		return policeStationCode;
	}

	public void setPoliceStationCode(String policeStationCode) {
		this.policeStationCode = policeStationCode;
	}

	public String getTypeofRoad() {
		return typeofRoad;
	}

	public void setTypeofRoad(String typeofRoad) {
		this.typeofRoad = typeofRoad;
	}

	public String getUnderSectionId() {
		return underSectionId;
	}

	public void setUnderSectionId(String underSectionId) {
		this.underSectionId = underSectionId;
	}

	public String getFirRegNumber() {
		return firRegNumber;
	}

	public void setFirRegNumber(String firRegNumber) {
		this.firRegNumber = firRegNumber;
	}

}

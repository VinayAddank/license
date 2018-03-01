/**
 * 
 */
package org.rta.citizen.permit.model;

import org.rta.citizen.common.model.payment.FeeModel;

/**
 * @author arun.verma
 *
 */
public class PermitAuthorizationCardModel {

	private String rtaOfficeName;
	private String PermitNo;
	private String nameOfHolder;
	private String careOff;
	private String fullAddress;
	private String prNumber;
	private String vehicleClassDesc;
	private String makersName;
	private String manufactureYear;
	private String dateOfRegAsNew;
	private String bodyType;
	private String maxPassangerCapacity;
	private String chassisNo;
	private String engineNo;
	private String permitArea;
	private String validitiPeriod;
	private String fareRate;
	private String place;
	private String date;
	private String authority;
	private String renewedUpto;
	private Long validFrom;
	private Long validTo;
	private FeeModel feeModel;
	private Long ulw;
	private Long rlw;
	private Long permitExpiryDate;
	private String authorizationNumber;
	private String rtoSignFilePath;
	private Long authExpiryDate;

	public String getAuthorizationNumber() {
		return authorizationNumber;
	}

	public void setAuthorizationNumber(String authorizationNumber) {
		this.authorizationNumber = authorizationNumber;
	}

	public Long getUlw() {
		return ulw;
	}

	public void setUlw(Long ulw) {
		this.ulw = ulw;
	}

	public Long getRlw() {
		return rlw;
	}

	public void setRlw(Long rlw) {
		this.rlw = rlw;
	}

	public Long getPermitExpiryDate() {
		return permitExpiryDate;
	}

	public void setPermitExpiryDate(Long permitExpiryDate) {
		this.permitExpiryDate = permitExpiryDate;
	}

	public FeeModel getFeeModel() {
		return feeModel;
	}

	public void setFeeModel(FeeModel feeModel) {
		this.feeModel = feeModel;
	}

	public String getRtaOfficeName() {
		return rtaOfficeName;
	}

	public void setRtaOfficeName(String rtaOfficeName) {
		this.rtaOfficeName = rtaOfficeName;
	}

	public String getPermitNo() {
		return PermitNo;
	}

	public void setPermitNo(String permitNo) {
		PermitNo = permitNo;
	}

	public String getNameOfHolder() {
		return nameOfHolder;
	}

	public void setNameOfHolder(String nameOfHolder) {
		this.nameOfHolder = nameOfHolder;
	}

	public String getCareOff() {
		return careOff;
	}

	public void setCareOff(String careOff) {
		this.careOff = careOff;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getPrNumber() {
		return prNumber;
	}

	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}

	public String getVehicleClassDesc() {
		return vehicleClassDesc;
	}

	public void setVehicleClassDesc(String vehicleClassDesc) {
		this.vehicleClassDesc = vehicleClassDesc;
	}

	public String getMakersName() {
		return makersName;
	}

	public void setMakersName(String makersName) {
		this.makersName = makersName;
	}

	public String getManufactureYear() {
		return manufactureYear;
	}

	public void setManufactureYear(String manufactureYear) {
		this.manufactureYear = manufactureYear;
	}

	public String getDateOfRegAsNew() {
		return dateOfRegAsNew;
	}

	public void setDateOfRegAsNew(String dateOfRegAsNew) {
		this.dateOfRegAsNew = dateOfRegAsNew;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getMaxPassangerCapacity() {
		return maxPassangerCapacity;
	}

	public void setMaxPassangerCapacity(String maxPassangerCapacity) {
		this.maxPassangerCapacity = maxPassangerCapacity;
	}

	public String getChassisNo() {
		return chassisNo;
	}

	public void setChassisNo(String chassisNo) {
		this.chassisNo = chassisNo;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getPermitArea() {
		return permitArea;
	}

	public void setPermitArea(String permitArea) {
		this.permitArea = permitArea;
	}

	public String getValiditiPeriod() {
		return validitiPeriod;
	}

	public void setValiditiPeriod(String validitiPeriod) {
		this.validitiPeriod = validitiPeriod;
	}

	public String getFareRate() {
		return fareRate;
	}

	public void setFareRate(String fareRate) {
		this.fareRate = fareRate;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getRenewedUpto() {
		return renewedUpto;
	}

	public void setRenewedUpto(String renewedUpto) {
		this.renewedUpto = renewedUpto;
	}

	public Long getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Long validFrom) {
		this.validFrom = validFrom;
	}

	public Long getValidTo() {
		return validTo;
	}

	public void setValidTo(Long validTo) {
		this.validTo = validTo;
	}

	public String getRtoSignFilePath() {
		return rtoSignFilePath;
	}

	public void setRtoSignFilePath(String rtoSignFilePath) {
		this.rtoSignFilePath = rtoSignFilePath;
	}

	public Long getAuthExpiryDate() {
		return authExpiryDate;
	}

	public void setAuthExpiryDate(Long authExpiryDate) {
		this.authExpiryDate = authExpiryDate;
	}

}

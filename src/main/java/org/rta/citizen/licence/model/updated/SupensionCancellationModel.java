package org.rta.citizen.licence.model.updated;

import java.util.Date;

public class SupensionCancellationModel {

	private String suspensionType;
	private String reason;
	private String comment;
	private String vehicleRc;
	private String sectionName;
	private String vehicleReason;
	private String cov;
	private String vcrNo;
	private String mviName;
	private Date fromDate;
	private Date toDate;
	private Date vcrDate;

	public String getSuspensionType() {
		return suspensionType;
	}

	public void setSuspensionType(String suspensionType) {
		this.suspensionType = suspensionType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getVehicleRc() {
		return vehicleRc;
	}

	public void setVehicleRc(String vehicleRc) {
		this.vehicleRc = vehicleRc;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getVehicleReason() {
		return vehicleReason;
	}

	public void setVehicleReason(String vehicleReason) {
		this.vehicleReason = vehicleReason;
	}

	public String getCov() {
		return cov;
	}

	public void setCov(String cov) {
		this.cov = cov;
	}

	public String getVcrNo() {
		return vcrNo;
	}

	public void setVcrNo(String vcrNo) {
		this.vcrNo = vcrNo;
	}

	public String getMviName() {
		return mviName;
	}

	public void setMviName(String mviName) {
		this.mviName = mviName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getVcrDate() {
		return vcrDate;
	}

	public void setVcrDate(Date vcrDate) {
		this.vcrDate = vcrDate;
	}

}

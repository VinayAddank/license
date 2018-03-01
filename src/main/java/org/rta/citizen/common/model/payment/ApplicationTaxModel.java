package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.PermitHeaderModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class ApplicationTaxModel {

	private double taxAmt ;
	private long taxValidUpto ;
	private double invoiceAmt ;
	private long invoiceDate ;
	private Integer regType ;
	private long vehicleRcId ;
	private String vehicleSubClass;
	private long gvw ;
	private long prIssueTime ;
	private long prExpiryTime ;
	private long trIssueTime ;
	private Integer taxType;
	private String taxTypeName;
	private String citizenName ;
	private String vehicleClass;
	private int seatingCapacity;
	private long pucExpireDate;
	private long insuranceEndDate ;
	private String makerName;
	private String chassisNo;
	private String engineNo;
	private String colour;
	private String contactNo;
	private String emailId;
	private FcDetailsModel fitnessDetailsModel;
	private boolean pucRequired;
	private PermitHeaderModel permitHeaderModel;
	private String fuelType;
	private String ownerType;
	private long ulw ;
	private long greenTaxValidTo;
	private long permitAuthValidTo;
	private RegFeeDetailModel regFeeDetailModel;
	private long cessFeeValidUpTo;
	private String oldVehicleSubClass;
	private Integer periodicTaxType;
	private Integer oldTaxType;
	private String vehicleSubClassDesc;
	
	public double getTaxAmt() {
		return taxAmt;
	}
	public void setTaxAmt(double taxAmt) {
		this.taxAmt = taxAmt;
	}
	public long getTaxValidUpto() {
		return taxValidUpto;
	}
	public void setTaxValidUpto(long taxValidUpto) {
		this.taxValidUpto = taxValidUpto;
	}
	public double getInvoiceAmt() {
		return invoiceAmt;
	}
	public void setInvoiceAmt(double invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}
	public long getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(long invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public Integer getRegType() {
		return regType;
	}
	public void setRegType(Integer regType) {
		this.regType = regType;
	}
	public long getVehicleRcId() {
		return vehicleRcId;
	}
	public void setVehicleRcId(long vehicleRcId) {
		this.vehicleRcId = vehicleRcId;
	}
	public String getVehicleSubClass() {
		return vehicleSubClass;
	}
	public void setVehicleSubClass(String vehicleSubClass) {
		this.vehicleSubClass = vehicleSubClass;
	}
	public long getGvw() {
		return gvw;
	}
	public void setGvw(long gvw) {
		this.gvw = gvw;
	}
	public long getPrIssueTime() {
		return prIssueTime;
	}
	public void setPrIssueTime(long prIssueTime) {
		this.prIssueTime = prIssueTime;
	}
	public long getTrIssueTime() {
		return trIssueTime;
	}
	public void setTrIssueTime(long trIssueTime) {
		this.trIssueTime = trIssueTime;
	}
	public Integer getTaxType() {
		return taxType;
	}
	public void setTaxType(Integer taxType) {
		this.taxType = taxType;
	}
	public String getCitizenName() {
		return citizenName;
	}
	public void setCitizenName(String citizenName) {
		this.citizenName = citizenName;
	}
	public String getVehicleClass() {
		return vehicleClass;
	}
	public void setVehicleClass(String vehicleClass) {
		this.vehicleClass = vehicleClass;
	}
	public int getSeatingCapacity() {
		return seatingCapacity;
	}
	public void setSeatingCapacity(int seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}
	public long getPucExpireDate() {
		return pucExpireDate;
	}
	public void setPucExpireDate(long pucExpireDate) {
		this.pucExpireDate = pucExpireDate;
	}
	public long getInsuranceEndDate() {
		return insuranceEndDate;
	}
	public void setInsuranceEndDate(long insuranceEndDate) {
		this.insuranceEndDate = insuranceEndDate;
	}
	public String getMakerName() {
		return makerName;
	}
	public void setMakerName(String makerName) {
		this.makerName = makerName;
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
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public long getPrExpiryTime() {
		return prExpiryTime;
	}
	public void setPrExpiryTime(long prExpiryTime) {
		this.prExpiryTime = prExpiryTime;
	}
	public String getTaxTypeName() {
		return taxTypeName;
	}
	public void setTaxTypeName(String taxTypeName) {
		this.taxTypeName = taxTypeName;
	}
	public FcDetailsModel getFitnessDetailsModel() {
		return fitnessDetailsModel;
	}
	public void setFitnessDetailsModel(FcDetailsModel fitnessDetailsModel) {
		this.fitnessDetailsModel = fitnessDetailsModel;
	}
    public boolean isPucRequired() {
        return pucRequired;
    }
    public void setPucRequired(boolean pucRequired) {
        this.pucRequired = pucRequired;
    }
	public PermitHeaderModel getPermitHeaderModel() {
		return permitHeaderModel;
	}
	public void setPermitHeaderModel(PermitHeaderModel permitHeaderModel) {
		this.permitHeaderModel = permitHeaderModel;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public String getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
	public long getUlw() {
		return ulw;
	}
	public void setUlw(long ulw) {
		this.ulw = ulw;
	}
	public long getGreenTaxValidTo() {
		return greenTaxValidTo;
	}
	public void setGreenTaxValidTo(long greenTaxValidTo) {
		this.greenTaxValidTo = greenTaxValidTo;
	}
	public long getPermitAuthValidTo() {
		return permitAuthValidTo;
	}
	public void setPermitAuthValidTo(long permitAuthValidTo) {
		this.permitAuthValidTo = permitAuthValidTo;
	}
	public RegFeeDetailModel getRegFeeDetailModel() {
		return regFeeDetailModel;
	}
	public void setRegFeeDetailModel(RegFeeDetailModel regFeeDetailModel) {
		this.regFeeDetailModel = regFeeDetailModel;
	}
	public long getCessFeeValidUpTo() {
		return cessFeeValidUpTo;
	}
	public void setCessFeeValidUpTo(long cessFeeValidUpTo) {
		this.cessFeeValidUpTo = cessFeeValidUpTo;
	}
	public String getOldVehicleSubClass() {
		return oldVehicleSubClass;
	}
	public void setOldVehicleSubClass(String oldVehicleSubClass) {
		this.oldVehicleSubClass = oldVehicleSubClass;
	}
	public Integer getPeriodicTaxType() {
		return periodicTaxType;
	}
	public void setPeriodicTaxType(Integer periodicTaxType) {
		this.periodicTaxType = periodicTaxType;
	}
	public Integer getOldTaxType() {
		return oldTaxType;
	}
	public void setOldTaxType(Integer oldTaxType) {
		this.oldTaxType = oldTaxType;
	}
	
	public String getVehicleSubClassDesc() {
		return vehicleSubClassDesc;
	}
	public void setVehicleSubClassDesc(String vehicleSubClassDesc) {
		this.vehicleSubClassDesc = vehicleSubClassDesc;
	}
	
}

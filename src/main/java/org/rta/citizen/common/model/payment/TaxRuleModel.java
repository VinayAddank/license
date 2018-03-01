package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaxRuleModel {
    
    private int regCategory;
    private String taxType;
    private Double tax;
    private String ownerType;
    private Boolean isPHCertificate;
    private Boolean isPHDriverLicense;
    private Boolean isTwoWheeler;
    private Boolean isFourWheeler;
    private String vehicleClass;
    private int invoiceAmount;
    private Boolean isSecondVehicle;
    private String fuelType;
    private int taxAmount;
    private int monthType;
    private String vehicleClassCategory;
    private int seatingCapacity;
    private long gvw;
    private long ulw;
    private int hsrpAmount;
    private Boolean isDisabled;
    private Boolean isInvalidCarriage;
    private String permitType;
    private String serviceCode;
    private Boolean iSuzo;
    private boolean greenTax;
    private int greenTaxAmt;
    private Double quarterAmt;
    private boolean isPermitValid; 
	private long prIssueDate;
	private int cessFee;
	private boolean isCessFeeValid;	
	private double oldTaxAmt ;
	private double penalty;
	private double taxAmtArrears;
	private double penaltyArrears;
	private long taxValidUpto;
	private int quarterlyTaxType;
	private double serviceFee;
	private String stateCode;
	private int vehicleAge;
	private String permitSubType;
	private String oldTaxType;
	private long permitValidTo;	
	private boolean vcrFlag;
	private String pliedAs;
	private long vcrBookedDt;
	private String vehicleSiezed;
	private Integer periodicTaxType;
	private long trIssueDate;
	
    public int getRegCategory() {
		return regCategory;
	}
	public void setRegCategory(int regCategory) {
		this.regCategory = regCategory;
	}
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
	}
	public String getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
	public Boolean getIsPHCertificate() {
		return isPHCertificate;
	}
	public void setIsPHCertificate(Boolean isPHCertificate) {
		this.isPHCertificate = isPHCertificate;
	}
	public Boolean getIsPHDriverLicense() {
		return isPHDriverLicense;
	}
	public void setIsPHDriverLicense(Boolean isPHDriverLicense) {
		this.isPHDriverLicense = isPHDriverLicense;
	}
	public Boolean getIsTwoWheeler() {
		return isTwoWheeler;
	}
	public void setIsTwoWheeler(Boolean isTwoWheeler) {
		this.isTwoWheeler = isTwoWheeler;
	}
	public Boolean getIsFourWheeler() {
		return isFourWheeler;
	}
	public void setIsFourWheeler(Boolean isFourWheeler) {
		this.isFourWheeler = isFourWheeler;
	}
	public String getVehicleClass() {
		return vehicleClass;
	}
	public void setVehicleClass(String vehicleClass) {
		this.vehicleClass = vehicleClass;
	}
	public int getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(int invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public Boolean getIsSecondVehicle() {
		return isSecondVehicle;
	}
	public void setIsSecondVehicle(Boolean isSecondVehicle) {
		this.isSecondVehicle = isSecondVehicle;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public int getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(int taxAmount) {
		this.taxAmount = taxAmount;
	}
	public int getMonthType() {
		return monthType;
	}
	public void setMonthType(int monthType) {
		this.monthType = monthType;
	}
	public String getVehicleClassCategory() {
		return vehicleClassCategory;
	}
	public void setVehicleClassCategory(String vehicleClassCategory) {
		this.vehicleClassCategory = vehicleClassCategory;
	}
	public int getSeatingCapacity() {
		return seatingCapacity;
	}
	public void setSeatingCapacity(int seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}
	public long getGvw() {
		return gvw;
	}
	public void setGvw(long gvw) {
		this.gvw = gvw;
	}
	public long getUlw() {
		return ulw;
	}
	public void setUlw(long ulw) {
		this.ulw = ulw;
	}
	public int getHsrpAmount() {
		return hsrpAmount;
	}
	public void setHsrpAmount(int hsrpAmount) {
		this.hsrpAmount = hsrpAmount;
	}
	public Boolean getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
	public Boolean getIsInvalidCarriage() {
		return isInvalidCarriage;
	}
	public void setIsInvalidCarriage(Boolean isInvalidCarriage) {
		this.isInvalidCarriage = isInvalidCarriage;
	}
	public String getPermitType() {
		return permitType;
	}
	public void setPermitType(String permitType) {
		this.permitType = permitType;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public Boolean getISuzo() {
		return iSuzo;
	}
	public void setISuzo(Boolean iSuzo) {
		this.iSuzo = iSuzo;
	}
	public boolean getGreenTax() {
		return greenTax;
	}
	public void setGreenTax(boolean greenTax) {
		this.greenTax = greenTax;
	}
	public int getGreenTaxAmt() {
		return greenTaxAmt;
	}
	public void setGreenTaxAmt(int greenTaxAmt) {
		this.greenTaxAmt = greenTaxAmt;
	}
	public Double getQuarterAmt() {
		return quarterAmt;
	}
	public void setQuarterAmt(Double quarterAmt) {
		this.quarterAmt = quarterAmt;
	}
	
	public boolean getIsPermitValid() {
		return isPermitValid;
	}
	public void setIsPermitValid(boolean isPermitValid) {
		this.isPermitValid = isPermitValid;
	}
	public long getPrIssueDate() {
		return prIssueDate;
	}
	public void setPrIssueDate(long prIssueDate) {
		this.prIssueDate = prIssueDate;
	}
	public int getCessFee() {
		return cessFee;
	}
	public void setCessFee(int cessFee) {
		this.cessFee = cessFee;
	}
	public boolean getIsCessFeeValid() {
		return isCessFeeValid;
	}
	public void setIsCessFeeValid(boolean isCessFeeValid) {
		this.isCessFeeValid = isCessFeeValid;
	}
	public double getOldTaxAmt() {
		return oldTaxAmt;
	}
	public void setOldTaxAmt(double oldTaxAmt) {
		this.oldTaxAmt = oldTaxAmt;
	}
	public double getPenalty() {
		return penalty;
	}
	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}
	public double getTaxAmtArrears() {
		return taxAmtArrears;
	}
	public void setTaxAmtArrears(double taxAmtArrears) {
		this.taxAmtArrears = taxAmtArrears;
	}
	public double getPenaltyArrears() {
		return penaltyArrears;
	}
	public void setPenaltyArrears(double penaltyArrears) {
		this.penaltyArrears = penaltyArrears;
	}
	public long getTaxValidUpto() {
		return taxValidUpto;
	}
	public void setTaxValidUpto(long taxValidUpto) {
		this.taxValidUpto = taxValidUpto;
	}
	public int getQuarterlyTaxType() {
		return quarterlyTaxType;
	}
	public void setQuarterlyTaxType(int quarterlyTaxType) {
		this.quarterlyTaxType = quarterlyTaxType;
	}
	public double getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(double serviceFee) {
		this.serviceFee = serviceFee;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public int getVehicleAge() {
		return vehicleAge;
	}
	public void setVehicleAge(int vehicleAge) {
		this.vehicleAge = vehicleAge;
	}
	public String getPermitSubType() {
		return permitSubType;
	}
	public void setPermitSubType(String permitSubType) {
		this.permitSubType = permitSubType;
	}
	public String getOldTaxType() {
		return oldTaxType;
	}
	public void setOldTaxType(String oldTaxType) {
		this.oldTaxType = oldTaxType;
	}
	public long getPermitValidTo() {
		return permitValidTo;
	}
	public void setPermitValidTo(long permitValidTo) {
		this.permitValidTo = permitValidTo;
	}
	public boolean getVcrFlag() {
		return vcrFlag;
	}
	public void setVcrFlag(boolean vcrFlag) {
		this.vcrFlag = vcrFlag;
	}
	public String getPliedAs() {
		return pliedAs;
	}
	public void setPliedAs(String pliedAs) {
		this.pliedAs = pliedAs;
	}
	public long getVcrBookedDt() {
		return vcrBookedDt;
	}
	public void setVcrBookedDt(long vcrBookedDt) {
		this.vcrBookedDt = vcrBookedDt;
	}
	public String getVehicleSiezed() {
		return vehicleSiezed;
	}
	public void setVehicleSiezed(String vehicleSiezed) {
		this.vehicleSiezed = vehicleSiezed;
	}
	public Integer getPeriodicTaxType() {
		return periodicTaxType;
	}
	public void setPeriodicTaxType(Integer periodicTaxType) {
		this.periodicTaxType = periodicTaxType;
	}
	public long getTrIssueDate() {
		return trIssueDate;
	}
	public void setTrIssueDate(long trIssueDate) {
		this.trIssueDate = trIssueDate;
	}


}

package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FeeRuleModel {

	private int regCategory;
	private String vehicleClassCategory;
	private double trFee;
	private double trService;
	private double hpaFee;
	private double cessFee;
	private double totalTrFee;
	private double prFee;
	private double prService;
	private double postalFee;
	private double cardFee;
	private double totalPrFee;
	private double fitnessFee;
	private double fitnessService;
	private double totalFitnessFee;
	private String permitType;
	private double permitFee;
	private double permitService;
	private double totalPermitFee;
	private double otherPermitFee;
	private long gvw;
	private int ownerType;
	private boolean isFinance;
	private int seatingCapacity;
	private double totalFee;
	private String serviceCode;
	private String licenseCategory;
	private String classOfVehicleLicense;
	private double licenseAppFee;
	private double licenseServiceCharge;
	private double licenseTestFee;
	private double licenseCOV;
	private boolean gracePeriod;
	private double penalty;
	private int penaltyYear;
	private String endorseType;
	private boolean isCalculate;
	private boolean oldVehicle;
	private int monthCount;
	private int daysCount;
	private int expiryMonthCount;
	private boolean isBadge;
	private String permitClass;
	private int specialNoFlag;
	private double specialNumberFee;
	private double lateFee ;
	private double hsrpAmount;
	private String vehicleClass;
	private String permitTransferNdSurrender;
	private boolean fitnessExpireFlag;
	private boolean altrationFlag;
	private boolean reassinmentFlag;
	private boolean cardFeeFlag;
	private String oldClassOfVehicle;
	private boolean hsrpFlag;
	private int oldRegCategory;
	
	public int getRegCategory() {
		return regCategory;
	}

	public void setRegCategory(int regCategory) {
		this.regCategory = regCategory;
	}

	public String getVehicleClassCategory() {
		return vehicleClassCategory;
	}

	public void setVehicleClassCategory(String vehicleClassCategory) {
		this.vehicleClassCategory = vehicleClassCategory;
	}

	public double getTrFee() {
		return trFee;
	}

	public void setTrFee(double trFee) {
		this.trFee = trFee;
	}

	public double getTrService() {
		return trService;
	}

	public void setTrService(double trService) {
		this.trService = trService;
	}

	public double getHpaFee() {
		return hpaFee;
	}

	public void setHpaFee(double hpaFee) {
		this.hpaFee = hpaFee;
	}

	public double getCessFee() {
		return cessFee;
	}

	public void setCessFee(double cessFee) {
		this.cessFee = cessFee;
	}

	public double getTotalTrFee() {
		return totalTrFee;
	}

	public void setTotalTrFee(double totalTrFee) {
		this.totalTrFee = totalTrFee;
	}

	public double getPrFee() {
		return prFee;
	}

	public void setPrFee(double prFee) {
		this.prFee = prFee;
	}

	public double getPrService() {
		return prService;
	}

	public void setPrService(double prService) {
		this.prService = prService;
	}

	public double getPostalFee() {
		return postalFee;
	}

	public void setPostalFee(double postalFee) {
		this.postalFee = postalFee;
	}

	public double getCardFee() {
		return cardFee;
	}

	public void setCardFee(double cardFee) {
		this.cardFee = cardFee;
	}

	public double getTotalPrFee() {
		return totalPrFee;
	}

	public void setTotalPrFee(double totalPrFee) {
		this.totalPrFee = totalPrFee;
	}

	public double getFitnessFee() {
		return fitnessFee;
	}

	public void setFitnessFee(double fitnessFee) {
		this.fitnessFee = fitnessFee;
	}

	public double getFitnessService() {
		return fitnessService;
	}

	public void setFitnessService(double fitnessService) {
		this.fitnessService = fitnessService;
	}

	public double getTotalFitnessFee() {
		return totalFitnessFee;
	}

	public void setTotalFitnessFee(double totalFitnessFee) {
		this.totalFitnessFee = totalFitnessFee;
	}

	public String getPermitType() {
		return permitType;
	}

	public void setPermitType(String permitType) {
		this.permitType = permitType;
	}

	public double getPermitFee() {
		return permitFee;
	}

	public void setPermitFee(double permitFee) {
		this.permitFee = permitFee;
	}

	public double getPermitService() {
		return permitService;
	}

	public void setPermitService(double permitService) {
		this.permitService = permitService;
	}

	public double getTotalPermitFee() {
		return totalPermitFee;
	}

	public void setTotalPermitFee(double totalPermitFee) {
		this.totalPermitFee = totalPermitFee;
	}

	public double getOtherPermitFee() {
		return otherPermitFee;
	}

	public void setOtherPermitFee(double otherPermitFee) {
		this.otherPermitFee = otherPermitFee;
	}

	public long getGvw() {
		return gvw;
	}

	public void setGvw(long gvw) {
		this.gvw = gvw;
	}

	public int getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(int ownerType) {
		this.ownerType = ownerType;
	}

	public boolean isFinance() {
		return isFinance;
	}

	public void setFinance(boolean isFinance) {
		this.isFinance = isFinance;
	}

	public int getSeatingCapacity() {
		return seatingCapacity;
	}

	public void setSeatingCapacity(int seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}

	public double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getLicenseCategory() {
		return licenseCategory;
	}

	public void setLicenseCategory(String licenseCategory) {
		this.licenseCategory = licenseCategory;
	}

	public String getClassOfVehicleLicense() {
		return classOfVehicleLicense;
	}

	public void setClassOfVehicleLicense(String classOfVehicleLicense) {
		this.classOfVehicleLicense = classOfVehicleLicense;
	}

	public double getLicenseAppFee() {
		return licenseAppFee;
	}

	public void setLicenseAppFee(double licenseAppFee) {
		this.licenseAppFee = licenseAppFee;
	}

	public double getLicenseServiceCharge() {
		return licenseServiceCharge;
	}

	public void setLicenseServiceCharge(double licenseServiceCharge) {
		this.licenseServiceCharge = licenseServiceCharge;
	}

	public double getLicenseTestFee() {
		return licenseTestFee;
	}

	public void setLicenseTestFee(double licenseTestFee) {
		this.licenseTestFee = licenseTestFee;
	}

	public double getLicenseCOV() {
		return licenseCOV;
	}

	public void setLicenseCOV(double licenseCOV) {
		this.licenseCOV = licenseCOV;
	}

	public boolean isGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(boolean gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public double getPenalty() {
		return penalty;
	}

	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

	public int getPenaltyYear() {
		return penaltyYear;
	}

	public void setPenaltyYear(int penaltyYear) {
		this.penaltyYear = penaltyYear;
	}

	public String getEndorseType() {
		return endorseType;
	}

	public void setEndorseType(String endorseType) {
		this.endorseType = endorseType;
	}

	public boolean isCalculate() {
		return isCalculate;
	}

	public void setCalculate(boolean isCalculate) {
		this.isCalculate = isCalculate;
	}

	public boolean getOldVehicle() {
		return oldVehicle;
	}

	public void setOldVehicle(boolean oldVehicle) {
		this.oldVehicle = oldVehicle;
	}

	public int getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(int monthCount) {
		this.monthCount = monthCount;
	}

	public int getDaysCount() {
		return daysCount;
	}

	public void setDaysCount(int daysCount) {
		this.daysCount = daysCount;
	}

	public int getExpiryMonthCount() {
		return expiryMonthCount;
	}

	public void setExpiryMonthCount(int expiryMonthCount) {
		this.expiryMonthCount = expiryMonthCount;
	}

	public boolean isBadge() {
		return isBadge;
	}

	public void setBadge(boolean isBadge) {
		this.isBadge = isBadge;
	}

	public String getPermitClass() {
		return permitClass;
	}

	public void setPermitClass(String permitClass) {
		this.permitClass = permitClass;
	}

	public int getSpecialNoFlag() {
		return specialNoFlag;
	}

	public void setSpecialNoFlag(int specialNoFlag) {
		this.specialNoFlag = specialNoFlag;
	}

	public double getSpecialNumberFee() {
		return specialNumberFee;
	}

	public void setSpecialNumberFee(double specialNumberFee) {
		this.specialNumberFee = specialNumberFee;
	}

	public double getLateFee() {
		return lateFee;
	}

	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}

	public double getHsrpAmount() {
		return hsrpAmount;
	}

	public void setHsrpAmount(double hsrpAmount) {
		this.hsrpAmount = hsrpAmount;
	}

	public String getVehicleClass() {
		return vehicleClass;
	}

	public void setVehicleClass(String vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public String getPermitTransferNdSurrender() {
		return permitTransferNdSurrender;
	}

	public void setPermitTransferNdSurrender(String permitTransferNdSurrender) {
		this.permitTransferNdSurrender = permitTransferNdSurrender;
	}

	public boolean getFitnessExpireFlag() {
		return fitnessExpireFlag;
	}

	public void setFitnessExpireFlag(boolean fitnessExpireFlag) {
		this.fitnessExpireFlag = fitnessExpireFlag;
	}

	public boolean getAltrationFlag() {
		return altrationFlag;
	}

	public void setAltrationFlag(boolean altrationFlag) {
		this.altrationFlag = altrationFlag;
	}

	public boolean getReassinmentFlag() {
		return reassinmentFlag;
	}

	public void setReassinmentFlag(boolean reassinmentFlag) {
		this.reassinmentFlag = reassinmentFlag;
	}

	public boolean getCardFeeFlag() {
		return cardFeeFlag;
	}

	public void setCardFeeFlag(boolean cardFeeFlag) {
		this.cardFeeFlag = cardFeeFlag;
	}

	public String getOldClassOfVehicle() {
		return oldClassOfVehicle;
	}

	public void setOldClassOfVehicle(String oldClassOfVehicle) {
		this.oldClassOfVehicle = oldClassOfVehicle;
	}

	public boolean getHsrpFlag() {
		return hsrpFlag;
	}

	public void setHsrpFlag(boolean hsrpFlag) {
		this.hsrpFlag = hsrpFlag;
	}

	public int getOldRegCategory() {
		return oldRegCategory;
	}

	public void setOldRegCategory(int oldRegCategory) {
		this.oldRegCategory = oldRegCategory;
	}

		
		
}

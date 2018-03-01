package org.rta.citizen.freshrc;

public class FreshRcModel{
	private Long vehicleRcId;
	private Boolean vehicleUnderPossession;
	private Boolean ownerConsent;
	private FinanceYardModel yard;
	private Long amountDue;
	private Long overDueSince;
	private Integer currentStep;
	private Long financerId;
	private String ownerComment;
	private Long defaultedAmount;
    private Long noOfEmi;
    private String createdBy;
    private Long createdOn;
    private Long modifiedOn;
    private String showCauseIssuedBy;
    private Long showCauseDate;
    private String applicationNumber;
    private Long ownerConscentDate;
    private String showCauseDoc;
    private String prNumber;

	public Long getVehicleRcId() {
		return vehicleRcId;
	}

	public void setVehicleRcId(Long vehicleRcId) {
		this.vehicleRcId = vehicleRcId;
	}

	public Boolean getVehicleUnderPossession() {
		return vehicleUnderPossession;
	}

	public void setVehicleUnderPossession(Boolean vehicleUnderPossession) {
		this.vehicleUnderPossession = vehicleUnderPossession;
	}

	public Boolean getOwnerConsent() {
		return ownerConsent;
	}

	public void setOwnerConsent(Boolean ownerConsent) {
		this.ownerConsent = ownerConsent;
	}

	public Long getFinancerId() {
		return financerId;
	}

	public void setFinancerId(Long financerId) {
		this.financerId = financerId;
	}

	public FinanceYardModel getYard() {
		return yard;
	}

	public void setYard(FinanceYardModel yard) {
		this.yard = yard;
	}

	public Long getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(Long amountDue) {
		this.amountDue = amountDue;
	}

	public Long getOverDueSince() {
		return overDueSince;
	}

	public void setOverDueSince(Long overDueSince) {
		this.overDueSince = overDueSince;
	}

	public Integer getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(Integer currentStep) {
		this.currentStep = currentStep;
	}

	public String getOwnerComment() {
		return ownerComment;
	}

	public void setOwnerComment(String ownerComment) {
		this.ownerComment = ownerComment;
	}

	public Long getDefaultedAmount() {
		return defaultedAmount;
	}

	public void setDefaultedAmount(Long defaultedAmount) {
		this.defaultedAmount = defaultedAmount;
	}

	public Long getNoOfEmi() {
		return noOfEmi;
	}

	public void setNoOfEmi(Long noOfEmi) {
		this.noOfEmi = noOfEmi;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getShowCauseIssuedBy() {
		return showCauseIssuedBy;
	}

	public void setShowCauseIssuedBy(String showCauseIssuedBy) {
		this.showCauseIssuedBy = showCauseIssuedBy;
	}

	public Long getShowCauseDate() {
		return showCauseDate;
	}

	public void setShowCauseDate(Long showCauseDate) {
		this.showCauseDate = showCauseDate;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public Long getOwnerConscentDate() {
		return ownerConscentDate;
	}

	public void setOwnerConscentDate(Long ownerConscentDate) {
		this.ownerConscentDate = ownerConscentDate;
	}

	public String getShowCauseDoc() {
		return showCauseDoc;
	}

	public void setShowCauseDoc(String showCauseDoc) {
		this.showCauseDoc = showCauseDoc;
	}

	public String getPrNumber() {
		return prNumber;
	}

	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}

	public Long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}

	public Long getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
}

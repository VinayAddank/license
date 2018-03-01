package org.rta.citizen.common.model;

import org.rta.citizen.common.enums.Status;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class FinanceModel {


    private String vehicleRcId;
    private String name;
    private Integer financerMode; // online or offline
    private Long financerId; // sbi or any other
    private Integer financeApprovalStatus;
    private String appStatus;
    private String city;
    private Integer mandal;
    private String district;

    private String state;
    @JsonIgnore
    private Long userId; // from financermaster
    @JsonIgnore
    private String userNm;
    private Boolean isFinance;
    private String mandalName;
    private String DistrcitName;
    private String stateName;

    private Long dateOfAgreement;
    private Double amount;
    private Integer tenure;
    private float emi;
    private Long endDt;
    private Float intrestRate;
    private Integer paymentMode;
    private Long chequeNo;
    private String approverName;
    private Boolean isAppliedHPA;
    private boolean financeTerminated;
    private String agreementLetterurl;
    private Status rtaApproved;
    
    private String financerContactNumber;
    private String financerOfficialEmailId;
    private String doorNo;
    private String street;

    public Status getRtaApproved() {
        return rtaApproved;
    }

    public void setRtaApproved(Status rtaApproved) {
        this.rtaApproved = rtaApproved;
    }

    public String getAgreementLetterurl() {
        return agreementLetterurl;
    }

    public void setAgreementLetterurl(String agreementLetterurl) {
        this.agreementLetterurl = agreementLetterurl;
    }

    public boolean isFinanceTerminated() {
        return financeTerminated;
    }

    public void setFinanceTerminated(boolean financeTerminated) {
        this.financeTerminated = financeTerminated;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public Float getIntrestRate() {
        return intrestRate;
    }

    public void setIntrestRate(Float intrestRate) {
        this.intrestRate = intrestRate;
    }

    public Integer getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Integer paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Long getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(Long chequeNo) {
        this.chequeNo = chequeNo;
    }

    public float getEmi() {
        return emi;
    }

    public void setEmi(float emi) {
        this.emi = emi;
    }

    public Long getEndDt() {
        return endDt;
    }

    public void setEndDt(Long endDt) {
        this.endDt = endDt;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public String getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(String vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDateOfAgreement() {
        return dateOfAgreement;
    }

    public void setDateOfAgreement(Long dateOfAgreement) {
        this.dateOfAgreement = dateOfAgreement;
    }

    public Integer getFinancerMode() {
        return financerMode;
    }

    public void setFinancerMode(Integer financerMode) {
        this.financerMode = financerMode;
    }

    public Long getFinancerId() {
        return financerId;
    }

    public void setFinancerId(Long financerId) {
        this.financerId = financerId;
    }

    public Integer getFinanceApprovalStatus() {
        return financeApprovalStatus;
    }

    public void setFinanceApprovalStatus(Integer financeApprovalStatus) {
        this.financeApprovalStatus = financeApprovalStatus;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getMandal() {
        return mandal;
    }

    public void setMandal(Integer mandal) {
        this.mandal = mandal;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public Boolean getIsFinance() {
        return isFinance;
    }

    public void setIsFinance(Boolean isFinance) {
        this.isFinance = isFinance;
    }

    public String getMandalName() {
        return mandalName;
    }

    public void setMandalName(String mandalName) {
        this.mandalName = mandalName;
    }

    public String getDistrcitName() {
        return DistrcitName;
    }

    public void setDistrcitName(String distrcitName) {
        DistrcitName = distrcitName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public Boolean getIsAppliedHPA() {
        return isAppliedHPA;
    }

    public void setIsAppliedHPA(Boolean isAppliedHPA) {
        this.isAppliedHPA = isAppliedHPA;
    }

    public String getFinancerContactNumber() {
        return financerContactNumber;
    }

    public void setFinancerContactNumber(String financerContactNumber) {
        this.financerContactNumber = financerContactNumber;
    }

    public String getFinancerOfficialEmailId() {
        return financerOfficialEmailId;
    }

    public void setFinancerOfficialEmailId(String financerOfficialEmailId) {
        this.financerOfficialEmailId = financerOfficialEmailId;
    }

    public String getDoorNo() {
        return doorNo;
    }

    public void setDoorNo(String doorNo) {
        this.doorNo = doorNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}

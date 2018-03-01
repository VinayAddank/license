package org.rta.citizen.common.model.payment;

import java.util.HashMap;
import java.util.Optional;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class TransactionDetailModel {

	private String transactionNo;
	private String vehicleRcId;
	private Double amount ;
	private String encryptRequest;
	private String decryptRequest;
	private String status;
	private String message;
	private HashMap<String, String> sbiResponseMap;
	private Long transactionId;
	private double FeeAmt;
	private double serviceCharge;
	private double postalCharge;
	private double taxAmt;
	private double hsrpAmt;
    private double permitAmt;
    private int sbiVerifyStatus;
    private Integer payType;
    private String sbiRefNo;
    private String createdBy;
    private String paymentDate;
    private String serviceCode;
    private int regType;
    private Boolean newApplicantFlag;
    private String remiterName;
    private String districtCode ;
    private String appNo;
    private double greenTaxAmt;
    private double compoundAmount;
    private double cessFee;
    private String pgType;
    private String firstName;
 	private String email;
 	private String phoneNumber;
 	private String productInfo;
 	private String key;
 	private String hash;
 	private PayUTransactionDetails payUTransactionDetails;
 	private boolean payURespopnseStatus;
    
    public String getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}
	
	public String getEncryptRequest() {
		return encryptRequest;
	}
	public void setEncryptRequest(String encryptRequest) {
		this.encryptRequest = encryptRequest;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public HashMap<String, String> getSbiResponseMap() {
		return sbiResponseMap;
	}
	public void setSbiResponseMap(HashMap<String, String> sbiResponseMap) {
		this.sbiResponseMap = sbiResponseMap;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public double getFeeAmt() {
		return FeeAmt;
	}
	public void setFeeAmt(double feeAmt) {
		FeeAmt = feeAmt;
	}
	public double getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	public double getPostalCharge() {
		return postalCharge;
	}
	public void setPostalCharge(double postalCharge) {
		this.postalCharge = postalCharge;
	}
	public double getTaxAmt() {
		return taxAmt;
	}
	public void setTaxAmt(double taxAmt) {
		this.taxAmt = taxAmt;
	}
	
	public double getPermitAmt() {
		return permitAmt;
	}
	public void setPermitAmt(double permitAmt) {
		this.permitAmt = permitAmt;
	}
	public int getSbiVerifyStatus() {
		return sbiVerifyStatus;
	}
	public void setSbiVerifyStatus(int sbiVerifyStatus) {
		this.sbiVerifyStatus = sbiVerifyStatus;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public String getSbiRefNo() {
		return sbiRefNo;
	}
	public void setSbiRefNo(String sbiRefNo) {
		this.sbiRefNo = sbiRefNo;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getVehicleRcId() {
		return vehicleRcId;
	}
	public void setVehicleRcId(String vehicleRcId) {
		this.vehicleRcId = vehicleRcId;
	}
	public String getDecryptRequest() {
		return decryptRequest;
	}
	public void setDecryptRequest(String decryptRequest) {
		this.decryptRequest = decryptRequest;
	}
	public double getHsrpAmt() {
		return hsrpAmt;
	}
	public void setHsrpAmt(double hsrpAmt) {
		this.hsrpAmt = hsrpAmt;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	
	public String getRemiterName() {
		return remiterName;
	}
	public void setRemiterName(String remiterName) {
		this.remiterName = remiterName;
	}
	public String getDistrictCode() {
		return districtCode;
	}
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}
	
	public Boolean getNewApplicantFlag() {
		return newApplicantFlag;
	}
	public void setNewApplicantFlag(Boolean newApplicantFlag) {
		this.newApplicantFlag = newApplicantFlag;
	}
	public String getAppNo() {
		return appNo;
	}
	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}
	public int getRegType() {
		return regType;
	}
	public void setRegType(int regType) {
		this.regType = regType;
	}
	public double getGreenTaxAmt() {
		return greenTaxAmt;
	}
	public void setGreenTaxAmt(double greenTaxAmt) {
		this.greenTaxAmt = greenTaxAmt;
	}
    public double getCompoundAmount() {
        return compoundAmount;
    }
    public void setCompoundAmount(double compoundAmount) {
        this.compoundAmount = compoundAmount;
    }
	public double getCessFee() {
		return cessFee;
	}
	public void setCessFee(double cessFee) {
		this.cessFee = cessFee;
	}
	public String getPgType() {
		return pgType;
	}
	public void setPgType(String pgType) {
		this.pgType = pgType;
	}
	public PayUTransactionDetails getPayUTransactionDetails() {
		return payUTransactionDetails;
	}
	public void setPayUTransactionDetails(PayUTransactionDetails payUTransactionDetails) {
		this.payUTransactionDetails = payUTransactionDetails;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getProductInfo() {
		return productInfo;
	}
	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public boolean getPayURespopnseStatus() {
		return payURespopnseStatus;
	}
	public void setPayURespopnseStatus(boolean payURespopnseStatus) {
		this.payURespopnseStatus = payURespopnseStatus;
	}	
}

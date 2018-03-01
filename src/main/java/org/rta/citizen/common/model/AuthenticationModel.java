package org.rta.citizen.common.model;

import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.OwnershipTransferType;
import org.rta.citizen.common.enums.UserType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AuthenticationModel extends AadhaarTCSDetailsRequestModel {

    private static final long serialVersionUID = 5858479637955487914L;
    
    private String prNumber;
    private String tokenNumber;
    private String trNumber;
    private UserType userType;
    private OwnershipTransferType transferType;
	private String llrNumber;
	private String applicationId;
	private String applicationNumber;
	private String aadhaarNumber;
	private String dlNumber;
	private String clientIp;
	private String passportNo;
	private String dob;
    private String ownershipTypeCode;
    private KeyType keyType;
    private String chassisNumber;
    
	public String getPrNumber() {
		return prNumber;
	}
	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}
	public String getTokenNumber() {
		return tokenNumber;
	}
	public void setTokenNumber(String tokenNumber) {
		this.tokenNumber = tokenNumber;
	}
	public String getTrNumber() {
		return trNumber;
	}
	public void setTrNumber(String trNumber) {
		this.trNumber = trNumber;
	}
	public UserType getUserType() {
		return userType;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public OwnershipTransferType getTransferType() {
		return transferType;
	}
	public void setTransferType(OwnershipTransferType transferType) {
		this.transferType = transferType;
	}
	public String getLlrNumber() {
		return llrNumber;
	}
	public void setLlrNumber(String llrNumber) {
		this.llrNumber = llrNumber;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getApplicationNumber() {
		return applicationNumber;
	}
	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}
	public String getAadhaarNumber() {
		return aadhaarNumber;
	}
	public void setAadhaarNumber(String aadhaarNumber) {
		this.aadhaarNumber = aadhaarNumber;
	}
	public String getDlNumber() {
		return dlNumber;
	}
	public void setDlNumber(String dlNumber) {
		this.dlNumber = dlNumber;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getPassportNo() {
		return passportNo;
	}
	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getOwnershipTypeCode() {
		return ownershipTypeCode;
	}
	public void setOwnershipTypeCode(String ownershipTypeCode) {
		this.ownershipTypeCode = ownershipTypeCode;
	}
	public KeyType getKeyType() {
		return keyType;
	}
	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
    }
    public String getChassisNumber() {
        return chassisNumber;
    }
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
	}
	
}

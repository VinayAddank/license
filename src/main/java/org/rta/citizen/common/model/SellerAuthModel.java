package org.rta.citizen.common.model;

public class SellerAuthModel extends AadhaarTCSDetailsRequestModel {
	private String name;
    private String aadharNumber;
    private String prNumber;
    private String trNumber;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAadharNumber() {
		return aadharNumber;
	}
	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
	public String getPrNumber() {
		return prNumber;
	}
	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}
	public String getTrNumber() {
		return trNumber;
	}
	public void setTrNumber(String trNumber) {
		this.trNumber = trNumber;
	}
    
    
    
}

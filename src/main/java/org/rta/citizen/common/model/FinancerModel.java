package org.rta.citizen.common.model;

public class FinancerModel {
	private String financerName;
    private Long financerId;
    private AddressModel financierAddress;
    private String sanctionLetterUrl;
    
	public String getSanctionLetterUrl() {
		return sanctionLetterUrl;
	}
	public void setSanctionLetterUrl(String sanctionLetterUrl) {
		this.sanctionLetterUrl = sanctionLetterUrl;
	}
	public AddressModel getFinancierAddress() {
		return financierAddress;
	}
	public void setFinancierAddress(AddressModel financierAddress) {
		this.financierAddress = financierAddress;
	}
	public String getFinancerName() {
		return financerName;
	}
	public void setFinancerName(String financerName) {
		this.financerName = financerName;
	}

    public Long getFinancerId() {
		return financerId;
	}

    public void setFinancerId(Long financerId) {
		this.financerId = financerId;
	}
	

}

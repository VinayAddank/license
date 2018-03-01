package org.rta.citizen.licence.model.updated;

public class InternationalPermitModel {

	private String passportNumber;
	private String issuedBy;
	private String countryName;
	private String countryCode;
	private Long issusedDate;
	private Long expiryDate;
	private Integer stayPeriod;

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Long getIssusedDate() {
		return issusedDate;
	}

	public void setIssusedDate(Long issusedDate) {
		this.issusedDate = issusedDate;
	}

	public Long getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Long expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getStayPeriod() {
		return stayPeriod;
	}

	public void setStayPeriod(Integer stayPeriod) {
		this.stayPeriod = stayPeriod;
	}

}

package org.rta.citizen.common.model;

import org.rta.citizen.common.enums.AddressType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AddressModel extends BaseModel {

    private static final long serialVersionUID = -5029020753765272406L;
    private Long addressId;
    private AddressType type;
    private Boolean status;
    private String doorNo;
    private String street;
    private Long user;
    private Long postOffice;
    private Long mandal;
    private String city;
    private Long district;
    private Long state;
    private Long country;
    private String districtCode;
    private String stateCode;
    private String mandalName;
    private String districtName;
    private String stateName;
    private String countryName;
    private String countryCode;
    private Integer mandalCode;
    private Boolean isSameAadhar;
   
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(Long postOffice) {
        this.postOffice = postOffice;
    }

    public Long getMandal() {
        return mandal;
    }

    public void setMandal(Long mandal) {
        this.mandal = mandal;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getDistrict() {
        return district;
    }

    public void setDistrict(Long district) {
        this.district = district;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Long getCountry() {
        return country;
    }

    public String getMandalName() {
		return mandalName;
	}

	public void setMandalName(String mandalName) {
		this.mandalName = mandalName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public void setCountry(Long country) {
        this.country = country;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getMandalCode() {
        return mandalCode;
    }

    public void setMandalCode(Integer mandalCode) {
        this.mandalCode = mandalCode;
    }

    public Boolean getIsSameAadhar() {
        return isSameAadhar;
    }

    public void setIsSameAadhar(Boolean isSameAadhar) {
        this.isSameAadhar = isSameAadhar;
    }

}

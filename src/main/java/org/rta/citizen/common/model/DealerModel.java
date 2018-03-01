package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "dealer")
@JsonInclude(Include.NON_NULL)
public class DealerModel extends BaseModel {

	private static final long serialVersionUID = 102468090682735443L;

	private Long userId;
	private String showRoomName; // Dealer Showroom Name
	private String userName;
	private String phone;
	private String fax;
	private Long parentId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String userType;
	private String email;
	private String mobile;
	private Boolean status;
	private Long lastPwdReset;
	private Long lastLogin;

	private String doorNo;
	private String street;
	private String city;
	private String state;
	private Integer mandalId;
	private String mandalName;
	private String district;
	private String rtaOffice;
	private Boolean eligibleToPay;
	private String password;
	private String userSignature;
	private String stateCode;
	private String districtCode;
	private String pincode;
	private GeolocationModel location;
	
	@JsonIgnore
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return Dealer's Showroom name
	 */
	public String getShowRoomName() {
		return showRoomName;
	}

	/**
	 * @param name
	 *            Dealer's Showroom name
	 */
	public void setShowRoomName(String showRoomName) {
		this.showRoomName = showRoomName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Long getLastPwdReset() {
		return lastPwdReset;
	}

	public void setLastPwdReset(Long lastPwdReset) {
		this.lastPwdReset = lastPwdReset;
	}

	public Long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Long lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getRtaOffice() {
		return rtaOffice;
	}

	public void setRtaOffice(String rtaOffice) {
		this.rtaOffice = rtaOffice;
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

	public Integer getMandalId() {
		return mandalId;
	}

	public void setMandalId(Integer mandalId) {
		this.mandalId = mandalId;
	}

	public String getMandalName() {
		return mandalName;
	}

	public void setMandalName(String mandalName) {
		this.mandalName = mandalName;
	}

	public Boolean getEligibleToPay() {
		return eligibleToPay;
	}

	public void setEligibleToPay(Boolean eligibleToPay) {
		this.eligibleToPay = eligibleToPay;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserSignature() {
		return userSignature;
	}
	
	public void setUserSignature(String userSignature) {
		this.userSignature = userSignature;
	}
	
	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getPincode() {
		return pincode;
	}
	
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

    public GeolocationModel getLocation() {
        return location;
    }

    public void setLocation(GeolocationModel location) {
        this.location = location;
    }
	
}

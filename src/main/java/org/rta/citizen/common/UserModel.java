package org.rta.citizen.common;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.model.BaseModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "user")
@JsonInclude(Include.NON_NULL)
public class UserModel extends BaseModel {

    public static final String PARENT = "PARENT";
    public static final String CHILD = "CHILD";
    
    private static final long serialVersionUID = -8695616333458519171L;
    private Long userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String userName;
    private String password;
    private String emailId;
    private String mobile;
    private String phone;
    private String fax;
    private String doorNo;
    private String street;
    private String city;
    private String state;
    private Long mandalId;
    private String mandalName;
    private String district;
    private String rtaOffice;
    private String rtaOfficeCodeTo;
    private String rtaOfficeCodeFrom;
    private String rtaOfficeCode;
    private Boolean status;
    private List<String> roles;
    private String userType;
    // Dealer
    private String showRoomName;
    private Long parentId;
    private Boolean eligibleToPay;
    private String type;//parrent or child
    // Rta Employee
    private String departmentName;
    private String userSignature;
    private String aadharNumber;
    
	@JsonIgnore
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Long getMandalId() {
        return mandalId;
    }

    public void setMandalId(Long mandalId) {
        this.mandalId = mandalId;
    }

    public String getMandalName() {
        return mandalName;
    }

    public void setMandalName(String mandalName) {
        this.mandalName = mandalName;
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

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    public List<String> getUserRole() {
        return roles;
    }

    public void setUserRole(List<String> userRole) {
        this.roles = userRole;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public String getShowRoomName() {
        return showRoomName;
    }

    public void setShowRoomName(String showRoomName) {
        this.showRoomName = showRoomName;
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

    public Boolean getEligibleToPay() {
        return eligibleToPay;
    }

    public void setEligibleToPay(Boolean eligibleToPay) {
        this.eligibleToPay = eligibleToPay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public String getRtaOfficeCodeTo() {
		return rtaOfficeCodeTo;
	}
    
    public void setRtaOfficeCodeTo(String rtaOfficeCodeTo) {
		this.rtaOfficeCodeTo = rtaOfficeCodeTo;
	}

	public String getRtaOfficeCodeFrom() {
		return rtaOfficeCodeFrom;
	}

	public void setRtaOfficeCodeFrom(String rtaOfficeCodeFrom) {
		this.rtaOfficeCodeFrom = rtaOfficeCodeFrom;
	}    
	
	public String getUserSignature() {
		return userSignature;
	}
	public void setUserSignature(String userSignature) {
		this.userSignature = userSignature;
	}

    public String getRtaOfficeCode() {
        return rtaOfficeCode;
    }

    public void setRtaOfficeCode(String rtaOfficeCode) {
        this.rtaOfficeCode = rtaOfficeCode;
    }
    
    public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

}

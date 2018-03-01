package org.rta.citizen.aadharseeding.rc.model;

import java.util.List;

import org.rta.citizen.common.model.UserActionModel;

public class RCAadharSeedModel {

    private String aadharNumber;
    private String prNumber;
    private String lastName;
    private String mandalName;
    private Integer mandalCode;
    private String pinCode;
    private String mobileNumber;
    private String emailId;
    private List<UserActionModel> actionModelList;

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMandalName() {
        return mandalName;
    }

    public void setMandalName(String mandalName) {
        this.mandalName = mandalName;
    }

    public Integer getMandalCode() {
        return mandalCode;
    }

    public void setMandalCode(Integer mandalCode) {
        this.mandalCode = mandalCode;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}

	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}

}

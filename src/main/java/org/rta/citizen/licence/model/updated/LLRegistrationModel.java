package org.rta.citizen.licence.model.updated;

import org.rta.citizen.common.model.AddressModel;

public class LLRegistrationModel extends AddressModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String displayName;
	private String fullName;
	private String firstName;
	private String lastName;
	private String guardianName;
	private String bloodGroup;
	private String mobileNo;
	private String emailId;
	private String qualification;
	private Boolean selfDecalartion;
	private Integer qualificationCode;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public Boolean getSelfDecalartion() {
		return selfDecalartion;
	}

	public void setSelfDecalartion(Boolean selfDecalartion) {
		this.selfDecalartion = selfDecalartion;
	}

	public Integer getQualificationCode() {
		return qualificationCode;
	}

	public void setQualificationCode(Integer qualificationCode) {
		this.qualificationCode = qualificationCode;
	}

}

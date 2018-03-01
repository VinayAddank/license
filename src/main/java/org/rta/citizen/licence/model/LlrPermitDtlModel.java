package org.rta.citizen.licence.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class LlrPermitDtlModel {

	private long licence_holder_id;
	private int llr_sequence_id;
	private String llr_no;
	private String llr_no_type;
	private String llr_vehicle_class_code;
	private String application_id;
	private long photo_attachment_id;
	private long sign_attachment_id;
	private String parent_consent_aadhaar_no;
	private String test_id;
	private char test_exempted;
	private String test_exempted_reason;
	private String test_result;
	private Date test_date;
	private Date llr_issuedt;
	private Date valid_to;
	private Date valid_from;
	private Date date_of_first_issue;
	private String status_code;
	private String status_updated_by;
	private Date status_date;
	private String status_remarks;
	private long rta_office_code;
	private String application_origination;
	private String ticket_details;
	private String reference_id;
	private String approved_mvi;
	private String approved_ao;
	private String retest_flag;
	private String displayName;
	private String previousTestDate;
	private String bloodGroup;
	private String mobileNumber;
	private String email;
	private String qualification;
	private String doorNo;
	private String street;
	private String townCity;
	private String mandalName;
	private String district;
	private String state;
	private String country;
	private String pincode;
	private Long aadharNumber;

	public Long getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(Long aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPreviousTestDate() {
		return previousTestDate;
	}

	public void setPreviousTestDate(String previousTestDate) {
		this.previousTestDate = previousTestDate;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
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

	public String getTownCity() {
		return townCity;
	}

	public void setTownCity(String townCity) {
		this.townCity = townCity;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public long getLicence_holder_id() {
		return licence_holder_id;
	}

	public void setLicence_holder_id(long licence_holder_id) {
		this.licence_holder_id = licence_holder_id;
	}

	public int getLlr_sequence_id() {
		return llr_sequence_id;
	}

	public void setLlr_sequence_id(int llr_sequence_id) {
		this.llr_sequence_id = llr_sequence_id;
	}

	public String getLlr_no() {
		return llr_no;
	}

	public void setLlr_no(String llr_no) {
		this.llr_no = llr_no;
	}

	public String getLlr_no_type() {
		return llr_no_type;
	}

	public void setLlr_no_type(String llr_no_type) {
		this.llr_no_type = llr_no_type;
	}

	public String getLlr_vehicle_class_code() {
		return llr_vehicle_class_code;
	}

	public void setLlr_vehicle_class_code(String llr_vehicle_class_code) {
		this.llr_vehicle_class_code = llr_vehicle_class_code;
	}

	public String getApplication_id() {
		return application_id;
	}

	public void setApplication_id(String application_id) {
		this.application_id = application_id;
	}

	public long getPhoto_attachment_id() {
		return photo_attachment_id;
	}

	public void setPhoto_attachment_id(long photo_attachment_id) {
		this.photo_attachment_id = photo_attachment_id;
	}

	public long getSign_attachment_id() {
		return sign_attachment_id;
	}

	public void setSign_attachment_id(long sign_attachment_id) {
		this.sign_attachment_id = sign_attachment_id;
	}

	public String getParent_consent_aadhaar_no() {
		return parent_consent_aadhaar_no;
	}

	public void setParent_consent_aadhaar_no(String parent_consent_aadhaar_no) {
		this.parent_consent_aadhaar_no = parent_consent_aadhaar_no;
	}

	public String getTest_id() {
		return test_id;
	}

	public void setTest_id(String test_id) {
		this.test_id = test_id;
	}

	public char getTest_exempted() {
		return test_exempted;
	}

	public void setTest_exempted(char test_exempted) {
		this.test_exempted = test_exempted;
	}

	public String getTest_exempted_reason() {
		return test_exempted_reason;
	}

	public void setTest_exempted_reason(String test_exempted_reason) {
		this.test_exempted_reason = test_exempted_reason;
	}

	public String getTest_result() {
		return test_result;
	}

	public void setTest_result(String test_result) {
		this.test_result = test_result;
	}

	public Date getTest_date() {
		return test_date;
	}

	public void setTest_date(Date test_date) {
		this.test_date = test_date;
	}

	public Date getLlr_issuedt() {
		return llr_issuedt;
	}

	public void setLlr_issuedt(Date llr_issuedt) {
		this.llr_issuedt = llr_issuedt;
	}

	public Date getValid_to() {
		return valid_to;
	}

	public void setValid_to(Date valid_to) {
		this.valid_to = valid_to;
	}

	public Date getValid_from() {
		return valid_from;
	}

	public void setValid_from(Date valid_from) {
		this.valid_from = valid_from;
	}

	public Date getDate_of_first_issue() {
		return date_of_first_issue;
	}

	public void setDate_of_first_issue(Date date_of_first_issue) {
		this.date_of_first_issue = date_of_first_issue;
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getStatus_updated_by() {
		return status_updated_by;
	}

	public void setStatus_updated_by(String status_updated_by) {
		this.status_updated_by = status_updated_by;
	}

	public Date getStatus_date() {
		return status_date;
	}

	public void setStatus_date(Date status_date) {
		this.status_date = status_date;
	}

	public String getStatus_remarks() {
		return status_remarks;
	}

	public void setStatus_remarks(String status_remarks) {
		this.status_remarks = status_remarks;
	}

	public long getRta_office_code() {
		return rta_office_code;
	}

	public void setRta_office_code(long rta_office_code) {
		this.rta_office_code = rta_office_code;
	}

	public String getApplication_origination() {
		return application_origination;
	}

	public void setApplication_origination(String application_origination) {
		this.application_origination = application_origination;
	}

	public String getTicket_details() {
		return ticket_details;
	}

	public void setTicket_details(String ticket_details) {
		this.ticket_details = ticket_details;
	}

	public String getReference_id() {
		return reference_id;
	}

	public void setReference_id(String reference_id) {
		this.reference_id = reference_id;
	}

	public String getApproved_mvi() {
		return approved_mvi;
	}

	public void setApproved_mvi(String approved_mvi) {
		this.approved_mvi = approved_mvi;
	}

	public String getApproved_ao() {
		return approved_ao;
	}

	public void setApproved_ao(String approved_ao) {
		this.approved_ao = approved_ao;
	}

	public String getRetest_flag() {
		return retest_flag;
	}

	public void setRetest_flag(String retest_flag) {
		this.retest_flag = retest_flag;
	}
}

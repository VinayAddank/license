package org.rta.citizen.licence.entity.updated;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rta.citizen.common.entity.ApplicationEntity;

/**
 * The persistent class for the license_permit_details database table.
 * 
 */
@Entity
@Table(name = "license_permit_details")
public class LicensePermitDetailsEntity extends BaseLicenseEntity implements Serializable {

	private static final long serialVersionUID = -8430490709765159015L;

	@Id
	@Column(name = "license_permit_details_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_permit_details_seq")
	@SequenceGenerator(name = "license_permit_details_seq", sequenceName = "license_permit_details_seq", allocationSize = 1)
	private Long permitDetailsId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationId;

	@Column(name = "license_number")
	private String licenseNumber;

	@Column(name = "license_type")
	private String licenseType;

	@Column(name = "vehicle_class_code")
	private String vehicleClassCode;

	@Temporal(TemporalType.DATE)
	@Column(name = "test_date")
	private Date testDate;

	@Column(name = "test_exempted")
	private char testExempted;

	@Column(name = "test_exempted_reason")
	private String testExemptedReason;

	@Column(name = "test_no_of_attemp")
	private Integer testNoOfAttemp;

	@Column(name = "test_result")
	private String testResult;

	@Column(name = "test_marks")
	private String testMarks;

	@Column(name = "parent_consent_aadhaar_no")
	private String parentConsentAadhaarNo;

	@Column(name = "app_exam_number")
	private String applicationExamNumber;

	@Column(name = "is_badge", columnDefinition = "boolean default false")
	private boolean isBadge;

	@Column(name = "status")
	private Integer status;

	// @Column(name = "cco_user_name")
	// private String ccoUserName;
	//
	// @Column(name = "cco_action_status")
	// private Integer ccoActionStatus;
	//
	// @Column(name = "mvi_user_name")
	// private String mviUserName;
	//
	// @Column(name = "mvi_action_status")
	// private Integer mviActionStatus;
	//
	// @Column(name = "ao_user_name")
	// private String aoUserName;
	//
	// @Column(name = "ao_action_status")
	// private Integer aoActionStatus;

	// @Column(name = "rto_user_name")
	// private String rtoUserName;
	//
	// @Column(name = "rto_action_status")
	// private Integer rtoActionStatus;

	public Long getPermitDetailsId() {
		return permitDetailsId;
	}

	public void setPermitDetailsId(Long permitDetailsId) {
		this.permitDetailsId = permitDetailsId;
	}

	public ApplicationEntity getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(ApplicationEntity applicationId) {
		this.applicationId = applicationId;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public char getTestExempted() {
		return testExempted;
	}

	public void setTestExempted(char testExempted) {
		this.testExempted = testExempted;
	}

	public String getTestExemptedReason() {
		return testExemptedReason;
	}

	public void setTestExemptedReason(String testExemptedReason) {
		this.testExemptedReason = testExemptedReason;
	}

	public Integer getTestNoOfAttemp() {
		return testNoOfAttemp;
	}

	public void setTestNoOfAttemp(Integer testNoOfAttemp) {
		this.testNoOfAttemp = testNoOfAttemp;
	}

	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}

	public String getTestMarks() {
		return testMarks;
	}

	public void setTestMarks(String testMarks) {
		this.testMarks = testMarks;
	}

	public String getVehicleClassCode() {
		return vehicleClassCode;
	}

	public void setVehicleClassCode(String vehicleClassCode) {
		this.vehicleClassCode = vehicleClassCode;
	}

	public String getParentConsentAadhaarNo() {
		return parentConsentAadhaarNo;
	}

	public void setParentConsentAadhaarNo(String parentConsentAadhaarNo) {
		this.parentConsentAadhaarNo = parentConsentAadhaarNo;
	}

	public String getApplicationExamNumber() {
		return applicationExamNumber;
	}

	public void setApplicationExamNumber(String applicationExamNumber) {
		this.applicationExamNumber = applicationExamNumber;
	}

	public boolean isBadge() {
		return isBadge;
	}

	public void setBadge(boolean isBadge) {
		this.isBadge = isBadge;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
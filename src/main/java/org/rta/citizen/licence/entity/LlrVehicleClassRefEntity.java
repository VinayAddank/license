package org.rta.citizen.licence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;

@Entity
@Table(name = "vehicle_class_ref")
public class LlrVehicleClassRefEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 8387838928367969044L;
	@Id
	@Column(name = "vehicle_class_Id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_class_ref_seq")
	@SequenceGenerator(name = "vehicle_class_ref_seq", sequenceName = "vehicle_class_ref_seq", allocationSize = 1)
	private Long vehicleClassId;

	@Column(name = "licence_class_type")
	private String licenceClassType;

	@Column(name = "age_group_cd")
	private String ageGroupCode;

	@Column(name = "vehicle_class", length = 50)
	private String vehicleClassCode;

	@Column(name = "idp_class", length = 50)
	private String idpClass;

	@Column(name = "requires_doctor_cert")
	private Boolean requiresDoctorCert;

	@Column(name = "badge_available")
	private Boolean badgeAvailable;

	@Column(name = "hazardous")
	private Boolean hazardous;

	@Column(name = "validity_period")
	private Integer validityPeriod;

	@Column(name = "max_age")
	private Integer maxAge;

	public Long getVehicle_class_code() {
		return vehicleClassId;
	}

	public void setVehicle_class_code(Long vehicle_class_code) {
		this.vehicleClassId = vehicle_class_code;
	}

	public String getLicence_class_Type() {
		return licenceClassType;
	}

	public void setLicence_class_Type(String licence_class_code) {
		this.licenceClassType = licence_class_code;
	}

	public String getAge_group_cd() {
		return ageGroupCode;
	}

	public void setAge_group_cd(String age_group_cd) {
		this.ageGroupCode = age_group_cd;
	}

	public String getVehicle_class() {
		return vehicleClassCode;
	}

	public void setVehicle_class(String vehicle_class) {
		this.vehicleClassCode = vehicle_class;
	}

	public String getIdp_class() {
		return idpClass;
	}

	public void setIdp_class(String idp_class) {
		this.idpClass = idp_class;
	}

	public Boolean isRequires_doctor_cert() {
		return requiresDoctorCert;
	}

	public void setRequires_doctor_cert(Boolean requires_doctor_cert) {
		this.requiresDoctorCert = requires_doctor_cert;
	}

	public Boolean isBadge_available() {
		return badgeAvailable;
	}

	public void setBadge_available(Boolean badge_available) {
		this.badgeAvailable = badge_available;
	}

	public Boolean isHazardous() {
		return hazardous;
	}

	public void setHazardous(Boolean hazardous) {
		this.hazardous = hazardous;
	}

	public Integer getValidity_period() {
		return validityPeriod;
	}

	public void setValidity_period(Integer validity_period) {
		this.validityPeriod = validity_period;
	}

	public Integer getMax_age() {
		return maxAge;
	}

	public void setMax_age(Integer max_age) {
		this.maxAge = max_age;
	}

}

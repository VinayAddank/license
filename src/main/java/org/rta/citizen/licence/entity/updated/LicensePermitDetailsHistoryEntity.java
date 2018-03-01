package org.rta.citizen.licence.entity.updated;

import java.io.Serializable;

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
import javax.validation.constraints.NotNull;

import org.rta.citizen.common.entity.ApplicationEntity;

/**
 * The persistent class for the license_permit_details_history database table.
 * 
 */
@Entity
@Table(name = "license_permit_details_history")
public class LicensePermitDetailsHistoryEntity extends BaseLicenseEntity implements Serializable {

	private static final long serialVersionUID = -8430490709765159015L;

	@Id
	@Column(name = "license_permit_details_history_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_permit_details_history_seq")
	@SequenceGenerator(name = "license_permit_details_history_seq", sequenceName = "license_permit_details_history_seq", allocationSize = 1)
	private Long permitDetailsHistoryId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "vehicle_class_code")
	private String vehicleClassCode;

	@Column(name = "status")
	private Integer status;

	public Long getPermitDetailsHistoryId() {
		return permitDetailsHistoryId;
	}

	public void setPermitDetailsHistoryId(Long permitDetailsHistoryId) {
		this.permitDetailsHistoryId = permitDetailsHistoryId;
	}

	public ApplicationEntity getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(ApplicationEntity applicationId) {
		this.applicationId = applicationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getVehicleClassCode() {
		return vehicleClassCode;
	}

	public void setVehicleClassCode(String vehicleClassCode) {
		this.vehicleClassCode = vehicleClassCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
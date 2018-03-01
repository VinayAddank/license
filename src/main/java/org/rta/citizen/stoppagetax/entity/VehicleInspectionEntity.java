/**
 * 
 */
package org.rta.citizen.stoppagetax.entity;

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
import org.rta.citizen.common.entity.BaseEntity;

/**
 * @author sohan.maurya
 *
 */

@Entity
@Table(name = "vehicle_inspection")
public class VehicleInspectionEntity extends BaseEntity {

	/**
	* 
	*/
	private static final long serialVersionUID = -6470464874487801122L;

	@Id
	@Column(name = "vehicle_inspection_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_inspection_seq")
	@SequenceGenerator(name = "vehicle_inspection_seq", sequenceName = "vehicle_inspection_seq", allocationSize = 1)
	private Long vehicleInspectionId;

	@Column(name = "user_id")
	private Long userId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationId;

	@Column(name = "inspection_status", columnDefinition = "Integer DEFAULT 8", nullable = false)
	private Integer inspectionStatus;								

	@Column(name = "schedule_inspection_date")
	private Long scheduleInspectionDate;

	@Column(name = "inspection_date")
	private Long inspectionDate;
	
	@Column(name = "revocation_status", columnDefinition = "Integer DEFAULT 2", nullable = false)
	private Integer revocationStatus;

	
	public Long getVehicleInspectionId() {
		return vehicleInspectionId;
	}

	public void setVehicleInspectionId(Long vehicleInspectionId) {
		this.vehicleInspectionId = vehicleInspectionId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public ApplicationEntity getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(ApplicationEntity applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getInspectionStatus() {
		return inspectionStatus;
	}

	public void setInspectionStatus(Integer inspectionStatus) {
		this.inspectionStatus = inspectionStatus;
	}

	public Long getScheduleInspectionDate() {
		return scheduleInspectionDate;
	}

	public void setScheduleInspectionDate(Long scheduleInspectionDate) {
		this.scheduleInspectionDate = scheduleInspectionDate;
	}

	public Long getInspectionDate() {
		return inspectionDate;
	}

	public void setInspectionDate(Long inspectionDate) {
		this.inspectionDate = inspectionDate;
	}

	public Integer getRevocationStatus() {
		return revocationStatus;
	}

	public void setRevocationStatus(Integer revocationStatus) {
		this.revocationStatus = revocationStatus;
	}				

}

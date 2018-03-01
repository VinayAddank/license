package org.rta.citizen.licence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;

/**
 * The persistent class for the vehicle_class_mst database table.
 * 
 */
@Entity
@Table(name = "vehicle_class_mst")
public class LlrVehicleClassMasterEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1821979555252888597L;

	@Id
	@Column(name = "vehicle_class")
	private String vehicleClass;

	@Column(name = "is_active")
	private String isActive;

	@Column(name = "vehicle_class_description")
	private String vehicleClassDescription;

	@Column(name = "vehicle_class_group")
	private String vehicleClassGroup;

	@Column(name = "vehicle_class_type")
	private String vehicleClassType;

	@Column(name = "vehicle_transport_type")
	private String vehicleTransportType;

	public String getVehicleClass() {
		return this.vehicleClass;
	}

	public void setVehicleClass(String vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public String getIsActive() {
		return this.isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getVehicleClassDescription() {
		return this.vehicleClassDescription;
	}

	public void setVehicleClassDescription(String vehicleClassDescription) {
		this.vehicleClassDescription = vehicleClassDescription;
	}

	public String getVehicleClassGroup() {
		return this.vehicleClassGroup;
	}

	public void setVehicleClassGroup(String vehicleClassGroup) {
		this.vehicleClassGroup = vehicleClassGroup;
	}

	public String getVehicleClassType() {
		return this.vehicleClassType;
	}

	public void setVehicleClassType(String vehicleClassType) {
		this.vehicleClassType = vehicleClassType;
	}

	public String getVehicleTransportType() {
		return this.vehicleTransportType;
	}

	public void setVehicleTransportType(String vehicleTransportType) {
		this.vehicleTransportType = vehicleTransportType;
	}

}
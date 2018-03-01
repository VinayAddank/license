package org.rta.citizen.licence.entity.tests;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

@Entity
@Table(name = "vehicle_class_tests_mst")
public class VehicleClassTestsEntity extends BaseEntity {

	private static final long serialVersionUID = 3982764193919765839L;

	@Id
	@Column(name = "vehicle_class_tests_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_class_tests_seq")
	@SequenceGenerator(name = "vehicle_class_tests_seq", sequenceName = "vehicle_class_tests_seq", allocationSize = 1)
	private Long vehicleClassTestsId;

	@Column(name = "vehicle_class")
	private String vehicleClass;

	@Column(name = "test_type")
	@Enumerated(EnumType.STRING)
	private SlotServiceType testType;

	public Long getVehicleClassTestsId() {
		return vehicleClassTestsId;
	}

	public void setVehicleClassTestsId(Long vehicleClassTestsId) {
		this.vehicleClassTestsId = vehicleClassTestsId;
	}

	public String getVehicleClass() {
		return vehicleClass;
	}

	public void setVehicleClass(String vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public SlotServiceType getTestType() {
		return testType;
	}

	public void setTestType(SlotServiceType testType) {
		this.testType = testType;
	}

}

package org.rta.citizen.common.model;

import java.util.List;

import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.enums.PRType;

/**
 * @Author sohan.maurya created on Nov 24, 2016.
 */
public class VehicleBodyModel {

	private Integer seatingCapacity;
	private Long vehicleRcId;
	private Long lengthUpdated;
	private Long heightUpdated;
	private Long widthUpdated;
	private Long completionDate;
	private String vehicleSubClass;
	private String vehicleSubClassDecs;
	private String chassisNumber;
	private String bodyTypeUpdated;
	private String fuelType;
	private String gasKitNumber;
	private List<AlterationCategory> alterationCategory;
	private String applicationNumber;
	private PRType prType;
	private String engineNo;
	private String registrationCategoryCode;
	private String specialPrNo;
	private String oldRegistrationCategoryCode;
	private Long rlwUpdated;
	private Long ulwUpdated;
	private String colorUpdated;
	
	public Integer getSeatingCapacity() {
		return seatingCapacity;
	}

	public void setSeatingCapacity(Integer seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}

	public Long getVehicleRcId() {
		return vehicleRcId;
	}

	public void setVehicleRcId(Long vehicleRcId) {
		this.vehicleRcId = vehicleRcId;
	}

	public Long getLengthUpdated() {
		return lengthUpdated;
	}

	public void setLengthUpdated(Long lengthUpdated) {
		this.lengthUpdated = lengthUpdated;
	}

	public Long getHeightUpdated() {
		return heightUpdated;
	}

	public void setHeightUpdated(Long heightUpdated) {
		this.heightUpdated = heightUpdated;
	}

	public Long getWidthUpdated() {
		return widthUpdated;
	}

	public void setWidthUpdated(Long widthUpdated) {
		this.widthUpdated = widthUpdated;
	}

	public Long getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Long completionDate) {
		this.completionDate = completionDate;
	}

	public String getVehicleSubClass() {
		return vehicleSubClass;
	}

	public void setVehicleSubClass(String vehicleSubClass) {
		this.vehicleSubClass = vehicleSubClass;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public String getBodyTypeUpdated() {
		return bodyTypeUpdated;
	}

	public void setBodyTypeUpdated(String bodyTypeUpdated) {
		this.bodyTypeUpdated = bodyTypeUpdated;
	}

	public List<AlterationCategory> getAlterationCategory() {
		return alterationCategory;
	}

	public void setAlterationCategory(List<AlterationCategory> alterationCategory) {
		this.alterationCategory = alterationCategory;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getVehicleSubClassDecs() {
		return vehicleSubClassDecs;
	}

	public void setVehicleSubClassDecs(String vehicleSubClassDecs) {
		this.vehicleSubClassDecs = vehicleSubClassDecs;
	}

	public String getGasKitNumber() {
		return gasKitNumber;
	}

	public void setGasKitNumber(String gasKitNumber) {
		this.gasKitNumber = gasKitNumber;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public PRType getPrType() {
		return prType;
	}

	public void setPrType(PRType prType) {
		this.prType = prType;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getRegistrationCategoryCode() {
		return registrationCategoryCode;
	}

	public void setRegistrationCategoryCode(String registrationCategoryCode) {
		this.registrationCategoryCode = registrationCategoryCode;
	}

	public String getSpecialPrNo() {
		return specialPrNo;
	}

	public void setSpecialPrNo(String specialPrNo) {
		this.specialPrNo = specialPrNo;
	}

	public String getOldRegistrationCategoryCode() {
		return oldRegistrationCategoryCode;
	}

	public void setOldRegistrationCategoryCode(String oldRegistrationCategoryCode) {
		this.oldRegistrationCategoryCode = oldRegistrationCategoryCode;
	}

	public Long getRlwUpdated() {
		return rlwUpdated;
	}

	public void setRlwUpdated(Long rlwUpdated) {
		this.rlwUpdated = rlwUpdated;
	}

	public Long getUlwUpdated() {
		return ulwUpdated;
	}

	public void setUlwUpdated(Long ulwUpdated) {
		this.ulwUpdated = ulwUpdated;
	}

	public String getColorUpdated() {
		return colorUpdated;
	}

	public void setColorUpdated(String colorUpdated) {
		this.colorUpdated = colorUpdated;
	}
	
}

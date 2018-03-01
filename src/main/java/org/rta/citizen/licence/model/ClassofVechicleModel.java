package org.rta.citizen.licence.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClassofVechicleModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8799897595131891222L;
	private String ageGroup;
	private String covCode;
	private String covDescription;
	private String vehicleClassType;
	@NotNull(message = "selected vehicle class is missing")
	private String selected;

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public String getCovCode() {
		return covCode;
	}

	public void setCovCode(String covCode) {
		this.covCode = covCode;
	}

	public String getCovDescription() {
		return covDescription;
	}

	public void setCovDescription(String covDescription) {
		this.covDescription = covDescription;
	}

	public String getVehicleClassType() {
		return vehicleClassType;
	}

	public void setVehicleClassType(String vehicleClassType) {
		this.vehicleClassType = vehicleClassType;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}

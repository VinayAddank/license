package org.rta.citizen.licence.model;

import java.io.Serializable;

public class LLRClassofVechicleDetailsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8799897595131891222L;
	private String age_group;
	private String cov_code;
	private String cov_description;
	private String vehicleClassType;

	public String getAge_group() {
		return age_group;
	}

	public void setAge_group(String age_group) {
		this.age_group = age_group;
	}

	public String getCov_code() {
		return cov_code;
	}

	public void setCov_code(String cov_code) {
		this.cov_code = cov_code;
	}

	public String getCov_description() {
		return cov_description;
	}

	public void setCov_description(String cov_description) {
		this.cov_description = cov_description;
	}

	public String getVehicleClassType() {
		return vehicleClassType;
	}

	public void setVehicleClassType(String vehicleClassType) {
		this.vehicleClassType = vehicleClassType;
	}

}

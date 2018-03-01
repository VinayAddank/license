package org.rta.citizen.common.model;

import java.util.List;

import org.rta.citizen.common.model.payment.DifferentialTaxFeeModel;

/**
 * 
 * @author prabhat.singh
 * @description to hold VehicleBodyModel and ApplicationTaxModel
 *
 */
public class VehicleAlterationUpdateModel {
	
	private VehicleBodyModel vehicleBodyModel;
	private DifferentialTaxFeeModel differentialTaxFeeModel;
	private List<UserActionModel> userActionModel;

	public VehicleBodyModel getVehicleBodyModel() {
		return vehicleBodyModel;
	}

	public void setVehicleBodyModel(VehicleBodyModel vehicleBodyModel) {
		this.vehicleBodyModel = vehicleBodyModel;
	}

	public DifferentialTaxFeeModel getDifferentialTaxFeeModel() {
		return differentialTaxFeeModel;
	}

	public void setDifferentialTaxFeeModel(DifferentialTaxFeeModel differentialTaxFeeModel) {
		this.differentialTaxFeeModel = differentialTaxFeeModel;
	}

	public List<UserActionModel> getUserActionModel() {
		return userActionModel;
	}

	public void setUserActionModel(List<UserActionModel> userActionModel) {
		this.userActionModel = userActionModel;
	}

}

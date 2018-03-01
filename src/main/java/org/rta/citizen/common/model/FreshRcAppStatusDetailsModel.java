package org.rta.citizen.common.model;

import org.rta.citizen.freshrc.FreshRcModel;

public class FreshRcAppStatusDetailsModel {

	private FinanceModel financeModel;
	private VehicleDetailsRequestModel vehicleDetailsModel;
	private FreshRcModel freshRcModel;
	
	public FinanceModel getFinanceModel() {
		return financeModel;
	}
	public void setFinanceModel(FinanceModel financeModel) {
		this.financeModel = financeModel;
	}
	
	public FreshRcModel getFreshRcModel() {
		return freshRcModel;
	}
	public void setFreshRcModel(FreshRcModel freshRcModel) {
		this.freshRcModel = freshRcModel;
	}
	public VehicleDetailsRequestModel getVehicleDetailsModel() {
		return vehicleDetailsModel;
	}
	public void setVehicleDetailsModel(VehicleDetailsRequestModel vehicleDetailsModel) {
		this.vehicleDetailsModel = vehicleDetailsModel;
	}
}

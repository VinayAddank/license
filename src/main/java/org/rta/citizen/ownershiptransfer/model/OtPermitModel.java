package org.rta.citizen.ownershiptransfer.model;

import java.util.List;

public class OtPermitModel {

	private String otPermitMsg;
	
	private List<OtPermitOptionModel> otPermitOption;

	public String getOtPermitMsg() {
		return otPermitMsg;
	}

	public void setOtPermitMsg(String otPermitMsg) {
		this.otPermitMsg = otPermitMsg;
	}

	public List<OtPermitOptionModel> getOtPermitOption() {
		return otPermitOption;
	}

	public void setOtPermitOption(List<OtPermitOptionModel> otPermitOption) {
		this.otPermitOption = otPermitOption;
	}
	
}

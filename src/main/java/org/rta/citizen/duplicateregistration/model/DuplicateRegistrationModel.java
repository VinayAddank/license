package org.rta.citizen.duplicateregistration.model;

import java.util.List;

import org.rta.citizen.common.model.UserActionModel;

public class DuplicateRegistrationModel {
	
	private String reason;
	private String comment;
	private String prNumber;
	private List<UserActionModel> actionModelList;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getPrNumber() {
		return prNumber;
	}
	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}
	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}
	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}
	
}

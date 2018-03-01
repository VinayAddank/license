package org.rta.citizen.vehiclereassignment.model;

import java.util.List;

import org.rta.citizen.common.enums.PRType;
import org.rta.citizen.common.model.UserActionModel;

/**
 * 
 * @author prabhat.singh
 *
 */

public class VehicleReassignmentModel {
	private String applicationNo;
	private PRType prType;
	private String specialPrNo;
	private UserActionModel userActionModel;
	private String prNumber;
	private String regCategoryCode;
	private String serviceCode;
	private String cov;
	private List<UserActionModel> actionModelList;
	
	public String getApplicationNo() {
		return applicationNo;
	}
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}
	
	public String getSpecialPrNo() {
		return specialPrNo;
	}
	public void setSpecialPrNo(String specialPrNo) {
		this.specialPrNo = specialPrNo;
	}
	public PRType getPrType() {
		return prType;
	}
	public void setPrType(PRType prType) {
		this.prType = prType;
	}
	public UserActionModel getUserActionModel() {
		return userActionModel;
	}
	public void setUserActionModel(UserActionModel userActionModel) {
		this.userActionModel = userActionModel;
	}
	public String getPrNumber() {
		return prNumber;
	}
	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}
	public String getRegCategoryCode() {
		return regCategoryCode;
	}
	public void setRegCategoryCode(String regCategoryCode) {
		this.regCategoryCode = regCategoryCode;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getCov() {
		return cov;
	}
	public void setCov(String cov) {
		this.cov = cov;
	}
	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}
	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}
	
}

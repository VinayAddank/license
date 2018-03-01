package org.rta.citizen.freshrc;

import org.rta.citizen.common.model.AddressModel;

public class FinanceYardModel {
	private Long yardId;
	private Long headFinancerId;
	private String yardName;
	private Integer activeStatus;
	private Long yardArea;
	AddressModel yardAddress;
	// TODO : missing elements need to be added 
	
	public Long getYardId() {
		return yardId;
	}
	public void setYardId(Long yardId) {
		this.yardId = yardId;
	}
	public Long getHeadFinancerId() {
		return headFinancerId;
	}
	public void setHeadFinancerId(Long headFinancerId) {
		this.headFinancerId = headFinancerId;
	}
	public String getYardName() {
		return yardName;
	}
	public void setYardName(String yardName) {
		this.yardName = yardName;
	}
	public Integer getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Integer activeStatus) {
		this.activeStatus = activeStatus;
	}
	public Long getYardArea() {
		return yardArea;
	}
	public void setYardArea(Long yardArea) {
		this.yardArea = yardArea;
	}
	public AddressModel getYardAddress() {
		return yardAddress;
	}
	public void setYardAddress(AddressModel yardAddress) {
		this.yardAddress = yardAddress;
	}
	
	
}

package org.rta.citizen.common.model.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.model.UserActionModel;

@XmlRootElement
public class TaxModel {

	private Long taxValidUpto;
	private String taxType;
	private String taxAmt;
	private String taxPercent;
	private String penalty;
	private String totalAmt;
	private int serviceType;
	private String greenTaxAmt;
	private String serviceFee;
	private Long greenTaxValidTo;
	private String prNumber;
	private String adharNumber;
 	private String cessFee;
   	private Long cessFeeValidUpto;		
    private String penaltyArrears;
	private String taxArrears;
	private String compoundFee;
	private String transactionNo;
	private String challanNo;
	private List<UserActionModel> actionModelList;
	
	public Long getTaxValidUpto() {
		return taxValidUpto;
	}
	public void setTaxValidUpto(Long taxValidUpto) {
		this.taxValidUpto = taxValidUpto;
	}
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public String getTaxAmt() {
		return taxAmt;
	}
	public void setTaxAmt(String taxAmt) {
		this.taxAmt = taxAmt;
	}
	public String getTaxPercent() {
		return taxPercent;
	}
	public void setTaxPercent(String taxPercent) {
		this.taxPercent = taxPercent;
	}
	public String getPenalty() {
		return penalty;
	}
	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}
	public String getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}
	public int getServiceType() {
		return serviceType;
	}
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
	public String getGreenTaxAmt() {
		return greenTaxAmt;
	}
	public void setGreenTaxAmt(String greenTaxAmt) {
		this.greenTaxAmt = greenTaxAmt;
	}
	public String getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(String serviceFee) {
		this.serviceFee = serviceFee;
	}
	public Long getGreenTaxValidTo() {
		return greenTaxValidTo;
	}
	public void setGreenTaxValidTo(Long greenTaxValidTo) {
		this.greenTaxValidTo = greenTaxValidTo;
	}
	public String getPrNumber() {
		return prNumber;
	}
	public void setPrNumber(String prNumber) {
		this.prNumber = prNumber;
	}
	public String getAdharNumber() {
		return adharNumber;
	}
	public void setAdharNumber(String adharNumber) {
		this.adharNumber = adharNumber;
	}
	public String getCessFee() {
		return cessFee;
	}
	public void setCessFee(String cessFee) {
		this.cessFee = cessFee;
	}
	public Long getCessFeeValidUpto() {
		return cessFeeValidUpto;
	}
	public void setCessFeeValidUpto(Long cessFeeValidUpto) {
		this.cessFeeValidUpto = cessFeeValidUpto;
	}
	public String getPenaltyArrears() {
		return penaltyArrears;
	}
	public void setPenaltyArrears(String penaltyArrears) {
		this.penaltyArrears = penaltyArrears;
	}
	public String getTaxArrears() {
		return taxArrears;
	}
	public void setTaxArrears(String taxArrears) {
		this.taxArrears = taxArrears;
	}
	public String getCompoundFee() {
		return compoundFee;
	}
	public void setCompoundFee(String compoundFee) {
		this.compoundFee = compoundFee;
	}
	public String getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}
	public String getChallanNo() {
		return challanNo;
	}
	public void setChallanNo(String challanNo) {
		this.challanNo = challanNo;
	}
	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}
	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}
	
}

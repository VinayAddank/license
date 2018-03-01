package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class TaxFeeModel {

	private FeeModel feeModel ;
	private TaxModel taxModel ;
	private String 	 grandTotal ;
	private String customerName ;
	private int isSBIVerification ;
	private int IsPayUVerification ;
	
	public FeeModel getFeeModel() {
		return feeModel;
	}
	public void setFeeModel(FeeModel feeModel) {
		this.feeModel = feeModel;
	}
	public TaxModel getTaxModel() {
		return taxModel;
	}
	public void setTaxModel(TaxModel taxModel) {
		this.taxModel = taxModel;
	}
	public String getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getIsSBIVerification() {
		return isSBIVerification;
	}
	public void setIsSBIVerification(int isSBIVerification) {
		this.isSBIVerification = isSBIVerification;
	}
	public int getIsPayUVerification() {
		return IsPayUVerification;
	}
	public void setIsPayUVerification(int isPayUVerification) {
		IsPayUVerification = isPayUVerification;
	}
	
	
}

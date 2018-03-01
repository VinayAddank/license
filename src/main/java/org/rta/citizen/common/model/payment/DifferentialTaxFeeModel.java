/**
 * 
 */
package org.rta.citizen.common.model.payment;

/**
 * @author sohan.maurya
 *
 */

public class DifferentialTaxFeeModel {
	
	private double taxAmount;
	private double prFee;
	private double fitnessFee;
	private double lateFee;
	private double serviceFee;
	private double fitnessServiceFee;
	private double postalFee;
	private double smartCardFee;
	//totalFee = lateFee + fitnessFee + prFee + fitnessServiceFee + serviceFee + postalFee + smartCardFee
	private double totalFee;
	private double totalTaxFeeAmount;
	private String taxType;
	
	public double getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}
	public double getPrFee() {
		return prFee;
	}
	public void setPrFee(double prFee) {
		this.prFee = prFee;
	}
	public double getFitnessFee() {
		return fitnessFee;
	}
	public void setFitnessFee(double fitnessFee) {
		this.fitnessFee = fitnessFee;
	}
	public double getLateFee() {
		return lateFee;
	}
	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}
	public double getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public double getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(double serviceFee) {
		this.serviceFee = serviceFee;
	}
	public double getFitnessServiceFee() {
		return fitnessServiceFee;
	}
	public void setFitnessServiceFee(double fitnessServiceFee) {
		this.fitnessServiceFee = fitnessServiceFee;
	}
	public double getTotalTaxFeeAmount() {
		return totalTaxFeeAmount;
	}
	public void setTotalTaxFeeAmount(double totalTaxFeeAmount) {
		this.totalTaxFeeAmount = totalTaxFeeAmount;
	}
	public double getPostalFee() {
		return postalFee;
	}
	public void setPostalFee(double postalFee) {
		this.postalFee = postalFee;
	}
	public double getSmartCardFee() {
		return smartCardFee;
	}
	public void setSmartCardFee(double smartCardFee) {
		this.smartCardFee = smartCardFee;
	}
	
}

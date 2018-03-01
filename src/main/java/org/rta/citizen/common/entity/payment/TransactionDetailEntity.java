package org.rta.citizen.common.entity.payment;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.BaseEntity;

@Entity
@Table(name = "application_bank_transaction_detail")
public class TransactionDetailEntity extends BaseEntity {

	private static final long serialVersionUID = 3962338293823130468L;

	@Id
	@Column(name = "transaction_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tran_dtl_seq")
	@SequenceGenerator(name = "tran_dtl_seq", sequenceName = "tran_dtl_seq", allocationSize = 1)
	private Long transactionId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationId;

	@Column(name = "bank_transac_no", unique = true)
	private String bankTransacNo;

	@Column(name = "pay_amount")
	private double payAmount;

	@Column(name = "payment_type")
	private Integer paymentType;

	@Column(name = "payment_time")
	private Long paymentTime;

	@Column(name = "status")
	private Integer status;

	@Column(name = "sbi_ref_no")
	private String sbiRefNo;

	@Column(name = "bank_status_message")
	private String bankStatusMessage;

	@Column(name = "fee_amount")
	private double FeeAmount;

	@Column(name = "service_charge")
	private double serviceCharge;

	@Column(name = "postal_charge")
	private double postalCharge;

	@Column(name = "tax_amount")
	private double taxAmount;

	@Column(name = "compound_amount")
	private double compoundAmount;

	@Column(name = "permit_amount")
	private double permitAmount;

	@Column(name = "green_tax_amt")
	private double greenTaxAmt;
	
	@Column(name = "cess_fee")
	private double cessFee;
	
	@Column(name = "service_code")
	private String serviceCode;

	@Column(name = "pg_type")
	private String pgType;
	
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public ApplicationEntity getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(ApplicationEntity applicationId) {
		this.applicationId = applicationId;
	}

	public String getBankTransacNo() {
		return bankTransacNo;
	}

	public void setBankTransacNo(String bankTransacNo) {
		this.bankTransacNo = bankTransacNo;
	}

	public double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(double payAmount) {
		this.payAmount = payAmount;
	}

	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	public Long getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Long paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSbiRefNo() {
		return sbiRefNo;
	}

	public void setSbiRefNo(String sbiRefNo) {
		this.sbiRefNo = sbiRefNo;
	}

	public String getBankStatusMessage() {
		return bankStatusMessage;
	}

	public void setBankStatusMessage(String bankStatusMessage) {
		this.bankStatusMessage = bankStatusMessage;
	}

	public double getFeeAmount() {
		return FeeAmount;
	}

	public void setFeeAmount(double feeAmount) {
		FeeAmount = feeAmount;
	}

	public double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public double getPostalCharge() {
		return postalCharge;
	}

	public void setPostalCharge(double postalCharge) {
		this.postalCharge = postalCharge;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public double getCompoundAmount() {
		return compoundAmount;
	}

	public void setCompoundAmount(double compoundAmount) {
		this.compoundAmount = compoundAmount;
	}

	public double getPermitAmount() {
		return permitAmount;
	}

	public void setPermitAmount(double permitAmount) {
		this.permitAmount = permitAmount;
	}

	public double getGreenTaxAmt() {
		return greenTaxAmt;
	}

	public void setGreenTaxAmt(double greenTaxAmt) {
		this.greenTaxAmt = greenTaxAmt;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public double getCessFee() {
		return cessFee;
	}

	public void setCessFee(double cessFee) {
		this.cessFee = cessFee;
	}

	public String getPgType() {
		return pgType;
	}

	public void setPgType(String pgType) {
		this.pgType = pgType;
	}

}

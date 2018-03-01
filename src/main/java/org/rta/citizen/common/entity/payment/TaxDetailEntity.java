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
@Table(name = "application_tax_detail")
public class TaxDetailEntity extends BaseEntity {

	private static final long serialVersionUID = 2012266108018029931L;

	@Id
	@Column(name = "tax_dtl_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tax_dtl_seq")
	@SequenceGenerator(name = "tax_dtl_seq", sequenceName = "tax_dtl_seq", allocationSize = 1)
	private Long taxDtlId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationId;
	
	@Column(name = "tax_type")
	private String taxType;

	@Column(name = "tax_amount")
	private double taxAmt;
	
	@Column(name = "tax_percentage")
	private double taxPercentage;

	@Column(name = "total_amt")
	private double totalAmt;

	@Column(name = "valid_upto")
	private Long validUpto;
	
	@Column(name = "green_tax_amt" , columnDefinition = "Integer DEFAULT 0")
	private double greenTaxAmt;

	@Column(name = "green_tax_valid_to")
	private Long greenTaxValidTo;

	@Column(name = "penalty_amt" , columnDefinition = "Integer DEFAULT 0")
	private double penaltyAmt;
	
	@Column(name = "service_fee" , columnDefinition = "Integer DEFAULT 0")
	private double serviceFee;

	@Column(name = "quarter_amt" , columnDefinition = "Integer DEFAULT 0")
	private double quarterAmt;
	
    @Column(name = "cess_fee" , columnDefinition = "Integer DEFAULT 0")
	private double cessFee;
    
    @Column(name = "cessFee_valid_upto")
	private Long cessFeeValidUpto;

	@Column(name = "tax_amount_arrears" , columnDefinition = "Integer DEFAULT 0")
	private double taxAmtArrears;
	
	@Column(name = "penalty_amt_arrears" , columnDefinition = "Integer DEFAULT 0")
	private double penaltyAmtArrears;
	
	@Column(name = "compound_fee" , columnDefinition = "Integer DEFAULT 0")
	private double compoundFee;

	public Long getTaxDtlId() {
		return taxDtlId;
	}

	public void setTaxDtlId(Long taxDtlId) {
		this.taxDtlId = taxDtlId;
	}

	public ApplicationEntity getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(ApplicationEntity applicationId) {
		this.applicationId = applicationId;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public double getTaxAmt() {
		return taxAmt;
	}

	public void setTaxAmt(double taxAmt) {
		this.taxAmt = taxAmt;
	}

	public double getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(double taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public Long getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(Long validUpto) {
		this.validUpto = validUpto;
	}

	public double getGreenTaxAmt() {
		return greenTaxAmt;
	}

	public void setGreenTaxAmt(double greenTaxAmt) {
		this.greenTaxAmt = greenTaxAmt;
	}

	public Long getGreenTaxValidTo() {
		return greenTaxValidTo;
	}

	public void setGreenTaxValidTo(Long greenTaxValidTo) {
		this.greenTaxValidTo = greenTaxValidTo;
	}

	public double getPenaltyAmt() {
		return penaltyAmt;
	}

	public void setPenaltyAmt(double penaltyAmt) {
		this.penaltyAmt = penaltyAmt;
	}

	public double getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(double serviceFee) {
		this.serviceFee = serviceFee;
	}

	public double getTaxAmtArrears() {
		return taxAmtArrears;
	}

	public void setTaxAmtArrears(double taxAmtArrears) {
		this.taxAmtArrears = taxAmtArrears;
	}

	public double getPenaltyAmtArrears() {
		return penaltyAmtArrears;
	}

	public void setPenaltyAmtArrears(double penaltyAmtArrears) {
		this.penaltyAmtArrears = penaltyAmtArrears;
	}

	public double getCessFee() {
		return cessFee;
	}

	public void setCessFee(double cessFee) {
		this.cessFee = cessFee;
	}

	public Long getCessFeeValidUpto() {
		return cessFeeValidUpto;
	}

	public void setCessFeeValidUpto(Long cessFeeValidUpto) {
		this.cessFeeValidUpto = cessFeeValidUpto;
	}

	public double getQuarterAmt() {
		return quarterAmt;
	}

	public void setQuarterAmt(double quarterAmt) {
		this.quarterAmt = quarterAmt;
	}

	public double getCompoundFee() {
		return compoundFee;
	}

	public void setCompoundFee(double compoundFee) {
		this.compoundFee = compoundFee;
	}

	
        
}


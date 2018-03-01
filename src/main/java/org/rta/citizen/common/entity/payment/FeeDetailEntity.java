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
@Table(name = "application_fee_detail")
public class FeeDetailEntity extends BaseEntity {

	private static final long serialVersionUID = -3622868921756279934L;

	@Id
	@Column(name = "fee_dtl_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fee_dtl_seq")
	@SequenceGenerator(name = "fee_dtl_seq", sequenceName = "fee_dtl_seq", allocationSize = 1)
	private Long feeDtlId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationId;

	@Column(name = "application_fee")
	private double applicationFee;

	@Column(name = "postal_charge")
	private double postalCharge;

	@Column(name = "smart_card_fee")
	private double smartCardFee;

	@Column(name = "application_service_charge")
	private double applicationServiceCharge;

	@Column(name = "fitness_fee")
	private double fitnessFee;

	@Column(name = "permit_fee")
	private double permitFee;

	@Column(name = "other_permit_fee")
	private double OtherPermitFee;

	@Column(name = "fitness_service_charge")
	private double fitnessServiceCharge;

	@Column(name = "permit_service_charge")
	private double permitServiceCharge;

	@Column(name = "total_fee")
	private double totalFee;
	
	@Column(name = "license_test_fee")
	private double licenseTestFee;
	
	@Column(name = "penality_fee")
	private double penaltyFee;
	
	@Column(name = "late_fee")
	private double lateFee;

	@Column(name = "special_number_fee" , columnDefinition = "Integer DEFAULT 0")
	private double specialNumberFee;
	
	@Column(name = "hsrp_fee" , columnDefinition = "Integer DEFAULT 0")
	private double HSRPFee;
	
	public Long getFeeDtlId() {
		return feeDtlId;
	}

	public void setFeeDtlId(Long feeDtlId) {
		this.feeDtlId = feeDtlId;
	}

	public ApplicationEntity getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(ApplicationEntity applicationId) {
		this.applicationId = applicationId;
	}

	public double getApplicationFee() {
		return applicationFee;
	}

	public void setApplicationFee(double applicationFee) {
		this.applicationFee = applicationFee;
	}

	public double getPostalCharge() {
		return postalCharge;
	}

	public void setPostalCharge(double postalCharge) {
		this.postalCharge = postalCharge;
	}

	public double getSmartCardFee() {
		return smartCardFee;
	}

	public void setSmartCardFee(double smartCardFee) {
		this.smartCardFee = smartCardFee;
	}

	public double getApplicationServiceCharge() {
		return applicationServiceCharge;
	}

	public void setApplicationServiceCharge(double applicationServiceCharge) {
		this.applicationServiceCharge = applicationServiceCharge;
	}

	public double getFitnessFee() {
		return fitnessFee;
	}

	public void setFitnessFee(double fitnessFee) {
		this.fitnessFee = fitnessFee;
	}

	public double getPermitFee() {
		return permitFee;
	}

	public void setPermitFee(double permitFee) {
		this.permitFee = permitFee;
	}

	public double getFitnessServiceCharge() {
		return fitnessServiceCharge;
	}

	public void setFitnessServiceCharge(double fitnessServiceCharge) {
		this.fitnessServiceCharge = fitnessServiceCharge;
	}

	public double getPermitServiceCharge() {
		return permitServiceCharge;
	}

	public void setPermitServiceCharge(double permitServiceCharge) {
		this.permitServiceCharge = permitServiceCharge;
	}

	public double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}

	public double getOtherPermitFee() {
		return OtherPermitFee;
	}

	public void setOtherPermitFee(double otherPermitFee) {
		OtherPermitFee = otherPermitFee;
	}

	public double getLicenseTestFee() {
		return licenseTestFee;
	}

	public void setLicenseTestFee(double licenseTestFee) {
		this.licenseTestFee = licenseTestFee;
	}

	public double getPenaltyFee() {
		return penaltyFee;
	}

	public void setPenaltyFee(double penaltyFee) {
		this.penaltyFee = penaltyFee;
	}

	public double getLateFee() {
		return lateFee;
	}

	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}

	public double getSpecialNumberFee() {
		return specialNumberFee;
	}

	public void setSpecialNumberFee(double specialNumberFee) {
		this.specialNumberFee = specialNumberFee;
	}

	public double getHSRPFee() {
		return HSRPFee;
	}

	public void setHSRPFee(double hSRPFee) {
		HSRPFee = hSRPFee;
	}

}

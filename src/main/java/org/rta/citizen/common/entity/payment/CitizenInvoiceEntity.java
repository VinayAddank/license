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
@Table(name = "citizen_invoice")
public class CitizenInvoiceEntity extends BaseEntity {

	private static final long serialVersionUID = 5207142240567316930L;

	@Id
	@Column(name = "citizen_invc_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "citizen_inv_seq")
	@SequenceGenerator(name = "citizen_inv_seq", sequenceName = "citizen_inv_seq", allocationSize = 1)
	private Long citizenInvcId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "tax_dtl_id")
	private TaxDetailEntity taxDtlId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "fee_dtl_id")
	private FeeDetailEntity regFeeDtlId;

	@Column(name = "total_amount")
	private double totalAmount;

	@Column(name = "invoice_no")
	private String invoiceNo;

	@Column(name = "invoice_amt")
	private double invoiceAmt;

	@Column(name = "invoice_date")
	private Long invoiceDate;

	@Column(name = "status")
	private Integer status;

	public Long getCitizenInvcId() {
		return citizenInvcId;
	}

	public void setCitizenInvcId(Long citizenInvcId) {
		this.citizenInvcId = citizenInvcId;
	}

	public TaxDetailEntity getTaxDtlId() {
		return taxDtlId;
	}

	public void setTaxDtlId(TaxDetailEntity taxDtlId) {
		this.taxDtlId = taxDtlId;
	}

	public FeeDetailEntity getRegFeeDtlId() {
		return regFeeDtlId;
	}

	public void setRegFeeDtlId(FeeDetailEntity regFeeDtlId) {
		this.regFeeDtlId = regFeeDtlId;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public double getInvoiceAmt() {
		return invoiceAmt;
	}

	public void setInvoiceAmt(double invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	public Long getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Long invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public ApplicationEntity getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(ApplicationEntity applicationId) {
		this.applicationId = applicationId;
	}

}

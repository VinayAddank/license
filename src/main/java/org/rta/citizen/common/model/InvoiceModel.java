package org.rta.citizen.common.model;

import java.util.List;

import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.slotbooking.model.SlotModel;

public class InvoiceModel extends BaseModel {

	private static final long serialVersionUID = 756549642540693195L;

	private String appNo;
	private String applicantName;
	private String adharNo;
	private String appRCNo;
	private List<SlotModel> slots;
	private FeeModel feeModel ;
	private long payDate ;
	private TaxModel taxModel ;
	private String grandTotal;
	private AddressModel addresModel;
	private String makerName;
	private String chassisNo;
	private String engineNo;
	private String colour;
	private String permitType;
	private String serviceCode;
	private String cov;
	private String covDesc;
	
	public String getAppNo() {
		return appNo;
	}
	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	public String getAdharNo() {
		return adharNo;
	}
	public void setAdharNo(String adharNo) {
		this.adharNo = adharNo;
	}
	public String getAppRCNo() {
		return appRCNo;
	}
	public void setAppRCNo(String appRCNo) {
		this.appRCNo = appRCNo;
	}
    public List<SlotModel> getSlots() {
        return slots;
    }
    public void setSlots(List<SlotModel> slots) {
        this.slots = slots;
    }
	public FeeModel getFeeModel() {
		return feeModel;
	}
	public void setFeeModel(FeeModel feeModel) {
		this.feeModel = feeModel;
	}
	public long getPayDate() {
		return payDate;
	}
	public void setPayDate(long payDate) {
		this.payDate = payDate;
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
	public AddressModel getAddresModel() {
		return addresModel;
	}
	public void setAddresModel(AddressModel addresModel) {
		this.addresModel = addresModel;
	}
	public String getMakerName() {
		return makerName;
	}
	public void setMakerName(String makerName) {
		this.makerName = makerName;
	}
	public String getChassisNo() {
		return chassisNo;
	}
	public void setChassisNo(String chassisNo) {
		this.chassisNo = chassisNo;
	}
	public String getEngineNo() {
		return engineNo;
	}
	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getPermitType() {
		return permitType;
	}
	public void setPermitType(String permitType) {
		this.permitType = permitType;
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
	public String getCovDesc() {
		return covDesc;
	}
	public void setCovDesc(String covDesc) {
		this.covDesc = covDesc;
	}
	
}

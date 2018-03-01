package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegFeeDetailModel {

    private Long regFeeDtlId;
    private String regFee;
    private String postalCharge;
    private String smartCardFee;
    private String serviceCharge;
    private String importedFee;
    private String totalRegFee;
    private String specialNumberFee;
    private PermitFeeModel permitFeeModel;
    private FitnessFeeModel fitnessFeeModel;

    public Long getRegFeeDtlId() {
        return regFeeDtlId;
    }

    public void setRegFeeDtlId(Long regFeeDtlId) {
        this.regFeeDtlId = regFeeDtlId;
    }

    public String getRegFee() {
        return regFee;
    }

    public void setRegFee(String regFee) {
        this.regFee = regFee;
    }

    public String getPostalCharge() {
        return postalCharge;
    }

    public void setPostalCharge(String postalCharge) {
        this.postalCharge = postalCharge;
    }

    public String getSmartCardFee() {
        return smartCardFee;
    }

    public void setSmartCardFee(String smartCardFee) {
        this.smartCardFee = smartCardFee;
    }

    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getImportedFee() {
        return importedFee;
    }

    public void setImportedFee(String importedFee) {
        this.importedFee = importedFee;
    }

    public String getTotalRegFee() {
        return totalRegFee;
    }

    public void setTotalRegFee(String totalRegFee) {
        this.totalRegFee = totalRegFee;
    }

    public String getSpecialNumberFee() {
        return specialNumberFee;
    }

    public void setSpecialNumberFee(String specialNumberFee) {
        this.specialNumberFee = specialNumberFee;
    }

    public PermitFeeModel getPermitFeeModel() {
        return permitFeeModel;
    }

    public void setPermitFeeModel(PermitFeeModel permitFeeModel) {
        this.permitFeeModel = permitFeeModel;
    }

    public FitnessFeeModel getFitnessFeeModel() {
        return fitnessFeeModel;
    }

    public void setFitnessFeeModel(FitnessFeeModel fitnessFeeModel) {
        this.fitnessFeeModel = fitnessFeeModel;
    }



}

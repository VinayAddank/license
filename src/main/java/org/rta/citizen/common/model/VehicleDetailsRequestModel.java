package org.rta.citizen.common.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class VehicleDetailsRequestModel {

//    @NotNull(message = "registrationCategory is missing")
    //private Long registrationCategoryId;
    
    @NotNull(message = "vehicleRcId is missing")
    private String vehicleRcId;
    /*
    @NotNull(message = "taxType is missing")
    private Integer taxType;
    */
    @NotNull(message = "dealerRTA is missing")
    private Long rtaOfficeCode;
    
    /*@NotNull(message = "numberChoice is missing")
    private Integer numberChoice;*/

    @Valid
    @NotNull(message = "vehicle is missing")
    private UnregisteredVehicleModel vehicle;
    
    @Valid
    @NotNull(message = "billModel is missing")
    private PurchaseDetails billModel;
    
    private String panNumber;
    private Boolean isSecondVehicle;
    private RegistrationCategoryModel regCategoryDetails;
    private String prNumber;
    private Long prExpireDate;
    private Long prIssueDate;
    
    public Long getPrIssueDate() {
        return prIssueDate;
    }

    public void setPrIssueDate(Long prIssueDate) {
        this.prIssueDate = prIssueDate;
    }

    public Long getDealerRTA() {
        return rtaOfficeCode;
    }
    
    public void setDealerRTA(Long rtaOfficeCode) {
        this.rtaOfficeCode = rtaOfficeCode;
    }

    public String getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(String vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public UnregisteredVehicleModel getVehicle() {
        return vehicle;
    }

    public void setVehicle(UnregisteredVehicleModel vehicle) {
        this.vehicle = vehicle;
    }

/*    public BillingDetailsModel getBillModel() {
        return billModel;
    }

    public void setBillModel(BillingDetailsModel billModel) {
        this.billModel = billModel;
    }*/

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public Boolean getIsSecondVehicle() {
        return isSecondVehicle;
    }

    public void setIsSecondVehicle(Boolean isSecondVehicle) {
        this.isSecondVehicle = isSecondVehicle;
    }

    public PurchaseDetails getBillModel() {
        return billModel;
    }

    public void setBillModel(PurchaseDetails billModel) {
        this.billModel = billModel;
    }

    public RegistrationCategoryModel getRegCategoryDetails() {
        return regCategoryDetails;
    }

    public void setRegCategoryDetails(RegistrationCategoryModel regCategoryDetails) {
        this.regCategoryDetails = regCategoryDetails;
    }

    public String getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }

    public Long getPrExpireDate() {
		return prExpireDate;
	}
    
    public void setPrExpireDate(Long prExpireDate) {
		this.prExpireDate = prExpireDate;
	}
    
}

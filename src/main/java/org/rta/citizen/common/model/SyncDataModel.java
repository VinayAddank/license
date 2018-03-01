package org.rta.citizen.common.model;

import java.util.List;

import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.model.payment.TaxModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SyncDataModel {

    private Long vehicleRcId;
    private String prNumber;
    private String serviceType;
    private String serviceCategory;
    private List<ApplicationFormDataModel> formList;
    private FeeModel feeModel;
    private String applicationNumber;
    private List<UserActionModel> actionModelList;
    private TaxModel taxModel;
    
    public Long getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(Long vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public String getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public List<ApplicationFormDataModel> getFormList() {
        return formList;
    }

    public void setFormList(List<ApplicationFormDataModel> formList) {
        this.formList = formList;
    }

    public FeeModel getFeeModel() {
        return feeModel;
    }

    public void setFeeModel(FeeModel feeModel) {
        this.feeModel = feeModel;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}

	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}

	public TaxModel getTaxModel() {
		return taxModel;
	}

	public void setTaxModel(TaxModel taxModel) {
		this.taxModel = taxModel;
	}

}

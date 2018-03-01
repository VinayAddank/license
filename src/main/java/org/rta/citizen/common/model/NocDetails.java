/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@JsonInclude(Include.NON_NULL)
public class NocDetails {

    private Long vehicleRcId;
    private Long appliedDate;
    private Long issueDate;
    private Long cancellationDate;
    private String applicationNo;
    private Boolean status;
    private String nocAddressCode;
    private AddressModel address;
    private RTAOfficeModel rtaOffice;
    private String serviceCode;
    private List<UserActionModel> actionModelList;

    public Long getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(Long vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public Long getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Long appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public Long getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Long cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getNocAddressCode() {
        return nocAddressCode;
    }

    public void setNocAddressCode(String nocAddressCode) {
        this.nocAddressCode = nocAddressCode;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public RTAOfficeModel getRtaOffice() {
        return rtaOffice;
    }

    public void setRtaOffice(RTAOfficeModel rtaOffice) {
        this.rtaOffice = rtaOffice;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}

	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}
    
}

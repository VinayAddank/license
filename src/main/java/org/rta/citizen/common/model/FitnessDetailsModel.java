/**
 * 
 */
package org.rta.citizen.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@JsonInclude(Include.NON_NULL)
public class FitnessDetailsModel {

    private String fcNumber;
    private String rtaOfficeName;
    private Long issueDate;
    private Long expiryDate;
    private Boolean status;
    private String registrationNumber;
    private String mviName;
    private VehicleBaseModel vehicleDetails;

    public String getFcNumber() {
        return fcNumber;
    }

    public void setFcNumber(String fcNumber) {
        this.fcNumber = fcNumber;
    }

    public String getRtaOfficeName() {
        return rtaOfficeName;
    }

    public void setRtaOfficeName(String rtaOfficeName) {
        this.rtaOfficeName = rtaOfficeName;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMviName() {
        return mviName;
    }

    public void setMviName(String mviName) {
        this.mviName = mviName;
    }

    public VehicleBaseModel getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(VehicleBaseModel vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }


}

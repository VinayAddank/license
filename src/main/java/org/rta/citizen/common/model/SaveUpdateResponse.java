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
public class SaveUpdateResponse {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    private String status;
    private String message;
    private String vehicleRcId;
    private Integer currentStep;
    private Integer code;

    public SaveUpdateResponse() {}

    public SaveUpdateResponse(String status, String message, String vehicleRcId) {
        this.status = status;
        this.message = message;
        this.vehicleRcId = vehicleRcId;
    }

    public SaveUpdateResponse(String status, String message, String vehicleRcId, Integer currentStep) {
        this.status = status;
        this.message = message;
        this.vehicleRcId = vehicleRcId;
        this.currentStep = currentStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(String vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "SaveUpdateResponse [status=" + status + ", message=" + message + ", vehicleRcId=" + vehicleRcId
                + ", currentStep=" + currentStep + ", code=" + code + "]";
    }

}

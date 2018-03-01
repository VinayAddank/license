/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.List;
import java.util.Map;

import org.rta.citizen.common.model.activiti.RtaTaskInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@JsonInclude(Include.NON_NULL)
public class ResponseModel<D> {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILURE";
    public static final String PENDING = "PENDING";
    public static final String SAVE = "SAVE";
    public static final String UPDATE = "UPDATE";
    public static final String GET = "GET";

    private String status;
    private String message;
    private Integer statusCode;
    private D data;
    private Map<String, String> errors;
    private List<RtaTaskInfo> activitiTasks;

    public ResponseModel() {}

    public ResponseModel(String status) {
        this.status = status;
    }

    public ResponseModel(String status, String message, Integer statusCode) {
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }

    public ResponseModel(String status, D data) {
        this.status = status;
        this.data = data;
    }

    public ResponseModel(String status, D data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public ResponseModel(String status, D data, String message, Integer statusCode) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
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

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public List<RtaTaskInfo> getActivitiTasks() {
        return activitiTasks;
    }

    public void setActivitiTasks(List<RtaTaskInfo> activitiTasks) {
        this.activitiTasks = activitiTasks;
    }

}

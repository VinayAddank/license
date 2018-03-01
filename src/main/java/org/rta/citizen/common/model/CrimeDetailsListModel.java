package org.rta.citizen.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CrimeDetailsListModel {
	private List<CrimeDetailsModel> result;
	private String status;
	private String httpStatus;
	private String message;
	private String errors;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	public List<CrimeDetailsModel> getResult() {
		return result;
	}

	public void setResult(List<CrimeDetailsModel> result) {
		this.result = result;
	}
}

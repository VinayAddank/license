package org.rta.citizen.common.model.payment;

import java.util.List;

public class PayUResponse {

	private String status;
	private String message;
	private String errorCode;
	private String responseCode;
	private List<PayUVerifyResponseTransaction> result;

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

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public List<PayUVerifyResponseTransaction> getResult() {
		return result;
	}

	public void setResult(List<PayUVerifyResponseTransaction> result) {
		this.result = result;
	}

}

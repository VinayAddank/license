package org.rta.citizen.common.exception;

public class ServiceValidationException extends Exception {

    private static final long serialVersionUID = -2911199436356955107L;
    private Integer errorCode;
    private String errorMsg;

    public ServiceValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceValidationException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ServiceValidationException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
    
}

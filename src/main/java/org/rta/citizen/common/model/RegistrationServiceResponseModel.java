package org.rta.citizen.common.model;

import org.springframework.http.HttpStatus;

public class RegistrationServiceResponseModel<T> {

    private HttpStatus httpStatus;
    private T responseBody;

    public RegistrationServiceResponseModel(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public RegistrationServiceResponseModel(HttpStatus httpStatus, T responseBody) {
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public T getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(T responseBody) {
        this.responseBody = responseBody;
    }

}

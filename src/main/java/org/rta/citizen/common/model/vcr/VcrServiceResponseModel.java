package org.rta.citizen.common.model.vcr;

import org.springframework.http.HttpStatus;

public class VcrServiceResponseModel<T> {

    private HttpStatus httpStatus;
    private T responseBody;

    public VcrServiceResponseModel(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public VcrServiceResponseModel(HttpStatus httpStatus, T responseBody) {
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

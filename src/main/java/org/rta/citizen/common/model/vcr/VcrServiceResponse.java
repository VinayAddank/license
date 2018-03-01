package org.rta.citizen.common.model.vcr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetLicRegVCRDetailsResponse", namespace = "http://tempuri.org/")
@XmlAccessorType( XmlAccessType.FIELD )
public class VcrServiceResponse {
    
    @XmlElement(name = "GetLicRegVCRDetailsResult")
    private String responseData;

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}

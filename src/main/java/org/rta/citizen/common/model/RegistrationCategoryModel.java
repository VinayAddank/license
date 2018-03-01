package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class RegistrationCategoryModel extends BaseMasterModel {

    private String code;
    private Integer registrationCategoryId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getRegistrationCategoryId() {
        return registrationCategoryId;
    }

    public void setRegistrationCategoryId(Integer registrationCategoryId) {
        this.registrationCategoryId = registrationCategoryId;
    }


}

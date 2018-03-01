package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class DistrictModel extends BaseMasterModel {

    private String code;
    private StateModel stateModel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public StateModel getStateModel() {
        return stateModel;
    }

    public void setStateModel(StateModel stateModel) {
        this.stateModel = stateModel;
    }

}

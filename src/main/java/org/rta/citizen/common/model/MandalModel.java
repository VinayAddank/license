package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class MandalModel extends BaseMasterModel {

    private Integer code;
    private DistrictModel districtModel;
    private RTAOfficeModel rtaOfficeModel;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DistrictModel getDistrictModel() {
        return districtModel;
    }

    public void setDistrictModel(DistrictModel districtModel) {
        this.districtModel = districtModel;
    }

    public RTAOfficeModel getRtaOfficeModel() {
        return rtaOfficeModel;
    }

    public void setRtaOfficeModel(RTAOfficeModel rtaOfficeModel) {
        this.rtaOfficeModel = rtaOfficeModel;
    }

}

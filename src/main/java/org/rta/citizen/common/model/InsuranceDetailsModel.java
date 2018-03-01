package org.rta.citizen.common.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class InsuranceDetailsModel extends BaseModel {

    private static final long serialVersionUID = -3663969168376678953L;
    @JsonIgnore
    private Long insuranceDtlId;
    @NotNull(message = "mode required !")
    private String mode;
    @NotNull(message = "provider required !")
    private String provider;
    @NotNull(message = "policyNo required !")
    private String policyNo;
    @NotNull(message = "startDate required !")
    private Long startDate;
    @NotNull(message = "endDate required !")
    private Long endDate;
    @NotNull(message = "tenure required !")
    private Integer tenure;
    @JsonIgnore
    private Integer status;
    @NotNull(message = "vehicleRcId required !")
    private String vehicleRcId;
    @NotNull(message = "insuranceTypeCode required !")
    private String insuranceTypeCode;
    private String insuranceTypeName;


    public Long getInsuranceDtlId() {
        return insuranceDtlId;
    }

    public void setInsuranceDtlId(Long insuranceDtlId) {
        this.insuranceDtlId = insuranceDtlId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(String vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public String getInsuranceTypeCode() {
        return insuranceTypeCode;
    }

    public void setInsuranceTypeCode(String insuranceId) {
        this.insuranceTypeCode = insuranceId;
    }

    public String getInsuranceTypeName() {
        return insuranceTypeName;
    }

    public void setInsuranceTypeName(String insuranceTypeName) {
        this.insuranceTypeName = insuranceTypeName;
    }

    @Override
    public String toString() {
        return "InsuranceDetailsModel [insuranceDtlId=" + insuranceDtlId + ", mode=" + mode + ", provider=" + provider
                + ", policyNo=" + policyNo + ", startDate=" + startDate + ", endDate=" + endDate + ", tenure=" + tenure
                + ", status=" + status + ", vehicleRcId=" + vehicleRcId + ", insuranceTypeCode=" + insuranceTypeCode
                + ", insuranceTypeName=" + insuranceTypeName + "]";
    }

}

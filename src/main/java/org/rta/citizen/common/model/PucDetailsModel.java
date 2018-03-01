package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *	@Author sohan.maurya created on Dec 15, 2016.
 */

@XmlRootElement
// @JsonInclude(Include.NON_NULL)
public class PucDetailsModel {

    private Long vehicleRcId;
    private Long issueDate;
    private Long expiryDate;
    private Float co;
    private Float hc;
    private Float kAvg;
    private Float hsu;
    private Boolean status;

    public Long getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(Long vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Float getCo() {
        return co;
    }

    public void setCo(Float co) {
        this.co = co;
    }

    public Float getHc() {
        return hc;
    }

    public void setHc(Float hc) {
        this.hc = hc;
    }

    public Float getkAvg() {
        return kAvg;
    }

    public void setkAvg(Float kAvg) {
        this.kAvg = kAvg;
    }

    public Float getHsu() {
        return hsu;
    }

    public void setHsu(Float hsu) {
        this.hsu = hsu;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


}

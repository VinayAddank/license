package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PermitFeeModel {


    private String permitFee;
    private String permitServiceCharge;
    private String permitType;
    private String otherFee;
    private String totalPermitFee;


    public String getPermitFee() {
        return permitFee;
    }

    public void setPermitFee(String permitFee) {
        this.permitFee = permitFee;
    }

    public String getPermitServiceCharge() {
        return permitServiceCharge;
    }

    public void setPermitServiceCharge(String permitServiceCharge) {
        this.permitServiceCharge = permitServiceCharge;
    }

    public String getPermitType() {
        return permitType;
    }

    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public String getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(String otherFee) {
        this.otherFee = otherFee;
    }

    public String getTotalPermitFee() {
        return totalPermitFee;
    }

    public void setTotalPermitFee(String totalPermitFee) {
        this.totalPermitFee = totalPermitFee;
    }


}

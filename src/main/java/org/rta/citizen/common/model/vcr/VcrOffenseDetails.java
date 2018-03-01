package org.rta.citizen.common.model.vcr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Table1")
@XmlAccessorType(XmlAccessType.FIELD)
public class VcrOffenseDetails {

    @XmlElement(name = "VCR_NUMBER")
    private String vcrNumber;

    @XmlElement(name = "OFFENSE")
    private String offense;

    @XmlElement(name = "FINEAMOUNT")
    private String fineAmount;

    public String getVcrNumber() {
        return vcrNumber;
    }

    public void setVcrNumber(String vcrNumber) {
        this.vcrNumber = vcrNumber;
    }

    public String getOffense() {
        return offense;
    }

    public void setOffense(String offense) {
        this.offense = offense;
    }

    public String getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(String fineAmount) {
        this.fineAmount = fineAmount;
    }
}

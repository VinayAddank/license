package org.rta.citizen.common.model.vcr;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NewDataSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class VcrDataModel {

    @XmlElement(name = "Table")
    private List<VcrBookingData> bookingData;

    @XmlElement(name = "Table1")
    private List<VcrOffenseDetails> offenseDetails = new ArrayList<>();

    public List<VcrBookingData> getBookingData() {
        return bookingData;
    }

    public void setBookingData(List<VcrBookingData> bookingData) {
        this.bookingData = bookingData;
    }

    public List<VcrOffenseDetails> getOffenseDetails() {
        return offenseDetails;
    }

    public void setOffenseDetails(List<VcrOffenseDetails> offenseDetails) {
        this.offenseDetails = offenseDetails;
    }
}

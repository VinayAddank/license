package org.rta.citizen.slotbooking.model;

import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.ReceiptModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SlotBookingReceiptModel extends ReceiptModel {

    private Long dateOfInspection;
    private Long timeOfInspection;
    private RTAOfficeModel rtaOffice;

    public Long getDateOfInspection() {
        return dateOfInspection;
    }

    public void setDateOfInspection(Long dateOfInspection) {
        this.dateOfInspection = dateOfInspection;
    }

    public Long getTimeOfInspection() {
        return timeOfInspection;
    }

    public void setTimeOfInspection(Long timeOfInspection) {
        this.timeOfInspection = timeOfInspection;
    }

    public RTAOfficeModel getRtaOffice() {
        return rtaOffice;
    }

    public void setRtaOffice(RTAOfficeModel rtaOffice) {
        this.rtaOffice = rtaOffice;
    }

}

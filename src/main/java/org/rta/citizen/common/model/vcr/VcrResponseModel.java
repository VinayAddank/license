package org.rta.citizen.common.model.vcr;

import java.util.List;

public class VcrResponseModel {

    private List<VcrBookingData> bookings;

    public List<VcrBookingData> getBookings() {
        return bookings;
    }

    public void setBookingData(List<VcrBookingData> bookings) {
        this.bookings = bookings;
    }
}

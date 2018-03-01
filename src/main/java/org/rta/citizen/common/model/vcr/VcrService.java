package org.rta.citizen.common.model.vcr;

import java.util.List;

import org.rta.citizen.common.exception.UnauthorizedException;

public interface VcrService {

    VcrServiceResponseModel<List<VcrBookingData>> getVCRForRCNumber(String prNumber) throws UnauthorizedException;

}

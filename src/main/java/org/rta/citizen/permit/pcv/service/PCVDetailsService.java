package org.rta.citizen.permit.pcv.service;

import org.apache.log4j.Logger;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.GeneralDetails;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.AbstractDetailsService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class PCVDetailsService extends AbstractDetailsService {
	
	private static final Logger log = Logger.getLogger(PCVDetailsService.class);

    @Autowired
    private RegistrationService registrationService;

    @Override
    public GeneralDetails getDetails(String aadharNumber, String uniqueKey) {
        RegistrationServiceResponseModel<ApplicationModel> result = null;
        try {
            result = registrationService.getPRDetails(uniqueKey);
        } catch (RestClientException e) {
            log.error("error when getting pr details : " + e);
        } catch (UnauthorizedException e) {
            log.error("unauthorized");
        }
        if (ObjectsUtil.isNull(result)) {
            log.info("pr details not found for pr number : " + uniqueKey);
            return null;
        }
        if (result.getHttpStatus() != HttpStatus.OK) {
            log.info("error in http request " + result.getHttpStatus());
            return null;
        }
        ApplicationModel applicationModel = result.getResponseBody();
        Long vehicleRcId = applicationModel.getVehicleRcId();
        return GeneralDetails.builder().add(getCustomerDetails(vehicleRcId)).add(getVehicleDetails(vehicleRcId))
                .addChallanDetails(getChallanList(applicationModel.getPrNumber())).addCrimeDetails(getCrimeDetails(applicationModel.getPrNumber()))
                .add(getFinancierDetails(vehicleRcId, applicationModel.getPrNumber())).add(getInsuranceDetails(vehicleRcId))
                .add(getTaxDetails(applicationModel.getPrNumber())).add(getPermitDetails(vehicleRcId)).add(getFitnessDetails(vehicleRcId))
                .add(getPucDetails(vehicleRcId)).add(getNocDetails(vehicleRcId)).add(getSuspensionDetails(vehicleRcId)).build();
    }
}

package org.rta.citizen.slotbooking.service.impl;

import org.apache.log4j.Logger;
import org.rta.citizen.common.controller.ApplicationController;
import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.GeneralDetails;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.UnregisteredVehicleModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.service.AbstractDetailsService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class SlotBookingDetailsService extends AbstractDetailsService {

	private static final Logger log = Logger.getLogger(ApplicationController.class);

    @Autowired
    private RegistrationService registrationService;
    
    @Override
    public GeneralDetails getDetails(String aadharNumber, String uniqueKey) {
        RegistrationServiceResponseModel<ApplicationModel> result = null;
        RegistrationServiceResponseModel<VehicleBodyModel> alterationResponse = null;
        Long vehicleRcId = null;
        try {
            result = registrationService.getTRDetails(uniqueKey);
            vehicleRcId = result.getResponseBody().getVehicleRcId();
            alterationResponse = registrationService.getVehicleAlterationDetails(vehicleRcId, null);
        } catch (RestClientException e) {
            log.error("error when getting tr details : " + e);
        } catch (UnauthorizedException e) {
            log.error("unauthorized");
        } catch (Exception e) {
            log.error("getting exception");
        }
        if (ObjectsUtil.isNull(alterationResponse)) {
            log.info("tr details not found for tr number : " + uniqueKey);
            return null;
        }
        if (alterationResponse.getHttpStatus() != HttpStatus.OK) {
            log.info("error in http request " + alterationResponse.getHttpStatus());
            return null;
        }
        VehicleBodyModel atlerationModel = alterationResponse.getResponseBody();
        VehicleDetailsRequestModel vehicleDetaislModel = getVehicleDetails(vehicleRcId);
        UnregisteredVehicleModel unregisteredVehicleModel = vehicleDetaislModel.getVehicle();
        if (!ObjectsUtil.isNull(atlerationModel)) {
			//unregisteredVehicleModel.setRlw(atlerationModel.getRlwUpdated());
			unregisteredVehicleModel.setUlw(atlerationModel.getUlwUpdated());
			unregisteredVehicleModel.setVehicleSubClass(atlerationModel.getVehicleSubClass());
			unregisteredVehicleModel.setVehicleClassDes(atlerationModel.getVehicleSubClassDecs());
			if(!StringsUtil.isNullOrEmpty(atlerationModel.getColorUpdated())){
				unregisteredVehicleModel.setColor(atlerationModel.getColorUpdated());
			}
			for (AlterationCategory alterationCategory : atlerationModel.getAlterationCategory()) {
				if (alterationCategory == AlterationCategory.SEATING_CAPACITY) {
					unregisteredVehicleModel.setSeatingCapacity(atlerationModel.getSeatingCapacity());
				} else if (alterationCategory == AlterationCategory.BODY_TYPE) {
					unregisteredVehicleModel.setBodyTypeDesc(atlerationModel.getBodyTypeUpdated());
				} else if (alterationCategory == AlterationCategory.NEW_VEHICLE_ALTERATION){
					unregisteredVehicleModel.setBodyTypeDesc(atlerationModel.getBodyTypeUpdated());
					unregisteredVehicleModel.setSeatingCapacity(atlerationModel.getSeatingCapacity());
				}
			}
		} 
        vehicleDetaislModel.setVehicle(unregisteredVehicleModel);
        return GeneralDetails.builder().add(getCustomerDetails(vehicleRcId)).add(vehicleDetaislModel)
                .build();
    }

}

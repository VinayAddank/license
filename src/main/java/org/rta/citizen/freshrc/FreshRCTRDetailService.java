package org.rta.citizen.freshrc;

import org.apache.log4j.Logger;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.GeneralDetails;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.AbstractDetailsService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.hpa.service.HPADetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class FreshRCTRDetailService extends AbstractDetailsService {

	private static final Logger log = Logger.getLogger(HPADetailsService.class);

	@Autowired
	private RegistrationService registrationService;

	@Override
	public GeneralDetails getDetails(String aadharNumber, String uniqueKey) {
		RegistrationServiceResponseModel<ApplicationModel> result = null;
		try {
			result = registrationService.getTRDetails(uniqueKey);
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
		CustomerDetailsRequestModel customerDetailsRequestModel = getCustomerDetails(vehicleRcId);
		RegistrationServiceResponseModel<FinancerFreshContactDetailsModel> financerFreshContactDetails = getFinancierContactDetails(vehicleRcId);
		if (!ObjectsUtil.isNull(financerFreshContactDetails.getResponseBody())) {
			log.info("<<<<<<<<<<<<financerFreshRcEntity>>>>>>>>>>>>>" + financerFreshContactDetails);
			FinancerFreshContactDetailsModel financerFreshContactDetailsModel = financerFreshContactDetails.getResponseBody();
			if (!ObjectsUtil.isNull(financerFreshContactDetailsModel)) {
				log.info(
						"<<<<<<<<<<<<financerFreshContactDetailsModel>>>>>>>>>>>>>" + financerFreshContactDetailsModel);
				customerDetailsRequestModel.setMobileNumber(financerFreshContactDetailsModel.getMobileNumber());
				customerDetailsRequestModel.setEmailid(financerFreshContactDetailsModel.getEmail());
			}
		}
		return GeneralDetails.builder().add(customerDetailsRequestModel).add(getVehicleDetails(vehicleRcId)).build();
	}

}

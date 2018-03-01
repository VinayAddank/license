package org.rta.citizen.licence.service.updated.impl.details;

import org.apache.log4j.Logger;
import org.rta.citizen.common.model.GeneralDetails;
import org.rta.citizen.common.service.AbstractDetailsService;
import org.springframework.stereotype.Service;

/**
 * @Author sohan.maurya created on Dec 20, 2016.
 */

@Service
public class LicenseDetailsService extends AbstractDetailsService {

	private static final Logger logger = Logger.getLogger(LicenseDetailsService.class);

	@Override
	public GeneralDetails getDetails(String aadharNumber, String uniqueKey) {
		logger.info("Getting Details :::::::::::: for DL services");

		return GeneralDetails.builder().add(getDriversLicenseDtls(aadharNumber, uniqueKey)).build();
	}

}

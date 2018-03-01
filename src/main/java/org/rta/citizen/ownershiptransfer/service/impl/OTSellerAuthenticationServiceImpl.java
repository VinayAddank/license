/**
 * 
 */
package org.rta.citizen.ownershiptransfer.service.impl;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.hpa.service.HPAAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author arun.verma
 *
 */

@Service
public class OTSellerAuthenticationServiceImpl extends AuthenticationService {

	private static final Logger log = Logger.getLogger(HPAAuthenticationService.class);

    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
        RegistrationServiceResponseModel<Boolean> res = registrationService.hasAppliedHPA(uniqueKey);
        if (res.getHttpStatus() != HttpStatus.OK) {
            log.error("hasAppliedHPA Status is not OK...");
            throw new UnauthorizedException("unauthorized");
        }
        boolean hasApplied = res.getResponseBody();
        if (hasApplied) {
            log.error("HPA is running for this vehicle. Can't apply OTS");
            throw new ServiceValidationException(ServiceValidation.HPA_FOUND.getCode(), ServiceValidation.HPA_FOUND.getValue());
        }
        return true;
    }

}

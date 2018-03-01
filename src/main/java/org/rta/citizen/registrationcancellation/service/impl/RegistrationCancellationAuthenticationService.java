package org.rta.citizen.registrationcancellation.service.impl;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *	@Author sohan.maurya created on Dec 26, 2016.
 */

@Service
public class RegistrationCancellationAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(RegistrationCancellationAuthenticationService.class);
    
    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
        log.info("::Registration Cancellation Authentication Service validation:::::start::");
        RegistrationServiceResponseModel<Boolean> hasHPARes = registrationService.hasAppliedHPA(uniqueKey);
        if (hasHPARes.getHttpStatus().equals(HttpStatus.OK)) {
            boolean hasApplied = hasHPARes.getResponseBody();
            if(hasApplied){
                log.error(":: There is a HPA associated with this appliocation :"+uniqueKey);
                throw new ServiceValidationException(ServiceValidation.HPA_FOUND.getCode(), ServiceValidation.HPA_FOUND.getValue());
            }
        }
        return true;
    }
}

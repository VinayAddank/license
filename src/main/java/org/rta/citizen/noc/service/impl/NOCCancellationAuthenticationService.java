package org.rta.citizen.noc.service.impl;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class NOCCancellationAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(NOCCancellationAuthenticationService.class);
    
    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
        RegistrationServiceResponseModel<NocDetails> res = registrationService.getNocDetails(null, uniqueKey);
        if (res.getHttpStatus().equals(HttpStatus.OK)) {
            if(ObjectsUtil.isNull(res.getResponseBody()) || !res.getResponseBody().getStatus()){
                log.error("for CC, NOC yet not applied for : " + uniqueKey);
                throw new ServiceValidationException(ServiceValidation.NOC_NOT_ISSUED.getCode(), ServiceValidation.NOC_NOT_ISSUED.getValue());
            }
            return true;
        } else if(res.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)){
            log.error("for CC, NOC yet not applied for : " + uniqueKey);
            throw new ServiceValidationException(ServiceValidation.NOC_NOT_ISSUED.getCode(), ServiceValidation.NOC_NOT_ISSUED.getValue());
        } else if(res.getHttpStatus().equals(HttpStatus.BAD_REQUEST)){
            throw new UnauthorizedException("Invalid PR Number");
        } else {
            log.error("getNocDetails Status is not OK... for CC");
            throw new UnauthorizedException("unknown error");
        }
    }

}

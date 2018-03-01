package org.rta.citizen.noc.service.impl;

import org.rta.citizen.common.service.AuthenticationService;
import org.springframework.stereotype.Service;

/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */
@Service
public class NocAuthenticationServiceImpl extends AuthenticationService {
/*
 	private static final Logger log = Logger.getLogger(NocAuthenticationServiceImpl.class);
    
    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
        RegistrationServiceResponseModel<NocDetails> res = registrationService.getNocDetails(null, uniqueKey);
        if (res.getHttpStatus().equals(HttpStatus.OK)) {
            NocDetails noc = res.getResponseBody();
            if(ObjectsUtil.isNull(noc)){
                return true;
            } else {
                if(!noc.getStatus()){
                    return true;
                }
                log.error("NOC is already applied(found in registration) for : " + uniqueKey);
                throw new ServiceValidationException(ServiceValidation.NOC_ISSUED.getCode(), ServiceValidation.NOC_ISSUED.getValue());
            }
        } else if(res.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)){
            return true;
        } else if(res.getHttpStatus().equals(HttpStatus.BAD_REQUEST)){
            throw new UnauthorizedException("Invalid PR Number");
        } else {
            log.error("getNocDetails Status is not OK... for NOC");
            throw new UnauthorizedException("unknown error");
        }
    }*/
    
}

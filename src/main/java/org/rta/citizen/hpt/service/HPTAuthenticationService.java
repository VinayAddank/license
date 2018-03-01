/**
 * 
 */
package org.rta.citizen.hpt.service;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author arun.verma
 *
 */
@Service
public class HPTAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(HPTAuthenticationService.class);
    
    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
        /*RegistrationServiceResponseModel<FinanceOtherServiceModel> res = registrationService.getFinancier(uniqueKey);
        if (!res.getHttpStatus().equals(HttpStatus.OK)) {
            if (res.getHttpStatus().equals(HttpStatus.FORBIDDEN)) {
                throw new ServiceValidationException(600, "Vehicle is not Financed !!!");
            } else if (res.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)) {
                throw new ServiceValidationException(601, "Financer not found for given PR Number!!!");
            }
            log.error("Exception While getting financier from registration : applying HPA ...");
            return false;
        }*/
        
        RegistrationServiceResponseModel<ApplicationModel> prDetailResponse = registrationService.getPRDetails(uniqueKey);
        if (!ObjectsUtil.isNull(prDetailResponse.getResponseBody()) && prDetailResponse.getResponseBody().getPrStatus().equals(Status.SUSPENDED)) {
            throw new ServiceValidationException("Vehicle RC is already suspended with respect to vehicle Number " + uniqueKey);
        }
        
        log.info("::HPTAuthenticationService:::::start::");
        RegistrationServiceResponseModel<Boolean> hasHPARes = registrationService.hasAppliedHPA(uniqueKey);
        if (!hasHPARes.getHttpStatus().equals(HttpStatus.OK)) {
            log.error("hasAppliedHPA Status is not OK...");
            throw new ServiceValidationException(ServiceValidation.HPA_NOT_FOUND.getCode(), ServiceValidation.HPA_NOT_FOUND.getValue());
        }
        boolean hasApplied = hasHPARes.getResponseBody();
        if(!hasApplied){
            log.error("already having for HPT");
            throw new ServiceValidationException(ServiceValidation.HPA_NOT_FOUND.getCode(), ServiceValidation.HPA_NOT_FOUND.getValue());
        }
        log.info("::HPTAuthenticationService:::::end:: " + hasApplied);
        return true;
    }

}

package org.rta.citizen.hpa.service;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.FinanceType;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HPAAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(HPAAuthenticationService.class);
    
    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
        
        RegistrationServiceResponseModel<ApplicationModel> prDetailResponse = registrationService.getPRDetails(uniqueKey);
        if (!ObjectsUtil.isNull(prDetailResponse.getResponseBody()) && prDetailResponse.getResponseBody().getPrStatus().equals(Status.SUSPENDED)) {
            throw new ServiceValidationException("Vehicle RC is already suspended with respect to vehicle Number " + uniqueKey);
        }

        RegistrationServiceResponseModel<FinanceModel> body = registrationService.getFinanceInfo(uniqueKey);
        if (!body.getHttpStatus().equals(HttpStatus.OK) || body.getResponseBody() == null) {
            log.error("hasAppliedHPA Status is not OK...: " + body.getHttpStatus());
            throw new ServiceValidationException(ServiceValidation.HPA_FOUND.getCode(), ServiceValidation.HPA_FOUND.getValue());
        }
        
        FinanceModel financeModel = body.getResponseBody();
        if(FinanceType.ONLINE.getId() == financeModel.getFinancerMode() && !financeModel.isFinanceTerminated()) {
            log.error("Vehicle has already been financed.");
            throw new ServiceValidationException(ServiceValidation.HPA_FOUND.getCode(), ServiceValidation.HPA_FOUND.getValue()); 
        } else if (FinanceType.OFFLINE.getId() == financeModel.getFinancerMode()) {
            log.error("Vehicle has been financed offline.");
            throw new ServiceValidationException(ServiceValidation.OFFLINE_FINANCED.getCode(), ServiceValidation.OFFLINE_FINANCED.getValue());
        }
        
        return true;
    }

    
    
}

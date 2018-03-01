/**
 * 
 */
package org.rta.citizen.paytax.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.service.AbstractDetailsService;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author arun.verma
 *
 */
@Service
public class PayTaxAuthenticationService extends AuthenticationService{

    private static final Log log = LogFactory.getLog(PayTaxAuthenticationService.class);
    
    @Autowired
    @Qualifier(value = "duplicateRegistrationDetailsService")
    private AbstractDetailsService abstractDetailsService;

    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
        log.info("::PayTaxAuthenticationService:::::start::");
        RegistrationServiceResponseModel<ApplicationTaxModel> taxDetail = registrationService.getTaxDetails(uniqueKey);
        if (!taxDetail.getHttpStatus().equals(HttpStatus.OK)) {
            log.error("PayTaxAuthenticationService: alltaxdetails service response status is not OK.");
            throw new ServiceValidationException(ServiceValidation.TAX_DETAILS_NOT_FOUND.getCode(), ServiceValidation.TAX_DETAILS_NOT_FOUND.getValue());
        }
        ApplicationTaxModel taxModel = taxDetail.getResponseBody();
        if(null == taxModel){
            log.error("PayTaxAuthenticationService: ApplicationTaxModel is null.");
            throw new ServiceValidationException(ServiceValidation.TAX_DETAILS_NOT_FOUND.getCode(), ServiceValidation.TAX_DETAILS_NOT_FOUND.getValue());
        } else if(taxModel.getTaxValidUpto() > DateUtil.toCurrentUTCTimeStamp() && !abstractDetailsService.vcrValidate(uniqueKey)) {
            log.error("Tax hasn't been expired for this vehicle.");
            throw new ServiceValidationException(ServiceValidation.TAX_NOT_EXPIRED.getCode(), ServiceValidation.TAX_NOT_EXPIRED.getValue());
        }
        log.info("::PayTaxAuthenticationService:::::end::  Validation Success.");
        return true;
    }

}

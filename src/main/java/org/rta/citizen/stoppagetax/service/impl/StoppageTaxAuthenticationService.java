package org.rta.citizen.stoppagetax.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author sohan.maurya created on Jul 5, 2017
 *
 */

@Service
public class StoppageTaxAuthenticationService extends AuthenticationService{
	
	 private static final Log log = LogFactory.getLog(StoppageTaxAuthenticationService.class);

	    @Override
	    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
	            throws ServiceValidationException, UnauthorizedException {
	        super.validate(serviceType, uniqueKey, aadharNumber, keyType);
	        log.info("::StoppageTaxAuthenticationService:::::start::");
	        RegistrationServiceResponseModel<ApplicationTaxModel> taxDetail = registrationService.getTaxDetails(uniqueKey);
	        if (!taxDetail.getHttpStatus().equals(HttpStatus.OK)) {
	            log.error("StoppageTaxAuthenticationService: alltaxdetails service response status is not OK.");
	            throw new ServiceValidationException(ServiceValidation.TAX_DETAILS_NOT_FOUND.getCode(), ServiceValidation.TAX_DETAILS_NOT_FOUND.getValue());
	        }
	        ApplicationTaxModel taxModel = taxDetail.getResponseBody();
	        if(ObjectsUtil.isNull(taxModel)){
	            log.error("StoppageTaxAuthenticationService: ApplicationTaxModel is null.");
	            throw new ServiceValidationException(ServiceValidation.TAX_DETAILS_NOT_FOUND.getCode(), ServiceValidation.TAX_DETAILS_NOT_FOUND.getValue());
	        } else if(taxModel.getTaxType() == SomeConstants.TWO || taxModel.getRegType() != SomeConstants.ONE ) {
	            log.error("Stoppage Tax hasn't been applicable for this Vehicle.");
	            throw new ServiceValidationException(ServiceValidation.STOPPAGE_TAX_NOT_APLICABLE.getCode(), ServiceValidation.STOPPAGE_TAX_NOT_APLICABLE.getValue());
	        } else if(taxModel.getTaxValidUpto() < DateUtil.getTimeStampTonight() ) {
	            log.error("Tax has been expired for this vehicle.");
	            throw new ServiceValidationException(ServiceValidation.TAX_EXPIRED.getCode(), ServiceValidation.TAX_EXPIRED.getValue());
	        }
	        log.info("::StoppageTaxAuthenticationService:::::end::  Validation Success.");
	        return true;
	    }

}

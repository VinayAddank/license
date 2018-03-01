package org.rta.citizen.stoppagetax.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sohan.maurya created on Jul 15, 2017
 *
 */

@Service
public class StoppageTaxRevocationAuthenticationService extends AuthenticationService{
	
	private static final Log log = LogFactory.getLog(StoppageTaxRevocationAuthenticationService.class);
	 
	@Autowired
	private UserSessionDAO userSessionDAO;
	
    @Override
    protected boolean validate(ServiceType serviceType, String uniqueKey, String aadharNumber, KeyType keyType)
            throws ServiceValidationException, UnauthorizedException {
    	log.info("Login validation for STR");
    	UserSessionEntity userSessionEntity = userSessionDAO.getLatestUserSession(aadharNumber, uniqueKey, keyType, ServiceType.STOPPAGE_TAX, Status.APPROVED);
    	if(ObjectsUtil.isNull(userSessionEntity)){
    		 throw new ServiceValidationException(ServiceValidation.VEHICLE_IS_NOT_STOPPAGE_TAX.getCode(), ServiceValidation.VEHICLE_IS_NOT_STOPPAGE_TAX.getValue());
    	}
    	return super.validate(serviceType, uniqueKey, aadharNumber, keyType);
    }
}

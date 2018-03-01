package org.rta.citizen.common.service;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javassist.NotFoundException;

public abstract class AbstractAppSearchService {
    
	private static final Logger log = Logger.getLogger(AbstractAppSearchService.class);
    
    @Autowired
    protected RegistrationService registrationService;

    @Transactional
    public void getRegistrationCategory(ApplicationEntity appEntity, ApplicationStatusModel mdl)
            throws UnauthorizedException, NotFoundException {
        UserSessionEntity session = appEntity.getLoginHistory();
        RegistrationServiceResponseModel<ApplicationModel> res = registrationService.getDetails(session.getUniqueKey(), session.getKeyType());
        ApplicationModel applicationModel;
        try{
            if (res.getHttpStatus() == HttpStatus.OK) {
                applicationModel = res.getResponseBody();
                mdl.setRegCategory(applicationModel.getRegistrationCategory());
            } else {
                log.error(session.getKeyType() + " number not found : " + session.getUniqueKey());
                throw new NotFoundException(session.getKeyType() + " number not found : " + session.getUniqueKey());
            }
        } catch(Exception ex){
            if (res !=null) {
                log.error("res : " + res.getHttpStatus());
            }
            log.error(session.getKeyType() + " number not found : " + session.getUniqueKey());
            throw new NotFoundException(session.getKeyType() + " number not found : " + session.getUniqueKey());
        }
    }
    
}

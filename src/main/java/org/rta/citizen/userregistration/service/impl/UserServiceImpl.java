package org.rta.citizen.userregistration.service.impl;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.userregistration.dao.PendingUsernameDAO;
import org.rta.citizen.userregistration.entity.PendingUsernameEntity;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.rta.citizen.userregistration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

/**
 *	@Author sohan.maurya created on Jan 2, 2017.
 */
@Service
public class UserServiceImpl implements UserService {
    
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private PendingUsernameDAO pendingUsernameDAO;
    
    @Autowired
    private ApplicationDAO applicationDAO;
    
    @Override
    public UserSignupModel register(UserSessionModel session, UserSignupModel user) {
        return null;
    }

    @Override
    public ResponseModel<UserSignupModel> saveOrUpdateUser(UserSessionModel session, UserSignupModel user) throws UnauthorizedException {
        // we must have the aadhar number of every user specially of Online Financer
        user.setAadharNumber(session.getAadharNumber());
        RegistrationServiceResponseModel<UserSignupModel> response = registrationService.saveOrUpdateUser(user);
        if (response.getHttpStatus() == HttpStatus.OK) {
            return new ResponseModel<>(ResponseModel.SUCCESS, response.getResponseBody());
        }
        logger.error("error in creating user : " + user.getLoginDetails().getUsername());
        return null;
    }
    
    @Override
    @Transactional
    public Boolean isUserExists(UserSessionModel session, String username) throws UnauthorizedException {
        RegistrationServiceResponseModel<UserModel> response = null;
        try {
            ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
            if (ObjectsUtil.isNull(appEntity)) {
                logger.debug("app not found for session : " + session.getSessionId());
            }
            PendingUsernameEntity pen = pendingUsernameDAO.getByUsernameAndStatus(username, Status.PENDING);
            if (!ObjectsUtil.isNull(pen) && pen.getApplication().getApplicationId() != appEntity.getApplicationId()) {
                logger.debug("username found in pending username table : " + username);
                return Boolean.TRUE;
            }
            response = registrationService.getUser(username);
            if (response.getHttpStatus() == HttpStatus.OK && !ObjectsUtil.isNull(response.getResponseBody())) {
                logger.debug("username found in registration users table : " + username);
                return Boolean.TRUE;
            }
        } catch (HttpStatusCodeException e) {
            logger.error("error getting user details for username : " + username + ", statuscode : " + e.getStatusCode());
        }
        return Boolean.FALSE;
    }
    
}

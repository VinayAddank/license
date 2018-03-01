package org.rta.citizen.userregistration.service;

import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.userregistration.model.UserSignupModel;

/**
 *	@Author sohan.maurya created on Jan 2, 2017.
 */
public interface UserService {

    public UserSignupModel register(UserSessionModel session, UserSignupModel user);
    
    public ResponseModel<UserSignupModel> saveOrUpdateUser(UserSessionModel session, UserSignupModel user) throws UnauthorizedException;

    Boolean isUserExists(UserSessionModel session, String username) throws UnauthorizedException;

}

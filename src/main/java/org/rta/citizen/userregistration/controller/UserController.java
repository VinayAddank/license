package org.rta.citizen.userregistration.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.UserSessionService;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.rta.citizen.userregistration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author sohan.maurya created on Jan 2, 2017.
 */
@RestController
@RequestMapping("/{servicetype}/users")
public class UserController {

	private static final Logger log = Logger.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserSessionService userSessionService;

    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestHeader(value = "Authorization") String token,
            @PathVariable("servicetype") String serviceType, @RequestBody UserSignupModel user) {

        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        UserSessionModel session = userSessionService.getSession(sessionId);
        if (ObjectsUtil.isNull(session)) {
            log.info("unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
        }
        userService.register(session, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{username}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> isUsernameExists(@RequestHeader(value = "Authorization") String token,
            @PathVariable("servicetype") String serviceType, @PathVariable("username") String username) {

        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        UserSessionModel session = userSessionService.getSession(sessionId);
        if (ObjectsUtil.isNull(session)) {
            log.info("unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
        }
        ResponseModel<Map<String, String>> response;
        try {
            Map<String, String> map = new HashMap<>();
            Boolean isExists = userService.isUserExists(session, username);
            map.put("isExists", (isExists != null && isExists) ? "true":"false"/* boolean changed to string due to front end issue*/);
            response = new ResponseModel<Map<String, String>>(ResponseModel.SUCCESS);
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(map);
        } catch (UnauthorizedException e) {
            response = new ResponseModel<Map<String, String>>(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return ResponseEntity.ok(response);
    }

}

package org.rta.citizen.noc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.rta.citizen.common.UserSessionService;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */

@RestController
@RequestMapping(value = "/noc")
public class NocController {

	private static final Logger logger = Logger.getLogger(NocController.class);

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserSessionService userSessionService;


    @RequestMapping(value = "address", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addressDetails(@RequestParam(name = "districtcode", required = false) String districtCode,
            @RequestParam(name = "nocaddresscode", required = false) String nocAddressCode) {

        if (StringsUtil.isNullOrEmpty(districtCode) && StringsUtil.isNullOrEmpty(nocAddressCode)) {
            throw new IllegalArgumentException("bad request..");
        }
        if (!StringsUtil.isNullOrEmpty(nocAddressCode)) {
            RegistrationServiceResponseModel<NocDetails> nocAddress = null;
            try {
                nocAddress = registrationService.getNocAddressDetails(nocAddressCode);
            } catch (RestClientException e) {
                logger.error("error when getting NOC Address details : " + e);
            } catch (UnauthorizedException ex) {
                throw new IllegalArgumentException(
                        "There is not Noc Adddress Details for nocAddressCode = " + nocAddressCode);
            }
            if (ObjectsUtil.isNull(nocAddress)) {
                logger.info("NOC Address details not found for nocAddressCode: " + nocAddressCode);
                return null;
            }
            if (nocAddress.getHttpStatus() != HttpStatus.OK) {
                logger.info("error in http request " + nocAddress.getHttpStatus());
                return null;
            }
            logger.info(" NOC Address Details Send Succsssfully ");
            return ResponseEntity.ok(new ResponseModel<NocDetails>(ResponseModel.SUCCESS, nocAddress.getResponseBody()));
        } else if (!StringsUtil.isNullOrEmpty(districtCode)) {
            RegistrationServiceResponseModel<List<NocDetails>> nocAddressList = null;
            try {
                nocAddressList = registrationService.getNocAddressList(districtCode);
            } catch (RestClientException e) {
                logger.error("error when getting NOC Address details : " + e);
            } catch (UnauthorizedException ex) {
                throw new IllegalArgumentException(
                        "There is not Noc Adddress Details for nocAddressCode = " + nocAddressCode);
            }
            if (ObjectsUtil.isNull(nocAddressList)) {
                logger.info("NOC Address details not found for nocAddressCode: " + nocAddressCode);
                return null;
            }
            if (nocAddressList.getHttpStatus() != HttpStatus.OK) {
                logger.info("error in http request " + nocAddressList.getHttpStatus());
                return null;
            }
            logger.info(" NOC Address List Send Succsssfully ");
            return ResponseEntity.ok(new ResponseModel<List<NocDetails>>(ResponseModel.SUCCESS, nocAddressList.getResponseBody()));
        }
        logger.info(" get bad request ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
    }

    @RequestMapping(value = "details", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getNocDetails(HttpServletRequest request) {
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        UserSessionModel userSessionModel = userSessionService.getSession(jwtTokenUtil.getSessionIdFromToken(request.getHeader(tokenHeader)));
        if (ObjectsUtil.isNull(userSessionModel)) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Invalid Reqest");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        RegistrationServiceResponseModel<NocDetails> result = null;
        try {
            result = registrationService.getNocDetails(null, userSessionModel.getUniqueKey());
        }catch (RestClientException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("getting Error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UnauthorizedException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("User Not Found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } if(result.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)){
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setMessage("Invalid PR Number");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (result.getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Bad Request..!!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(new ResponseModel<NocDetails>(ResponseModel.SUCCESS, result.getResponseBody()));
    }
}

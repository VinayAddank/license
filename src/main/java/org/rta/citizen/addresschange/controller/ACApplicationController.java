package org.rta.citizen.addresschange.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.service.ACApplicationService;
import org.rta.citizen.common.controller.AttachmentController;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *	@Author sohan.maurya created on Dec 20, 2016.
 *
 */

@RestController
public class ACApplicationController {

	private static final Logger logger = Logger.getLogger(AttachmentController.class);

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${doc.root}")
    private String rootPath;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ACApplicationService acApplicationService;


    @RequestMapping(value = "aadhar/details", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> aadharDetails(HttpServletRequest request,@RequestParam(name = "applicationno", required = false) String applicationNumber ) {

        String token = request.getHeader(tokenHeader);
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        logger.info(" get session : " + sessionId);
        AadharModel aadharModel = null;
        try {
            aadharModel = acApplicationService.getAadharDetails(sessionId, applicationNumber);
        } catch (NotFoundException e) {
            logger.error("error when getting Aadhar Number: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "There is not aadher details for this service ");
        }
        if (ObjectsUtil.isNull(aadharModel)) {
            logger.error("there is not aadhar details ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
        }
        logger.info(" Aadhar details Send Succsssfully ");
        try {
            aadharModel.setAge(DateUtil.getCurrentAge(aadharModel.getDob()));
        } catch (Exception e) {
        }
        return ResponseEntity.ok(new ResponseModel<AadharModel>(ResponseModel.SUCCESS, aadharModel));

    }

}

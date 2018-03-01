package org.rta.citizen.common.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.TokenType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.DocTypesModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.AttachmentService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

/**
 *	@Author sohan.maurya created on Dec 12, 2016.
 */
@RestController
public class AttachmentController {

	private static final Logger logger = Logger.getLogger(AttachmentController.class);

    @Value("${jwt.header}")
    private String tokenHeader;
    
    @Value("${doc.root}")
    private String rootPath;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ApplicationService applicationService;


    @RequestMapping(value = "/attachment/doctype", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAttachmentsType(HttpServletRequest request,
    		 @RequestParam(name = "applicationno", required = false) String applicationNo) {
        String token = request.getHeader(tokenHeader);
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        List<DocTypesModel> docTypesModels = null;
        UserType userType = null;
        
        try{
        	TokenType tokenType = null;
        	String tokenTypeStr= jwtTokenUtil.getTokenType(token);
        	if(null != tokenTypeStr) {
        		tokenType  = TokenType.valueOf(tokenTypeStr);
        	}
            if(null != tokenType && tokenType == TokenType.CITIZEN){
                userType = UserType.ROLE_CITIZEN;
            } else {
                String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
                if(!StringsUtil.isNullOrEmpty(userRole)){
                    userType = UserType.valueOf(userRole);
                }
            }
        } catch(Exception ex){
            logger.error("Exception while getting user type from token ....");
        }
       try{
            docTypesModels = attachmentService.getAttachments(sessionId, applicationNo, userType );
        } catch (Exception nfe) {
            logger.info(nfe.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        return ResponseEntity.ok(new ResponseModel<List<DocTypesModel>>(ResponseModel.SUCCESS, docTypesModels));
    }
    

    @RequestMapping(value = "/status/attachment/doctype", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAttachmentsType( @RequestParam(name = "applicationno", required = true) String applicationNo) {
        
        List<DocTypesModel> docTypesModels = null;
        UserType userType = UserType.ROLE_CITIZEN;
        
        try{
        	CitizenApplicationModel model= applicationService.getCitizenApplication(applicationNo);
        	if(model.getServiceType() == ServiceType.FRESH_RC_FINANCIER){
        		 docTypesModels = attachmentService.getAttachments(null, applicationNo, userType );
        	}else{
        		  return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                          .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        	}
        } catch (Exception nfe) {
            logger.info(nfe.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        return ResponseEntity.ok(new ResponseModel<List<DocTypesModel>>(ResponseModel.SUCCESS, docTypesModels));
    }

    @RequestMapping(value = "/attachment", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateAttachment(@Valid @RequestBody AttachmentModel model, HttpServletRequest request,
    		 @RequestParam(name = "applicationno", required = false) String applicationNo) {
        if (ObjectsUtil.isNull(model)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        String token = request.getHeader(tokenHeader);
        if(StringsUtil.isNullOrEmpty(applicationNo)){
            Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
            model.setSessionId(sessionId);
        }
        try{
        	model.setUserType(jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0));
        }catch (Exception e) {}
        ResponseModel<Object> responseModel = null;
        try {
            responseModel = attachmentService.saveOrUpdate(model, applicationNo);
        } catch (DataMismatchException dme) {
            logger.error("Update attachments details:::::::::::::Exception ", dme);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        logger.info("Update attachments details:::::::::::::Successful");
        return ResponseEntity.ok(new ResponseModel<Object>(ResponseModel.SUCCESS, responseModel));
    }

    @RequestMapping(value = "/attachment/multiple", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateAttachmentMultiple(@Valid @RequestBody List<AttachmentModel> models, HttpServletRequest request,
    		@RequestParam(name = "applicationno", required = false) String applicationNo ) {
        if (ObjectsUtil.isNull(models)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        String token = request.getHeader(tokenHeader);
        if(StringsUtil.isNullOrEmpty(applicationNo)){
            Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
            models.get(0).setSessionId(sessionId);
        }
        try{
       		models.get(0).setUserType(jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0));
        }catch (Exception e) {}
        ResponseModel<Object> responseModel = null;
        try {
            responseModel = attachmentService.saveOrUpdateMultiple(models, applicationNo);
        } catch (DataMismatchException dme) {
            logger.error("Update attachments details:::::::::::::Exception ", dme);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        logger.info("Update attachments details:::::::::::::Successful");
        return ResponseEntity.ok(new ResponseModel<Object>(ResponseModel.SUCCESS, responseModel));
    }


    @RequestMapping(value = "/attachment", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAttachmentDetails(HttpServletRequest request,
            @RequestParam(name = "applicationno", required = false) String applicationNo) {

        String token = request.getHeader(tokenHeader);
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        UserType userType = null;
        try{
        	userType = UserType.valueOf(jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0));
        }catch (Exception e) {}
        List<AttachmentModel> responseModel = null;
        try {
            responseModel = attachmentService.getAttachmentDetails(sessionId, applicationNo, userType);
        } catch (NotFoundException nfe) {
            logger.error("getting attachments details:::::::::::::Exception ", nfe);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        logger.info("getting attachments details:::::::::::::Successful");
        return ResponseEntity.ok(new ResponseModel<List<AttachmentModel>>(ResponseModel.SUCCESS, responseModel));
    }


    @RequestMapping(value = "/uploaddocs", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> uploadFileHandler(HttpServletRequest request,
            @RequestParam(name = "doctype", required = true) String docType,
            @RequestParam(name = "file", required = true) MultipartFile file) {

        String token = request.getHeader(tokenHeader);
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        // if (!ObjectsUtil.isNull(isValidUpload) && isValidUpload) {
        String filePath = "/rta/citizen/" + sessionId + File.separator + docType;
        String name = file.getOriginalFilename();
        File dir = new File(rootPath + filePath);
        if (!dir.exists())
            dir.mkdirs();

        logger.info("Uploading document :" + name);
        try {
            byte[] bytes = file.getBytes();

            // Create the file on server
            File serverFile = new File(dir.getAbsolutePath() + File.separator + name.replace(" ", ""));
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
            logger.info("Doc upload completed, Location:" + serverFile.getAbsolutePath());
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "We are getting some problem"));
        }
        Map<String, String> map = new HashMap<>();
        map.put("source", filePath + File.separator + name.replace(" ", ""));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel<Object>(ResponseModel.SUCCESS, map, "Documents Uploaded successfully "));
    }

    @RequestMapping(value = "/attachment/user/{username}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUserAttachmentDetails(@PathVariable("username") String userName,
            @RequestParam(name = "docid", required = false) Integer docId) {
        logger.info("getting attachments details for user:::::::::::::Start");
        if (StringsUtil.isNullOrEmpty(userName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request, user name is not valid"));
        }
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        RegistrationServiceResponseModel<?> responseModel = null;
        try {
            responseModel = registrationService.getUserAttachmentDetails(userName, docId);
        } catch (RestClientException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("getting Error");
            logger.error("getting attachments details for user:::::::::::::Exception");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UnauthorizedException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("User Not Found");
            logger.error("getting attachments details for user:::::::::::::Exception");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        logger.info("getting attachments details for user:::::::::::::Successful");
        return ResponseEntity.ok(new ResponseModel<Object>(ResponseModel.SUCCESS, responseModel.getResponseBody()));
    }
    
    @RequestMapping(value = "/attachment/user", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUserAttachmentDetails(@Valid @RequestBody AttachmentModel model) {
        logger.info("saving attachments details for user:::::::::::::Start");
        if (ObjectsUtil.isNull(model)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        RegistrationServiceResponseModel<SaveUpdateResponse> responseModel = null;
        try {
            responseModel = registrationService.saveOrUpdateForUser(model);
        } catch (RestClientException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("getting Error");
            logger.error("saving attachments details for user:::::::::::::Exception");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UnauthorizedException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("User Not Found");
            logger.error("saving attachments details for user:::::::::::::Exception");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        logger.info("saving attachments details for user:::::::::::::Successful");
        return ResponseEntity.ok(new ResponseModel<SaveUpdateResponse>(ResponseModel.SUCCESS, responseModel.getResponseBody()));
    }
    
    @RequestMapping(value = "/attachment/multiple/user", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> multipleUserAttachmentDetails(@Valid @RequestBody List<AttachmentModel> models) {
        logger.info("saving attachments details for user:::::::::::::Start");
        if (ObjectsUtil.isNull(models)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request"));
        }
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        RegistrationServiceResponseModel<SaveUpdateResponse> responseModel = null;
        try {
            responseModel = registrationService.multipleSaveOrUpdateForUser(models);
        } catch (RestClientException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("getting Error");
            logger.error("saving attachments details for user:::::::::::::Exception");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UnauthorizedException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("User Not Found");
            logger.error("saving attachments details for user:::::::::::::Exception");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        logger.info("saving attachments details for user:::::::::::::Successful");
        return ResponseEntity.ok(new ResponseModel<SaveUpdateResponse>(ResponseModel.SUCCESS, responseModel.getResponseBody()));
    }

}

/**
 * 
 */
package org.rta.citizen.permit.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.PermitDetailsType;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.PermitDetailsModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.PermitTypeModel;
import org.rta.citizen.common.model.PermitTypeVehicleClassModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.permit.model.PermitAuthorizationCardModel;
import org.rta.citizen.permit.model.PermitCodeDescModel;
import org.rta.citizen.permit.service.PermitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author arun.verma
 *
 */
@RestController
public class PermitController {
    
	private static final Logger logger = Logger.getLogger(PermitController.class);
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private PermitService permitService;
    
    
    
    @RequestMapping(value = "/permittype", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPermitType(@RequestParam(value = "trnumber", required = true) String trNumber) {
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        RegistrationServiceResponseModel<PermitTypeVehicleClassModel> permitResponse = null;
        try {
            permitResponse = registrationService.getPermitTypeByTr(trNumber);
        } catch (RestClientException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("getting Error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UnauthorizedException e) {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("User Not Found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        if(permitResponse.getHttpStatus().equals(HttpStatus.OK)){
            return ResponseEntity.ok(new ResponseModel<PermitTypeVehicleClassModel>(ResponseModel.SUCCESS, permitResponse.getResponseBody()));
        } else {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(permitResponse.getHttpStatus().value());
            response.setMessage("Unable to get Permit Types ....");
            return ResponseEntity.status(permitResponse.getHttpStatus()).body(response);
        }
    }
    
    @RequestMapping(value = "/primarypermits/cov/{cov}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPermitTypesByCov(@PathVariable("cov") String cov) throws UnauthorizedException {
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        RegistrationServiceResponseModel<List<PermitTypeModel>> permitResponse = null;
        permitResponse = registrationService.getPermitType(cov);
        if(permitResponse.getHttpStatus().equals(HttpStatus.OK)){
            return ResponseEntity.ok(new ResponseModel<List<PermitTypeModel>>(ResponseModel.SUCCESS, permitResponse.getResponseBody()));
        } else {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(permitResponse.getHttpStatus().value());
            response.setMessage("Unable to get Permit Types ....");
            return ResponseEntity.status(permitResponse.getHttpStatus()).body(response);
        }
    }
    
    @RequestMapping(path = "/goodsdetails/cov/{cov}/permit/{permit_type}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getGoodsDetails(@PathVariable("cov") String cov, @PathVariable("permit_type") String permitType) throws UnauthorizedException {
        logger.info("getGoodsDetails start for cov :" + cov + " permit: " + permitType);
        RegistrationServiceResponseModel<List<PermitCodeDescModel>> goodsListRes = registrationService.getGoodsRouteCondnsForPrimaryPermit(PermitDetailsType.GOODS, cov, permitType);
        ResponseModel<List<PermitCodeDescModel>> res = new ResponseModel<>();
        if(goodsListRes.getHttpStatus().equals(HttpStatus.OK)){
            res.setStatus(ResponseModel.SUCCESS);
            res.setData(goodsListRes.getResponseBody());
        } else {
            logger.error("Error in getting PermitGoodsModelList Status: " + goodsListRes.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setStatusCode(goodsListRes.getHttpStatus().value());
        }
        logger.info(" getGoodsDetails end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/arearouts/cov/{cov}/permit/{permit_type}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAreaRouts(@PathVariable("cov") String cov, @PathVariable("permit_type") String permitType) throws UnauthorizedException {
        logger.info("getAreaRouts start for cov :" + cov + " permit: " + permitType);
        RegistrationServiceResponseModel<List<PermitCodeDescModel>> routeListRes = registrationService.getGoodsRouteCondnsForPrimaryPermit(PermitDetailsType.ROUTE, cov, permitType);
        ResponseModel<List<PermitCodeDescModel>> res = new ResponseModel<>();
        if(routeListRes.getHttpStatus().equals(HttpStatus.OK)){
            res.setStatus(ResponseModel.SUCCESS);
            res.setData(routeListRes.getResponseBody());
        } else {
            logger.error("Error in getting PermitRouteModel Status: " + routeListRes.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setStatusCode(routeListRes.getHttpStatus().value());
        }
        logger.info(" getAreaRouts end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/permitconditions/cov/{cov}/permit/{permit_type}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPermitConditions(@PathVariable("cov") String cov, @PathVariable("permit_type") String permitType) throws UnauthorizedException {
        logger.info("getPermitConditions start for cov :" + cov + " permit: " + permitType);
        RegistrationServiceResponseModel<List<PermitCodeDescModel>> condtionListRes = registrationService.getGoodsRouteCondnsForPrimaryPermit(PermitDetailsType.CONDITION, cov, permitType);
        ResponseModel<List<PermitCodeDescModel>> res = new ResponseModel<>();
        if(condtionListRes.getHttpStatus().equals(HttpStatus.OK)){
            res.setStatus(ResponseModel.SUCCESS);
            res.setData(condtionListRes.getResponseBody());
        } else {
            logger.error("Error in getting PermitConditionsModel Status: " + condtionListRes.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setStatusCode(condtionListRes.getHttpStatus().value());
        }
        logger.info(" getPermitConditions end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/temppermit/{type}/permit/{permit_type}/temp/{temp_type}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getRouteGoodsConditions(@PathVariable("type") PermitDetailsType permitDetailsType, @PathVariable("permit_type") String permitType,
            @PathVariable("temp_type") String tempPermitType) throws UnauthorizedException {
        logger.info("getRouteGoodsConditions start for permit_type :" + permitType + " temp_type: " + tempPermitType);
        RegistrationServiceResponseModel<List<PermitCodeDescModel>> condtionListRes = registrationService.getRouteGoodsConditionsForTempPermit(permitDetailsType, permitType, tempPermitType);
        ResponseModel<List<PermitCodeDescModel>> res = new ResponseModel<>();
        if(condtionListRes.getHttpStatus().equals(HttpStatus.OK)){
            res.setStatus(ResponseModel.SUCCESS);
            res.setData(condtionListRes.getResponseBody());
        } else {
            logger.error("Error in getting PermitConditionsModel Status: " + condtionListRes.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setStatusCode(condtionListRes.getHttpStatus().value());
        }
        logger.info(" getPermitConditions end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/permit/stagecarrierno/cov/{cov}/permit/{permit_type}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getNumberOfStageCarrige(@RequestHeader("Authorization") String token,
            @RequestParam(name = "appnumber", required = false) String appNumber) throws UnauthorizedException {
        logger.info("getNumberOfStageCarrige Start ....");
        Long sessionId = getSession(token, appNumber);
        Map<String, Integer> map = new HashMap<String, Integer>();
        /**
         * TODO jab ye APSRTC login banega tab DB banega toh waha se value ayegi 
         */
        map.put("count", 10);
        return ResponseEntity.ok(new ResponseModel<Map>(ResponseModel.SUCCESS, map));
    }
    
    private Long getSession(String token, String appNumber) {
        if (!StringsUtil.isNullOrEmpty(appNumber)) {
            return applicationService.getSession(appNumber).getSessionId();
        } else {
            return jwtTokenUtil.getSessionIdFromToken(token);
        }
    }
    
    @RequestMapping(path = "/permit/temporarypermits", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getTempPermits(@RequestHeader("Authorization") String token,
            @RequestParam(name = "appnumber", required = false) String appNumber) throws UnauthorizedException {
        Long sessionId = getSession(token, appNumber);
        logger.info("getTempPermits start for sessionId :" + sessionId);
        ResponseModel<?> res = permitService.getTempPermits(sessionId);
        logger.info(" getTempPermits end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/permit/authorizationcard/{app_no}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPermitAuthorizationCard(@PathVariable("app_no") String appNo) throws UnauthorizedException, NotFoundException {
        logger.info("getPermitAuthorizationCard start for app no :" + appNo);
        ResponseModel<PermitAuthorizationCardModel> permitAuthCardModel = permitService.getPermitAuthCardDetails(appNo);
        
        FeeModel feeModel=permitService.getpermitFeesDetails(appNo);
        if (permitAuthCardModel.getStatus().equalsIgnoreCase(ResponseModel.SUCCESS)&& !ObjectsUtil.isNull(feeModel)) {
        	permitAuthCardModel.getData().setFeeModel(feeModel);
            return ResponseEntity.ok(permitAuthCardModel.getData());
        }else{
        logger.info("permit auth card details not found for application number : " + appNo);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        		.body(new ResponseModel<PermitAuthorizationCardModel>(ResponseModel.FAILED,null,"Not Found "));
        }
    }
    
    @RequestMapping(path = "/pukkatemp/permittype/{pr_number}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getSelectedPukkaTempPermit(@RequestHeader("Authorization") String token, @PathVariable("pr_number") String prNumber) throws UnauthorizedException {
        Long sessionId = getSession(token, null);
        logger.info("getSelectedPukkaTempPermit start for pr no :" + prNumber);
        ResponseModel<?> res = permitService.getSelectedPukkaTempPermit(prNumber, sessionId);
        logger.info(" getSelectedPukkaTempPermit end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/permit/certificate/app/{app_no}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPermitCertificate(@PathVariable("app_no") String appNo) throws UnauthorizedException, JsonProcessingException, IOException {
        logger.info("getPermitCertificate start for app no :" + appNo);
        ResponseModel<?> res = permitService.getPermitCertificate(appNo);
        logger.info(" getPermitCertificate end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/permit/certificate/pr/{pr_no}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPermitCertificateByPrNumber(@RequestHeader("Authorization") String token, @PathVariable("pr_no") String prNo) throws UnauthorizedException, JsonProcessingException, IOException {
        logger.info("getPermitCertificate start for pr no :" + prNo);
        Long sessionId = getSession(token, null);
        ResponseModel<?> res = permitService.getPermitCertificateByPr(prNo, sessionId);
        logger.info(" getSelectedPukkaTempPermit end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/permit/pr/{pr_no}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getPermitDetailByPrNumberAndPermitType(@RequestHeader("Authorization") String token,
			@PathVariable("pr_no") String prNo, @RequestParam(name="permit_type", required=false) String permitType)
			throws UnauthorizedException, JsonProcessingException, IOException {
		logger.info("getPermitDetailByPrNumberAndPermitType start for pr no :" + prNo);
		PermitDetailsModel res = permitService.getPermitDetails(prNo, permitType);
		logger.info(" getPermitDetailByPrNumberAndPermitType end  ");
		return ResponseEntity.ok(res);
	}
    
    
    //Get all permits on a pr
    @RequestMapping(path = "/permit/allpermits", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAllPermitDetails(@RequestHeader("Authorization") String token) throws UnauthorizedException, JsonProcessingException, IOException {
        Long sessionId = getSession(token, null);
        List<PermitHeaderModel> res = permitService.getAllPermits(sessionId);
        logger.info(" getSelectedPukkaTempPermit end  ");
        return ResponseEntity.ok(res);
    }
    

}

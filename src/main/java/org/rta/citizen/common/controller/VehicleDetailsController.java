/**
 * 
 */
package org.rta.citizen.common.controller;

import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.PermitTypeVehicleClassModel;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleClassDescModel;
import org.rta.citizen.common.model.payment.TaxTypeModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
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

/**
 * @author arun.verma
 *
 */

@RestController
public class VehicleDetailsController {
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/cov/pr/{pr_number}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPermitTypes(@PathVariable("pr_number") String prNumber) throws UnauthorizedException {
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        RegistrationServiceResponseModel<VehicleClassDescModel> covResponse = null;
        covResponse = registrationService.getCovDetails(prNumber);
        if(covResponse.getHttpStatus().equals(HttpStatus.OK)){
            return ResponseEntity.ok(new ResponseModel<VehicleClassDescModel>(ResponseModel.SUCCESS, covResponse.getResponseBody()));
        } else {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(covResponse.getHttpStatus().value());
            response.setMessage("Unable to get COV details ....");
            return ResponseEntity.status(covResponse.getHttpStatus()).body(response);
        }
    }
    
    private Long getSession(String token, String appNumber) {
        if (!StringsUtil.isNullOrEmpty(appNumber)) {
            return applicationService.getSession(appNumber).getSessionId();
        } else {
            return jwtTokenUtil.getSessionIdFromToken(token);
        }
    }
    
    @RequestMapping(value = "/vehicle/regcategory", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getRegCategory(@RequestParam(name="appNumber", required = false) String appNumber,
    		@RequestHeader("Authorization") String authToken) throws UnauthorizedException {
    	Long sessionId = getSession(authToken, appNumber);
        if (ObjectsUtil.isNull(sessionId)) {
            throw new IllegalArgumentException("no session found for this application number");
        }
        ResponseModel<Object> response = new ResponseModel<Object>(ResponseModel.FAILED);
        
        RegistrationServiceResponseModel<RegistrationCategoryModel> regCategoryResponse = null;
        
        regCategoryResponse = registrationService.getRegCategoryByRcId(applicationService.getVehicleRcId(sessionId));
        if(regCategoryResponse.getHttpStatus().equals(HttpStatus.OK)){
            return ResponseEntity.ok(new ResponseModel<RegistrationCategoryModel>(ResponseModel.SUCCESS, regCategoryResponse.getResponseBody()));
        } else {
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(regCategoryResponse.getHttpStatus().value());
            response.setMessage("Unable to get registation category details ....");
            return ResponseEntity.status(regCategoryResponse.getHttpStatus()).body(response);
        }
    }
}

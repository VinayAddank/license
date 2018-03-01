/**
 * 
 */
package org.rta.citizen.hpa.controller;

import java.util.List;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ApplicationNotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.FinancerModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.hpa.service.HPAService;
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
 * @author arun.verma
 *
 */
@RestController
@RequestMapping(value = "/hpa")
public class HPAController {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private HPAService hPAService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @RequestMapping(value = "/approve/financier/app/{app_no}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> approveFinancer(@RequestBody FinancerModel financerModel, @PathVariable("app_no") String appNo) throws UnauthorizedException{
        ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>();
        if(ObjectsUtil.isNull(financerModel.getFinancerId())){
            response = new ResponseModel<>(ResponseModel.FAILED, null, "Financier Id Requiered !!!");
            return ResponseEntity.ok(response);
        }
        CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNo);
        if(ObjectsUtil.isNull(citizenApp)){
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("Application Not Found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        response = hPAService.approveFinancier(appNo, citizenApp.getSessionId(), financerModel);
        return ResponseEntity.ok(response);
    }
    
    @RequestMapping(value = "/finance/details/user/{userId}/app/{app_no}/action/{status}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> submitFinanceDetails(@RequestHeader("Authorization") String token, @PathVariable("userId") String userName, @PathVariable("app_no") String appNo,
            @PathVariable("status") String status, @RequestBody CommentModel comment) throws ApplicationNotFoundException{
        Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
        String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
        ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>();
        Status status1 = Status.getStatus(status);
        if(ObjectsUtil.isNull(status1) || !(status1 == Status.APPROVED || status1 == Status.REJECTED)){
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Invalid Status : " + status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        CitizenApplicationModel citizenApp = applicationService.getCitizenApplication(appNo);
        if(ObjectsUtil.isNull(citizenApp)){
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("Application Not Found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        response = hPAService.submitFinanceDetails(appNo, citizenApp.getSessionId(), userName, citizenApp.getAppId(), userId, userRole, status1, citizenApp.getIteration(), comment);
        return ResponseEntity.ok(response);
    }
}

/**
 * 
 */
package org.rta.citizen.fitness.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.fitness.cfx.model.CFXNoticeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author arun.verma
 *
 */
@Service
public class FitnessServiceImpl implements FitnessService{

	private static final Logger logger = Logger.getLogger(FitnessServiceImpl.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ApplicationDAO applicationDAO;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
    
    @Override
    @Transactional
    public ResponseModel<Map<String, Object>> getFitnessCertificate(String appNo) throws UnauthorizedException {
        ResponseModel<Map<String, Object>> res = new ResponseModel<>();
        Map<String, Object> map = new HashMap<>();
        ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
        if (ObjectsUtil.isNull(appEntity)) {
            res.setStatus(ResponseModel.FAILED);
            res.setMessage("Application Not Found.");
            res.setStatusCode(HttpStatus.NOT_FOUND.value());
            return res;
        }
        logger.info("Calling registration for getFitnessCertificate app : " + appNo);
        UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
        Long mviUserId = null;
        ApplicationApprovalHistoryEntity history = applicationApprovalHistoryDAO.getRoleLastAction(UserType.ROLE_MVI.name(), appEntity.getApplicationId(), appEntity.getIteration());
        if(!ObjectsUtil.isNull(history)){
            mviUserId = history.getRtaUserId();
        }
        RegistrationServiceResponseModel<FcDetailsModel> permitResp = registrationService
                .getFitnessCertificate(userSessionEntity.getVehicleRcId(), mviUserId);
        if (permitResp.getHttpStatus().equals(HttpStatus.OK)) {
            map.put("fitnessDetails", permitResp.getResponseBody());
        } else {
            logger.error("Some Error Occured. while calling registration getFitnessCertificate. Satus : "
                    + permitResp.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setMessage("Some Error Occured.");
            res.setStatusCode(permitResp.getHttpStatus().value());
            return res;
        }
        logger.info("Calling registration for vehicle details app : " + appNo);
        RegistrationServiceResponseModel<VehicleDetailsRequestModel> vehicleResp = registrationService
                .getVehicleDetails(userSessionEntity.getVehicleRcId());
        if (vehicleResp.getHttpStatus().equals(HttpStatus.OK)) {
            map.put("vehicleDetails", vehicleResp.getResponseBody());
        } else {
            logger.error("Some Error Occured. while calling registration vehicle details. Satus : "
                    + vehicleResp.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setMessage("Some Error Occured.");
            res.setStatusCode(vehicleResp.getHttpStatus().value());
            return res;
        }
        logger.info("Calling registration for customer details app : " + appNo);
        RegistrationServiceResponseModel<CustomerDetailsRequestModel> customerResp = registrationService
                .getCustomerDetails(userSessionEntity.getVehicleRcId());
        if (customerResp.getHttpStatus().equals(HttpStatus.OK)) {
            map.put("customerDetails", customerResp.getResponseBody());
        } else {
            logger.error("Some Error Occured. while calling registration customer details. Satus : "
                    + customerResp.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setMessage("Some Error Occured.");
            res.setStatusCode(customerResp.getHttpStatus().value());
            return res;
        }
        res.setStatus(ResponseModel.SUCCESS);
        res.setData(map);
        return res;
    }
    
    @Override
    @Transactional
    public CFXNoticeModel getFitnessCFXNote(String appNo) {
        ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
        UserSessionEntity userSession = appEntity.getLoginHistory();
        RegistrationServiceResponseModel<CFXNoticeModel> model = null;
        try {
            model = registrationService.getFCFXNote(userSession.getUniqueKey());
        } catch (UnauthorizedException e) {
            logger.error("error when getting fcfx note for application number : " + appNo);
        }
        if (model != null && model.getHttpStatus() == HttpStatus.OK) {
            return model.getResponseBody();
        }
        return null;
    }
}

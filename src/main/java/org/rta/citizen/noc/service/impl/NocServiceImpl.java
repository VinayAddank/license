package org.rta.citizen.noc.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.noc.service.NocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *	@Author sohan.maurya created on Jan 4, 2017.
 */

@Repository("nocService")
public class NocServiceImpl implements NocService {

	private static final Logger logger = Logger.getLogger(NocServiceImpl.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ApplicationFormDataDAO applicationFormDataDAO;

    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
    @Autowired
    private ApplicationDAO applicationDAO;
    
    @Transactional
    @Override
    public ResponseModel<String> saveOrUpdateNocDetails(Long vehicleRcId, Long applicationId, String prNumber) {

        try{
            ApplicationFormDataEntity entity =  applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.NOC_FORM.getLabel());
            ObjectMapper mapper = new ObjectMapper();
            NocDetails model = mapper.readValue(entity.getFormData(), NocDetails.class);
            model.setServiceCode(ServiceType.NOC_ISSUE.getCode());
            
            ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
            List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
            List<UserActionModel> actionModelList = new ArrayList<>();
            for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
            	UserActionModel actionModel = new UserActionModel();
            	actionModel.setUserId(String.valueOf(history.getRtaUserId()));
            	actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
            	actionModel.setUserAction(Status.getLabel(history.getStatus()));
            	actionModelList.add(actionModel);
            }
            model.setActionModelList(actionModelList);
            
            if (!ObjectsUtil.isNull(vehicleRcId)) {
                model.setVehicleRcId(vehicleRcId);
            } else {
                RegistrationServiceResponseModel<ApplicationModel> registrationServiceResponseModel= registrationService.getPRDetails(prNumber);
                model.setVehicleRcId(registrationServiceResponseModel.getResponseBody().getVehicleRcId());
            }
            model.setAppliedDate(entity.getCreatedOn());
            model.setIssueDate(DateUtil.toCurrentUTCTimeStamp());
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForNocDetails(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
        }catch(Exception ex){
            logger.error("Getting error in update Or save in NocDetails");
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
    }

    @Transactional
    @Override
    public ResponseModel<String> saveOrUpdateCancellationNocDetails(Long vehicleRcId, Long applicationId, String prNumber) {
        try {
            ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.CC_FORM.getLabel());
            ObjectMapper mapper = new ObjectMapper();
            NocDetails model = mapper.readValue(entity.getFormData(), NocDetails.class);
            model.setServiceCode(ServiceType.NOC_CANCELLATION.getCode());
            
            ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
            List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
            List<UserActionModel> actionModelList = new ArrayList<>();
            for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
            	UserActionModel actionModel = new UserActionModel();
            	actionModel.setUserId(String.valueOf(history.getRtaUserId()));
            	actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
            	actionModel.setUserAction(Status.getLabel(history.getStatus()));
            	actionModelList.add(actionModel);
            }
            model.setActionModelList(actionModelList);
            
            if (!ObjectsUtil.isNull(vehicleRcId)) {
                model.setVehicleRcId(vehicleRcId);
            } else {
                RegistrationServiceResponseModel<ApplicationModel> registrationServiceResponseModel= registrationService.getPRDetails(prNumber);
                model.setVehicleRcId(registrationServiceResponseModel.getResponseBody().getVehicleRcId());
            }
            model.setCancellationDate(DateUtil.toCurrentUTCTimeStamp());
            model.setStatus(Boolean.FALSE);
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForNocDetails(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
        } catch (Exception ex) {
            logger.error("Getting error in update Or save in cancellation of Noc Details");
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
    }



}

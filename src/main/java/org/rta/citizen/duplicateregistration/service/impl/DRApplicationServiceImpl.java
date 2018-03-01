package org.rta.citizen.duplicateregistration.service.impl;

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
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.duplicateregistration.model.DuplicateRegistrationModel;
import org.rta.citizen.duplicateregistration.service.DRApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *	@Author sohan.maurya created on Dec 20, 2016.
 */

@Service
public class DRApplicationServiceImpl implements DRApplicationService {

	private static final Logger logger = Logger.getLogger(DRApplicationServiceImpl.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ApplicationFormDataDAO applicationFormDataDAO;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
        
    @Autowired
    private ApplicationDAO applicationDAO;

	@Override
	@Transactional
	public ResponseModel<String> saveOrUpdateDuplicateRegistration(Long applicationId, String prNumber) {
		try {
            ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.DR_FORM.getLabel());
            ObjectMapper mapper = new ObjectMapper();
            DuplicateRegistrationModel model = mapper.readValue(entity.getFormData(), DuplicateRegistrationModel.class);
            
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
            
            model.setPrNumber(prNumber);
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForDuplicateRegistrationDetails(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
        } catch (Exception ex) {
            logger.error("Getting error in update Or save in Duplicate Registration details");
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
	}

}

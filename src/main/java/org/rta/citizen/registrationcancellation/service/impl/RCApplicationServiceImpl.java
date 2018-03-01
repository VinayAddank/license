package org.rta.citizen.registrationcancellation.service.impl;

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
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.registrationcancellation.service.RCApplicationService;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RCApplicationServiceImpl implements  RCApplicationService{
	
	private static final Logger logger = Logger.getLogger(RCApplicationServiceImpl.class);
	
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
	public ResponseModel<String> saveOrUpdateRegistrationCanellation(String prNumber, Long applicationId) {

		try{	
			ApplicationFormDataEntity enFormDataEntity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.RC_FORM.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			CommonServiceModel model = mapper.readValue(enFormDataEntity.getFormData(), CommonServiceModel.class);
	        model.setServiceType(ServiceType.REGISTRATION_CANCELLATION); 
	        
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
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForCitizenCommonSerives(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
	        } catch (Exception ex) {
	            logger.error("Getting error in update Or save in Reassignment of Vehicle");
	        }
		return null;
	}

}

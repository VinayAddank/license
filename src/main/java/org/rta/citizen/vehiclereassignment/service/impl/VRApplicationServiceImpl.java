package org.rta.citizen.vehiclereassignment.service.impl;

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
import org.rta.citizen.vehiclereassignment.model.VehicleReassignmentModel;
import org.rta.citizen.vehiclereassignment.service.VRApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VRApplicationServiceImpl implements VRApplicationService{
	
	private static final Logger logger = Logger.getLogger(VRApplicationServiceImpl.class);
	
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
	public ResponseModel<String> saveOrUpdateVehicleReassignment(Long applicationId, String prNumber) {
		 try{	
			ApplicationFormDataEntity enFormDataEntity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.VR_FORM.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			VehicleReassignmentModel model = mapper.readValue(enFormDataEntity.getFormData(), VehicleReassignmentModel.class);
			model.setPrNumber(prNumber);
			
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
			
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForVehicleReassignmentSerives(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
	        } catch (Exception ex) {
	            logger.error("Getting error in update Or save in Reassignment of Vehicle");
	        }
	 return new ResponseModel<String>(ResponseModel.FAILED);
	}

}

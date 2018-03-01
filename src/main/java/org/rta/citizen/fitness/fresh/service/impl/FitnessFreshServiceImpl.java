package org.rta.citizen.fitness.fresh.service.impl;

import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.FitnessDetailsModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.fitness.fresh.service.FitnessFreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FitnessFreshServiceImpl implements  FitnessFreshService {

	@Autowired
	private ApplicationDAO applicationDAO;
	
	@Autowired 
	private RegistrationService registrationService;
	
	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
	
	@Override
	public FitnessDetailsModel getFitnessDetails(String applicationNumber) throws NotFoundException {
		Long vehicleRcId = null;
		UserSessionEntity userSessionEntity = applicationDAO.getApplication(applicationNumber).getLoginHistory();
		vehicleRcId = userSessionEntity.getVehicleRcId();
		FitnessDetailsModel model = null;
		try{
			if(ObjectsUtil.isNull(vehicleRcId)){
				RegistrationServiceResponseModel<ApplicationModel> registrationServiceResponseModel = 
						registrationService.getPRDetails(userSessionEntity.getUniqueKey());
				vehicleRcId =registrationServiceResponseModel.getResponseBody().getVehicleRcId();
			}
			ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(userSessionEntity.getSessionId());
			Long mviUserId = null;
	        ApplicationApprovalHistoryEntity history = applicationApprovalHistoryDAO.getRoleLastAction(UserType.ROLE_MVI.name(), appEntity.getApplicationId(), appEntity.getIteration());
	        if(!ObjectsUtil.isNull(history)){
	            mviUserId = history.getRtaUserId();
	        }
			RegistrationServiceResponseModel<FitnessDetailsModel> responseModel = 
					registrationService.getFitnessDetails(vehicleRcId, mviUserId);
			if(responseModel.getHttpStatus() == HttpStatus.OK){
				model = responseModel.getResponseBody();
			}
		}catch (Exception e) {
			throw new NotFoundException();
		}
		return model;
	}

}

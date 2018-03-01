package org.rta.citizen.vehiclealteration.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.model.VehicleAlterationUpdateModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.VehicleClassDescModel;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.impl.ApplicationFormDataServiceImpl;
import org.rta.citizen.common.service.payment.TaxFeeService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.vehiclealteration.service.VehicleAlterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *	@Author sohan.maurya created on Jan 6, 2017.
 */

@Service("vehicleAlterationService")
public class VehicleAlterationServiceImpl implements VehicleAlterationService {

	private static final Logger logger = Logger.getLogger(ApplicationFormDataServiceImpl.class);

    @Autowired
    private ApplicationFormDataService applicationFormDataService;
    
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserSessionDAO userSessionDAO;
    
    @Autowired
    private ApplicationFormDataDAO applicationFormDataDAO;

    @Autowired
    private TaxFeeService taxFeeService;
    
    @Autowired
    private ApplicationDAO applicationDAO;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
    @Transactional
    @Override
    public ResponseModel<Boolean> updateDataAfterPayments(Long sessionId) {

        try{
            ResponseModel<ApplicationFormDataModel> response = applicationFormDataService
                    .getApplicationFormDataBySessionId(sessionId, FormCodeType.VA_FORM.getLabel());
            ApplicationFormDataModel form = response.getData();
            ObjectMapper mapper = new ObjectMapper();
            VehicleBodyModel model = mapper.readValue(form.getFormData(), VehicleBodyModel.class);
            ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
	        model.setVehicleRcId(userSessionDAO.getEntity(UserSessionEntity.class, sessionId).getVehicleRcId());
            model.setApplicationNumber(appEntity.getApplicationNumber());
			RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService
					.updateVehicleAlterationForBodyBuilder(model);
			if (regResponse.getHttpStatus() == HttpStatus.OK) {
				if (regResponse.getResponseBody().getStatus().equals(SaveUpdateResponse.FAILURE)) {
					return new ResponseModel<Boolean>(ResponseModel.FAILED, false);
				}
				return new ResponseModel<Boolean>(ResponseModel.SUCCESS, true);
			}
        }catch(Exception ex){
            logger.error("Getting error in update Data After Payments", ex);
            return new ResponseModel<Boolean>(ResponseModel.FAILED, false);
        }
        return new ResponseModel<Boolean>(ResponseModel.SUCCESS, false);
    }

	@Override
	@Transactional
	public ResponseModel<String> saveOrUpdateVehicleAlteration(String prNumber, Long applicationId) {

		try {
			ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(applicationId,
					FormCodeType.VA_FORM.getLabel());
			ObjectMapper mapper = new ObjectMapper();
			VehicleBodyModel model = mapper.readValue(entity.getFormData(), VehicleBodyModel.class);
			ApplicationEntity appEntity = applicationDAO.getEntity(ApplicationEntity.class, applicationId);
			model.setVehicleRcId(appEntity.getLoginHistory().getVehicleRcId());
			VehicleAlterationUpdateModel vehicleAlterationUpdateModel=new VehicleAlterationUpdateModel();
			vehicleAlterationUpdateModel.setVehicleBodyModel(model);
			vehicleAlterationUpdateModel.setDifferentialTaxFeeModel(taxFeeService.saveOrUpdateDifferentialTaxFee(null, applicationId));
			
			List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO
					.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
			List<UserActionModel> actionModelList = new ArrayList<>();
			for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
				UserActionModel actionModel = new UserActionModel();
				actionModel.setUserId(String.valueOf(history.getRtaUserId()));
				actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
				actionModel.setUserAction(Status.getLabel(history.getStatus()));
				actionModelList.add(actionModel);
			}
			vehicleAlterationUpdateModel.setUserActionModel(actionModelList);
			RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService
					.saveOrUpdateForVehicleAlterationDetails(vehicleAlterationUpdateModel);
			if (regResponse.getHttpStatus() == HttpStatus.OK) {
				return new ResponseModel<String>(ResponseModel.SUCCESS);
			}
		} catch (Exception ex) {
			logger.error("Getting error in update Or save in Change of Address details");
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	@Transactional
	@Override
	public ResponseModel<RegistrationCategoryModel> getVehicleType(String prNumber) {
		RegistrationCategoryModel registrationCategoryModel = null;
		try {
			RegistrationServiceResponseModel<ApplicationModel> result = registrationService.getPRDetails(prNumber);
			registrationCategoryModel = result.getResponseBody().getRegistrationCategory();

		} catch (UnauthorizedException e) {
			logger.error("unauthorized");
			e.printStackTrace();
		}
		return new ResponseModel<RegistrationCategoryModel>(ResponseModel.SUCCESS, registrationCategoryModel);
	}

	@Override
	public ResponseModel<String> getFuelType(String prNumber) {
		String fuelType=null;
		try {
			RegistrationServiceResponseModel<ApplicationModel> result=	registrationService.getPRDetails(prNumber);
			fuelType=	 result.getResponseBody().getVehicleModel().getFuelUsed();
		 
		} catch (UnauthorizedException e) {
			 logger.error("unauthorized");
			e.printStackTrace();
		}
		return new ResponseModel<String>(ResponseModel.SUCCESS,fuelType);
	}

	/**
	 * @author prabhat.singh
	 * @description get the list of cov for alteration of vehicle.
	 * @param prNumber
	 * @return List of VehicleClassDescModel
	 */
	@Override
	public List<VehicleClassDescModel> getAlterationCovList(String prNumber,String regCatCode) {
		try {
			RegistrationServiceResponseModel<List<VehicleClassDescModel>> result = registrationService
					.getAlterationCovList(prNumber,regCatCode);
			if (result.getHttpStatus() == HttpStatus.OK) {
				return result.getResponseBody();
			}
		} catch (UnauthorizedException e) {
			logger.error("unauthorized");
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * @author prabhat.singh
	 * @description get the list of body type for alteration of vehicle.
	 * @return List of  body type
	 */
	@Override
	public List<String> getBodyTypeList() {
		try {
			RegistrationServiceResponseModel<List<String>> result = registrationService.getBodyTypeList();
			if (result.getHttpStatus() == HttpStatus.OK) {
				return result.getResponseBody();
			}
		} catch (UnauthorizedException e) {
			logger.error("unauthorized");
			e.printStackTrace();
		}
		return null;
	}
    
}

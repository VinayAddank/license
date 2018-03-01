package org.rta.citizen.stoppagetax.service.impl;

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
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.stoppagetax.dao.VehicleInspectionDAO;
import org.rta.citizen.stoppagetax.entity.VehicleInspectionEntity;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxReportModel;
import org.rta.citizen.stoppagetax.service.StoppageTaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author sohan.maurya created on Jul 5, 2017
 *
 */

@Service
public class StoppageTaxServiceImpl implements StoppageTaxService{


	private static final Logger logger = Logger.getLogger(StoppageTaxServiceImpl.class);
	
	@Value("${stoppage.tax.inspection.days}")
	private int inspectionDays;

    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private ApplicationFormDataDAO applicationFormDataDAO;
    
    @Autowired
    private ApplicationDAO applicationDAO;
    
    @Autowired
    private VehicleInspectionDAO vehicleInspectionDAO;
    
    @Autowired
    private UserSessionDAO userSessionDAO;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
	@Override
	@Transactional
	public ResponseModel<String> saveOrUpdateStoppageTax(String prNumber, Long applicationId) {
		try{
			List<ApplicationFormDataModel> models = new ArrayList<ApplicationFormDataModel>();
			List<ApplicationFormDataEntity> entities = applicationFormDataDAO.getAllApplicationFormData(applicationId);
			ApplicationFormDataModel model = null;
			for(ApplicationFormDataEntity entity : entities){
				model = new ApplicationFormDataModel();
				model.setApplicaionNumber(entity.getApplicationEntity().getApplicationNumber());
				model.setApplicationId(applicationId);
				model.setFormCode(entity.getFormCode());
				model.setFormData(entity.getFormData());
				models.add(model);
			}
			RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveStoppageTaxDetails(models, prNumber);
	        if (regResponse.getHttpStatus() == HttpStatus.OK) {
	        	List<String> userRoles = new ArrayList<>();
	        	userRoles.add(UserType.ROLE_MVI.toString());
	        	List<ApplicationApprovalHistoryEntity> approvalHistoryEntities = applicationApprovalHistoryDAO
	    				.getApprovalHistories(applicationId, Status.APPROVED, userRoles);
	        	VehicleInspectionEntity inspectionInsertion = new VehicleInspectionEntity();
	        	inspectionInsertion.setApplicationId(entities.get(0).getApplicationEntity());
	            if(!ObjectsUtil.isNull(approvalHistoryEntities) && approvalHistoryEntities.size() > 0){
	            	inspectionInsertion.setUserId(approvalHistoryEntities.get(0).getRtaUserId());
	            }
	            inspectionInsertion.setInspectionStatus(Status.OPEN.getValue());
	            inspectionInsertion.setScheduleInspectionDate(DateUtil.addDays(DateUtil.toCurrentUTCTimeStamp(), inspectionDays));
	            inspectionInsertion.setRevocationStatus(Status.PENDING.getValue());
	            vehicleInspectionDAO.saveOrUpdate(inspectionInsertion);
	            return new ResponseModel<String>(ResponseModel.SUCCESS);
	        }
	    } catch (Exception ex) {
	        logger.error("Getting error in update Or save in Stoppage tax details :::: Application Id = "+applicationId);
	    }
	    return new ResponseModel<String>(ResponseModel.FAILED);
	}
	
	@Override
	@Transactional
	public ResponseModel<String> stoppageTaxReportSync(StoppageTaxReportModel model, String userName, Long userId) {
		try{
			ApplicationEntity applicationEntity = applicationDAO.getApplication(model.getApplicationNo());
			RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveStoppageTaxReportDetails(model, applicationEntity.getLoginHistory().getUniqueKey(), userName);
	        if (regResponse.getHttpStatus() == HttpStatus.OK) {
	        	if(ServiceType.STOPPAGE_TAX.getCode().equalsIgnoreCase(applicationEntity.getServiceCode())){
	        		VehicleInspectionEntity inspectionUpdation = vehicleInspectionDAO.getVehicleInspection(applicationEntity.getApplicationId(), DateUtil.getTimeStampTonight());
		            if(!ObjectsUtil.isNull(inspectionUpdation)){
		            	
		            	inspectionUpdation.setInspectionStatus(Status.CLOSED.getValue());
		            	inspectionUpdation.setInspectionDate(DateUtil.toCurrentUTCTimeStamp());
		            	vehicleInspectionDAO.update(inspectionUpdation);
			            VehicleInspectionEntity inspectionInsertion = new VehicleInspectionEntity();
			            inspectionInsertion.setInspectionStatus(Status.OPEN.getValue());
			            inspectionInsertion.setApplicationId(applicationEntity);
			            inspectionInsertion.setUserId(userId);
			            inspectionInsertion.setScheduleInspectionDate(DateUtil.addDays(inspectionUpdation.getScheduleInspectionDate(), inspectionDays));
			            inspectionInsertion.setRevocationStatus(Status.PENDING.getValue());
			            vehicleInspectionDAO.saveOrUpdate(inspectionInsertion);
		            }
	        	}
	        	return new ResponseModel<String>(ResponseModel.SUCCESS,null,"Stoppage Tax Report Successfully Submitted");
	        }
	    } catch (Exception ex) {
	        logger.error("Getting error in update Or save in Stoppage tax details :::: Application number = "+model.getApplicationNo());
	    }
	    return new ResponseModel<String>(ResponseModel.FAILED);
	}

	@Override
	@Transactional
	public List<CitizenApplicationModel> getInspectionOpenApplications(Long userId, String applicationNumber) {
		logger.info("getInspectionApplications : " + " userId: " + userId + " application Number : " + applicationNumber);
		List<CitizenApplicationModel> appList = new ArrayList<CitizenApplicationModel>();
		List<VehicleInspectionEntity> vehicleInspectionEntities = vehicleInspectionDAO.getVehicleInspectionList(userId, DateUtil.getTimeStampTonight());
		for (VehicleInspectionEntity vehicleInspectionEntity : vehicleInspectionEntities) {
			ApplicationEntity appEntity = vehicleInspectionEntity.getApplicationId();
			if (!ObjectsUtil.isNull(applicationNumber) && !applicationNumber.isEmpty())
				if (!appEntity.getApplicationNumber().equals(applicationNumber)) {
					continue;
				}
			CitizenApplicationModel app = new CitizenApplicationModel();
			app.setIteration(vehicleInspectionDAO.getVehicleInspectionCount(appEntity.getApplicationId()));
			app.setApplicationNumber(appEntity.getApplicationNumber());
			app.setServiceType(ServiceType.STOPPAGE_TAX);
			app.setServiceTypeText(ServiceType.STOPPAGE_TAX.getLabel());
			appList.add(app);
		}
		return appList;
	}

	@Override
	@Transactional
	public Integer getInspectionOpenApplicationCount(Long userId) {
		logger.info("getInspectionOpenApplicationCount " + " userId: " + userId );
		List<VehicleInspectionEntity> vehicleInspectionEntities = vehicleInspectionDAO.getVehicleInspectionList(userId, DateUtil.getTimeStampTonight());
		return vehicleInspectionEntities.size();
	}
	
	@Override
	@Transactional
	public List<StoppageTaxReportModel> getStoppageTaxReport(String applicationNo) {
		try{
			ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNo);
			UserSessionEntity activeUserEntity = applicationEntity.getLoginHistory();
			UserSessionEntity stoppageUserEntity = userSessionDAO.getLatestUserSession(activeUserEntity.getAadharNumber(),
					activeUserEntity.getUniqueKey(), activeUserEntity.getKeyType(), ServiceType.STOPPAGE_TAX, Status.APPROVED);
	    	if(!ObjectsUtil.isNull(stoppageUserEntity)){
	    		ApplicationEntity applicationEntity1 = applicationDAO.getApplicationFromSession(stoppageUserEntity.getSessionId());
	    		RegistrationServiceResponseModel<List<StoppageTaxReportModel>> response = 
	    				registrationService.getStoppageTaxReportDetails(applicationEntity1.getApplicationNumber());
		        if (response.getHttpStatus() == HttpStatus.OK) {
		        	return response.getResponseBody();
		        }
	    	}
	    } catch (Exception ex) {
	        logger.error("Getting error in getStoppageTaxReport :::: Application number = "+applicationNo);
	    }
		logger.debug("Getting error in getStoppageTaxReport :::: Application number = "+applicationNo);
	    return null;
	}

	@Override
	@Transactional
	public StoppageTaxDetailsModel getStoppageTaxDetails(String applicationNo) {
		try{
			ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNo);
	    	if(!ObjectsUtil.isNull(applicationEntity)){
	    		RegistrationServiceResponseModel<StoppageTaxDetailsModel> response = 
	    				registrationService.getStoppageTaxDetails(applicationEntity.getLoginHistory().getUniqueKey());
		        if (response.getHttpStatus() == HttpStatus.OK) {
		        	return response.getResponseBody();
		        }
	    	}
	    } catch (Exception ex) {
	        logger.error("Getting error in getStoppageTaxDetails :::: Application number = "+applicationNo);
	    }
		logger.debug("Getting error in getStoppageTaxDetails :::: Application number = "+applicationNo);
	    return null;
	}

}

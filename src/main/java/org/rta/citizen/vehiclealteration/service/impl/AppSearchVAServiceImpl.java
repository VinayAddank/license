package org.rta.citizen.vehiclealteration.service.impl;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.RegistrationCategoryType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.NotFoundException;

/**
 *	@Author sohan.maurya created on Dec 26, 2016.
 */


@Service
@Qualifier("appSearchVAServiceImpl")
public class AppSearchVAServiceImpl extends AbstractAppSearchService implements AppSearchService {

	private static final Logger log = Logger.getLogger(AppSearchVAServiceImpl.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private ApplicationFormDataService applicationFormDataService;
	@Override
	@Transactional
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId) throws UnauthorizedException {

		return new ResponseModel<Object>(ResponseModel.SUCCESS, null);
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId) throws UnauthorizedException, NotFoundException {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(ServiceType.VEHICLE_ATLERATION.getLabel());
		mdl.setServiceCode(ServiceType.VEHICLE_ATLERATION.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
        try{
        	RegistrationServiceResponseModel<RegistrationCategoryModel> regCategoryResponse = 
        			registrationService.getRegCategoryByRcId(applicationService.getVehicleRcId(sessionId));
        	mdl.setRegCategory(regCategoryResponse.getResponseBody());
        } catch(Exception ex){
            log.error("Error in getRegistrationCategory");
        }
    	List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO
				.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for (ApplicationApprovalHistoryEntity history : histpryEntityList) {
			if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_ONLINE_FINANCER) {
				mdl.setFinancierStatus(Status.getStatus(history.getStatus()));
				mdl.setFinancierActionDate(history.getCreatedOn());
				mdl.setFinancierRemark(history.getComments());
			} else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_CCO) {
				mdl.setCcoStatus(Status.getStatus(history.getStatus()));
				mdl.setCcoActionDate(history.getCreatedOn());
				mdl.setCcoRemark(history.getComments());
			} else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_MVI) {
				mdl.setMviStatus(Status.getStatus(history.getStatus()));
				mdl.setMviActionDate(history.getCreatedOn());
				mdl.setMviRemark(history.getComments());
			} else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_AO) {
				mdl.setAoStatus(Status.getStatus(history.getStatus()));
				mdl.setAoActionDate(history.getCreatedOn());
				mdl.setAoRemark(history.getComments());
			} else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_RTO) {
				mdl.setRtoStatus(Status.getStatus(history.getStatus()));
				mdl.setRtoActionDate(history.getCreatedOn());
				mdl.setRtoRemark(history.getComments());
			}
		}
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		if(overAllStatus == Status.PENDING || overAllStatus == Status.FRESH){
			Status financierStatus = mdl.getFinancierStatus();
			if (ObjectsUtil.isNull(financierStatus)) {
				//--- is financed ----------
				RegistrationServiceResponseModel<Boolean> res = registrationService.hasAppliedHPA(appEntity.getLoginHistory().getUniqueKey());
				if (res.getHttpStatus().equals(HttpStatus.OK)) {
					if(res.getResponseBody()){
						mdl.setFinancierStatus(Status.PENDING);
					}
				} else {
					log.error("hasAppliedHPA Status is not OK...");
				}
			}
			if (ObjectsUtil.isNull(mdl.getMviStatus())) {
				mdl.setMviStatus(Status.PENDING);
			}
			if (ObjectsUtil.isNull(mdl.getCcoStatus())) {
				mdl.setCcoStatus(Status.PENDING);
			}
			if (ObjectsUtil.isNull(mdl.getAoStatus()) && ObjectsUtil.isNull(mdl.getRtoStatus())) {
				mdl.setAoStatus(Status.PENDING);
				mdl.setRtoStatus(Status.PENDING);
			}
		}
		mdl.setOverAllStatus(overAllStatus);
		String instanceId = applicationService.getProcessInstanceId(sessionId);
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
		if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			mdl.setActivitiTasks(actRes.getData());
		}
		//----------set is download fitness certificate ----------------
		if(overAllStatus == Status.APPROVED){
			try {
				ResponseModel<ApplicationFormDataModel> response = applicationFormDataService.getApplicationFormDataBySessionId(sessionId, FormCodeType.VA_FORM.getLabel());
				ApplicationFormDataModel form = response.getData();
	            ObjectMapper mapper = new ObjectMapper();
	            VehicleBodyModel vehicleBodyModel = mapper.readValue(form.getFormData(), VehicleBodyModel.class);
	            if(ObjectsUtil.isNull(vehicleBodyModel.getRegistrationCategoryCode()) || ObjectsUtil.isNull(vehicleBodyModel.getOldRegistrationCategoryCode())
	            		|| vehicleBodyModel.getRegistrationCategoryCode().equalsIgnoreCase(RegistrationCategoryType.NON_TRANSPORT.getCode()) 
	            		|| vehicleBodyModel.getRegistrationCategoryCode().equalsIgnoreCase(vehicleBodyModel.getOldRegistrationCategoryCode())){
	            	mdl.setIsDownloadFitness(false);
	            } else {
	            	mdl.setIsDownloadFitness(true);
	            }
			} catch (JsonProcessingException e) {
				log.error("JsonProcessingException in getApplicationStatus...");
			} catch (IOException e) {
				log.error("IOException in getApplicationStatus...");
			} catch(Exception ex){
				log.error("Exception in getApplicationStatus...: " + ex.getMessage());
			}
		}
		return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
	}

}

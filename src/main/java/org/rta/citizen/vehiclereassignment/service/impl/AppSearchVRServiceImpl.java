package org.rta.citizen.vehiclereassignment.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

/**
 *	@Author sohan.maurya created on Dec 26, 2016.
 */


@Service
@Qualifier("appSearchVRServiceImpl")
public class AppSearchVRServiceImpl extends AbstractAppSearchService implements AppSearchService {

	private static final Logger log = Logger.getLogger(AppSearchVRServiceImpl.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

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
		mdl.setApplicationType(ServiceType.VEHICLE_REASSIGNMENT.getLabel());
		mdl.setServiceCode(ServiceType.VEHICLE_REASSIGNMENT.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		getRegistrationCategory(appEntity, mdl);
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
				RegistrationServiceResponseModel<Boolean> res = registrationService.hasAppliedHPA(appEntity.getLoginHistory().getVehicleRcId());
				if (res.getHttpStatus().equals(HttpStatus.OK)) {
					if(res.getResponseBody()){
						mdl.setFinancierStatus(Status.PENDING);
					}
				} else {
					log.error("hasAppliedHPA Status is not OK...");
				}
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
		return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
	}

	@Override
	@Transactional
	public void getRegistrationCategory(ApplicationEntity appEntity, ApplicationStatusModel mdl)
			throws UnauthorizedException, NotFoundException {
		UserSessionEntity session = appEntity.getLoginHistory();
		RegistrationServiceResponseModel<VehicleDetailsRequestModel> res = null;
		if(!ObjectsUtil.isNull(session.getVehicleRcId())){
			res = registrationService.getVehicleDetails(session.getVehicleRcId());
		}else{
			res = registrationService.getVehicleDetails(registrationService.getPRDetails(session.getUniqueKey()).getResponseBody().getVehicleRcId());
		}
		VehicleDetailsRequestModel applicationModel;
		try{
			if (res.getHttpStatus() == HttpStatus.OK) {
				applicationModel = res.getResponseBody();
				mdl.setRegCategory(applicationModel.getRegCategoryDetails());
			} else {
				log.error(session.getKeyType() + " number not found : " + session.getUniqueKey());
				throw new NotFoundException(session.getKeyType() + " number not found : " + session.getUniqueKey());
			}
		} catch(Exception ex){
			if (res !=null) {
				log.error("res : " + res.getHttpStatus());
			}
			log.error(session.getKeyType() + " number not found : " + session.getUniqueKey());
			throw new NotFoundException(session.getKeyType() + " number not found : " + session.getUniqueKey());
		}
	}



}

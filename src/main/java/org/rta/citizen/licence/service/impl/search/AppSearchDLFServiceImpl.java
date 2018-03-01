package org.rta.citizen.licence.service.impl.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

@Service
@Qualifier("appSearchDLFServiceImpl")
public class AppSearchDLFServiceImpl extends AbstractAppSearchService implements AppSearchService {

	private static final Logger log = Logger.getLogger(AppSearchDLFServiceImpl.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private LicensePermitDetailsDAO licensePermitDetailsDAO;

	@Override
	@Transactional
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId) throws UnauthorizedException {
		return new ResponseModel<Object>(ResponseModel.SUCCESS, null);
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
			throws UnauthorizedException, NotFoundException {
		log.info("::::::::::Application Search Start:::::::");
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(ServiceType.DL_FRESH.getLabel());
		mdl.setServiceCode(ServiceType.DL_FRESH.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		String covsStatus = "";
		Map<String, String> map = new HashMap<>();
		List<LicensePermitDetailsEntity> licenseEntities = licensePermitDetailsDAO.getLicensePermitDetails(appEntity.getApplicationId());
		for(LicensePermitDetailsEntity entity : licenseEntities){
			if(entity.getStatus() == Status.APPROVED.getValue()){
				String covs = map.get(Status.APPROVED.getLabel());
				if(!StringsUtil.isNullOrEmpty(covs)){
					covs = covs+", "+ entity.getVehicleClassCode();	
				}else{
					covs = entity.getVehicleClassCode();
				}
				map.put(Status.APPROVED.getLabel(), covs);
			}else if(entity.getStatus() == Status.REJECTED.getValue()){
				String covs = map.get(Status.REJECTED.getLabel());
				if(!StringsUtil.isNullOrEmpty(covs)){
					covs = covs+", "+ entity.getVehicleClassCode();	
				}else{
					covs = entity.getVehicleClassCode();
				}
				map.put(Status.REJECTED.getLabel(), covs);
			}else{
				String covs = map.get(Status.PENDING.getLabel());
				if(!StringsUtil.isNullOrEmpty(covs)){
					covs = covs+", "+ entity.getVehicleClassCode();	
				}else{
					covs = entity.getVehicleClassCode();
				}
				map.put(Status.PENDING.getLabel(), covs);
			}
		}
		covsStatus = map.get(Status.APPROVED.getLabel())+" : "+ Status.APPROVED.getLabel() 
			+" | "+map.get(Status.PENDING.getLabel())+" : "+ Status.PENDING.getLabel() 
			+" | "+map.get(Status.REJECTED.getLabel())+" : "+ Status.REJECTED.getLabel() ;
		
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO
				.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for (ApplicationApprovalHistoryEntity history : histpryEntityList) {
			if(!(Status.getStatus(history.getStatus()) == Status.APPROVED || Status.getStatus(history.getStatus()) == Status.REJECTED)){
				continue;
			}
			if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_MVI) {
				mdl.setMviStatus(Status.getStatus(history.getStatus()));
				mdl.setMviActionDate(history.getCreatedOn());
				mdl.setMviRemark(history.getComments());
				mdl.setCovsStatus(covsStatus);
			}
		}
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		if(overAllStatus == Status.PENDING || overAllStatus == Status.FRESH){
			if (ObjectsUtil.isNull(mdl.getMviStatus())) {
				mdl.setMviStatus(Status.PENDING);
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

}

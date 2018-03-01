package org.rta.citizen.theftintimation.service.impl;

import java.io.IOException;
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
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.NotFoundException;

/**
 *	@Author sohan.maurya created on Dec 26, 2016.
 */

@Service
@Qualifier("appSearchTIServiceImpl")
public class AppSearchTIServiceImpl extends AbstractAppSearchService implements AppSearchService {

	private static final Logger log = Logger.getLogger(AppSearchTIServiceImpl.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;

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
		mdl.setApplicationType(ServiceType.THEFT_INTIMATION.getLabel());
		mdl.setServiceCode(ServiceType.THEFT_INTIMATION.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		getRegistrationCategory(appEntity, mdl);
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO
				.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for (ApplicationApprovalHistoryEntity history : histpryEntityList) {
			if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_CCO) {
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
		if (ServiceType.getServiceType(appEntity.getServiceCode()) == ServiceType.THEFT_INTIMATION) {
			ApplicationFormDataEntity formDataEntity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.TI_FORM.getLabel());
			if (!ObjectsUtil.isNull(formDataEntity)) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					TheftIntimationRevocationModel model = mapper.readValue(formDataEntity.getFormData(), TheftIntimationRevocationModel.class);
					mdl.setTheftStatus(model.getTheftStatus());
				} catch (JsonParseException e) {
					log.error("unable to parse formData for application number : " + appNo);
				} catch (JsonMappingException e) {
					log.error("unable to map formData for application number : " + appNo);
				} catch (IOException e) {
					log.error("unable to get formData for application number : " + appNo);
				}
			}
		}
		return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
	}

}

/**
 * 
 */
package org.rta.citizen.slotbooking.service.impl;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

/**
 * @author arun.verma
 *
 */
@Service
@Qualifier("appSearchDTServiceImpl")
public class AppSearchDTServiceImpl extends AbstractAppSearchService implements AppSearchService{

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActivitiService activitiService;

	@Autowired
	private ApplicationDAO applicationDAO;
	
	@Override
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId)
			throws UnauthorizedException, VehicleNotFinanced {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
			throws UnauthorizedException, NotFoundException, IOException, VehicleNotFinanced {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(ServiceType.DIFFERENTIAL_TAX.getLabel());
		mdl.setServiceCode(ServiceType.DIFFERENTIAL_TAX.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		mdl.setOverAllStatus(overAllStatus);
		String instanceId = applicationService.getProcessInstanceId(sessionId);
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
		if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			mdl.setActivitiTasks(actRes.getData());
		}
		return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
	}

}

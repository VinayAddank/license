package org.rta.citizen.licence.service.impl.search;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.constant.SomeConstants;
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
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;

@Service
@Qualifier("appSearchDLEXServiceImpl")
public class AppSearchDLEXServiceImpl extends AbstractAppSearchService implements AppSearchService {

	private static final Logger log = Logger.getLogger(AppSearchDLEXServiceImpl.class);

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
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
			throws UnauthorizedException, NotFoundException {
		log.info("::::::::::Application Search Start:::::::");
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(ServiceType.DL_EXPIRED.getLabel());
		mdl.setServiceCode(ServiceType.DL_EXPIRED.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO
				.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for (ApplicationApprovalHistoryEntity history : histpryEntityList) {
			if (!(Status.getStatus(history.getStatus()) == Status.APPROVED
					|| Status.getStatus(history.getStatus()) == Status.REJECTED)) {
				continue;
			}
			try {
				if (DateUtil.getCurrentAge(appEntity.getApplicantDob()) >= SomeConstants.FIFTY) {
					if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_CCO) {
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
				} else if (UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_MVI) {
					mdl.setMviStatus(Status.getStatus(history.getStatus()));
					mdl.setMviActionDate(history.getCreatedOn());
					mdl.setMviRemark(history.getComments());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		if (overAllStatus == Status.PENDING || overAllStatus == Status.FRESH) {
			try {
				if (DateUtil.getCurrentAge(appEntity.getApplicantDob()) >= SomeConstants.FIFTY) {
					if (ObjectsUtil.isNull(mdl.getCcoStatus())) {
						mdl.setCcoStatus(Status.PENDING);
					}
					if (ObjectsUtil.isNull(mdl.getMviStatus())) {
						mdl.setMviStatus(Status.PENDING);
					}
					if (ObjectsUtil.isNull(mdl.getAoStatus()) && ObjectsUtil.isNull(mdl.getRtoStatus())) {
						mdl.setAoStatus(Status.PENDING);
						mdl.setRtoStatus(Status.PENDING);
					}
				} else if (ObjectsUtil.isNull(mdl.getMviStatus())) {
					mdl.setMviStatus(Status.PENDING);
				}
			} catch (Exception e) {
				e.printStackTrace();
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

/**
 * 
 */
package org.rta.citizen.ownershiptransfer.service.impl;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicantModel;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.AbstractAppSearchService;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.AppSearchService;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.ownershiptransfer.dao.OTTokenDAO;
import org.rta.citizen.ownershiptransfer.entity.OTTokenEntity;
import org.rta.citizen.ownershiptransfer.model.BuyerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.NotFoundException;

/**
 * @author arun.verma
 *
 */
@Qualifier("appSearchOTServiceImpl")
@Service
public class AppSearchOTServiceImpl extends AbstractAppSearchService implements AppSearchService{

	private static final Logger log = Logger.getLogger(AppSearchOTServiceImpl.class);

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

	@Autowired
	private OTTokenDAO oTTokenDAO;

	@Override
	@Transactional
	public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId) throws UnauthorizedException {
		ResponseModel<Object> res =new ResponseModel<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		if(ServiceType.getServiceType(appEntity.getServiceCode()) != ServiceType.OWNERSHIP_TRANSFER_SALE){
			log.error("Application Service code is not OTS. app : " + appNo);
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Invalid Application Number !!!");
			res.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return res;
		}
		OTTokenEntity otTokenEntity = oTTokenDAO.getTokenEntity(appEntity.getApplicationId());
		if(ObjectsUtil.isNull(otTokenEntity) || !otTokenEntity.getIsClaimed()){
			log.error("Either token is yet not generated or yet not claimed by buyer. app : " + appNo);
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Invalid Application Status !!!");
			res.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return res;
		}
		BuyerModel buyer = new BuyerModel();
		ApplicantModel applicant = new ApplicantModel();
		applicant.setName(otTokenEntity.getClaimantName());
		applicant.setAadharNumber(otTokenEntity.getClaimantAadhaarNumber());
		buyer.setApplicant(applicant);
		try {
			ResponseModel<ApplicationFormDataModel> formResponse = applicationFormDataService
					.getApplicationFormDataBySessionId(sessionId, FormCodeType.OTS_FORM.getLabel());
			buyer.setForm(formResponse.getData());
		} catch (Exception e) {
			log.error("Exception while getting OTS form data for app :" + appNo + " Exception msg: " + e.getMessage());
		}
		res.setStatus(ResponseModel.SUCCESS);
		res.setData(buyer);
		return res;
	}

	@Override
	@Transactional
	public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId)
			throws UnauthorizedException, NotFoundException {
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		ServiceType service = ServiceType.getServiceType(appEntity.getLoginHistory().getServiceCode());
		ApplicationStatusModel mdl = new ApplicationStatusModel();
		mdl.setApplicationType(service.getLabel());
		mdl.setServiceCode(service.getCode());
		mdl.setSubmittedOn(appEntity.getCreatedOn());
		getRegistrationCategory(appEntity, mdl);
		List<ApplicationApprovalHistoryEntity> histpryEntityList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
		for(ApplicationApprovalHistoryEntity history : histpryEntityList){
			if(!(Status.getStatus(history.getStatus()) == Status.APPROVED || Status.getStatus(history.getStatus()) == Status.REJECTED)){
				continue;
			}
			if(UserType.valueOf(history.getRtaUserRole())  == UserType.ROLE_ONLINE_FINANCER){
				mdl.setFinancierStatus(Status.getStatus(history.getStatus()));
				mdl.setFinancierActionDate(history.getCreatedOn());
				mdl.setFinancierRemark(history.getComments());
			} else if(UserType.valueOf(history.getRtaUserRole())  == UserType.ROLE_SELLER){
				mdl.setSellerStatus(Status.getStatus(history.getStatus()));
				mdl.setSellerActionDate(history.getCreatedOn());
				mdl.setSellerRemark(history.getComments());
			} else if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_CCO){
				mdl.setCcoStatus(Status.getStatus(history.getStatus()));
				mdl.setCcoActionDate(history.getCreatedOn());
				mdl.setCcoRemark(history.getComments());
			} else if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_AO){
				mdl.setAoStatus(Status.getStatus(history.getStatus()));
				mdl.setAoActionDate(history.getCreatedOn());
				mdl.setAoRemark(history.getComments());
			} else if(UserType.valueOf(history.getRtaUserRole()) == UserType.ROLE_RTO){
				mdl.setRtoStatus(Status.getStatus(history.getStatus()));
				mdl.setRtoActionDate(history.getCreatedOn());
				mdl.setRtoRemark(history.getComments());
			}
		}
		Status overAllStatus = Status.getStatus(appEntity.getLoginHistory().getCompletionStatus());
		if(overAllStatus == Status.PENDING || overAllStatus == Status.FRESH){
			if(ObjectsUtil.isNull(mdl.getSellerStatus()) && service == ServiceType.OWNERSHIP_TRANSFER_SALE){
				mdl.setSellerStatus(Status.PENDING);
			}
			if(service == ServiceType.OWNERSHIP_TRANSFER_DEATH && ObjectsUtil.isNull(mdl.getFinancierStatus())){
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
			if(ObjectsUtil.isNull(mdl.getCcoStatus())){
				mdl.setCcoStatus(Status.PENDING);
			}
			if(ObjectsUtil.isNull(mdl.getAoStatus()) && ObjectsUtil.isNull(mdl.getRtoStatus())){
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
		if(service == ServiceType.OWNERSHIP_TRANSFER_SALE){
			try {
				ResponseModel<ApplicationFormDataModel> response = applicationFormDataService.getApplicationFormData(appNo, FormCodeType.OTS_FORM.getLabel());
				 ApplicationFormDataModel form = response.getData();
                 ObjectMapper mapper = new ObjectMapper();
                 AddressChangeModel addressChangeModel = mapper.readValue(form.getFormData(), AddressChangeModel.class);
				 mdl.setPermitOption(addressChangeModel.getPermitTransferType());
			} catch (JsonProcessingException e) {
				log.error("Json parsing exception on ownership transfer sale");
			} catch (IOException e) {
				log.error("Exception on ownership transfer sale");
			}
		}
		return new ResponseModel<ApplicationStatusModel>(ResponseModel.SUCCESS, mdl);
	}

}

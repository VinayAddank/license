/**
 * 
 */
package org.rta.citizen.ownershiptransfer.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.addresschange.service.impl.ACApplicationServiceImpl;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.AttachmentDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.AttachmentEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.PermitSubType;
import org.rta.citizen.common.enums.PermitType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.ApplicationNotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ApplicantModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.DocSyncModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.ReceiptModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.SellerAuthModel;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.ownershiptransfer.dao.OTTokenDAO;
import org.rta.citizen.ownershiptransfer.entity.OTTokenEntity;
import org.rta.citizen.ownershiptransfer.model.OtPermitModel;
import org.rta.citizen.ownershiptransfer.model.OtPermitOptionModel;
import org.rta.citizen.ownershiptransfer.service.OTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author arun.verma
 *
 */
@Service
public class OTServiceImpl implements OTService {

	private static final Logger logger = Logger.getLogger(ACApplicationServiceImpl.class);

    @Autowired
    private ApplicationDAO applicationDAO;
    
    @Autowired
    private OTTokenDAO oTTokenDAO; 
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private ApplicationFormDataDAO applicationFormDataDAO;

    @Autowired
    private ActivitiService activitiService;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
    @Autowired
	private ApplicationService applicationService;
    
    @Value("${activiti.citizen.ots.code.approvebuyer}")
    private String approveBuyerTaskDef;
    
    @Autowired
    private AttachmentDAO attachmentDAO;
    
    @Override
    @Transactional
    public ReceiptModel generateToken(Long sessionId, String clientIp) throws ApplicationNotFoundException {
    	logger.info("::generateToken::::::start::::  " + sessionId);
        ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
        if(ObjectsUtil.isNull(appEntity)){
            throw new ApplicationNotFoundException("Application Not Found !!!");
        }
        OTTokenEntity oTTokenEntity = oTTokenDAO.getTokenEntity(appEntity.getApplicationId());
        Long timeStamp = DateUtil.toCurrentUTCTimeStamp(); 
        if(ObjectsUtil.isNull(oTTokenEntity)){
            AadharModel aadhar = null;
            try {
                RegistrationServiceResponseModel<AadharModel> res = registrationService.getAadharDetails(Long.parseLong(appEntity.getLoginHistory().getAadharNumber()));
                if(res.getHttpStatus().equals(HttpStatus.OK)){
                    aadhar = res.getResponseBody();
                }
            } catch (NumberFormatException | UnauthorizedException e) {
                logger.error("Error while calling registration for Aadhar data...");
            }
            
            oTTokenEntity = new OTTokenEntity();
            //TODO generate token number with logic
            oTTokenEntity.setTokenNumber("RTA-OTS-" + timeStamp + appEntity.hashCode());
            oTTokenEntity.setApplicationEntity(appEntity);
            oTTokenEntity.setGeneratorAadhaarNumber(appEntity.getLoginHistory().getAadharNumber());
            oTTokenEntity.setIsClaimed(false);
            oTTokenEntity.setGeneratorIp(clientIp);
            if(!ObjectsUtil.isNull(aadhar)){
                oTTokenEntity.setGeneratorName(aadhar.getName());
            }
            oTTokenEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
            oTTokenEntity.setCreatedOn(timeStamp);
            oTTokenDAO.saveOrUpdate(oTTokenEntity);
        }
        ReceiptModel receipt = new ReceiptModel();
        receipt.setServiceType(ServiceType.OWNERSHIP_TRANSFER_SALE.getLabel());
        receipt.setCurrDateTime(oTTokenEntity.getCreatedOn());
        receipt.setAppNumber(appEntity.getApplicationNumber());
        receipt.setTokenNumber(oTTokenEntity.getTokenNumber());
        ApplicantModel applicantDetails = new ApplicantModel();
        applicantDetails.setName(oTTokenEntity.getGeneratorName());
        applicantDetails.setPrNumber(appEntity.getLoginHistory().getUniqueKey());
        applicantDetails.setAadharNumber(appEntity.getLoginHistory().getAadharNumber());
        receipt.setApplicantDetails(applicantDetails);
        logger.info("::generateToken::::::start::::  " + oTTokenEntity.getTokenNumber());
        return receipt;
    }
    
    @Override
    @Transactional
    public ReceiptModel getTokenReceipt(Long sessionId) throws ApplicationNotFoundException {
        ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
        if(ObjectsUtil.isNull(appEntity)){
            logger.error("application for sessionId : " + sessionId + " not found!");
            throw new ApplicationNotFoundException("Application Not Found !!!");
        }
        OTTokenEntity oTTokenEntity = oTTokenDAO.getTokenEntity(appEntity.getApplicationId());
        if(ObjectsUtil.isNull(oTTokenEntity)){
            logger.error("token for application number " + appEntity.getApplicationNumber() + " not found!");
            throw new ApplicationNotFoundException("Token Not Found !!!");
        }
        ReceiptModel receipt = new ReceiptModel();
        receipt.setServiceType(ServiceType.OWNERSHIP_TRANSFER_SALE.getLabel());
        receipt.setCurrDateTime(oTTokenEntity.getCreatedOn());
        receipt.setAppNumber(appEntity.getApplicationNumber());
        receipt.setTokenNumber(oTTokenEntity.getTokenNumber());
        ApplicantModel applicantDetails = new ApplicantModel();
        applicantDetails.setName(oTTokenEntity.getGeneratorName());
        applicantDetails.setPrNumber(appEntity.getLoginHistory().getUniqueKey());
        applicantDetails.setAadharNumber(appEntity.getLoginHistory().getAadharNumber());
        receipt.setApplicantDetails(applicantDetails);
        return receipt;
    }

    @Override
    @Transactional
    public CitizenApplicationModel getCitizenAppByOTSToken(String token){
        CitizenApplicationModel model = new CitizenApplicationModel();
        OTTokenEntity tokenEntity = oTTokenDAO.getTokenEntity(token);
        if(ObjectsUtil.isNull(tokenEntity)){
            return null;
        }
        ApplicationEntity appEntity = tokenEntity.getApplicationEntity();
        model.setUniqueKey(appEntity.getLoginHistory().getUniqueKey());
        model.setServiceType(ServiceType.getServiceType(appEntity.getLoginHistory().getServiceCode()));
        return model;
    }

    @Override
    public ResponseModel<String> saveOrUpdateOwnershipTransfer(Long vehicleRcId, Long applicationId, String prNumber,
            ServiceType service, String aadharNumber) {
        try {
            ApplicationFormDataEntity entity=null;
            ObjectMapper mapper = new ObjectMapper();
            AddressChangeModel model = null;
            if (service == ServiceType.OWNERSHIP_TRANSFER_AUCTION) {
                entity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.OTA_FORM.getLabel());
                model = mapper.readValue(entity.getFormData(), AddressChangeModel.class);
                model.setAadharNumber(aadharNumber);
            } else if (service == ServiceType.OWNERSHIP_TRANSFER_DEATH) {
                entity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.OTD_FORM.getLabel());
                model = mapper.readValue(entity.getFormData(), AddressChangeModel.class);
                model.setAadharNumber(aadharNumber);
            } else {
                entity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.OTS_FORM.getLabel());
                model = mapper.readValue(entity.getFormData(), AddressChangeModel.class);
                OTTokenEntity oTTokenEntity = oTTokenDAO.getTokenEntity(applicationId);
                model.setAadharNumber(oTTokenEntity.getClaimantAadhaarNumber());
            }
            
            ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
            List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
            List<UserActionModel> actionModelList = new ArrayList<>();
            for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
            	UserActionModel actionModel = new UserActionModel();
            	actionModel.setUserId(String.valueOf(history.getRtaUserId()));
            	actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
            	actionModel.setUserAction(Status.getLabel(history.getStatus()));
            	actionModelList.add(actionModel);
            }
            model.setActionModelList(actionModelList);
            
            List<AttachmentEntity> attachMentList = attachmentDAO.getAttachmentDetails(applicationId);
            List<DocSyncModel> docList = new ArrayList<>();
            if(!ObjectsUtil.isNullOrEmpty(attachMentList)){
            	for(AttachmentEntity ae : attachMentList){
            		DocSyncModel doc = new DocSyncModel();
            		doc.setTitle(ae.getAttachmentTitle());
            		doc.setDocTypeId(ae.getDocTypes().getDocTypeId());
            		doc.setFilename(ae.getFileName());
            		doc.setSource(ae.getSource());
            		if(!ObjectsUtil.isNull(ae.getAttachmentFrom())){
            			doc.setAttachmentFrom(ae.getAttachmentFrom().getValue());
            		}
            		docList.add(doc);
            	}
            	model.setDocList(docList);
            }
            
            if(!ObjectsUtil.isNull(model)){
                model.setOwnershipTypeCode(model.getOwnershipTypeCode());
            }
            if (!ObjectsUtil.isNull(vehicleRcId)) {
                model.setVehicleRcId(vehicleRcId);
            } else {
                RegistrationServiceResponseModel<ApplicationModel> registrationServiceResponseModel= registrationService.getPRDetails(prNumber);
                model.setVehicleRcId(registrationServiceResponseModel.getResponseBody().getVehicleRcId());
            }
            model.setServiceType(service);
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForCustomerDetails(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
        } catch (Exception ex) {
            logger.error("Getting error in update Or save in Change of Address details" + service);
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
    }

    
    //piyush.singh
    @Override
    @Transactional
    public ResponseModel<List<RtaTaskInfo>> approveBuyer(String appNo, SellerAuthModel applicant, Status status)
            throws UnauthorizedException {
    	

        ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>(ResponseModel.SUCCESS);
        ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
        
        OTTokenEntity oTToken= oTTokenDAO.getTokenEntity(appEntity.getApplicationId());
        if(!oTToken.getGeneratorAadhaarNumber().equals(applicant.getUid_num())){
        	response.setStatus(ResponseModel.FAILED);
			response.setMessage("adhar number not found");
			return response;
        }
        
         RegistrationServiceResponseModel<AadharModel> res = registrationService.aadharAuthentication(applicant);
    	
    	ResponseModel<AadharModel> responseAdhar = null;
		AadharModel adharModel = res.getResponseBody();
		if (res.getHttpStatus().equals(HttpStatus.OK)) {
			//responseAdhar = new ResponseModel<>(ResponseModel.SUCCESS, adharModel);
			response.setStatus(ResponseModel.SUCCESS);
			
		} else {
			//responseAdhar = new ResponseModel<>(ResponseModel.FAILED, adharModel);
			//responseAdhar.setStatusCode(res.getHttpStatus().value());
			
			response.setStatus(ResponseModel.FAILED);
			response.setMessage("seller adhar number not found");
			return response;
		}
		
		
		
        boolean taskFound = false;
        ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(appEntity.getExecutionId());
        List<RtaTaskInfo> tasks = actRes.getActiveTasks();
        for (RtaTaskInfo task : tasks) {
            if (task.getTaskDefKey().equalsIgnoreCase(approveBuyerTaskDef)) {
                taskFound = true;
            }
        }
        if (!taskFound) {
        	logger.error("InstanceId " + appEntity.getExecutionId() + " not belong to task : " + approveBuyerTaskDef);
            response.setStatus(ResponseModel.FAILED);
            response.setMessage("Invalid Action !!!");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        OTTokenEntity tokenEntity = oTTokenDAO.getTokenEntity(appEntity.getApplicationId());
        if(!applicant.getAadharNumber().equals(tokenEntity.getClaimantAadhaarNumber())){
        	logger.error("Claimant and Applicant aadhar number not matched. app : " + appNo);
        	response.setStatus(ResponseModel.FAILED);
            response.setMessage("Invalid Buyer !!!");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        
        //---------save in history----------
        Long time = DateUtil.toCurrentUTCTimeStamp();
        ApplicationApprovalHistoryEntity history = new ApplicationApprovalHistoryEntity();
        history.setApplicationEntity(appEntity);
        history.setCreatedBy(tokenEntity.getGeneratorAadhaarNumber());
        history.setCreatedOn(time);
        history.setRtaUserId(null);
        history.setRtaUserRole(UserType.ROLE_SELLER.toString());
        history.setStatus(status.getValue());
        history.setIteration(appEntity.getIteration());
        applicationApprovalHistoryDAO.saveOrUpdate(history);
        // ---- for activiti ----------------------
        logger.info("calling activiti to approve buyer by seller. app: " + appNo);
        Assignee assignee = new Assignee();
        assignee.setUserId(CitizenConstants.CITIZEN_USERID);
        ActivitiResponseModel<List<RtaTaskInfo>> actResponse =
                activitiService.completeTaskWithAction(assignee, approveBuyerTaskDef, status.getLabel(), appEntity.getExecutionId(), true);
        response.setActivitiTasks(actResponse.getActiveTasks());
        // ----------------------------------------
        //sendSMSEmail(status, appEntity);
        return response;
    }

	@Override
	@Transactional
	public OtPermitModel getPermitOptions(Long sessionId, Integer buyerMandalCode) throws ApplicationNotFoundException {
		OtPermitModel otPermitModel = null;
		try {
			logger.info( "In side getPermitOptions method with sessionId : "+ sessionId);
			ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
			String msg = "";
			boolean surrenderAllowed = false, transferAllowed = false;

			if (appEntity != null) {
                VehicleDetailsRequestModel vehicleDtls = applicationService.getVehicleDetails(appEntity.getLoginHistory().getVehicleRcId());
                CustomerDetailsRequestModel  sellerCustomerDtls = applicationService.getCustomerDetails(appEntity.getLoginHistory().getVehicleRcId());
                
                RegistrationServiceResponseModel<RTAOfficeModel> sellerRtaOfficeResponse = registrationService.getRTAOfficeByMandal(Integer.parseInt(sellerCustomerDtls.getTemp_mandal_code()),appEntity.getLoginHistory().getVehicleRcId());
    			
                RegistrationServiceResponseModel<RTAOfficeModel> buyerRtaOfficeResponse = null;
			
                if(ObjectsUtil.isNull(buyerMandalCode)){
                    buyerRtaOfficeResponse = sellerRtaOfficeResponse;
                }else{
                    buyerRtaOfficeResponse=  registrationService.getRTAOfficeByMandal(buyerMandalCode,appEntity.getLoginHistory().getVehicleRcId());
                }
                
                RegistrationServiceResponseModel<List<PermitHeaderModel>> permitDetailList = registrationService.getPermitDetails(appEntity.getLoginHistory().getVehicleRcId());
                otPermitModel = new OtPermitModel();
                if (ObjectsUtil.isNull(permitDetailList.getResponseBody()) || permitDetailList.getResponseBody().isEmpty()) {
                    msg = "There is not any Permit Letter available for the entered RC number.";
                    transferAllowed = false;
                    surrenderAllowed = false;
                } else {
					for (PermitHeaderModel headerModel : permitDetailList.getResponseBody()) {
						if (!headerModel.getIsTempPermit()) {
							if (PermitType.CCP.getValue().equals(headerModel.getPermitType())
									|| PermitType.EIB.getValue().equals(headerModel.getPermitType())
									|| PermitType.SCP.getValue().equals(headerModel.getPermitType())
									|| PermitType.PSVP.getValue().equals(headerModel.getPermitType())) {
								if ((((PermitType.CCP.getValue().equals(headerModel.getPermitType()))
										&& (vehicleDtls.getVehicle().getVehicleSubClass().equalsIgnoreCase("ARKT")))
										|| ((PermitType.CCP.getValue().equals(headerModel.getPermitType()))
												&& ((vehicleDtls.getVehicle().getVehicleSubClass()
														.equalsIgnoreCase("COCT"))
														|| (vehicleDtls.getVehicle().getVehicleSubClass()
																.equalsIgnoreCase("TOVT")))
												&& (headerModel.getPermitSubType()
														.equalsIgnoreCase(PermitSubType.HOME_DISTRICT.getCode())
														|| headerModel.getPermitSubType().equalsIgnoreCase(
																PermitSubType.NEIGHBOURING_DISTRICT.getCode())))
										|| PermitType.EIB.getValue().equals(headerModel.getPermitType())
										|| PermitType.SCP.getValue().equals(headerModel.getPermitType())
										|| PermitType.PSVP.getValue().equals(headerModel.getPermitType()))
										&& (sellerRtaOfficeResponse.getResponseBody().getCode().equalsIgnoreCase(
												buyerRtaOfficeResponse.getResponseBody().getCode()))) {
									msg = "There is a Permit Letter available for the entered RC number, please select an option to proceed";
									transferAllowed = true;
									surrenderAllowed = true;
								} else {
									msg = "There is a Permit Letter available for the entered RC number, you have to surrender this permit";
									transferAllowed = false;
									surrenderAllowed = true;
								}
							}else{
								msg = "There is a Permit Letter available for the entered RC number, please select an option to proceed";
								transferAllowed = true;
								surrenderAllowed = true;
							}
							break;
						}
					}
                }
            }
            otPermitModel.setOtPermitMsg(msg);
            List<OtPermitOptionModel> otPermitOptionModelList = new ArrayList<OtPermitOptionModel>();

            OtPermitOptionModel transferPermitModel = new OtPermitOptionModel();
            transferPermitModel.setOptionName("Transfer");
            transferPermitModel.setOptionAllowed(transferAllowed);

            OtPermitOptionModel surrenderPermitModel = new OtPermitOptionModel();
            surrenderPermitModel.setOptionName("Surrender");
            surrenderPermitModel.setOptionAllowed(surrenderAllowed);

            otPermitOptionModelList.add(transferPermitModel);
            otPermitOptionModelList.add(surrenderPermitModel);

            otPermitModel.setOtPermitOption(otPermitOptionModelList);

            logger.info("Returning OtPermitModel with permit options");


        } catch (Exception e) {
            e.printStackTrace();
        	logger.error("Application not found");
        	throw new ApplicationNotFoundException("Application not found");
        }
		
		return otPermitModel;
	}
    
    
    /*public void sendSMSEmail(Status status , ApplicationEntity appEntity){
    	logger.info("::::sendSMSEmail::start:::::");
    	CustMsgModel custModel=null;
    	boolean msgSend=false;
    	custModel = communicationService.getCustInfoForSellerToBuyer(status, appEntity , false , FormCodeType.OTS_FORM.getLabel());
		msgSend = communicationService.sendMsg(RtaApplicationServiceImpl.SEND_SMS_EMAIL, custModel);
		logger.info("::::sendSMSEmail::end:::: " + msgSend);
    }*/
}

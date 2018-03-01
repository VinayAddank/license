package org.rta.citizen.common.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.converters.AttachmentConverter;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.AttachmentDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.AttachmentEntity;
import org.rta.citizen.common.entity.DocumentMasterEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.model.DocPermissionModel;
import org.rta.citizen.common.model.DocTypesModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.service.AttachmentService;
import org.rta.citizen.common.service.RuleEngineService;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.dao.LicenceEndorsCOVDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *	@Author sohan.maurya created on Dec 12, 2016.
 */

@Service("attachmentService")
public class AttachmentServiceImpl implements AttachmentService {

	private static final Logger log = Logger.getLogger(AttachmentServiceImpl.class);

    @Autowired
    private RuleEngineService ruleEngineService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private AttachmentConverter attachmentConverter;

    @Autowired
    private UserSessionDAO userSessionDAO;

    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private ApplicationFormDataDAO applicationFormDataDAO;
    
    @Autowired
    private LicensePermitDetailsDAO licencePermitDetailsDAO;
    
    @Autowired
    private LicenceEndorsCOVDAO licenceEndorsCOVDAO;
    
    @Autowired
    private RegistrationLicenseService registrationLicenseService;
    
    @Override
    @Transactional
    public List<DocTypesModel> getAttachments(Long sessionId, String applicationNo, UserType userType) throws NotFoundException, UnauthorizedException {
    	ServiceType serviceType =null;
    	String serviceCategory = null;
    	UserSessionEntity userSessionEntity = null;
    	ApplicationEntity applicationEntity = null;
    	VehicleBodyModel model=null;
    	if(!StringsUtil.isNullOrEmpty(applicationNo)){
    		applicationEntity = applicationDAO.getApplication(applicationNo);
    		userSessionEntity = applicationEntity.getLoginHistory();
    		serviceType = ServiceType.getServiceType(applicationEntity.getServiceCode());
    		serviceCategory = applicationEntity.getServiceCategory();
    		if(!ObjectsUtil.isNull(userType) && userType==UserType.ROLE_MVI){
                try {
                    ApplicationFormDataEntity entity=applicationFormDataDAO.getApplicationFormData(applicationEntity.getApplicationId());
                    ObjectMapper mapper = new ObjectMapper();
                    if(null != entity){
                        model = mapper.readValue(entity.getFormData(), VehicleBodyModel.class);   
                    }
    			} catch (JsonParseException e) {
    				log.error("JsonParseException while get attachment for mvi");
    			} catch (JsonMappingException e) {
    			    log.error("JsonMappingException while get attachment for mvi");
    			} catch (IOException e) {
    			    log.error("IOException while get attachment for mvi");
    			}
    		}
    	}else{
    		userSessionEntity = userSessionDAO.getUserSession(sessionId);
    		serviceType = ServiceType.getServiceType(userSessionEntity.getServiceCode());
    		applicationEntity = applicationDAO.getApplicationFromSession(sessionId);
    		serviceCategory = applicationEntity.getServiceCategory();
    	}
        DocPermissionModel docPermissionModel = new DocPermissionModel();
        docPermissionModel.setServiceTypeCode(serviceType.getCode());
        docPermissionModel.setUserType(userType);
        switch (serviceType) {
            case ADDRESS_CHANGE:
                break;
            case ALTERATION_AGENCY_SIGNUP:
                break;
            case BODYBUILDER_SIGNUP:
                break;
            case DEALER_SIGNUP:
                break;
            case DEFAULT:
                break;
            case DIFFERENTIAL_TAX:
                break;
            case DL_BADGE:
                break;
            case DL_CHANGEADDRS_OS:
                break;
            case DL_CHANGE_ADDRESS:
                break;
            case DL_DLINFO:
                break;
            case DL_DUPLICATE:
                break;
            case DL_ENDORSMENT:{
            	docPermissionModel.setRegistrationCategoryType(getRegistrationCategory(sessionId, ServiceType.DL_ENDORSMENT));
            }
                break;
            case DL_EXPIRED:{
            	try {
					docPermissionModel.setCustomerAge(DateUtil.getCurrentAge(applicationEntity.getApplicantDob()));
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
                break;
            case DL_FOREIGN_CITIZEN:
                break;
            case DL_FRESH:{
            	docPermissionModel.setIsBadge(getBadge(sessionId, ServiceType.DL_FRESH));
            }
                break;
            case DL_INT_PERMIT:
                break;
            case DL_MILITRY:{
            	try {
					docPermissionModel.setCustomerAge(DateUtil.getCurrentAge(applicationEntity.getApplicantDob()));
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
                break;
            case DL_RENEWAL:{
            	try {
					docPermissionModel.setCustomerAge(DateUtil.getCurrentAge(applicationEntity.getApplicantDob()));
				} catch (Exception e) {
					e.printStackTrace();
				}
                docPermissionModel.setRegistrationCategoryType(getRegistrationCategory(sessionId, ServiceType.DL_RENEWAL));
            }
                break;
            case DL_RETEST:
                break;
            case DL_SURRENDER:
                break;
            case DL_REVO_SUS:
                break;
            case DUPLICATE_REGISTRATION:
                break;
            case FINANCIER_SIGNUP:
                break;
            case FRESH_RC_FINANCIER:
                break;
            case HPA:
                break;
            case HPT:
                break;
            case LL_DUPLICATE:
                break;
            case LL_ENDORSEMENT:{
            	try {
					docPermissionModel.setCustomerAge(DateUtil.getCurrentAge(applicationEntity.getApplicantDob()));
				} catch (Exception e) {
					e.printStackTrace();
				}
                docPermissionModel.setRegistrationCategoryType(getRegistrationCategory(sessionId, ServiceType.LL_ENDORSEMENT));
            }
                break;
            case LL_FRESH:{
            	docPermissionModel.setCustomerAge(getCustomerAge( userSessionEntity.getAadharNumber() ));
            }
                break;
            case LL_RETEST:
                break;
            case NOC_CANCELLATION:
                break;
            case NOC_ISSUE:
                break;
            case OWNERSHIP_TRANSFER_AUCTION:
                break;
            case OWNERSHIP_TRANSFER_DEATH:
                break;
            case OWNERSHIP_TRANSFER_SALE:
                break;
            case PUC_USER_SIGNUP:
                break;
            case REGISTRATION_CANCELLATION:
                break;
            case REGISTRATION_RENEWAL:
                break;
            case REGISTRATION_SUS_CANCELLATION:
                break;
            case SUSPENSION_REVOCATION:{
            	RegistrationServiceResponseModel<ApplicationModel> response = registrationService.getPRDetails(userSessionEntity.getUniqueKey());
            	if(response.getHttpStatus() == HttpStatus.OK){
            		ApplicationModel applicationModel = response.getResponseBody();
            		docPermissionModel.setStatus(applicationModel.getPrStatus().getValue());
            	}
            }
                break;
            case THEFT_INTIMATION:
                break;
		case VEHICLE_ATLERATION:
			docPermissionModel.setUserType(userType);
			if (!ObjectsUtil.isNull(model)) {
				docPermissionModel.setAlterationCategory(model.getAlterationCategory());
			}
			break;
            case VEHICLE_REASSIGNMENT:
                break;
            default:
                break;
        }
        if( (ServiceCategory.REG_CATEGORY.getCode().equalsIgnoreCase(serviceCategory) || ServiceCategory.PERMIT_FITNESS_CATEGORY.getCode().equalsIgnoreCase(serviceCategory))
        		&& !( serviceType == ServiceType.THEFT_INTIMATION ||  serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION
        				|| serviceType == ServiceType.FINANCIER_SIGNUP || serviceType == ServiceType.BODYBUILDER_SIGNUP
        				|| serviceType == ServiceType.DEALER_SIGNUP || serviceType == ServiceType.PUC_USER_SIGNUP
        				|| serviceType == ServiceType.ALTERATION_AGENCY_SIGNUP || serviceType == ServiceType.DRIVING_INSTITUTE
        				|| serviceType == ServiceType.HAZARDOUS_VEH_TRAIN_INST || serviceType == ServiceType.MEDICAL_PRACTITIONER
        				|| serviceType == ServiceType.VEHICLE_ATLERATION || serviceType == ServiceType.STOPPAGE_TAX
        				|| serviceType == ServiceType.FRESH_RC_FINANCIER)
        		&& ObjectsUtil.isNull(registrationService.getInsuranceDetails(userSessionEntity.getVehicleRcId()).getResponseBody())){
        	docPermissionModel.setIsInsuraceDocs(Boolean.TRUE);
        }
    	
        RegistrationServiceResponseModel<List<DocTypesModel>> result = ruleEngineService.getAttachments(docPermissionModel);
        if (result.getHttpStatus() == HttpStatus.OK) {
            return result.getResponseBody();
        }
    	
        log.info("error in http request " + result.getHttpStatus());
        return null;
    }

    @Transactional
    @Override
    public ResponseModel<Object> saveOrUpdate(AttachmentModel model, String applicationNo) throws DataMismatchException {
        
        ApplicationEntity applicationEntity = null;
        if(StringsUtil.isNullOrEmpty(applicationNo)){
        	applicationEntity = applicationDAO.getApplicationFromSession(model.getSessionId());
        }else{
        	applicationEntity = applicationDAO.getApplication(applicationNo);;
        }
        
        AttachmentEntity entity =
                attachmentDAO.getAttachmentDetails(model.getId(), applicationEntity.getApplicationId());
        String msg = "";
        if (ObjectsUtil.isNull(entity)) {
            entity = attachmentConverter.convertToEntity(model);
            entity.setApplicationId(applicationEntity);
            entity.setDocTypes(new DocumentMasterEntity(model.getId()));
            // entity.setCreatedBy(String.valueOf(model.getSessionId()));
            entity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
            entity.setUserType(model.getUserType());
            msg = "Successfully Saved....";
        }else{
            entity.setAttachmentFrom(model.getAttachmentFrom());
            entity.setAttachmentTitle(model.getAttachmentTitle());
            entity.setFileName(model.getFileName());
            entity.setSource(model.getSource());
            entity.setStatus(Status.PENDING);
            entity.setUserType(model.getUserType());
            msg = "Successfully Updated....";
        }
        // entity.setModifiedBy(String.valueOf(model.getSessionId()));
        entity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
        attachmentDAO.saveOrUpdate(entity);

        return new ResponseModel<Object>(ResponseModel.SUCCESS, null, msg);
    }
    
    @Transactional
    @Override
    public ResponseModel<Object> saveOrUpdateMultiple(List<AttachmentModel> models, String applicationNo) throws DataMismatchException {
        
    	ApplicationEntity applicationEntity = null;
        if(StringsUtil.isNullOrEmpty(applicationNo)){
        	applicationEntity = applicationDAO.getApplicationFromSession(models.get(0).getSessionId());
        }else{
        	applicationEntity = applicationDAO.getApplication(applicationNo);;
        }
        String msg = "";
        for(AttachmentModel model :models){
            AttachmentEntity entity = attachmentDAO.getAttachmentDetails(model.getId(), applicationEntity.getApplicationId());

            if (ObjectsUtil.isNull(entity)) {
                entity = attachmentConverter.convertToEntity(model);
                entity.setApplicationId(applicationEntity);
                entity.setDocTypes(new DocumentMasterEntity(model.getId()));
                // entity.setCreatedBy(String.valueOf(model.getSessionId()));
                entity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
                entity.setUserType(models.get(0).getUserType());
                msg = "Successfully Saved....";
            } else {
                entity.setAttachmentFrom(model.getAttachmentFrom());
                entity.setAttachmentTitle(model.getAttachmentTitle());
                entity.setFileName(model.getFileName());
                entity.setSource(model.getSource());
                entity.setStatus(Status.PENDING);
                entity.setUserType(models.get(0).getUserType());
                msg = "Successfully Updated....";
            }
            // entity.setModifiedBy(String.valueOf(model.getSessionId()));
            entity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());

            attachmentDAO.saveOrUpdate(entity);
        }
        return new ResponseModel<Object>(ResponseModel.SUCCESS, null, msg);
    }

    @Transactional
    @Override
    public List<AttachmentModel> getAttachmentDetails(Long sessionId, String applicationNo, UserType userType) throws NotFoundException {
        if (!StringsUtil.isNullOrEmpty(applicationNo)) {
        	ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNo);
        	if(userType == UserType.ROLE_MVI && ServiceType.VEHICLE_ATLERATION.getCode().equalsIgnoreCase(applicationEntity.getServiceCode())){
        		List<AttachmentModel> list= (List<AttachmentModel>) attachmentConverter.convertToModelList(attachmentDAO
                        .getAttachmentDetails(applicationEntity.getApplicationId()));
        		list.addAll(getBodyBuilderAttachmentList(applicationEntity));
        		return list;
        		}
            return (List<AttachmentModel>) attachmentConverter.convertToModelList(attachmentDAO
                    .getAttachmentDetails(applicationEntity.getApplicationId()));
        }
        return (List<AttachmentModel>) attachmentConverter.convertToModelList(attachmentDAO
                .getAttachmentDetails(applicationDAO.getApplicationFromSession(sessionId).getApplicationId()));
    }

    public Integer getRegistrationCategory(Long sessionId, ServiceType serviceType ){
    	boolean flag = false;
    	List<LicensePermitDetailsEntity> entities = licencePermitDetailsDAO.getLicensePermitDetails(
    			applicationDAO.getApplicationFromSession(sessionId).getApplicationId());
    	Set<String> vehicleCodeSet = new HashSet<String>();
    	for(LicensePermitDetailsEntity entity : entities){
    		vehicleCodeSet.add(entity.getVehicleClassCode());
    	}
    	if(serviceType == ServiceType.DL_ENDORSMENT){
    		if(vehicleCodeSet.contains(SomeConstants.HGV) || vehicleCodeSet.contains(SomeConstants.HZRD)){
    			return SomeConstants.ONE;
    		}
    		return SomeConstants.TWO;
    	}
    	List<LlrVehicleClassMasterEntity> vehicleClassList= licenceEndorsCOVDAO.getVehicleDescription(vehicleCodeSet);
    	for(LlrVehicleClassMasterEntity entity : vehicleClassList){
    		if(SomeConstants.TRANSPORT.equalsIgnoreCase(entity.getVehicleTransportType())){
    			flag = true;
    			break;
    		}
    	}
    	return flag ?  SomeConstants.ONE : SomeConstants.TWO;
    }
    
    public Boolean getBadge(Long sessionId, ServiceType serviceType ){
    	boolean flag = false;
    	List<LicensePermitDetailsEntity> entities = licencePermitDetailsDAO.getLicensePermitDetails(
    			applicationDAO.getApplicationFromSession(sessionId).getApplicationId());
    	for(LicensePermitDetailsEntity entity : entities) {
    		if(entity.isBadge()){
    			flag = true;
    			break;
    		}
    	}
    	return flag ?  Boolean.TRUE : Boolean.FALSE;
    }
    
    @Transactional
    @Override
    public ResponseModel<String> saveOrUpdateAttachments(Long applicationId, String  aadhaarNo) {
        try {
        	List<AttachmentModel> attachments = (List<AttachmentModel>) attachmentConverter.convertToModelList(attachmentDAO.getAttachmentDetails(applicationId));
            if(!ObjectsUtil.isNull(attachments)){
            	RegLicenseServiceResponseModel<SaveUpdateResponse> regResponse = registrationLicenseService.updateLicenceAttachmentsDetails(attachments, aadhaarNo);
                if (regResponse.getHttpStatus() == HttpStatus.OK) {
                    return new ResponseModel<String>(ResponseModel.SUCCESS);
                }	
            }
        } catch (Exception ex) {
            log.error("Getting error in update Or save in saveOrUpdateAttachments");
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
    }
    
    @Transactional
    @Override
    public Integer getCustomerAge( String aadhaarNo ){
    	Integer age = 0 ;
    	try{
            RegistrationServiceResponseModel<AadharModel> model = registrationService.getAadharDetails(Long.valueOf(aadhaarNo));
            if (model.getHttpStatus() == HttpStatus.OK) {
         	   age = DateUtil.getCurrentAge(model.getResponseBody().getDob());
            }
     	}catch (Exception e) {
     		log.error("error in http request " +e.getMessage());
			}
    	return age;
    }
    
    private List<AttachmentModel> getBodyBuilderAttachmentList(ApplicationEntity applicationEntity){
    	if(ServiceType.VEHICLE_ATLERATION.getCode().equalsIgnoreCase(applicationEntity.getServiceCode())){
    		try{
    			boolean isDoc = false;
    			ApplicationFormDataEntity formModel = applicationFormDataDAO.getApplicationFormData(applicationEntity.getApplicationId(), FormCodeType.VA_FORM.getLabel());
                VehicleBodyModel model = new ObjectMapper().readValue(formModel.getFormData(), VehicleBodyModel.class);
				for (AlterationCategory altCategory : model.getAlterationCategory()) {
					if (AlterationCategory.BODY_TYPE == altCategory
							|| AlterationCategory.SEATING_CAPACITY == altCategory) {
						isDoc = true;
					}
				}
				if(isDoc){
					RegistrationServiceResponseModel<ApplicationModel> registrationServiceResponseModel = registrationService
							.getPRDetails(applicationEntity.getLoginHistory().getUniqueKey());
					ApplicationModel applicationModel = registrationServiceResponseModel.getResponseBody();
					log.error("getting data for Application Model : " + applicationEntity.getApplicationNumber());
					return registrationService.getAttachmentsDetails(applicationModel.getVehicleModel().getChassisNumber(),
									applicationModel.getVehicleRcId()).getResponseBody();	
				}else{
					return null;
				}
            } catch(Exception ex) {
                log.error("error in getting data for application number : " +  applicationEntity.getApplicationNumber(), ex);
            }
    	}
    	return null;
    }
    
    //get Attachments without token
    
}

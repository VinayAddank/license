package org.rta.citizen.addresschange.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.addresschange.service.ACApplicationService;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.ownershiptransfer.dao.OTTokenDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *	@Author sohan.maurya created on Dec 20, 2016.
 */

@Service("acApplicationService")
public class ACApplicationServiceImpl implements ACApplicationService {

	private static final Logger logger = Logger.getLogger(ACApplicationServiceImpl.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserSessionDAO userSessionDAO;

    @Autowired
    private ApplicationFormDataDAO applicationFormDataDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private OTTokenDAO otTokenDAO;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
    @Override
    @Transactional
    public AadharModel getAadharDetails(Long sessionId, String applicationNumber) throws NotFoundException, Exception {
    	AadharModel model = null;
    	if(!StringsUtil.isNullOrEmpty(applicationNumber)){
        	Long aadharNumber = Long.valueOf(applicationDAO.getApplication(applicationNumber).getLoginHistory().getAadharNumber());
        	model = registrationService.getAadharDetails(aadharNumber).getResponseBody();
        }else{
        	UserSessionEntity entity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
            if (ObjectsUtil.isNull(entity)) {
            throw new NotFoundException();
            }
            ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(sessionId);
           Long aadharNumber =null;
           if(entity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_SALE.getCode())){
           	aadharNumber = Long.valueOf(otTokenDAO.getTokenEntity(applicationEntity.getApplicationId()).getClaimantAadhaarNumber());
           }else{
           	aadharNumber = Long.valueOf(entity.getAadharNumber());
           }
           logger.info("test...Aadhar number: " + entity.getAadharNumber());         
           model = registrationService.getAadharDetails(aadharNumber).getResponseBody();
           model.setApplicationNumber(applicationEntity.getApplicationNumber());
   		}
    	if (!StringsUtil.isNullOrEmpty(model.getName())) {
			Map<String, String> map = getSplitFirstLastName(model.getName());
			model.setFirstName(map.get("firstName"));
			model.setLastName(map.get("lastName"));
    	}
        return model;
    }

    @Transactional
    @Override
    public ResponseModel<String> saveOrUpdateAddressChange(Long vehicleRcId, Long applicationId, String prNumber) {
        try {
            ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.AC_FORM.getLabel());
            ObjectMapper mapper = new ObjectMapper();
            AddressChangeModel model = mapper.readValue(entity.getFormData(), AddressChangeModel.class);
            
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
            
            if (!ObjectsUtil.isNull(vehicleRcId)) {
                model.setVehicleRcId(vehicleRcId);
            } else {
                RegistrationServiceResponseModel<ApplicationModel> registrationServiceResponseModel= registrationService.getPRDetails(prNumber);
                model.setVehicleRcId(registrationServiceResponseModel.getResponseBody().getVehicleRcId());
            }
            model.setServiceType(ServiceType.ADDRESS_CHANGE);
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForCustomerDetails(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
        } catch (Exception ex) {
            logger.error("Getting error in update Or save in Change of Address details");
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
    }
    
    private static Map<String, String> getSplitFirstLastName(String fullName) {
		Map<String, String> map = new HashMap<String, String>();
		String[] name = fullName.split(" ", 2);
		if (name.length == 2) {
			map.put("firstName", name[0]);
			map.put("lastName", name[1]);
		} else {
			map.put("firstName", name[0]);
		}
		return map;
	}
}

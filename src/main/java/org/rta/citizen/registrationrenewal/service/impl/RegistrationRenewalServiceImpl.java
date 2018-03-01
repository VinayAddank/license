package org.rta.citizen.registrationrenewal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.converters.payment.TaxDetailConverter;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.rta.citizen.registrationrenewal.service.RegistrationRenewalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *	@Author sohan.maurya created on Jan 9, 2017.
 */
@Service("registrationRenewalService")
public class RegistrationRenewalServiceImpl implements RegistrationRenewalService {

	private static final Logger logger = Logger.getLogger(RegistrationRenewalServiceImpl.class);

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private TaxDetailDAO taxDetailDAO;
    @Autowired
    private ApplicationDAO appDao;
    @Autowired
    private TaxDetailConverter taxDetailConverter;
    
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
    
    @Transactional
    @Override
    public ResponseModel<String> saveOrUpdateRegistrationRenewal(String prNumber, Long applicationId) {
        try{
            CommonServiceModel model = new CommonServiceModel();
            ApplicationEntity appEntity = appDao.findByApplicationId(applicationId);
            try {
				TaxDetailEntity taxDetailEntity = taxDetailDAO.getByAppId(appEntity); 
				TaxModel taxModel = taxDetailConverter.convertToModel4Sync(taxDetailEntity, appEntity.getLoginHistory());
				model.setTaxModel(taxModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
            
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
            
            model.setPrNumber(prNumber);
            model.setStatus(Boolean.TRUE);
            model.setServiceType(ServiceType.REGISTRATION_RENEWAL);
            model.setApprovedDate(DateUtil.toCurrentUTCTimeStamp());
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.saveOrUpdateForCitizenCommonSerives(model);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
        } catch (Exception ex) {
            logger.error("Getting error in update Or save in Renewal of Registration");
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
    }

}

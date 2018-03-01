package org.rta.citizen.paytax.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rta.citizen.common.converters.payment.TaxDetailConverter;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PayTaxServiceImpl  implements PayTaxService{

	private static final Log log = LogFactory.getLog(PayTaxServiceImpl.class);
	@Autowired
	private TaxDetailDAO taxDetailDAO;
	@Autowired
	private TaxDetailConverter taxDetailConverter;
	@Autowired
    private RegistrationService registrationService;
	
	@Autowired
	private TransactionDetailDAO transactionDetailDAO;
	
	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
	
	@Override
	public ResponseModel<String> syncPayTaxData(ApplicationEntity appEntity, UserSessionEntity usersession) {
		log.info("::syncPayTaxData:::: " + usersession.getUniqueKey());
		try {
			TaxDetailEntity taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
			TaxModel taxModel = taxDetailConverter.convertToModel4Sync(taxDetailEntity , usersession);
			try{
				
				List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
				List<UserActionModel> actionModelList = new ArrayList<>();
				for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
					UserActionModel actionModel = new UserActionModel();
					actionModel.setUserId(String.valueOf(history.getRtaUserId()));
					actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
					actionModel.setUserAction(Status.getLabel(history.getStatus()));
					actionModelList.add(actionModel);
				}
				taxModel.setActionModelList(actionModelList);
				
				TransactionDetailEntity transactionDetailEntity =transactionDetailDAO.getByAppNdServiceType(appEntity);
				taxModel.setTransactionNo(transactionDetailEntity.getBankTransacNo());
				taxModel.setChallanNo(transactionDetailEntity.getSbiRefNo());
			}catch(Exception e){
				log.info("-------------- Can't get transaction and compound details for vcr----------------------------");
			}
            RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.syncPayTaxData(taxModel);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
        } catch (Exception ex) {
            log.error("Getting error in update Or save in Pay Tax  details");
        }
        return new ResponseModel<String>(ResponseModel.FAILED);
	}

	
}

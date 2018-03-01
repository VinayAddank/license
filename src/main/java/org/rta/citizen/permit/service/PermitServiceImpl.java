/**
 * 
 */
package org.rta.citizen.permit.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.dao.payment.FeeDetailDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.FeeDetailEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.OwnershipType;
import org.rta.citizen.common.enums.PermitClassType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.PermitDetailsModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.permit.model.PermitAuthorizationCardModel;
import org.rta.citizen.permit.model.PermitNewRequestModel;
import org.rta.citizen.permit.model.PermitTempPermitModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author arun.verma
 *
 */

@Service
public class PermitServiceImpl implements PermitService {

	private static final Logger logger = Logger.getLogger(PermitServiceImpl.class);

    public static final String PERMIT_CERTIFICATE = "PC";
    public static final String TEMPORARY_PERMIT_CERTIFICATE = "TPC";
	
	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Autowired
	private ApplicationDAO applicationDAO;
	
	@Autowired
	private FeeDetailDAO feeDetailDAO;
	
	@Autowired
    private ApplicationFormDataService applicationFormDataService;
	
	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;

	@Autowired
	private ApplicationService applicationService;
	
	@Override
	@Transactional
	public ResponseModel<PermitTempPermitModel> getTempPermits(Long sessionId) throws UnauthorizedException {
		UserSessionEntity entity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
		ResponseModel<PermitTempPermitModel> res = new ResponseModel<PermitTempPermitModel>();
		if (ObjectsUtil.isNull(entity)) {
			logger.info("No Session Found sssionId : " + sessionId);
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("No Session Found !!!");
			res.setStatusCode(HttpStatus.NOT_FOUND.value());
		}
		String prNumber = entity.getUniqueKey();
		logger.info("Calling registration for getPermitTempPermits with unique key : " + prNumber);
		RegistrationServiceResponseModel<PermitTempPermitModel> regResp = registrationService
				.getPermitTempPermits(prNumber);
		if (regResp.getHttpStatus().equals(HttpStatus.OK)) {
			res.setStatus(ResponseModel.SUCCESS);
			res.setData(regResp.getResponseBody());
		} else if (regResp.getHttpStatus().equals(HttpStatus.NOT_ACCEPTABLE)) {
			logger.info(
					"Primary Permit Not Found for Provided Details. Please Select Pukka Permit First pr: " + prNumber);
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Primary Permit Not Found for Provided Details. Please Select Pukka Permit First !!!");
			res.setStatusCode(regResp.getHttpStatus().value());
		} else {
			logger.error("Some Error Occured. while calling registration getPermitTempPermits Satus : "
					+ regResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(regResp.getHttpStatus().value());
		}
		return res;
	}

	@Override
	@Transactional
	public ResponseModel<Map<String, Object>> getSelectedPukkaTempPermit(String prNumber) throws UnauthorizedException {
		ResponseModel<Map<String, Object>> res = new ResponseModel<>();
		logger.info("Calling registration for getSelectedPukkaTempPermit with unique key : " + prNumber);
		RegistrationServiceResponseModel<Map<String, Object>> regResp = registrationService
				.getSelectedPukkaTempPermit(prNumber);
		if (regResp.getHttpStatus() == HttpStatus.OK) {
			res.setStatus(ResponseModel.SUCCESS);
			res.setData(regResp.getResponseBody());
		} else {
			logger.error("Some Error Occured. while calling registration getSelectedPukkaTempPermit Satus : "
					+ regResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(regResp.getHttpStatus().value());
		}
		return res;
	}
	
	@Override
    @Transactional
    public ResponseModel<Map<String, Object>> getSelectedPukkaTempPermit(String prNumber, Long sessionId) throws UnauthorizedException {
        ResponseModel<Map<String, Object>> res = new ResponseModel<>();
        logger.info("Calling registration for getSelectedPukkaTempPermit with unique key : " + prNumber);
        RegistrationServiceResponseModel<Map<String, Object>> regResp = registrationService
                .getSelectedPukkaTempPermit(prNumber);
        if (regResp.getHttpStatus() == HttpStatus.OK) {
            res.setStatus(ResponseModel.SUCCESS);
            ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
            Map<String, Object> mm = regResp.getResponseBody();
            
            // authcard renewal only applicable to pukka permit only
            if (!ObjectsUtil.isNullOrEmpty(mm) && ServiceType.getServiceType(appEntity.getServiceCode()) == ServiceType.PERMIT_RENEWAL_AUTH_CARD) {
                mm.remove("temporaryPermit");
            }
            res.setData(mm);
        } else {
            logger.error("Some Error Occured. while calling registration getSelectedPukkaTempPermit Satus : "
                    + regResp.getHttpStatus());
            res.setStatus(ResponseModel.FAILED);
            res.setMessage("Some Error Occured.");
            res.setStatusCode(regResp.getHttpStatus().value());
        }
        return res;
    }
	
	@Override
	@Transactional
	public ResponseModel<Map<String, Object>> getPermitCertificate(String appNo)
			throws UnauthorizedException, JsonProcessingException, IOException {
		ResponseModel<Map<String, Object>> res = new ResponseModel<>();
		Map<String, Object> map = new HashMap<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		if (ObjectsUtil.isNull(appEntity)) {
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Application Not Found.");
			res.setStatusCode(HttpStatus.NOT_FOUND.value());
			return res;
		}
		logger.info("Calling registration for getPermitCertificate app : " + appNo);
		UserSessionEntity userSessionEntity = appEntity.getLoginHistory();

		ResponseModel<ApplicationFormDataModel> response = applicationFormDataService
				.getApplicationFormDataBySessionId(userSessionEntity.getSessionId(), FormCodeType.PCF_FORM.getLabel());
		ApplicationFormDataModel form = response.getData();
		RegistrationServiceResponseModel<PermitDetailsModel> permitResp = null;
		Long mviUserId = null;
		// in case of other then PCF form.
		if (form == null) {
			permitResp = registrationService.getPermitCertificate(userSessionEntity.getVehicleRcId(),
					PERMIT_CERTIFICATE, mviUserId);
		} else {
			ObjectMapper mapper = new ObjectMapper();
			PermitNewRequestModel permitNewModel = mapper.readValue(form.getFormData(), PermitNewRequestModel.class);
			ApplicationApprovalHistoryEntity history = applicationApprovalHistoryDAO.getRoleLastAction(
					UserType.ROLE_MVI.name(), appEntity.getApplicationId(), appEntity.getIteration());
			if (!ObjectsUtil.isNull(history)) {
				mviUserId = history.getRtaUserId();
			}
			if (permitNewModel.getPermitClass().equals(PermitClassType.TEMPORARY.getLabel())) {
				permitResp = registrationService.getPermitCertificate(userSessionEntity.getVehicleRcId(),
						TEMPORARY_PERMIT_CERTIFICATE, mviUserId);
			} else {
				permitResp = registrationService.getPermitCertificate(userSessionEntity.getVehicleRcId(),
						PERMIT_CERTIFICATE, mviUserId);
			}
		}
		if (permitResp.getHttpStatus().equals(HttpStatus.OK)) {
			map.put("permitDetails", permitResp.getResponseBody());
		} else {
			logger.error("Some Error Occured. while calling registration getPermitCertificate. Satus : "
					+ permitResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(permitResp.getHttpStatus().value());
			return res;
		}
		logger.info("Calling registration for vehicle details app : " + appNo);
		RegistrationServiceResponseModel<VehicleDetailsRequestModel> vehicleResp = registrationService
				.getVehicleDetails(userSessionEntity.getVehicleRcId());
		if (vehicleResp.getHttpStatus().equals(HttpStatus.OK)) {
			map.put("vehicleDetails", vehicleResp.getResponseBody());
		} else {
			logger.error("Some Error Occured. while calling registration vehicle details. Satus : "
					+ vehicleResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(vehicleResp.getHttpStatus().value());
			return res;
		}
		logger.info("Calling registration for customer details app : " + appNo);
		RegistrationServiceResponseModel<CustomerDetailsRequestModel> customerResp = registrationService
				.getCustomerDetails(userSessionEntity.getVehicleRcId());
		if (customerResp.getHttpStatus().equals(HttpStatus.OK)) {
			CustomerDetailsRequestModel customerDetail = customerResp.getResponseBody();
			if (OwnershipType.INDIVIDUAL.getCode().equalsIgnoreCase(customerDetail.getOwnershipType())) {
				try {
					customerDetail.setAge(DateUtil.getCurrentAge(customerDetail.getDob()));
				} catch (Exception e) {
					logger.info("Can't fetch age from customer details : " + e.getMessage());
				}
			}
			map.put("customerDetails", customerDetail);
		} else {
			logger.error("Some Error Occured. while calling registration customer details. Satus : "
					+ customerResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(customerResp.getHttpStatus().value());
			return res;
		}
		
		logger.info("Calling registration for all tax details app : " + appNo);
		RegistrationServiceResponseModel<TaxModel> taxDetailsResp = registrationService.getTaxDetails(userSessionEntity.getVehicleRcId());
		if (taxDetailsResp.getHttpStatus().equals(HttpStatus.OK)) {
			TaxModel taxDetails = taxDetailsResp.getResponseBody();
			map.put("taxDetails", taxDetails);
		} else {
			logger.error("Some Error Occured. while calling registration tax details. Satus : "
					+ taxDetailsResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(customerResp.getHttpStatus().value());
			return res;
		}
		res.setStatus(ResponseModel.SUCCESS);
		res.setData(map);
		return res;
	}

	@Override
	@Transactional
	public ResponseModel<PermitDetailsModel> getPermitCertificateByPr(String prNumber, Long sessionId) throws UnauthorizedException, JsonProcessingException, IOException {
		ResponseModel<PermitDetailsModel> res = new ResponseModel<>();
		UserSessionEntity sessionEntity = userSessionDAO.getUserSession(sessionId);
		if (ObjectsUtil.isNull(sessionEntity) || ObjectsUtil.isNull(sessionEntity.getVehicleRcId())) {
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Application Not Found.");
			res.setStatusCode(HttpStatus.NOT_FOUND.value());
			return res;
		}
		logger.info("Calling registration for getPermitCertificate vehicleRcId : " + sessionEntity.getVehicleRcId());
		
		ResponseModel<ApplicationFormDataModel> response = applicationFormDataService.getApplicationFormDataBySessionId(sessionEntity.getSessionId(), FormCodeType.PCF_FORM.getLabel());
        ApplicationFormDataModel form = response.getData();
        ObjectMapper mapper = new ObjectMapper();
        PermitNewRequestModel permitNewModel = null;
        if(null != form) {
        	permitNewModel = mapper.readValue(form.getFormData(), PermitNewRequestModel.class);
        }
        ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
        RegistrationServiceResponseModel<PermitDetailsModel> regResp = null;
        Long mviUserId = null;
        ApplicationApprovalHistoryEntity history = applicationApprovalHistoryDAO.getRoleLastAction(UserType.ROLE_MVI.name(), appEntity.getApplicationId(), appEntity.getIteration());
        if(!ObjectsUtil.isNull(history)){
            mviUserId = history.getRtaUserId();
        }
        if(null != permitNewModel && permitNewModel.getPermitClass().equals(PermitClassType.TEMPORARY.getLabel())){
            regResp = registrationService.getPermitCertificate(sessionEntity.getVehicleRcId(), TEMPORARY_PERMIT_CERTIFICATE, mviUserId);
        } else {
            regResp = registrationService.getPermitCertificate(sessionEntity.getVehicleRcId(), PERMIT_CERTIFICATE, mviUserId);
        }
		if (regResp.getHttpStatus().equals(HttpStatus.OK)) {
			res.setStatus(ResponseModel.SUCCESS);
			res.setData(regResp.getResponseBody());
		} else {
			logger.error("Some Error Occured. while calling registration getSelectedPukkaTempPermit Satus : "
					+ regResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(regResp.getHttpStatus().value());
		}
		return res;
	}

	@Override
	@Transactional
	public ResponseModel<PermitAuthorizationCardModel> getPermitAuthCardDetails(String applicationNumber)
			throws UnauthorizedException {
		ResponseModel<PermitAuthorizationCardModel> res = new ResponseModel<>();
		ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
		UserSessionEntity sessionEntity = new UserSessionEntity();
		if (!ObjectsUtil.isNull(appEntity)) {
			sessionEntity = appEntity.getLoginHistory();
		} else if (ObjectsUtil.isNull(sessionEntity) || ObjectsUtil.isNull(sessionEntity.getVehicleRcId())) {
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Application Not Found.");
			res.setStatusCode(HttpStatus.NOT_FOUND.value());
			return res;
		}
		logger.info("Calling registration for getPermitCertificate vehicleRcId : " + sessionEntity.getVehicleRcId());
		RegistrationServiceResponseModel<PermitAuthorizationCardModel> regResp = registrationService
				.getPermitAuthCardDetails(sessionEntity.getUniqueKey());
		if (regResp.getHttpStatus().equals(HttpStatus.OK)) {
			res.setStatus(ResponseModel.SUCCESS);
			res.setData(regResp.getResponseBody());
		} else {
			logger.error("Some Error Occured. while calling registration getSelectedPukkaTempPermit Satus : "
					+ regResp.getHttpStatus());
			res.setStatus(ResponseModel.FAILED);
			res.setMessage("Some Error Occured.");
			res.setStatusCode(regResp.getHttpStatus().value());
		}
		return res;
	}

	@Override
	@Transactional
	public FeeModel getpermitFeesDetails(String applicationNumber) throws NotFoundException {
		FeeModel res = new FeeModel();
		if (ObjectsUtil.isNull(applicationNumber)) {
			logger.error("Application Number is null");
			/*
			 * res.setStatus(ResponseModel.FAILED);
			 * res.setMessage("Application Number is blank.");
			 * res.setStatusCode(HttpStatus.NOT_FOUND.value());
			 */
			return res;
		} else {
			ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
			if (ObjectsUtil.isNull(appEntity)) {
				logger.error("Application details not found for given application Number" + applicationNumber);
				/*
				 * res.setStatus(ResponseModel.FAILED); res.
				 * setMessage("Application details not found for given application Number"
				 * +applicationNumber);
				 * res.setStatusCode(HttpStatus.NOT_FOUND.value());
				 */
				return res;

			} else {
				FeeDetailEntity feeDetailEntity = new FeeDetailEntity();
				feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
				if (ObjectsUtil.isNull(feeDetailEntity)) {
					logger.error("Permit fees not found");
					/*
					 * res.setStatus(ResponseModel.FAILED);
					 * res.setMessage("Permit fees not found");
					 * res.setStatusCode(HttpStatus.NOT_FOUND.value());
					 */
					return res;
				} else

					res.setApplicationFee(String.valueOf(feeDetailEntity.getApplicationFee()));
				  res.setApplicationServiceCharge(String.valueOf(feeDetailEntity.getApplicationServiceCharge()));
				  res.setPermitFee(String.valueOf(feeDetailEntity.
				  getPermitFee()));
				 res.setPermitServiceCharge(String.valueOf(
				  feeDetailEntity.getPermitServiceCharge()));
				  res.setOtherPermitFee(String.valueOf(
				  feeDetailEntity.getOtherPermitFee()));
				  res.setFitnessFee(String.valueOf(feeDetailEntity.
				  getFitnessFee()));
				  res.setFitnessServiceCharge(String.valueOf(
				  feeDetailEntity.getFitnessServiceCharge()));
				  res.setPostalCharge(String.valueOf(feeDetailEntity.
				  getPostalCharge()));
				  res.setSmartCardFee(String.valueOf(feeDetailEntity.
				  getSmartCardFee()));
				  res.setTotalFee(String.valueOf(feeDetailEntity.
				  getTotalFee()));
				 
			}
		}
		return res;
	}
	
	@Override
	public PermitDetailsModel getPermitDetails(String prNumber, String permitType) throws UnauthorizedException {
		RegistrationServiceResponseModel<ApplicationModel> response =  registrationService.getPRDetails(prNumber);
		if (response.getHttpStatus() == HttpStatus.OK) {
			ApplicationModel appModel = response.getResponseBody();
			RegistrationServiceResponseModel<PermitDetailsModel> a = registrationService.getPermitCertificate(appModel.getVehicleRcId(), permitType, null);
			if (a.getHttpStatus() == HttpStatus.OK) {
				return a.getResponseBody();
			}
		}
		return null;
	}

	@Override
	public List<PermitHeaderModel> getAllPermits(Long sessionId) throws UnauthorizedException {
		UserSessionEntity userSession = userSessionDAO.getUserSession(sessionId);
		List<PermitHeaderModel> permitHeaderModel = applicationService.getPermitDetails(userSession.getVehicleRcId());
		return permitHeaderModel;
	}

}

package org.rta.citizen.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.UserSessionService;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.converters.ApplicationConverter;
import org.rta.citizen.common.converters.UserSessionConverter;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.RejectedAppRemovedExeIdHistoryDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.RejectedAppRemovedExeIdHistoryEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.RegistrationCategoryType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TaxType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.AlertModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.ChallanDetailsModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.CrimeDetailsListModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.FitnessDetailsModel;
import org.rta.citizen.common.model.InsuranceDetailsModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.OwnerConscent;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.PucDetailsModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaProcessInfo;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.model.vcr.VcrBookingData;
import org.rta.citizen.common.model.vcr.VcrService;
import org.rta.citizen.common.model.vcr.VcrServiceResponseModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.freshrc.FreshRcModel;
import org.rta.citizen.hpt.service.HPTAuthenticationService;
import org.rta.citizen.slotbooking.dao.SlotDAO;
import org.rta.citizen.slotbooking.entity.SlotEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.stoppagetax.dao.VehicleInspectionDAO;
import org.rta.citizen.stoppagetax.entity.VehicleInspectionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class ApplicationServiceImpl implements ApplicationService {

	private static final Logger log = Logger.getLogger(ApplicationServiceImpl.class);

	@Autowired
    private VcrService vcrService;
	
	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private ApplicationConverter applicationConverter;

	@Autowired
	private UserSessionConverter userSessionConverter;
	
    @Autowired
    private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
	
	@Autowired
	private SlotDAO slotDAO;
	
    @Autowired
    private VehicleInspectionDAO vehicleInspectionDAO;
    
    @Autowired
    private ActivitiService activitiService;
    
    @Autowired
    private UserSessionService userSessionService;
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private UserSessionDAO userSessionDAO;
    
    @Autowired
	private HPTAuthenticationService hPTAuthenticationService;
    
	@Value("${nontrans.tax.validty}")
	private int nonTransTaxValidty;

	@Value("${trans.tax.validty}")
	private int transTaxValidty;

	@Value("${rta.permit.validity}")
	private int permitValidty;
	
	@Value("${rta.fitness.validity}")
	private int fitnessValidty;
	
	@Value("${activiti.citizen.code.financier}")
    private String financierTaskDef;
	
	@Value("${activiti.citizen.iteration.max}")
	private Integer maxIterationAllowed;
	
    @Value("${iib.skip}")
    private Boolean skipIIB;
	
	@Autowired
	private ServiceMasterService serviceMasterService;
	
	@Autowired
	@Qualifier(value = "duplicateRegistrationDetailsService")
	private AbstractDetailsService abstractDetailsService;
	
	@Autowired
	private RejectedAppRemovedExeIdHistoryDAO rejectedAppRemovedExeIdHistoryDAO;

	@Override
	public VehicleDetailsRequestModel getVehicleDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<VehicleDetailsRequestModel> result = registrationService
				.getVehicleDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public CustomerDetailsRequestModel getCustomerDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<CustomerDetailsRequestModel> result = registrationService
				.getCustomerDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public InsuranceDetailsModel getInsuranceDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<InsuranceDetailsModel> result = registrationService
				.getInsuranceDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public FinanceModel getFinancierDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<FinanceModel> result = registrationService.getFinancierDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public CrimeDetailsListModel getCrimeDetails(String prNo) throws UnauthorizedException {
		RegistrationServiceResponseModel<CrimeDetailsListModel> result = registrationService
				.getCrimeDetails(prNo);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public PucDetailsModel getPucDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<PucDetailsModel> result = registrationService.getPucDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public NocDetails getNOCDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<NocDetails> result = registrationService.getNocDetails(vehicleRcId, null);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public List<ChallanDetailsModel> getChallanDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<List<ChallanDetailsModel>> result = registrationService
				.getChallanDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public TaxModel getTaxDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<TaxModel> result = registrationService.getTaxDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}
	
	@Override
    public ApplicationTaxModel getTaxDetails(String prOrTrNumber) throws UnauthorizedException {
        RegistrationServiceResponseModel<ApplicationTaxModel> result = registrationService.getTaxDetails(prOrTrNumber);
        if (result.getHttpStatus() == HttpStatus.OK) {
            return result.getResponseBody();
        }
        log.info("error in http request " + result.getHttpStatus());
        return null;
    }

	@Override
	public List<PermitHeaderModel> getPermitDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<List<PermitHeaderModel>> result = registrationService.getPermitDetails(vehicleRcId);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}

	@Override
	public FitnessDetailsModel getFitnessDetails(Long vehicleRcId) throws UnauthorizedException {
		RegistrationServiceResponseModel<FitnessDetailsModel> result = registrationService
				.getFitnessDetails(vehicleRcId, null);
		if (result.getHttpStatus() == HttpStatus.OK) {
			return result.getResponseBody();
		}
		log.info("error in http request " + result.getHttpStatus());
		return null;
	}
	
	@Override
	public UserModel getRTAUserByToken(String token) throws UnauthorizedException {
	    RegistrationServiceResponseModel<UserModel> result = registrationService.getRTAUserFromToken(token);
	    if (result.getHttpStatus() == HttpStatus.OK) {
	        return result.getResponseBody();
	    }
	    log.info("error in http request " + result.getHttpStatus());
	    return null;
	}

	@Override
	@Transactional
	public CitizenApplicationModel getApplicationModel(Long sessionId) {
		return applicationConverter.convertToModel(applicationDAO.getApplicationFromSession(sessionId));
	}

    @Override
    @Transactional
    public CitizenApplicationModel getApplicationById(Long appId) {
        return applicationConverter.convertToModel(applicationDAO.findByApplicationId(appId));
    }
	
	@Override
	@Transactional
	public CitizenApplicationModel saveOrUpdate(UserSessionModel session, CitizenApplicationModel applicationModel) {

		ApplicationEntity entity = applicationDAO.getApplicationFromSession(session.getSessionId());
		if (ObjectsUtil.isNull(entity)) {
			entity = new ApplicationEntity();
			entity.setCreatedBy(session.getAadharNumber());
			entity.setCreatedOn(applicationModel.getCreatedOn());
			entity.setApplicationNumber(generateApplicationNumber(session));
		}
		entity.setLoginHistory(userSessionConverter.converToEntity(session));
		entity.setModifiedBy(applicationModel.getModifiedBy());
		entity.setModifiedOn(applicationModel.getModifiedOn());
		entity.setServiceCode(applicationModel.getServiceType().getCode());
		applicationDAO.saveOrUpdate(entity);
		return applicationConverter.convertToModel(entity);
	}

	@Override
	@Transactional
	public String getProcessInstanceId(Long sessionId) {
		return applicationDAO.getApplicationFromSession(sessionId).getExecutionId();
	}

	@Override
	@Transactional
	public void saveUpdateExecutionId(Long sessionId, String executionId) {
		ApplicationEntity entity = applicationDAO.getApplicationFromSession(sessionId);
		if(ObjectsUtil.isNull(entity.getIteration())){
		    entity.setIteration(1);
		}
		entity.setExecutionId(executionId);
		applicationDAO.saveOrUpdate(entity);
	}

	@Override
	@Transactional
	public ResponseModel<Object> generalDetailsNext(Long sessionId, String taskDef, String userName) {
		log.info("::::generalDetailsNext::::start:::");
		ResponseModel<Object> response = null;
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		response = validateAllDetails(appEntity);
		
		if (response != null && response.getStatus().equals(ResponseModel.SUCCESS)) {
		    Map<String, Object> map = new HashMap<>();
		    if(ServiceType.getServiceType(appEntity.getLoginHistory().getServiceCode()) == ServiceType.OWNERSHIP_TRANSFER_SALE){
		        //TODO have to pay or not for type OwnerShip Transfer Sale by sandeep
		        map.put(ActivitiService.IS_PAYMENT, false);
		    }
		       
		    //---- set the iteration in activiti ---------
		    map.put(ActivitiService.ITERATION, ObjectsUtil.isNull(appEntity.getIteration()) ? 0 : appEntity.getIteration());
		
		    Assignee assignee = new Assignee();
            assignee.setUserId(userName);
            String instanceId = getProcessInstanceId(sessionId);
            ActivitiResponseModel<List<RtaTaskInfo>> actResponse = null;
            if(ServiceType.STOPPAGE_TAX_REVOCATION.getCode().equalsIgnoreCase(appEntity.getServiceCode())){
            	UserSessionEntity activeUserEntity = appEntity.getLoginHistory();
    			UserSessionEntity stoppageUserEntity = userSessionDAO.getLatestUserSession(activeUserEntity.getAadharNumber(),
    					activeUserEntity.getUniqueKey(), activeUserEntity.getKeyType(), ServiceType.STOPPAGE_TAX, Status.APPROVED);
    	    	ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(stoppageUserEntity.getSessionId());
            	VehicleInspectionEntity inspectionUpdation = vehicleInspectionDAO.getVehicleInspection(applicationEntity.getApplicationId(), null);
            	Assignee nextAssignee = new Assignee();
            	try{
            		RegistrationServiceResponseModel<UserModel> responseModel = registrationService.getUserDetails(inspectionUpdation.getUserId());
            		if(responseModel.getHttpStatus() == HttpStatus.OK){
            			nextAssignee.setUserId(responseModel.getResponseBody().getUserName());
            		}
            	}catch (Exception e) {
					log.error(":::::generalDetailsNext::::::::::"+e.getMessage());
				}
            	actResponse= activitiService.completeTask(assignee, taskDef, instanceId, true, map, SomeConstants.MVI, nextAssignee);
            }else{
            	actResponse= activitiService.completeTask(assignee, taskDef, instanceId, true, map);
            }
            response.setActivitiTasks(actResponse.getActiveTasks());
        }
		//-----------logging -----------------------
		try{
			String msg = response.getMessage();
	    	if(ObjectsUtil.isNull(msg)){
	    		msg = "";
	    	}
	    	if(!ObjectsUtil.isNull(response.getData())){
	    		@SuppressWarnings("unchecked")
				List<AlertModel> alertModels = (List<AlertModel>) response.getData();
	    		if(!ObjectsUtil.isNullOrEmpty(alertModels)){
	    			for(AlertModel alert : alertModels){
	    				msg = msg + alert.getValue() + ", ";
	    			}
	    		}
	    	}
	    	createLog(response.getStatus() + "2", msg, sessionId, response.getStatusCode());
		}catch(Exception ex){
		}
		//----------------------------------------------
		log.info("::::generalDetailsNext::::end:::");
		return response;
	}

	@Override
	@Transactional
	public void createLog(String status, String msg, Long sessionId, Integer code){
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		try{
	    	AuthenticationModel model = new AuthenticationModel();
	    	KeyType keyType = appEntity.getLoginHistory().getKeyType();
	    	if(ObjectsUtil.isNull(keyType)){
	    		keyType = KeyType.PR;
	    	}
	    	model.setKeyType(keyType);
	    	switch(keyType){
			case PR:
				model.setPrNumber(appEntity.getLoginHistory().getUniqueKey());
				break;
			case TR:
				model.setTrNumber(appEntity.getLoginHistory().getUniqueKey());
				break;
			case DLEX:
			case DLIN:
			case DLS:
			case DLSC:
			case DLB:
			case DLCA:
			case DLD:
			case DLE:
			case DLI:
			case DLR:
			case DLRE:
				model.setDlNumber(appEntity.getLoginHistory().getUniqueKey());
				break;
			case LLE:
				if(ObjectsUtil.isNull(model.getLlrNumber())){
					model.setDlNumber(appEntity.getLoginHistory().getUniqueKey());
				} else {
					model.setLlrNumber(appEntity.getLoginHistory().getUniqueKey());
				}
				break;
			case DLF:
			case LLD:
				model.setLlrNumber(appEntity.getLoginHistory().getUniqueKey());
				break;
			case DLFC:
				model.setDob(appEntity.getLoginHistory().getUniqueKey());
				break;
			case LLR:
				model.setApplicationNumber(appEntity.getLoginHistory().getUniqueKey());
				break;
			default:
				break;
	    	}
	    	model.setUid_num(appEntity.getLoginHistory().getAadharNumber());
			hPTAuthenticationService.logAttempt(model, ServiceType.getServiceType(appEntity.getServiceCode()), status, msg, code);
		} catch(Exception ex){
			log.error("Error while logging at step 2 ....................");
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public ResponseModel<Object> validateAllDetails(ApplicationEntity appEntity) {
		RegistrationServiceResponseModel<ApplicationTaxModel> result = null;
		try {
			result = registrationService.getTaxDetails(appEntity.getLoginHistory().getUniqueKey());
		} catch (RestClientException e) {
			log.error("error when getting tr details : " + e);
		}
		if (ObjectsUtil.isNull(result)) {
			log.info("tr details not found for tr number : " + appEntity.getLoginHistory().getUniqueKey());
			return null;
		}
		if (result.getHttpStatus() != HttpStatus.OK) {
			log.info("error in http request " + result.getHttpStatus());
			return null;
		}
		ApplicationTaxModel appTaxModel = result.getResponseBody();
		ResponseModel<Object> response = null;
		List<AlertModel> alertModels = new ArrayList<AlertModel>();
		if (abstractDetailsService.vcrValidate(appEntity.getLoginHistory().getUniqueKey()) && !(ServiceType.getServiceType(appEntity.getServiceCode()) == ServiceType.THEFT_INTIMATION || ServiceType.getServiceType(appEntity.getServiceCode()) == ServiceType.PAY_TAX)) {
            alertModels.add(new AlertModel("VCR", "VCR is found against this vehicle, Please pay all dues."));
            log.info("::::validateAllDetails:: Not Applicable as Challan is pending :::end::: " + appEntity.getServiceCode());
            return new ResponseModel<>(ResponseModel.FAILED,alertModels);
        }
		boolean isPayTax = true;
		switch(ServiceType.getServiceType(appEntity.getServiceCode())){
			case OWNERSHIP_TRANSFER_AUCTION:
			case OWNERSHIP_TRANSFER_DEATH:
				response = new ResponseModel<Object>(ResponseModel.SUCCESS);
				return response;
			case DIFFERENTIAL_TAX: 
			case REGISTRATION_SUS_CANCELLATION:
			case THEFT_INTIMATION: 
			case PAY_TAX: {
				response = new ResponseModel<Object>(ResponseModel.SUCCESS);
				log.info("::::validateAllDetails:: Not Applicable :::end::: " + appEntity.getServiceCode());
				return response;
			}
		}
		
		if(TaxType.LIFE_TAX.getValue() != appTaxModel.getTaxType())
			if (taxValidate(appTaxModel)) {
				alertModels.add(new AlertModel("Tax", "Tax is Pending against this vehicle, Please pay the tax dues."));
			}
		//check only for tax if suspension revocation RTA-2615, REGISTRATION_CANCELLATION RTA-3266
		if(!(ServiceType.getServiceType(appEntity.getServiceCode()) == ServiceType.SUSPENSION_REVOCATION || ServiceType.getServiceType(appEntity.getServiceCode()) == ServiceType.REGISTRATION_CANCELLATION)){
		    if (!ServiceUtil.isFitnessService(ServiceType.getServiceType(appEntity.getServiceCode())) && fitnessValidate(appTaxModel, appTaxModel.getRegType())) {
	            alertModels.add(new AlertModel("Fitness", "Fitness for the Vehicle is expired, please renew your Fitness"));
	        }
//To DO Testing	        
		    /*if (pucValidate(appTaxModel)) {
	            alertModels.add(new AlertModel("PUC", "PUC is expired please renew PUC."));
	        }*/
	        //Skip IIB depending on project environments
	        //always skip as order by guru ji on 15-06-2017
	        /*if(!skipIIB){
	            if(ObjectsUtil.isNull(appTaxModel.getInsuranceEndDate()) || appTaxModel.getInsuranceEndDate() <= 0){
	                alertModels.add(new AlertModel("Insurance", "Your latest insurance data is not available with the us. Please try again atleast after 72 hours."));
	            } else if (insuranceValidate(appTaxModel)) {
	                alertModels.add(new AlertModel("Insurance", "Insurance is expired please renew insurance."));
	            }
	        }*/
		}
				
		if(ObjectsUtil.isNullOrEmpty(alertModels)) {
			response = new ResponseModel<Object>(ResponseModel.SUCCESS);
		} else {
			response = new ResponseModel<>(ResponseModel.FAILED,alertModels);
		}
		return response;
	}
	
	public boolean insuranceValidate(ApplicationTaxModel appTaxModel) {
		log.info("::::::InsuranceValidae::::start::");
		boolean isInsuranceValidate = false;
		if(DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(),
				appTaxModel.getInsuranceEndDate()))
			isInsuranceValidate = true;
		log.info("::::::InsuranceValidae:::end::");
		return isInsuranceValidate;
	}
	
	public boolean pucValidate(ApplicationTaxModel appTaxModel) {
		log.info("validating PUC.......");
		boolean isPUCValidate = false;
		if(!ObjectsUtil.isNull(appTaxModel.getPucExpireDate()) && appTaxModel.getPucExpireDate() != 0){
		    if(DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(),
                    appTaxModel.getPucExpireDate())){
                isPUCValidate = true;
            }
		} else {
		    if (!appTaxModel.isPucRequired()) {
	            return false;
	        }
	        if(appTaxModel.getPucExpireDate() == 0){
	            if(DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(),
	                    DateUtil.addDays(appTaxModel.getPrIssueTime(), 179)))
	                isPUCValidate = true;
	        }
		}
		return isPUCValidate;
	}
	
	@Override
	public boolean taxValidate(ApplicationTaxModel appTaxModel) {
		log.info("::::::taxValidae::::::");
		
		boolean isTaxValidate = false;
		if(DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(),
				appTaxModel.getTaxValidUpto()))
			isTaxValidate = true;
		return isTaxValidate;
	}
	
	public boolean quarterValidate(ApplicationTaxModel appTaxModel){
		log.info(":quarterValidate:::start:::");
		int monthDiff = DateUtil.getMonth(appTaxModel.getTaxValidUpto()) - DateUtil.getMonth(DateUtil.toCurrentUTCTimeStamp());
		int yearDiff  = DateUtil.getYear(appTaxModel.getTaxValidUpto()) - DateUtil.getYear(DateUtil.toCurrentUTCTimeStamp());
		boolean isQuarter = false;
		if(yearDiff ==0 && monthDiff >= 0){
			isQuarter = false;
		}else
			isQuarter = true;	
		log.info(":quarterValidate:::end:::");
		return isQuarter;
	}
	
	@Override
	public boolean permitValidate(long prIssueDate, int regType) {
		boolean isPermitValidate = false;
		switch (RegistrationCategoryType.getRegistrationCategoryType(regType)) {
		case NON_TRANSPORT:
			isPermitValidate = false;
			break;
		case TRANSPORT:
			isPermitValidate = DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(),
					DateUtil.addYears(prIssueDate, permitValidty));
			break;
		}
		return isPermitValidate;

	}
	
	@Override
	public boolean fitnessValidate(long prIssueDate, int regType) {
		boolean isFitnessValidate = false;
		switch (RegistrationCategoryType.getRegistrationCategoryType(regType)) {
		case NON_TRANSPORT:
			isFitnessValidate = false;
			break;
		case TRANSPORT:
			isFitnessValidate = DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(),
					DateUtil.addYears(prIssueDate, fitnessValidty));
			break;
		}
		return isFitnessValidate;

	}
	
	@Override
	public boolean fitnessValidate(ApplicationTaxModel appTaxModel, int regType) {
		FcDetailsModel fdm = appTaxModel.getFitnessDetailsModel();
		if (ObjectsUtil.isNull(fdm)) {
			return false;
		}
		switch (RegistrationCategoryType.getRegistrationCategoryType(regType)) {
		case NON_TRANSPORT:
			return false;
		case TRANSPORT:
			return DateUtil.toCurrentUTCTimeStamp() > fdm.getExpiryDate();
		}
		return false;
	}
	
	
	
	@Override
	public String generateApplicationNumber(UserSessionModel session) {
	    //CitizenApplicationModel applicationModel = getApplicationModel(session.getSessionId());
	    /*try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(String.valueOf(1).getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
             sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            System.out.println(sb);
        } catch (NoSuchAlgorithmException e) {
            
        }*/
	    Long timestamp = DateUtil.toCurrentUTCTimeStamp();
	    String serviceCode = session.getServiceType().getCode();
	    return new StringBuilder(serviceCode).append(timestamp).toString().toUpperCase();
	}

    @Override
    @Transactional
    public CitizenApplicationModel getCitizenApplication(String applicationNumber) {
        ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
        if(ObjectsUtil.isNull(appEntity)){
            return null;
        }
        CitizenApplicationModel mdl = new CitizenApplicationModel();
        mdl.setApplicationNumber(appEntity.getApplicationNumber());
        mdl.setSessionId(appEntity.getLoginHistory().getSessionId());
        mdl.setServiceType(ServiceType.getServiceType(appEntity.getLoginHistory().getServiceCode()));
        mdl.setAppStatus(Status.getStatus(appEntity.getLoginHistory().getCompletionStatus()));
        mdl.setAadharNumber(appEntity.getLoginHistory().getAadharNumber());
        mdl.setUniqueKey(appEntity.getLoginHistory().getUniqueKey());
        mdl.setIteration(appEntity.getIteration());
        mdl.setServiceCategoryCode(appEntity.getServiceCategory());
        return mdl;
    }

	@Override
	@Transactional
	public List<CitizenApplicationModel> getSlotPendingApplications(Long timestamp, ServiceType serviceType, SlotServiceType slotServiceType, 
	        String rtaOfficeCode, String userId) {
	    Map<String,Object> map = new HashMap<>();
	    map.put(ActivitiService.RTA_OFFICE_CODE, rtaOfficeCode);
	    ActivitiResponseModel<List<RtaTaskInfo>> response = activitiService.getTasks(userId, map);
	    List<RtaTaskInfo> list = response.getActiveTasks();
	    List<CitizenApplicationModel> pendingApplications = new ArrayList<>();
	    if (ObjectsUtil.isNullOrEmpty(list)) {
	        log.debug("no task found");
	        return pendingApplications;
	    }
	    List<String> executionIdsList = list.stream().map(l->l.getTaskId()).collect(Collectors.toList());
	    List<ApplicationEntity> applications = applicationDAO.getApplications(executionIdsList);
	    applications.stream().forEach(e -> {
	        List<SlotEntity> slotEntityList = slotDAO.getSlots(e.getApplicationId(), timestamp, slotServiceType, null);
	        if (!ObjectsUtil.isNullOrEmpty(slotEntityList)) {
	            CitizenApplicationModel apps = new CitizenApplicationModel();
	            apps.setAppId(e.getApplicationId());
	            apps.setApplicationNumber(e.getApplicationNumber());
	            UserSessionModel sessionModel = userSessionService.getSession(e.getLoginHistory().getSessionId());
	            apps.setUniqueKey(sessionModel.getUniqueKey());
	            apps.setKeyType(sessionModel.getKeyType());
	            List<SlotModel> slotModelList = new ArrayList<>();
	            slotEntityList.stream().forEach(se -> {
	                SlotModel s = new SlotModel();
	                s.setDuration(se.getDuration());
	                s.setEndTime(se.getEndTime());
	                RTAOfficeModel office = new RTAOfficeModel();
	                office.setCode(se.getRtaOfficeCode());
	                s.setRtaOfficeModel(office);
	                s.setScheduledDate(s.getScheduledDate());
	                s.setScheduledTime(se.getScheduledTime());
	                s.setSlotId(se.getSlotId());
//	                s.setSlotStatus(SlotStatus.getSlotStatus(se.getSlotStatus()));
	                s.setStartTime(se.getStartTime());
//	                s.setType(se.getSlotType());
	                slotModelList.add(s);
	            });
	            pendingApplications.add(apps);
	        }
        });
	    return pendingApplications;
	}

    @Override
    public Boolean hasAppliedHPA(String prNumber) throws VehicleNotFinanced, UnauthorizedException, FinancerNotFound {
        RegistrationServiceResponseModel<Boolean> res=registrationService.hasAppliedHPA(prNumber);
        if(!res.getHttpStatus().equals(HttpStatus.OK)){
               if(res.getHttpStatus().equals(HttpStatus.FORBIDDEN)){
                   throw new VehicleNotFinanced("Vehicle is not Financed !!!");
               } else if(res.getHttpStatus().equals(HttpStatus.EXPECTATION_FAILED)){
                   throw new FinancerNotFound("Financer not found for given PR Number!!!");
               }
               // will return false if HPA already applied.
               return !res.getResponseBody();
           }
        return !res.getResponseBody();
    }

    @Override
    @Transactional
    public ResponseModel<Object> getAlerts(Long sessionId) {
    	log.info(":::getAlerts:::::start::::");
    	ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
    	 return validateAllDetails(appEntity); 
    	
    }
    
    @Override
    public List<VcrBookingData> getVcrDetails(String prNumber) throws UnauthorizedException {
        VcrServiceResponseModel<List<VcrBookingData>> result = vcrService.getVCRForRCNumber(prNumber);
        if (result.getHttpStatus() == HttpStatus.OK) {
            return result.getResponseBody();
        }
        log.info("error in http request " + result.getHttpStatus());
        return null;
    }

    @Override
    @Transactional
    public ResponseModel<List<RtaTaskInfo>> financierAction(String appNo, String userName, Long userId, String userRole, Status status, CommentModel comment) {
        ResponseModel<List<RtaTaskInfo>> response = new ResponseModel<>(ResponseModel.SUCCESS);
        ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
        if(ObjectsUtil.isNull(appEntity)){
            log.error("ApplicationNot found for app no :" + appNo);
            response.setStatus(ResponseModel.FAILED);
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("Application Not Found");
            return response;
        }
        String instanceId = appEntity.getExecutionId();
        log.info("Complete by online financer: " + userName + " app: " + appNo);
        if(status == Status.REJECTED){
            UserSessionEntity userSessionEntity = appEntity.getLoginHistory();
            userSessionEntity.setCompletionStatus(Status.REJECTED.getValue());
            userSessionDAO.saveOrUpdate(userSessionEntity);
        }
        //------insert in history -----------------
        Long time = DateUtil.toCurrentUTCTimeStamp();
        ApplicationApprovalHistoryEntity history = new ApplicationApprovalHistoryEntity();
        history.setApplicationEntity(appEntity);
        history.setCreatedBy(userName);
        history.setCreatedOn(time);
        history.setRtaUserId(userId);
        history.setRtaUserRole(userRole);
        history.setStatus(status.getValue());
        history.setIteration(appEntity.getIteration());
        if(!ObjectsUtil.isNull(comment)){
            history.setComments(comment.getComment());
        }
        applicationApprovalHistoryDAO.saveOrUpdate(history);
        //--------- complete activiti task-----------------
        Assignee assignee = new Assignee();
        assignee.setUserId(userName);
        ActivitiResponseModel<List<RtaTaskInfo>> actResponse =
                activitiService.completeTaskWithAction(assignee, financierTaskDef, status.getLabel(), instanceId, true);
        response.setActivitiTasks(actResponse.getActiveTasks());
        // ----------------------------------------
        return response;
    }
    
    @Override
    @Transactional
    public UserSessionModel getSession(String appNumber) {
    	ApplicationEntity appEntity = applicationDAO.getApplication(appNumber);
    	return userSessionConverter.converToModel(appEntity.getLoginHistory());
    }

	@Override
	@Transactional
	public String getProcessInstanceId(String applicationNumber) {
		
		return applicationDAO.getApplication(applicationNumber).getExecutionId();
	}
	
	@Override
    @Transactional
    public CustomerDetailsRequestModel getCustomerInfoBySession(Long sessionId) throws UnauthorizedException {
	    UserSessionEntity sessionEntity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
	    if(ObjectsUtil.isNull(sessionEntity)){
	        log.error("No User Session Found for sessionId : " + sessionId);
	        return null;
	    }
	    Long vehicleRcId = sessionEntity.getVehicleRcId();
	    if(ObjectsUtil.isNull(vehicleRcId)){
            log.error("VehicleRcId not found  for sessionId : " + sessionId);
            return null;
        }
        return getCustomerDetails(vehicleRcId);
    }
	
	@Override
    @Transactional
	public ResponseModel<String> reIterateApp(String appNumber) throws UnauthorizedException, ServiceValidationException{
		log.debug("Going to Start Re-Iteration for Application Number : " + appNumber);
		ResponseModel<String> resp = new ResponseModel<>(ResponseModel.SUCCESS);
		
		ApplicationEntity appEntity = applicationDAO.getApplication(appNumber);
		if(ObjectsUtil.isNull(appEntity)){
			log.error("No application Found with App : " + appNumber);
			resp.setStatus(ResponseModel.FAILED);
			resp.setMessage("Application Not Found");
			return resp;
		}
		UserSessionEntity userSession = appEntity.getLoginHistory();
		ServiceType serviceType = ServiceType.getServiceType(userSession.getServiceCode());
		if(serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION || serviceType == ServiceType.DIFFERENTIAL_TAX){
		    log.error("Re-initiation of this service is not allowed : " + appNumber);
            resp.setStatus(ResponseModel.FAILED);
            resp.setMessage("This Application can not be re-initiated please make a new request !!!");
            return resp;
        } else if(serviceType == ServiceType.FC_FRESH || serviceType == ServiceType.FC_RENEWAL || serviceType == ServiceType.FC_OTHER_STATION
                || serviceType == ServiceType.FC_RE_INSPECTION_SB){
            log.error("Re-initiation of this service is not allowed : " + appNumber);
            resp.setStatus(ResponseModel.FAILED);
            resp.setMessage("This Application can not be re-initiated. Please apply " + ServiceType.FC_RE_INSPECTION_SB.getLabel());
            return resp;
        }
		if (serviceType != ServiceType.NOC_CANCELLATION) {      
		    RegistrationServiceResponseModel res = registrationService.getNocDetails(null, userSession.getUniqueKey());           
		    if (res.getHttpStatus().equals(HttpStatus.OK)) {           
		        NocDetails noc = (NocDetails) res.getResponseBody();               
		        if(!ObjectsUtil.isNull(noc) && noc.getStatus()){              
		            log.error("NOC is found (found in registration) for : " + userSession.getUniqueKey());             
		            throw new ServiceValidationException(ServiceValidation.NOC_ISSUED.getCode(), ServiceValidation.NOC_ISSUED.getValue());  
		        }
		    }
		}
		
		if (serviceType == ServiceType.FRESH_RC_FINANCIER) {      
		    RegistrationServiceResponseModel res = registrationService.getFreshRcDataByApplicationNumber(appNumber);           
		    if (res.getHttpStatus().equals(HttpStatus.OK)) {           
		        FreshRcModel freshrc = (FreshRcModel) res.getResponseBody();               
		        if(ObjectsUtil.isNull(freshrc) ){              
		            log.error("No request found for fresh rc for this application" + appNumber);             
		            throw new ServiceValidationException(ServiceValidation.FRESH_RC_FINANCIER.getCode(), ServiceValidation.FRESH_RC_FINANCIER.getValue());  
		        } else{
		        	OwnerConscent ownerConscent = new OwnerConscent();
		        	ownerConscent.setOwnerConscent(null);
		        	ownerConscent.setOwnerComment(null);
		        	registrationService.reIterateAppForFreshRc(ownerConscent, appNumber);
		        }
		        
		    }
		}
		
		if(Status.getStatus(userSession.getCompletionStatus()) != Status.REJECTED){
			log.error("Trying to re-iterate not rejected App : " + appNumber);
			resp.setStatus(ResponseModel.FAILED);
			resp.setMessage("Application Yet Not Rejected !!!");
			return resp;
		}
		Integer iteration = ObjectsUtil.isNull(appEntity.getIteration()) ? 1 : appEntity.getIteration();
		if(iteration > maxIterationAllowed){
		    log.error("Maximum Number of Iteration for application can't be greater than " + maxIterationAllowed + " for app : " + appNumber);
            resp.setStatus(ResponseModel.FAILED);
            resp.setMessage("Application has reached maximum rejection iteration flow !!!");
            resp.setStatusCode(HttpStatus.FORBIDDEN.value());
            return resp;
		}
		String aadharNumber = null;
		if(serviceType == ServiceType.FINANCIER_SIGNUP || serviceType == ServiceType.BODYBUILDER_SIGNUP ||serviceType == ServiceType.DEALER_SIGNUP ||serviceType == ServiceType.PUC_USER_SIGNUP 
                ||serviceType == ServiceType.ALTERATION_AGENCY_SIGNUP ||serviceType == ServiceType.DRIVING_INSTITUTE ||serviceType == ServiceType.HAZARDOUS_VEH_TRAIN_INST ||serviceType == ServiceType.MEDICAL_PRACTITIONER){
		    aadharNumber = userSession.getAadharNumber();
		}
		UserSessionEntity userSessionEntity = userSessionDAO.getUserSessions(aadharNumber, userSession.getUniqueKey(), Status.PENDING, null);
        if (!ObjectsUtil.isNull(userSessionEntity)) {
            log.error("can't re-initiate, another session exist. sessionId : " + userSession.getSessionId());
            resp.setStatus(ResponseModel.FAILED);
            resp.setMessage(ServiceType.getServiceType(userSessionEntity.getServiceCode()).getLabel() + " with application number : " + applicationDAO.getApplicationFromSession(userSessionEntity.getSessionId()).getApplicationNumber() + " is already in process.");
            resp.setStatusCode(ServiceValidation.ANOTHER_SERVICE_EXISTS.getCode());
            return resp;
        }
        UserSessionEntity sameTypeSessionEntity = userSessionDAO.getLatestUserSession(aadharNumber, userSession.getUniqueKey(), null, ServiceType.getServiceType(userSession.getServiceCode()), Status.FRESH);
        if (!ObjectsUtil.isNull(sameTypeSessionEntity)) {
            log.error("can't re-initiate, another session exist. sessionId : " + sameTypeSessionEntity.getSessionId());
            resp.setStatus(ResponseModel.FAILED);
            resp.setMessage("An other application already initiated with same credential !!!");
            resp.setStatusCode(ServiceValidation.ANOTHER_SERVICE_EXISTS.getCode());
            return resp;
        }
		
		//---- for re-iteration of application -----------
		
		//lock the rejected executionId with application number for history.
		RejectedAppRemovedExeIdHistoryEntity rejectedHistoryEntity = new RejectedAppRemovedExeIdHistoryEntity();
		rejectedHistoryEntity.setApplicationNumber(appNumber);
		rejectedHistoryEntity.setExecutionId(appEntity.getExecutionId());
		rejectedHistoryEntity.setIteration(iteration);
		rejectedHistoryEntity.setServiceCode(userSession.getServiceCode());
		rejectedHistoryEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		rejectedHistoryEntity.setCreatedBy(CitizenConstants.CITIZEN_USERID);
		rejectedAppRemovedExeIdHistoryDAO.save(rejectedHistoryEntity);
		
		userSession.setCompletionStatus(Status.PENDING.getValue());
		userSessionDAO.saveOrUpdate(userSession);
		appEntity.setIteration(iteration + 1);
		//create a new executionId in activiti and assign to application.
		Assignee assignee = new Assignee();
        assignee.setUserId(CitizenConstants.CITIZEN_USERID);
		log.info("creating new process with rejection flow for ServiceType : " + userSession.getServiceCode());
		String processId = serviceMasterService.getProcessId(serviceType);
        ActivitiResponseModel<RtaProcessInfo> actResponse = activitiService.startProcess(assignee, processId, serviceType);
        List<RtaTaskInfo> tasks = actResponse.getActiveTasks();
        appEntity.setExecutionId(tasks.get(0).getProcessInstanceId());
        applicationDAO.saveOrUpdate(appEntity);
        
        //--- re initiate app at financer end ---------------
        if(ServiceUtil.getServiceCategory(serviceType) == ServiceCategory.REG_CATEGORY || ServiceUtil.getServiceCategory(serviceType) == ServiceCategory.PERMIT_FITNESS_CATEGORY){
        	try{
            	RegistrationServiceResponseModel<SaveUpdateResponse> res = registrationService.reIterateAppForFinance(appNumber);
            	if(res.getResponseBody().getStatus().equals(SaveUpdateResponse.FAILURE)){
            		if(res.getResponseBody().getCode() == HttpStatus.NOT_FOUND.value()){
            			log.info("Application not went to financer in prior iteration. App No : " + appNumber);
            		} else {
            			log.error("Error while Re-Iterating the app : " + appNumber);
            		}
            	}
            } catch(Exception ex){
            	log.error("Exception calling to registration for reiteration of app : " + ex.getMessage());
            	log.debug("Exception: ", ex);
            }
        }
		resp.setStatusCode(ServiceValidation.APPLICATION_RE_INITIATED.getCode());
		resp.setMessage(ServiceValidation.APPLICATION_RE_INITIATED.getValue());
		return resp;
	}
	
	@Override
	public boolean greenTaxValidate(ApplicationTaxModel appTaxModel) {
		log.debug("::::::greenTaxValidate::::::");
		boolean isTaxValidate = false;
		try {
			if(DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(),
					appTaxModel.getGreenTaxValidTo()))
				isTaxValidate = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isTaxValidate;
	}
	
	@Override
	public boolean payTaxValidate(ApplicationTaxModel appTaxModel) {
		log.debug("::::::payTaxValidate::::::");
		boolean isValidate = false;
		switch (TaxType.getTaxType(TaxType.getTaxType(appTaxModel.getTaxType()).getValue())) {
        case LIFE_TAX:
        	isValidate = true;
        break;
		}
		
		return isValidate;
	}
	
	@Override
	public Long getVehicleRcId(Long sessionId) {
		try {
			 UserSessionEntity sessionEntity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
			    if(ObjectsUtil.isNull(sessionEntity)){
			        log.error("No User Session Found for sessionId : " + sessionId);
			        return null;
			    }
			    Long vehicleRcId = sessionEntity.getVehicleRcId();
			    return vehicleRcId;
       
        } catch (Exception e) {
            log.error("Vehicle Rc Id not found");
        }
		return null;
	}

	@Transactional
	@Override
	public CitizenApplicationModel getCitizenAppDeatails(Long sessionId) {
		ApplicationEntity applicationEntity=applicationDAO.getApplicationFromSession(sessionId);
		CitizenApplicationModel citizenApplicationModel=new CitizenApplicationModel();
		citizenApplicationModel.setIteration(applicationEntity.getIteration());
		citizenApplicationModel.setApplicationNumber(applicationEntity.getApplicationNumber());
		citizenApplicationModel.setServiceType(ServiceType.getServiceType(applicationEntity.getServiceCode()));
		return citizenApplicationModel;
	}
	
	@Transactional
    @Override
    public ResponseModel<String> getLastMviInspectionComment(String appNumber) {
        try{
            ResponseModel<String> res = new ResponseModel<>(ResponseModel.SUCCESS);
            ApplicationEntity appEntity = applicationDAO.getApplication(appNumber);
        ApplicationApprovalHistoryEntity applicationApprovalHistoryEntity= applicationApprovalHistoryDAO.getRoleLastAction(UserType.ROLE_MVI.name(), appEntity.getApplicationId(), appEntity.getIteration());
        if(!ObjectsUtil.isNull(applicationApprovalHistoryEntity)){
            res.setData(applicationApprovalHistoryEntity.getComments());
        }
        else{
            log.error("No comment found for application:"+appNumber);
            res.setStatus(ResponseModel.FAILED);
            res.setMessage("No comment found for application");
        }
        return res;
        }catch(Exception ex){
            log.error("No comment found for application:"+appNumber);

        }
        return null;
    }
	
    @Transactional
    @Override
    public String getLastApplicationForMviInspectionComment(Long sessionId) throws UnauthorizedException {
        try {
            UserSessionEntity userSessionEntity=userSessionDAO.getUserSession(sessionId);
            //ApplicationEntity applicationEntity=applicationDAO.getLastApplicationForMviInspectionComment(sessionId, ServiceType.FC_RE_INSPECTION_SB.getCode());
            if(ObjectsUtil.isNull(userSessionEntity)){
                log.error("No application id found for sessionId:"+sessionId);
            }
            UserSessionEntity userSessionEntity2=userSessionDAO.getLastSessionForFitnessReInspection(userSessionEntity.getUniqueKey());
            ApplicationEntity applicationEntity=applicationDAO.getLastApplicationForMviInspectionComment(userSessionEntity2.getSessionId(), ServiceType.FC_RE_INSPECTION_SB.getCode());

            return applicationEntity.getApplicationNumber();
        } catch (Exception e) {
            log.error("No application Id found for sessionId:"+sessionId);

        }
        return null;
    }

    @Transactional
	@Override
	public Map<String, Object> getCustomerAppStatusDetails(String appNo) throws UnauthorizedException, VehicleNotFinanced {

		ApplicationEntity entity = applicationDAO.getApplication(appNo);
		UserSessionEntity userSessionEntity =userSessionDAO.getUserSession(entity.getLoginHistory().getSessionId());
		Map<String, Object> map = new HashMap<>();
		RegistrationServiceResponseModel<FinanceModel>  financedetails = registrationService.getFinancierDetails(userSessionEntity.getVehicleRcId());
		RegistrationServiceResponseModel<VehicleDetailsRequestModel> vehicleDetails = registrationService.getVehicleDetails(userSessionEntity.getVehicleRcId());
		RegistrationServiceResponseModel<FreshRcModel> freshRcDetails = registrationService.getFreshRcDataByAadharAndVehicleRcId(userSessionEntity.getVehicleRcId(), userSessionEntity.getAadharNumber());
		
		map.put("financedetails", financedetails);
		map.put("vehicleDetails", vehicleDetails);
		map.put("freshRcDetails", freshRcDetails);
		try {
			map.put("attachments", attachmentService.getAttachmentDetails(null,appNo, UserType.ROLE_CITIZEN));
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	@Transactional
	public Map<String, String> getApplicationFromExecId(List<String> exectionIds) {
		List<ApplicationEntity> applications = applicationDAO.getApplicationFromExecId(exectionIds);
		Map<String, String> appList = new HashMap<>();
		if(!ObjectsUtil.isNull(applications)){
			for(ApplicationEntity app : applications){
				appList.put(app.getExecutionId(), app.getApplicationNumber());
			}
		}
		return appList;
	}
	
	@Override
	@Transactional
	public ResponseModel<String> cancelApplication(String appNo){
		ResponseModel<String> resp = new ResponseModel<>(ResponseModel.SUCCESS);
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		if(ObjectsUtil.isNull(appEntity)){
			resp.setStatus(ResponseModel.FAILED);
			resp.setMessage("Invalid Application Number !!!");
			return resp;
		}
		UserSessionEntity userSession = appEntity.getLoginHistory();
		Status status = Status.getStatus(userSession.getCompletionStatus());
		if(status != Status.CANCELLED){
			ActivitiResponseModel<RtaProcessInfo> actResp = activitiService.deleteProcessInstance(appEntity.getExecutionId());
			if(actResp.getStatus().equalsIgnoreCase(ActivitiResponseModel.SUCCESS)){
				userSession.setCompletionStatus(Status.CANCELLED.getValue());
				userSessionDAO.update(userSession);
			} else {
				resp.setStatus(ResponseModel.FAILED);
				resp.setMessage("Application can't be cancelled. Please Try again later !!!");
				return resp;
			}
		} else {
			resp.setStatus(ResponseModel.FAILED);
			resp.setMessage("Application can't be cancelled. Current Status of application is : " + status);
			return resp;
		}
		return resp;
	}
}

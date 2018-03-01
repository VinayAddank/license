package org.rta.citizen.common.service;

import java.util.List;
import java.util.Map;

import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.ChallanDetailsModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.CrimeDetailsListModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.FitnessDetailsModel;
import org.rta.citizen.common.model.InsuranceDetailsModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.PucDetailsModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.model.vcr.VcrBookingData;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface ApplicationService {

	public VehicleDetailsRequestModel getVehicleDetails(Long vehicleRcId) throws UnauthorizedException;

	public CustomerDetailsRequestModel getCustomerDetails(Long vehicleRcId) throws UnauthorizedException;

	public InsuranceDetailsModel getInsuranceDetails(Long vehicleRcId) throws UnauthorizedException;

	public FinanceModel getFinancierDetails(Long vehicleRcId) throws UnauthorizedException;

	public CrimeDetailsListModel getCrimeDetails(String prNo) throws UnauthorizedException;

	public PucDetailsModel getPucDetails(Long vehicleRcId) throws UnauthorizedException;

	public NocDetails getNOCDetails(Long vehicleRcId) throws UnauthorizedException;

	public List<ChallanDetailsModel> getChallanDetails(Long vehicleRcId) throws UnauthorizedException;

	public TaxModel getTaxDetails(Long vehicleRcId) throws UnauthorizedException;

	public List<PermitHeaderModel> getPermitDetails(Long vehicleRcId) throws UnauthorizedException;

	public FitnessDetailsModel getFitnessDetails(Long vehicleRcId) throws UnauthorizedException;

	public CitizenApplicationModel getApplicationModel(Long sessionId);

	CitizenApplicationModel saveOrUpdate(UserSessionModel session, CitizenApplicationModel applicationModel);

	public String getProcessInstanceId(Long sessionId);

	public void saveUpdateExecutionId(Long sessionId, String executionId);

	public ResponseModel<Object> generalDetailsNext(Long sessionId, String taskDef, String userName);
	
    public String generateApplicationNumber(UserSessionModel session);

    public List<CitizenApplicationModel> getSlotPendingApplications(Long timestamp, ServiceType serviceType,
            SlotServiceType slotServiceType, String rtaOfficeCode, String userId);
    public CitizenApplicationModel getCitizenApplication(String applicationNumber);

    public UserModel getRTAUserByToken(String token) throws UnauthorizedException;

    public Boolean hasAppliedHPA(String prNumber) throws VehicleNotFinanced, UnauthorizedException, FinancerNotFound;

    public ApplicationTaxModel getTaxDetails(String prOrTrNumber) throws UnauthorizedException;
    
    public ResponseModel<Object> getAlerts(Long sessionId);

    public List<VcrBookingData> getVcrDetails(String prNumber) throws UnauthorizedException;
    
    public boolean taxValidate(ApplicationTaxModel appTaxModel);
    
    public boolean permitValidate(long prIssueDate, int regType);
    
    public boolean fitnessValidate(long prIssueDate, int regType);
    
    public ResponseModel<Object> validateAllDetails(ApplicationEntity appEntity);
    
    public ResponseModel<List<RtaTaskInfo>> financierAction(String appNo, String userName, Long userId, String userRole, Status status, CommentModel comment);

	public UserSessionModel getSession(String appNumber);
	
	public String getProcessInstanceId(String applicationNumber);

	public CustomerDetailsRequestModel getCustomerInfoBySession(Long sessionId) throws UnauthorizedException;

    public CitizenApplicationModel getApplicationById(Long appId);
	public ResponseModel<String> reIterateApp(String appNumber) throws UnauthorizedException, ServiceValidationException;	
	boolean fitnessValidate(ApplicationTaxModel appTaxModel, int regType);
	
	public boolean greenTaxValidate(ApplicationTaxModel appTaxModel);
	public boolean payTaxValidate(ApplicationTaxModel appTaxModel);

	public Long getVehicleRcId(Long sessionId);
	public CitizenApplicationModel getCitizenAppDeatails(Long sessionId);
	
	public String getLastApplicationForMviInspectionComment(Long sessionId) throws UnauthorizedException;
    public ResponseModel<String> getLastMviInspectionComment(String appNumber) throws UnauthorizedException;
    
    public Map<String, Object> getCustomerAppStatusDetails(String appNo) throws UnauthorizedException, VehicleNotFinanced;
    
    public Map<String, String> getApplicationFromExecId(List<String> exectionIds);
    
    /**
     * Cancell the application in citizen
     * delete activiti instance from activiti
     * 
     * @param appNo
     * @return
     */
    public ResponseModel<String> cancelApplication(String appNo);

	public void createLog(String status, String msg, Long sessionId, Integer code);
}

package org.rta.citizen.common.service.registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.rta.MessageConfig;
import org.rta.citizen.aadharseeding.rc.model.RCAadharSeedModel;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.PermitDetailsType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadhaarTCSDetailsRequestModel;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.model.ChallanDetailsModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.CrimeDetailsListModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.DealerModel;
import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.FinancerModel;
import org.rta.citizen.common.model.FitnessDetailsModel;
import org.rta.citizen.common.model.HPAHPTSyncModel;
import org.rta.citizen.common.model.InsuranceDetailsModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.OwnerConscent;
import org.rta.citizen.common.model.PermitDetailsModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.PermitTypeModel;
import org.rta.citizen.common.model.PermitTypeVehicleClassModel;
import org.rta.citizen.common.model.PucDetailsModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.SuspendedRCNumberModel;
import org.rta.citizen.common.model.SyncDataModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.model.VehicleAlterationUpdateModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.VehicleClassDescModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.PayUResponse;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.model.payment.TaxTypeModel;
import org.rta.citizen.common.model.payment.TransactionDetailModel;
import org.rta.citizen.common.model.vcr.VcrModel;
import org.rta.citizen.duplicateregistration.model.DuplicateRegistrationModel;
import org.rta.citizen.fitness.cfx.model.CFXModel;
import org.rta.citizen.fitness.cfx.model.CFXNoticeModel;
import org.rta.citizen.freshrc.FinancerFreshContactDetailsModel;
import org.rta.citizen.freshrc.FreshRcModel;
import org.rta.citizen.freshrc.ShowcaseInfoRequestModel;
import org.rta.citizen.freshrc.ShowcaseNoticeInfoModel;
import org.rta.citizen.hpt.model.FinanceOtherServiceModel;
import org.rta.citizen.permit.model.PermitAuthorizationCardModel;
import org.rta.citizen.permit.model.PermitCodeDescModel;
import org.rta.citizen.permit.model.PermitTempPermitModel;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxReportModel;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.rta.citizen.vehiclereassignment.model.VehicleReassignmentModel;

public interface RegistrationService {

	public RegistrationServiceResponseModel<TokenModel> loginIfRequired(String userName, String password)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<ApplicationModel> getPRDetails(String prNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<ApplicationModel> getTRDetails(String trNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<ApplicationModel> getDetails(String uniqueKey, KeyType keyType)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<AadharModel> aadharAuthentication(AadhaarTCSDetailsRequestModel model)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<VehicleDetailsRequestModel> getVehicleDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<CustomerDetailsRequestModel> getCustomerDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<InsuranceDetailsModel> getInsuranceDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<FinanceModel> getFinancierDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<CrimeDetailsListModel> getCrimeDetails(String prNo)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<PucDetailsModel> getPucDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<NocDetails> getNocDetails(Long vehicleRcId, String prNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<ChallanDetailsModel>> getChallanDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<TaxModel> getTaxDetails(Long vehicleRcId) throws UnauthorizedException;
	
	public RegistrationServiceResponseModel<TaxTypeModel> getTaxTypeByCov(String cov) throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<PermitHeaderModel>> getPermitDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<FitnessDetailsModel> getFitnessDetails(Long vehicleRcId, Long mviUserId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<RTAOfficeModel> getRTAOfficeByMandal(Integer code, Long vehicleRcId);

	public RegistrationServiceResponseModel<Boolean> getIsVehicleReassignmentApplicable(String prNumber)
			throws UnauthorizedException;
	
	public RegistrationServiceResponseModel<Boolean> getIsLifeTaxPaid(Long vehicleRcId)
			throws UnauthorizedException;

	/**
	 * Get Financier by pr number from registration
	 * 
	 * @param prNumber
	 * 
	 * @return
	 */
	public RegistrationServiceResponseModel<FinanceOtherServiceModel> getFinancier(String prNumber)
			throws UnauthorizedException;

	/**
	 * Send other applications to Financier
	 * 
	 * @param form
	 * @return
	 * @throws UnauthorizedException
	 */
	public RegistrationServiceResponseModel<FinanceOtherServiceModel> sendOtherAppToFinancier(
			FinanceOtherServiceModel form) throws UnauthorizedException;

	/**
	 * apply HPA to registration
	 * 
	 * @param appNo
	 * @param prNumber
	 * @param quoteAmount
	 * @return
	 */
	public RegistrationServiceResponseModel<Object> saveHPA(String appNo, String prNumber, Long quoteAmount)
			throws UnauthorizedException;

	/**
	 * Is online financed
	 * 
	 * @param prNum
	 * @return
	 * @throws UnauthorizedException
	 */
	public RegistrationServiceResponseModel<HashMap<String, Boolean>> isOnlineFinanced(String prNum)
			throws UnauthorizedException;

	/**
	 * Get financer list from registration aggreed for HPA
	 * 
	 * @param applicationNumber
	 * @return
	 */
	public RegistrationServiceResponseModel<List<FinancerModel>> getFinancerList(String applicationNumber)
			throws UnauthorizedException;

	/**
	 * Approve Financier to registraion db
	 * 
	 * @param applicationNumber
	 * @param financerModel
	 * @return
	 * @throws UnauthorizedException
	 */
	public RegistrationServiceResponseModel<Object> approveFinancier(String applicationNumber,
			FinancerModel financerModel) throws UnauthorizedException;

	/**
	 * get RTA User details by rta token
	 * 
	 * @param rtaToken
	 * @return
	 */
	public UserModel getRtaUserByToken(String rtaToken) throws UnauthorizedException;

	public RegistrationServiceResponseModel<ApplicationTaxModel> getTaxDetails(String prOrTrNumber);

	public String getChallanNumber();

	public TransactionDetailModel getEncryptedSBIParameter(TransactionDetailModel transactionDetailModel);

	public TransactionDetailModel decryptSBIResponse(TransactionDetailModel transactionDetailModel);

	public TransactionDetailModel getVerificationEncrytData(TransactionDetailModel transactionDetailModel);

	public RegistrationServiceResponseModel<List<NocDetails>> getNocAddressList(String districtCode)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<UserModel> getRTAUserFromToken(String rtaToken)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<NocDetails> getNocAddressDetails(String nocAddressCode)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<RTAOfficeModel> getRtaDetails(String code) throws UnauthorizedException;

	public RegistrationServiceResponseModel<AadharModel> getAadharDetails(Long aadharNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<DealerModel> getUserDetails(String userName) throws UnauthorizedException;

	public RegistrationServiceResponseModel<Boolean> hasAppliedHPA(String prNum) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveSlots(CitizenApplicationModel citizenApplication)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<PermitTypeVehicleClassModel> getPermitTypeByTr(String trPrNumber)
			throws UnauthorizedException;

	// public RegistrationServiceResponseModel<?>
	// getAllUserAttachmentDetails(String userName) throws
	// UnauthorizedException;

	public RegistrationServiceResponseModel<?> getUserAttachmentDetails(String userName, Integer docId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> multipleSaveOrUpdateForUser(
			List<AttachmentModel> models) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> updateVehicleAlterationForBodyBuilder(
			VehicleBodyModel model) throws UnauthorizedException;

	public RegistrationServiceResponseModel<UserModel> getUser(String userName) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForNocDetails(NocDetails model)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForCustomerDetails(AddressChangeModel model)
			throws UnauthorizedException;

    public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForVehicleAlterationDetails(VehicleAlterationUpdateModel model) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForUser(AttachmentModel model)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateDealer(UserModel model)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<UserSignupModel> saveOrUpdateUser(UserSignupModel model)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForDuplicateRegistrationDetails(
			DuplicateRegistrationModel model) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> updateDataHPAHPT(HPAHPTSyncModel hPAHPTSyncModel)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<MessageConfig> getCommunicationConfig() throws UnauthorizedException;

	public RegistrationServiceResponseModel<String> getCustomerInvoice(Long vehicleRcId, String regType, boolean isNoc)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<String> getSignature(Long vehicleRcId) throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<VehicleClassDescModel>> getVehicleClassDesc(String regCategoryCode,
			Integer alterationCategory) throws UnauthorizedException;

	public RegistrationServiceResponseModel<VehicleBodyModel> getVehicleAlterationDetails(Long vehicleRcId, String authToken)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForCitizenCommonSerives(
			CommonServiceModel model) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveOrUpdateForVehicleReassignmentSerives(
			VehicleReassignmentModel model) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> syncTheft(TheftIntimationRevocationModel theftModel,
			String prNumber) throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<AttachmentModel>> getAttachmentsDetails(String chassisNumber,
			Long vehicleRcId) throws UnauthorizedException;

	public RegistrationServiceResponseModel<ShowcaseNoticeInfoModel> getShowcaseInfo(ShowcaseInfoRequestModel request)
			throws UnauthorizedException;

	RegistrationServiceResponseModel<FreshRcModel> getFreshRcDataByAadharAndVehicleRcId(Long vehicleRcId,
			String aadharNumber) throws UnauthorizedException;

	RegistrationServiceResponseModel<SaveUpdateResponse> saveCustomerInfoForDataEntry(String token,
			CustomerDetailsRequestModel customerRequest) throws UnauthorizedException;

	public RegistrationServiceResponseModel<VehicleClassDescModel> getCovDetails(String prNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<PermitTypeModel>> getPermitType(String cov)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> syncData(SyncDataModel syncDataModel)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<PermitTempPermitModel> getPermitTempPermits(String prNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<Map<String, Object>> getSelectedPukkaTempPermit(String prNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<PermitDetailsModel> getPermitCertificate(Long vehicleRcId,
			String certificateType, Long mviUserId) throws UnauthorizedException;

	public RegistrationServiceResponseModel<PermitAuthorizationCardModel> getPermitAuthCardDetails(String prNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<Boolean> isUserExistsByAadharAndType(String aadharNumber, UserType userType)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<Boolean> hasAppliedHPA(Long vehicleRcId) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> aadhaarSeedingWithSystem(
			RCAadharSeedModel rcAadharSeedModel) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> reIterateAppForFinance(String appNo)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<PermitCodeDescModel>> getRouteGoodsConditionsForTempPermit(
			PermitDetailsType detailsType, String primaryPermit, String temporaryPermit) throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<PermitCodeDescModel>> getGoodsRouteCondnsForPrimaryPermit(
			PermitDetailsType permitDetailsType, String cov, String permitType) throws UnauthorizedException;

	public RegistrationServiceResponseModel<FcDetailsModel> getFitnessCertificate(Long vehicleRcId, Long mviUserId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SuspendedRCNumberModel> getSuspensionDetails(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<RTAOfficeModel>> getRTAOfficeByState(String stateCode);

	public RegistrationServiceResponseModel<FinanceModel> getFinanceInfo(String prNum) throws UnauthorizedException;

    public RegistrationServiceResponseModel<SaveUpdateResponse> saveFCFXNote(CFXModel cfxModel) throws UnauthorizedException;

    public RegistrationServiceResponseModel<CFXNoticeModel> getFCFXNote(String prNumber) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> syncPayTaxData(TaxModel taxModel)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<RegistrationCategoryModel> getRegCategoryByRcId(Long vehicleRcId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<VehicleClassDescModel>> getAlterationCovList(String prNo,
			String regCatCode) throws UnauthorizedException;

	RegistrationServiceResponseModel<FinancerFreshContactDetailsModel> getFinancerFreshContactDetails(Long vehicleRcId)
			throws UnauthorizedException;

	// public FreshRcAppStatusDetailsModel getCustomerAppStatusDetails(Long
	// vehicleRcId) throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> submitOwnerConscent(OwnerConscent ownerConscent, String appNumber) 
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<FreshRcModel> getFreshRcDataByApplicationNumber(String applicationNumber) 
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveStoppageTaxDetails(List<ApplicationFormDataModel> models, String prNumber) 
			throws UnauthorizedException;
	
	public VcrModel getVCRTax(String docType, String docNumber) 
			throws UnauthorizedException ;

	public RegistrationServiceResponseModel<SaveUpdateResponse> reIterateAppForFreshRc(OwnerConscent ownerConscent, String appNumber) 
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<SaveUpdateResponse> saveStoppageTaxReportDetails(StoppageTaxReportModel model,	String prNumber, String userName) 
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<StoppageTaxDetailsModel> getStoppageTaxDetails(String prNumber)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<List<StoppageTaxReportModel>> getStoppageTaxReportDetails(String applicationNo)
			throws UnauthorizedException;
	
	public RegistrationServiceResponseModel<List<String>> getBodyTypeList()
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<StoppageTaxReportModel> getStoppageTaxSingleReportDetails(Long stoppageTaxReportId)
			throws UnauthorizedException;

	public RegistrationServiceResponseModel<UserModel> getUserDetails(Long userId) 
			throws UnauthorizedException;

    public TransactionDetailModel getPayUVerificationEncrytData(TransactionDetailModel transactionDetailModel);	

}

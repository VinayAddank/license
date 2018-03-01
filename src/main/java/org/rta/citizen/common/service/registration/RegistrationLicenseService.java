package org.rta.citizen.common.service.registration;

import java.util.List;

import org.rta.citizen.common.UserModel;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.model.updated.LicenseIDPDtlsModel;
import org.rta.citizen.licence.model.updated.SupensionCancellationModel;
import org.rta.citizen.licence.model.updated.SuspensionRevocationModel;

public interface RegistrationLicenseService {

	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getLicenseHolderDtls(String aadharNo,
			Long licenceHolderId, String uniqueNumber) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLicenseHolderDtls(LicenseHolderDtlsModel model)
			throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLicenseHolderDtls(LicenseHolderPermitDetails models)
			throws UnauthorizedException;

	public RegLicenseServiceResponseModel<List<LearnersPermitDtlModel>> getLearnerPermitDtls(Long licenceHolderId)
			throws UnauthorizedException;

	public RegLicenseServiceResponseModel<List<String>> getVehicleClasses(String aadharNo, String showVehicleClassr)
			throws UnauthorizedException;

	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getLicenseHolderDtlsForDriver(String aadharNo,
			String uniqueKey) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLearnerPermitDetails(
			List<LearnersPermitDtlModel> models, String aadharNumber) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveLicenseHolderDtlsForDriver(
			LicenseHolderPermitDetails model, String aadharNo) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> updateLicenseHolderDetails(LicenseHolderDtlsModel model)
			throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> addEndorsmentsInDriverPermitDetails(
			LicenseHolderPermitDetails model) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> addDriverPermitDetailsForDLRE(
			List<DriversLicenceDetailsModel> models, String aadharNumber) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<List<RTAOfficeModel>> getRtaOfficeList() throws UnauthorizedException;

	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getLicenseHolderDtlsForDriver(String uniqueKey)
			throws UnauthorizedException;

	public RegLicenseServiceResponseModel<LicenseHolderDtlsModel> getLicenseHolderDtls(String aadhaarNumber,
			String passportNumber) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> driverLicenceCommonService(String aadharNumber,
			List<DriversLicenceDetailsModel> models) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<String> updateInLicenseHolderDetails(String serviceCode,
			LicenseHolderDtlsModel model) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> updateLicenceAttachmentsDetails(
			List<AttachmentModel> models, String aadhaarNo) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<LicenseHolderPermitDetails> getForgotLicenceNumber(String dob,
			String aadharNumber) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> saveUpdateIntrnationalLicenseDtls(
			LicenseIDPDtlsModel model, String aadharNo) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> suspendCancelLicense(String dLNumber,
			SupensionCancellationModel model) throws UnauthorizedException;

	public RegLicenseServiceResponseModel<SaveUpdateResponse> licenseRevokeSuspension(String dLNumber,
			SuspensionRevocationModel model) throws UnauthorizedException;

	public RegistrationServiceResponseModel<UserModel> getUserDetails(String userName) throws UnauthorizedException;

}

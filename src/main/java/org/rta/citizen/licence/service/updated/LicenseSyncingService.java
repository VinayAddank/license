package org.rta.citizen.licence.service.updated;

import java.util.Map;

import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.SupensionCancellationModel;

public interface LicenseSyncingService {

	public ResponseModel<String> updateLicenseHolderDetails(Long applicationId, String aadharNumber);

	public ResponseModel<String> saveUpdateDriversEndorsmentsDtls(Status status, Long applicationId,
			String aadharNumber, String uniqueKey);

	public ResponseModel<String> saveLearnerPermitDetailsForLLR(Long applicationId, String aadharNumber,
			String uniqueKey, Status status);

	public ResponseModel<String> saveUpdateLearnerPermitDtls(Long applicationId, String aadharNumber);

	public ResponseModel<String> saveUpdateDriversPermitDtlsForDLRE(Status status, String applicationNo,
			String aadharNumber, String uniqueKey);

	public ResponseModel<String> driverLicenceCommonService(Long applicationId, String aadharNumber,
			String serviceCode);

	public ResponseModel<String> updateInLicenseHolderDetails(Long sessionId, LLRegistrationModel model);

	public Map<String, Object> getEmployeeUserId(Long applicationId, ServiceType serviceType);
	
	public ResponseModel<String> suspendCancelLicense(Long applicationId, String aadharNumber);
	
	public ResponseModel<String> licenseRevokeSuspension(Long applicationId, String aadharNumber);

	ResponseModel<String> suspendCancelLicenseByAO(SupensionCancellationModel supensionCancellationModel, UserSessionEntity usEntity);

}

package org.rta.citizen.licence.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.ExaminerFoundException;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.SlotBookingException;
import org.rta.citizen.common.exception.TaskNotFound;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.licence.entity.tests.QuestionnaireFeedbackEntity;
import org.rta.citizen.licence.model.ClassofVechicleModel;
import org.rta.citizen.licence.model.LLRAgeGroupModel;
import org.rta.citizen.licence.model.LicenceDetailsModel;
import org.rta.citizen.licence.model.LlrRetestDetailsRequestModel;
import org.rta.citizen.licence.model.tests.QuestionModel;
import org.rta.citizen.licence.model.updated.ExamResultModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.model.updated.QuestionnaireFeedbackModel;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface LicenceService {

	public LLRAgeGroupModel getAgeGroup(Long sessionId) throws Exception;

	public List<ClassofVechicleModel> getCOV(String ageGroup, String serviceCode);

	public List<ClassofVechicleModel> getCovList(List<String> vehicleClass);

	public List<LicenceDetailsModel> getDLDetails(Long sessionId, String serviceType);

	public List<LicenceDetailsModel> getLlrDetails(Long sessionId, String serviceType);

	public List<LlrRetestDetailsRequestModel> llRetestDetails(Long sessionId);

	public Integer getLLFAttempts(String appNo);

	public Boolean saveExamResults(ExamResultModel examResultModel, CitizenApplicationModel appModel)
			throws ParseException;

	public ResponseModel<String> saveUpdateLicenseHolderDtls(Long applicationId, String aadharNumber);

	public List<RtaTaskInfo> completeExamTask(String token, String appNo, Status status);

	public LicenseHolderPermitDetails getLicenseDetails(Long sessionId, boolean isAll);

	public ResponseModel<List<QuestionnaireFeedbackEntity>> saveQuestionsFeedbackEntities(String applicationNumber,
			List<QuestionModel> questions, SlotServiceType testType, String username);

	public void saveQuestionsFeedback(List<QuestionnaireFeedbackEntity> entities);

	public List<QuestionModel> getQuestions(SlotServiceType type);

	public ResponseModel<String> saveUpdateDriversFreshPermitDtls(Status status, Long applicationId,
			String aadharNumber, String uniqueKey);

	public ResponseModel<List<QuestionnaireFeedbackModel>> getQuestionsFeedbackEntities(String applicationNumber,
			SlotServiceType testType, String username);

	public List<RtaTaskInfo> callAfterGettingDetails(Long sessionId, String taskDef, String userName);

	public Date getDLValidity(String dateOfBirth);

	public LicenseHolderPermitDetails getLicenceDetails(String applicationNo);

	public Boolean isPaymentCompleted(Long sessionId);

	public ResponseModel<String> saveUpdateIntrnationalLicenseDtls(Status status, Long applicationId,
			String aadharNumber, String uniqueKey);



	public LicenseHolderPermitDetails getSuspCancelDriverLicence(String dLNumber, String userName, UserType userType)
			throws UnauthorizedException, ForbiddenException, AadharNotFoundException, DataMismatchException,
			NotFoundException, AadharAuthenticationFailedException, VehicleNotFinanced, FinancerNotFound,
			ParseException, ServiceValidationException, ConflictException;

}

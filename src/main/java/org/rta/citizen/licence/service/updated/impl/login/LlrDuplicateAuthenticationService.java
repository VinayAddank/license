package org.rta.citizen.licence.service.updated.impl.login;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.FinancerNotFound;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class LlrDuplicateAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(LlrDuplicateAuthenticationService.class);

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException,
			AadharNotFoundException, DataMismatchException, NotFoundException, AadharAuthenticationFailedException,
			VehicleNotFinanced, FinancerNotFound, ConflictException {

		if (ObjectsUtil.isNull(citizen) || StringsUtil.isNullOrEmpty(citizen.getUid_num())
				|| StringsUtil.isNullOrEmpty(citizen.getLlrNumber())) {
			log.info("Citizen LLR OR Aadhar details not provided.");
			throw new AadharNotFoundException("Citizen LLR OR Aadhar details not provided.");
		}
		String aadharNumber = citizen.getUid_num();
		String uniqueKey = getUniqueKey(citizen);
		KeyType keyType = getKeyType();
		// TODO: This should be removed soon with more generic approach
		List<UserSessionEntity> approveSessions = userSessionDAO.getApprovedAppSessions(aadharNumber);
		for (UserSessionEntity approve : approveSessions) {
			if (ObjectsUtil.isNullOrEmpty(approveSessions) && approve.getUniqueKey().equals(uniqueKey)
					&& (approve.getServiceCode().equals(ServiceType.DL_FRESH.getCode()))) {
				log.info("User can't apply for LLD as He has already having Driving License with LLR Number is: "
						+ approve.getUniqueKey());
				throw new ConflictException(
						"User can't apply for LLD as He has already having Driving License with LLR Number is: "
								+ approve.getUniqueKey());
			}
		}
		Long timestamp = DateUtil.toCurrentUTCTimeStamp();
		LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(citizen.getUid_num(), uniqueKey,
				keyType, DateUtil.getLongDate(DateUtil.extractDateAsString(timestamp), "00:00:00"),
				DateUtil.getLongDate(DateUtil.extractDateAsString(timestamp), "23:59:59"));
		Boolean newLoggedIn = Boolean.FALSE;
		Integer loginCount = Integer.valueOf(1);
		if (!ObjectsUtil.isNull(loginHistory)) {
			loginCount = loginHistory.getLoginCount();
			if (loginCount >= maxAllowedPerDay) {
				throw new ForbiddenException("login limit exceeded");
			}
			loginCount++;
		} else {
			newLoggedIn = Boolean.TRUE;
		}

		String pinCode = null;
		boolean validate = false;
		String rtaOfficeCode = null;
		Boolean isExpired = false;
		if (isValidllrNo(citizen)) {
			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = registrationLicenseService
					.getLicenseHolderDtls(aadharNumber, null, citizen.getLlrNumber());
			if (null != holderDetails) {
				LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
				if (null != holder) {
					LicenseHolderDtlsModel holderDetail = holder.getLicenseHolderDetails();
					List<LearnersPermitDtlModel> permitDtlsModel = holder.getLearnersPermitDetailsList();
					if ("Y".equalsIgnoreCase(holderDetail.getIsAdharVerify())
							|| null != holderDetail.getRtaOfficeDetails()) {
						rtaOfficeCode = holderDetail.getRtaOfficeDetails().getCode();
						pinCode = holderDetail.getPresAddrPinCode();
						if (!ObjectsUtil.isNullOrEmpty(permitDtlsModel) && holderDetail.getLicenceHolderId()
								.equals(permitDtlsModel.get(0).getLicenceHolderId()))
							validate = true;
					} else {
						log.info("Aadhaar is not seeded OR RTA office code is not present:" + citizen.getLlrNumber());
						throw new NotFoundException(
								"Aadhaar is not seeded OR RTA office code is not present:" + citizen.getLlrNumber());
					}
					for (LearnersPermitDtlModel learnersDtlModel : permitDtlsModel) {
						if (learnersDtlModel.getValidTo().before(new Date())) {
							isExpired = true;
						}
					}
				}
			}
		} else {
			throw new NotFoundException("LLR Number is Not Valid");
		}
		if (!validate) {
			log.info("No Learning License entry OR Aadhaar-LLR combination found for provided details:"
					+ citizen.getLlrNumber());
			throw new NotFoundException(
					"No Learning License entry OR Aadhaar-LLR combination found for provided details:"
							+ citizen.getLlrNumber());
		}
		if (isExpired) {
			log.info("You Cannot apply for LLD, Your Learners License has Expired.");
			throw new ConflictException(
					"You Cannot apply for LLD, Your Learners License has Expired.:" + citizen.getLlrNumber());
		}

		try {
			if (!StringsUtil.isNullOrEmpty(pinCode) || !authenticateAadhar) {
				citizen.setPincode(pinCode);
				String dob = null;
				if (authenticateAadhar) {
					dob = aadharAuthentication(citizen).getDob();
				} else {
					dob = "24-03-1986";
				}
				TokenModel tokenModel;
				UserSessionEntity activeSession = userSessionDAO.getActiveSession(aadharNumber, uniqueKey, keyType,
						serviceType);
				if (ObjectsUtil.isNull(activeSession)) {
					activeSession = new UserSessionEntity();
					activeSession.setAadharNumber(aadharNumber);
					activeSession.setCompletionStatus(Status.FRESH.getValue());
					activeSession.setCreatedBy(aadharNumber);
					activeSession.setCreatedOn(timestamp);
					activeSession.setKeyType(keyType);
					activeSession.setServiceCode(serviceType.getCode());
					activeSession.setUniqueKey(uniqueKey);
				}

				activeSession.setModifiedBy(aadharNumber);
				activeSession.setModifiedOn(timestamp);
				activeSession.setLoginTime(timestamp);
				userSessionDAO.saveOrUpdate(activeSession);
				tokenModel = generateToken(activeSession);
				if (newLoggedIn) {
					loginHistory = new LoginAttemptHistoryEntity();
					loginHistory.setAadharNumber(aadharNumber);
					loginHistory.setCreatedBy(aadharNumber);
					loginHistory.setCreatedOn(timestamp);
					loginHistory.setKeyType(keyType);
					loginHistory.setLoginTime(timestamp);
					loginHistory.setModifiedBy(aadharNumber);
					loginHistory.setModifiedOn(timestamp);
					loginHistory.setUniqueKey(uniqueKey);
				}
				loginHistory.setLoginCount(loginCount);

				// create application if not exists
				ApplicationEntity citizenApplicationEntity = applicationDAO
						.getApplicationFromSession(activeSession.getSessionId());
				if (ObjectsUtil.isNull(citizenApplicationEntity)) {
					citizenApplicationEntity = new ApplicationEntity();
					citizenApplicationEntity.setCreatedBy(activeSession.getAadharNumber());
					citizenApplicationEntity.setCreatedOn(timestamp);
					citizenApplicationEntity.setApplicationNumber(applicationService
							.generateApplicationNumber(userSessionConverter.converToModel(activeSession)));
					param.put(CitizenConstants.CREATE_NEW_PROCESS, true);
				} else {
					param.put(CitizenConstants.CREATE_NEW_PROCESS, false);
				}
				citizenApplicationEntity.setModifiedBy(activeSession.getAadharNumber());
				citizenApplicationEntity.setModifiedOn(timestamp);
				citizenApplicationEntity.setServiceCode(serviceType.getCode());
				citizenApplicationEntity.setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
				citizenApplicationEntity.setLoginHistory(activeSession);
				citizenApplicationEntity.setRtaOfficeCode(rtaOfficeCode);
				citizenApplicationEntity.setIsAuthenticated(true);
				citizenApplicationEntity.setApplicantDob(dob);
				applicationDAO.saveOrUpdate(citizenApplicationEntity);
				loginAttemptsDAO.saveOrUpdate(loginHistory);
				return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
			}
		} catch (HttpClientErrorException e) {
			log.info("error in registration service response : ", e);
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				log.info("Not found " + keyType.toString() + " " + uniqueKey);
				throw new NotFoundException("Not found " + keyType.toString() + " " + uniqueKey);
			}
		}
		throw new UnauthorizedException();
	}

	@Override
	protected String getUniqueKey(AuthenticationModel citizen) {
		String llrNumber = citizen.getLlrNumber();
		if (StringsUtil.isNullOrEmpty(llrNumber)) {
			throw new IllegalArgumentException("LLR Number is missing");
		} else if (!StringsUtil.isNullOrEmpty(llrNumber)) {
			return llrNumber;
		}
		return null;
	}

	protected Boolean isValidllrNo(AuthenticationModel citizen) throws DataMismatchException {
		String llrNumber = citizen.getLlrNumber();
		if (!StringsUtil.isNullOrEmpty(llrNumber) && llrNumber.startsWith(ServiceCategory.LL_CATEGORY.getCode())) {
			return true;
		} else {
			log.info("LLR Number is Not Valid : " + citizen.getLlrNumber());
			return false;
		}

	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.LLD;
	}
}

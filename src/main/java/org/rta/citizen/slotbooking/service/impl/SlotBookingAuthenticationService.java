package org.rta.citizen.slotbooking.service.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.LoginAttemptHistoryEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.ServiceValidation;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.AadharNotFoundException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.ForbiddenException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.AuthenticationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.TokenModel;
import org.rta.citizen.common.service.AuthenticationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class SlotBookingAuthenticationService extends AuthenticationService {

	private static final Logger log = Logger.getLogger(SlotBookingAuthenticationService.class);

	@Override
	@Transactional
	public ResponseModel<TokenModel> authenticate(AuthenticationModel citizen, ServiceType serviceType,
			HashMap<String, Boolean> param, String userReq) throws UnauthorizedException, ForbiddenException, AadharNotFoundException,
			DataMismatchException, NotFoundException, AadharAuthenticationFailedException, ServiceValidationException {
		String uniqueKey = getUniqueKey(citizen);
		Long timestamp = DateUtil.toCurrentUTCTimeStamp();
		LoginAttemptHistoryEntity loginHistory = loginAttemptsDAO.getLoginAttempts(citizen.getUid_num(), uniqueKey,
				getKeyType(), DateUtil.getLongDate(DateUtil.extractDateAsString(timestamp), "00:00:00"),
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
		RegistrationServiceResponseModel<ApplicationModel> trDetailsResponse = null;
		try {
			trDetailsResponse = registrationService.getTRDetails(uniqueKey);
			HttpStatus statusCode = trDetailsResponse.getHttpStatus();
			ApplicationModel applicationModel = null;
			if (statusCode == HttpStatus.OK) {
				applicationModel = trDetailsResponse.getResponseBody();
				if (applicationModel.getProcessStatus() != Status.DIFFERENTIAL_TAX){
				    log.error("Diffrential Tax is not applicable. " + uniqueKey);
				    throw new ServiceValidationException(ServiceValidation.DIFFERENTIAL_TAX_NOT_FOUND.getCode(), ServiceValidation.DIFFERENTIAL_TAX_NOT_FOUND.getValue());
				}else if (applicationModel.getPrStatus() == Status.APPROVED){
				    log.error("Diffrential Tax is not applicable if PR generated. " + uniqueKey);
				    throw new ServiceValidationException(ServiceValidation.RC_FOUND.getCode(), "RC already generated, So you can not apply differential tax Service.");
				}
				RTAOfficeModel office = applicationModel.getRtaOffice();
				String aadharNumber = applicationModel.getAadharNumber();
				if (!StringsUtil.isNullOrEmpty(aadharNumber)) {
					if (aadharNumber.equals(citizen.getUid_num())) {
						CustomerDetailsRequestModel cdrm = applicationModel.getCustomerDetails();
						if (!ObjectsUtil.isNull(cdrm)) {
							citizen.setPincode(cdrm.getTemp_pincode());
							if (authenticateAadhar) {
								aadharAuthentication(citizen);
							}
							TokenModel tokenModel;
							UserSessionEntity activeSession = userSessionDAO.getActiveSession(aadharNumber, uniqueKey,
									getKeyType(), serviceType);
							if (ObjectsUtil.isNull(activeSession)) {
								activeSession = new UserSessionEntity();
								activeSession.setAadharNumber(aadharNumber);
								activeSession.setCompletionStatus(Status.FRESH.getValue());
								activeSession.setCreatedBy(aadharNumber);
								activeSession.setCreatedOn(timestamp);
								activeSession.setKeyType(getKeyType());
								activeSession.setServiceCode(serviceType.getCode());
								activeSession.setUniqueKey(uniqueKey);
								activeSession.setVehicleRcId(applicationModel.getVehicleRcId());
							}
							activeSession.setModifiedBy(aadharNumber);
							activeSession.setModifiedOn(timestamp);
							activeSession.setLoginTime(timestamp);
							pendingServiceExists(serviceType, activeSession);
							userSessionDAO.saveOrUpdate(activeSession);
							tokenModel = generateToken(activeSession);
							if (newLoggedIn) {
								loginHistory = new LoginAttemptHistoryEntity();
								loginHistory.setAadharNumber(aadharNumber);
								loginHistory.setCreatedBy(aadharNumber);
								loginHistory.setCreatedOn(timestamp);
								loginHistory.setKeyType(getKeyType());
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
							citizenApplicationEntity
									.setServiceCategory(ServiceUtil.getServiceCategory(serviceType).getCode());
							citizenApplicationEntity.setLoginHistory(activeSession);
							citizenApplicationEntity.setRtaOfficeCode(office.getCode());
							applicationDAO.saveOrUpdate(citizenApplicationEntity);
							loginAttemptsDAO.saveOrUpdate(loginHistory);
							return new ResponseModel<TokenModel>(ResponseModel.SUCCESS, tokenModel);
						}
					} else {
						log.info("Aadhaar is not found for " + getKeyType().toString() + " " + uniqueKey);
						throw new ServiceValidationException(ServiceValidation.AADHAR_MISMATCH_WITH_RC.getCode(),
								ServiceValidation.AADHAR_MISMATCH_WITH_RC.getValue());
					}
				} else {
					log.info("aadhar not mapped to this " + getKeyType().toString() + " " + uniqueKey);
					throw new ServiceValidationException(ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getCode(),
							ServiceValidation.AADHAR_NOT_FOUND_IN_RC.getValue());
				}
			}
		} catch (HttpClientErrorException e) {
			log.info("error in registration service response : ", e);
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				log.info("Not found " + getKeyType().toString() + " " + uniqueKey);
				throw new NotFoundException("Your TR Record not found.");
			}
		}
		throw new UnauthorizedException();
	}

	@Override
	protected KeyType getKeyType() {
		return KeyType.TR;
	}

	@Override
	protected String getUniqueKey(AuthenticationModel citizen) {
		String trNumber = citizen.getTrNumber();
		if (ObjectsUtil.isNull(trNumber)) {
			throw new IllegalArgumentException("trNumber is empty");
		}
		return trNumber;
	}

}

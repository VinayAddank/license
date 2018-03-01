package org.rta.citizen.licence.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.PaymentType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.dao.LicenceDAO;
import org.rta.citizen.licence.dao.VehicleClassTestsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsHistoryDAO;
import org.rta.citizen.licence.dao.updated.LlrVehicleClassMasterDAO;
import org.rta.citizen.licence.entity.LlrAgeGroupRefEntity;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.tests.VehicleClassTestsEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsHistoryEntity;
import org.rta.citizen.licence.enums.LicenceVehicleClass;
import org.rta.citizen.licence.model.ClassofVechicleModel;
import org.rta.citizen.licence.model.EndorseCOVModel;
import org.rta.citizen.licence.model.updated.CovDetailsModel;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.service.LicenceEndorsCOVService;
import org.rta.citizen.licence.service.LicenceService;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LicenceEndorsCOVServiceImpl implements LicenceEndorsCOVService {

	private static final Logger log = Logger.getLogger(LicenceEndorsCOVServiceImpl.class);

	private ApplicationDAO applicationDAO;
	private UserSessionDAO userSessionDAO;
	private LicenceService licenceService;
	private LicenceDAO licenceDAO;
	private LicensePermitDetailsDAO licensePermitDetailsDAO;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private VehicleClassTestsDAO vehicleClassTestsDAO;

	@Autowired
	private TransactionDetailDAO transactionDetailDAO;

	@Autowired
	private LicensePermitDetailsHistoryDAO licensePermitDetailsHistoryDAO;

	@Autowired
	private LlrVehicleClassMasterDAO llrVehicleClassMasterDAO;

	@Autowired
	public LicenceEndorsCOVServiceImpl(ApplicationDAO applicationDAO, UserSessionDAO userSessionDAO,
			LicenceService licenceService, LicenceDAO licenceDAO, LicensePermitDetailsDAO licensePermitDetailsDAO) {
		this.applicationDAO = applicationDAO;
		this.userSessionDAO = userSessionDAO;
		this.licenceService = licenceService;
		this.licenceDAO = licenceDAO;
		this.licensePermitDetailsDAO = licensePermitDetailsDAO;
	}

	@Transactional
	@Override
	public SaveUpdateResponse saveCovDetails(EndorseCOVModel model, Long sessionId, Long userId, UserType userType) {
		UserSessionEntity entity = userSessionDAO.getEntity(UserSessionEntity.class, sessionId);
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		String serviceCode = entity.getServiceCode();
		try {
			if (serviceCode.equalsIgnoreCase(ServiceType.LL_FRESH.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.LL_ENDORSEMENT.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.LL_DUPLICATE.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.DL_FRESH.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.DL_ENDORSMENT.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.DL_FOREIGN_CITIZEN.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.DL_EXPIRED.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.DL_RENEWAL.getCode())
					|| serviceCode.equalsIgnoreCase(ServiceType.DL_MILITRY.getCode())) {
				List<LicensePermitDetailsEntity> licensePermitDetails = licensePermitDetailsDAO
						.getLicensePermitDetails(appEntity.getApplicationId());
				List<String> selectedVehicleClass = new ArrayList<String>();
				List<String> newVehicleClass = model.getLlrVehicleClassCode().stream().collect(Collectors.toList());
				for (LicensePermitDetailsEntity permit : licensePermitDetails) {
					if (null != newVehicleClass && newVehicleClass.contains(permit.getVehicleClassCode())) {
						selectedVehicleClass.add(permit.getVehicleClassCode());
					} else {
						if ((serviceCode.equalsIgnoreCase(ServiceType.DL_MILITRY.getCode())
								|| serviceCode.equalsIgnoreCase(ServiceType.DL_FOREIGN_CITIZEN.getCode()))
								&& (userType == UserType.ROLE_CCO || userType == UserType.ROLE_AO
										|| userType == UserType.ROLE_RTO)) {
							LicensePermitDetailsHistoryEntity licensePermitDetailsHistoryEntity = licensePermitDetailsHistoryDAO
									.getCOVDetails(appEntity.getApplicationId(), permit.getVehicleClassCode());
							if (ObjectsUtil.isNull(licensePermitDetailsHistoryEntity)) {
								licensePermitDetailsHistoryEntity = new LicensePermitDetailsHistoryEntity();
								licensePermitDetailsHistoryEntity.setApplicationId(appEntity);
								licensePermitDetailsHistoryEntity.setVehicleClassCode(permit.getVehicleClassCode());
								licensePermitDetailsHistoryEntity.setStatus(Status.REJECTED.getValue());
								licensePermitDetailsHistoryEntity.setCreatedOn(new Date());
								licensePermitDetailsHistoryEntity.setCreatedBy(userType + " " + userId);
								licensePermitDetailsHistoryEntity.setUserId(userId);
								licensePermitDetailsHistoryEntity.setModifiedOn(new Date());
								licensePermitDetailsHistoryEntity.setModifiedBy(userType + " " + userId);
								licensePermitDetailsHistoryDAO.saveOrUpdate(licensePermitDetailsHistoryEntity);
							}
						}
						licensePermitDetailsDAO.delete(permit);
					}
				}
				for (String vehicleClass : model.getLlrVehicleClassCode()) {
					if (null != licensePermitDetails && selectedVehicleClass.contains(vehicleClass)) {
						if ((serviceCode.equalsIgnoreCase(ServiceType.DL_MILITRY.getCode())
								|| serviceCode.equalsIgnoreCase(ServiceType.DL_FOREIGN_CITIZEN.getCode()))
								&& (userType == UserType.ROLE_CCO || userType == UserType.ROLE_AO
										|| userType == UserType.ROLE_RTO)) {
							LicensePermitDetailsHistoryEntity licensePermitDetailsHistoryEntity = licensePermitDetailsHistoryDAO
									.getCOVDetails(appEntity.getApplicationId(), vehicleClass);
							if (!ObjectsUtil.isNull(licensePermitDetailsHistoryEntity)) {
								licensePermitDetailsHistoryDAO.delete(licensePermitDetailsHistoryEntity);
							}
						}
						continue;
					}
					LicensePermitDetailsEntity llp = new LicensePermitDetailsEntity();
					llp.setApplicationId(appEntity);
					llp.setCreatedBy(entity.getAadharNumber());
					llp.setModifiedBy(entity.getAadharNumber());
					llp.setModifiedBy(entity.getAadharNumber());
					llp.setCreatedOn(new Date());
					llp.setModifiedOn(new Date());
					if (null != model.getAadharNumber()) {
						llp.setParentConsentAadhaarNo(model.getAadharNumber().toString());
					}
					if (serviceCode.equals(ServiceType.LL_ENDORSEMENT.getCode())) {
						llp.setTestExempted('Y');
						llp.setTestExemptedReason("Service Typ is LL ENDORSEMENT");
					} else {
						llp.setTestExempted('N');
					}
					if (serviceCode.equals(ServiceType.DL_FRESH.getCode()) && model.getIsBadge()
							&& (LicenceVehicleClass.AUTORICKSHAW_TRANSPORT.getCode().equalsIgnoreCase(vehicleClass)
									|| LicenceVehicleClass.HEAVY_PASSENGER_VEHICLE.getCode()
											.equalsIgnoreCase(vehicleClass)
									|| LicenceVehicleClass.LIGHT_MOTOR_VEHICLE_TRANSPORT.getCode()
											.equalsIgnoreCase(vehicleClass)
									|| LicenceVehicleClass.MEDIUM_PASSENGER_VEHICLE.getCode()
											.equalsIgnoreCase(vehicleClass)
									|| LicenceVehicleClass.MOTOR_CAB.getCode().equalsIgnoreCase(vehicleClass))) {
						llp.setBadge(model.getIsBadge());
					} else if (serviceCode.equals(ServiceType.DL_FRESH.getCode())
							&& (LicenceVehicleClass.AUTORICKSHAW_TRANSPORT.getCode().equalsIgnoreCase(vehicleClass)
									|| LicenceVehicleClass.MOTOR_CAB.getCode().equalsIgnoreCase(vehicleClass))) {
						int age = DateUtil.getCurrentAge(appEntity.getApplicantDob());
						if (age >= 20) {
							llp.setBadge(Boolean.TRUE);
						}
					}
					llp.setVehicleClassCode(vehicleClass);
					licensePermitDetailsDAO.saveOrUpdate(llp);
				}
				return new SaveUpdateResponse(SaveUpdateResponse.SUCCESS, "COV details update Successfully !!", null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		log.info("COV details update Successfully !!");
		return new SaveUpdateResponse(SaveUpdateResponse.SUCCESS, "COV details update Successfully !!", null);
	}

	@Transactional
	@Override
	public CovDetailsModel getCovDetails(Long sessionId, UserType userType) {
		CovDetailsModel model = new CovDetailsModel();
		ApplicationEntity application = applicationDAO.getApplicationFromSession(sessionId);
		if (null != application) {
			List<ClassofVechicleModel> lLRCOVModel = new ArrayList<ClassofVechicleModel>();
			List<String> vehicleClass = new ArrayList<String>();
			List<String> preSelectedVehicleClass = new ArrayList<String>();
			List<LicensePermitDetailsEntity> learnersPermitDtls = licensePermitDetailsDAO
					.getLicensePermitDetails(application.getApplicationId());

			for (LicensePermitDetailsEntity permit : learnersPermitDtls) {
				vehicleClass.add(permit.getVehicleClassCode());
				// TODO: Optimize If else
				if (permit.isBadge()) {
					model.setIsBadge(permit.isBadge());
				}
			}
			// TODO: Optimize If else
			if (ServiceType.LL_ENDORSEMENT.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.LL_DUPLICATE.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.LL_RETEST.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.DL_FRESH.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.DL_RETEST.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.DL_DUPLICATE.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.DL_ENDORSMENT.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.DL_EXPIRED.getCode().equalsIgnoreCase(application.getServiceCode())
					|| ServiceType.DL_RENEWAL.getCode().equalsIgnoreCase(application.getServiceCode())) {

				preSelectedVehicleClass = getSelectedVehicleClass(application);
			}
			try {
				LlrAgeGroupRefEntity ageGroupEntity = licenceDAO
						.getAgeGroup(DateUtil.getCurrentAge(application.getApplicantDob()));
				log.info("Based on Age Group getting Cov details ");
				if (null != ageGroupEntity) {
					lLRCOVModel = licenceService.getCOV(ageGroupEntity.getAge_group_cd(), application.getServiceCode());
					Iterator<ClassofVechicleModel> covIterator = lLRCOVModel.iterator();
					while (covIterator.hasNext()) {
						ClassofVechicleModel llrcovlist = covIterator.next();
						if (preSelectedVehicleClass.contains(llrcovlist.getCovCode()) && ServiceType.LL_ENDORSEMENT
								.getCode().equalsIgnoreCase(application.getServiceCode())) {
							covIterator.remove();
						} else if (preSelectedVehicleClass.contains(llrcovlist.getCovCode()) && vehicleClass.size() > 0
								&& !vehicleClass.contains(llrcovlist.getCovCode()) && ServiceCategory.DL_CATEGORY
										.getCode().equalsIgnoreCase(application.getServiceCategory())) {
							llrcovlist.setSelected("optional");
						} else if (vehicleClass.contains(llrcovlist.getCovCode())
								|| preSelectedVehicleClass.contains(llrcovlist.getCovCode())) {
							llrcovlist.setSelected("true");
						} else {
							llrcovlist.setSelected("false");
						}
					}
				}
				if ((userType == UserType.ROLE_CCO || userType == UserType.ROLE_AO || userType == UserType.ROLE_RTO)
						&& (ServiceType.DL_MILITRY.getCode().equalsIgnoreCase(application.getServiceCode())
								|| ServiceType.DL_FOREIGN_CITIZEN.getCode()
										.equalsIgnoreCase(application.getServiceCode()))) {
					List<LicensePermitDetailsHistoryEntity> licensePermitDetailsHistoryEntityList = licensePermitDetailsHistoryDAO
							.getCOVDetails(application.getApplicationId(), Status.REJECTED.getValue());
					for (LicensePermitDetailsHistoryEntity licensePermitDetailsHistoryEntity : licensePermitDetailsHistoryEntityList) {
						Iterator<ClassofVechicleModel> covIterator = lLRCOVModel.iterator();
						while (covIterator.hasNext()) {
							ClassofVechicleModel llrcovlist = covIterator.next();
							if (llrcovlist.getCovCode()
									.equals(licensePermitDetailsHistoryEntity.getVehicleClassCode())) {
								llrcovlist.setSelected("optional");
							}
						}
					}
				}
				model.setCovDetails(lLRCOVModel);
				model.setAadharNumber(application.getLoginHistory().getAadharNumber());
				model.setLlrNumber(application.getLoginHistory().getUniqueKey());
				if (ServiceType.LL_RETEST.getCode().equalsIgnoreCase(application.getServiceCode())) {
					model.setApplicationNumber(application.getLoginHistory().getUniqueKey());
				} else {
					model.setApplicationNumber(application.getApplicationNumber());
				}
				if (!ObjectsUtil.isNull(transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(application,
						PaymentType.PAY))) {
					model.setIsPaymentCompleted(Boolean.TRUE);
				} else {
					model.setIsPaymentCompleted(Boolean.FALSE);
				}
				if (application.getServiceCode().equals(ServiceType.DL_FRESH.getCode())) {
					for (String cov : preSelectedVehicleClass) {
						if (LicenceVehicleClass.AUTORICKSHAW_TRANSPORT.getCode().equalsIgnoreCase(cov)
								|| LicenceVehicleClass.MOTOR_CAB.getCode().equalsIgnoreCase(cov)) {
							model.setIsBadgeAllowed(Boolean.TRUE);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.info(" Application not found for session: " + sessionId);
		}
		return model;
	}

	private List<String> getSelectedVehicleClass(ApplicationEntity appEntity) {
		String learnerOrAadhaarNumber, serviceCode = null;
		Set<String> vehicleClass = new HashSet<String>();
		if (null != appEntity) {
			learnerOrAadhaarNumber = appEntity.getLoginHistory().getAadharNumber();
			serviceCode = appEntity.getServiceCode();

			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> holderDetails = null;
			try {

				holderDetails = registrationLicenseService.getLicenseHolderDtls(learnerOrAadhaarNumber, null, null);

				if (null != holderDetails) {
					LicenseHolderPermitDetails holder = holderDetails.getResponseBody();
					if (null != holder) {
						if (ServiceType.DL_ENDORSMENT.getCode().equalsIgnoreCase(serviceCode)) {
							String llrNumber = null;
							// Get LLR number if login with LLR
							if (appEntity.getLoginHistory() != null) {
								llrNumber = appEntity.getLoginHistory().getUniqueKey();
							}
							// Get LLE detail
							if (llrNumber != null && llrNumber.startsWith("LL")) {
								RegLicenseServiceResponseModel<LicenseHolderPermitDetails> llrHolderDetails = registrationLicenseService
										.getLicenseHolderDtls(null, null, llrNumber);
								if (llrHolderDetails.getResponseBody() != null) {
									for (LearnersPermitDtlModel model : llrHolderDetails.getResponseBody()
											.getLearnersPermitDetailsList()) {
										if (ServiceType.LL_ENDORSEMENT.getCode()
												.equalsIgnoreCase(model.getLlrNoType())) {
											vehicleClass.add(model.getLlrVehicleClassCode());
										}
									}
								}

							}
							// Other wise login with DL
							else {
								for (LearnersPermitDtlModel model : holder.getLearnersPermitDetailsList()) {
									if (ServiceType.LL_ENDORSEMENT.getCode().equalsIgnoreCase(model.getLlrNoType())) {
										vehicleClass.add(model.getLlrVehicleClassCode());
									}
								}
							}
							for (DriversLicenceDetailsModel model : holder.getDriversPermitDetailsList()) {
								if (vehicleClass.contains(model.getDlVehicleClassCode())) {
									vehicleClass.remove(model.getDlVehicleClassCode());
								}
							}
						} else if (ServiceType.DL_RETEST.getCode().equalsIgnoreCase(serviceCode)
								&& null != holder.getDriversPermitDetailsList()) {
							for (DriversLicenceDetailsModel model : holder.getDriversPermitDetailsList()) {
								if (StringsUtil.isNullOrEmpty(model.getDlNo())
										|| (SomeConstants.VALID.equalsIgnoreCase(model.getStatusCode())
												&& DateUtil.addYearToDate(model.getValidTo(), SomeConstants.FIVE)
														.before(new Date()))) {
									vehicleClass.add(model.getDlVehicleClassCode());
								}
							}
						} else if (ServiceType.DL_EXPIRED.getCode().equalsIgnoreCase(serviceCode)
								&& null != holder.getDriversPermitDetailsList()) {
							for (DriversLicenceDetailsModel model : holder.getDriversPermitDetailsList()) {
								if (SomeConstants.VALID.equalsIgnoreCase(model.getStatusCode()) && DateUtil
										.addYearToDate(model.getValidTo(), SomeConstants.FIVE).before(new Date())) {
									vehicleClass.add(model.getDlVehicleClassCode());
								}
							}
						} else if (ServiceType.DL_RENEWAL.getCode().equalsIgnoreCase(serviceCode)
								&& null != holder.getDriversPermitDetailsList()) {
							for (DriversLicenceDetailsModel model : holder.getDriversPermitDetailsList()) {
								if (SomeConstants.VALID.equalsIgnoreCase(model.getStatusCode())
										&& model.getValidTo().before(DateUtil.addMonths(new Date(), SomeConstants.ONE))
										&& model.getValidTo()
												.after(DateUtil.delYearToDate(new Date(), SomeConstants.FIVE))) {
									vehicleClass.add(model.getDlVehicleClassCode());
								}
							}
						} else if ((appEntity.getLoginHistory().getUniqueKey().startsWith("AP")
								|| ServiceType.DL_DUPLICATE.getCode().equalsIgnoreCase(serviceCode))
								&& null != holder.getDriversPermitDetailsList()) {
							for (DriversLicenceDetailsModel model : holder.getDriversPermitDetailsList()) {
								if (SomeConstants.VALID.equalsIgnoreCase(model.getStatusCode())) {
									vehicleClass.add(model.getDlVehicleClassCode());
								}
							}
						} else if (null != holder.getLearnersPermitDetailsList()) {
							for (LearnersPermitDtlModel model : holder.getLearnersPermitDetailsList()) {
								vehicleClass.add(model.getLlrVehicleClassCode());
							}
						}
					}
				}
			} catch (UnauthorizedException e) {
				e.printStackTrace();
			}
		}

		return new ArrayList<>(vehicleClass);
	}

	// TODO: User ABOVE untill this is corrected.
	/*
	 * private List<String> getSelectedVehicleClass(String
	 * learnersLicenseNumber) { RegLicenseServiceResponseModel<List<String>>
	 * holderVehicleClass; List<String> vehicleClasses = new
	 * ArrayList<String>(); try { holderVehicleClass =
	 * registrationLicenseService.getVehicleClasses(learnersLicenseNumber);
	 * if(holderVehicleClass.getHttpStatus() == HttpStatus.OK){ vehicleClasses =
	 * holderVehicleClass.getResponseBody(); for(String vehicleClass :
	 * vehicleClasses){ vehicleClasses.add(vehicleClass); } } } catch
	 * (UnauthorizedException e) { e.printStackTrace(); } return vehicleClasses;
	 * }
	 */

	/**
	 * Get applicable tests for Vehicle Classes
	 * 
	 * @param vehicleClassList
	 * @return applicable tests
	 */
	@Override
	@Transactional
	public ResponseModel<Map<SlotServiceType, List<String>>> getTestsForVehicleClass(List<String> vehicleClassList) {
		List<VehicleClassTestsEntity> testEntityList = vehicleClassTestsDAO.getTests(vehicleClassList);
		Map<SlotServiceType, List<String>> map = null;
		if (!ObjectsUtil.isNull(testEntityList)) {
			map = new HashMap<SlotServiceType, List<String>>();
			for (VehicleClassTestsEntity test : testEntityList) {
				List<String> covList = map.get(test.getTestType());
				if (ObjectsUtil.isNull(covList)) {
					List<String> list = new ArrayList<>();
					list.add(test.getVehicleClass());
					map.put(test.getTestType(), list);
				} else {
					covList.add(test.getVehicleClass());
				}
			}
		}
		if (!ObjectsUtil.isNullOrEmpty(map)) {
			return new ResponseModel<>(ResponseModel.SUCCESS, map, null, HttpStatus.OK.value());
		} else {
			return new ResponseModel<>(ResponseModel.FAILED, "no test found", HttpStatus.NO_CONTENT.value());
		}
	}

	@Override
	@Transactional
	public CovDetailsModel getClassOfVehicleInfo(Long sessionId) {
		CovDetailsModel model = new CovDetailsModel();
		List<ClassofVechicleModel> lLRCOVModel = new ArrayList<ClassofVechicleModel>();
		List<String> preSelectedVehicleClass = new ArrayList<String>();
		List<String> vehicleClass = new ArrayList<String>();
		ApplicationEntity application = applicationDAO.getApplicationFromSession(sessionId);
		UserSessionEntity userSession = userSessionDAO.getUserSession(sessionId);
		RegLicenseServiceResponseModel<List<String>> holderVehicleClass;
		List<String> vehicleList = new ArrayList<>();
		List<LicensePermitDetailsEntity> learnersPermitDtls = licensePermitDetailsDAO
				.getLicensePermitDetails(application.getApplicationId());

		for (LicensePermitDetailsEntity permit : learnersPermitDtls) {
			vehicleClass.add(permit.getVehicleClassCode());
			// TODO: Optimize If else
			if (permit.isBadge()) {
				model.setIsBadge(permit.isBadge());
			}
		}
		preSelectedVehicleClass = getSelectedVehicleClass(application);
		try {
			holderVehicleClass = registrationLicenseService.getVehicleClasses(userSession.getAadharNumber(), null);
			if (holderVehicleClass.getHttpStatus() == HttpStatus.OK) {
				vehicleList = holderVehicleClass.getResponseBody();
				lLRCOVModel = licenceService.getCovList(vehicleList);
				Iterator<ClassofVechicleModel> covIterator = lLRCOVModel.iterator();
				while (covIterator.hasNext()) {
					ClassofVechicleModel llrcovlist = covIterator.next();
					if (preSelectedVehicleClass.contains(llrcovlist.getCovCode()) && vehicleClass.size() > 0
							&& !vehicleClass.contains(llrcovlist.getCovCode()) && ServiceCategory.DL_CATEGORY.getCode()
									.equalsIgnoreCase(application.getServiceCategory())) {
						llrcovlist.setSelected("optional");
					} else if (preSelectedVehicleClass.contains(llrcovlist.getCovCode())) {
						llrcovlist.setSelected("true");
					} else {
						llrcovlist.setSelected("false");
					}
				}
			}
			model.setCovDetails(lLRCOVModel);
			model.setAadharNumber(application.getLoginHistory().getAadharNumber());
			model.setLlrNumber(application.getLoginHistory().getUniqueKey());
			model.setApplicationNumber(application.getApplicationNumber());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		return model;
	}
}

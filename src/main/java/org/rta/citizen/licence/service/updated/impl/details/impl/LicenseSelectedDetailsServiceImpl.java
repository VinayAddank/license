package org.rta.citizen.licence.service.updated.impl.details.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.RegistrationCategoryType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.dao.LicenceDAO;
import org.rta.citizen.licence.dao.VehicleClassTestsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.service.updated.impl.details.LicenseSelectedDetailsService;
import org.rta.citizen.slotbooking.dao.SlotDAO;
import org.rta.citizen.slotbooking.entity.SlotApplicationsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LicenseSelectedDetailsServiceImpl implements LicenseSelectedDetailsService {

	private static final Logger logger = Logger.getLogger(LicenseSelectedDetailsServiceImpl.class);

	@Autowired
	private SlotDAO slotDAO;

	@Autowired
	private LicenceDAO licenceDAO;

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Autowired
	private VehicleClassTestsDAO vehicleClassTestsDAO;

	@Autowired
	private LicensePermitDetailsDAO licensePermitDetailsDAO;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Override
	public LicenseHolderPermitDetails getSelectedDetailsList(String aadharNumber, String uniqueKey) {

		logger.info("Getting Selected Details :::::::::::: for License services");
		ApplicationEntity appEntity = null;
		List<String> covs = new ArrayList<>();
		List<String> vehicleClass = new ArrayList<String>();
		LicenseHolderPermitDetails licenseHolderPermitDetails = null;
		List<Integer> statusList = new ArrayList<>(Arrays.asList(4));
		List<String> serviceTypeList = new ArrayList<>(Arrays.asList(ServiceType.DL_FRESH.getCode(),
				ServiceType.DL_ENDORSMENT.getCode(), ServiceType.DL_EXPIRED.getCode()));
		if (uniqueKey.contains(ServiceType.DL_RETEST.getCode())) {
			String uniKey = applicationDAO.getApplication(uniqueKey).getLoginHistory().getUniqueKey();
			UserSessionEntity usEntity = userSessionDAO.getLastRejectedApprovedSession(uniKey, serviceTypeList,
					statusList);
			appEntity = applicationDAO.getApplicationFromSession(usEntity.getSessionId());

		} else {
			appEntity = applicationDAO.getApplication(uniqueKey);
		}
		List<SlotApplicationsEntity> slotApplicationEntityList = null;
		if (uniqueKey.contains(ServiceType.DL_RETEST.getCode())) {
			slotApplicationEntityList = slotDAO.getBookedSlotByApplicationIdAndIteration(
					applicationDAO.getApplication(uniqueKey).getApplicationId(), appEntity.getIteration());
		} else {
			slotApplicationEntityList = slotDAO.getBookedSlotByApplicationIdAndIteration(appEntity.getApplicationId(),
					appEntity.getIteration());
		}
		List<LicensePermitDetailsEntity> licensePermitDetailsEntities = licensePermitDetailsDAO
				.getLicensePermitDetails(appEntity.getApplicationId());
		for (LicensePermitDetailsEntity entity : licensePermitDetailsEntities) {
			covs.add(entity.getVehicleClassCode());
		}
		try {
			RegLicenseServiceResponseModel<LicenseHolderPermitDetails> response = registrationLicenseService
					.getLicenseHolderDtls(aadharNumber, null, null);
			if (response.getHttpStatus().equals(HttpStatus.OK)) {
				licenseHolderPermitDetails = response.getResponseBody();
				List<LearnersPermitDtlModel> learnersPermitDetailsList = licenseHolderPermitDetails
						.getLearnersPermitDetailsList();
				List<DriversLicenceDetailsModel> driversLicenceDetailsModels = licenseHolderPermitDetails
						.getDriversPermitDetailsList();
				if (!ObjectsUtil.isNullOrEmpty(driversLicenceDetailsModels)) {
					for (DriversLicenceDetailsModel model : driversLicenceDetailsModels) {
						if (!ObjectsUtil.isNull(model.getValidTo()) && !model.getValidTo().before(new Date())) {
							vehicleClass.add(model.getDlVehicleClassCode());
						}
					}
				}
				if (appEntity.getServiceCategory().equals(ServiceCategory.LL_CATEGORY.getCode())
						|| appEntity.getServiceCode().equals(ServiceType.DL_FRESH.getCode())
						|| appEntity.getServiceCode().equals(ServiceType.DL_RETEST.getCode())
						|| appEntity.getServiceCode().equals(ServiceType.DL_ENDORSMENT.getCode())) {
					Iterator<LearnersPermitDtlModel> llrIterator = learnersPermitDetailsList.iterator();
					while (llrIterator.hasNext()) {
						LearnersPermitDtlModel learnersPermitDtlModel = llrIterator.next();
						if (!covs.contains(learnersPermitDtlModel.getLlrVehicleClassCode())) {
							llrIterator.remove();
						} else if (!ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
							for (SlotApplicationsEntity slotAppEntity : slotApplicationEntityList) {
								if (slotAppEntity.getSlotServiceType().equals(vehicleClassTestsDAO
										.getTest(learnersPermitDtlModel.getLlrVehicleClassCode()).getTestType())) {
									learnersPermitDtlModel.setTestDate(
											DateUtil.getDatefromString(DateUtil.extractDateAsStringWithHyphen(
													slotAppEntity.getSlot().getScheduledDate())));
								}
							}
						}
					}
				} else {
					licenseHolderPermitDetails.setLearnersPermitDetailsList(null);
				}
				Iterator<DriversLicenceDetailsModel> iterator = driversLicenceDetailsModels.iterator();
				while (iterator.hasNext()) {
					DriversLicenceDetailsModel driversLicenceDetailsModel = iterator.next();
					if (!covs.contains(driversLicenceDetailsModel.getDlVehicleClassCode())) {
						iterator.remove();
					} else if ((appEntity.getServiceCode().equals(ServiceType.DL_EXPIRED.getCode())
							|| appEntity.getServiceCode().equals(ServiceType.DL_RETEST.getCode()))
							&& !ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
						for (SlotApplicationsEntity slotAppEntity : slotApplicationEntityList) {
							if (slotAppEntity.getSlotServiceType().equals(vehicleClassTestsDAO
									.getTest(driversLicenceDetailsModel.getDlVehicleClassCode()).getTestType())) {
								driversLicenceDetailsModel.setTestDate(DateUtil.getDatefromString(DateUtil
										.extractDateAsStringWithHyphen(slotAppEntity.getSlot().getScheduledDate())));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("Getting Exception in LicensePermitDtls");
		}
		return licenseHolderPermitDetails;
	}

}

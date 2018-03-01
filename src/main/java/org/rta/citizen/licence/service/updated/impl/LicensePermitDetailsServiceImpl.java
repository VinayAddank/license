package org.rta.citizen.licence.service.updated.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.licence.dao.VehicleClassTestsDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.tests.VehicleClassTestsEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.model.updated.LicensePermitDetailsModel;
import org.rta.citizen.licence.service.updated.LicensePermitDetailsService;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LicensePermitDetailsServiceImpl implements LicensePermitDetailsService {

	private static final Logger logger = Logger.getLogger(LicensePermitDetailsServiceImpl.class);

	@Autowired
	private LicensePermitDetailsDAO licensePermitDetailsDAO;

	@Autowired
	private VehicleClassTestsDAO vehicleClassTestsDAO;

	@Override
	public List<LicensePermitDetailsModel> getLicenseDetails(Long applicationId) {

		return null;
	}

	@Override
	@Transactional
	public String update(Long applicationId, SlotServiceType slotServiceType, Status status) {

		String response = ResponseModel.SUCCESS;
		try {
			List<LicensePermitDetailsEntity> liEntities = licensePermitDetailsDAO
					.getLicensePermitDetails(applicationId);
			Set<String> covSet = new HashSet<String>();
			for (VehicleClassTestsEntity entity : vehicleClassTestsDAO.getTests(slotServiceType)) {
				covSet.add(entity.getVehicleClass());
			}
			for (LicensePermitDetailsEntity entity : liEntities) {
				if (covSet.contains(entity.getVehicleClassCode())) {
					entity.setStatus(status.getValue());
					licensePermitDetailsDAO.update(entity);
				}
			}
		} catch (Exception e) {
			logger.error("getting error in updating the status in LicensePermitDetailsEntity" + e.getMessage());
			response = ResponseModel.FAILED;
		}
		return response;
	}

}

package org.rta.citizen.licence.service;

import java.util.List;
import java.util.Map;

import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.licence.model.EndorseCOVModel;
import org.rta.citizen.licence.model.updated.CovDetailsModel;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface LicenceEndorsCOVService {

	public SaveUpdateResponse saveCovDetails(EndorseCOVModel model, Long sessionId, Long userId, UserType userType);

	public CovDetailsModel getCovDetails(Long sessionId, UserType userType);

	public CovDetailsModel getClassOfVehicleInfo(Long sessionId);

	ResponseModel<Map<SlotServiceType, List<String>>> getTestsForVehicleClass(List<String> vehicleClassList);
}

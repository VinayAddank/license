package org.rta.citizen.licence.dao;

import java.util.List;
import java.util.Set;

import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;

public interface LicenceEndorsCOVDAO {

	public List<LlrVehicleClassMasterEntity> getEndorsDetails();

	public List<LlrVehicleClassMasterEntity> getVehicleDescription(String VehicleCode);

	public List<LlrVehicleClassMasterEntity> getVehicleDescription(Set<String> vehicleCode);
}

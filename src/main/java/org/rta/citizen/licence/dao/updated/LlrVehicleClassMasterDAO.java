package org.rta.citizen.licence.dao.updated;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;

public interface LlrVehicleClassMasterDAO extends GenericDAO<LlrVehicleClassMasterEntity> {

	public LlrVehicleClassMasterEntity getVehicleClassDetails(String covCode);
}

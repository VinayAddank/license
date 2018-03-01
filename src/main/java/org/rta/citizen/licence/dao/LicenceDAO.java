package org.rta.citizen.licence.dao;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.LlrAgeGroupRefEntity;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.LlrVehicleClassRefEntity;

public interface LicenceDAO extends GenericDAO<LlrAgeGroupRefEntity> {

	public LlrAgeGroupRefEntity getAgeGroup(Integer age);

	public List<LlrVehicleClassRefEntity> getCOV(String ageGroup);

	public LlrVehicleClassMasterEntity getDetailCOV(String covcode);

	public List<LlrVehicleClassMasterEntity> getAllDetailCOV();

	public List<LlrVehicleClassMasterEntity> getAllCOV();

	public List<LlrVehicleClassMasterEntity> getCovList(String covcode);

	public List<LlrVehicleClassMasterEntity> getCovListIn(List<String> covCodes);

}

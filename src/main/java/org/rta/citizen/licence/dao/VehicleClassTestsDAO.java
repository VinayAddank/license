package org.rta.citizen.licence.dao;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.tests.VehicleClassTestsEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface VehicleClassTestsDAO extends GenericDAO<VehicleClassTestsEntity> {
	
	public VehicleClassTestsEntity getTest(String cov);

	public List<VehicleClassTestsEntity> getTests(List<String> covs);

	public List<VehicleClassTestsEntity> getTests(SlotServiceType slotServiceType);

}

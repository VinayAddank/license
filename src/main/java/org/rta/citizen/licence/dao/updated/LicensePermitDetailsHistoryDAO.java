package org.rta.citizen.licence.dao.updated;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsHistoryEntity;

public interface LicensePermitDetailsHistoryDAO extends GenericDAO<LicensePermitDetailsHistoryEntity> {

	public List<LicensePermitDetailsHistoryEntity> getCOVDetails(Long appId, Integer status);
	
	public LicensePermitDetailsHistoryEntity getCOVDetails(Long appId, String covName);
}

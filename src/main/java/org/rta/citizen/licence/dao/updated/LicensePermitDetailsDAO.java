package org.rta.citizen.licence.dao.updated;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;

public interface LicensePermitDetailsDAO extends GenericDAO<LicensePermitDetailsEntity> {

	public List<LicensePermitDetailsEntity> getSelectedCOV(Long sessionId);

	public List<LicensePermitDetailsEntity> getLicensePermitDetails(Long applicationId);
}

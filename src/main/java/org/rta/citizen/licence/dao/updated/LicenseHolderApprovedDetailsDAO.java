package org.rta.citizen.licence.dao.updated;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.updated.LicenseHolderApprovedDetailsEntity;

public interface LicenseHolderApprovedDetailsDAO extends GenericDAO<LicenseHolderApprovedDetailsEntity> {
	
	public LicenseHolderApprovedDetailsEntity getHolderApprovedDetails(Long applicationId);

}

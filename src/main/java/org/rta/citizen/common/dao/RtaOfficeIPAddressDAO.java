package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.entity.RtaOfficeIPAddressEntity;

public interface RtaOfficeIPAddressDAO extends GenericDAO<RtaOfficeIPAddressEntity> {
	
	public RtaOfficeIPAddressEntity getRTAOfficeByIP(String ipAddress);

	public List<RtaOfficeIPAddressEntity> getRTAOfficeByOfficeCode(String officeCode);

}

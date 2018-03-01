package org.rta.citizen.licence.dao;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.DlSeriesMasterEntity;

public interface DlSeriesMasterDAO extends GenericDAO<DlSeriesMasterEntity> {
	public DlSeriesMasterEntity getByYear(Integer year);
}

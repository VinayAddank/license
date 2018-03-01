/**
 * 
 */
package org.rta.citizen.stoppagetax.dao;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.stoppagetax.entity.VehicleInspectionEntity;

/**
 * @author sohan.maurya
 *
 */
public interface VehicleInspectionDAO extends GenericDAO<VehicleInspectionEntity> {
	
	public List<VehicleInspectionEntity> getVehicleInspectionList(Long userId, Long scheduleInspectionDate);
	
	public VehicleInspectionEntity getVehicleInspection(Long applicationId, Long scheduleInspectionDate);

	public Integer getVehicleInspectionCount(Long applicationId);

}

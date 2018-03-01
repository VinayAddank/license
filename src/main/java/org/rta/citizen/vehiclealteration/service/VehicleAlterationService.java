package org.rta.citizen.vehiclealteration.service;

import java.util.List;

import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleClassDescModel;

/**
 *	@Author sohan.maurya created on Jan 6, 2017.
 */
public interface VehicleAlterationService {

    public ResponseModel<Boolean> updateDataAfterPayments(Long sessionId);

    public ResponseModel<String> saveOrUpdateVehicleAlteration(String prNumber, Long applicationId);
    
    public ResponseModel<RegistrationCategoryModel> getVehicleType(String prNumber);
    
    public ResponseModel<String> getFuelType(String prNumber);
    
    public List<VehicleClassDescModel> getAlterationCovList(String prNumber,String regCatCode);
    
    public List<String> getBodyTypeList();

}

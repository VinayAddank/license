package org.rta.citizen.common.model;

import java.util.List;

/**
 *	@Author sohan.maurya created on Dec 30, 2016.
 */
public class PermitTypeVehicleClassModel {

    
    private VehicleClassDescModel vehicleClassDetails;
    private List<PermitTypeModel> permitType;

    public VehicleClassDescModel getVehicleClassDetails() {
        return vehicleClassDetails;
    }

    public void setVehicleClassDetails(VehicleClassDescModel vehicleClassDetails) {
        this.vehicleClassDetails = vehicleClassDetails;
    }

    public List<PermitTypeModel> getPermitType() {
        return permitType;
    }

    public void setPermitType(List<PermitTypeModel> permitType) {
        this.permitType = permitType;
    }

}

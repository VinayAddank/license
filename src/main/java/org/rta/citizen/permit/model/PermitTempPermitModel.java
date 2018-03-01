/**
 * 
 */
package org.rta.citizen.permit.model;

import java.util.List;

import org.rta.citizen.common.model.PermitTypeModel;

/**
 * @author arun.verma
 *
 */
public class PermitTempPermitModel {

    private PermitTypeModel permitType;
    private List<PermitTypeModel> temporaryPermits;

    public PermitTypeModel getPermitType() {
        return permitType;
    }

    public void setPermitType(PermitTypeModel permitType) {
        this.permitType = permitType;
    }

    public List<PermitTypeModel> getTemporaryPermits() {
        return temporaryPermits;
    }

    public void setTemporaryPermits(List<PermitTypeModel> temporaryPermits) {
        this.temporaryPermits = temporaryPermits;
    }

}

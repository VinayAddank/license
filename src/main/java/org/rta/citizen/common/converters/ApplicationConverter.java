package org.rta.citizen.common.converters;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConverter {

    public CitizenApplicationModel convertToModel(ApplicationEntity entity) {
        if (ObjectsUtil.isNull(entity)) {
            return null;
        }
        CitizenApplicationModel model = new CitizenApplicationModel();
        model.setAppId(entity.getApplicationId());
        model.setApplicationNumber(entity.getApplicationNumber());
        model.setKeyType(entity.getLoginHistory().getKeyType());
        model.setUniqueKey(entity.getLoginHistory().getUniqueKey());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedOn(entity.getCreatedOn());
        model.setModifiedBy(entity.getModifiedBy());
        model.setModifiedOn(entity.getModifiedOn());
        model.setServiceType(ServiceType.getServiceType(entity.getServiceCode()));
        model.setSessionId(entity.getLoginHistory().getSessionId());
        model.setDob(entity.getApplicantDob());
        return model;
    }
    
    /*public List<CitizenApplicationModel> convertToModel(List<ApplicationEntity> entity) {
        if (ObjectsUtil.isNull(entity)) {
            return null;
        }
        List<CitizenApplicationModel> citizenApplications = new ArrayList<>();
        entity.stream().forEach(e->{
            CitizenApplicationModel model = new CitizenApplicationModel();
            model.setAppId(e.getApplicationId());
            model.setApplicationNumber(e.getApplicationNumber());
            model.setCreatedBy(e.getCreatedBy());
            model.setCreatedOn(e.getCreatedOn());
            model.setModifiedBy(e.getModifiedBy());
            model.setModifiedOn(e.getModifiedOn());
            model.set
            citizenApplications.add( model);
        });
        CitizenApplicationModel model = new CitizenApplicationModel();
        model.setAppId(entity.getApplicationId());
        model.setApplicationNumber(entity.getApplicationNumber());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedOn(entity.getCreatedOn());
        model.setModifiedBy(entity.getModifiedBy());
        model.setModifiedOn(entity.getModifiedOn());
        model.setServiceType(ServiceType.getServiceType(entity.getServiceCode()));
        model.setSessionId(entity.getLoginHistory().getSessionId());
        return model;
    }*/

}

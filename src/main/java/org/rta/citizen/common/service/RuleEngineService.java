package org.rta.citizen.common.service;

import java.util.List;

import org.rta.citizen.common.model.DocPermissionModel;
import org.rta.citizen.common.model.DocTypesModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;



public interface RuleEngineService {

    public RegistrationServiceResponseModel<List<DocTypesModel>> getAttachments(DocPermissionModel model);
    
}

package org.rta.citizen.common.service;

import org.rta.citizen.common.model.GeneralDetails;

public interface DetailsService {

    public GeneralDetails getDetails(String aadharNumber, String uniqueKey);
    
}

package org.rta.citizen.common.exception;

import org.rta.citizen.common.model.AadharModel;

public class AadharAuthenticationFailedException extends Exception {

    private static final long serialVersionUID = -8814594751651187498L;

    private AadharModel aadharModel;
    
    public AadharAuthenticationFailedException() {
        super();
    }
    
    public AadharAuthenticationFailedException(AadharModel aadharModel) {
        super();
        this.aadharModel = aadharModel;
    }

    public AadharModel getAadharModel() {
        return aadharModel;
    }

}

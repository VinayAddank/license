package org.rta.citizen.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Nikhilesh.Tiwari
 *
 */
@JsonInclude(Include.NON_NULL)
public class LegalHierDetailsModel {

    private String aadharNumber;
    private Boolean isConsent;

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public Boolean getIsConsent() {
        return isConsent;
    }

    public void setIsConsent(Boolean isConsent) {
        this.isConsent = isConsent;
    }

}

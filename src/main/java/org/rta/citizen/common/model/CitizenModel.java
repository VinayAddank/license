package org.rta.citizen.common.model;

import org.rta.citizen.common.enums.UserType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CitizenModel {
    
    private String aadharNumber;
    private String prNumber;
    private String tokenNumber;
    private String trNumber;
    private UserType userType;
    
    public String getAadharNumber() {
        return aadharNumber;
    }
    public String getPrNumber() {
        return prNumber;
    }
    public String getTokenNumber() {
        return tokenNumber;
    }
    public String getTrNumber() {
        return trNumber;
    }
    public UserType getUserType() {
        return userType;
    }
    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }
    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }
    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }
    public void setTrNumber(String trNumber) {
        this.trNumber = trNumber;
    }
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    
}

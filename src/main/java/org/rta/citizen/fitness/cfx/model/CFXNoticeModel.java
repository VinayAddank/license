package org.rta.citizen.fitness.cfx.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CFXNoticeModel {

    private CFXModel cfx;
    private String make;
    private String model;
    private String userSignature;
    private Long fitnessIssueDate;
    private String fitnessOfficerName;
    private String ownerName;
    private String ownerAddress;

    public CFXModel getCfx() {
        return cfx;
    }

    public void setCfx(CFXModel cfx) {
        this.cfx = cfx;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUserSignature() {
        return userSignature;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
    }

    public Long getFitnessIssueDate() {
        return fitnessIssueDate;
    }

    public void setFitnessIssueDate(Long fitnessIssueDate) {
        this.fitnessIssueDate = fitnessIssueDate;
    }

    public String getFitnessOfficerName() {
        return fitnessOfficerName;
    }

    public void setFitnessOfficerName(String fitnessOfficerName) {
        this.fitnessOfficerName = fitnessOfficerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

}

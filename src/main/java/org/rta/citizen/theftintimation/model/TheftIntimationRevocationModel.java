package org.rta.citizen.theftintimation.model;

import java.util.List;

import org.rta.citizen.common.enums.TheftIntSusType;
import org.rta.citizen.common.model.UserActionModel;

/**
 * @Author sohan.maurya created on Dec 24, 2016.
 */

public class TheftIntimationRevocationModel {

    private String theftType;
    private TheftIntSusType theftStatus;
    private String firNo;
    private Long complaintDate;
    private String policeStationName;
    private String remarks;
    private boolean otherState;
    private String firYear;
    private String district;
    private List<UserActionModel> actionModelList;


    public String getTheftType() {
        return theftType;
    }

    public void setTheftType(String theftType) {
        this.theftType = theftType;
    }

    public TheftIntSusType getTheftStatus() {
        return theftStatus;
    }

    public void setTheftStatus(TheftIntSusType theftStatus) {
        this.theftStatus = theftStatus;
    }

    public String getFirNo() {
        return firNo;
    }

    public void setFirNo(String firNo) {
        this.firNo = firNo;
    }

    public Long getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(Long complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getPoliceStationName() {
        return policeStationName;
    }

    public void setPoliceStationName(String policeStationName) {
        this.policeStationName = policeStationName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isOtherState() {
        return otherState;
    }

    public void setOtherState(boolean otherState) {
        this.otherState = otherState;
    }

    public String getFirYear() {
        return firYear;
    }

    public void setFirYear(String firYear) {
        this.firYear = firYear;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

	public List<UserActionModel> getActionModelList() {
		return actionModelList;
	}

	public void setActionModelList(List<UserActionModel> actionModelList) {
		this.actionModelList = actionModelList;
	}

}

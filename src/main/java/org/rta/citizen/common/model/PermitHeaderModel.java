/**
 * 
 */
package org.rta.citizen.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@JsonInclude(Include.NON_NULL)
public class PermitHeaderModel {

    private String permitNo;
    private String applId;
    private String permitIssuedTo;
    private String applType;
    private String vehicleRegdNo;
    private String permitTypeId;
    private String permitTypeName;
    private String permitSubType;
    private String permitSubTypeName;
    private Integer noOfTrips;
    private Boolean isTempPermit;
    private String stateNationalPermit;
    private Long issueDate;
    private Long validFromDate;
    private Long validToDate;
    private String status;
    private String rtaOfficeCd;
    private String approvedBy;
    private Long approvedDate;
    private String approvalRmks;
    private String moduleCd;
    private String entityCd;
    private String otherStateNm;
    private String homeStateAuthority;
    private String osRecomLttrId;
    private Long stateEntryDt;
    private String purpose;
    private String forwardJourneyStartingStation;
    private String forwardJourneyEnroute;
    private String forwardJourneyDestination;
    private String returnJourneyStartingStation;
    private String returnJourneyEnroute;
    private String returnJourneyDestination;
    private Integer daysPerTrip;
    private String holderAadharNo;
    private String holderNm;
    private String holderMobileNo;
    private String passangerListEnclId;
    private String supportTicketRemarks;
    private String applOrigination;
    private String rtaOfficeName;
    private String permitType;

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public String getApplId() {
        return applId;
    }

    public void setApplId(String applId) {
        this.applId = applId;
    }

    public String getPermitIssuedTo() {
        return permitIssuedTo;
    }

    public void setPermitIssuedTo(String permitIssuedTo) {
        this.permitIssuedTo = permitIssuedTo;
    }

    public String getApplType() {
        return applType;
    }

    public void setApplType(String applType) {
        this.applType = applType;
    }

    public String getVehicleRegdNo() {
        return vehicleRegdNo;
    }

    public void setVehicleRegdNo(String vehicleRegdNo) {
        this.vehicleRegdNo = vehicleRegdNo;
    }

    public String getPermitTypeId() {
        return permitTypeId;
    }

    public void setPermitTypeId(String permitTypeId) {
        this.permitTypeId = permitTypeId;
    }

    public String getPermitTypeName() {
        return permitTypeName;
    }

    public void setPermitTypeName(String permitTypeName) {
        this.permitTypeName = permitTypeName;
    }

    public String getPermitSubTypeName() {
        return permitSubTypeName;
    }

    public void setPermitSubTypeName(String permitSubTypeName) {
        this.permitSubTypeName = permitSubTypeName;
    }

    public Integer getNoOfTrips() {
        return noOfTrips;
    }

    public void setNoOfTrips(Integer noOfTrips) {
        this.noOfTrips = noOfTrips;
    }

    public Boolean getIsTempPermit() {
        return isTempPermit;
    }

    public void setIsTempPermit(Boolean isTempPermit) {
        this.isTempPermit = isTempPermit;
    }

    public String getStateNationalPermit() {
        return stateNationalPermit;
    }

    public void setStateNationalPermit(String stateNationalPermit) {
        this.stateNationalPermit = stateNationalPermit;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public Long getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(Long validFromDate) {
        this.validFromDate = validFromDate;
    }

    public Long getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(Long validToDate) {
        this.validToDate = validToDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRtaOfficeCd() {
        return rtaOfficeCd;
    }

    public void setRtaOfficeCd(String rtaOfficeCd) {
        this.rtaOfficeCd = rtaOfficeCd;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Long getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Long approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getApprovalRmks() {
        return approvalRmks;
    }

    public void setApprovalRmks(String approvalRmks) {
        this.approvalRmks = approvalRmks;
    }

    public String getModuleCd() {
        return moduleCd;
    }

    public void setModuleCd(String moduleCd) {
        this.moduleCd = moduleCd;
    }

    public String getEntityCd() {
        return entityCd;
    }

    public void setEntityCd(String entityCd) {
        this.entityCd = entityCd;
    }

    public String getOtherStateNm() {
        return otherStateNm;
    }

    public void setOtherStateNm(String otherStateNm) {
        this.otherStateNm = otherStateNm;
    }

    public String getHomeStateAuthority() {
        return homeStateAuthority;
    }

    public void setHomeStateAuthority(String homeStateAuthority) {
        this.homeStateAuthority = homeStateAuthority;
    }

    public String getOsRecomLttrId() {
        return osRecomLttrId;
    }

    public void setOsRecomLttrId(String osRecomLttrId) {
        this.osRecomLttrId = osRecomLttrId;
    }

    public Long getStateEntryDt() {
        return stateEntryDt;
    }

    public void setStateEntryDt(Long stateEntryDt) {
        this.stateEntryDt = stateEntryDt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getForwardJourneyStartingStation() {
        return forwardJourneyStartingStation;
    }

    public void setForwardJourneyStartingStation(String forwardJourneyStartingStation) {
        this.forwardJourneyStartingStation = forwardJourneyStartingStation;
    }

    public String getForwardJourneyEnroute() {
        return forwardJourneyEnroute;
    }

    public void setForwardJourneyEnroute(String forwardJourneyEnroute) {
        this.forwardJourneyEnroute = forwardJourneyEnroute;
    }

    public String getForwardJourneyDestination() {
        return forwardJourneyDestination;
    }

    public void setForwardJourneyDestination(String forwardJourneyDestination) {
        this.forwardJourneyDestination = forwardJourneyDestination;
    }

    public String getReturnJourneyStartingStation() {
        return returnJourneyStartingStation;
    }

    public void setReturnJourneyStartingStation(String returnJourneyStartingStation) {
        this.returnJourneyStartingStation = returnJourneyStartingStation;
    }

    public String getReturnJourneyEnroute() {
        return returnJourneyEnroute;
    }

    public void setReturnJourneyEnroute(String returnJourneyEnroute) {
        this.returnJourneyEnroute = returnJourneyEnroute;
    }

    public String getReturnJourneyDestination() {
        return returnJourneyDestination;
    }

    public void setReturnJourneyDestination(String returnJourneyDestination) {
        this.returnJourneyDestination = returnJourneyDestination;
    }

    public Integer getDaysPerTrip() {
        return daysPerTrip;
    }

    public void setDaysPerTrip(Integer daysPerTrip) {
        this.daysPerTrip = daysPerTrip;
    }

    public String getHolderAadharNo() {
        return holderAadharNo;
    }

    public void setHolderAadharNo(String holderAadharNo) {
        this.holderAadharNo = holderAadharNo;
    }

    public String getHolderNm() {
        return holderNm;
    }

    public void setHolderNm(String holderNm) {
        this.holderNm = holderNm;
    }

    public String getHolderMobileNo() {
        return holderMobileNo;
    }

    public void setHolderMobileNo(String holderMobileNo) {
        this.holderMobileNo = holderMobileNo;
    }

    public String getPassangerListEnclId() {
        return passangerListEnclId;
    }

    public void setPassangerListEnclId(String passangerListEnclId) {
        this.passangerListEnclId = passangerListEnclId;
    }

    public String getSupportTicketRemarks() {
        return supportTicketRemarks;
    }

    public void setSupportTicketRemarks(String supportTicketRemarks) {
        this.supportTicketRemarks = supportTicketRemarks;
    }

    public String getApplOrigination() {
        return applOrigination;
    }

    public void setApplOrigination(String applOrigination) {
        this.applOrigination = applOrigination;
    }

    public String getRtaOfficeName() {
        return rtaOfficeName;
    }

    public void setRtaOfficeName(String rtaOfficeName) {
        this.rtaOfficeName = rtaOfficeName;
    }

	public String getPermitType() {
		return permitType;
	}

	public void setPermitType(String permitType) {
		this.permitType = permitType;
	}

	public String getPermitSubType() {
		return permitSubType;
	}

	public void setPermitSubType(String permitSubType) {
		this.permitSubType = permitSubType;
	}

}

package org.rta.citizen.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @Author sohan.maurya created on Nov 10, 2016.
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class PermitDetailsModel {

    // @JsonIgnore
    private Long vehicleRcId;
    private Long permitDetailsId;
    private String permitType;
    private String permitSubType;
    private String code;
    private String name;
    private String permitNumber;
    private String rtaOfficeName;
    private Long issueDate;
    private Long validFromDate;
    private Long expiryDate;
    private Boolean status;
    private String routeAreaPermitCode;
    private String routeAreaPermit;
    private String stateUnionPermitCode;
    private String stateUnionPermit;
    private String natureGoodsCarriedCode;
    private String natureGoodsCarried;
    private String anyOtherConditionsCode;
    private String anyOtherConditions;
    private String purposeForVehicleCode;
    private String purposeForVehicle;
    private String rtoSignFilePath;
    private Boolean isTempPermit;
    private Integer daysPerTrip;
    private String forwardJourneyDestination;
    private String forwardJourneyEnroute;
    private String forwardJourneyStartingStation;
    private Integer noOfTrips;
    private String otherStateNm;
    private String returnJourneyDestination;
    private String returnJourneyEnroute;
    private String returnJourneyStartingStation;
    private List<CodeNameModel> neighbouringDistricts;
    private String cov;

    public Long getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(Long vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public Long getPermitDetailsId() {
        return permitDetailsId;
    }

    public void setPermitDetailsId(Long permitDetailsId) {
        this.permitDetailsId = permitDetailsId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public String getRtaOfficeName() {
        return rtaOfficeName;
    }

    public void setRtaOfficeName(String rtaOfficeName) {
        this.rtaOfficeName = rtaOfficeName;
    }

    public Long getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(Long validFromDate) {
        this.validFromDate = validFromDate;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getRouteAreaPermitCode() {
        return routeAreaPermitCode;
    }

    public void setRouteAreaPermitCode(String routeAreaPermitCode) {
        this.routeAreaPermitCode = routeAreaPermitCode;
    }

    public String getRouteAreaPermit() {
        return routeAreaPermit;
    }

    public void setRouteAreaPermit(String routeAreaPermit) {
        this.routeAreaPermit = routeAreaPermit;
    }

    public String getStateUnionPermitCode() {
        return stateUnionPermitCode;
    }

    public void setStateUnionPermitCode(String stateUnionPermitCode) {
        this.stateUnionPermitCode = stateUnionPermitCode;
    }

    public String getStateUnionPermit() {
        return stateUnionPermit;
    }

    public void setStateUnionPermit(String stateUnionPermit) {
        this.stateUnionPermit = stateUnionPermit;
    }

    public String getNatureGoodsCarriedCode() {
        return natureGoodsCarriedCode;
    }

    public void setNatureGoodsCarriedCode(String natureGoodsCarriedCode) {
        this.natureGoodsCarriedCode = natureGoodsCarriedCode;
    }

    public String getNatureGoodsCarried() {
        return natureGoodsCarried;
    }

    public void setNatureGoodsCarried(String natureGoodsCarried) {
        this.natureGoodsCarried = natureGoodsCarried;
    }

    public String getAnyOtherConditionsCode() {
        return anyOtherConditionsCode;
    }

    public void setAnyOtherConditionsCode(String anyOtherConditionsCode) {
        this.anyOtherConditionsCode = anyOtherConditionsCode;
    }

    public String getAnyOtherConditions() {
        return anyOtherConditions;
    }

    public void setAnyOtherConditions(String anyOtherConditions) {
        this.anyOtherConditions = anyOtherConditions;
    }

    public String getPurposeForVehicleCode() {
        return purposeForVehicleCode;
    }

    public void setPurposeForVehicleCode(String purposeForVehicleCode) {
        this.purposeForVehicleCode = purposeForVehicleCode;
    }

    public String getPurposeForVehicle() {
        return purposeForVehicle;
    }

    public void setPurposeForVehicle(String purposeForVehicle) {
        this.purposeForVehicle = purposeForVehicle;
    }
    
    public String getRtoSignFilePath() {
        return rtoSignFilePath;
    }

    public void setRtoSignFilePath(String rtoSignFilePath) {
        this.rtoSignFilePath = rtoSignFilePath;
    }

    public String getPermitType() {
        return permitType;
    }

    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public Boolean getIsTempPermit() {
        return isTempPermit;
    }

    public void setIsTempPermit(Boolean isTempPermit) {
        this.isTempPermit = isTempPermit;
    }

    public Integer getDaysPerTrip() {
        return daysPerTrip;
    }

    public void setDaysPerTrip(Integer daysPerTrip) {
        this.daysPerTrip = daysPerTrip;
    }

    public String getForwardJourneyDestination() {
        return forwardJourneyDestination;
    }

    public void setForwardJourneyDestination(String forwardJourneyDestination) {
        this.forwardJourneyDestination = forwardJourneyDestination;
    }

    public String getForwardJourneyEnroute() {
        return forwardJourneyEnroute;
    }

    public void setForwardJourneyEnroute(String forwardJourneyEnroute) {
        this.forwardJourneyEnroute = forwardJourneyEnroute;
    }

    public String getForwardJourneyStartingStation() {
        return forwardJourneyStartingStation;
    }

    public void setForwardJourneyStartingStation(String forwardJourneyStartingStation) {
        this.forwardJourneyStartingStation = forwardJourneyStartingStation;
    }

    public Integer getNoOfTrips() {
        return noOfTrips;
    }

    public void setNoOfTrips(Integer noOfTrips) {
        this.noOfTrips = noOfTrips;
    }

    public String getOtherStateNm() {
        return otherStateNm;
    }

    public void setOtherStateNm(String otherStateNm) {
        this.otherStateNm = otherStateNm;
    }

    public String getReturnJourneyDestination() {
        return returnJourneyDestination;
    }

    public void setReturnJourneyDestination(String returnJourneyDestination) {
        this.returnJourneyDestination = returnJourneyDestination;
    }

    public String getReturnJourneyEnroute() {
        return returnJourneyEnroute;
    }

    public void setReturnJourneyEnroute(String returnJourneyEnroute) {
        this.returnJourneyEnroute = returnJourneyEnroute;
    }

    public String getReturnJourneyStartingStation() {
        return returnJourneyStartingStation;
    }

    public void setReturnJourneyStartingStation(String returnJourneyStartingStation) {
        this.returnJourneyStartingStation = returnJourneyStartingStation;
    }

	public String getPermitSubType() {
		return permitSubType;
	}

	public void setPermitSubType(String permitSubType) {
		this.permitSubType = permitSubType;
	}
    public List<CodeNameModel> getNeighbouringDistricts() {
        return neighbouringDistricts;
    }

    public void setNeighbouringDistricts(List<CodeNameModel> neighbouringDistricts) {
        this.neighbouringDistricts = neighbouringDistricts;
    }

	public String getCov() {
    	return cov;
    }

    public void setCov(String cov) {
    	this.cov = cov;
    }
}

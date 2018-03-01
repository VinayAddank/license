/**
 * 
 */
package org.rta.citizen.permit.model;

import java.util.List;

import org.rta.citizen.common.model.CodeNameModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@JsonInclude(Include.NON_NULL)
public class PermitNewRequestModel {

    private String vehicleSubClassCode;
    private String vehicleSubClassDecs;
    private String permitClass;
    private String permitType;
    private String permitTypeName;
    private String permitTypeDesc;
    private String tempPermitType;
    private String tempPermitTypeName;
    private String perGoods;
    private String perGoodsDesc;
    private String perRoute;
    private String perRouteDesc;
    private String permitCond;
    private String permitCondDesc;
    private String businessArea;
    private String permitSubType;
    private String permitSubTypeDesc;
    private List<CodeNameModel> neighbouringDistricts;
    private String homeStateCode;
    private String homeStateName;
    private String neighbouringStateCode;
    private String neighbouringStateName;
    private Long fromDate;
    private Long toDate;
    private String fwdStartStateName;
    private String fwdStartDistrictName;
    private String fwdStartPlaceName;
    private String fwdDestStateName;
    private String fwdDestStateCode;
    private String fwdDestDistrictCode;
    private String fwdDestDistrictName;
    private String fwdDestPlaceName;
    private String rtnStartStateName;
    private String rtnStartDistrictName;
    private String rtnStartPlaceName;
    private String rtnDestStateName;
    private String rtnDestDistrictName;
    private String rtnDestPlaceName;
    private String purpose;
    private String fwdPlacesCovering;
    private String rtnPlacesCovering;
    private String adhaarNo;

    public String getVehicleSubClassCode() {
        return vehicleSubClassCode;
    }

    public void setVehicleSubClassCode(String vehicleSubClassCode) {
        this.vehicleSubClassCode = vehicleSubClassCode;
    }

    public String getVehicleSubClassDecs() {
        return vehicleSubClassDecs;
    }

    public void setVehicleSubClassDecs(String vehicleSubClassDecs) {
        this.vehicleSubClassDecs = vehicleSubClassDecs;
    }

    public String getPermitClass() {
        return permitClass;
    }

    public void setPermitClass(String permitClass) {
        this.permitClass = permitClass;
    }

    public String getPermitType() {
        return permitType;
    }

    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public String getPermitTypeName() {
        return permitTypeName;
    }

    public void setPermitTypeName(String permitTypeName) {
        this.permitTypeName = permitTypeName;
    }

    public String getPermitTypeDesc() {
        return permitTypeDesc;
    }

    public void setPermitTypeDesc(String permitTypeDesc) {
        this.permitTypeDesc = permitTypeDesc;
    }

    public String getTempPermitType() {
        return tempPermitType;
    }

    public void setTempPermitType(String tempPermitType) {
        this.tempPermitType = tempPermitType;
    }

    public String getTempPermitTypeName() {
        return tempPermitTypeName;
    }

    public void setTempPermitTypeName(String tempPermitTypeName) {
        this.tempPermitTypeName = tempPermitTypeName;
    }

    public String getPerGoods() {
        return perGoods;
    }

    public void setPerGoods(String perGoods) {
        this.perGoods = perGoods;
    }

    public String getPerGoodsDesc() {
        return perGoodsDesc;
    }

    public void setPerGoodsDesc(String perGoodsDesc) {
        this.perGoodsDesc = perGoodsDesc;
    }

    public String getPerRoute() {
        return perRoute;
    }

    public void setPerRoute(String perRoute) {
        this.perRoute = perRoute;
    }

    public String getPerRouteDesc() {
        return perRouteDesc;
    }

    public void setPerRouteDesc(String perRouteDesc) {
        this.perRouteDesc = perRouteDesc;
    }

    public String getPermitCond() {
        return permitCond;
    }

    public void setPermitCond(String permitCond) {
        this.permitCond = permitCond;
    }

    public String getPermitCondDesc() {
        return permitCondDesc;
    }

    public void setPermitCondDesc(String permitCondDesc) {
        this.permitCondDesc = permitCondDesc;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public String getPermitSubType() {
        return permitSubType;
    }

    public void setPermitSubType(String permitSubType) {
        this.permitSubType = permitSubType;
    }

    public String getPermitSubTypeDesc() {
        return permitSubTypeDesc;
    }

    public void setPermitSubTypeDesc(String permitSubTypeDesc) {
        this.permitSubTypeDesc = permitSubTypeDesc;
    }

    public List<CodeNameModel> getNeighbouringDistricts() {
        return neighbouringDistricts;
    }

    public void setNeighbouringDistricts(List<CodeNameModel> neighbouringDistricts) {
        this.neighbouringDistricts = neighbouringDistricts;
    }

    public String getHomeStateCode() {
        return homeStateCode;
    }

    public void setHomeStateCode(String homeStateCode) {
        this.homeStateCode = homeStateCode;
    }

    public String getHomeStateName() {
        return homeStateName;
    }

    public void setHomeStateName(String homeStateName) {
        this.homeStateName = homeStateName;
    }

    public String getNeighbouringStateCode() {
        return neighbouringStateCode;
    }

    public void setNeighbouringStateCode(String neighbouringStateCode) {
        this.neighbouringStateCode = neighbouringStateCode;
    }

    public String getNeighbouringStateName() {
        return neighbouringStateName;
    }

    public void setNeighbouringStateName(String neighbouringStateName) {
        this.neighbouringStateName = neighbouringStateName;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public String getFwdStartStateName() {
        return fwdStartStateName;
    }

    public void setFwdStartStateName(String fwdStartStateName) {
        this.fwdStartStateName = fwdStartStateName;
    }

    public String getFwdStartDistrictName() {
        return fwdStartDistrictName;
    }

    public void setFwdStartDistrictName(String fwdStartDistrictName) {
        this.fwdStartDistrictName = fwdStartDistrictName;
    }

    public String getFwdStartPlaceName() {
        return fwdStartPlaceName;
    }

    public void setFwdStartPlaceName(String fwdStartPlaceName) {
        this.fwdStartPlaceName = fwdStartPlaceName;
    }

    public String getFwdDestStateName() {
        return fwdDestStateName;
    }

    public void setFwdDestStateName(String fwdDestStateName) {
        this.fwdDestStateName = fwdDestStateName;
    }

    public String getFwdDestStateCode() {
        return fwdDestStateCode;
    }

    public void setFwdDestStateCode(String fwdDestStateCode) {
        this.fwdDestStateCode = fwdDestStateCode;
    }

    public String getFwdDestDistrictCode() {
        return fwdDestDistrictCode;
    }

    public void setFwdDestDistrictCode(String fwdDestDistrictCode) {
        this.fwdDestDistrictCode = fwdDestDistrictCode;
    }

    public String getFwdDestDistrictName() {
        return fwdDestDistrictName;
    }

    public void setFwdDestDistrictName(String fwdDestDistrictName) {
        this.fwdDestDistrictName = fwdDestDistrictName;
    }

    public String getFwdDestPlaceName() {
        return fwdDestPlaceName;
    }

    public void setFwdDestPlaceName(String fwdDestPlaceName) {
        this.fwdDestPlaceName = fwdDestPlaceName;
    }

    public String getRtnStartStateName() {
        return rtnStartStateName;
    }

    public void setRtnStartStateName(String rtnStartStateName) {
        this.rtnStartStateName = rtnStartStateName;
    }

    public String getRtnStartDistrictName() {
        return rtnStartDistrictName;
    }

    public void setRtnStartDistrictName(String rtnStartDistrictName) {
        this.rtnStartDistrictName = rtnStartDistrictName;
    }

    public String getRtnStartPlaceName() {
        return rtnStartPlaceName;
    }

    public void setRtnStartPlaceName(String rtnStartPlaceName) {
        this.rtnStartPlaceName = rtnStartPlaceName;
    }

    public String getRtnDestStateName() {
        return rtnDestStateName;
    }

    public void setRtnDestStateName(String rtnDestStateName) {
        this.rtnDestStateName = rtnDestStateName;
    }

    public String getRtnDestDistrictName() {
        return rtnDestDistrictName;
    }

    public void setRtnDestDistrictName(String rtnDestDistrictName) {
        this.rtnDestDistrictName = rtnDestDistrictName;
    }

    public String getRtnDestPlaceName() {
        return rtnDestPlaceName;
    }

    public void setRtnDestPlaceName(String rtnDestPlaceName) {
        this.rtnDestPlaceName = rtnDestPlaceName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getFwdPlacesCovering() {
        return fwdPlacesCovering;
    }

    public void setFwdPlacesCovering(String fwdPlacesCovering) {
        this.fwdPlacesCovering = fwdPlacesCovering;
    }

    public String getRtnPlacesCovering() {
        return rtnPlacesCovering;
    }

    public void setRtnPlacesCovering(String rtnPlacesCovering) {
        this.rtnPlacesCovering = rtnPlacesCovering;
    }

    public String getAdhaarNo() {
        return adhaarNo;
    }

    public void setAdhaarNo(String adhaarNo) {
        this.adhaarNo = adhaarNo;
    }

    @Override
    public String toString() {
        return "PermitNewRequestModel [vehicleSubClassCode=" + vehicleSubClassCode + ", vehicleSubClassDecs="
                + vehicleSubClassDecs + ", permitClass=" + permitClass + ", permitType=" + permitType
                + ", permitTypeName=" + permitTypeName + ", permitTypeDesc=" + permitTypeDesc + ", tempPermitType="
                + tempPermitType + ", tempPermitTypeName=" + tempPermitTypeName + ", perGoods=" + perGoods
                + ", perGoodsDesc=" + perGoodsDesc + ", perRoute=" + perRoute + ", perRouteDesc=" + perRouteDesc
                + ", permitCond=" + permitCond + ", permitCondDesc=" + permitCondDesc + ", businessArea=" + businessArea
                + ", permitSubType=" + permitSubType + ", permitSubTypeDesc=" + permitSubTypeDesc
                + ", neighbouringDistricts=" + neighbouringDistricts + ", homeStateCode=" + homeStateCode
                + ", homeStateName=" + homeStateName + ", neighbouringStateCode=" + neighbouringStateCode
                + ", neighbouringStateName=" + neighbouringStateName + ", fromDate=" + fromDate + ", toDate=" + toDate
                + ", fwdStartStateName=" + fwdStartStateName + ", fwdStartDistrictName=" + fwdStartDistrictName
                + ", fwdStartPlaceName=" + fwdStartPlaceName + ", fwdDestStateName=" + fwdDestStateName
                + ", fwdDestStateCode=" + fwdDestStateCode + ", fwdDestDistrictCode=" + fwdDestDistrictCode
                + ", fwdDestDistrictName=" + fwdDestDistrictName + ", fwdDestPlaceName=" + fwdDestPlaceName
                + ", rtnStartStateName=" + rtnStartStateName + ", rtnStartDistrictName=" + rtnStartDistrictName
                + ", rtnStartPlaceName=" + rtnStartPlaceName + ", rtnDestStateName=" + rtnDestStateName
                + ", rtnDestDistrictName=" + rtnDestDistrictName + ", rtnDestPlaceName=" + rtnDestPlaceName
                + ", purpose=" + purpose + ", fwdPlacesCovering=" + fwdPlacesCovering + ", rtnPlacesCovering="
                + rtnPlacesCovering + ", adhaarNo=" + adhaarNo + "]";
    }

}

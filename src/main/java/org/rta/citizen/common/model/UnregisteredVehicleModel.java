package org.rta.citizen.common.model;

import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.enums.VehicleCategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class UnregisteredVehicleModel extends VehicleBaseModel {

    private static final long serialVersionUID = -5607153723648570938L;
    private Double enginePower;
    private String frontAxleDesc;
    private Double frontAxleWeight;
    private Long height;
    private Long length;
    private String pollutionNormsDesc;
    private String o1AxleDesc;
    private Integer o1AxleWeight;
    private String o2AxleDesc;
    private Integer o2AxleWeight;
    private String o3AxleDesc;
    private Integer o3AxleWeight;
    private String o4AxleDesc;
    private Integer o4AxleWeight;
    private String o5AxleDesc;
    private Integer o5AxleWeight;
    private String rearAxleDesc;
    private Integer rearAxleWeight;
    private Integer wheelbase;
    private Long width;
    private String tandemAxelDescp;
    private Integer tandemAxelWeight;
    private String bodyTypeUpdated;
    private Long heightUpdated;
    private Long widthUpdated;
    private Long lengthUpdated;
    private Long completionDate;
    private AlterationCategory alterationCategory;
    private String vehicleClassDes;
    private VehicleCategory vehicleCategory;

    public Double getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Double enginePower) {
        this.enginePower = enginePower;
    }

    public String getFrontAxleDesc() {
        return frontAxleDesc;
    }

    public void setFrontAxleDesc(String frontAxleDesc) {
        this.frontAxleDesc = frontAxleDesc;
    }

    public Double getFrontAxleWeight() {
        return frontAxleWeight;
    }

    public void setFrontAxleWeight(Double frontAxleWeight) {
        this.frontAxleWeight = frontAxleWeight;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getPollutionNormsDesc() {
        return pollutionNormsDesc;
    }

    public void setPollutionNormsDesc(String pollutionNormsDesc) {
        this.pollutionNormsDesc = pollutionNormsDesc;
    }

    public String getO1AxleDesc() {
        return o1AxleDesc;
    }

    public void setO1AxleDesc(String o1AxleDesc) {
        this.o1AxleDesc = o1AxleDesc;
    }

    public Integer getO1AxleWeight() {
        return o1AxleWeight;
    }

    public void setO1AxleWeight(Integer o1AxleWeight) {
        this.o1AxleWeight = o1AxleWeight;
    }

    public String getO2AxleDesc() {
        return o2AxleDesc;
    }

    public void setO2AxleDesc(String o2AxleDesc) {
        this.o2AxleDesc = o2AxleDesc;
    }

    public Integer getO2AxleWeight() {
        return o2AxleWeight;
    }

    public void setO2AxleWeight(Integer o2AxleWeight) {
        this.o2AxleWeight = o2AxleWeight;
    }

    public String getO3AxleDesc() {
        return o3AxleDesc;
    }

    public void setO3AxleDesc(String o3AxleDesc) {
        this.o3AxleDesc = o3AxleDesc;
    }

    public Integer getO3AxleWeight() {
        return o3AxleWeight;
    }

    public void setO3AxleWeight(Integer o3AxleWeight) {
        this.o3AxleWeight = o3AxleWeight;
    }

    public String getO4AxleDesc() {
        return o4AxleDesc;
    }

    public void setO4AxleDesc(String o4AxleDesc) {
        this.o4AxleDesc = o4AxleDesc;
    }

    public Integer getO4AxleWeight() {
        return o4AxleWeight;
    }

    public void setO4AxleWeight(Integer o4AxleWeight) {
        this.o4AxleWeight = o4AxleWeight;
    }

    public String getO5AxleDesc() {
        return o5AxleDesc;
    }

    public void setO5AxleDesc(String o5AxleDesc) {
        this.o5AxleDesc = o5AxleDesc;
    }

    public Integer getO5AxleWeight() {
        return o5AxleWeight;
    }

    public void setO5AxleWeight(Integer o5AxleWeight) {
        this.o5AxleWeight = o5AxleWeight;
    }

    public String getRearAxleDesc() {
        return rearAxleDesc;
    }

    public void setRearAxleDesc(String rearAxleDesc) {
        this.rearAxleDesc = rearAxleDesc;
    }

    public Integer getRearAxleWeight() {
        return rearAxleWeight;
    }

    public void setRearAxleWeight(Integer rearAxleWeight) {
        this.rearAxleWeight = rearAxleWeight;
    }

    public Integer getWheelbase() {
        return wheelbase;
    }

    public void setWheelbase(Integer wheelbase) {
        this.wheelbase = wheelbase;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public String getTandemAxelDescp() {
        return tandemAxelDescp;
    }

    public void setTandemAxelDescp(String tandemAxelDescp) {
        this.tandemAxelDescp = tandemAxelDescp;
    }

    public Integer getTandemAxelWeight() {
        return tandemAxelWeight;
    }

    public void setTandemAxelWeight(Integer tandemAxelWeight) {
        this.tandemAxelWeight = tandemAxelWeight;
    }

    public String getBodyTypeUpdated() {
        return bodyTypeUpdated;
    }

    public void setBodyTypeUpdated(String bodyTypeUpdated) {
        this.bodyTypeUpdated = bodyTypeUpdated;
    }

    public Long getHeightUpdated() {
        return heightUpdated;
    }

    public void setHeightUpdated(Long heightUpdated) {
        this.heightUpdated = heightUpdated;
    }

    public Long getWidthUpdated() {
        return widthUpdated;
    }

    public void setWidthUpdated(Long widthUpdated) {
        this.widthUpdated = widthUpdated;
    }

    public Long getLengthUpdated() {
        return lengthUpdated;
    }

    public void setLengthUpdated(Long lengthUpdated) {
        this.lengthUpdated = lengthUpdated;
    }

    public Long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Long completionDate) {
        this.completionDate = completionDate;
    }

    public AlterationCategory getAlterationCategory() {
        return alterationCategory;
    }

    public void setAlterationCategory(AlterationCategory alterationCategory) {
        this.alterationCategory = alterationCategory;
    }

    public String getVehicleClassDes() {
        return vehicleClassDes;
    }

    public void setVehicleClassDes(String vehicleClassDes) {
        this.vehicleClassDes = vehicleClassDes;
    }

    public VehicleCategory getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(VehicleCategory vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }
}

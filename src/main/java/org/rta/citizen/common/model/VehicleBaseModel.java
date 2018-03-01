package org.rta.citizen.common.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class VehicleBaseModel extends BaseModel {

    private static final long serialVersionUID = -743202083593468529L;
    
//    @NotNull(message = "vehicleDetailsId is missing")
    private Long vehicleDetailsId;
    
    @NotNull(message = "vahanId is missing")
    private Long vahanId;
    
    @NotNull(message = "chassisNumber is missing")
    private String chassisNumber;
    
    @NotNull(message = "engineNumber is missing")
    private String engineNumber;
    
    @NotNull(message = "fuelUsed is missing")
    private String fuelUsed;
    
    @NotNull(message = "vehicleClass is missing")
    private String vehicleClass;
    
    @NotNull(message = "makersName is missing")
    private String makersName;
    
    @NotNull(message = "makersClass is missing")
    private String makersClass;
    
    @NotNull(message = "manufacturingMonthYear is missing")
    private String manufacturingMonthYear;
    
    @NotNull(message = "seatingCapacity is missing")
    private Integer seatingCapacity;
    
    @NotNull(message = "ladenWeight is missing")
    private Long rlw;
    
    @NotNull(message = "unLadenWeight is missing")
    private Long ulw;
    
    @NotNull(message = "color is missing")
    private String color;
    
//    @NotNull(message = "vahanId is missing")
    private Integer noCyl;
    
//    @NotNull(message = "standCapacity is missing")
    private Integer standCapacity;
    
//    @NotNull(message = "cubicCapacity is missing")
    private Double cubicCapacity;
    
//    @NotNull(message = "bodyTypeDesc is missing")
    private String bodyTypeDesc;
    
    @NotNull(message = "taxType is missing")
    private String taxType;
    
    @NotNull(message = "vehicleSubClass is missing")
    private String vehicleSubClass;

    private VehicleBodyModel alterationDetails;
    
    public Long getVehicleDetailsId() {
        return vehicleDetailsId;
    }

    public void setVehicleDetailsId(Long vehicleDetailsId) {
        this.vehicleDetailsId = vehicleDetailsId;
    }

    public Long getVahanId() {
        return vahanId;
    }

    public void setVahanId(Long vahanId) {
        this.vahanId = vahanId;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(String fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getMakersName() {
        return makersName;
    }

    public void setMakersName(String makersName) {
        this.makersName = makersName;
    }

    public String getMakersClass() {
        return makersClass;
    }

    public void setMakersClass(String makersClass) {
        this.makersClass = makersClass;
    }

    public String getManufacturingMonthYear() {
        return manufacturingMonthYear;
    }

    public void setManufacturingMonthYear(String manufacturingMonthYear) {
        this.manufacturingMonthYear = manufacturingMonthYear;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    @JsonProperty(value = "ladenWeight")
    public Long getRlw() {
        return rlw;
    }

    public void setRlw(Long rlw) {
        this.rlw = rlw;
    }

    @JsonProperty(value = "unladenWeight")
    public Long getUlw() {
        return ulw;
    }

    public void setUlw(Long ulw) {
        this.ulw = ulw;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getNoCyl() {
        return noCyl;
    }

    public void setNoCyl(Integer noCyl) {
        this.noCyl = noCyl;
    }

    public Integer getStandCapacity() {
        return standCapacity;
    }

    public void setStandCapacity(Integer standCapacity) {
        this.standCapacity = standCapacity;
    }

    public Double getCubicCapacity() {
        return cubicCapacity;
    }

    public void setCubicCapacity(Double cubicCapacity) {
        this.cubicCapacity = cubicCapacity;
    }

    public String getBodyTypeDesc() {
        return bodyTypeDesc;
    }

    public void setBodyTypeDesc(String bodyTypeDesc) {
        this.bodyTypeDesc = bodyTypeDesc;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getVehicleSubClass() {
        return vehicleSubClass;
    }

    public void setVehicleSubClass(String vehicleSubClass) {
        this.vehicleSubClass = vehicleSubClass;
    }

	public VehicleBodyModel getAlterationDetails() {
		return alterationDetails;
	}

	public void setAlterationDetails(VehicleBodyModel alterationDetails) {
		this.alterationDetails = alterationDetails;
	}
    
}

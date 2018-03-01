package org.rta.citizen.licence.enums;

import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.StringsUtil;


public enum LicenceVehicleClass {

	AUTORICKSHAW_NON_TRANSPORT("ARNT","Autorickshaw Non Transport", "N"), AUTORICKSHAW_TRANSPORT("ART","Autorickshaw Transport", "T"),
	HEAVY_GOODS_VEHICLE("HGV","Heavy Goods Vehicle", "T"), HEAVY_PASSENGER_VEHICLE("HPV","Heavy Passenger Vehicle", "T"),
	HEAVY_TRANSPORT_VEHICLE("HTV","Heavy Transport Vehicle", "T"),	HAZARDDOUS_GOODS_CARRIAGE("HZRD","Hazardous Goods Carriage", "T"),
	INVALID_CARRIAGE("IC","Invalid Carriage", "N"), LIGHT_MOTOR_VEHICLE_NON_TRANSPORT("LMVNT","Light Motor Vehicle Non Transport", "N"),
	LIGHT_MOTOR_VEHICLE_TRANSPORT("LMVT","Light Motor Vehicle Transport", "T"), MOTOR_CYCLE_WITHOUT_GEAR("MCW","Motor Cycle Without Gear (Below 50 CC)", "N"),
	MOTOR_CYCLE_WITH_GEAR("MCWG","Motor Cycle With Gear", "N"), MEDIUM_GOODS_VEHICLE("MGV","Medium Goods Vehicle", "T"),
	MEDIUM_PASSENGER_VEHICLE("MPV","Medium Passenger Vehicle", "T"),	MOTOR_CAB("MTL","Motor Cab", "T"),
	ROAD_ROLLER_NON_TRANSPORT("RDRLNT","Road roller Non Transport", "N"), TRACTOR("TR","Tractor", "N"), OTHER("OTH","Other","T");

    private static Map<String, LicenceVehicleClass> codeToCovs = new HashMap<>();

    private String code;
    private String label;
    private String transportType;

    static {
        for (LicenceVehicleClass licenceVehicleClass : LicenceVehicleClass.values()) {
        	codeToCovs.put(licenceVehicleClass.getCode(), licenceVehicleClass);
        }
    }

    private LicenceVehicleClass(String code, String label, String transportType) {
        this.label = label;
        this.code = code;
        this.transportType = transportType;
    }

    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(String label) {
		this.label = label;
	}
    
    public String getCode() {
		return code;
	}

    public void setCode(String code) {
		this.code = code;
	}
        
    public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}
	
    public static LicenceVehicleClass getLicenceVehicleClassByCode(String code) {
    	if(StringsUtil.isNullOrEmpty(code))
    		return OTHER;
        return codeToCovs.get(code.toUpperCase());
    }
}

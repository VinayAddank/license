package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PermitType {
    AITC("AITC", "ALL INDIA TOURIST CAB"), AITP("AITP", "ALL INDIA TOURIST PERMIT"), CCP("CCP", "CONTRACT CARRIAGE PERMIT"),
    CSPP("CSPP", "COUNTER SIGNATURE PERMIT PUCCKA"), EIB("EIB","EDUCATIONAL INSTITUTE PERMIT"), 
    GCP("GCP", "GOODS CARRIAGE PERMIT"), NP("NP", "NATIONAL PERMIT"), PSVP("PSVP","PRIVATE SERVICE VEHICLE PERMIT"), 
    SBP("SBP", "SPARE BUS PERMIT"), SCP("SCP","STAGE CARRIAGE PERMIT"), SSP("SSP", "SPECIAL PERMIT"), 
    SSPSO("SSPSO", "SPECIAL PERMIT FOR SPECIAL OCCASIONS"), STL("STL","SHORT TERM LICENSES"), 
    TCCP("TCCP","TEMPORARY CONTRACT CARRIAGE PERMIT"), TGCP("TGCP","TEMPORARY GOODS CARRIAGE PERMIT"), 
    TSCP("TSCP","TEMPORARY STAGE CARRIAGE PERMIT");

    private static final Map<String, PermitType> labelToType = new HashMap<String, PermitType>();
    private static final Map<String, PermitType> valueToType = new HashMap<String, PermitType>();
    static {
        for (PermitType permitType : PermitType.values()) {
            labelToType.put(permitType.getLabel(), permitType);
        }
        for (PermitType permitType : EnumSet.allOf(PermitType.class)) {
            valueToType.put(permitType.getValue(), permitType);
        }
    }

    private PermitType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    private String code;
    private String label;

    @JsonValue
    public String toValue() {
        return this.label;
    }

    public String getValue() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static PermitType getPermitType(String label) {
        return labelToType.get(label);
    }

    public static PermitType getPermitTypeCode(String code) {
        return valueToType.get(code);
    }
}


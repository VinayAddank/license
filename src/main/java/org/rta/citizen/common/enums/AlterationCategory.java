package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AlterationCategory {

    SEATING_CAPACITY(1, "SEATING CAPACITY"), BODY_TYPE(2, "BODY TYPE"), FUEL_TYPE(3, "FUEL TYPE"), VEHICLE_TYPE(4, "VEHICLE TYPE"), ENGINE_ALTERATION(5,"ENGINE ALTERATION"),
    NEW_VEHICLE_ALTERATION(6,"NEW VEHICLE ALTERATION");


    private static final Map<String, AlterationCategory> labelToType = new HashMap<String, AlterationCategory>();
    private static final Map<Integer, AlterationCategory> valueToType = new HashMap<Integer, AlterationCategory>();
    

    static {
        for (AlterationCategory alterationCategory : AlterationCategory.values()) {
            labelToType.put(alterationCategory.getLabel(), alterationCategory);
        }
        for (AlterationCategory alterationCategory : EnumSet.allOf(AlterationCategory.class)) {
            valueToType.put(alterationCategory.getValue(), alterationCategory);
        }
    }
    
    private AlterationCategory(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private Integer value;
    private String label;
            
    @JsonValue
    public String toValue() {
        return this.label;
    }

    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
    
    public static AlterationCategory getAlterationCategory(String label) {
        return labelToType.get(label);
    }

    public static AlterationCategory getAlterationCategory(Integer value) {
        return valueToType.get(value);
    }
}

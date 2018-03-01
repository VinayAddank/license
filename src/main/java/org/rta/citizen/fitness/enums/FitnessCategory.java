package org.rta.citizen.fitness.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FitnessCategory {
	
	NEW_VEHICLE_FC(1, "New Vehicle"), OLD_VEHICLE_FC(2, "Old Vehicle");
    
    private static final Map<String, FitnessCategory> labelToType = new HashMap<String, FitnessCategory>();
    private static final Map<Integer, FitnessCategory> valueToType = new HashMap<Integer, FitnessCategory>();
    static {
        for (FitnessCategory fitnessCategory : FitnessCategory.values()) {
            labelToType.put(fitnessCategory.getLabel(), fitnessCategory);
        }
        for (FitnessCategory fitnessCategory : EnumSet.allOf(FitnessCategory.class)) {
            valueToType.put(fitnessCategory.getValue(), fitnessCategory);
        }
    }

    private FitnessCategory(Integer value, String label) {
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

    public static FitnessCategory getFitnessCategory(String label) {
        return labelToType.get(label);
    }

    public static FitnessCategory getFitnessCategory(Integer value) {
        return valueToType.get(value);
    }
	
}

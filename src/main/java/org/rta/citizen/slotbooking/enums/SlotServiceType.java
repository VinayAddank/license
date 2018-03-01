package org.rta.citizen.slotbooking.enums;

import java.util.HashMap;
import java.util.Map;

public enum SlotServiceType {

    DRIVING_LICENCE_TEST_LMV("DRIVING_LICENCE_TEST_LMV"),
    DRIVING_LICENCE_TEST_HMV("DRIVING_LICENCE_TEST_HMV"),
    DRIVING_LICENCE_TEST_MCWG50CC("DRIVING_LICENCE_TEST_MCWG50CC"),
    DRIVING_LICENCE_TEST_MCWG("DRIVING_LICENCE_TEST_MCWG"),
    
    DRIVING_LICENCE_MACHINE_TEST("DRIVING_LICENCE_MACHINE_TEST"),
    VEHICLE_INSPECTION_CHASSIS_ONLY("VEHICLE_INSPECTION_CHASSIS_ONLY"), 
    TWO_WHEELER_TEST("TWO_WHEELER_TEST"),
    THREE_FOUR_WHEELER_TEST("THREE_FOUR_WHEELER_TEST"),
    HMV_TEST("HMV_TEST");

    private static Map<String, SlotServiceType> labelToType = new HashMap<>();
    
    static {
        for (SlotServiceType slotServiceType : SlotServiceType.values()) {
            labelToType.put(slotServiceType.label, slotServiceType);
        }
    }
    
    private String label;

    private SlotServiceType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static SlotServiceType getSlotType(String label) {
        if (label == null) {
            return null;
        }
        return labelToType.get(label);
        /*if (SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY.label.equalsIgnoreCase(label)) {
            return SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY;
        } else if (SlotServiceType.DRIVING_LICENCE_TEST_LMV.label.equalsIgnoreCase(label)) {
            return SlotServiceType.DRIVING_LICENCE_TEST_LMV;
        } else if (SlotServiceType.DRIVING_LICENCE_TEST_HMV.label.equalsIgnoreCase(label)) {
            return SlotServiceType.DRIVING_LICENCE_TEST_HMV;
        } else if (SlotServiceType.DRIVING_LICENCE_TEST_MCWG50CC.label.equalsIgnoreCase(label)) {
            return SlotServiceType.DRIVING_LICENCE_TEST_MCWG50CC;
        } else if (SlotServiceType.DRIVING_LICENCE_TEST_MCWG.label.equalsIgnoreCase(label)) {
            return SlotServiceType.DRIVING_LICENCE_TEST_MCWG;
        } 
        return null;*/
    }
    
}

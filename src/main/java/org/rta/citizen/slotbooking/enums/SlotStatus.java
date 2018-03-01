package org.rta.citizen.slotbooking.enums;

import java.util.HashMap;
import java.util.Map;

public enum SlotStatus {

    OCCUPIED(1, "occupied"), BOOKED(2, "booked"), FREE(3, "free"), EXPIRED(4, "expired");

    private Integer value;
    private String label;

    private static Map<String, SlotStatus> slotStatus = new HashMap<>();
    private static Map<Integer, SlotStatus> valueToType = new HashMap<>();

    static {
        for (SlotStatus status : SlotStatus.values()) {
            slotStatus.put(status.getLabel(), status);
        }
        for (SlotStatus status : SlotStatus.values()) {
            valueToType.put(status.getValue(), status);
        }
    }

    private SlotStatus(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    public static SlotStatus getSlotStatus(String label) {
        return label != null ? slotStatus.get(label) : null;
    }
    
    public static SlotStatus getSlotStatus(Integer value) {
        return value != null ? valueToType.get(value) : null;
    }

}

package org.rta.citizen.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum CalendarDirection {
    
    PREVIOUS(-1, "Previous"),
    NEXT(1, "Next");
    
    private static Map<Integer, CalendarDirection> valueToCalendarDirection = new HashMap<>();
    private static Map<String, CalendarDirection> labelToCalendarDirection = new HashMap<>();
    
    private Integer value;
    private String label;
    
    static {
        for (CalendarDirection calendarDirection : CalendarDirection.values()) {
            valueToCalendarDirection.put(calendarDirection.getValue(), calendarDirection);
        }
        
        for (CalendarDirection calendarDirection : CalendarDirection.values()) {
            labelToCalendarDirection.put(calendarDirection.getLabel(), calendarDirection);
        }
    }
    
    private CalendarDirection(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
    
    public Integer getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }
    
    public static CalendarDirection getCalendarDirection(Integer value) {
        return valueToCalendarDirection.get(value);
    }
    
    public static CalendarDirection getCalendarDirection(String label) {
        return labelToCalendarDirection.get(label);
    }
    
}

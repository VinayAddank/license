package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @Author rahul.sharma
 */

public enum PRType {

    ORDINARY(1, "Ordinary"),
    SPECIAL(2, "Special");

    private int id;
    private String label;

    private PRType() {}

    private PRType(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public static PRType getPRType(Integer value) {
        if (ObjectsUtil.isNull(value)) {
            return null;
        }
        if (value == ORDINARY.getId()) {
            return ORDINARY;
        } else if (value == SPECIAL.getId()) {
            return SPECIAL;
        }
        return null;
    }
    
    public static PRType getPRType(String value) {
        if (ObjectsUtil.isNull(value)) {
            return null;
        }
        if (ORDINARY.getLabel().toUpperCase().equals(value.toUpperCase())) {
            return ORDINARY;
        } else if (SPECIAL.getLabel().toUpperCase().equals(value.toUpperCase())) {
            return SPECIAL;
        }
        return null;
    }

}

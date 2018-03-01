/**
 * 
 */
package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @author arun.verma
 *
 */
public enum Gender {

    MALE(1, "MALE"), FEMALE(2, "FEMALE"), NOT_RECORDED(3, "NOT_RECORDED");

    private int id;
    private String label;

    Gender() {}

    Gender(int id, String label) {
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

    public static Gender getGender(Integer value) {
        if (ObjectsUtil.isNull(value)) {
            return null;
        }
        if (value == MALE.getId()) {
            return MALE;
        } else if (value == FEMALE.getId()) {
            return FEMALE;
        }
        return null;
    }

    public static Gender getGender(String value) {
        if (ObjectsUtil.isNull(value)) {
            return null;
        }
        if (MALE.getLabel().toUpperCase().equals(value.toUpperCase())) {
            return MALE;
        } else if (FEMALE.getLabel().toUpperCase().equals(value.toUpperCase())) {
            return FEMALE;
        }
        return null;
    }
}

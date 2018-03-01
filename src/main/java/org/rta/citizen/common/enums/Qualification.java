package org.rta.citizen.common.enums;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Qualification enum </br>
 * 
 * @author rahul.sharma
 *
 */
public enum Qualification {

    /**
     * value is code
     * label is name
     */
    POSTGRADUATE(1, "POST GRADUATE"), GRADUATE(2, "GRADUATE"), HIGHSCHOOL(3, "10TH PASS"), PASS(4,
            "8TH PASS"), ILLITERATE(5, "ILLITERATE"), INTERMEDIATE(7, "INTERMEDIATE"),NA(6, "N.A.");

    private Integer value;
    private String label;

    private Qualification(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private static Map<Integer, String> map;

    static {
        Integer count = Qualification.values().length;
        map = new LinkedHashMap<>(count);
        for (Qualification taxType : Qualification.values()) {
            map.put(taxType.value, taxType.label);
        }
    }

    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static Qualification getQualification(String label) {
        if (!Objects.isNull(label)) {
            if (Qualification.POSTGRADUATE.label.equals(label)) {
                return Qualification.POSTGRADUATE;
            }
            if (Qualification.GRADUATE.label.equals(label)) {
                return Qualification.GRADUATE;
            }
            if (Qualification.HIGHSCHOOL.label.equals(label)) {
                return Qualification.HIGHSCHOOL;
            }
            if (Qualification.PASS.label.equals(label)) {
                return Qualification.PASS;
            }
            if (Qualification.ILLITERATE.label.equals(label)) {
                return Qualification.ILLITERATE;
            }
            if (Qualification.INTERMEDIATE.label.equals(label)) {
                return Qualification.INTERMEDIATE;
            }
            if (Qualification.NA.label.equals(label)) {
                return Qualification.NA;
            }
        }
        return null;
    }

    public static Qualification getQualification(Integer value) {
        if (!Objects.isNull(value)) {
            if (Qualification.POSTGRADUATE.value == value) {
                return Qualification.POSTGRADUATE;
            }
            if (Qualification.GRADUATE.value == value) {
                return Qualification.GRADUATE;
            }
            if (Qualification.HIGHSCHOOL.value == value) {
                return Qualification.HIGHSCHOOL;
            }
            if (Qualification.PASS.value == value) {
                return Qualification.PASS;
            }
            if (Qualification.ILLITERATE.value == value) {
                return Qualification.ILLITERATE;
            }
            if (Qualification.INTERMEDIATE.value == value) {
                return Qualification.INTERMEDIATE;
            }
            if (Qualification.NA.value == value) {
                return Qualification.NA;
            }
        }
        return null;
    }

    public static Map<Integer, String> getAll() {
        return map;
    }
}

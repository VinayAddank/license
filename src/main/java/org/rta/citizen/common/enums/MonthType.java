package org.rta.citizen.common.enums;


/**
 * @Author sandeep.yadav
 */

public enum MonthType {

    JANUARY(0, "JANUARY"), FEBRUARY(1, "FEBRUARY"), MARCH(2, "MARCH"), APRIL(3, "APRIL"), MAY(4, "MAY"), JUNE(5,
            "JUNE"), JULY(6, "JULY"), AUGUST(7, "AUGUST"), SEPTEMBER(8,
                    "SEPTEMBER"), OCTOBER(9, "OCTOBER"), NOVEMBER(10, "NOVEMBER"), DECEMBER(11, "DECEMBER");

    private int id;
    private String label;

    private MonthType() {}

    private MonthType(int id, String label) {
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

    public static MonthType getMonthType(Integer value) {
        if (value == null) {
            return null;
        }
        if (value == JANUARY.getId()) {
            return JANUARY;
        } else if (value == FEBRUARY.getId()) {
            return FEBRUARY;
        } else if (value == MARCH.getId()) {
            return MARCH;
        } else if (value == APRIL.getId()) {
            return APRIL;
        } else if (value == MAY.getId()) {
            return MAY;
        } else if (value == JUNE.getId()) {
            return JUNE;
        } else if (value == JULY.getId()) {
            return JULY;
        } else if (value == AUGUST.getId()) {
            return AUGUST;
        } else if (value == SEPTEMBER.getId()) {
            return SEPTEMBER;
        } else if (value == OCTOBER.getId()) {
            return OCTOBER;
        } else if (value == NOVEMBER.getId()) {
            return NOVEMBER;
        } else if (value == DECEMBER.getId()) {
            return DECEMBER;
        }
        return null;
    }

}

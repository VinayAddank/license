package org.rta.citizen.noc.enums;
/**
 *	@Author sohan.maurya created on Dec 8, 2016.
 */
public enum IssueType {

    ISSUE_NOC(1, "NOC"), CANCELLATION_NOC(2, "CC");

    private IssueType(Integer value, String label) {

    }

    private Integer value;
    private String label;

    public String getLabel() {
        return label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static IssueType getIssueType(String label) {
        if (label == ISSUE_NOC.getLabel()) {
            return ISSUE_NOC;
        } else if (label == CANCELLATION_NOC.getLabel()) {
            return CANCELLATION_NOC;
        }
        return null;
    }

    public static IssueType getIssueType(Integer value) {
        if (value == ISSUE_NOC.getValue()) {
            return ISSUE_NOC;
        } else if (value == CANCELLATION_NOC.getValue()) {
            return CANCELLATION_NOC;
        }
        return null;
    }
}

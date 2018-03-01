package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @Author rahul.sharma
 */

public enum OwnershipType {

    INDIVIDUAL(1, "Individual", "indv"),  COMPANY(2, "Company", "comp"),  GOVERNMENT(3, "Government", "govt"),
    DIPLOMATIC_OFFICER(4, "Diplomatic Officer", "dptof"),  ORGANIZATION(5, "Organization","orgn"),
    POLICE(6, "Police", "police"),   STU_VEHICLES(7, "STU Vehicles", "stuveh");

    private int id;
    private String label;
    private String code;

    private OwnershipType() {}

    private OwnershipType(int id, String label, String code) {
        this.id = id;
        this.label = label;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static OwnershipType getOwnershipType(String code) {
        if (ObjectsUtil.isNull(code)) {
            return null;
        }
        if (code.equalsIgnoreCase(INDIVIDUAL.getCode())) {
            return INDIVIDUAL;
        } else if (code.equalsIgnoreCase(COMPANY.getCode())) {
            return COMPANY;
        } else if (code.equalsIgnoreCase(GOVERNMENT.getCode())) {
            return GOVERNMENT;
        } else if (code.equalsIgnoreCase(DIPLOMATIC_OFFICER.getCode())) {
            return DIPLOMATIC_OFFICER;
        } else if (code.equalsIgnoreCase(ORGANIZATION.getCode())) {
            return ORGANIZATION;
        } else if (code.equalsIgnoreCase(POLICE.getCode())) {
            return POLICE;
        } else if (code.equalsIgnoreCase(STU_VEHICLES.getCode())) {
            return STU_VEHICLES;
        }
        return null;
    }
    
    public static OwnershipType getOwnershipTypeByLabel(String label) {
        if (ObjectsUtil.isNull(label)) {
            return null;
        }
        if (label.equalsIgnoreCase(INDIVIDUAL.getLabel())) {
            return INDIVIDUAL;
        } else if (label.equalsIgnoreCase(COMPANY.getLabel())) {
            return COMPANY;
        } else if (label.equalsIgnoreCase(GOVERNMENT.getLabel())) {
            return GOVERNMENT;
        } else if (label.equalsIgnoreCase(DIPLOMATIC_OFFICER.getLabel())) {
            return DIPLOMATIC_OFFICER;
        } else if (label.equalsIgnoreCase(ORGANIZATION.getLabel())) {
            return ORGANIZATION;
        } else if (label.equalsIgnoreCase(POLICE.getLabel())) {
            return POLICE;
        } else if (label.equalsIgnoreCase(STU_VEHICLES.getLabel())) {
            return STU_VEHICLES;
        }
        return null;
    }

    public static OwnershipType getOwnershipType(Integer value) {
        if (ObjectsUtil.isNull(value)) {
            return null;
        }
        if (value == INDIVIDUAL.getId()) {
            return INDIVIDUAL;
        } else if (value == COMPANY.getId()) {
            return COMPANY;
        } else if (value == GOVERNMENT.getId()) {
            return GOVERNMENT;
        } else if (value == DIPLOMATIC_OFFICER.getId()) {
            return DIPLOMATIC_OFFICER;
        } else if (value == ORGANIZATION.getId()) {
            return ORGANIZATION;
        } else if (value == POLICE.getId()) {
            return POLICE;
        } else if (value == STU_VEHICLES.getId()) {
            return STU_VEHICLES;
        }
        return null;
    }

}

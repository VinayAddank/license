package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

public enum OwnershipTransferType {

    SALE(1, "SALE"), DEATH(2, "DEATH"), AUCTION(3, "AUCTION");
    
    private Integer value;
    private String label;
    
    private OwnershipTransferType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public static OwnershipTransferType getTransferType(int value) {
        if (value == SALE.getValue()) {
            return SALE;
        } else if (value == DEATH.getValue()) {
            return DEATH;
        } else if (value == AUCTION.getValue()) {
            return AUCTION;
        }
        return null;
    }
    
    public static OwnershipTransferType getTransferType(String label) {
        if (label.toUpperCase().equals(SALE.getValue())) {
            return SALE;
        } else if (label.toUpperCase().equals(DEATH.getValue())) {
            return DEATH;
        } else if (label.toUpperCase().equals(AUCTION.getValue())) {
            return AUCTION;
        }
        return null;
    }
    
    public static boolean isValidTransferType(int value) {
        if (ObjectsUtil.isNull(getTransferType(value))) {
            return false;
        }
        return true;
    }
    
    public static boolean isValidTransferType(String label) {
        if (ObjectsUtil.isNull(getTransferType(label))) {
            return false;
        }
        return true;
    }
    
}

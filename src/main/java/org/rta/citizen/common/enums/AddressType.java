package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.ObjectsUtil;


/**
 * this is describe Address came from Aadhar or noc
 * 
 * @Author sohan.maurya created on Aug 11, 2016.
 */
public enum AddressType {

    AADHAR(1, "AADHAR"), NOC(2, "NOC"), CURRENT_ADDRESS(3, "CURRENT ADDRESS");

    private static final Map<String, AddressType> labelToAddType = new HashMap<String, AddressType>();
    private static final Map<Integer, AddressType> valueToAddType = new HashMap<Integer, AddressType>();

    static {
        for (AddressType attachmentFrom : AddressType.values()) {
            labelToAddType.put(attachmentFrom.getLabel(), attachmentFrom);
        }
        for (AddressType attachmentFrom : EnumSet.allOf(AddressType.class)) {
            valueToAddType.put(attachmentFrom.getValue(), attachmentFrom);
        }
    }

    private String label;
    private Integer value;

    private AddressType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static AddressType getAddressType(String label) {
        return ObjectsUtil.isNull(label) ? null : labelToAddType.get(label.toUpperCase());
    }

    public static AddressType getAddressType(Integer value) {
        return valueToAddType.get(value);
    }

}

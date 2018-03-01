package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @Author sohan.maurya created on Aug 27, 2016.
 */
public enum VehicleCategory {

    FULLY_BUILD(0, "Fully Build", "fully_built"), CHASSIS_ONLY(1, "Chassis Only", "chassis_only");

    private static final Map<String, VehicleCategory> labelToVehicleCat =
            new HashMap<String, VehicleCategory>();
    private static final Map<Integer, VehicleCategory> valueToVehicleCat =
            new HashMap<Integer, VehicleCategory>();
    private static final Map<String, VehicleCategory> codeToVehicleCat =
            new HashMap<String, VehicleCategory>();

    static {

        for (VehicleCategory regCat : EnumSet.allOf(VehicleCategory.class)) {
            labelToVehicleCat.put(regCat.getLabel(), regCat);
        }
        for (VehicleCategory regCat : EnumSet.allOf(VehicleCategory.class)) {
            valueToVehicleCat.put(regCat.getValue(), regCat);
        }
        for (VehicleCategory regCat : EnumSet.allOf(VehicleCategory.class)) {
            codeToVehicleCat.put(regCat.getCode(), regCat);
        }
    }

    private Integer value;
    private String label;
    private String code;

    private VehicleCategory(Integer value, String label, String code) {
        this.value = value;
        this.label = label;
        this.code = code;
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

    public static VehicleCategory getVehicleCategory(String label) {
        return ObjectsUtil.isNull(label) ? null : labelToVehicleCat.get(label.toUpperCase());
    }

    public static VehicleCategory getVehicleCategory(Integer value) {
        return valueToVehicleCat.get(value);
    }
    
    public static VehicleCategory getVehicleCategoryByCode(String code) {
        return codeToVehicleCat.get(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

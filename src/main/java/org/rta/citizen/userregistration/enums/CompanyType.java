package org.rta.citizen.userregistration.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 *	@Author sohan.maurya created on Jan 2, 2017.
 */
public enum CompanyType {
    
    INDIVIDUAL_OWNED(1,"Individual Owned"), PARTNERSHIP(2,"Partnership"), PVT_LTD(3,"Pvt. LTD"), PUBLIC_LTD(4,"Public LTD");
    
    private Integer value;
    private String label;
    
    private CompanyType(Integer value, String label) {
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


    private static final Map<String, CompanyType> labelToCompany = new HashMap<String, CompanyType>();
    private static final Map<Integer, CompanyType> valueToCompany = new HashMap<Integer, CompanyType>();

    static {
        for (CompanyType companyType : CompanyType.values()) {
            labelToCompany.put(companyType.getLabel(), companyType);
        }
        for (CompanyType companyType : EnumSet.allOf(CompanyType.class)) {
            valueToCompany.put(companyType.getValue(), companyType);
        }
    }

    public static CompanyType getCompanyType(String label) {
        return ObjectsUtil.isNull(label) ? null : labelToCompany.get(label.toUpperCase());
    }

    public static CompanyType getCompanyType(Integer value) {
        return ObjectsUtil.isNull(value) ? null : valueToCompany.get(value);
    }
}

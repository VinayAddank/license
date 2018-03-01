/**
 * 
 */
package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @author arun.verma
 *
 */
public enum ServiceCategory {

    REG_CATEGORY(1, "Registration", "REG"), DL_CATEGORY(2, "DL", "DL"), LL_CATEGORY(3, "LL", "LL"), PERMIT_FITNESS_CATEGORY(4,"PERMIT/FITNESS","PER_FC"), DATA_ENTRY(5, "DE", "DE"),
    RC_AADHAR_SEEDING(6, "RC AADHAR SEEDING", "RCAS"), DL_AADHAR_SEEDING(7, "DL AADHAR SEEDING", "DLAS"), TAX_CATEGORY(8, "TAX", "TAX");

    private Integer id;
    private String text;
    private String code;

    ServiceCategory(Integer id, String text, String code) {
        this.id = id;
        this.text = text;
        this.code = code;
    }

    public static ServiceCategory getServiceTypeCat(String code) {
        if(ObjectsUtil.isNull(code)){
            return null;
        }
        
        if (code.toUpperCase().equals(REG_CATEGORY.getCode())) {
            return ServiceCategory.REG_CATEGORY;
        } else if (code.toUpperCase().equals(DL_CATEGORY.getCode())) {
            return ServiceCategory.DL_CATEGORY;
        } else if (code.toUpperCase().equals(LL_CATEGORY.getCode())) {
            return ServiceCategory.LL_CATEGORY;
        } else if (code.toUpperCase().equals(PERMIT_FITNESS_CATEGORY.getCode())) {
            return ServiceCategory.PERMIT_FITNESS_CATEGORY;
        } else if (code.toUpperCase().equals(DATA_ENTRY.getCode())) {
            return ServiceCategory.DATA_ENTRY;
        } else if (code.toUpperCase().equals(RC_AADHAR_SEEDING.getCode())) {
            return ServiceCategory.RC_AADHAR_SEEDING;
        } else if (code.toUpperCase().equals(DL_AADHAR_SEEDING.getCode())) {
            return ServiceCategory.DL_AADHAR_SEEDING;
        } else if (code.toUpperCase().equals(TAX_CATEGORY.getCode())) {
            return ServiceCategory.TAX_CATEGORY;
        }
        
        return null;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.getCode();
    }
    
}

package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

public enum SlotCategory {
    
    FITNESS_TESTS(1, "Fitness Test", "FT"), DL_CATEGORY(1, "DL", "DL"), LL_CATEGORY(1, "LL", "LL");

    private Integer id;
    private String text;
    private String code;

    private SlotCategory(Integer id, String text, String code) {
        this.id = id;
        this.text = text;
        this.code = code;
    }

    public static SlotCategory getServiceTypeCat(String code) {
        if(ObjectsUtil.isNull(code)){
            return null;
        }
        
        if (code.toUpperCase().equals(FITNESS_TESTS.getCode())) {
            return SlotCategory.FITNESS_TESTS;
        } else if (code.toUpperCase().equals(DL_CATEGORY.getCode())) {
            return SlotCategory.DL_CATEGORY;
        } else if (code.toUpperCase().equals(LL_CATEGORY.getCode())) {
            return SlotCategory.LL_CATEGORY;
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

package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.ObjectsUtil;

public enum PermitSubType {

    HOME_DISTRICT(1, "HD", "Home district as per RC"),  /*One District*/
    NEIGHBOURING_DISTRICT(2, "ND", "Neighbouring District"), /*Two District*/
    THROUGHOUT_STATE(3, "TS", "All Routes Except Prohibited In The State of Andhra Pradesh");

    private static final Map<String, PermitSubType> lookup = new HashMap<String, PermitSubType>();

    static{
        for (PermitSubType subtype : EnumSet.allOf(PermitSubType.class)) {
            lookup.put(subtype.code, subtype);
        }
    }
    
    private PermitSubType(Integer id, String code, String value) {
        this.id = id;
        this.code = code;
        this.value = value;
    }

    private Integer id;
    private String code;
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    PermitSubType getPermitSubType(String code){
        if(ObjectsUtil.isNull(code)){
            return null;
        }
        return lookup.get(code.toUpperCase());
    }
}
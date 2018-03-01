package org.rta.citizen.common.enums;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public enum TaxType {

	QUARTERLY_TAX(1, "Quarterly Tax" ,"FQ"), LIFE_TAX(2, "Life Tax" ,"LT"), GREEN_TAX(3, "Green Tax" , "GT"), HALFYEARLY_TAX(6, "HalfYearly Tax" , "HY"), ANNUAL_TAX(12, "Annual Tax" , "AT");

	private Integer value;
	private String label;
	private String code;
	
	 private TaxType(Integer value, String label  ,String code) {
	        this.value = value;
	        this.label = label;
	        this.code  = code;
	    }

	private static Map<Integer, String> map;

	static {
		Integer count = TaxType.values().length;
		map = new LinkedHashMap<>(count);
		for (TaxType taxType : TaxType.values()) {
			map.put(taxType.value, taxType.label);
		}
	}

	public Integer getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	 public String getCode() {
	        return code;
	 }
	
	public static TaxType getTaxType(String label) {
		if (!Objects.isNull(label)) {
			if (TaxType.LIFE_TAX.label.equals(label)) {
				return TaxType.LIFE_TAX;
			}
			if (TaxType.QUARTERLY_TAX.label.equals(label)) {
				return TaxType.QUARTERLY_TAX;
			}
			if (TaxType.GREEN_TAX.label.equals(label)) {
				return TaxType.GREEN_TAX;
			}
			if (TaxType.HALFYEARLY_TAX.label.equals(label)) {
				return TaxType.HALFYEARLY_TAX;
			}
			if (TaxType.ANNUAL_TAX.label.equals(label)) {
				return TaxType.ANNUAL_TAX;
			}
		}
		return null;
	}

	public static TaxType getTaxType(Integer value) {
		if (!Objects.isNull(value)) {
			if (TaxType.LIFE_TAX.value == value) {
				return TaxType.LIFE_TAX;
			}
			if (TaxType.QUARTERLY_TAX.value == value) {
				return TaxType.QUARTERLY_TAX;
			}
			if (TaxType.GREEN_TAX.value == value) {
				return TaxType.GREEN_TAX;
			}
			if (TaxType.HALFYEARLY_TAX.value == value) {
				return TaxType.HALFYEARLY_TAX;
			}
			if (TaxType.ANNUAL_TAX.value == value) {
				return TaxType.ANNUAL_TAX;
			}
		}
		return null;
	}
	
	public static TaxType getTaxTypeByCode(String code) {
        if (!Objects.isNull(code)) {
            if (TaxType.LIFE_TAX.code.equals(code)) {
                return TaxType.LIFE_TAX;
            }
            if (TaxType.QUARTERLY_TAX.code.equals(code)) {
                return TaxType.QUARTERLY_TAX;
            }
            if (TaxType.HALFYEARLY_TAX.label.equals(code)) {
                return TaxType.HALFYEARLY_TAX;
            }
            if (TaxType.ANNUAL_TAX.label.equals(code)) {
                return TaxType.ANNUAL_TAX;
            }
        }
        return null;
    }

	public static Map<Integer, String> getAll() {
		return map;
	}

}

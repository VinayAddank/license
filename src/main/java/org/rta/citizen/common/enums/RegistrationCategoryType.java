package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @Author sohan.maurya created on Aug 27, 2016.
 */
public enum RegistrationCategoryType {

	TRANSPORT(1, "Transport","T"), NON_TRANSPORT(2, "Non Transport","NT"), LICENSE(3, "LICENSE","LICENSE"), USERREG(4, "USERREG","USERREG");

	private static final Map<String, RegistrationCategoryType> labelToRegCat = new HashMap<String, RegistrationCategoryType>();
	private static final Map<Integer, RegistrationCategoryType> valueToRegCat = new HashMap<Integer, RegistrationCategoryType>();
	private static final Map<String, RegistrationCategoryType> labelToRegCode = new HashMap<String, RegistrationCategoryType>();

	static {

		for (RegistrationCategoryType regCat : EnumSet.allOf(RegistrationCategoryType.class)) {
			labelToRegCat.put(regCat.getLabel(), regCat);
		}
		for (RegistrationCategoryType regCat : EnumSet.allOf(RegistrationCategoryType.class)) {
			valueToRegCat.put(regCat.getValue(), regCat);
		}
		for (RegistrationCategoryType regCat : EnumSet.allOf(RegistrationCategoryType.class)) {
			labelToRegCat.put(regCat.getCode(), regCat);
		}
	}

	private Integer value;
	private String label;
	private String code;

	private RegistrationCategoryType(Integer value, String label, String code) {
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public static RegistrationCategoryType getRegistrationCategoryType(String label) {
		return ObjectsUtil.isNull(label) ? null : labelToRegCat.get(label.toUpperCase());
	}

	public static RegistrationCategoryType getRegistrationCategoryType(Integer value) {
		return valueToRegCat.get(value);
	}

	public static RegistrationCategoryType getRegistrationCategoryTypeByCode(String code) {
		return ObjectsUtil.isNull(code) ? null : labelToRegCode.get(code.toUpperCase());
	}
}

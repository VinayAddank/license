package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @Author sandeep.yadav
 */

public enum PermitClassType {

	PUKKA(1, "PUKKA"), TEMPORARY(2, "TEMPORARY");

	private Integer id;
	private String label;

	private PermitClassType() {
	}

	private PermitClassType(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static PermitClassType getPermitClassType(Integer value) {
		if (ObjectsUtil.isNull(value)) {
			return null;
		}
		if (value == PUKKA.getId()) {
			return PUKKA;
		} else if (value == TEMPORARY.getId()) {
			return TEMPORARY;
		}
		return null;
	}

}

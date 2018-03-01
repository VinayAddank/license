package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

/**
 * @Author sandeep.yadav
 */

public enum PermitDetailsType {

	DISTRICT(1, "DISTRICT"), STATE(2, "STATE"), CONDITION(3,
			"CONDITION"), ROUTE(4, "ROUTE"), GOODS(5, "GOODS");

	private Integer id;
	private String label;

	private PermitDetailsType() {
	}

	private PermitDetailsType(Integer id, String label) {
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

	public static PermitDetailsType getPermitDetailsType(Integer value) {
		if (ObjectsUtil.isNull(value)) {
			return null;
		}
		if (value == DISTRICT.getId()) {
			return DISTRICT;
		} else if (value == STATE.getId()) {
			return STATE;
		} else if (value == CONDITION.getId()) {
			return CONDITION;
		} else if (value == ROUTE.getId()) {
			return ROUTE;
		}else if (value == GOODS.getId()) {
			return GOODS;
		}
		return null;
	}

}

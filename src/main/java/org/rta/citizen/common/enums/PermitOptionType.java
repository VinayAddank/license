package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;

public enum PermitOptionType {
	TRANSFER(1, "Transfer"), SURRENDER(2, "Surrender");
	private int id;
	private String label;

	private PermitOptionType() {
	}

	private PermitOptionType(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static PermitOptionType getPermitOptionType(Integer value) {
		if (ObjectsUtil.isNull(value)) {
			return null;
		}
		if (value == TRANSFER.getId()) {
			return TRANSFER;
		} else if (value == SURRENDER.getId()) {
			return SURRENDER;
		}
		return null;
	}

	public static PermitOptionType getPermitOptionType(String value) {
		if (ObjectsUtil.isNull(value)) {
			return null;
		}
		if (TRANSFER.getLabel().toUpperCase().equals(value.toUpperCase())) {
			return TRANSFER;
		} else if (SURRENDER.getLabel().toUpperCase().equals(value.toUpperCase())) {
			return SURRENDER;
		}
		return null;
	}
}

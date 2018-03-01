package org.rta.citizen.licence.enums;

import java.util.HashMap;
import java.util.Map;

public enum LicenseType {

	LL("LL"), DL("DL");

	private static Map<String, LicenseType> labelType = new HashMap<>();
	private String label;

	private LicenseType(String label) {
		this.label = label;
	}

	static {
		for (LicenseType codeType : LicenseType.values()) {
			labelType.put(codeType.getLabel(), codeType);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static LicenseType getLicenseType(String label) {
		return labelType.get(label);
	}
}

package org.rta.citizen.licence.enums;

import java.util.HashMap;
import java.util.Map;

public enum MedicalFitnessType {

	SELF_CERTIFICATE("Self Certified"), DOCTOR_CERTIFICATE("Doctor Certified ");

	private static Map<String, MedicalFitnessType> labelType = new HashMap<>();
	private String label;

	private MedicalFitnessType(String label) {
		this.label = label;
	}

	static {
		for (MedicalFitnessType codeType : MedicalFitnessType.values()) {
			labelType.put(codeType.getLabel(), codeType);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static MedicalFitnessType getMedicalFitnessType(String label) {
		return labelType.get(label);
	}
}

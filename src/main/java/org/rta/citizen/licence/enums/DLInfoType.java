package org.rta.citizen.licence.enums;

public enum DLInfoType {

	MY_DL("MyDL"), OTHER_DL("OtherDL");

	private DLInfoType() {
	}

	private DLInfoType(String label) {

		this.label = label;
	}

	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

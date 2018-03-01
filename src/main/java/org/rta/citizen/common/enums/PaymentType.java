package org.rta.citizen.common.enums;


public enum PaymentType {

	ALTERATION_DT(2, "ALTERATION_DT"),TOW_BUYER(1, "TOW_BUYER"),PAY(0, "PAY");

	private Integer id;
	private String label;

	private PaymentType() {
	}

	private PaymentType(Integer id, String label) {
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

	public static PaymentType getPaymentType(Integer value) {
		if (value == null) {
			return null;
		}
		if (value == TOW_BUYER.getId()) {
			return TOW_BUYER;
		}else if (value == PAY.getId()) {
			return PAY;
		}else if (value == ALTERATION_DT.getId()) {
			return ALTERATION_DT;
		}
		
		return null;
	}

}

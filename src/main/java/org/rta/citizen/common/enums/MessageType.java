package org.rta.citizen.common.enums;

public enum MessageType {
	PAYMENT("Payment"), 
	TEST_RESULT_PASS("Test_Pass"),
	TEST_RESULT_FAIL("Test_Fail"), 
	AO_RTO_CCO_APPROVED("AO/RTO/CCO approved"),
	AO_RTO_CCO_REJECTED("AO/RTO/CCO rejected"), 
	TEST_RESULT_PASS_WITH_ATTACHEMENT("Test_Pass_with_attachement"),
	TEST_RESULT_FAIL_WITH_ATTACHEMENT("Test_Fail_attachement"), 
	DL_INFO("dl_info"),
	CCO_RASIE_SUSPENSION("cco_raise_suspension"),
	AO_APPROVED_SUSPENSION("approve_suspension"),
	AO_REJECT_SUSPENSION("reject_suspension"),
	AO_APPROVED_CANCELATION("approve_cancelation"), 
	AO_REJECT_CANCELATION("reject_cancelation");

	private String label;

	MessageType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}

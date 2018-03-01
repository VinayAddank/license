package org.rta.citizen.licence.model.updated;

import javax.validation.constraints.NotNull;

public class ExamResultModel {

	@NotNull
	private String applicationNo;
	@NotNull
	private String result;
	private Integer marks;
	private String testDate;
	private Integer testNo;
	private String systemIP;
	private Long applicationId;

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getMarks() {
		return marks;
	}

	public void setMarks(Integer marks) {
		this.marks = marks;
	}

	public String getTestDate() {
		return testDate;
	}

	public void setTestDate(String testDate) {
		this.testDate = testDate;
	}

	public Integer getTestNo() {
		return testNo;
	}

	public void setTestNo(Integer testNo) {
		this.testNo = testNo;
	}

	public String getSystemIP() {
		return systemIP;
	}

	public void setSystemIP(String systemIP) {
		this.systemIP = systemIP;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

}

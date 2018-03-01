package org.rta.citizen.licence.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class EndorseCOVModel {
	private String vechileCode;
	private String vechileDescription;
	private String llrEndorsementNo;
	private String applicationId;
	private List<String> llrVehicleClassCode;
	private Long aadharNumber; // used for parent consent in LLF while saving
								// COV
	private String uniqKey;
	private String llrNo;
	private Boolean isBadge;

	public String getUniqKey() {
		return uniqKey;
	}

	public void setUniqKey(String uniqKey) {
		this.uniqKey = uniqKey;
	}

	public String getLlrNo() {
		return llrNo;
	}

	public void setLlrNo(String llrNo) {
		this.llrNo = llrNo;
	}

	public Long getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(Long aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getVechileCode() {
		return vechileCode;
	}

	public void setVechileCode(String vechileCode) {
		this.vechileCode = vechileCode;
	}

	public String getVechileDescription() {
		return vechileDescription;
	}

	public void setVechileDescription(String vechileDescription) {
		this.vechileDescription = vechileDescription;
	}

	public String getLlrEndorsementNo() {
		return llrEndorsementNo;
	}

	public void setLlrEndorsementNo(String llrEndorsementNo) {
		this.llrEndorsementNo = llrEndorsementNo;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<String> getLlrVehicleClassCode() {
		return llrVehicleClassCode;
	}

	public void setLlrVehicleClassCode(List<String> llrVehicleClassCode) {
		this.llrVehicleClassCode = llrVehicleClassCode;
	}

	public Boolean getIsBadge() {
		return isBadge;
	}

	public void setIsBadge(Boolean isBadge) {
		this.isBadge = isBadge;
	}
}

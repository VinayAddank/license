package org.rta.citizen.licence.model.updated;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.BaseModel;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class QuestionnaireFeedbackModel extends BaseModel {

	private static final long serialVersionUID = 2171663106609278664L;

	private Long questionnaireFeedbackId;

	private String question;

	private String answer;

	private Boolean isCorrect;

	private Status status;

	private SlotServiceType testType;

	public Long getQuestionnaireFeedbackId() {
		return questionnaireFeedbackId;
	}

	public void setQuestionnaireFeedbackId(Long questionnaireFeedbackId) {
		this.questionnaireFeedbackId = questionnaireFeedbackId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public SlotServiceType getTestType() {
		return testType;
	}

	public void setTestType(SlotServiceType testType) {
		this.testType = testType;
	}

}

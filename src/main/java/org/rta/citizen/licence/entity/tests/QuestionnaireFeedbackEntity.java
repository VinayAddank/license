package org.rta.citizen.licence.entity.tests;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.BaseEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

@Entity
@Table(name = "questionnaire_feedback")
public class QuestionnaireFeedbackEntity extends BaseEntity {

	private static final long serialVersionUID = -94672464647811327L;

	@Id
	@Column(name = "questionnaire_feedback_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionnaire_feedback_seq")
	@SequenceGenerator(name = "questionnaire_feedback_seq", sequenceName = "questionnaire_feedback_seq", allocationSize = 1)
	private Long questionnaireFeedbackId;

	@Column(name = "question")
	private String question;

	@Column(name = "answer")
	private String answer;

	@Column(name = "is_correct")
	private Boolean isCorrect;

	@Column(name = "status")
	private Integer status;

	@Column(name = "test_type")
	@Enumerated(EnumType.STRING)
	private SlotServiceType testType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity application;

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public SlotServiceType getTestType() {
		return testType;
	}

	public void setTestType(SlotServiceType testType) {
		this.testType = testType;
	}

	public ApplicationEntity getApplication() {
		return application;
	}

	public void setApplication(ApplicationEntity application) {
		this.application = application;
	}

}

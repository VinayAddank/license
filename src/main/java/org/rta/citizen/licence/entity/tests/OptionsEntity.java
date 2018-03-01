package org.rta.citizen.licence.entity.tests;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;

@Entity
@Table(name = "question_options")
public class OptionsEntity extends BaseEntity {

	private static final long serialVersionUID = -7318570167075026608L;

	@Id
	@Column(name = "question_options_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_options_seq")
	@SequenceGenerator(name = "question_options_seq", sequenceName = "question_options_seq", allocationSize = 1)
	private Long questionOptionsId;

	@Column(name = "option")
	private String option;

	@Column(name = "is_correct")
	private Boolean isCorrect;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "question_id")
	private QuestionEntity question;

	@Column(name = "status")
	private Integer status;

	@Column(name = "is_mandatory", columnDefinition = "boolean default false")
	private boolean isMandatory;

	public Long getQuestionOptionsId() {
		return questionOptionsId;
	}

	public void setQuestionOptionsId(Long questionOptionsId) {
		this.questionOptionsId = questionOptionsId;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public QuestionEntity getQuestion() {
		return question;
	}

	public void setQuestion(QuestionEntity question) {
		this.question = question;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

}

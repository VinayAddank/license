package org.rta.citizen.licence.model.tests;

import java.util.List;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.BaseModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class QuestionModel extends BaseModel {

	private static final long serialVersionUID = -7309679827290292981L;

	private Long questionId;
	private String question;
	private Status status;
	private List<OptionModel> options;
	private OptionModel selectedOption;

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<OptionModel> getOptions() {
		return options;
	}

	public void setOptions(List<OptionModel> options) {
		this.options = options;
	}

	public OptionModel getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(OptionModel selectedOption) {
		this.selectedOption = selectedOption;
	}

}

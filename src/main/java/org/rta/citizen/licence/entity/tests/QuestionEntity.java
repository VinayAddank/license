package org.rta.citizen.licence.entity.tests;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

@Entity
@Table(name = "questions")
public class QuestionEntity extends BaseEntity {

	private static final long serialVersionUID = 3246226173500961084L;

	@Id
	@Column(name = "question_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questions_seq")
	@SequenceGenerator(name = "questions_seq", sequenceName = "questions_seq", allocationSize = 1)
	private Long questionId;

	@Column(name = "question")
	private String question;

	@Column(name = "vehicle_class")
	private String vehicleClass;

	@Column(name = "test_type")
	@Enumerated(EnumType.STRING)
	private SlotServiceType testType;

	@Column(name = "status")
	private Integer status;

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getVehicleClass() {
		return vehicleClass;
	}

	public void setVehicleClass(String vehicleClass) {
		this.vehicleClass = vehicleClass;
	}

	public SlotServiceType getTestType() {
		return testType;
	}

	public void setTestType(SlotServiceType testType) {
		this.testType = testType;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}

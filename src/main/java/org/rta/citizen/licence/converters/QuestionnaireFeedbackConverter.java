package org.rta.citizen.licence.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.entity.tests.QuestionnaireFeedbackEntity;
import org.rta.citizen.licence.model.updated.QuestionnaireFeedbackModel;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireFeedbackConverter {
	public QuestionnaireFeedbackModel convertToModel(QuestionnaireFeedbackEntity source) {
		if (ObjectsUtil.isNull(source)) {
			return null;
		}
		QuestionnaireFeedbackModel model = new QuestionnaireFeedbackModel();
		model.setCreatedBy(source.getCreatedBy());
		model.setCreatedOn(source.getCreatedOn());
		model.setModifiedBy(source.getModifiedBy());
		model.setModifiedOn(source.getModifiedOn());
		model.setQuestion(source.getQuestion());
		model.setStatus(Status.getStatus(source.getStatus()));
		model.setAnswer(source.getAnswer());
		model.setIsCorrect(source.getIsCorrect());
		model.setQuestionnaireFeedbackId(source.getQuestionnaireFeedbackId());
		model.setTestType(source.getTestType());
		return model;
	}

	public List<QuestionnaireFeedbackModel> convertToModelList(List<QuestionnaireFeedbackEntity> questionsEntity) {
		if (ObjectsUtil.isNull(questionsEntity)) {
			return null;
		}
		return questionsEntity.stream().map(s -> convertToModel(s)).collect(Collectors.toList());
	}
}

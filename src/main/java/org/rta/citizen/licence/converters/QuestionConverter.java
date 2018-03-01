package org.rta.citizen.licence.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.entity.tests.QuestionEntity;
import org.rta.citizen.licence.model.tests.QuestionModel;
import org.springframework.stereotype.Component;

@Component
public class QuestionConverter {

	public QuestionModel convertToModel(QuestionEntity source) {
		if (ObjectsUtil.isNull(source)) {
			return null;
		}
		QuestionModel model = new QuestionModel();
		model.setCreatedBy(source.getCreatedBy());
		model.setCreatedOn(source.getCreatedOn());
		model.setModifiedBy(source.getModifiedBy());
		model.setModifiedOn(source.getModifiedOn());
		model.setQuestion(source.getQuestion());
		model.setQuestionId(source.getQuestionId());
		model.setStatus(Status.getStatus(source.getStatus()));
		return model;
	}

	public List<QuestionModel> convertToModelList(List<QuestionEntity> questionsEntity) {
		if (ObjectsUtil.isNull(questionsEntity)) {
			return null;
		}
		return questionsEntity.stream().map(s -> convertToModel(s)).collect(Collectors.toList());
	}

}

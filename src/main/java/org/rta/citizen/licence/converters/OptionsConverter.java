package org.rta.citizen.licence.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.entity.tests.OptionsEntity;
import org.rta.citizen.licence.model.tests.OptionModel;
import org.springframework.stereotype.Component;

@Component
public class OptionsConverter {

	public OptionModel convertToModel(OptionsEntity source) {
		if (ObjectsUtil.isNull(source)) {
			return null;
		}
		OptionModel model = new OptionModel();
		model.setCreatedBy(source.getCreatedBy());
		model.setCreatedOn(source.getCreatedOn());
		model.setModifiedBy(source.getModifiedBy());
		model.setModifiedOn(source.getModifiedOn());
		model.setOption(source.getOption());
		model.setStatus(Status.getStatus(source.getStatus()));
		model.setOptionId(source.getQuestionOptionsId());
		model.setIsCorrect(source.getIsCorrect());
		model.setIsMandatory(source.isMandatory());
		return model;
	}

	public List<OptionModel> convertToModelList(List<OptionsEntity> questionsEntity) {
		if (ObjectsUtil.isNull(questionsEntity)) {
			return null;
		}
		return questionsEntity.stream().map(s -> convertToModel(s)).collect(Collectors.toList());
	}

}

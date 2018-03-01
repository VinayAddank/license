package org.rta.citizen.licence.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.entity.LlrAgeGroupRefEntity;
import org.rta.citizen.licence.model.LLRAgeGroupModel;
import org.springframework.stereotype.Component;

@Component
public class AgeGroupConverter {

	public LLRAgeGroupModel convertToModel(LlrAgeGroupRefEntity source) {
		if (ObjectsUtil.isNull(source)) {
			return null;
		}
		LLRAgeGroupModel model = new LLRAgeGroupModel();
		model.setAge_code(source.getAge_group_cd());
		model.setAge_description(source.getAgeGroupDesc());
		return model;
	}

	public List<LLRAgeGroupModel> convertToModelList(List<LlrAgeGroupRefEntity> ageGroups) {
		if (ObjectsUtil.isNull(ageGroups)) {
			return null;
		}
		return ageGroups.stream().map(s -> convertToModel(s)).collect(Collectors.toList());
	}

}

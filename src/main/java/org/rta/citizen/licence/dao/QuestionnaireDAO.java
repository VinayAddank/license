package org.rta.citizen.licence.dao;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.licence.entity.tests.OptionsEntity;
import org.rta.citizen.licence.entity.tests.QuestionEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface QuestionnaireDAO extends GenericDAO<QuestionEntity> {

	List<QuestionEntity> getQuestions(SlotServiceType type, Status status);

	List<OptionsEntity> getAnswers(Long questionId, Status status);

	OptionsEntity getOption(Long optionId, Status status);

}

package org.rta.citizen.licence.dao.updated;

import java.util.List;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.licence.entity.tests.QuestionnaireFeedbackEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;

public interface QuestionnaireFeedbackDAO extends GenericDAO<QuestionnaireFeedbackEntity> {

	void saveInBulk(List<QuestionnaireFeedbackEntity> questions);

	List<QuestionnaireFeedbackEntity> getQuestionnaire(SlotServiceType type, Long applicationId);

}

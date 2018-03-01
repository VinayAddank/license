package org.rta.citizen.licence.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.licence.dao.QuestionnaireDAO;
import org.rta.citizen.licence.entity.tests.OptionsEntity;
import org.rta.citizen.licence.entity.tests.QuestionEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionnaireDAOImpl extends BaseDAO<QuestionEntity> implements QuestionnaireDAO {

	public QuestionnaireDAOImpl() {
		super(QuestionEntity.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<QuestionEntity> getQuestions(SlotServiceType type, Status status) {
		Criteria criteria = getSession().createCriteria(QuestionEntity.class);
		criteria.add(Restrictions.eq("testType", type));
		criteria.add(Restrictions.eq("status", status.getValue()));
		return (List<QuestionEntity>) criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<OptionsEntity> getAnswers(Long questionId, Status status) {
		Criteria criteria = getSession().createCriteria(OptionsEntity.class);
		criteria.add(Restrictions.eq("question.questionId", questionId));
		criteria.add(Restrictions.eq("status", status.getValue()));
		return (List<OptionsEntity>) criteria.list();
	}

	@Override
	public OptionsEntity getOption(Long optionId, Status status) {
		Criteria criteria = getSession().createCriteria(OptionsEntity.class);
		criteria.add(Restrictions.eq("questionOptionsId", optionId));
		criteria.add(Restrictions.eq("status", status.getValue()));
		return (OptionsEntity) criteria.uniqueResult();
	}

}

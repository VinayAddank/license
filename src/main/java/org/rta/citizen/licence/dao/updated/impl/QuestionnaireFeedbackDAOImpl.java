package org.rta.citizen.licence.dao.updated.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.licence.dao.updated.QuestionnaireFeedbackDAO;
import org.rta.citizen.licence.entity.tests.QuestionnaireFeedbackEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionnaireFeedbackDAOImpl extends BaseDAO<QuestionnaireFeedbackEntity>
		implements QuestionnaireFeedbackDAO {

	public QuestionnaireFeedbackDAOImpl() {
		super(QuestionnaireFeedbackEntity.class);
	}

	@Override
	public void saveInBulk(List<QuestionnaireFeedbackEntity> questions) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		for (int i = 0; i < questions.size(); i++) {
			if (i % 7 == 0) {
				session.save(questions.get(i));
				session.flush();
				session.clear();
			}
		}
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QuestionnaireFeedbackEntity> getQuestionnaire(SlotServiceType type, Long applicationId) {
		Criteria criteria = getSession().createCriteria(QuestionnaireFeedbackEntity.class);
		criteria.add(Restrictions.eq("testType", type));
		criteria.add(Restrictions.eq("application.applicationId", applicationId));
		return (List<QuestionnaireFeedbackEntity>) criteria.list();
	}

}

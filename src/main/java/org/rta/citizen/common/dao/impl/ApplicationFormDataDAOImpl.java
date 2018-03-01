/**
 * 
 */
package org.rta.citizen.common.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author arun.verma
 *
 */

@Repository
public class ApplicationFormDataDAOImpl extends BaseDAO<ApplicationFormDataEntity> implements ApplicationFormDataDAO{

    public ApplicationFormDataDAOImpl() {
        super(ApplicationFormDataEntity.class);
    }

    @Override
    public ApplicationFormDataEntity getApplicationFormData(Long applicationId, String formCode) {
        Criteria criteria = getSession().createCriteria(ApplicationFormDataEntity.class);
        criteria.add(Restrictions.eq("applicationEntity.applicationId", applicationId));
        criteria.add(Restrictions.eq("formCode", formCode));
        return (ApplicationFormDataEntity) criteria.uniqueResult();
    }

	@Override
	public List<ApplicationFormDataEntity> getAllApplicationFormData(Long applicationId) {
		Criteria criteria = getSession().createCriteria(ApplicationFormDataEntity.class);
        criteria.add(Restrictions.eq("applicationEntity.applicationId", applicationId));
        return (List<ApplicationFormDataEntity>) criteria.list();
	}

	@Override
	public ApplicationFormDataEntity getApplicationFormData(Long applicationId) {
		Criteria criteria = getSession().createCriteria(ApplicationFormDataEntity.class);
        criteria.add(Restrictions.eq("applicationEntity.applicationId", applicationId));
        return (ApplicationFormDataEntity) criteria.uniqueResult();
    }
}

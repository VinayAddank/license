package org.rta.citizen.common.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.AttachmentDAO;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.entity.AttachmentEntity;
import org.springframework.stereotype.Repository;

/**
 *	@Author sohan.maurya created on Dec 12, 2016.
 */
@Repository("attachmentDAO")
public class AttachmentDAOImpl extends BaseDAO<AttachmentEntity> implements AttachmentDAO {

    public AttachmentDAOImpl() {
        super(AttachmentEntity.class);
    }

    @Override
    public AttachmentEntity getAttachmentDetails(Integer docTypeId, Long applicationId) {

        Criteria criteria = getSession().createCriteria(AttachmentEntity.class);
        criteria.add(Restrictions.eq("applicationId.applicationId", applicationId))
                .add(Restrictions.eq("docTypes.docTypeId", docTypeId));
        return (AttachmentEntity) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AttachmentEntity> getAttachmentDetails(Long applicationId) {

        Criteria criteria = getSession().createCriteria(AttachmentEntity.class);
        criteria.add(Restrictions.eq("applicationId.applicationId", applicationId));
        return criteria.list();
    }

}

package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.entity.AttachmentEntity;

/**
 *	@Author sohan.maurya created on Dec 12, 2016.
 */
public interface AttachmentDAO extends GenericDAO<AttachmentEntity> {

    public AttachmentEntity getAttachmentDetails(Integer docTypeId, Long applicationId);

    public List<AttachmentEntity> getAttachmentDetails(Long applicationId);
}

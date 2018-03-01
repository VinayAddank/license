package org.rta.citizen.common.converters;

import java.util.Collection;
import java.util.stream.Collectors;

import org.rta.citizen.common.entity.AttachmentEntity;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

/**
 *	@Author sohan.maurya created on Dec 12, 2016.
 */

@Component
public class AttachmentConverter implements BaseConverter<AttachmentEntity, AttachmentModel> {

    @Override
    public AttachmentModel convertToModel(AttachmentEntity source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        AttachmentModel model = new AttachmentModel();
        model.setAttachmentTitle(source.getAttachmentTitle());
        model.setAttachmentFrom(source.getAttachmentFrom());
        model.setFileName(source.getFileName());
        model.setSource(source.getSource());
        model.setStatus(source.getStatus());
        model.setId(source.getDocTypes().getDocTypeId());
        model.setUserType(source.getUserType());
        return model;
    }

    @Override
    public AttachmentEntity convertToEntity(AttachmentModel source) {
        
        if(ObjectsUtil.isNull(source)){
            return null;
        }
        AttachmentEntity entity = new AttachmentEntity();
        entity.setAttachmentTitle(source.getAttachmentTitle());
        entity.setAttachmentFrom(source.getAttachmentFrom());
        entity.setFileName(source.getFileName());
        entity.setSource(source.getSource());
        entity.setStatus(Status.PENDING);
        entity.setUserType(source.getUserType());
        return entity;
    }

    @Override
    public Collection<AttachmentModel> convertToModelList(Collection<AttachmentEntity> source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        return source.stream().map(s -> convertToModel(s)).collect(Collectors.toList());
    }

    @Override
    public Collection<AttachmentEntity> convertToEntityList(Collection<AttachmentModel> source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        return source.stream().map(s -> convertToEntity(s)).collect(Collectors.toList());
    }

}

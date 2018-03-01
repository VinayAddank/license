package org.rta.citizen.common.service;

import java.util.List;

import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AttachmentModel;
import org.rta.citizen.common.model.DocTypesModel;
import org.rta.citizen.common.model.ResponseModel;

/**
 *	@Author sohan.maurya created on Dec 12, 2016.
 */
public interface AttachmentService {

    public List<DocTypesModel> getAttachments(Long sessionId, String applicationNo, UserType userType) throws NotFoundException, UnauthorizedException;

    public ResponseModel<Object> saveOrUpdate(AttachmentModel model, String userName) throws DataMismatchException;

    public List<AttachmentModel> getAttachmentDetails(Long sessionId, String applicationNo, UserType userType) throws NotFoundException;

    public ResponseModel<Object> saveOrUpdateMultiple(List<AttachmentModel> models, String userName) throws DataMismatchException;

	public ResponseModel<String> saveOrUpdateAttachments(Long applicationId, String aadhaarNo);

	public Integer getCustomerAge(String aadhaarNo);

}

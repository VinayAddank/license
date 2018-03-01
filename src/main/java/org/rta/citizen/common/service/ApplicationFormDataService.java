/**
 * 
 */
package org.rta.citizen.common.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.exception.ConflictException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.ServiceValidationException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author arun.verma
 *
 */
public interface ApplicationFormDataService {

    public ResponseModel<ApplicationFormDataModel> saveForm(List<ApplicationFormDataModel> forms, Long sessionId, Long userId) throws UnauthorizedException, JsonMappingException, IOException, DataMismatchException, NotFoundException, ConflictException, ServiceValidationException;
   
    public ResponseModel<ApplicationFormDataModel> getApplicationFormData(Long applicationId, String formId);
    
    public ResponseModel<ApplicationFormDataModel> getApplicationFormData(String applicationNumber, String formId) throws JsonProcessingException, IOException, UnauthorizedException;

    public ResponseModel<ApplicationFormDataModel> getApplicationFormDataBySessionId(Long sessionId, String formId) throws JsonProcessingException, IOException, UnauthorizedException;

    public List<RtaTaskInfo> completeFormDataActiviti(Long sessionId, String taskDef, String userName) throws UnauthorizedException;

	public ResponseModel<Map<String, Object>> getAllForms(String appNo) throws JsonProcessingException, IOException, NumberFormatException, UnauthorizedException;

	public ResponseModel<ApplicationFormDataModel> saveUpdateForm(ApplicationFormDataModel applicationFormDataModel, String applicationNumber) throws JsonMappingException, IOException, DataMismatchException, NotFoundException, ConflictException;

	public Integer getRegistrationCategory(Long applicationId, ServiceType serviceType);

	public Boolean aadhaarSeedingAvailability(Long sessionId);

	
}

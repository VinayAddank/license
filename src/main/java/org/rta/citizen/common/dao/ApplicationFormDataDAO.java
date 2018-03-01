/**
 * 
 */
package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.entity.ApplicationFormDataEntity;

/**
 * @author arun.verma
 *
 */
public interface ApplicationFormDataDAO extends GenericDAO<ApplicationFormDataEntity>{

    public ApplicationFormDataEntity getApplicationFormData(Long applicationId, String formCode);
    
    public ApplicationFormDataEntity getApplicationFormData(Long applicationId);
    
    public List<ApplicationFormDataEntity> getAllApplicationFormData(Long appId);
}

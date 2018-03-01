/**
 * 
 */
package org.rta.citizen.ownershiptransfer.dao;

import org.rta.citizen.common.dao.GenericDAO;
import org.rta.citizen.ownershiptransfer.entity.OTTokenEntity;

/**
 * @author arun.verma
 *
 */
public interface OTTokenDAO extends GenericDAO<OTTokenEntity>{
    
    public OTTokenEntity getTokenEntity(Long applicationId);
    
    public OTTokenEntity getTokenEntity(String tokenNumber);
}

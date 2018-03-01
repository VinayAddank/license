/**
 * 
 */
package org.rta.citizen.fitness.service;

import java.util.Map;

import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.fitness.cfx.model.CFXNoticeModel;

/**
 * @author arun.verma
 *
 */
public interface FitnessService {

    public ResponseModel<Map<String, Object>> getFitnessCertificate(String appNo) throws UnauthorizedException;

    CFXNoticeModel getFitnessCFXNote(String appNo);

}

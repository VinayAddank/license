/**
 * 
 */
package org.rta.citizen.common.service;

import java.io.IOException;

import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.exception.VehicleNotFinanced;
import org.rta.citizen.common.model.ApplicationStatusModel;
import org.rta.citizen.common.model.ResponseModel;

import javassist.NotFoundException;

/**
 * @author arun.verma
 *
 */
public interface AppSearchService {

    public ResponseModel<Object> getSearchApplication(String appNo, Long sessionId) throws UnauthorizedException, VehicleNotFinanced;

    public ResponseModel<ApplicationStatusModel> getApplicationStatus(String appNo, Long sessionId) throws UnauthorizedException, NotFoundException, IOException, VehicleNotFinanced;
}

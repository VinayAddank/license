/**
 * 
 */
package org.rta.citizen.permit.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.PermitDetailsModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.permit.model.PermitAuthorizationCardModel;
import org.rta.citizen.permit.model.PermitTempPermitModel;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author arun.verma
 *
 */
public interface PermitService {

    public ResponseModel<PermitTempPermitModel> getTempPermits(Long sessionId) throws UnauthorizedException;

    public ResponseModel<Map<String, Object>> getSelectedPukkaTempPermit(String prNumber) throws UnauthorizedException;

    public ResponseModel<Map<String, Object>> getPermitCertificate(String appNo) throws UnauthorizedException, JsonProcessingException, IOException;

    public ResponseModel<PermitDetailsModel> getPermitCertificateByPr(String prNumber, Long sessionId) throws UnauthorizedException, JsonProcessingException, IOException;

    ResponseModel<PermitAuthorizationCardModel> getPermitAuthCardDetails(String applicationNumber)
    
            throws UnauthorizedException;
    public FeeModel getpermitFeesDetails(String ApplicationNumber) throws NotFoundException;

    ResponseModel<Map<String, Object>> getSelectedPukkaTempPermit(String prNumber, Long sessionId)
            throws UnauthorizedException;

	PermitDetailsModel getPermitDetails(String prNumber, String permitType) throws UnauthorizedException;

	public List<PermitHeaderModel> getAllPermits(Long sessionId) throws UnauthorizedException;
}
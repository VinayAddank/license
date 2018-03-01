/**
 * 
 */
package org.rta.citizen.ownershiptransfer.service;

import java.util.List;

import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ApplicationNotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.ReceiptModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SellerAuthModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.ownershiptransfer.model.OtPermitModel;

/**
 * @author arun.verma
 *
 */
public interface OTService {

    public ReceiptModel generateToken(Long sessionId, String clientIp)  throws ApplicationNotFoundException;

    public CitizenApplicationModel getCitizenAppByOTSToken(String token);
    
    public OtPermitModel getPermitOptions(Long sessionId, Integer buyerMandalCode) throws ApplicationNotFoundException;

    public ResponseModel<String> saveOrUpdateOwnershipTransfer(Long vehicle, Long applicationId, String prNumber,
            ServiceType service, String aadharNumber);

	public ResponseModel<List<RtaTaskInfo>> approveBuyer(String appNo, SellerAuthModel applicant, Status status) throws UnauthorizedException;

    ReceiptModel getTokenReceipt(Long sessionId) throws ApplicationNotFoundException;
}

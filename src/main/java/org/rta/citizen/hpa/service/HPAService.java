/**
 * 
 */
package org.rta.citizen.hpa.service;

import java.util.List;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.ApplicationNotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CommentModel;
import org.rta.citizen.common.model.FinancerModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;

/**
 * @author arun.verma
 *
 */
public interface HPAService {

    public ResponseModel<List<RtaTaskInfo>> approveFinancier(String appNo, Long sessionId, FinancerModel financier) throws UnauthorizedException;
    
    public ResponseModel<List<RtaTaskInfo>> submitFinanceDetails(String appNo, Long sessionId, String userName, Long appId, Long userId, String userRole, Status status, Integer iteration, CommentModel comment) throws ApplicationNotFoundException;
    
}

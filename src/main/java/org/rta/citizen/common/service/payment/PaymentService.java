/**
 * 
 */
package org.rta.citizen.common.service.payment;

import java.util.List;
import java.util.Map;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.PaymentGatewayType;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.payment.TransactionDetailModel;

public interface PaymentService {

	public TransactionDetailModel createPaymentRequest(long sessionId , String appNo , PaymentGatewayType paymentGatewayType);

	
	public ResponseModel<String> processPaymentResponse(TransactionDetailModel transactionDetailModel,long sessionId);
	
	public TransactionDetailModel paymentVerificationReq(long sessionId , String appNo);
	public ResponseModel<String> payUPaymentVerificationReq(long sessionId , String appNo);
	
	public ResponseModel<String> processPaymentVerifyResponse(TransactionDetailModel transactionDetailModel,Long sessionId);

    public List<RtaTaskInfo> callAfterPaymentSuccess(Long sessionId, String taskDef, String userName);


    public void beforeActivitiTask(ApplicationEntity appEntity, Long sessionId, Map<String, Object> variableMap,
            Map<String, Object> otherDataMap);


    public Boolean isPayDiffTaxForVA(Long sessionId);

}

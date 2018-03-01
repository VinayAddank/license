package org.rta.citizen.common.service.payment;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.CitizenInvoiceEntity;
import org.rta.citizen.common.enums.PaymentGatewayType;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.TransactionDetailModel;

public interface TransactionDetailsService {

	public TransactionDetailModel createBankTransaction(ApplicationEntity applicationEntity,
			CitizenInvoiceEntity citizenInvoiceEntity , ApplicationTaxModel appTaxModel , PaymentGatewayType paymentGatewayType);

	public ResponseModel<String> updatePaymentProcess(TransactionDetailModel transactionDetailModel , ApplicationEntity appEntity);
	
	public ResponseModel<String> updatePayUPaymentProcess(TransactionDetailModel transactionDetailModel , ApplicationEntity appEntity);
	
	public void updatePayUFaliureResponse(String txnNumber , ApplicationEntity applicationEntity);
}

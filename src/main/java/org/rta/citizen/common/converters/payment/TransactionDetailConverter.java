package org.rta.citizen.common.converters.payment;

import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.model.payment.TransactionDetailModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

@Component
public class TransactionDetailConverter {

	 public TransactionDetailModel convertToModel(TransactionDetailEntity entity) {
	        if (ObjectsUtil.isNull(entity)) {
	            return null;
	        }
	        TransactionDetailModel transactionDetailModel = new TransactionDetailModel();
	        transactionDetailModel.setFeeAmt(entity.getFeeAmount());
	        transactionDetailModel.setPermitAmt(entity.getPermitAmount());
	        transactionDetailModel.setPostalCharge(entity.getPostalCharge());
	        transactionDetailModel.setServiceCharge(entity.getServiceCharge());
	        transactionDetailModel.setTaxAmt(entity.getTaxAmount());
	        transactionDetailModel.setTransactionId(entity.getTransactionId());
	        transactionDetailModel.setTransactionNo(entity.getBankTransacNo());
	        transactionDetailModel.setAmount(entity.getPayAmount());
	        transactionDetailModel.setGreenTaxAmt(entity.getGreenTaxAmt());
	        transactionDetailModel.setCompoundAmount(entity.getCompoundAmount());
	        transactionDetailModel.setCessFee(entity.getCessFee());
	        if(entity.getPgType() != null)
	        transactionDetailModel.setPgType(entity.getPgType());
	        return transactionDetailModel;
	       
	    }
}

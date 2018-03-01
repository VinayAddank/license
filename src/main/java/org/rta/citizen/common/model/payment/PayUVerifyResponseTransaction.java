package org.rta.citizen.common.model.payment;

public class PayUVerifyResponseTransaction {

	private String merchantTransactionId;
	private PayUTransactionDetails postBackParam;

	public String getMerchantTransactionId() {
		return merchantTransactionId;
	}

	public void setMerchantTransactionId(String merchantTransactionId) {
		this.merchantTransactionId = merchantTransactionId;
	}

	public PayUTransactionDetails getPostBackParam() {
		return postBackParam;
	}

	public void setPostBackParam(PayUTransactionDetails postBackParam) {
		this.postBackParam = postBackParam;
	}

}

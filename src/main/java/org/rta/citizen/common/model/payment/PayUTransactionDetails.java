package org.rta.citizen.common.model.payment;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PayUTransactionDetails {

	private String key;
	private String mihpayid;
	private String status;
	private String bank_ref_num;
	private String request_id;
	private String amt;
	private String amount;
	private String cardCategory;
	private String discount;
	private String transaction_amount;
	private String txnid;
	private String additional_charges;
	private String productinfo;
	private String firstname;
	private String lastname;
	private String address1;
	private String address2;
	private String city;
	private String email;
	private String phone;
	private String state;
	private String country;
	private String zipcode;
	private String bankcode;
	private String udf1;
	private String udf2;
	private String udf3;
	private String udf4;
	private String udf5;
	private String udf6;
	private String udf7;
	private String udf8;
	private String udf9;
	private String udf10;
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	private String field5;
	private String field6;
	private String field7;
	private String field8;
	private String field9;
	private String card_type;
	private String error;
	private String error_code;
	private String error_Message;
	private String net_amount_debit;
	private String disc;
	private String mode;

	@JsonProperty("PG_TYPE")
	private String pgTYPE;
	private String card_no;
	private String name_on_card;
	private String addedon;
	private String unmappedstatus;
	private String Merchant_UTR;
	private String Settled_At;
	private String payment_source;
	private String cardnum;
	private String cardhash;
	private String hash;
	
	private Map<String, String> properties = new HashMap<>();
	private String additionalCharges;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMihpayid() {
		return mihpayid;
	}

	public void setMihpayid(String mihpayid) {
		this.mihpayid = mihpayid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBank_ref_num() {
		return bank_ref_num;
	}

	public void setBank_ref_num(String bank_ref_num) {
		this.bank_ref_num = bank_ref_num;
	}

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCardCategory() {
		return cardCategory;
	}

	public void setCardCategory(String cardCategory) {
		this.cardCategory = cardCategory;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getTransaction_amount() {
		return transaction_amount;
	}

	public void setTransaction_amount(String transaction_amount) {
		this.transaction_amount = transaction_amount;
	}

	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	public String getAdditional_charges() {
		return additional_charges;
	}

	public void setAdditional_charges(String additional_charges) {
		this.additional_charges = additional_charges;
	}

	public String getProductinfo() {
		return productinfo;
	}

	public void setProductinfo(String productinfo) {
		this.productinfo = productinfo;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public String getUdf1() {
		return udf1;
	}

	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}

	public String getUdf2() {
		return udf2;
	}

	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}

	public String getUdf3() {
		return udf3;
	}

	public void setUdf3(String udf3) {
		this.udf3 = udf3;
	}

	public String getUdf4() {
		return udf4;
	}

	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}

	public String getUdf5() {
		return udf5;
	}

	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}

	public String getUdf6() {
		return udf6;
	}

	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}

	public String getUdf7() {
		return udf7;
	}

	public void setUdf7(String udf7) {
		this.udf7 = udf7;
	}

	public String getUdf8() {
		return udf8;
	}

	public void setUdf8(String udf8) {
		this.udf8 = udf8;
	}

	public String getUdf9() {
		return udf9;
	}

	public void setUdf9(String udf9) {
		this.udf9 = udf9;
	}

	public String getUdf10() {
		return udf10;
	}

	public void setUdf10(String udf10) {
		this.udf10 = udf10;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getError_Message() {
		return error_Message;
	}

	public void setError_Message(String error_Message) {
		this.error_Message = error_Message;
	}

	public String getNet_amount_debit() {
		return net_amount_debit;
	}

	public void setNet_amount_debit(String net_amount_debit) {
		this.net_amount_debit = net_amount_debit;
	}

	public String getDisc() {
		return disc;
	}

	public void setDisc(String disc) {
		this.disc = disc;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPgTYPE() {
		return pgTYPE;
	}

	public void setPgTYPE(String pgTYPE) {
		this.pgTYPE = pgTYPE;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getName_on_card() {
		return name_on_card;
	}

	public void setName_on_card(String name_on_card) {
		this.name_on_card = name_on_card;
	}

	public String getAddedon() {
		return addedon;
	}

	public void setAddedon(String addedon) {
		this.addedon = addedon;
	}

	public String getUnmappedstatus() {
		return unmappedstatus;
	}

	public void setUnmappedstatus(String unmappedstatus) {
		this.unmappedstatus = unmappedstatus;
	}

	public String getMerchant_UTR() {
		return Merchant_UTR;
	}

	public void setMerchant_UTR(String merchant_UTR) {
		Merchant_UTR = merchant_UTR;
	}

	public String getSettled_At() {
		return Settled_At;
	}

	public void setSettled_At(String settled_At) {
		Settled_At = settled_At;
	}

	public String getPayment_source() {
		return payment_source;
	}

	public void setPayment_source(String payment_source) {
		this.payment_source = payment_source;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public String getCardhash() {
		return cardhash;
	}

	public void setCardhash(String cardhash) {
		this.cardhash = cardhash;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	@JsonAnySetter
    public void set(String fieldName, String value){
        this.properties.put(fieldName, value);
    }

	@JsonAnyGetter
    public String get(String fieldName){
        return this.properties.get(fieldName);
    }

	public String getAdditionalCharges() {
		return additionalCharges;
	}

	public void setAdditionalCharges(String additionalCharges) {
		this.additionalCharges = additionalCharges;
	}
}

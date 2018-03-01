package org.rta.citizen.common.model.communication;

import java.util.List;

import org.rta.MessageConfig;

public class CustMsgModel {

	String vehicleRcId;
	int ownershipType;
	String custName;
	private String smsMsg;
	private String mobileNo; 
	private String subject;
	private String to;
	private String cc;
	private String bcc;
	private String mailContent;
	private String citizenName;
	private List<String> attachments;
	private MessageConfig communicationConfig;
	
	public String getVehicleRcId() {
		return vehicleRcId;
	}
	public void setVehicleRcId(String vehicleRcId) {
		this.vehicleRcId = vehicleRcId;
	}
	public int getOwnershipType() {
		return ownershipType;
	}
	public void setOwnershipType(int ownershipType) {
		this.ownershipType = ownershipType;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getSmsMsg() {
		return smsMsg;
	}
	public void setSmsMsg(String smsMsg) {
		this.smsMsg = smsMsg;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public String getMailContent() {
		return mailContent;
	}
	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}
	public List<String> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
	public MessageConfig getCommunicationConfig() {
		return communicationConfig;
	}
	public void setCommunicationConfig(MessageConfig communicationConfig) {
		this.communicationConfig = communicationConfig;
	}
	public String getCitizenName() {
		return citizenName;
	}
	public void setCitizenName(String citizenName) {
		this.citizenName = citizenName;
	}
	
	
	
	
	

}

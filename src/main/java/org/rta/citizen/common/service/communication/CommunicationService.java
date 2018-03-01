package org.rta.citizen.common.service.communication;

import org.rta.MessageConfig;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.communication.CustMsgModel;

public interface CommunicationService {

	public String sendSms(CustMsgModel customerModel);

	public String sendEmail(CustMsgModel customerModel);

	public boolean sendMsg(int msgMode, CustMsgModel customerModel) ;
	
	public CustMsgModel getCustInfo(Status status , ApplicationEntity appEntity , Boolean isNewCitizen ,String formCodeType);
	
	public CustMsgModel getCustInfoForSellerToBuyer (ApplicationEntity appEntity);
	
	public boolean sendMsg(int msgMode, CustMsgModel customerModel,FinanceModel financeModel,Boolean isAppliedHpa,String serviceCode) ;
	
	public String sendSms(FinanceModel financeModel,CustMsgModel custMsgModel);
	public String sendEmail(FinanceModel financeModel,CustMsgModel custMsgModel);

    MessageConfig getCommunicationConfig();
    public CustMsgModel getLicenceCustInfo(Status status, ApplicationEntity appEntity, String activityType);
    
    //test
    public CustMsgModel checkCommunication(String applicationNumber);

}

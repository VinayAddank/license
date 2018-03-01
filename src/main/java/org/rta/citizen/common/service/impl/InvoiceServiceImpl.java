package org.rta.citizen.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.converters.UserSessionConverter;
import org.rta.citizen.common.converters.payment.FeeDetailConverter;
import org.rta.citizen.common.converters.payment.TaxDetailConverter;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.EventDAO;
import org.rta.citizen.common.dao.payment.FeeDetailDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.EventEntity;
import org.rta.citizen.common.entity.payment.FeeDetailEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.PaymentType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.AddressModel;
import org.rta.citizen.common.model.InvoiceModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.InvoiceService;
import org.rta.citizen.common.service.payment.impl.TaxFeeServiceImpl;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.NumberParser;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.model.updated.DLMilataryDetailsModel;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.ownershiptransfer.dao.OTTokenDAO;
import org.rta.citizen.ownershiptransfer.entity.OTTokenEntity;
import org.rta.citizen.slotbooking.exception.SlotUnavailableException;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InvoiceServiceImpl implements InvoiceService{

	private static final Logger log = Logger.getLogger(TaxFeeServiceImpl.class);

	@Autowired
	private ApplicationDAO applicationDAO;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private FeeDetailDAO feeDetailDAO;

	@Autowired
	private TaxDetailDAO taxDetailDAO;

	@Autowired
	private FeeDetailConverter feeDetailConverter;

	@Autowired
	private TransactionDetailDAO transactionDetailDAO;

	@Autowired
	private TaxDetailConverter taxDetailConverter;

	@Autowired
	private SlotService slotService;

	@Autowired
	private UserSessionConverter userSessionConverter;

	@Autowired
	private OTTokenDAO oTTokenDAO;

	@Autowired
	private EventDAO eventDAO;

	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;
	@Autowired
	private ActivitiService  activitiService;

	@Override
	@Transactional
	public InvoiceModel getInvoiceDetails(long sessionId , String appNo) {
		log.info("::CITIZEN::::getInvoiceDetails:::::appNo " + appNo + " sessionId "+ sessionId );
		ApplicationEntity appEntity = null;
		if(appNo != null)
			appEntity = applicationDAO.getApplication(appNo);
		else
			appEntity = applicationDAO.getApplicationFromSession(sessionId);
		if(ObjectsUtil.isNull(appEntity)){
			return null;
		}
		TransactionDetailEntity transactionDetailEntity = new TransactionDetailEntity();;
		transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(appEntity);
		if(transactionDetailEntity != null)
			throw new IllegalArgumentException("Payment is Pending !");
		log.info("::CITIZEN::::getInvoiceDetails:::::ServiceCode " + appEntity.getServiceCode());
		RegistrationServiceResponseModel<ApplicationTaxModel> result = null;
		try {
			result = registrationService.getTaxDetails(appEntity.getLoginHistory().getUniqueKey());
		} catch (RestClientException e) {
			log.error("error when getting tr details : " + e);
		}	
		if (ObjectsUtil.isNull(result)) {
			log.info("tr details not found for tr number : " + appEntity.getLoginHistory().getUniqueKey());
			return null;
		}
		if (result.getHttpStatus() != HttpStatus.OK) {
			log.info("error in http request " + result.getHttpStatus());
			return null;
		}
		ApplicationTaxModel appTaxModel = result.getResponseBody();
		InvoiceModel invoiceModel = new InvoiceModel();
		List<SlotModel> slots = new ArrayList<SlotModel>();
		boolean isBuyer = false;
		boolean isAlteration =false;
        ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(appEntity.getExecutionId());
        switch(ServiceType.getServiceType(appEntity.getServiceCode())){
		case VEHICLE_ATLERATION:
			  if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			   for(RtaTaskInfo task : actRes.getActiveTasks()){
				     if (task.getTaskDefKey().equalsIgnoreCase("dt_tax"))
			            	isAlteration = true;
			   }
			  }
			break;
        }
        List<RtaTaskInfo> tasks = actRes.getActiveTasks();
        for (RtaTaskInfo task : tasks) {
            if (task.getTaskDefKey().equalsIgnoreCase("payment_buyer"))
                isBuyer = true;
        }
        if(isBuyer)
			transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(appEntity , PaymentType.TOW_BUYER);
        else if(isAlteration)
			transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(appEntity , PaymentType.ALTERATION_DT);
		else
			transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(appEntity , PaymentType.PAY);
		if(appTaxModel != null && appTaxModel.getCitizenName() != null && appEntity.getLoginHistory().getAadharNumber() != null){
			invoiceModel.setApplicantName(appTaxModel.getCitizenName());
			invoiceModel.setAdharNo(appEntity.getLoginHistory().getAadharNumber());
		}else{
			log.info("::Invoice Detail Not found(Exclude OTS Service)::::sessionId:: " + sessionId + " AppNo "+appNo);
			invoiceModel.setApplicantName("");
			invoiceModel.setAdharNo("");
		}
		if(transactionDetailEntity != null){
			if(transactionDetailEntity.getPaymentType() == PaymentType.TOW_BUYER.getId()){
				AadharModel aadharModel = new AadharModel();
				try {
					OTTokenEntity otTokenEntity = oTTokenDAO.getTokenEntity(appEntity.getApplicationId()); 
					invoiceModel.setAdharNo(otTokenEntity.getClaimantAadhaarNumber());
					aadharModel =  registrationService.getAadharDetails(Long.valueOf(otTokenEntity.getClaimantAadhaarNumber())).getResponseBody();
					if(!ObjectsUtil.isNull(aadharModel)){
						invoiceModel.setApplicantName(aadharModel.getName());
					} else {
						log.error("Aadhar Details Not Found for aadhar No: " + appEntity.getLoginHistory().getAadharNumber());
					}
					log.info("::invoice for buyer:::seller buyer: adhar "+otTokenEntity.getClaimantAadhaarNumber() +" name "+ aadharModel.getName());
				} catch (NumberFormatException | UnauthorizedException e1) {
					e1.printStackTrace();
					throw new IllegalArgumentException("Internal Server Error getInvoiceDLDetails !");
				}

			}
			if(transactionDetailEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_DEATH.getCode()) || transactionDetailEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_AUCTION.getCode())
					|| transactionDetailEntity.getServiceCode().equalsIgnoreCase(ServiceType.FRESH_RC_FINANCIER.getCode())){
				AadharModel aadharModel = new AadharModel();
				try {
					invoiceModel.setAdharNo(appEntity.getLoginHistory().getAadharNumber());
					aadharModel =  registrationService.getAadharDetails(Long.valueOf(appEntity.getLoginHistory().getAadharNumber())).getResponseBody();
					if(!ObjectsUtil.isNull(aadharModel)){
						invoiceModel.setApplicantName(aadharModel.getName());
						log.info("::invoice for buyer:::Death or Auction buyer: adhar "+invoiceModel.getAdharNo() +" name "+ aadharModel.getName());
					} else {
						log.error("Aadhar Details Not Found for aadhar No: " + appEntity.getLoginHistory().getAadharNumber());
					}
				} catch (NumberFormatException | UnauthorizedException e1) {
					e1.printStackTrace();
					throw new IllegalArgumentException("Internal Server Error getInvoiceDLDetails !");
				} 
			}

		}

		invoiceModel.setAppNo(appEntity.getApplicationNumber());
		invoiceModel.setAppRCNo(appEntity.getLoginHistory().getUniqueKey());
		invoiceModel.setChassisNo(appTaxModel.getChassisNo());
		invoiceModel.setColour(appTaxModel.getColour());
		invoiceModel.setMakerName(appTaxModel.getMakerName());
		invoiceModel.setEngineNo(appTaxModel.getEngineNo());
		invoiceModel.setCov(appTaxModel.getVehicleSubClass());
		invoiceModel.setCovDesc(appTaxModel.getVehicleSubClassDesc());
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		TaxDetailEntity taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		TaxModel taxModel = new TaxModel();
		if(taxDetailEntity != null){
			taxModel = taxDetailConverter.convertToModel(taxDetailEntity);
			invoiceModel.setTaxModel(taxModel);
		}
		FeeModel feeModel = new FeeModel();
		if(feeDetailEntity != null){
			feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
			invoiceModel.setFeeModel(feeModel);
		}
		if(!ObjectsUtil.isNull(transactionDetailEntity)){
			invoiceModel.setPayDate(transactionDetailEntity.getCreatedOn());
		} else {
			invoiceModel.setPayDate(appEntity.getCreatedOn());
		}
		if(taxDetailEntity != null && feeDetailEntity != null)
			invoiceModel.setGrandTotal(NumberParser.numberFormat(feeDetailEntity.getTotalFee() + taxDetailEntity.getTotalAmt()));
		else if(taxDetailEntity != null && feeDetailEntity == null)
			invoiceModel.setGrandTotal(NumberParser.numberFormat(taxDetailEntity.getTotalAmt()));
		else if(taxDetailEntity == null && feeDetailEntity != null)
			invoiceModel.setGrandTotal(NumberParser.numberFormat(feeDetailEntity.getTotalFee()));

		try {
			log.info("::Slot Booking ::::start:::: " + appEntity.getLoginHistory().getSessionId());
			slots = slotService.getReceipt(userSessionConverter.converToModel(appEntity.getLoginHistory()));
			log.info("::Slot Booking ::::end:::: " + slots);
		} catch (SlotUnavailableException e) {
			log.error("SlotUnavailableException : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		invoiceModel.setSlots(slots);
		if(transactionDetailEntity != null && transactionDetailEntity.getServiceCode().equalsIgnoreCase(ServiceType.OWNERSHIP_TRANSFER_SALE.getCode()))
		{	AddressChangeModel addressChangeModel = new AddressChangeModel();
		try {
			log.info(" ::::::::START:::::::Permit transfer/surrender permitType in invoice ");
			ApplicationFormDataEntity appFormDataEntity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.OTS_FORM.getLabel());
			if(appFormDataEntity != null){
				addressChangeModel =  new ObjectMapper().readValue(appFormDataEntity.getFormData(), AddressChangeModel.class);
				if(addressChangeModel.getPermitTransferType().equalsIgnoreCase("transfer") && invoiceModel.getFeeModel() != null)
				{			
					invoiceModel.getFeeModel().setPermitType("transfer");
				}
				if(addressChangeModel.getPermitTransferType().equalsIgnoreCase("surrender") && invoiceModel.getFeeModel() != null)
				{				
					invoiceModel.getFeeModel().setPermitType("surrender");
				}
				log.info(" ::::::::END:::::::Permit transfer/surrender permitType in invoice ");
			} 
		}catch (Exception e) {
			log.error(":::Permit Transfer/Surrender permitType in invoice:::::: = " + e.getMessage());
		} 
		}
		return invoiceModel;
	}

	@Override
	@Transactional
	public InvoiceModel getInvoiceDLDetails(long sessionId, String appNo) {
		log.info("::CITIZEN::::getInvoiceDetails:::::appNo " + appNo + " sessionId "+ sessionId );
		ApplicationEntity appEntity = null;
		if(appNo != null)
			appEntity = applicationDAO.getApplication(appNo);
		else
			appEntity = applicationDAO.getApplicationFromSession(sessionId);
		log.info("::CITIZEN::::getInvoiceDLDetails:::::ServiceCode " + appEntity.getServiceCode());
		TransactionDetailEntity transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(appEntity);
		if(transactionDetailEntity != null)
			throw new IllegalArgumentException("Payment is Pending !");

		InvoiceModel invoiceModel = new InvoiceModel();
		AddressModel addressModel = new AddressModel();
		List<SlotModel> slots = new ArrayList<SlotModel>();
		try {
			if(appEntity.getServiceCode().equals(ServiceType.DL_MILITRY.getCode()) 
					|| appEntity.getServiceCode().equals(ServiceType.LL_FRESH.getCode())
					|| appEntity.getServiceCode().equals(ServiceType.DL_FOREIGN_CITIZEN.getCode()) ){
				ApplicationFormDataEntity applicationFormDataEntity = null; 
				if(appEntity.getServiceCode().equals(ServiceType.DL_MILITRY.getCode())) {

					applicationFormDataEntity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.DLM_DETAIL_FORM.getLabel());
					DLMilataryDetailsModel llRegistrationModel = new ObjectMapper().readValue(applicationFormDataEntity.getFormData(), DLMilataryDetailsModel.class);	
					addressModel.setCity(llRegistrationModel.getCity());
					addressModel.setDoorNo(llRegistrationModel.getDoorNo());
					addressModel.setStreet(llRegistrationModel.getStreet());
					addressModel.setMandalName(llRegistrationModel.getMandalName());
					addressModel.setDistrictName(llRegistrationModel.getDistrictName());
					if(llRegistrationModel.getPostOffice() != null)
						addressModel.setPostOffice(Long.valueOf(llRegistrationModel.getPostOffice()));
					invoiceModel.setApplicantName(llRegistrationModel.getDisplayName());
					log.info(":::DL Reg::::::Display Name " + llRegistrationModel.getDisplayName());
				}else if(appEntity.getServiceCode().equals(ServiceType.LL_FRESH.getCode())) {

					applicationFormDataEntity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.LLF_DETAIL_FORM.getLabel());
					LLRegistrationModel llRegistrationModel = new ObjectMapper().readValue(applicationFormDataEntity.getFormData(), LLRegistrationModel.class);	
					addressModel.setCity(llRegistrationModel.getCity());
					addressModel.setDoorNo(llRegistrationModel.getDoorNo());
					addressModel.setStreet(llRegistrationModel.getStreet());
					addressModel.setMandalName(llRegistrationModel.getMandalName());
					addressModel.setDistrictName(llRegistrationModel.getDistrictName());
					if(llRegistrationModel.getPostOffice() != null)
						addressModel.setPostOffice(Long.valueOf(llRegistrationModel.getPostOffice()));
					invoiceModel.setApplicantName(llRegistrationModel.getDisplayName());
					log.info(":::DL Reg::::::Display Name " + llRegistrationModel.getDisplayName());
				} else if(appEntity.getServiceCode().equals(ServiceType.DL_FOREIGN_CITIZEN.getCode())) {

					applicationFormDataEntity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.DLFC_DETAIL_FORM.getLabel());
					LLRegistrationModel llRegistrationModel = new ObjectMapper().readValue(applicationFormDataEntity.getFormData(), LLRegistrationModel.class);	
					addressModel.setCity(llRegistrationModel.getCity());
					addressModel.setDoorNo(llRegistrationModel.getDoorNo());
					addressModel.setStreet(llRegistrationModel.getStreet());
					addressModel.setMandalName(llRegistrationModel.getMandalName());
					addressModel.setDistrictName(llRegistrationModel.getDistrictName());
					if(llRegistrationModel.getPostOffice() != null)
						addressModel.setPostOffice(Long.valueOf(llRegistrationModel.getPostOffice()));
					invoiceModel.setApplicantName(llRegistrationModel.getDisplayName());
					log.info(":::DL Reg::::::Display Name " + llRegistrationModel.getDisplayName());
				}   
			} else{
				RegLicenseServiceResponseModel<LicenseHolderDtlsModel> responce = null;
				if(appEntity.getServiceCode().equals(ServiceType.DL_FOREIGN_CITIZEN.getCode())){
					responce = registrationLicenseService.getLicenseHolderDtls(null, appEntity.getLoginHistory().getUniqueKey());
				}else{
					responce = registrationLicenseService.getLicenseHolderDtls(appEntity.getLoginHistory().getAadharNumber(), null);
				}
				LicenseHolderDtlsModel licenseHolderDetails= responce.getResponseBody();
				addressModel.setDoorNo(licenseHolderDetails.getPresAddrDoorNo());
				addressModel.setStreet(licenseHolderDetails.getPresAddrStreet());
				addressModel.setCity(licenseHolderDetails.getPresAddrTown());
				if(!ObjectsUtil.isNull(licenseHolderDetails.getMandalDetails())){
					addressModel.setMandalName(licenseHolderDetails.getMandalDetails().getName());
				}
				if(!ObjectsUtil.isNull(licenseHolderDetails.getDistrictDetails())){
					addressModel.setDistrictName(licenseHolderDetails.getDistrictDetails().getName());
				}
				if(!ObjectsUtil.isNull(licenseHolderDetails.getStateDetails())){
					addressModel.setStateName(licenseHolderDetails.getStateDetails().getName());
				}
				addressModel.setPostOffice(Long.valueOf(licenseHolderDetails.getPresAddrPinCode()));
				invoiceModel.setApplicantName(licenseHolderDetails.getDisplayName());
			}
		} catch (Exception e) {
			log.error(":::Licence Receipt ::::::appliocation number = "+ appNo+"  " + e.getMessage());
		}
		invoiceModel.setAddresModel(addressModel);
		invoiceModel.setAdharNo(appEntity.getLoginHistory().getAadharNumber());
		invoiceModel.setAppNo(appEntity.getApplicationNumber());
		invoiceModel.setAppRCNo(appEntity.getLoginHistory().getUniqueKey());
		invoiceModel.setServiceCode(appEntity.getServiceCode());
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if(!ObjectsUtil.isNull(feeDetailEntity)){
			FeeModel feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
			invoiceModel.setPayDate(feeDetailEntity.getCreatedOn());
			invoiceModel.setFeeModel(feeModel);
			invoiceModel.setGrandTotal(NumberParser.numberFormat(feeDetailEntity.getTotalFee()));
		}
		invoiceModel.setAdharNo(appEntity.getLoginHistory().getAadharNumber());
		try {
			slots = slotService.getReceipt(userSessionConverter.converToModel(appEntity.getLoginHistory()));
		} catch (SlotUnavailableException e) {
			log.error(":::Licence Receipt ::::::appliocation number = "+ appNo+"  " + e.getMessage());
		}
		log.info(":::DL Invoice Slots::::: " + slots);
		invoiceModel.setSlots(slots);
		return invoiceModel;
	}


	/*public String getFormCode(ApplicationEntity applicationEntity){
    	String formCode = "";
    	if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_DUPLICATE.getCode())){
    		formCode = FormCodeType.LLF_DETAIL_FORM.getLabel();
    	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_ENDORSEMENT.getCode())){
    		formCode = FormCodeType.ll.getLabel();
    	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_FRESH.getCode())){
    		formCode = FormCodeType.ll.getLabel();
    	}else if( applicationEntity.getServiceCode().equalsIgnoreCase(ServiceType.LL_RETEST.getCode())){
    		formCode = FormCodeType.ll.getLabel();
    	}
    	log.info("::getFormCode::::: " + formCode);
    	return formCode ;
    }*/

	@Override
	@Transactional
	public InvoiceModel getInvoiceUsersDetails(long sessionId) {
		log.info(":::getInvoiceUsersDetails:::::");
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		log.info("::CITIZEN::::getInvoiceDLDetails:::::ServiceCode " + appEntity.getServiceCode());
		String applicantName = "";
		TransactionDetailEntity transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(appEntity);
		if(transactionDetailEntity != null)
			throw new IllegalArgumentException("Payment is Pending !");

		transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(appEntity , PaymentType.PAY);
		AadharModel aadharModel = new AadharModel();
		try {
			aadharModel =  registrationService.getAadharDetails(Long.valueOf(appEntity.getLoginHistory().getAadharNumber())).getResponseBody();
			applicantName = aadharModel.getName();
		} catch (NumberFormatException | UnauthorizedException e1) {
			e1.printStackTrace();
			throw new IllegalArgumentException("Internal Server Error getInvoiceDLDetails !");
		} 

		InvoiceModel invoiceModel = new InvoiceModel();
		invoiceModel.setPayDate(appEntity.getCreatedOn());
		invoiceModel.setApplicantName(applicantName);
		invoiceModel.setAdharNo(appEntity.getLoginHistory().getAadharNumber());
		invoiceModel.setAppNo(appEntity.getApplicationNumber());
		invoiceModel.setAppRCNo(appEntity.getLoginHistory().getUniqueKey());
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		FeeModel feeModel = new FeeModel();
		if(feeDetailEntity != null){
			feeModel = feeDetailConverter.convertToModel4License(feeDetailEntity);
			invoiceModel.setFeeModel(feeModel);
			invoiceModel.setGrandTotal(feeModel.getTotalFee());
		}

		return invoiceModel;
	}

	@Override
	@Transactional
	public InvoiceModel getReceiptUsersDetails(String appNo) {
		log.info(":::getInvoiceUsersDetails:::::");
		ApplicationEntity appEntity = applicationDAO.getApplication(appNo);
		log.info("::CITIZEN::::getInvoiceDLDetails:::::ServiceCode " + appEntity.getServiceCode());
		String applicantName = "";
		TransactionDetailEntity transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(appEntity);
		if(transactionDetailEntity != null)
			throw new IllegalArgumentException("Payment is Pending !");

		transactionDetailEntity = transactionDetailDAO.getByAppNdServiceTypeNdPaymentTypeNdStatus(appEntity , PaymentType.PAY);
		AadharModel aadharModel = new AadharModel();
		try {
			aadharModel =  registrationService.getAadharDetails(Long.valueOf(appEntity.getLoginHistory().getAadharNumber())).getResponseBody();
			applicantName = aadharModel.getName();
		} catch (NumberFormatException | UnauthorizedException e1) {
			e1.printStackTrace();
			throw new IllegalArgumentException("Internal Server Error getInvoiceDLDetails !");
		} 

		InvoiceModel invoiceModel = new InvoiceModel();
		invoiceModel.setPayDate(appEntity.getCreatedOn());
		invoiceModel.setApplicantName(applicantName);
		invoiceModel.setAdharNo(appEntity.getLoginHistory().getAadharNumber());
		invoiceModel.setAppNo(appEntity.getApplicationNumber());
		invoiceModel.setAppRCNo(appEntity.getLoginHistory().getUniqueKey());
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		FeeModel feeModel = new FeeModel();
		if(feeDetailEntity != null){
			feeModel = feeDetailConverter.convertToModel4License(feeDetailEntity);
			invoiceModel.setFeeModel(feeModel);
			invoiceModel.setGrandTotal(feeModel.getTotalFee());
		}

		return invoiceModel;
	}

	@Override
	public Boolean attachments4Communication(String appNo, String attachmentURL) {
		log.info(":attachments4Communication:::start::::::::");
		ApplicationEntity appEntity = null;
		appEntity = applicationDAO.getApplication(appNo);
		if(appEntity == null)
			throw new IllegalArgumentException("Invalid application No !");
		EventEntity event = eventDAO.getByApp(appEntity);
		if(event != null){
			event.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
		}else{
			event = new EventEntity();
			event.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			attachmentURL=attachmentURL.replace("\"","");
			event.setAttachement(attachmentURL);
			event.setEventType("Approval");
			event.setApplicationId(appEntity.getApplicationId());
			event.setIteration(0);
			event.setServiceType(appEntity.getServiceCode());
		}	
		eventDAO.saveOrUpdate(event);
		log.info(":attachments4Communication:::end:::::::");
		return true;
	}

}

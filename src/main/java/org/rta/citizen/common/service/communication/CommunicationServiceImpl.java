package org.rta.citizen.common.service.communication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.CommunicationModel;
import org.rta.MessageConfig;
import org.rta.SmsEmailService;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.EventDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.MessageType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TheftIntSusType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ApplicationFormDataModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.LoginModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.communication.CustMsgModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.service.ApplicationFormDataService;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.freshrc.FinancerFreshContactDetailsModel;
import org.rta.citizen.freshrc.FreshRcModel;
import org.rta.citizen.licence.model.updated.LLRegistrationModel;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.rta.citizen.ownershiptransfer.dao.OTTokenDAO;
import org.rta.citizen.ownershiptransfer.entity.OTTokenEntity;
import org.rta.citizen.registrationrenewal.model.CommonServiceModel;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.SlotService;
import org.rta.citizen.theftintimation.model.TheftIntimationRevocationModel;
import org.rta.citizen.userregistration.model.UserSignupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CommunicationServiceImpl implements CommunicationService {

	private static final Logger log = Logger.getLogger(CommunicationServiceImpl.class);

	public final static short SEND_SMS_EMAIL = 0;
	public final static short SEND_SMS = 1;
	public final static short SEND_EMAIL = 2;

	@Autowired
	SmsEmailService smsEmailService;
	@Autowired
	RegistrationService registrationService;
	@Autowired
	ApplicationFormDataDAO applicationFormDataDAO;

	@Autowired
	private SlotService slotService;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	/*
	 * @Value("${attachments.path}") private String attachmentDocPath;
	 */
	@Autowired
	private EventDAO eventDAO;

	@Autowired
	private OTTokenDAO oTTokenDAO; 

	@Override
	public String sendSms(CustMsgModel customerModel) {
		CommunicationModel model = new CommunicationModel(customerModel.getSmsMsg(), customerModel.getMobileNo());
		return smsEmailService.sendSms(model, customerModel.getCommunicationConfig());
	}

	@Override
	public String sendEmail(CustMsgModel customerModel) {
		CommunicationModel model = new CommunicationModel(customerModel.getSubject(), customerModel.getTo(),
				customerModel.getCc(), customerModel.getBcc(), customerModel.getMailContent());
		model.setAttachments(customerModel.getAttachments());
		model.setCc(customerModel.getCc());
		model.setBcc(customerModel.getBcc());
		return smsEmailService.sendEmail(model, customerModel.getCommunicationConfig());
	}

	/**
	 * Send sms to financer
	 */
	@Override
	public String sendSms(FinanceModel financeModel, CustMsgModel custMsgModel) {
		String smsMessage = "Dear " + custMsgModel.getCitizenName() + ", " + financeModel.getName()
				+ " has accepted the request for giving finance for the (Makers Class) against the (Token Number).Please accept the financier's offer by accessing the application so that your application will be further processed";
		CommunicationModel model = new CommunicationModel(smsMessage, financeModel.getUserNm());
		return smsEmailService.sendSms(model, custMsgModel.getCommunicationConfig());
	}

	/**
	 * send email to financer
	 */
	@Override
	public String sendEmail(FinanceModel financeModel, CustMsgModel custMsgModel) {
		StringBuilder mailContent = new StringBuilder();
		mailContent
				.append("<table><tr><td>Dear " + custMsgModel.getCitizenName() + ", " + financeModel.getName()
						+ "</td></tr>")
				.append("<tr></tr><tr><td>Your  has accepted the request for giving finance for the (Makers Class).Please contact your dealer for continuing  online registration </td></tr>")
				.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
		CommunicationModel model = new CommunicationModel(custMsgModel.getSubject(), custMsgModel.getTo(),
				custMsgModel.getCc(), custMsgModel.getBcc(), mailContent.toString());
		model.setAttachments(custMsgModel.getAttachments());
		model.setCc(custMsgModel.getCc());
		model.setBcc(custMsgModel.getBcc());
		return smsEmailService.sendEmail(model, custMsgModel.getCommunicationConfig());
	}

	@Override
	public boolean sendMsg(int msgMode, CustMsgModel customerModel, FinanceModel financeModel, Boolean isAppliedHpa,
			String serviceCode) {
		log.info("::sendMsg:::start:::::");
		try {
			if (msgMode == SEND_SMS) {
				sendSms(customerModel);
				if (isAppliedHpa != null && isAppliedHpa.equals(true)
						&& serviceCode.equals(ServiceType.THEFT_INTIMATION.getCode())) {
					sendSms(financeModel, customerModel);
				}
			}

			if (msgMode == SEND_EMAIL) {
				sendEmail(customerModel);
				if (isAppliedHpa != null && isAppliedHpa.equals(true)
						&& serviceCode.equals(ServiceType.THEFT_INTIMATION.getCode())) {
					sendEmail(financeModel, customerModel);
				}
			}

			if (msgMode == SEND_SMS_EMAIL) {
				sendSms(customerModel);
				sendEmail(customerModel);
				if (isAppliedHpa != null && isAppliedHpa.equals(true)
						&& serviceCode.equals(ServiceType.THEFT_INTIMATION.getCode())) {
					sendSms(financeModel, customerModel);
					sendEmail(financeModel, customerModel);
				}
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
		log.info("::sendMsg::end::::");
		return true;
	}

	@Override
	public CustMsgModel getCustInfo(Status status, ApplicationEntity appEntity, Boolean isNewCitizen,
			String formCodeType) {
		log.info(":getCustInfo::start::: isNewCitizen " + isNewCitizen + " formCodeType " + formCodeType);
		CustMsgModel custMsgModel = new CustMsgModel();
		ObjectMapper mapper = new ObjectMapper();
		AddressChangeModel model = null;
		ApplicationFormDataEntity entity = null;
		String citizenName = null;
		String contactNo = null;
		String emailId = null;
		custMsgModel.setCommunicationConfig(getCommunicationConfig());
		String username = null;
		if (ServiceUtil.isUserRegistrationService(ServiceType.getServiceType(appEntity.getServiceCode()))) {
			entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
					getFormCodeType(appEntity.getServiceCode()));
			try {
				UserSignupModel userSignupModel = mapper.readValue(entity.getFormData(), UserSignupModel.class);
				citizenName = userSignupModel.getCustomerDetails().getFirst_name();

				// in case of Body Builder and Medical Practitioner, we don't
				// have emailId of customer
				if (ServiceType.MEDICAL_PRACTITIONER == ServiceType.getServiceType(appEntity.getServiceCode())
						|| ServiceType.BODYBUILDER_SIGNUP == ServiceType.getServiceType(appEntity.getServiceCode())) {
					emailId = userSignupModel.getOfficeEmailId();
					contactNo = userSignupModel.getOfficeContactNumber();
					log.info(
							"\n\n>>>>>>>>>>>>>>>>>>>>>>>>>CITIZEN NAME<<<<<<<<<<<<<<<<<<<<<<<<" + citizenName + "\n\n");
				} else {
					emailId = userSignupModel.getCustomerDetails().getEmailid();
					contactNo = userSignupModel.getCustomerDetails().getMobileNumber();
				}
				LoginModel loginModel = userSignupModel.getLoginDetails();
				username = loginModel.getUsername();
			} catch (Exception e) {
				log.error("error when getting Form data details : " + formCodeType);
				e.printStackTrace();
			}
		} else if (ServiceCategory.LL_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
				|| ServiceCategory.DL_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())) {
			if (ServiceType.LL_FRESH.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
				entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
						getFormCodeType(appEntity.getServiceCode()));
				try {
					LLRegistrationModel llRegistrationModel = mapper.readValue(entity.getFormData(),
							LLRegistrationModel.class);
					emailId = llRegistrationModel.getEmailId();
					contactNo = llRegistrationModel.getMobileNo();
					username = llRegistrationModel.getDisplayName();
				} catch (Exception e) {
					log.error("error when getting Form data details : " + formCodeType);
					e.printStackTrace();
				}
			}

		} else if (ServiceCategory.REG_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
				&& ServiceType.FRESH_RC_FINANCIER == ServiceType.getServiceType(appEntity.getServiceCode())) {
			// fresh rc Messaging case
			RegistrationServiceResponseModel<FinancerFreshContactDetailsModel> freshrc = null;
			RegistrationServiceResponseModel<CustomerDetailsRequestModel> customerDetails = null;
			try {
				customerDetails = registrationService.getCustomerDetails(appEntity.getLoginHistory().getVehicleRcId());

				freshrc = registrationService
						.getFinancerFreshContactDetails(appEntity.getLoginHistory().getVehicleRcId());

			} catch (UnauthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!ObjectsUtil.isNull(freshrc.getResponseBody())) {
				emailId = !ObjectsUtil.isNull(freshrc.getResponseBody().getEmail())
						? freshrc.getResponseBody().getEmail() : "";
				contactNo = !ObjectsUtil.isNull(freshrc.getResponseBody().getMobileNumber())
						? freshrc.getResponseBody().getMobileNumber() : "";
			}
			if (!ObjectsUtil.isNull(customerDetails.getResponseBody())) {
				citizenName = !ObjectsUtil.isNull(customerDetails.getResponseBody().getFirst_name())
						? customerDetails.getResponseBody().getFirst_name() : "";
			}
		} else if (ServiceCategory.REG_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
				&& ServiceType.REGISTRATION_SUS_CANCELLATION == ServiceType
						.getServiceType(appEntity.getServiceCode())) {
			ApplicationModel applicationModel = null;
			RegistrationServiceResponseModel<ApplicationModel> appModel = null;
			try {
				appModel = registrationService.getPRDetails(appEntity.getLoginHistory().getUniqueKey());
			} catch (UnauthorizedException e) {
				log.error("No access to pr detail service");
			}
			applicationModel = appModel.getResponseBody();
			RegistrationServiceResponseModel<CustomerDetailsRequestModel> custDetails = null;
			try {
				custDetails = registrationService.getCustomerDetails(applicationModel.getVehicleRcId());
			} catch (UnauthorizedException e) {
				log.error("No details found");
			}
			if (!ObjectsUtil.isNull(custDetails) && !ObjectsUtil.isNull(custDetails.getResponseBody())) {
				citizenName = custDetails.getResponseBody().getFirst_name();
				contactNo = custDetails.getResponseBody().getPermanentPhoneNumber();
				emailId = custDetails.getResponseBody().getEmailid();
			}

		} else {
			if (isNewCitizen) {
				entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(), formCodeType);
				try {
					model = mapper.readValue(entity.getFormData(), AddressChangeModel.class);
					citizenName = model.getDisplayName();
					contactNo = model.getMobileNo();
					emailId = model.getEmailId();
				} catch (Exception e) {
					log.error("error when getting Form data details : " + formCodeType);
					e.printStackTrace();
				}
			} else {
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
				citizenName = appTaxModel.getCitizenName();
				contactNo = appTaxModel.getContactNo();
				emailId = appTaxModel.getEmailId();
				log.debug(contactNo + " ::Email::Contact:: " + emailId);
			}
		}
		custMsgModel.setMailContent(getMailContent(status, appEntity, citizenName, username));
		// custMsgModel.setCc("sandeep.yadav@otsi.co.in");
		// custMsgModel.setCc("sandeep.yadav@otsi.co.in");
		custMsgModel.setSubject(getMailSubject(status, appEntity, citizenName));
		custMsgModel.setTo(emailId);
		custMsgModel.setCitizenName(citizenName);
		custMsgModel.setMobileNo(contactNo);
		log.info("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>CITIZEN NAME<<<<<<<<<<<<<<<<<<<<<<<<" + citizenName + "\n\n");
		custMsgModel.setSmsMsg(getMsgContent(status, appEntity, citizenName, username));
		log.debug(
				" ContactNo- " + contactNo + " emailId- " + emailId + " MailContent-  " + custMsgModel.getMailContent()
						+ " Subject- " + custMsgModel.getSubject() + " SMSmsg- " + custMsgModel.getSmsMsg());
		return custMsgModel;
	}

	private List<String> getAttachmentsFrmURL(String attchUrl) {
		trustSSL();
		String[] filePathNm = attchUrl.split(",");
		List<String> docAttach = new ArrayList<>(1);
		if (filePathNm.length < 1)
			new IllegalArgumentException("Attachments file name incorrect In DB");
		try {
			saveUrl(attachmentDocPath + File.separator + filePathNm[1], filePathNm[0]);
			docAttach.add(attachmentDocPath + File.separator + filePathNm[1] + "," + filePathNm[1].replace("%", "/"));
		} catch (IOException e) { // TODO Auto-generated catch block
									// e.printStackTrace();
			return null;
		}

		return docAttach;
	}

	private void saveUrl(final String filename, final String urlString) throws MalformedURLException, IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(urlString).openStream());
			fout = new FileOutputStream(filename);
			final byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
	}

	private String getFormCodeType(String serviceCode) {
		String formCode = null;
		if (ServiceType.getServiceType(serviceCode) == ServiceType.ALTERATION_AGENCY_SIGNUP) {
			formCode = FormCodeType.AAREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.BODYBUILDER_SIGNUP) {
			formCode = FormCodeType.BBREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.DEALER_SIGNUP) {
			formCode = FormCodeType.DEALERREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.DRIVING_INSTITUTE) {
			formCode = FormCodeType.DRIVINGINSTREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.FINANCIER_SIGNUP) {
			formCode = FormCodeType.FINREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.HAZARDOUS_VEH_TRAIN_INST) {
			formCode = FormCodeType.HAZVEHTRINSTREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.MEDICAL_PRACTITIONER) {
			formCode = FormCodeType.MEDPRTSNRREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.PUC_USER_SIGNUP) {
			formCode = FormCodeType.PUCREG_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.LL_FRESH) {
			formCode = FormCodeType.LLF_DETAIL_FORM.getLabel();
		} else if (ServiceType.getServiceType(serviceCode) == ServiceType.REGISTRATION_SUS_CANCELLATION) {
			formCode = FormCodeType.RSC_FORM.getLabel();
		}
		return formCode;
	}

	@Value(value = "${user.login.financiersignup}")
	private String financierLoginURL;

	@Value(value = "${citizen.login}")
	private String citizenLoginUrl;

	@Value(value = "${user.login.dealer}")
	private String dealerLoginURL;

	@Value(value = "${user.login.pucusersignup}")
	private String pucLoginURL;

	@Value(value = "${user.login.medicalpractitioner}")
	private String medicalPractitionerLoginURL;

	@Value(value = "${user.login.bodybuilder}")
	private String bodyBuilderLoginURL;

	@Value(value = "${user.login.drivinginstitute}")
	private String drivingInstituteLoginURL;

	@Value(value = "${user.login.alterationagencysignup}")
	private String alterationAgencyLoginURL;

	@Value(value = "${license.application.status}")
	private String licenceApplicationstatusURL;

	@Value(value = "${attachments.downloaded.path}")
	private String attachmentDocPath;

	@Value(value = "${license.application.status}")
	private String llEndrosementURL;

	@Value(value = "${attachments.frontend.url}")
	private String attachementUrl;

	@Autowired
	private ApplicationFormDataService applicationFormDataService;

	public String getMailContent(Status status, ApplicationEntity appEntity, String citizenName, String username) {
		ObjectMapper mapper = null;
		StringBuilder mailContent = new StringBuilder();
		switch (status) {
		case APPROVED:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case HPA:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Hire Purchase Agreement Service registered on the name of " + citizenName
								+ " has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case HPT:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Hire Purchase Termination Service registered on the name of " + citizenName
								+ " has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case OWNERSHIP_TRANSFER_SALE:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ "  for Transfer of Ownership  has been approved  by " + citizenName + " </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case OWNERSHIP_TRANSFER_DEATH:
				mailContent.append("Dear " + citizenName + ", Your application number "
						+ appEntity.getApplicationNumber() + " for Transfer of Ownership has been approved by AO/RTO");
				break;
			case OWNERSHIP_TRANSFER_AUCTION:
				mailContent.append("Dear " + citizenName + ", Your application number "
						+ appEntity.getApplicationNumber() + " for Transfer of Ownership has been approved by AO/RTO");
				break;
			case ADDRESS_CHANGE:

				// mailContent.append("ADDRESS_CHANGE ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ "  for Change of Address service on the name of  " + citizenName
								+ " has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case DIFFERENTIAL_TAX:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ "  for Change of Address service on the name of  " + citizenName
								+ " has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");

				break;
			case DUPLICATE_REGISTRATION:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Duplicate Registration service on the name of " + citizenName
								+ " has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");

				break;
			case NOC_CANCELLATION:
				// mailContent.append("NOC_CANCELLATION ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ "for Cancellation of NOC on the name of " + citizenName + "whose RC Number is "
								+ appEntity.getLoginHistory().getUniqueKey() + "  has been approved by AO/RTO <td><tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case NOC_ISSUE:
				// mailContent.append("NOC_ISSUE ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ "   for Issue of NOC on the name of  " + citizenName + "  whose RC Number is "
								+ appEntity.getLoginHistory().getUniqueKey()
								+ " has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case REGISTRATION_CANCELLATION:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Cancellation of registration service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been approved by AO/RTO.</td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport </td></tr></table>");
				break;
			case REGISTRATION_RENEWAL:
				// mailContent.append("REGISTRATION_RENEWAL ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Renewal of Registration service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been approved by AO/RTO </td><tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");

				break;
			case REGISTRATION_SUS_CANCELLATION:
				CommonServiceModel supensionCancellationModel = getSuspensionFormData(appEntity);
				String startDate = DateUtil.extractDateAsString(supensionCancellationModel.getStartTime());
				String endDate = DateUtil
						.getDateInString(DateUtil.addMonths(new Date(supensionCancellationModel.getStartTime() * 1000),
								supensionCancellationModel.getSuspensionTime()));
				if (supensionCancellationModel.getSuspensionType().getLabel().equals(Status.SUSPENDED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr><br> ")
							.append("<tr><td>Your vehicle RC <b>" + appEntity.getLoginHistory().getUniqueKey()
									+ " </b>has been Suspended by the department.</td></tr>")
							.append("<tr><td>Reason : "
									+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getReason())
											? supensionCancellationModel.getReason() : "")
									+ " , "
									+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getComment())
											? supensionCancellationModel.getComment() : "")
									+ "</td></tr>")
							.append("<tr><td>Suspended from " + startDate + " To " + endDate + "</tr></td>")
							.append("<tr><td>Please follow this <a href='").append(citizenLoginUrl + "'> link </a>")
							.append("to apply for revocation of Suspension.</tr></td><br>")
							.append("<tr><td>Regards,</tr></td>")
							.append("<tr><td>Andhra Pradesh Transport Department</tr></td>");
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.CANCELLED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr><br> ")
							.append("<tr><td>Your vehicle RC <b>" + appEntity.getLoginHistory().getUniqueKey()
									+ " </b>has been Cancelled by the department.</td></tr>")
							.append("<tr><td>Reason : "
									+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getReason())
											? supensionCancellationModel.getReason() : "")
									+ " , "
									+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getComment())
											? supensionCancellationModel.getComment() : "")
									+ "</td></tr>")
							.append("<tr><td>Please follow this  <a href='").append(citizenLoginUrl + "'> link </a>")
							.append("to apply for revocation of Cancellation.</tr></td><br>")
							.append("<tr><td>Regards,</tr></td>")
							.append("<tr><td>Andhra Pradesh Transport Department</tr></td>");
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.OBJECTION.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr><br> ")
							.append("<tr><td>Your vehicle RC <b>" + appEntity.getLoginHistory().getUniqueKey()
									+ " </b>has been Objected by the department.</td></tr>")
							.append("<tr><td>Reason : "
									+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getReason())
											? supensionCancellationModel.getReason() : "")
									+ " , "
									+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getComment())
											? supensionCancellationModel.getComment() : "")
									+ "</td></tr>")
							.append("<tr><td>Please follow this <a href='").append(citizenLoginUrl + "'> link </a>")
							.append("to apply for revocation of Objected.</tr></td><br>")
							.append("<tr><td>Regards,</tr></td>")
							.append("<tr><td>Andhra Pradesh Transport Department</tr></td>");
				}
				log.info(">>>>>>>>>>>>>MailContent<<<<<<<<<<<<<<<<<<" + mailContent);
				break;
			case SUSPENSION_REVOCATION:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr> ")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Revocation of suspension service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been approved by AO/RTO.</td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case THEFT_INTIMATION: {
				TheftIntSusType theftType = null;
				UserSessionEntity sessionEntity = appEntity.getLoginHistory();
				try {
					ResponseModel<ApplicationFormDataModel> res = applicationFormDataService
							.getApplicationFormDataBySessionId(sessionEntity.getSessionId(),
									FormCodeType.TI_FORM.getLabel());

					mapper = new ObjectMapper();
					TheftIntimationRevocationModel theftModel = mapper.readValue(res.getData().getFormData(),
							TheftIntimationRevocationModel.class);
					theftType = theftModel.getTheftStatus();
				} catch (Exception e) {
					log.error("can't get theft type for application Number : " + appEntity.getApplicationNumber());
				}
				if (theftType != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Theft Intimation-Revocation service on the name of " + citizenName
									+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
									+ " has been approved by AO/RTO.");
					if (theftType == TheftIntSusType.FRESH) {
						mailContent.append(
								"You will not be able to do transaction for any other registration service from now. </td></tr>");
					} else {
						mailContent.append(
								"You will be able to do transaction for any other registration service from now. </td></tr>");
					}
					mailContent.append(
							"<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
				} else {
					log.error("can't get theft type for application Number : " + appEntity.getApplicationNumber());
				}
				break;
			}
			case VEHICLE_ATLERATION:
				// mailContent.append("VEHICLE_ATLERATION ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Alteration of Vehicle service on the name of " + citizenName
								+ "has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case VEHICLE_REASSIGNMENT:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Reassignment of Vehicle service on the name of " + citizenName
								+ " has been approved by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case FINANCIER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Financer Registration service has been approved.Please follow this link <a href='")
							.append(financierLoginURL)
							.append("'>Login Here</a> to login. Your Login credentials are username : ")
							.append(username).append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DEALER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Dealer Registration service has been approved.Please follow this link <a href='")
							.append(dealerLoginURL)
							.append("'>Login Here</a> to login. Your Login credentials are username : ")
							.append(username).append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case BODYBUILDER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Body Builder Registration service has been approved. Please download app <a href='")
							.append(bodyBuilderLoginURL).append("'>Download</a> to login.")
							.append("Your Login credentials are username : ").append(username).append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case HAZARDOUS_VEH_TRAIN_INST:
				// TODO :
				break;
			case ALTERATION_AGENCY_SIGNUP:
				// TODO :
				break;
			case MEDICAL_PRACTITIONER:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Medical Practitioner Registration service has been approved. Your Login credentials are username : ")
							.append(username).append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case PUC_USER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for PUC Registration service has been approved. Please download app <a href='")
							.append(pucLoginURL).append("'>Download</a> to login.")
							.append("Your Login credentials are username : ").append(username).append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case LL_FRESH:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ " of Fresh Learner License has been approved. You are now eligible to apply for Driving License after 30 days.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards</td></tr><tr><td>A.P. Transport Department</td></tr></table>");
				}

				break;
			case LL_ENDORSEMENT:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ "  of  Learner License Endorse has been approved.You are now eligible to apply for Driving License after 30 days.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards</td></tr><tr><td>A.P. Transport Department</td></tr></table>");
				}
				break;

			}
			break;
		case REJECTED:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case HPA:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Hire Purchase Agreement Service registered on the name of " + citizenName
								+ " has been rejected by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case HPT:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Hire Purchase Termination Service registered on the name of " + citizenName
								+ " has been rejected by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case OWNERSHIP_TRANSFER_SALE:
				mailContent
						.append("Dear  " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
								+ " for Transfer of Ownership has been rejected by AO/RTO Thank You AP_Road Transport");
				break;
			case OWNERSHIP_TRANSFER_DEATH:
				mailContent
						.append("Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
								+ " for Transfer of Ownership has been rejected by AO/RTO Thank You AP_Road Transport");
				break;
			case OWNERSHIP_TRANSFER_AUCTION:
				mailContent
						.append("Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
								+ " for Transfer of Ownership has been rejected by AO/RTO Thank You AP_Road Transport");
				break;
			case ADDRESS_CHANGE:
				// mailContent.append("ADDRESS_CHANGE ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Change of Address on the name of " + citizenName
								+ " has been rejected by AO/RTO</td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case DIFFERENTIAL_TAX:
				mailContent.append("DIFFERENTIAL_TAX ");
				break;
			case DUPLICATE_REGISTRATION:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Duplicate Registration service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been rejected by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case NOC_CANCELLATION:
				// mailContent.append("NOC_CANCELLATION ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Cancellation of NOC on the name of " + citizenName + " whose RC Number is "
								+ appEntity.getLoginHistory().getUniqueKey()
								+ " has been rejected by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case NOC_ISSUE:
				// mailContent.append("NOC_ISSUE ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Issue of NOC on the name of " + citizenName + "whose RC Number is "
								+ appEntity.getLoginHistory().getUniqueKey()
								+ "  has been rejected by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case REGISTRATION_CANCELLATION:
				mailContent.append("<table><tr><td>Dear  " + citizenName + ",<td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Cancellation of registration service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been rejected by AO/RTO.</td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case REGISTRATION_RENEWAL:
				// mailContent.append("REGISTRATION_RENEWAL ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Renewal of Registration service on the name of " + citizenName
								+ "whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been rejected by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case SUSPENSION_REVOCATION:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr> ")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Revocation of suspension service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been rejected by AO/RTO. </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");

				break;
			case THEFT_INTIMATION:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ "for Theft Intimation-Revocation service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been rejected by AO/RTO. </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case VEHICLE_ATLERATION:
				// mailContent.append("VEHICLE_ATLERATION ");
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Alteration of Vehicle service on the name of " + citizenName
								+ "has been rejected by AO/RTO </td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case VEHICLE_REASSIGNMENT:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Reassignment of Vehicle service on the name of " + citizenName
								+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
								+ " has been rejected by AO/RTO</td></tr>")
						.append("<tr></tr><tr></tr><tr><td>Thank You</td></tr><tr><td>AP_Road Transport</td></tr></table>");
				break;
			case FINANCIER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Financer Registration service has been rejected.Please check application status for more details.")
							.append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DEALER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Dealer Registration service has been rejected.Please check application status for more details.")
							.append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case BODYBUILDER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Body Builder Registration service has been rejected.Please check application status for more details.")
							.append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case HAZARDOUS_VEH_TRAIN_INST:
				// TODO :
				break;
			case ALTERATION_AGENCY_SIGNUP:
				// TODO :
				break;
			case MEDICAL_PRACTITIONER:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for Medical Practitioner Registration service has been rejected.Please check application status for more details.")
							.append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case PUC_USER_SIGNUP:
				if (username != null) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
									+ " for PUC Registration service has been rejected.Please check application status for more details.")
							.append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case REGISTRATION_SUS_CANCELLATION:
				CommonServiceModel supensionCancellationModel = getSuspensionFormData(appEntity);
				if (supensionCancellationModel.getSuspensionType().getLabel().equals(Status.SUSPENDED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr><br> ")
							.append("<tr><td>application number " + appEntity.getApplicationNumber()
									+ " for Suspension of rc has been rejected.</tr></td><br>")
							.append("<tr><td>Regards,</tr></td>")
							.append("<tr><td>Andhra Pradesh Transport Department</tr></td>");
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.CANCELLED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr><br> ")
					.append("<tr><td>application number " + appEntity.getApplicationNumber()
							+ " for Cancellation of rc has been rejected.</tr></td><br>")
					.append("<tr><td>Regards,</tr></td>")
					.append("<tr><td>Andhra Pradesh Transport Department</tr></td>");
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.OBJECTION.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr><br> ")
					.append("<tr><td>application number " + appEntity.getApplicationNumber()
							+ " for Objection on rc has been rejected.</tr></td><br>")
					.append("<tr><td>Regards,</tr></td>")
					.append("<tr><td>Andhra Pradesh Transport Department</tr></td>");
				}
				log.info("<<<<<<<<<<<<<<<<<<<<<<<<<Rejected mailcontent>>>>>>>>>>>>>>>>>>>>>>"+mailContent);
				break;	
			}
			break;
		case FRESH:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case OWNERSHIP_TRANSFER_SALE:
				OTTokenEntity buyerInfo = oTTokenDAO.getTokenEntity(appEntity.getApplicationId());
				if(!ObjectsUtil.isNull(buyerInfo) && !StringsUtil.isNullOrEmpty(buyerInfo.getClaimantName())){
					mailContent.append("<table><tr><td>Dear " + citizenName + ", "+ buyerInfo.getClaimantName()+"is buying </tr></td><tr><td>"
							+ appEntity.getLoginHistory().getUniqueKey() + " from you.</tr></td> </tr></td>Please access the application "
							+ appEntity.getApplicationNumber() + " and approve for further processing.</tr></td>");
				}
				break;
			case PERMIT_FRESH:
				mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
						.append("<tr></tr><tr><td>Your application number " + appEntity.getApplicationNumber()
								+ " for Permit New service whose RC Number is "
								+ appEntity.getLoginHistory().getUniqueKey() + " has been generated.</td></tr>")
						.append("<tr></tr><tr></tr><tr>Thank You</tr><tr>AP_Road Transport</tr></table>");
				break;
			case PERMIT_RENEWAL:
				mailContent.append("PERMIT_RENEWAL");
				break;
			case PERMIT_SURRENDER:
				mailContent.append("PERMIT_SURRENDER");
				break;
			case PERMIT_RENEWAL_AUTH_CARD:
				mailContent.append("PERMIT_RENEWAL_AUTH_CARD");
				break;
			case PERMIT_REPLACEMENT_VEHICLE:
				mailContent.append("PERMIT_REPLACEMENT_VEHICLE");
				break;
			case PERMIT_VARIATIONS:
				mailContent.append("PERMIT_VARIATIONS");
				break;
			case FRESH_RC_FINANCIER:
				FreshRcModel freshRc = null;
				try {
					RegistrationServiceResponseModel<FreshRcModel> freshRcResponse = registrationService
							.getFreshRcDataByApplicationNumber(appEntity.getApplicationNumber());
					freshRc = freshRcResponse.getResponseBody();
				} catch (UnauthorizedException e) {
					e.printStackTrace();
				}

				if (citizenName != null && !ObjectsUtil.isNull(freshRc)) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr><td>Financier " + freshRc.getCreatedBy()
									+ " has applied for fresh RC,</tr></td>")
							.append("<tr><td>Vehicle RC No. " + freshRc.getPrNumber() + ".</tr></td>")
							.append("<tr><td>Please login to application status using the application number "
									+ appEntity.getApplicationNumber() + " and give your consent.</tr></td>")
							.append("<tr><td>Please give your consent within 5 days to avoid the show cause letter.</tr></td>")
							.append("<tr><td>Thanks and Regards,</tr></td>")
							.append("<tr><td>A.P. Transport Department</tr></td></table>");
				}
				break;
			}
			break;
		}
		return mailContent.toString();
	}

	private CommonServiceModel getSuspensionFormData(ApplicationEntity appEntity) {
		ApplicationFormDataEntity entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
				getFormCodeType(appEntity.getServiceCode()));
		ObjectMapper mapper = new ObjectMapper();
		CommonServiceModel supensionCancellationModel = null;
		try {
			supensionCancellationModel = (CommonServiceModel) mapper.readValue(entity.getFormData(),
					CommonServiceModel.class);
		} catch (IOException e1) {
			log.error("can't get REGISTRATION_SUS_CANCELLATION data for application Number : "
					+ appEntity.getApplicationNumber());
		}
		return supensionCancellationModel;
	}

	public String getMailSubject(Status status, ApplicationEntity appEntity, String citizenName) {
		String msgContent = "";
		switch (status) {
		case APPROVED:
			if (ServiceCategory.REG_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
					&& ServiceType.REGISTRATION_SUS_CANCELLATION == ServiceType
							.getServiceType(appEntity.getServiceCode())) {
				CommonServiceModel supensionCancellationModel = getSuspensionFormData(appEntity);
				if (supensionCancellationModel.getSuspensionType().getLabel().equals(Status.SUSPENDED.getLabel())) {
					msgContent = "AP RTA - Vehicle RC " + appEntity.getLoginHistory().getUniqueKey() + " Suspended";
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.CANCELLED.getLabel())) {
					msgContent = "AP RTA - Vehicle RC " + appEntity.getLoginHistory().getUniqueKey() + " Cancelled";
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.OBJECTION.getLabel())) {
					msgContent = "AP RTA - Vehicle RC " + appEntity.getLoginHistory().getUniqueKey() + " Objection";
				}
				log.info(">>>>>>>>>>>>>msgContent<<<<<<<<<<<<<<<<<<" + msgContent);
			} else {
				msgContent = "AP_RTD_" + appEntity.getApplicationNumber() + " approved by AO/RTO";
			}
			break;
		case REJECTED:
			msgContent = "AP_RTD_" + appEntity.getApplicationNumber() + " rejected by AO/RTO";
			break;
		case FRESH:
			if (appEntity.getServiceCategory().equalsIgnoreCase(ServiceCategory.PERMIT_FITNESS_CATEGORY.getCode())) {
				msgContent = "AP_RTD_" + appEntity.getApplicationNumber() + " Permit";
			} else if (ServiceCategory.REG_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
					&& ServiceType.FRESH_RC_FINANCIER == ServiceType.getServiceType(appEntity.getServiceCode())) {
				msgContent = "AP_RTD_" + appEntity.getApplicationNumber() + " Fresh Rc";
			} else {
				msgContent = "AP_RTD_" + appEntity.getApplicationNumber() + " OTS";
			}
			break;
		}
		return msgContent;
	}

	public String getMsgContent(Status status, ApplicationEntity appEntity, String citizenName, String username) {
		String msgContent = "";
		switch (status) {
		case APPROVED:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case HPA:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Hire Purchase Agreement Service registered on the name of " + citizenName
						+ " has been approved by AO/RTO";
				break;
			case HPT:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Hire Purchase Termination Service registered on the name of " + citizenName
						+ " has been approved by AO/RTO";
				break;
			case OWNERSHIP_TRANSFER_SALE:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Transfer of Ownership Service registered on the name of " + citizenName
						+ " approved by AO/RTO";
				break;
			case OWNERSHIP_TRANSFER_DEATH:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Transfer of Ownership Service registered on the name of " + citizenName
						+ " approved by AO/RTO";
				break;
			case OWNERSHIP_TRANSFER_AUCTION:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Transfer of Ownership Service registered on the name of " + citizenName
						+ " approved by AO/RTO";
				break;
			case ADDRESS_CHANGE:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Change of Address  Service registered on the name of " + citizenName
						+ " has been approved by AO/RTO ";
				break;
			case DIFFERENTIAL_TAX:

				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Hire Purchase Agreement Service registered on the name of " + citizenName
						+ " has been approved by AO/RTO";
				break;
			case DUPLICATE_REGISTRATION:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Duplicate Registration Service registered on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been approved by AO/RTO ";
				break;
			case NOC_CANCELLATION:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Cancellation of NOC Service registered on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ "  has been approved by AO/RTO";
				break;
			case NOC_ISSUE:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Issue of NOC  Service registered on the name of " + citizenName + " whose RC Number is "
						+ appEntity.getLoginHistory().getUniqueKey() + "  has been approved by AO/RTO";
				break;
			case REGISTRATION_CANCELLATION:
				msgContent = "Dear " + citizenName + ", Your Application Number " + appEntity.getApplicationNumber()
						+ " for Cancellation of registration service on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been approved by AO/RTO. ";
				break;
			case REGISTRATION_RENEWAL:
				msgContent = "Dear " + citizenName + ", Your " + appEntity.getApplicationNumber()
						+ " for Renewal of Registration Service registered on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been approved by AO/RTO ";
				break;
			case REGISTRATION_SUS_CANCELLATION:
				if (ServiceCategory.REG_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
						&& ServiceType.REGISTRATION_SUS_CANCELLATION == ServiceType
								.getServiceType(appEntity.getServiceCode())) {
					CommonServiceModel supensionCancellationModel = getSuspensionFormData(appEntity);
					if (supensionCancellationModel.getSuspensionType().getLabel().equals(Status.SUSPENDED.getLabel())) {
						msgContent = "Dear " + citizenName + ",\n Your vehicle RC "
								+ appEntity.getLoginHistory().getUniqueKey() + " has been Suspended by the department."
								+ "\n Reason : "
								+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getReason())
										? supensionCancellationModel.getReason() : "")
								+ " , "
								+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getComment())
										? supensionCancellationModel.getComment() : "")
								+ "\n" + "Regards,\n Andhra Pradesh Transport Department";

					} else if (supensionCancellationModel.getSuspensionType().getLabel()
							.equals(Status.CANCELLED.getLabel())) {
						msgContent = "Dear " + citizenName + ",\n Your vehicle RC "
								+ appEntity.getLoginHistory().getUniqueKey() + " has been Cancelled by the department."
								+ "\n Reason :"
								+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getReason())
										? supensionCancellationModel.getReason() : "")
								+ " , "
								+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getComment())
										? supensionCancellationModel.getComment() : "")
								+ "\n" + "Regards,\n Andhra Pradesh Transport Department";
					} else if (supensionCancellationModel.getSuspensionType().getLabel()
							.equals(Status.OBJECTION.getLabel())) {
						msgContent = "Dear " + citizenName + ",\n Your vehicle RC "
								+ appEntity.getLoginHistory().getUniqueKey() + " has been Objected by the department."
								+ "\n Reason :"
								+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getReason())
										? supensionCancellationModel.getReason() : "")
								+ " , "
								+ (!StringsUtil.isNullOrEmpty(supensionCancellationModel.getComment())
										? supensionCancellationModel.getComment() : "")
								+ "\n" + "Regards,\n Andhra Pradesh Transport Department";
					}
				}
				log.info(">>>>>>>>>>>>>>>>>>msgContent<<<<<<<<<<<<<<<" + msgContent);
				break;
			case SUSPENSION_REVOCATION:
				msgContent = "Dear " + citizenName + ", Your Application Number " + appEntity.getApplicationNumber()
						+ " for Revocation service on the name of " + citizenName + " whose RC Number is "
						+ appEntity.getLoginHistory().getUniqueKey() + " has been approved by AO/RTO. ";
				break;
			case THEFT_INTIMATION:
				TheftIntSusType theftType = null;
				UserSessionEntity sessionEntity = appEntity.getLoginHistory();
				try {
					ResponseModel<ApplicationFormDataModel> res = applicationFormDataService
							.getApplicationFormDataBySessionId(sessionEntity.getSessionId(),
									FormCodeType.TI_FORM.getLabel());
					ObjectMapper mapper = new ObjectMapper();
					TheftIntimationRevocationModel theftModel = mapper.readValue(res.getData().getFormData(),
							TheftIntimationRevocationModel.class);
					theftType = theftModel.getTheftStatus();
				} catch (Exception e) {
					log.error("can't get theft type for application Number : " + appEntity.getApplicationNumber());
				}
				if (theftType != null) {
					msgContent = "Dear " + citizenName + ", Your Application Number " + appEntity.getApplicationNumber()
							+ " for Theft Intimation service on the name of " + citizenName + " whose RC Number is "
							+ appEntity.getLoginHistory().getUniqueKey() + " has been approved by AO/RTO. ";
					if (theftType == TheftIntSusType.FRESH) {
						msgContent = msgContent
								+ "You will not be able to do transaction for any other registration service from now.";
					} else {
						msgContent = msgContent
								+ "You will be able to do transaction for any other registration service from now.";
					}
				}
				break;
			case VEHICLE_ATLERATION:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Alteration of Vehicle Service registered on the name of " + citizenName
						+ " has been approved by AO/RTO ";
				break;
			case VEHICLE_REASSIGNMENT:
				msgContent = "Dear " + citizenName + ", Your  Application Number " + appEntity.getApplicationNumber()
						+ " for Reassignment of Vehicle Service registered on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been approved by AO/RTO ";
				break;
			case FINANCIER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Financier User Registration has been approved. Please follow this link "
						+ financierLoginURL + " to login. Your Login credentials are username : " + username
						+ " Thanks, RTA Andhra Pradesh";
				break;
			case DEALER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Dealer Registration has been approved. Please follow this link " + dealerLoginURL
						+ " to login. Your Login credentials are username : " + username
						+ " Thanks, RTA Andhra Pradesh";
				break;
			case BODYBUILDER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Body Builder Registration has been approved. Please download app " + bodyBuilderLoginURL
						+ " to login. Your Login credentials are username : " + username
						+ " Thanks, RTA Andhra Pradesh";
				log.info("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>MSG CONTENT<<<<<<<<<<<<<<<<<<<<<<<<\n\n" + msgContent + "\n\n");
				break;
			case MEDICAL_PRACTITIONER:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Medical Practitioner Registration has been approved. Your Login credentials are username : "
						+ username + " Thanks, RTA Andhra Pradesh";
				break;
			case PUC_USER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for PUC User Registration has been approved. Please download app " + pucLoginURL
						+ " to login. Your Login credentials are username : " + username
						+ " Thanks, RTA Andhra Pradesh";
				break;
			case HAZARDOUS_VEH_TRAIN_INST:
				// TODO :
				break;
			case ALTERATION_AGENCY_SIGNUP:
				// TODO :
				break;

			}

			break;
		case REJECTED:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case HPA:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Hire Purchase Agreement Service registered on the name of " + citizenName
						+ " has been rejected by AO/RTO";
				break;
			case HPT:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Hire Purchase Termination Service registered on the name of " + citizenName
						+ " has been rejected by AO/RTO";
				break;
			case OWNERSHIP_TRANSFER_SALE:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Transfer of Ownership Service registered on the name of " + citizenName
						+ " rejected by AO/RTO";
				break;
			case OWNERSHIP_TRANSFER_DEATH:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Transfer of Ownership Service registered on the name of " + citizenName
						+ " rejected by AO/RTO";
				break;
			case OWNERSHIP_TRANSFER_AUCTION:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Transfer of Ownership Service registered on the name of " + citizenName
						+ " rejected by AO/RTO";
				break;
			case ADDRESS_CHANGE:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Change of Address Service registered on the name of " + citizenName
						+ " has been rejected by AO/RTO ";
				break;
			case DIFFERENTIAL_TAX:
				// msgContent = "DIFFERENTIAL_TAX ";
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Change of Address Service registered on the name of " + citizenName
						+ " has been rejected by AO/RTO ";

				break;
			case DUPLICATE_REGISTRATION:
				msgContent = "Dear " + citizenName + ", Your Application Number  " + appEntity.getApplicationNumber()
						+ " for Duplicate Registration Service registered on the name of " + citizenName
						+ " whose RC Number is (RC Number) has been rejected by AO/RTO ";
				break;
			case NOC_CANCELLATION:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Cancellation of NOC Service registered on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ "  has been rejected by AO/RTO ";
				break;
			case NOC_ISSUE:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Issue of NOC Service registered on the name of " + citizenName + " whose RC Number is "
						+ appEntity.getLoginHistory().getUniqueKey() + " has been rejected by AO/RTO ";
				break;
			case REGISTRATION_CANCELLATION:
				msgContent = "Dear " + citizenName + ", Your Application Number " + appEntity.getApplicationNumber()
						+ " for Cancellation of registration service on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been rejected by AO/RTO. ";
				break;
			case REGISTRATION_RENEWAL:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Renewal of Registration Service registered on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been rejected by AO/RTO ";
				break;
			case SUSPENSION_REVOCATION:
				msgContent = "Dear " + citizenName + ", Your Application Number " + appEntity.getApplicationNumber()
						+ " for Revocation service on the name of " + citizenName + " whose RC Number is "
						+ appEntity.getLoginHistory().getUniqueKey() + " has been rejected by AO/RTO. ";
				break;
			case THEFT_INTIMATION:
				msgContent = "Dear " + citizenName + ", Your Application Number " + appEntity.getApplicationNumber()
						+ " for Theft Intimation-Revocation service on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been rejected by AO/RTO. ";
				break;
			case VEHICLE_ATLERATION:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Alteration of Vehicle Service registered on the name of " + citizenName
						+ " has been rejected by AO/RTO ";
				break;
			case VEHICLE_REASSIGNMENT:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Reassignment of Vehicle Service registered on the name of " + citizenName
						+ " whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been rejected by AO/RTO ";
				break;
			case FINANCIER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Financier User Registration has been rejected. Please check application status for more details. Thanks, RTA Andhra Pradesh";
				break;
			case DEALER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Dealer Registration has been rejected. Please check application status for more details. Thanks, RTA Andhra Pradesh";
				break;
			case BODYBUILDER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Body Builder Registration has been rejected. Please check application status for more details. Thanks, RTA Andhra Pradesh";
				break;
			case MEDICAL_PRACTITIONER:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for Medical Practitioner Registration has been rejected. Please check application status for more details. Thanks, RTA Andhra Pradesh";
				break;
			case PUC_USER_SIGNUP:
				msgContent = "Dear " + citizenName + ", Your application with application number"
						+ appEntity.getApplicationNumber()
						+ " for PUC User Registration has been rejected. Please check application status for more details. Thanks, RTA Andhra Pradesh";
				break;
			case HAZARDOUS_VEH_TRAIN_INST:
				// TODO :
				break;
			case ALTERATION_AGENCY_SIGNUP:
				// TODO :
				break;
			case REGISTRATION_SUS_CANCELLATION:
				CommonServiceModel supensionCancellationModel = getSuspensionFormData(appEntity);
				if (supensionCancellationModel.getSuspensionType().getLabel().equals(Status.SUSPENDED.getLabel())) {
					msgContent = "Dear " + citizenName + ",\n Your application number "
							+ appEntity.getApplicationNumber() + " for Suspension of rc has been rejected."
							+ "Regards,\n" + "Andhra Pradesh Transport Department";
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.CANCELLED.getLabel())) {
					msgContent = "Dear " + citizenName + ",\n Your application number "
							+ appEntity.getApplicationNumber() + " for Suspension of rc has been rejected."
							+ "Regards,\n" + "Andhra Pradesh Transport Department";
				} else if (supensionCancellationModel.getSuspensionType().getLabel()
						.equals(Status.OBJECTION.getLabel())) {
					msgContent = "Dear " + citizenName + ",\n Your application number "
							+ appEntity.getApplicationNumber() + " for Suspension of rc has been rejected."
							+ "Regards,\n" + "Andhra Pradesh Transport Department";
				}
				log.info("<<<<<<<<<<<<<<<<<<<<<<<<<Rejected msgcontent>>>>>>>>>>>>>>>>>>>>>>"+msgContent);
				break;
			}
			break;
		case FRESH:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case OWNERSHIP_TRANSFER_SALE:
				OTTokenEntity buyerInfo = oTTokenDAO.getTokenEntity(appEntity.getApplicationId());
				if (!ObjectsUtil.isNull(buyerInfo) && !StringsUtil.isNullOrEmpty(buyerInfo.getClaimantName())) {
					msgContent = "Dear " + citizenName + ", "+buyerInfo.getClaimantName()+" is buying "
							+ appEntity.getLoginHistory().getUniqueKey() + " from you. Please access the application "
							+ appEntity.getApplicationNumber() + " and approve for further processing.";
				}
				break;
			case PERMIT_FRESH:
				msgContent = "Dear " + citizenName + ", Your application number " + appEntity.getApplicationNumber()
						+ " for Permit New service whose RC Number is " + appEntity.getLoginHistory().getUniqueKey()
						+ " has been generated.";
				break;
			case PERMIT_RENEWAL:
				msgContent = "PERMIT_RENEWAL";
				break;
			case PERMIT_SURRENDER:
				msgContent = "PERMIT_SURRENDER";
				break;
			case PERMIT_RENEWAL_AUTH_CARD:
				msgContent = "PERMIT_RENEWAL_AUTH_CARD";
				break;
			case PERMIT_REPLACEMENT_VEHICLE:
				msgContent = "PERMIT_REPLACEMENT_VEHICLE";
				break;
			case PERMIT_VARIATIONS:
				msgContent = "PERMIT_VARIATIONS";
				break;
			case FRESH_RC_FINANCIER:
				FreshRcModel freshRc = null;
				try {
					RegistrationServiceResponseModel<FreshRcModel> freshRcResponse = registrationService
							.getFreshRcDataByApplicationNumber(appEntity.getApplicationNumber());
					freshRc = freshRcResponse.getResponseBody();
				} catch (UnauthorizedException e) {
					e.printStackTrace();
				}

				if (citizenName != null && !ObjectsUtil.isNull(freshRc)) {
					msgContent = "Dear " + citizenName + " Financier " + freshRc.getCreatedBy()
							+ " has applied for fresh RC,\n" + "Vehicle RC No. " + freshRc.getPrNumber() + ".\n"
							+ "Please login to application status using the application number "
							+ appEntity.getApplicationNumber() + " and give your consent.\n"
							+ "Please give your consent within 5 days to avoid the show cause letter.\n"
							+ "Thanks and Regards,\n" + "A.P. Transport Department";
				}
				break;
			}
			break;
		}
		return msgContent;
	}

	@Override
	public MessageConfig getCommunicationConfig() {

		RegistrationServiceResponseModel<MessageConfig> result = null;

		try {
			result = registrationService.getCommunicationConfig();
		} catch (UnauthorizedException e) {
			log.info("UnauthorizedException error in http request " + result);
			e.printStackTrace();
		}

		if (result.getHttpStatus() != HttpStatus.OK) {
			log.info("error in http request " + result.getHttpStatus());
			return null;
		}
		MessageConfig messageConfig = result.getResponseBody();
		return messageConfig;
	}

	@Override
	public CustMsgModel getCustInfoForSellerToBuyer(ApplicationEntity appEntity) {
		log.info(":getCustInfoForSellerToBuyer::start:::");
		/*
		 * CustMsgModel custMsgModel = new CustMsgModel(); ObjectMapper mapper =
		 * new ObjectMapper(); AddressChangeModel model = null;
		 * ApplicationFormDataEntity entity=null; String buyerName = null;
		 * String buyerContactNo = null; String buyerEmailId = null; String
		 * sellerName = null; String sellerContactNo = null; String
		 * sellerEmailId = null; EventEntity event = null; event =
		 * eventDAO.getByApp(appEntity); if(event.getIteration() > 3) return
		 * null; custMsgModel.setCommunicationConfig(getCommunicationConfig());
		 * entity = applicationFormDataDAO.getApplicationFormData(appEntity.
		 * getApplicationId(), FormCodeType.OTS_FORM.getLabel()); if(entity !=
		 * null){ try { model = mapper.readValue(entity.getFormData(),
		 * AddressChangeModel.class); buyerName = model.getDisplayName();
		 * buyerContactNo = model.getMobileNo(); buyerEmailId =
		 * model.getEmailId(); } catch (Exception e) { log.error(
		 * "error when getting Form data details : "
		 * +FormCodeType.OTS_FORM.getLabel()); e.printStackTrace(); } }
		 * 
		 * RegistrationServiceResponseModel<ApplicationTaxModel> result = null;
		 * try { result =
		 * registrationService.getTaxDetails(appEntity.getLoginHistory().
		 * getUniqueKey()); } catch (RestClientException e) { log.error(
		 * "error when getting tr details : " + e); } if
		 * (ObjectsUtil.isNull(result)) { log.info(
		 * "tr details not found for tr number : " +
		 * appEntity.getLoginHistory().getUniqueKey()); return null; } if
		 * (result.getHttpStatus() != HttpStatus.OK) { log.info(
		 * "error in http request " + result.getHttpStatus()); return null; }
		 * ApplicationTaxModel appTaxModel = result.getResponseBody();
		 * if(appTaxModel != null){ sellerName = appTaxModel.getCitizenName();
		 * contactNo = appTaxModel.getContactNo(); emailId =
		 * appTaxModel.getEmailId(); }
		 * custMsgModel.setMailContent(getMailContent(status,appEntity,
		 * citizenName)); custMsgModel.setCc("sandeep.yadav@kelltontech.com");
		 * custMsgModel.setSubject(getMailSubject(status,appEntity,citizenName))
		 * ; custMsgModel.setTo(emailId); custMsgModel.setMobileNo(contactNo);
		 * custMsgModel.setSmsMsg(getMsgContent(status,appEntity,citizenName));
		 * List <String> attachmnets = new ArrayList<String>(); if(event !=
		 * null) attachmnets = getAttachmentsFrmURL(event.getAttachement());
		 * custMsgModel.setAttachments(attachmnets);
		 */
		log.info(":getCustInfo::end:::");
		return null;
	}

	@Override
	public boolean sendMsg(int msgMode, CustMsgModel customerModel) {
		log.info("::sendMsg:::start:::::");
		try {
			if (msgMode == SEND_SMS) {
				sendSms(customerModel);

			}

			if (msgMode == SEND_EMAIL) {
				sendEmail(customerModel);

			}
			if (msgMode == SEND_SMS_EMAIL) {
				sendSms(customerModel);
				sendEmail(customerModel);

			}
		} catch (IllegalArgumentException e) {
			return false;
		}
		log.info("::sendMsg::end::::");
		return true;
	}

	public String getLicenceMailContent(Status status, ApplicationEntity appEntity, String citizenName,
			String activityType, String dlNumber) {
		StringBuilder mailContent = new StringBuilder();
		switch (status) {
		case APPROVED:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case LL_FRESH:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>we have received your application "
									+ appEntity.getApplicationNumber()
									+ " of Fresh Learner License, You can check the status of your application via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully passed the learner license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber())
							.append(" For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You did not pass in the learner license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber()).append(" You can apply for retest via <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ " of Fresh learner License has been approved.")
							.append("You can download the Driving License via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ " of Fresh learner License has been Rejected.")
							.append("For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;

			case LL_ENDORSEMENT:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Please find the copy of your endorsed learner license and payment receipt in the attachment section.")
							.append("Your Application Number is ").append(appEntity.getApplicationNumber())
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ " of Learner License Endorse has been approved.")
							.append("You are now eligible to apply for Driving License after 30 days.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ " of Learner License Endorse has been Rejected.")
							.append("Please follow this link to reapply for the service <a href='")
							.append(llEndrosementURL).append("'>Click Here</a>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}

				break;
			case LL_DUPLICATE:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Please find the Duplicate learner license and payment receipt in the attachements section.")
							.append("Your Application Number is ").append(appEntity.getApplicationNumber())
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case LL_RETEST:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully booked a test slot. On ")
							.append(getSlotDetail(appEntity))
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DL_FRESH:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>we have received your application "
									+ appEntity.getApplicationNumber()
									+ " of Fresh Driving License, You have successfully booked a test slot. On ")
							.append(getSlotDetail(appEntity))
							.append(" Please find the payment receipt in the attachments section. ")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ " of Fresh Driving License has been approved.")
							.append("You can download the Driving License via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" Please find the Driving License in the attachments section. ")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber()
									+ " of Fresh Driving License has been Rejected.")
							.append("For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully passed the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber())
							.append(" For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You did not pass in the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber()).append(" You can apply for retest via <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}

				break;
			case DL_ENDORSMENT:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>we have received your application "
									+ appEntity.getApplicationNumber()
									+ " of Driving License Endorse, You can check the status of your application via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append(" Please find the payment receipt in the attachments section. ")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully passed the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber())
							.append(" For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append(" Please find the Endorse Driving License in the attachments section. ")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You did not pass in the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber()).append(" You can apply for retest via <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DL_DUPLICATE:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Please find the Duplicate driving license and payment receipt in the attachements section.")
							.append("Your Application Number is").append(appEntity.getApplicationNumber())
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DL_RETEST:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully booked a test slot. On ")
							.append(getSlotDetail(appEntity))
							.append(" Please find the payment receipt in the attachements section. ")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully passed the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber())
							.append(" For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append(" Please find the Driving license in the attachements section. ")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You did not pass in the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber()).append(" You can apply for retest via <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DL_CHANGE_ADDRESS:
			case DL_RENEWAL:
			case DL_INT_PERMIT:
			case DL_BADGE:
			case DL_MILITRY:
			case DL_FOREIGN_CITIZEN:
			case DL_SURRENDER:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>we have received your application "
									+ appEntity.getApplicationNumber() + "of ")
							.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append("  , You can check the status of your application via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber() + " of ")
							.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append(" has been approved.")
							.append(" You can download the Driving License via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" Please find the Driving License in the attachments section.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application ").append(appEntity.getApplicationNumber())
							.append(" of ").append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append(" has been Rejected ").append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DL_EXPIRED:
				if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>we have received your application "
									+ appEntity.getApplicationNumber() + "of ")
							.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append("  , You have successfully booked a test slot on ").append(getSlotDetail(appEntity))
							.append(" Please find the payment receipt and LLR in the attachments section")
							.append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber() + " of ")
							.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append(" has been approved.")
							.append(" You can download the Driving License via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" Please find the Driving License in the attachments section.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application ").append(appEntity.getApplicationNumber())
							.append(" of ").append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append(" has been Rejected ").append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully passed the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber())
							.append(" For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You did not pass in the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber()).append(" You can apply for retest via <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS_WITH_ATTACHEMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You have successfully passed the driving license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber())
							.append(" For more details please follow this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>").append("</td></tr>")
							.append(" Please find the Driving License in the attachments section.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL_WITH_ATTACHEMENT.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>You did not pass in the driver license test held on ")
							.append(getSlotDetail(appEntity)).append(". Your application Number is ")
							.append(appEntity.getApplicationNumber()).append(" You can apply for retest via <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DL_DLINFO:

				break;
			case DL_REVO_SUS:
				if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application " + appEntity.getApplicationNumber() + " of ")
							.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append(" has been approved.")
							.append(" You can download the Driving License via this link <a href='")
							.append(licenceApplicationstatusURL).append("'>Click Here</a>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>Your application ").append(appEntity.getApplicationNumber())
							.append(" of ").append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
							.append(" has been Rejected ").append(" For more details please follow this link <a href='")
							.append("licenceApplicationstatusURL").append("'>Click Here</a>").append("</td></tr>")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;
			case DL_SUSU_CANC:
				if (activityType.equalsIgnoreCase(MessageType.CCO_RASIE_SUSPENSION.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>CCO has initiated Suspension/Cancellation against your Driving License ")
							.append(dlNumber).append(", and issued showcause notice against your Driving License")
							.append(dlNumber)
							.append(". Respond to show cause notice by appearing before AO within 15 days after receiving the notice.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_APPROVED_SUSPENSION.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>AO has approved Suspension request against your Driving License ")
							.append(dlNumber).append(" and your will be suspended ")
							.append(DateUtil.getDateAsString(System.currentTimeMillis())).append(" to ")
							.append(DateUtil.getDateAsString(DateUtil.addDays(System.currentTimeMillis(), 15)))
							.append(" Please apply for revocation after the suspension period")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_REJECT_SUSPENSION.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>AO has rejected Suspension request against your Driving License ")
							.append(dlNumber)
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_APPROVED_CANCELATION.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>AO has approved Cancellation request against your Driving License ")
							.append(dlNumber).append(", and your Driving License will be cancelled.")
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				} else if (activityType.equalsIgnoreCase(MessageType.AO_REJECT_CANCELATION.getLabel())) {
					mailContent.append("<table><tr><td>Dear " + citizenName + ",</td></tr>")
							.append("<tr></tr><tr><td>AO has rejected Cancellation request against your Driving License ")
							.append(dlNumber)
							.append("<tr></tr><tr></tr><tr><td>Thanks and Regards </td></tr><tr><td>RTA Andhra Pradesh</td></tr></table>");
				}
				break;

			}

		case REJECTED:
			switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
			case LL_FRESH:

				break;
			}
			break;
		}
		return mailContent.toString();
	}

	public String getLicenceMailSubject(ApplicationEntity appEntity, String activityType) {
		String mailSubject = "";
		if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
			mailSubject = "AP_RTD: " + (ServiceType.getServiceType(appEntity.getServiceCode()).getLabel())
					+ " Payment confirmation";
		} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
			mailSubject = "AP_RTD: " + (ServiceType.getServiceType(appEntity.getServiceCode()).getLabel())
					+ " Application approved";
		} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
			mailSubject = "AP_RTD: " + (ServiceType.getServiceType(appEntity.getServiceCode()).getLabel())
					+ " Application rejected";
		} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())
				|| activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS_WITH_ATTACHEMENT.getLabel())) {
			mailSubject = "AP_RTD: " + (ServiceType.getServiceType(appEntity.getServiceCode()).getLabel())
					+ " Test Passed";
		} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())
				|| activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL_WITH_ATTACHEMENT.getLabel())) {
			mailSubject = "AP_RTD: " + (ServiceType.getServiceType(appEntity.getServiceCode()).getLabel())
					+ " Test Failed";
		} else if (activityType.equalsIgnoreCase(MessageType.DL_INFO.getLabel())) {
			mailSubject = "AP_RTD: " + (ServiceType.getServiceType(appEntity.getServiceCode()).getLabel());
		}
		return mailSubject;
	}

	public String getLicenceMsgContent(Status status, ApplicationEntity appEntity, String citizenName,
			String activityType, String dlNumber) {
		StringBuilder msgContent = new StringBuilder();
		switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
		case LL_FRESH:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("we have received your application " + appEntity.getApplicationNumber()
								+ "of Fresh Learner License, You can check the status of your application via this link ")
						.append(licenceApplicationstatusURL).append("Thanks and Regards,\n")
						.append("A.P. Transport Department");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
				msgContent.append("Dear " + citizenName + "\n")
						.append("You have successfully passed the learner license test held on ")
						.append(getSlotDetail(appEntity)).append(". Your application Number is ")
						.append(appEntity.getApplicationNumber()).append(" For more details please follow this link")
						.append(licenceApplicationstatusURL).append("\n Thank You \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("You did not pass in the learner license test held on ")
						.append(getSlotDetail(appEntity)).append(". Your application Number is ")
						.append(appEntity.getApplicationNumber()).append(" You can apply for retest via ")
						.append(licenceApplicationstatusURL).append(" For more details please follow this link ")
						.append("licenceApplicationstatusURL").append("\n Thanks and Regards\n\n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("Your application " + appEntity.getApplicationNumber()
								+ " of Fresh learner License has been approved ")
						.append("You can download the learner License via this link")
						.append(licenceApplicationstatusURL).append("Thanks and Regards,\n")
						.append("A.P. Transport Department");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("Your application " + appEntity.getApplicationNumber()
								+ " of Fresh learner License has been rejected ")
						.append("For more details please follow this link").append(licenceApplicationstatusURL)
						.append("Thanks and Regards,\n").append("A.P. Transport Department");
			}
			break;
		case LL_ENDORSEMENT:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",")
						.append("we have sent you the endorsed learner license on your registered Email ID.")
						.append("Your Application Number is").append(appEntity.getApplicationNumber())
						.append("\n Thanks and Regards \n\n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
				msgContent.append("Dear " + citizenName + "\n").append("Your application ")
						.append(appEntity.getApplicationNumber())
						.append(" of  Learner License Endorse has been approved.You are now eligible to apply for Driving License after 30 days.")
						.append("\n Thank You \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n").append("Your application ")
						.append(appEntity.getApplicationNumber())
						.append(" of  Learner License Endorse has been Rejected. ")
						.append(" Please follow this link to reapply for the service ").append(llEndrosementURL)
						.append("\n Thanks and Regards\n\n RTA Andhra Pradesh");
			}
			break;
		case LL_DUPLICATE:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",")
						.append("We have sent you the Duplicate learner license and payment receipt on your registered Email ID.")
						.append("Your Application Number is").append(appEntity.getApplicationNumber())
						.append("\n Thanks and Regards \n\n RTA Andhra Pradesh");
			}
			break;
		case DL_FRESH:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("we have received your application " + appEntity.getApplicationNumber()
								+ " You have successfully booked a test slot. On ")
						.append(getSlotDetail(appEntity)).append("Thanks and Regards,\n")
						.append("A.P. Transport Department");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("Your application " + appEntity.getApplicationNumber()
								+ " of Fresh Driving License has been approved ")
						.append("You can download the Driving License via this link")
						.append(licenceApplicationstatusURL).append("Thanks and Regards,\n")
						.append("A.P. Transport Department");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("Your application " + appEntity.getApplicationNumber()
								+ " of Fresh Driving License has been rejected ")
						.append("For more details please follow this link").append(licenceApplicationstatusURL)
						.append("Thanks and Regards,\n").append("A.P. Transport Department");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
				msgContent.append("Dear " + citizenName + "\n")
						.append("You have successfully passed the driver license test held on ")
						.append(getSlotDetail(appEntity)).append(". Your application Number is ")
						.append(appEntity.getApplicationNumber()).append(" For more details please follow this link")
						.append(licenceApplicationstatusURL).append("\n Thank You \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("You did not pass in the driver license test held on ").append(getSlotDetail(appEntity))
						.append(". Your application Number is ").append(appEntity.getApplicationNumber())
						.append(" You can apply for retest via ").append(licenceApplicationstatusURL)
						.append(" For more details please follow this link ").append("licenceApplicationstatusURL")
						.append("\n Thanks and Regards\n\n RTA Andhra Pradesh");
			}
			break;
		case DL_ENDORSMENT:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",").append("we have received your application")
						.append(appEntity.getApplicationNumber())
						.append("of Driving License Endorse, You can check the status of your application via this link")
						.append(licenceApplicationstatusURL).append("\n Thanks and Regards \n\n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
				msgContent.append("Dear " + citizenName + "\n")
						.append("You have successfully passed the driver license test held on ")
						.append(getSlotDetail(appEntity)).append(". Your application Number is ")
						.append(appEntity.getApplicationNumber()).append(" For more details please follow this link")
						.append(licenceApplicationstatusURL).append("\n Thank You \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("You did not pass in the driver license test held on ").append(getSlotDetail(appEntity))
						.append(". Your application Number is ").append(appEntity.getApplicationNumber())
						.append(" You can apply for retest via ").append(licenceApplicationstatusURL)
						.append(" For more details please follow this link ").append("licenceApplicationstatusURL")
						.append("\n Thanks and Regards\n\n RTA Andhra Pradesh");
			}

			break;
		case DL_DUPLICATE:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",")
						.append("We have sent you the Duplicate Driving license and payment receipt on your registered Email ID.")
						.append("Your Application Number is").append(appEntity.getApplicationNumber())
						.append("\n Thanks and Regards \n\n RTA Andhra Pradesh");
			}
			break;
		case DL_RETEST:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",").append("You have successfully booked a test slot. On")
						.append(getSlotDetail(appEntity)).append("\n Thanks and Regards \n\n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
				msgContent.append("Dear " + citizenName + "\n")
						.append("You have successfully passed the driver license test held on ")
						.append(getSlotDetail(appEntity)).append(". Your application Number is ")
						.append(appEntity.getApplicationNumber()).append(" For more details please follow this link")
						.append(licenceApplicationstatusURL).append("\n Thank You \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("You did not pass in the driver license test held on ").append(getSlotDetail(appEntity))
						.append(". Your application Number is ").append(appEntity.getApplicationNumber())
						.append(" You can apply for retest via ").append(licenceApplicationstatusURL)
						.append(" For more details please follow this link ").append("licenceApplicationstatusURL")
						.append("\n Thanks and Regards\n\n RTA Andhra Pradesh");
			}
			break;
		case DL_CHANGE_ADDRESS:
		case DL_RENEWAL:
		case DL_INT_PERMIT:
		case DL_BADGE:
		case DL_MILITRY:
		case DL_FOREIGN_CITIZEN:
		case DL_SURRENDER:
		case DL_REVO_SUS:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("we have received your application " + appEntity.getApplicationNumber() + " of ")
						.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
						.append(", You can check the status of your application via this link ")
						.append(licenceApplicationstatusURL).append("Thanks and Regards,\n")
						.append("A.P. Transport Department");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("Your application " + appEntity.getApplicationNumber() + " of ")
						.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
						.append("has been approved ").append("You can download the Driving License via this link")
						.append(licenceApplicationstatusURL).append("Thanks and Regards,\n")
						.append("A.P. Transport Department");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
				msgContent.append("Dear" + citizenName + "\n")
						.append("Your application " + appEntity.getApplicationNumber() + " of ")
						.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
						.append(" has been Rejected ").append("For more details please follow this link")
						.append(licenceApplicationstatusURL).append("Thanks and Regards,\n")
						.append("A.P. Transport Department");
			}
			break;
		case DL_EXPIRED:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("<we have received your application " + appEntity.getApplicationNumber() + "of ")
						.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
						.append("  , You have successfully booked a test slot on ").append(getSlotDetail(appEntity))
						.append("\n Thanks and Regards \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append(" Your application " + appEntity.getApplicationNumber() + " of ")
						.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
						.append(" has been approved.").append(" You can download the Driving License via this link ")
						.append(licenceApplicationstatusURL).append("\n Thanks and Regards \n RTA Andhra Pradesh \n");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_REJECTED.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n ").append("Your application ")
						.append(appEntity.getApplicationNumber()).append(" of ")
						.append((ServiceType.getServiceType(appEntity.getServiceCode()).getLabel()))
						.append(" has been Rejected ").append(" For more details please follow this link ")
						.append("licenceApplicationstatusURL").append("\n Thanks and Regards \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n ")
						.append("You have successfully passed the driver license test held on ")
						.append(getSlotDetail(appEntity)).append(". Your application Number is ")
						.append(appEntity.getApplicationNumber()).append(" For more details please follow this link ")
						.append(licenceApplicationstatusURL).append("\n Thanks and Regards \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("You did not pass in the driver license test held on ").append(getSlotDetail(appEntity))
						.append(". Your application Number is ").append(appEntity.getApplicationNumber())
						.append(" You can apply for retest via ").append(licenceApplicationstatusURL)
						.append(" For more details please follow this link <a href='")
						.append("licenceApplicationstatusURL").append(" Thanks and Regards \n RTA Andhra Pradesh \n");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS_WITH_ATTACHEMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("You have successfully passed the driving license test held on ")
						.append(getSlotDetail(appEntity)).append(". Your application Number is ")
						.append(appEntity.getApplicationNumber()).append(" For more details please follow this link ")
						.append(licenceApplicationstatusURL).append(" Thanks and Regards \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_FAIL_WITH_ATTACHEMENT.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("You did not pass in the driver license test held on ").append(getSlotDetail(appEntity))
						.append(". Your application Number is ").append(appEntity.getApplicationNumber())
						.append(" You can apply for retest via ").append(licenceApplicationstatusURL)
						.append(" For more details please follow this link ").append("licenceApplicationstatusURL")
						.append(" Thanks and Regards \n RTA Andhra Pradesh");
			}
			break;
		case DL_SUSU_CANC:
			if (activityType.equalsIgnoreCase(MessageType.CCO_RASIE_SUSPENSION.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("CCO has initiated Suspension/Cancellation against your Driving License ")
						.append(dlNumber).append(", and issued showcause notice against your Driving License")
						.append(dlNumber)
						.append(". Respond to show cause notice by appearing before AO within 15 days after receiving the notice.")
						.append(" Thanks and Regards \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_APPROVED_SUSPENSION.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("AO has approved Suspension request against your Driving License ").append(dlNumber)
						.append(" and your will be suspended ")
						.append(DateUtil.getDateAsString(System.currentTimeMillis())).append(" to ")
						.append(DateUtil.getDateAsString(DateUtil.addDays(System.currentTimeMillis(), 15)))
						.append(" Please apply for revocation after the suspension period")
						.append(" Thanks and Regards \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_REJECT_SUSPENSION.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("AO has rejected Suspension request against your Driving License ").append(dlNumber)
						.append(" Thanks and Regards \n RTA Andhra Pradesh");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_APPROVED_CANCELATION.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n ")
						.append("AO has approved Cancellation request against your Driving License ").append(dlNumber)
						.append(", and your Driving License will be cancelled.")
						.append(" Thanks and Regards \n RTA Andhra Pradesh ");
			} else if (activityType.equalsIgnoreCase(MessageType.AO_REJECT_CANCELATION.getLabel())) {
				msgContent.append("Dear " + citizenName + ",\n")
						.append("AO has rejected Cancellation request against your Driving License ").append(dlNumber)
						.append(" Thanks and Regards \n RTA Andhra Pradesh");
			}
		}
		return msgContent.toString();
	}

	@Transactional
	@Override
	public CustMsgModel getLicenceCustInfo(Status status, ApplicationEntity appEntity, String activityType) {
		CustMsgModel custMsgModel = new CustMsgModel();
		ObjectMapper mapper = new ObjectMapper();
		ApplicationFormDataEntity entity = null;
		String citizenName = null;
		String contactNo = null;
		String emailId = null;

		String dlNumber = "";
		custMsgModel.setCommunicationConfig(getCommunicationConfig());
		String username = null;
		if (ServiceCategory.LL_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())
				|| ServiceCategory.DL_CATEGORY.getCode().equalsIgnoreCase(appEntity.getServiceCategory())) {
			if (ServiceType.LL_FRESH.getCode().equalsIgnoreCase(appEntity.getServiceCode())
					|| ServiceType.LL_RETEST.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
				entity = applicationFormDataDAO.getApplicationFormData(appEntity.getApplicationId(),
						getFormCodeType(appEntity.getServiceCode()));
				try {
					LLRegistrationModel llRegistrationModel = mapper.readValue(entity.getFormData(),
							LLRegistrationModel.class);
					emailId = llRegistrationModel.getEmailId();
					contactNo = llRegistrationModel.getMobileNo();
					username = llRegistrationModel.getDisplayName();
					citizenName = llRegistrationModel.getDisplayName();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// Get license holder detail
				try {
					RegLicenseServiceResponseModel<LicenseHolderDtlsModel> responseBody = registrationLicenseService
							.getLicenseHolderDtls(appEntity.getLoginHistory().getAadharNumber(), "");
					if (responseBody.getHttpStatus() == HttpStatus.OK) {
						LicenseHolderDtlsModel licenseHolderDtlsModel = responseBody.getResponseBody();
						emailId = licenseHolderDtlsModel.getEmail();
						contactNo = licenseHolderDtlsModel.getMobileNo();
						username = licenseHolderDtlsModel.getDisplayName();
						citizenName = licenseHolderDtlsModel.getDisplayName();
						if (licenseHolderDtlsModel.getLicenceIdentitiesDetails() != null)
							dlNumber = licenseHolderDtlsModel.getLicenceIdentitiesDetails().getDlNumber();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		custMsgModel.setMailContent(getLicenceMailContent(status, appEntity, citizenName, activityType, dlNumber));
		custMsgModel.setSubject(getLicenceMailSubject(appEntity, activityType));
		// custMsgModel.setTo(emailId);
		custMsgModel.setTo("ankur.goel@kelltontech.com");
		custMsgModel.setCitizenName(citizenName);
		// custMsgModel.setMobileNo(contactNo);
		custMsgModel.setMobileNo("9717055017");
		custMsgModel.setSmsMsg(getLicenceMsgContent(status, appEntity, citizenName, activityType, dlNumber));
		List<String> attachmentList = null;
		StringBuffer attacehmentUrlfrontEnd = new StringBuffer(attachementUrl);
		attacehmentUrlfrontEnd.append("test_1487498682.pdf");// Just for test
		if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
			attachmentList = getAttachmentsFrmURL(attacehmentUrlfrontEnd.toString() + "," + "test_1487498682.pdf");

		} else if (activityType.equalsIgnoreCase(MessageType.AO_RTO_CCO_APPROVED.getLabel())) {
			attachmentList = getAttachmentsFrmURL(attacehmentUrlfrontEnd.toString() + "," + "test_1487498682.pdf");

		}

		switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
		case DL_DUPLICATE:
		case LL_DUPLICATE:
			attachmentList
					.addAll(getAttachmentsFrmURL(attacehmentUrlfrontEnd.toString() + "," + "test_1487498682.pdf"));
			break;

		case DL_ENDORSMENT:
			if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS.getLabel())) {
				attachmentList = getAttachmentsFrmURL(attacehmentUrlfrontEnd.toString() + "," + "test_1487498682.pdf");

			}
			break;

		case DL_EXPIRED:
			if (activityType.equalsIgnoreCase(MessageType.PAYMENT.getLabel())) {
				attachmentList
						.addAll(getAttachmentsFrmURL(attacehmentUrlfrontEnd.toString() + "," + "test_1487498682.pdf"));
			} else if (activityType.equalsIgnoreCase(MessageType.TEST_RESULT_PASS_WITH_ATTACHEMENT.getLabel())) {
				attachmentList = getAttachmentsFrmURL(attacehmentUrlfrontEnd.toString() + "," + "test_1487498682.pdf");

			}
			break;
		}
		custMsgModel.setAttachments(attachmentList);
		return custMsgModel;
	}

	/**
	 * test code
	 */
	@Autowired
	private ApplicationDAO applicationDAO;

	@Override
	@Transactional
	public CustMsgModel checkCommunication(String applicationNumber) {
		ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
		CustMsgModel customerModel = getLicenceCustInfo(Status.APPROVED, appEntity,
				MessageType.TEST_RESULT_PASS.getLabel());
		sendMsg(CommunicationServiceImpl.SEND_SMS_EMAIL, customerModel);
		return customerModel;
	}

	/*
	 * private String getSlotDate(ApplicationEntity appEntity) { String testDate
	 * = ""; List<SlotApplicationsEntity> slotApplicationsEntities =
	 * slotDAO.getBookedSlot(appEntity.getApplicationId(),
	 * appEntity.getIteration()); if (slotApplicationsEntities != null) {
	 * SlotApplicationsEntity applicationsEntit =
	 * slotApplicationsEntities.get(0); SlotEntity slotEntity =
	 * applicationsEntit.getSlot(); testDate =
	 * DateUtil.extractDateAsString(slotEntity.getScheduledTime()); } return
	 * testDate; }
	 */

	private String getSlotDetail(ApplicationEntity applicationEntity) {
		StringBuffer slotDetail = new StringBuffer();
		try {
			List<SlotModel> slotModels = slotService.getSlotBookingDetails(applicationEntity.getApplicationNumber());
			if (slotModels != null) {
				SlotModel slotModel = slotModels.get(0);
				slotDetail.append(DateUtil.extractDateAsString(slotModel.getScheduledTime())).append(" at ")
						.append(slotModel.getRtaOfficeModel().getAddress());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return slotDetail.toString();
	}

	private void trustSSL() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Activate the new trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
		}
	}
}

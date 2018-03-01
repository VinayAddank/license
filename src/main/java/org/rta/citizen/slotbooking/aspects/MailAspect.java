package org.rta.citizen.slotbooking.aspects;

import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.ReceiptModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.communication.CustMsgModel;
import org.rta.citizen.common.service.communication.CommunicationService;
import org.rta.citizen.common.service.communication.CommunicationServiceImpl;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Aspect
@Component
public class MailAspect {

    private static final Logger log = Logger.getLogger(MailAspect.class);

    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @AfterReturning(pointcut = "@annotation(org.rta.citizen.slotbooking.aspects.Notifiable)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, ResponseModel<ReceiptModel> result) {
        try {
            if (result != null) {
                if (result.getStatus() != null && result.getStatus().equalsIgnoreCase(ResponseModel.SUCCESS)) {
                    ReceiptModel receipt = result.getData();
                    ServiceType serviceType = ServiceType.getServiceType(receipt.getServiceType());
                    String applicationNumber = receipt.getAppNumber();
                    RTAOfficeModel officeModel = receipt.getRtaOfficeModel();
                    boolean emailExists = false;
                    boolean mobileExists = false;
                    switch (serviceType) {
                    case AADHAR_SEED_DL:
                        break;
                    case AADHAR_SEED_RC:
                        break;
                    case ADDRESS_CHANGE:
                        break;
                    case ALTERATION_AGENCY_SIGNUP:
                        break;
                    case BODYBUILDER_SIGNUP:
                        break;
                    case DEALER_SIGNUP:
                        break;
                    case DEFAULT:
                        break;
                    case DE_IMPORTED_VEHICLE:
                        break;
                    case DE_NOC_CHANGE_OF_ADDRESS:
                        break;
                    case DE_NOC_TRANSFER_OF_OWNERSHIP:
                        break;
                    case DE_OTHER_STATE_NEW_VEHICLE_TR_GENERATED:
                        break;
                    case DE_TRAILER:
                        break;
                    case DL_BADGE:
                        break;
                    case DL_CHANGEADDRS_OS:
                        break;
                    case DL_CHANGE_ADDRESS:
                        break;
                    case DL_DLINFO:
                        break;
                    case DL_DUPLICATE:
                        break;
                    case DL_ENDORSMENT:
                        break;
                    case DL_EXPIRED:
                        break;
                    case DL_FOREIGN_CITIZEN:
                        break;
                    case DL_FRESH:
                        break;
                    case DL_INT_PERMIT:
                        break;
                    case DL_MILITRY:
                        break;
                    case DL_RENEWAL:
                        break;
                    case DL_RETEST:
                        break;
                    case DL_REVO_SUS:
                        break;
                    case DL_SURRENDER:
                        break;
                    case DL_SUSU_CANC:
                        break;
                    case DRIVING_INSTITUTE:
                        break;
                    case DUPLICATE_REGISTRATION:
                        break;
                    case FC_FRESH:
                    case FC_RENEWAL:
                    case FC_OTHER_STATION:
                    case FC_REVOCATION_CFX:
                    case DIFFERENTIAL_TAX:
                    case REGISTRATION_RENEWAL: {
                        ApplicationEntity appEntity = applicationDAO.getApplication(applicationNumber);
                        RegistrationServiceResponseModel<CustomerDetailsRequestModel> custModel = null;
                        try {
                            custModel = registrationService
                                    .getCustomerDetails(appEntity.getLoginHistory().getVehicleRcId());
                            if (custModel != null && custModel.getHttpStatus() == HttpStatus.OK) {
                                CustMsgModel msgModel = new CustMsgModel();
                                CustomerDetailsRequestModel cdrm = custModel.getResponseBody();
                                String emailId = cdrm.getEmailid();
                                msgModel.setCommunicationConfig(communicationService.getCommunicationConfig());
                                if (!StringsUtil.isNullOrEmpty(emailId)) {
                                    msgModel.setSubject(getSubject(serviceType));
                                    msgModel.setTo(emailId);
                                    msgModel.setMailContent(getMailContent(serviceType, receipt.getSlotModel(),
                                            officeModel, cdrm.getFirst_name(), appEntity.getApplicationNumber()));
                                    emailExists = true;
                                }
                                String mobileNumber = cdrm.getMobileNumber();
                                if (!StringsUtil.isNullOrEmpty(mobileNumber)) {
                                    msgModel.setMobileNo(mobileNumber);
                                    msgModel.setSmsMsg(getMsgContent(serviceType, receipt.getSlotModel(), officeModel,
                                            cdrm.getFirst_name()));
                                    mobileExists = true;
                                }
                                if (emailExists) {
                                    communicationService.sendMsg(CommunicationServiceImpl.SEND_EMAIL, msgModel);
                                }
                                if (mobileExists) {
                                    communicationService.sendMsg(CommunicationServiceImpl.SEND_SMS, msgModel);
                                }
                            }
                        } catch (UnauthorizedException e) {
                            log.error("SLOT NOTIFICATION : error getting customer details for application number : "
                                    + applicationNumber);
                            return;
                        } catch (HttpClientErrorException e) {
                            log.error("SLOT NOTIFICATION : error getting customer details for application number : "
                                    + applicationNumber);
                            return;
                        }
                    }
                        break;
                    case FC_RE_INSPECTION_SB:
                        break;
                    case FINANCIER_SIGNUP:
                        break;
                    case FRESH_RC_FINANCIER:
                        break;
                    case HAZARDOUS_VEH_TRAIN_INST:
                        break;
                    case HPA:
                        break;
                    case HPT:
                        break;
                    case LL_DUPLICATE:
                        break;
                    case LL_ENDORSEMENT:
                        break;
                    case LL_FRESH:
                        break;
                    case LL_RETEST:
                        break;
                    case MEDICAL_PRACTITIONER:
                        break;
                    case NOC_CANCELLATION:
                        break;
                    case NOC_ISSUE:
                        break;
                    case OWNERSHIP_TRANSFER_AUCTION:
                        break;
                    case OWNERSHIP_TRANSFER_DEATH:
                        break;
                    case OWNERSHIP_TRANSFER_SALE:
                        break;
                    case PERMIT_FRESH:
                        break;
                    case PERMIT_RENEWAL:
                        break;
                    case PERMIT_RENEWAL_AUTH_CARD:
                        break;
                    case PERMIT_REPLACEMENT_VEHICLE:
                        break;
                    case PERMIT_SURRENDER:
                        break;
                    case PERMIT_VARIATIONS:
                        break;
                    case PUC_USER_SIGNUP:
                        break;
                    case REGISTRATION_CANCELLATION:
                        break;
                    case REGISTRATION_SUS_CANCELLATION:
                        break;
                    case SUSPENSION_REVOCATION:
                        break;
                    case THEFT_INTIMATION:
                        break;
                    case VEHICLE_ATLERATION:
                        break;
                    case VEHICLE_REASSIGNMENT:
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("SLOT NOTIFICATION : error sending mail : ", e);
        }
    }

    private String getSubject(ServiceType serviceType) {
        StringBuilder content = new StringBuilder();
        switch (serviceType) {
        case AADHAR_SEED_DL:
            break;
        case AADHAR_SEED_RC:
            break;
        case ADDRESS_CHANGE:
            break;
        case ALTERATION_AGENCY_SIGNUP:
            break;
        case BODYBUILDER_SIGNUP:
            break;
        case DEALER_SIGNUP:
            break;
        case DEFAULT:
            break;
        case DE_IMPORTED_VEHICLE:
            break;
        case DE_NOC_CHANGE_OF_ADDRESS:
            break;
        case DE_NOC_TRANSFER_OF_OWNERSHIP:
            break;
        case DE_OTHER_STATE_NEW_VEHICLE_TR_GENERATED:
            break;
        case DE_TRAILER:
            break;
        case DIFFERENTIAL_TAX:
            break;
        case DL_BADGE:
            break;
        case DL_CHANGEADDRS_OS:
            break;
        case DL_CHANGE_ADDRESS:
            break;
        case DL_DLINFO:
            break;
        case DL_DUPLICATE:
            break;
        case DL_ENDORSMENT:
            break;
        case DL_EXPIRED:
            break;
        case DL_FOREIGN_CITIZEN:
            break;
        case DL_FRESH:
            break;
        case DL_INT_PERMIT:
            break;
        case DL_MILITRY:
            break;
        case DL_RENEWAL:
            break;
        case DL_RETEST:
            break;
        case DL_REVO_SUS:
            break;
        case DL_SURRENDER:
            break;
        case DL_SUSU_CANC:
            break;
        case DRIVING_INSTITUTE:
            break;
        case DUPLICATE_REGISTRATION:
            break;
        case FC_FRESH:

            break;
        case FC_OTHER_STATION:
            break;
        case FC_RENEWAL:
            break;
        case FC_REVOCATION_CFX:
            break;
        case FC_RE_INSPECTION_SB:
            break;
        case FINANCIER_SIGNUP:
            break;
        case FRESH_RC_FINANCIER:
            break;
        case HAZARDOUS_VEH_TRAIN_INST:
            break;
        case HPA:
            break;
        case HPT:
            break;
        case LL_DUPLICATE:
            break;
        case LL_ENDORSEMENT:
            break;
        case LL_FRESH:
            break;
        case LL_RETEST:
            break;
        case MEDICAL_PRACTITIONER:
            break;
        case NOC_CANCELLATION:
            break;
        case NOC_ISSUE:
            break;
        case OWNERSHIP_TRANSFER_AUCTION:
            break;
        case OWNERSHIP_TRANSFER_DEATH:
            break;
        case OWNERSHIP_TRANSFER_SALE:
            break;
        case PERMIT_FRESH:
            break;
        case PERMIT_RENEWAL:
            break;
        case PERMIT_RENEWAL_AUTH_CARD:
            break;
        case PERMIT_REPLACEMENT_VEHICLE:
            break;
        case PERMIT_SURRENDER:
            break;
        case PERMIT_VARIATIONS:
            break;
        case PUC_USER_SIGNUP:
            break;
        case REGISTRATION_CANCELLATION:
            break;
        case REGISTRATION_RENEWAL:
            break;
        case REGISTRATION_SUS_CANCELLATION:
            break;
        case SUSPENSION_REVOCATION:
            break;
        case THEFT_INTIMATION:
            break;
        case VEHICLE_ATLERATION:
            break;
        case VEHICLE_REASSIGNMENT:
            break;
        }
        content.append("You have booked a slot for MVI Inspection.");
        return content.toString();
    }

    private String getMailContent(ServiceType serviceType, List<SlotModel> slots, RTAOfficeModel rtaOffice,
            String custName, String appNumber) {
        StringBuilder content = new StringBuilder();
        switch (serviceType) {
        case AADHAR_SEED_DL:
            break;
        case AADHAR_SEED_RC:
            break;
        case ADDRESS_CHANGE:
            break;
        case ALTERATION_AGENCY_SIGNUP:
            break;
        case BODYBUILDER_SIGNUP:
            break;
        case DEALER_SIGNUP:
            break;
        case DEFAULT:
            break;
        case DE_IMPORTED_VEHICLE:
            break;
        case DE_NOC_CHANGE_OF_ADDRESS:
            break;
        case DE_NOC_TRANSFER_OF_OWNERSHIP:
            break;
        case DE_OTHER_STATE_NEW_VEHICLE_TR_GENERATED:
            break;
        case DE_TRAILER:
            break;
        case DIFFERENTIAL_TAX:
            content.append("<table><tr><td>Dear ").append(custName)
                    .append(",</td></tr><tr></tr><tr><td>You have booked a slot for ").append(appNumber)
                    .append(" for MVI Inspection for Differential Tax Service on ")
                    .append(DateUtil.extractDateAsString(slots.get(0).getScheduledDate())).append(" at ")
                    .append(DateUtil.extractTimeAsString(
                            slots.get(0).getScheduledTime()/* + 19800 */))
                    .append(" at ").append(rtaOffice.getName()).append(", ").append(rtaOffice.getAddress())
                    .append(".</td></tr><tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
            break;
        case DL_BADGE:
            break;
        case DL_CHANGEADDRS_OS:
            break;
        case DL_CHANGE_ADDRESS:
            break;
        case DL_DLINFO:
            break;
        case DL_DUPLICATE:
            break;
        case DL_ENDORSMENT:
            break;
        case DL_EXPIRED:
            break;
        case DL_FOREIGN_CITIZEN:
            break;
        case DL_FRESH:
            break;
        case DL_INT_PERMIT:
            break;
        case DL_MILITRY:
            break;
        case DL_RENEWAL:
            break;
        case DL_RETEST:
            break;
        case DL_REVO_SUS:
            break;
        case DL_SURRENDER:
            break;
        case DL_SUSU_CANC:
            break;
        case DRIVING_INSTITUTE:
            break;
        case DUPLICATE_REGISTRATION:
            break;
        case FC_FRESH:
        case FC_OTHER_STATION:
        case FC_RENEWAL:
            content.append("<table><tr><td>Dear ").append(custName)
                    .append(",</td></tr><tr></tr><tr><td>You have booked a slot for ").append(appNumber)
                    .append(" for MVI Inspection for Fitness on ")
                    .append(DateUtil.extractDateAsString(slots.get(0).getScheduledDate())).append(" at ")
                    .append(DateUtil.extractTimeAsString(
                            slots.get(0).getScheduledTime()/* + 19800 */))
                    .append(" at ").append(rtaOffice.getName()).append(", ").append(rtaOffice.getAddress())
                    .append(".</td></tr><tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
            break;
        case FC_REVOCATION_CFX:
            content.append("<table><tr><td>Dear ").append(custName)
                    .append(",</td></tr><tr></tr><tr><td>You have booked a slot for ").append(appNumber)
                    .append(" for MVI Inspection for Fitness revocation on ")
                    .append(DateUtil.extractDateAsString(slots.get(0).getScheduledDate())).append(" at ")
                    .append(DateUtil.extractTimeAsString(
                            slots.get(0).getScheduledTime()/* + 19800 */))
                    .append(" at ").append(rtaOffice.getName()).append(", ").append(rtaOffice.getAddress())
                    .append(".</td></tr><tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
            break;
        case FC_RE_INSPECTION_SB:
            break;
        case FINANCIER_SIGNUP:
            break;
        case FRESH_RC_FINANCIER:
            break;
        case HAZARDOUS_VEH_TRAIN_INST:
            break;
        case HPA:
            break;
        case HPT:
            break;
        case LL_DUPLICATE:
            break;
        case LL_ENDORSEMENT:
            break;
        case LL_FRESH:
            break;
        case LL_RETEST:
            break;
        case MEDICAL_PRACTITIONER:
            break;
        case NOC_CANCELLATION:
            break;
        case NOC_ISSUE:
            break;
        case OWNERSHIP_TRANSFER_AUCTION:
            break;
        case OWNERSHIP_TRANSFER_DEATH:
            break;
        case OWNERSHIP_TRANSFER_SALE:
            break;
        case PERMIT_FRESH:
            break;
        case PERMIT_RENEWAL:
            break;
        case PERMIT_RENEWAL_AUTH_CARD:
            break;
        case PERMIT_REPLACEMENT_VEHICLE:
            break;
        case PERMIT_SURRENDER:
            break;
        case PERMIT_VARIATIONS:
            break;
        case PUC_USER_SIGNUP:
            break;
        case REGISTRATION_CANCELLATION:
            break;
        case REGISTRATION_RENEWAL:
            content.append("<table><tr><td>Dear ").append(custName)
                    .append(",</td></tr><tr></tr><tr><td>You have booked a slot for ").append(appNumber)
                    .append(" for MVI Inspection for Registration Renewal Service on ")
                    .append(DateUtil.extractDateAsString(slots.get(0).getScheduledDate())).append(" at ")
                    .append(DateUtil.extractTimeAsString(
                            slots.get(0).getScheduledTime()/* + 19800 */))
                    .append(" at ").append(rtaOffice.getName()).append(", ").append(rtaOffice.getAddress())
                    .append(".</td></tr><tr></tr><tr></tr><tr><td>Thank You </td></tr><tr><td>AP_Road Transport</td></tr></table>");
            break;
        case REGISTRATION_SUS_CANCELLATION:
            break;
        case SUSPENSION_REVOCATION:
            break;
        case THEFT_INTIMATION:
            break;
        case VEHICLE_ATLERATION:
            break;
        case VEHICLE_REASSIGNMENT:
            break;
        }
        return content.toString();
    }

    private String getMsgContent(ServiceType serviceType, List<SlotModel> slots, RTAOfficeModel rtaOffice,
            String custName) {
        StringBuilder content = new StringBuilder();
        switch (serviceType) {
        case AADHAR_SEED_DL:
            break;
        case AADHAR_SEED_RC:
            break;
        case ADDRESS_CHANGE:
            break;
        case ALTERATION_AGENCY_SIGNUP:
            break;
        case BODYBUILDER_SIGNUP:
            break;
        case DEALER_SIGNUP:
            break;
        case DEFAULT:
            break;
        case DE_IMPORTED_VEHICLE:
            break;
        case DE_NOC_CHANGE_OF_ADDRESS:
            break;
        case DE_NOC_TRANSFER_OF_OWNERSHIP:
            break;
        case DE_OTHER_STATE_NEW_VEHICLE_TR_GENERATED:
            break;
        case DE_TRAILER:
            break;
        case DIFFERENTIAL_TAX:
            break;
        case DL_BADGE:
            break;
        case DL_CHANGEADDRS_OS:
            break;
        case DL_CHANGE_ADDRESS:
            break;
        case DL_DLINFO:
            break;
        case DL_DUPLICATE:
            break;
        case DL_ENDORSMENT:
            break;
        case DL_EXPIRED:
            break;
        case DL_FOREIGN_CITIZEN:
            break;
        case DL_FRESH:
            break;
        case DL_INT_PERMIT:
            break;
        case DL_MILITRY:
            break;
        case DL_RENEWAL:
            break;
        case DL_RETEST:
            break;
        case DL_REVO_SUS:
            break;
        case DL_SURRENDER:
            break;
        case DL_SUSU_CANC:
            break;
        case DRIVING_INSTITUTE:
            break;
        case DUPLICATE_REGISTRATION:
            break;
        case FC_FRESH:
        case FC_OTHER_STATION:
        case FC_RENEWAL:
            content.append("Dear ").append(custName)
                    .append(",You have booked a slot for MVI Inspection for Fitness on ")
                    .append(DateUtil.extractDateAsString(slots.get(0).getScheduledDate())).append(" at ")
                    .append(DateUtil.extractTimeAsString(
                            slots.get(0).getScheduledTime()/* + 19800 */))
                    .append(" at ").append(rtaOffice.getName()).append(", ").append(rtaOffice.getAddress())
                    .append(".Thank You, AP_Road Transport");
            break;
        case FC_REVOCATION_CFX:
            break;
        case FC_RE_INSPECTION_SB:
            break;
        case FINANCIER_SIGNUP:
            break;
        case FRESH_RC_FINANCIER:
            break;
        case HAZARDOUS_VEH_TRAIN_INST:
            break;
        case HPA:
            break;
        case HPT:
            break;
        case LL_DUPLICATE:
            break;
        case LL_ENDORSEMENT:
            break;
        case LL_FRESH:
            break;
        case LL_RETEST:
            break;
        case MEDICAL_PRACTITIONER:
            break;
        case NOC_CANCELLATION:
            break;
        case NOC_ISSUE:
            break;
        case OWNERSHIP_TRANSFER_AUCTION:
            break;
        case OWNERSHIP_TRANSFER_DEATH:
            break;
        case OWNERSHIP_TRANSFER_SALE:
            break;
        case PERMIT_FRESH:
            break;
        case PERMIT_RENEWAL:
            break;
        case PERMIT_RENEWAL_AUTH_CARD:
            break;
        case PERMIT_REPLACEMENT_VEHICLE:
            break;
        case PERMIT_SURRENDER:
            break;
        case PERMIT_VARIATIONS:
            break;
        case PUC_USER_SIGNUP:
            break;
        case REGISTRATION_CANCELLATION:
            break;
        case REGISTRATION_RENEWAL:
            content.append("Dear ").append(custName)
                    .append(",You have booked a slot for MVI Inspection for Registration Renewal Service on ")
                    .append(DateUtil.extractDateAsString(slots.get(0).getScheduledDate())).append(" at ")
                    .append(DateUtil.extractTimeAsString(
                            slots.get(0).getScheduledTime()/* + 19800 */))
                    .append(" at ").append(rtaOffice.getName()).append(", ").append(rtaOffice.getAddress())
                    .append(".Thank You, AP_Road Transport");
            break;
        case REGISTRATION_SUS_CANCELLATION:
            break;
        case SUSPENSION_REVOCATION:
            break;
        case THEFT_INTIMATION:
            break;
        case VEHICLE_ATLERATION:
            break;
        case VEHICLE_REASSIGNMENT:
            break;
        }
        return content.toString();
    }

    /*
     * @Around("@annotation(org.rta.citizen.slotbooking.aspects.Notifiable)")
     * public Object aroundSampleCreation(ProceedingJoinPoint
     * proceedingJoinPoint,String sampleName) throws Throwable {
     * log.info("A request was issued for a sample name: "+sampleName);
     * sampleName = sampleName+"!"; Sample sample = (Sample)
     * proceedingJoinPoint.proceed(new Object[] {sampleName});
     * sample.setName(sample.getName().toUpperCase()); return sample; }
     */
}

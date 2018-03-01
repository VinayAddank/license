/**
 * 
 */
package org.rta.citizen.common.utils;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.SlotCategory;

/**
 * @author arun.verma
 *
 */
public class ServiceUtil {

	private static final Logger log = Logger.getLogger(ServiceUtil.class);
    
    public static ServiceCategory getServiceCategory(ServiceType service){
        if(ObjectsUtil.isNull(service)){
            log.error("ServiceType is null returning null value");
            return null;
        }
        //--- regstration services -------------
        if(service == ServiceType.OWNERSHIP_TRANSFER_SALE || service == ServiceType.NOC_ISSUE || service == ServiceType.NOC_CANCELLATION ||
        service == ServiceType.HPT || service == ServiceType.HPA || service == ServiceType.ADDRESS_CHANGE || service == ServiceType.DUPLICATE_REGISTRATION || 
        service == ServiceType.REGISTRATION_RENEWAL || service == ServiceType.SUSPENSION_REVOCATION || service == ServiceType.VEHICLE_ATLERATION || service == ServiceType.FRESH_RC_FINANCIER || 
        service == ServiceType.VEHICLE_REASSIGNMENT || service == ServiceType.REGISTRATION_SUS_CANCELLATION || service == ServiceType.THEFT_INTIMATION || service == ServiceType.OWNERSHIP_TRANSFER_DEATH || 
        service == ServiceType.OWNERSHIP_TRANSFER_AUCTION || service == ServiceType.REGISTRATION_CANCELLATION || service == ServiceType.DIFFERENTIAL_TAX
        || service == ServiceType.PAY_TAX ){
            return ServiceCategory.REG_CATEGORY;
        }
        //----- user registration ------------
        else if(service == ServiceType.FINANCIER_SIGNUP || service == ServiceType.BODYBUILDER_SIGNUP ||service == ServiceType.DEALER_SIGNUP ||service == ServiceType.PUC_USER_SIGNUP 
                ||service == ServiceType.ALTERATION_AGENCY_SIGNUP ||service == ServiceType.DRIVING_INSTITUTE ||service == ServiceType.HAZARDOUS_VEH_TRAIN_INST ||service == ServiceType.MEDICAL_PRACTITIONER){
            return ServiceCategory.REG_CATEGORY;
        }
        //-------LL---------------------------
        else if(service == ServiceType.LL_FRESH || service == ServiceType.LL_ENDORSEMENT || service == ServiceType.LL_RETEST || service == ServiceType.LL_DUPLICATE){
            return ServiceCategory.LL_CATEGORY;
        }
        //---------DL-------------------------
        else if(service == ServiceType.DL_FRESH || service == ServiceType.DL_DUPLICATE || service == ServiceType.DL_RENEWAL || service == ServiceType.DL_CHANGE_ADDRESS ||
                service == ServiceType.DL_ENDORSMENT || service == ServiceType.DL_RETEST || service == ServiceType.DL_SURRENDER || service == ServiceType.DL_BADGE ||
                service == ServiceType.DL_DLINFO || service == ServiceType.DL_REVO_SUS || service == ServiceType.DL_EXPIRED || service == ServiceType.DL_INT_PERMIT ||
                service == ServiceType.DL_MILITRY || service == ServiceType.DL_FOREIGN_CITIZEN || service == ServiceType.DL_CHANGEADDRS_OS || service == ServiceType.DL_EXPIRED  || service == ServiceType.DL_SUSU_CANC){
            return ServiceCategory.DL_CATEGORY;
        }
        //------Fitness services-----------------
        //------Permit services-----------------
        else if(service == ServiceType.PERMIT_FRESH || service == ServiceType.PERMIT_RENEWAL || service == ServiceType.PERMIT_SURRENDER || service == ServiceType.PERMIT_RENEWAL_AUTH_CARD
                || service == ServiceType.PERMIT_REPLACEMENT_VEHICLE || service == ServiceType.PERMIT_VARIATIONS
                || service == ServiceType.FC_FRESH || service == ServiceType.FC_RENEWAL || service == ServiceType.FC_RE_INSPECTION_SB || service == ServiceType.FC_REVOCATION_CFX
                || service == ServiceType.FC_OTHER_STATION|| service == ServiceType.FC_ISSUE_CFX) { 
        	return ServiceCategory.PERMIT_FITNESS_CATEGORY;
        }
        //-----RC Aadhar Seeding -----------------
        else if(service == ServiceType.AADHAR_SEED_RC){
            return ServiceCategory.RC_AADHAR_SEEDING;
        }
        //-----DL Aadhar Seeding -----------------
        else if(service == ServiceType.AADHAR_SEED_DL){
            return ServiceCategory.DL_AADHAR_SEEDING;
        }
      //-----Stoppage Tax and Stoppage Tax Revocation -----------------
        else if(service == ServiceType.STOPPAGE_TAX || service == ServiceType.STOPPAGE_TAX_REVOCATION){
            return ServiceCategory.TAX_CATEGORY;
        }
        log.error("for ServiceType : " + service + " returning null value");
        return null;
    }
    
    public static SlotCategory getSlotCategory(ServiceType service){
        if(ObjectsUtil.isNull(service)){
            log.error("ServiceType is null returning null value");
            return null;
        }
        //--- regstration services -------------
        if(service == ServiceType.OWNERSHIP_TRANSFER_SALE || service == ServiceType.NOC_ISSUE || service == ServiceType.NOC_CANCELLATION ||
        service == ServiceType.HPT || service == ServiceType.HPA || service == ServiceType.ADDRESS_CHANGE || service == ServiceType.DUPLICATE_REGISTRATION || 
        service == ServiceType.REGISTRATION_RENEWAL || service == ServiceType.SUSPENSION_REVOCATION || service == ServiceType.VEHICLE_ATLERATION || service == ServiceType.FRESH_RC_FINANCIER || 
        service == ServiceType.VEHICLE_REASSIGNMENT || service == ServiceType.REGISTRATION_SUS_CANCELLATION || service == ServiceType.THEFT_INTIMATION || service == ServiceType.OWNERSHIP_TRANSFER_DEATH || 
        service == ServiceType.OWNERSHIP_TRANSFER_AUCTION || service == ServiceType.REGISTRATION_CANCELLATION || service == ServiceType.DIFFERENTIAL_TAX || service == ServiceType.FINANCIER_SIGNUP || service == ServiceType.BODYBUILDER_SIGNUP ||service == ServiceType.DEALER_SIGNUP ||service == ServiceType.PUC_USER_SIGNUP 
        ||service == ServiceType.ALTERATION_AGENCY_SIGNUP ||service == ServiceType.DRIVING_INSTITUTE ||service == ServiceType.HAZARDOUS_VEH_TRAIN_INST ||service == ServiceType.MEDICAL_PRACTITIONER || service == ServiceType.FC_FRESH || service == ServiceType.FC_RENEWAL || service == ServiceType.FC_RE_INSPECTION_SB || service == ServiceType.FC_REVOCATION_CFX
        || service == ServiceType.FC_OTHER_STATION){
            return SlotCategory.FITNESS_TESTS;
        }
        //-------LL---------------------------
        else if(service == ServiceType.LL_FRESH || service == ServiceType.LL_ENDORSEMENT || service == ServiceType.LL_RETEST || service == ServiceType.LL_DUPLICATE){
            return SlotCategory.LL_CATEGORY;
        }
        
        //---------DL-------------------------
        else if(service == ServiceType.DL_FRESH || service == ServiceType.DL_DUPLICATE || service == ServiceType.DL_RENEWAL || service == ServiceType.DL_CHANGE_ADDRESS ||
                service == ServiceType.DL_ENDORSMENT || service == ServiceType.DL_RETEST || service == ServiceType.DL_SURRENDER || service == ServiceType.DL_BADGE ||
                service == ServiceType.DL_DLINFO || service == ServiceType.DL_REVO_SUS || service == ServiceType.DL_SUSU_CANC || service == ServiceType.DL_EXPIRED || service == ServiceType.DL_INT_PERMIT ||
                service == ServiceType.DL_MILITRY || service == ServiceType.DL_FOREIGN_CITIZEN || service == ServiceType.DL_CHANGEADDRS_OS || service == ServiceType.DL_EXPIRED){
            return SlotCategory.DL_CATEGORY;
        }
        //------Permit services-----------------
        else if(service == ServiceType.PERMIT_FRESH || service == ServiceType.PERMIT_RENEWAL || service == ServiceType.PERMIT_SURRENDER || service == ServiceType.PERMIT_RENEWAL_AUTH_CARD) { 
            return null;
        }
        log.error("for ServiceType : " + service + " returning null value");
        return null;
    }
    
    public static boolean isFitnessService(ServiceType serviceType) {
        return serviceType == ServiceType.FC_FRESH || serviceType == ServiceType.FC_OTHER_STATION 
                || serviceType == ServiceType.FC_RE_INSPECTION_SB || serviceType == ServiceType.FC_RENEWAL 
                || serviceType == ServiceType.FC_REVOCATION_CFX;
    }
    
    public static boolean isUserRegistrationService(ServiceType service) {
        return service == ServiceType.FINANCIER_SIGNUP || service == ServiceType.BODYBUILDER_SIGNUP ||service == ServiceType.DEALER_SIGNUP ||service == ServiceType.PUC_USER_SIGNUP 
                ||service == ServiceType.ALTERATION_AGENCY_SIGNUP ||service == ServiceType.DRIVING_INSTITUTE ||service == ServiceType.HAZARDOUS_VEH_TRAIN_INST ||service == ServiceType.MEDICAL_PRACTITIONER;
    }
}

/**
 * 
 */
package org.rta.citizen.common.enums;

/**
 * @author arun.verma
 *
 */
public enum ServiceValidation {

    ANOTHER_SERVICE_EXISTS(800, "Another service exists"), SAME_SERVICE_EXIST_WITH_ANOTHER_USER(801, "Same Service has been initiated by another user on same unique key."),
    
    RC_EXPIRED(600, "Your RC is expired."), RC_SUSPENDED(601, "This RC is already suspended, Kindly go for revocation."), THEFT_OBJECTION_FOUND(602, "Theft/Objection found on this Vehicle"), 
    INSURANCE_EXPIRED(603, "Your Insurance is Expired."), FITNESS_EXPIRED(604, "Your Fitness is Expired."), 
    PUC_EXPIRED(605, "Your PUC is Expired."), NOC_ISSUED(606, "NOC is issued you cannot do any transaction, please cancel the issued NOC first"), 
    TAX_PENDING(607,"Tax is pending against this Vehicle, please pay the tax dues."),
    ONLY_TRANSPORT_VEHICLE_IS_ALLOWED(608, "Only Transport Vehicles are allowed for this service."), PERMIT_EXPIRED(609, "Your Permit is Expired."), 
    PERMIT_NOT_FOUND(610, "Permit Not Found."), HPA_FOUND(611, "HPA is Found."), HPA_NOT_FOUND(612, "HPA Not Found"), RC_NOT_EXPIRED(613, "RC Not Expired."),
    NOC_NOT_ISSUED(614, "NOC Yet Not Issued."), RC_CANCELLED(615, "Your RC is Cancelled."), RC_NOT_SUSPENDED(616, "Your RC is Not Suspended."), PERMIT_NOT_EXPIRED(617, "Your Permit is Not Expired."),
    PERMIT_TEMP_NOT_EXPIRED(618, "Your Temporary Permit is Not Expired."), PERMIT_NOT_APPLICABLE(619, "Permit Not Applicable on Your Vehicle."),
    FITNESS_ALREADY_EXISTS(620, "Fitness for this vehicle is already exists."), FITNESS_NOT_FOUND(620, "Fitness not found."), RC_FOUND(621, "RC Already Generated."),
    OFFLINE_FINANCED(622,"Vehicle has been financed offline."),PERMIT_VARIATION_NOT_APPLICABLE(623,"Permit variation is not allowed for the existing permit."),
    FITNESS_SUSPENDED(624,"Your Fitness is Suspended. Please use Fitness Revocation Service."),FITNESS_NOT_SUSPENDED(625,"Your Fitness hasn't been Suspended."),
    RC_OBJECTED(626, "Objection found on this RC."), RC_NOT_SUSPENDED_OBJECTED(627, "There is not any Suspension/Objection found on given RC."),
    DIFFERENTIAL_TAX_NOT_FOUND(628, "Differential Tax not found."),
    
    //DON'T USE 777 Code - Saurabh
    AADHAR_ALREADY_SEEDED_WITH_RC(700, "Aadhar already seeded with given RC."), RC_NOT_MATCHED_CHASSI(701, "Chassis number is not matching against the given RC number, please verify the details."),
    RC_MAPPED_WITH_DIFFERENT_AADHAR(702, "We found some discrepancy with the details please visit RTO Office."), AADHAR_NOT_FOUND_IN_RC(703, "Aadhar Not Found in RC."), AADHAR_MISMATCH_WITH_RC(704, "Aadhar Not Matched with RC."),
    AADHAAR_MATCH_WITH_RC(705, "We can not process Aadhaar Seeding Service, because there are 0% match data between Aadhar Details and RC Details"),
    PERMIT_AUTH_CARD_NOT_FOUND(736,"Vehicle not issued with authorization card previously."),
    PERMIT_AUTH_CARD_NOT_UNDER_RENEWAL(737,"Authorization Card not under renewal period, please apply during the renewal period."),
    PERMIT_AUTH_CARD_EXPIRED(738,"You are applying after the expiry period for which late fee will be collected."),
    PRIMARY_PERMIT_AUTH_CARD(739,"You cannot apply for Auth card renewal, Your permit is going to expire within one year. Please surrender existing permit and apply for new permit to continue with this service."),
    PERMIT_RENEWAL_AUTH_CARD_NOT_APPLICABLE(740,"Renewal of Authorization card is not applicable."),
    USER_ALREADY_EXITS(900, "User Already Exists"),
    
    APPLICATION_RE_INITIATED(1000, "Application Re-Initiated."), REJECTED_APP_EXIST(1001, "Rejected Application Exist with Same Credential."),
    
    TAX_DETAILS_NOT_FOUND(1100,"Can't retrieve Tax details for this Vehicle, Please check with support team."),
    TAX_NOT_EXPIRED(1101,"Tax hasn't been expired for this Vehicle."), GREEN_TAX_EXPIRED(1102,"Green Tax has been expired for this vehicle."),
    STOPPAGE_TAX_NOT_APLICABLE(1103,"Stoppage Tax hasn't been applicable for this Vehicle."), VEHICLE_IS_STOPPAGE_TAX(1104, "Vehicle is in Stoppage Tax."),
    VEHICLE_IS_NOT_STOPPAGE_TAX(1105, "Vehicle is not in Stoppage Tax."), TAX_EXPIRED(1106,"Tax has been expired for this Vehicle."),
    
    NOC_REQUIRED(1500, "Please Apply NOC !!!"),
    CFX_ISSUED(2000, "CFX note is issued against this vehicle !!!"),
	
	VEHICLE_REASSIGNMENT(2001, "Vehicle Reassignmnet is not applicable for this vehicle !!!"),
	FITNESS_RE_INSPECTION_REQUIRED(2002, "Please Apply Fitness Re-Inspection Service !!!"),
	FITNESS_INSPECTION_NOT_APPLICABLE(2003,"Fitness Re-Inspection Not Applicable!!!"),
	FRESH_RC_FINANCIER(2004,"No fresh rc request found"),
	AADHAR_CAN_NOT_SAME(2005,"Login aadhar number can not be same as registered aadhar number with respect to given rc in case of ownership transfer death."),
	RC_LOCKED(2006,"It seems you have done some recent transaction in last two days on offline system. Please try after two days."),
	RC_INCOMPLETE_DATA(2007,"Your vehicle details are not correct in our records kindly go to RTO office for correction with required documents.");
	
    private Integer code;
    private String value;

    ServiceValidation(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

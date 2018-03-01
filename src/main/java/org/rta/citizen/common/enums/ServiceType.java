package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.ObjectsUtil;

public enum ServiceType {

    OWNERSHIP_TRANSFER_SALE(1, "TRANSFER OF OWNERSHIP SALE", "OTS"),
    DEFAULT(3, "DEFAULT", "DF"), NOC_ISSUE(4, "ISSUE OF NOC", "NOC"), NOC_CANCELLATION(5, "CANCELLATION OF NOC", "CC"),
    HPT(6, "Hire Purchase Termination", "HPT"), HPA(7, "Hire Purchase Agreement", "HPA"), ADDRESS_CHANGE(8, "Change of Address", "AC"), 
    DUPLICATE_REGISTRATION(9, "Duplicate Registration", "DR"), REGISTRATION_RENEWAL(10, "Renewal of Registration", "RR"),
    SUSPENSION_REVOCATION(11, "Revocation of Suspension-Objection", "SR"),VEHICLE_ATLERATION(12, "Alteration of Vehicle", "VA"),
    FRESH_RC_FINANCIER(13, "Fresh RC for Financier", "FRF"),VEHICLE_REASSIGNMENT(14, "Reassignment of Vehicle", "VR"),
    REGISTRATION_SUS_CANCELLATION(15, "Suspension-Cancellation-Objection of Registration", "RSC"),THEFT_INTIMATION(16, "Theft Intimation/Revocation", "TI"),
    OWNERSHIP_TRANSFER_DEATH(17, "TRANSFER OF OWNERSHIP DEATH", "OTD"), OWNERSHIP_TRANSFER_AUCTION(18, "TRANSFER OF OWNERSHIP AUCTION", "OTA"),
    REGISTRATION_CANCELLATION(19, "Cancellation of Registration", "RC"),FRESH_REGISTRATION(22, "FRESH REGISTRATION", "FREG"),
    //Tax 
    STOPPAGE_TAX(200, "Stoppage Tax", "ST"), STOPPAGE_TAX_REVOCATION(201, "Stoppage Tax Revocation", "STR"), DIFFERENTIAL_TAX(222, "Differential Tax", "DT"), 
    //LL and DL 
    LL_FRESH(301, "LLR Fresh", "LLF"), LL_ENDORSEMENT(302, "LLR Endorsement", "LLE"), LL_RETEST(303, "LLR Retest", "LLR"), LL_DUPLICATE(304, "LLR Duplicate", "LLD"),
    DL_FRESH(300, "DL Fresh", "DLF"),  DL_DUPLICATE(305, "Driving License Duplicate", "DLD"), DL_RENEWAL(306, "Driving License Renewal", "DLR"), DL_CHANGE_ADDRESS(307, "Driving License Change of Address", "DLCA"),
    DL_ENDORSMENT(308, "Driving License Endorsment", "DLE"), DL_RETEST(309, "Driving License Retest", "DLRE"), DL_BADGE(310, "Driving License Badge", "DLB"),
    DL_REVO_SUS(311, "Driving License Revocation of Suspension", "DLC"), DL_SURRENDER(312, "Driving License Surrender", "DLS"), DL_DLINFO(313, "Driving License DLINFO", "DLI"),
    DL_EXPIRED(314, "Driving Expired Dl", "DLEX"), DL_INT_PERMIT(315, "Driving License International Driving Permit", "DLIN"),
    DL_MILITRY(316, "DL Militry", "DLM"), DL_FOREIGN_CITIZEN(317, "DL Foreign Citizen", "DLFC"), DL_CHANGEADDRS_OS(318, "DL Other State change of address", "DLOS"),
    DL_SUSU_CANC(319, "Driving License Suspension/Cancellation", "DLSC"),
    
    FINANCIER_SIGNUP(401, "Financier Sign Up", "FINREG"), BODYBUILDER_SIGNUP(402, "Body Builder Sign Up", "BBREG"),
    DEALER_SIGNUP(403, "Dealer Sign Up", "DEALERREG"), PUC_USER_SIGNUP(404, "PUC User Sign Up", "PUCREG"),
    ALTERATION_AGENCY_SIGNUP(405, "Alteration Agency Sign Up", "AAREG"), DRIVING_INSTITUTE(406, "Driving Institute", "DRIVINGINSTREG"),
    HAZARDOUS_VEH_TRAIN_INST(407, "Hazardous Vehicle Training Institute", "HAZVEHTRINSTREG"), MEDICAL_PRACTITIONER(408, "Medical Practitioner", "MEDPRTSNRREG"),
    
    FC_FRESH(500, "Fitness Certificate Fresh", "FCF"), FC_RENEWAL(501, "Fitness Certificate Renewal", "FCR"), FC_REVOCATION_CFX(502, "Fitness Revocation of CFX", "FCRC"),
    FC_RE_INSPECTION_SB(503, "Fitness Re Inspection SlotBooking", "FCSB"),FC_OTHER_STATION(504, "Fitness Other Station", "FCOS"), FC_ISSUE_CFX(505, "Fitness Issue of CFX", "FCFX"),
    
	PERMIT_FRESH(600, "Permit Certificate Fresh", "PCF"), PERMIT_RENEWAL(601, "Permit Certificate Renewal", "PCR"),
	PERMIT_SURRENDER(602, "Permit Certificate Surrender", "PCS"),PERMIT_RENEWAL_AUTH_CARD(603, "Permit Renewal Authorization Card", "PCRAC"),
	PERMIT_REPLACEMENT_VEHICLE(604, "Permit Replacement of Vehicle", "PCRV"), PERMIT_VARIATIONS(605, "Permit variations", "PCV"), PERMIT_TRANSFER(606, "Permit Certificate Transfer", "PCT"),
	// Data Entry
	DE_NOC_CHANGE_OF_ADDRESS(700, "Data Entry for NOC Change of Address", "DENOCCA"), DE_NOC_TRANSFER_OF_OWNERSHIP(701, "Data Entry for NOC Transfer of Ownership", "DENOCOT"),
	DE_IMPORTED_VEHICLE(702, "Data Entry for Imported Vehicle", "DEIV"), DE_OTHER_STATE_NEW_VEHICLE_TR_GENERATED(703, "Data Entry for Other State-New Vehicle (TR Generated)", "DEOSNVTRG"),
	DE_TRAILER(704, "Data Entry for Trailer", "DET"),
	
	AADHAR_SEED_RC(800, "RC Aadhar Seeding", "RCAS"), AADHAR_SEED_DL(801, "DL Aadhar Seeding", "DLAS"),
	PAY_TAX(900, "Pay Tax", "PT");
	
    private Integer value;
    private String label;
    private String code;

    private static final Map<Integer, String> lookup = new HashMap<Integer, String>();
    private static final Map<String, ServiceType> labelToServiceType = new HashMap<String, ServiceType>();
    private static final Map<Integer, ServiceType> valueToServiceType = new HashMap<Integer, ServiceType>();
    private static final Map<String, ServiceType> codeToServiceType = new HashMap<String, ServiceType>();

    static {
        for (ServiceType serviceType : EnumSet.allOf(ServiceType.class)) {
            lookup.put(serviceType.getValue(), serviceType.getLabel());
        }
        for (ServiceType serviceType : EnumSet.allOf(ServiceType.class)) {
            labelToServiceType.put(serviceType.getLabel(), serviceType);
        }
        for (ServiceType serviceType : EnumSet.allOf(ServiceType.class)) {
            valueToServiceType.put(serviceType.getValue(), serviceType);
        }
        for (ServiceType serviceType : EnumSet.allOf(ServiceType.class)) {
            codeToServiceType.put(serviceType.getCode(), serviceType);
        }
    }
    
    private ServiceType(Integer value, String label, String code) {
        this.value = value;
        this.label = label;
        this.code = code;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static ServiceType getServiceType(String code) {
        if(ObjectsUtil.isNull(code)){
            return null;
        }
        if (code.toUpperCase().equals(OWNERSHIP_TRANSFER_SALE.getCode())) {
            return ServiceType.OWNERSHIP_TRANSFER_SALE;
        } else if (code.toUpperCase().equals(DEFAULT.getCode())) {
            return ServiceType.DEFAULT;
        } else if (code.toUpperCase().equals(NOC_ISSUE.getCode())) {
            return ServiceType.NOC_ISSUE;
        } else if (code.toUpperCase().equals(NOC_CANCELLATION.getCode())) {
            return ServiceType.NOC_CANCELLATION;
        } else if (code.toUpperCase().equals(HPT.getCode())) {
            return ServiceType.HPT;
        } else if (code.toUpperCase().equals(HPA.getCode())) {
            return ServiceType.HPA;
        } else if (code.toUpperCase().equals(ADDRESS_CHANGE.getCode())) {
            return ServiceType.ADDRESS_CHANGE;
        } else if (code.toUpperCase().equals(DUPLICATE_REGISTRATION.getCode())) {
            return ServiceType.DUPLICATE_REGISTRATION;
        } else if (code.toUpperCase().equals(REGISTRATION_RENEWAL.getCode())) {
            return ServiceType.REGISTRATION_RENEWAL;
        } else if (code.toUpperCase().equals(SUSPENSION_REVOCATION.getCode())) {
            return ServiceType.SUSPENSION_REVOCATION;
        } else if (code.toUpperCase().equals(VEHICLE_ATLERATION.getCode())) {
            return ServiceType.VEHICLE_ATLERATION;
        } else if (code.toUpperCase().equals(FRESH_RC_FINANCIER.getCode())) {
            return ServiceType.FRESH_RC_FINANCIER;
        } else if (code.toUpperCase().equals(VEHICLE_REASSIGNMENT.getCode())) {
            return ServiceType.VEHICLE_REASSIGNMENT;
        } else if (code.toUpperCase().equals(REGISTRATION_CANCELLATION.getCode())) {
            return ServiceType.REGISTRATION_CANCELLATION;
        } else if (code.toUpperCase().equals(THEFT_INTIMATION.getCode())) {
            return ServiceType.THEFT_INTIMATION;
        }  else if (code.toUpperCase().equals(OWNERSHIP_TRANSFER_DEATH.getCode())) {
            return ServiceType.OWNERSHIP_TRANSFER_DEATH;
        }  else if (code.toUpperCase().equals(OWNERSHIP_TRANSFER_AUCTION.getCode())) {
            return ServiceType.OWNERSHIP_TRANSFER_AUCTION;
        } else if (code.toUpperCase().equals(REGISTRATION_SUS_CANCELLATION.getCode())) {
            return ServiceType.REGISTRATION_SUS_CANCELLATION;
        } else if (code.toUpperCase().equals(FRESH_REGISTRATION.getCode())) {
            return ServiceType.FRESH_REGISTRATION;
        }
        
        else if (code.toUpperCase().equals(LL_FRESH.getCode())) {
            return ServiceType.LL_FRESH;
        } else if (code.toUpperCase().equals(LL_ENDORSEMENT.getCode())) {
            return ServiceType.LL_ENDORSEMENT;
        } else if (code.toUpperCase().equals(LL_RETEST.getCode())) {
            return ServiceType.LL_RETEST;
        } else if (code.toUpperCase().equals(LL_DUPLICATE.getCode())) {
            return ServiceType.LL_DUPLICATE;
        } else if (code.toUpperCase().equals(DL_FRESH.getCode())) {
            return ServiceType.DL_FRESH;
        } else if (code.toUpperCase().equals(DL_DUPLICATE.getCode())) {
            return ServiceType.DL_DUPLICATE;
        } else if (code.toUpperCase().equals(DL_RENEWAL.getCode())) {
            return ServiceType.DL_RENEWAL;
        } else if (code.toUpperCase().equals(DL_CHANGE_ADDRESS.getCode())) {
            return ServiceType.DL_CHANGE_ADDRESS;
        } else if (code.toUpperCase().equals(DL_ENDORSMENT.getCode())) {
            return ServiceType.DL_ENDORSMENT;
        } else if (code.toUpperCase().equals(DL_RETEST.getCode())) {
            return ServiceType.DL_RETEST;
        } else if (code.toUpperCase().equals(DL_BADGE.getCode())) {
            return ServiceType.DL_BADGE;
        } else if (code.toUpperCase().equals(DL_REVO_SUS.getCode())) {
            return ServiceType.DL_REVO_SUS;
        } else if (code.toUpperCase().equals(DL_SURRENDER.getCode())) {
            return ServiceType.DL_SURRENDER;
        } else if (code.toUpperCase().equals(DL_DLINFO.getCode())) {
            return ServiceType.DL_DLINFO;
        } else if (code.toUpperCase().equals(DL_EXPIRED.getCode())) {
            return ServiceType.DL_EXPIRED;
        } else if (code.toUpperCase().equals(DL_INT_PERMIT.getCode())) {
            return ServiceType.DL_INT_PERMIT;
        } else if (code.toUpperCase().equals(DL_MILITRY.getCode())) {
            return ServiceType.DL_MILITRY;
        } else if (code.toUpperCase().equals(DL_FOREIGN_CITIZEN.getCode())) {
            return ServiceType.DL_FOREIGN_CITIZEN;
        } else if (code.toUpperCase().equals(DL_CHANGEADDRS_OS.getCode())) {
            return ServiceType.DL_CHANGEADDRS_OS;
        }else if (code.toUpperCase().equals(DL_SUSU_CANC.getCode())) {
            return ServiceType.DL_SUSU_CANC;
        }
        
        
        else if (code.toUpperCase().equals(FINANCIER_SIGNUP.getCode())) {
            return ServiceType.FINANCIER_SIGNUP;
        }else if (code.toUpperCase().equals(BODYBUILDER_SIGNUP.getCode())) {
            return ServiceType.BODYBUILDER_SIGNUP;
        }else if (code.toUpperCase().equals(DEALER_SIGNUP.getCode())) {
            return ServiceType.DEALER_SIGNUP;
        }else if (code.toUpperCase().equals(PUC_USER_SIGNUP.getCode())) {
            return ServiceType.PUC_USER_SIGNUP;
        }else if (code.toUpperCase().equals(ALTERATION_AGENCY_SIGNUP.getCode())) {
            return ServiceType.ALTERATION_AGENCY_SIGNUP;
        }else if (code.toUpperCase().equals(DRIVING_INSTITUTE.getCode())) {
            return ServiceType.DRIVING_INSTITUTE;
        }else if (code.toUpperCase().equals(HAZARDOUS_VEH_TRAIN_INST.getCode())) {
            return ServiceType.HAZARDOUS_VEH_TRAIN_INST;
        }else if (code.toUpperCase().equals(MEDICAL_PRACTITIONER.getCode())) {
            return ServiceType.MEDICAL_PRACTITIONER;
        }
        
        else if (code.toUpperCase().equals(FC_FRESH.getCode())) {
            return ServiceType.FC_FRESH;
        } else if (code.toUpperCase().equals(FC_RENEWAL.getCode())) {
            return ServiceType.FC_RENEWAL;
        } else if (code.toUpperCase().equals(FC_REVOCATION_CFX.getCode())) {
            return ServiceType.FC_REVOCATION_CFX;
        } else if (code.toUpperCase().equals(FC_RE_INSPECTION_SB.getCode())) {
            return ServiceType.FC_RE_INSPECTION_SB;
        } else if (code.toUpperCase().equals(FC_OTHER_STATION.getCode())) {
            return ServiceType.FC_OTHER_STATION;
        } else if (code.toUpperCase().equals(FC_ISSUE_CFX.getCode())) {
            return ServiceType.FC_ISSUE_CFX;
        }
        
        else if (code.toUpperCase().equals(PERMIT_FRESH.getCode())) {
            return ServiceType.PERMIT_FRESH;
        } else if (code.toUpperCase().equals(PERMIT_RENEWAL.getCode())) {
            return ServiceType.PERMIT_RENEWAL;
        } else if (code.toUpperCase().equals(PERMIT_SURRENDER.getCode())) {
            return ServiceType.PERMIT_SURRENDER;
        } else if (code.toUpperCase().equals(PERMIT_RENEWAL_AUTH_CARD.getCode())) {
            return ServiceType.PERMIT_RENEWAL_AUTH_CARD;
        } else if (code.toUpperCase().equals(PERMIT_REPLACEMENT_VEHICLE.getCode())) {
            return ServiceType.PERMIT_REPLACEMENT_VEHICLE;
        } else if (code.toUpperCase().equals(PERMIT_VARIATIONS.getCode())) {
            return ServiceType.PERMIT_VARIATIONS;
        } else if (code.toUpperCase().equals(PERMIT_TRANSFER.getCode())) {
            return ServiceType.PERMIT_TRANSFER;
        } else if (code.toUpperCase().equals(DE_NOC_CHANGE_OF_ADDRESS.getCode())) {
            return ServiceType.DE_NOC_CHANGE_OF_ADDRESS;
        } else if (code.toUpperCase().equals(DE_NOC_TRANSFER_OF_OWNERSHIP.getCode())) {
            return ServiceType.DE_NOC_TRANSFER_OF_OWNERSHIP;
        } else if (code.toUpperCase().equals(DE_IMPORTED_VEHICLE.getCode())) {
            return ServiceType.DE_IMPORTED_VEHICLE;
        } else if (code.toUpperCase().equals(DE_OTHER_STATE_NEW_VEHICLE_TR_GENERATED.getCode())) {
            return ServiceType.DE_OTHER_STATE_NEW_VEHICLE_TR_GENERATED;
        } else if (code.toUpperCase().equals(DE_TRAILER.getCode())) {
            return ServiceType.DE_TRAILER;
        }
        
        else if (code.toUpperCase().equals(AADHAR_SEED_RC.getCode())) {
            return ServiceType.AADHAR_SEED_RC;
        } else if (code.toUpperCase().equals(AADHAR_SEED_DL.getCode())) {
            return ServiceType.AADHAR_SEED_DL;
        }
        
        else if (code.toUpperCase().equals(PAY_TAX.getCode())) {
            return ServiceType.PAY_TAX;
        } else if (code.toUpperCase().equals(STOPPAGE_TAX.getCode())) {
            return ServiceType.STOPPAGE_TAX;
        } else if (code.toUpperCase().equals(STOPPAGE_TAX_REVOCATION.getCode())) {
            return ServiceType.STOPPAGE_TAX_REVOCATION;
        } else if (code.toUpperCase().equals(DIFFERENTIAL_TAX.getCode())) {
            return ServiceType.DIFFERENTIAL_TAX;
        } 
        return null;
    }

    @Override
    public String toString() {
        return this.getCode();
    }
    
    public static ServiceType getServiceTypeByCode(String code) {
        return code == null ? null : codeToServiceType.get(code);
    }
    
}

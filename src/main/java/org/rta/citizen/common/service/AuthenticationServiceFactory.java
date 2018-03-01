package org.rta.citizen.common.service;

import org.rta.citizen.aadharseeding.rc.service.impl.RCASAuthenticationServiceImpl;
import org.rta.citizen.addresschange.service.impl.AddressChangeAuthenticationService;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.duplicateregistration.service.impl.DuplicateRegistrationAuthenticationService;
import org.rta.citizen.fitness.cfx.service.FCFXAuthenticationService;
import org.rta.citizen.fitness.fresh.service.impl.FitnessFreshAuthenticationService;
import org.rta.citizen.fitness.otherstation.service.FitnessOtherStationAuthenticationService;
import org.rta.citizen.fitness.reinspection.service.FitnessReInspenctionAuthenticationService;
import org.rta.citizen.fitness.renewal.service.FitnessRenewalAuthenticationService;
import org.rta.citizen.fitness.revocation.service.FitnessRevocationAuthenticationService;
import org.rta.citizen.freshrc.FreshRCAuthenticationService;
import org.rta.citizen.hpa.service.HPAAuthenticationService;
import org.rta.citizen.hpt.service.HPTAuthenticationService;
import org.rta.citizen.licence.service.impl.DrivingLicenceExpiredAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLicenceInternationalAuthentication;
import org.rta.citizen.licence.service.impl.DrivingLicenceOtherStateCAAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLicenceServiceImpl;
import org.rta.citizen.licence.service.impl.DrivingLicenceSurrenderAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLicenceSuspCancAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLicenceSuspensionAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceBadgeAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceChangeOfAddressAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceDuplicateAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceEndorsementAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceForiegnCitizenAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceInfoAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceMilitaryAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceRenewalAuthendication;
import org.rta.citizen.licence.service.impl.DrivingLiceneceRetestAuthendication;
import org.rta.citizen.licence.service.updated.impl.login.LlrAuthenticationService;
import org.rta.citizen.licence.service.updated.impl.login.LlrDuplicateAuthenticationService;
import org.rta.citizen.licence.service.updated.impl.login.LlrEndorsementAuthenticationService;
import org.rta.citizen.licence.service.updated.impl.login.LlrRetestAuthenticationService;
import org.rta.citizen.noc.service.impl.NOCCancellationAuthenticationService;
import org.rta.citizen.noc.service.impl.NocAuthenticationServiceImpl;
import org.rta.citizen.ownershiptransfer.service.impl.OTBuyerAuthenticationServiceImpl;
import org.rta.citizen.ownershiptransfer.service.impl.OTSellerAuthenticationServiceImpl;
import org.rta.citizen.paytax.service.PayTaxAuthenticationService;
import org.rta.citizen.permit.fresh.service.PermitFreshAuthentication;
import org.rta.citizen.permit.pcv.service.PCVAuthenticationService;
import org.rta.citizen.permit.renewal.service.PermitRenewalAuthentication;
import org.rta.citizen.permit.renewalauthcard.service.PermitRenewalAuthCardAuthentication;
import org.rta.citizen.permit.surrender.service.PermitSurrenderAuthenticationService;
import org.rta.citizen.permit.vehiclereplacement.service.PermitVehicleReplacementAuthenticationService;
import org.rta.citizen.registrationcancellation.service.impl.RegistrationCancellationAuthenticationService;
import org.rta.citizen.registrationrenewal.service.impl.RegistrationRenewalAuthenticationService;
import org.rta.citizen.rsc.service.RSCAuthenticationServiceImpl;
import org.rta.citizen.slotbooking.service.impl.SlotBookingAuthenticationService;
import org.rta.citizen.stoppagetax.service.impl.StoppageTaxAuthenticationService;
import org.rta.citizen.stoppagetax.service.impl.StoppageTaxRevocationAuthenticationService;
import org.rta.citizen.suspensionrevocation.service.impl.SuspensionRevocationAuthenticationService;
import org.rta.citizen.theftintimation.service.impl.TheftIntimationAuthenticationService;
import org.rta.citizen.userregistration.service.impl.UserRegAuthenticationServiceImpl;
import org.rta.citizen.vehiclealteration.service.impl.VehicleAlterationAuthenticationService;
import org.rta.citizen.vehiclereassignment.service.impl.VehicleReassignmentAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceFactory {

	@Autowired
	private SlotBookingAuthenticationService slotBookingAuthenticationService;

	@Autowired
	private HPTAuthenticationService hPTAuthenticationService;

	@Autowired
	private NocAuthenticationServiceImpl nocAuthenticationServiceImpl;

	@Autowired
	private HPAAuthenticationService hPAAuthenticationService;

	@Autowired
	private LlrAuthenticationService llrAuthenticationService;

	@Autowired
	private LlrEndorsementAuthenticationService llrEndorsementAuthenticationService;
	
	@Autowired
    private DrivingLicenceServiceImpl drivingLicenceServiceImpl;

	@Autowired
	private LlrDuplicateAuthenticationService llrduplicateauthenticationservice;

	@Autowired
	private LlrRetestAuthenticationService llrretestauthenticationservice;

	@Autowired
	private AddressChangeAuthenticationService addressChangeAuthenticationService;

	@Autowired
	private DuplicateRegistrationAuthenticationService duplicateRegistrationAuthenticationService;

    @Autowired
    private RegistrationRenewalAuthenticationService registrationRenewalAuthenticationService;

    @Autowired
    private SuspensionRevocationAuthenticationService suspensionRevocationAuthenticationService;

    @Autowired
    private TheftIntimationAuthenticationService theftIntimationAuthenticationService;

    @Autowired
    private VehicleAlterationAuthenticationService vehicleAlterationAuthenticationService;

    @Autowired
    private VehicleReassignmentAuthenticationService vehicleReassignmentAuthenticationService;

    @Autowired
    private RegistrationCancellationAuthenticationService registrationCancellationAuthenticationService;
    
    @Autowired
    private OTSellerAuthenticationServiceImpl oTSellerAuthenticationServiceImpl;
    
    @Autowired
    private OTBuyerAuthenticationServiceImpl oTBuyerAuthenticationServiceImpl;
    
    @Autowired
    private DrivingLiceneceRetestAuthendication drivingLiceneceRetestAuthendication;
    
    @Autowired
    private DrivingLiceneceChangeOfAddressAuthendication drivingLiceneceChangeOfAddressAuthendication;
    
    @Autowired
    private DrivingLiceneceBadgeAuthendication drivingLiceneceDrivingLicenceBadgeAuthendication;
    
    @Autowired
    private DrivingLiceneceRenewalAuthendication drivingLiceneceRenewalAuthendication;
    
    @Autowired
    private DrivingLiceneceDuplicateAuthendication drivingLiceneceDuplicateAuthendication;
    
    @Autowired
    private DrivingLicenceSurrenderAuthendication drivingLiceneceSurrenderAuthendication;
    
    @Autowired
    private DrivingLicenceSuspensionAuthendication drivingLicenceSuspensionAuthendication;
    
    @Autowired
    private DrivingLicenceExpiredAuthendication drivingLicenceExpriedAuthendication;
    
    @Autowired
    private DrivingLicenceInternationalAuthentication drivingLicenceInternationalAuthentication;
    
    @Autowired
    private DrivingLicenceOtherStateCAAuthendication drivingLicenceOtherStateCAAuthendication;
    
    @Autowired
    private DrivingLiceneceForiegnCitizenAuthendication drivingLiceneceForiegnCitizenAuthendication;
    
    @Autowired
    private DrivingLiceneceMilitaryAuthendication drivingLiceneceMilitaryAuthendication;
    
    @Autowired
    private DrivingLiceneceInfoAuthendication drivingLiceneceInfoAuthendication;
    
	@Autowired
	private DrivingLiceneceEndorsementAuthendication drivingLiceneceEndorsementAuthendication;
	
	@Autowired
	private DrivingLicenceSuspCancAuthendication drivingLicenceSuspCancAuthendication;
	
	@Autowired
	private NOCCancellationAuthenticationService nOCCancellationAuthenticationService;
	
	@Autowired
	private UserRegAuthenticationServiceImpl userRegAuthenticationServiceImpl;
	
	@Autowired
	private FitnessFreshAuthenticationService fitnessFreshAuthendication;
	
	@Autowired
	private FreshRCAuthenticationService freshRCAuthenticationService;
    
	@Autowired
	private FitnessReInspenctionAuthenticationService fitnessSlotBookingAuthendication;
	
	@Autowired
	private FitnessRevocationAuthenticationService fitnessRevocationAuthendication;
	
	@Autowired
	private PermitFreshAuthentication permitFreshAuthendication;
	
	@Autowired
	private PermitRenewalAuthentication permitRenewalAuthendication;
	
	@Autowired
	private FitnessRenewalAuthenticationService fitnessRenewalAuthendication;
	
	@Autowired
	private RSCAuthenticationServiceImpl rSCAuthenticationServiceImpl;
	
	@Autowired
	private PermitSurrenderAuthenticationService permitSurrenderAuthenticationService;
	
	@Autowired
	private FitnessOtherStationAuthenticationService fitnessOtherStationAuthenticationService;
	
	@Autowired
    private PermitRenewalAuthCardAuthentication permitRenewalAuthCardAuthentication;
	
	@Autowired
    private PermitVehicleReplacementAuthenticationService permitVehicleReplacementAuthenticationService;
	
	@Autowired
	private PCVAuthenticationService pcvAuthenticationService;
	
	@Autowired
	private RCASAuthenticationServiceImpl rCASAuthenticationServiceImpl;
	
	@Autowired
	private PayTaxAuthenticationService payTaxAuthenticationService;
	
	@Autowired
	private FCFXAuthenticationService fcfxAuthenticationService;
	
	@Autowired
	private StoppageTaxAuthenticationService stoppageTaxAuthenticationService;
	
	@Autowired
	private StoppageTaxRevocationAuthenticationService stoppageTaxRevocationAuthenticationService;
	
    
	public AuthenticationService getAuthenticationService(ServiceType serviceType, UserType userType) {
		if (serviceType == ServiceType.OWNERSHIP_TRANSFER_SALE) {
		    if(!ObjectsUtil.isNull(userType) && userType == UserType.ROLE_BUYER){
		        return oTBuyerAuthenticationServiceImpl;
		    } else {
		        return oTSellerAuthenticationServiceImpl;
		    }
		} else if(serviceType == ServiceType.OWNERSHIP_TRANSFER_DEATH || serviceType == ServiceType.OWNERSHIP_TRANSFER_AUCTION){
		    return oTBuyerAuthenticationServiceImpl;
		} else if (serviceType == ServiceType.DIFFERENTIAL_TAX/* || serviceType == ServiceType.SLOT_BOOKING*/) {
			return slotBookingAuthenticationService;
		} else if (serviceType == ServiceType.HPT) {
			return hPTAuthenticationService;
		} else if (serviceType == ServiceType.NOC_ISSUE) {
			return nocAuthenticationServiceImpl;
		} else if (serviceType == ServiceType.NOC_CANCELLATION) {
		    return nOCCancellationAuthenticationService;
		} else if (serviceType == ServiceType.HPT) {
			return hPTAuthenticationService;
		} else if (serviceType == ServiceType.HPA) {
			return hPAAuthenticationService;
		} else if (serviceType == ServiceType.LL_FRESH) {
			return llrAuthenticationService;
		} else if (serviceType == ServiceType.LL_ENDORSEMENT) {
			return llrEndorsementAuthenticationService;
		} else if (serviceType == ServiceType.ADDRESS_CHANGE) {
			return addressChangeAuthenticationService;
		} else if (serviceType == ServiceType.DUPLICATE_REGISTRATION) {
			return duplicateRegistrationAuthenticationService;
		} else if (serviceType == ServiceType.LL_DUPLICATE) {
			return llrduplicateauthenticationservice;
		} else if (serviceType == ServiceType.LL_RETEST) {
			return llrretestauthenticationservice;
        } else if (serviceType == ServiceType.REGISTRATION_RENEWAL) {
            return registrationRenewalAuthenticationService;
        } else if (serviceType == ServiceType.SUSPENSION_REVOCATION) {
            return suspensionRevocationAuthenticationService;
        } else if (serviceType == ServiceType.THEFT_INTIMATION) {
            return theftIntimationAuthenticationService;
        } else if (serviceType == ServiceType.VEHICLE_ATLERATION) {
            return vehicleAlterationAuthenticationService;
        } else if (serviceType == ServiceType.VEHICLE_REASSIGNMENT) {
            return vehicleReassignmentAuthenticationService;
        }else if (serviceType == ServiceType.DL_FRESH) {
            return drivingLicenceServiceImpl;
        } else if (serviceType == ServiceType.REGISTRATION_CANCELLATION) {
            return registrationCancellationAuthenticationService;
        }else if (serviceType == ServiceType.DL_ENDORSMENT) {
            return drivingLiceneceEndorsementAuthendication;
        }else if (serviceType == ServiceType.DL_RETEST) {
            return drivingLiceneceRetestAuthendication;
        }else if (serviceType == ServiceType.DL_CHANGE_ADDRESS) {
            return drivingLiceneceChangeOfAddressAuthendication;
        }else if (serviceType == ServiceType.DL_BADGE) {
            return drivingLiceneceDrivingLicenceBadgeAuthendication;
        }else if (serviceType == ServiceType.DL_RENEWAL) {
            return drivingLiceneceRenewalAuthendication;
        }else if (serviceType == ServiceType.DL_DUPLICATE) {
            return drivingLiceneceDuplicateAuthendication;
        }else if (serviceType == ServiceType.DL_SURRENDER) {
            return drivingLiceneceSurrenderAuthendication;
        }else if (serviceType == ServiceType.DL_REVO_SUS) {
            return drivingLicenceSuspensionAuthendication;
        }else if (serviceType == ServiceType.DL_EXPIRED) {
            return drivingLicenceExpriedAuthendication;
        }else if (serviceType == ServiceType.DL_INT_PERMIT) {
            return drivingLicenceInternationalAuthentication;
        }else if (serviceType == ServiceType.DL_MILITRY) {
            return drivingLiceneceMilitaryAuthendication;
        }else if (serviceType == ServiceType.DL_DLINFO) {
            return drivingLiceneceInfoAuthendication;
        }else if (serviceType == ServiceType.DL_FOREIGN_CITIZEN) {
            return drivingLiceneceForiegnCitizenAuthendication;
        }else if (serviceType == ServiceType.DL_CHANGEADDRS_OS) {
            return drivingLicenceOtherStateCAAuthendication;
        }else if (serviceType == ServiceType.DL_SUSU_CANC) {
            return drivingLicenceSuspCancAuthendication;
        }else if (serviceType == ServiceType.FC_FRESH) {
            return fitnessFreshAuthendication;
        }else if (serviceType == ServiceType.FC_RENEWAL) {
            return fitnessRenewalAuthendication;
        }else if (serviceType == ServiceType.FC_RE_INSPECTION_SB) {
            return fitnessSlotBookingAuthendication;
        }else if (serviceType == ServiceType.FC_REVOCATION_CFX) {
            return fitnessRevocationAuthendication;
        }else if (serviceType == ServiceType.FC_OTHER_STATION) {
            return fitnessOtherStationAuthenticationService;
        } else if (serviceType == ServiceType.PERMIT_FRESH) {
            return permitFreshAuthendication;
        } else if (serviceType == ServiceType.PERMIT_RENEWAL) {
            return permitRenewalAuthendication;
        } else if (serviceType == ServiceType.PERMIT_SURRENDER) {
            return permitSurrenderAuthenticationService;
        } else if (serviceType == ServiceType.PERMIT_RENEWAL_AUTH_CARD) {
            return permitRenewalAuthCardAuthentication;
        } else if (serviceType == ServiceType.PERMIT_REPLACEMENT_VEHICLE) {
            return permitVehicleReplacementAuthenticationService;
        } else if (serviceType == ServiceType.FINANCIER_SIGNUP || serviceType == ServiceType.ALTERATION_AGENCY_SIGNUP
                || serviceType == ServiceType.DEALER_SIGNUP || serviceType == ServiceType.BODYBUILDER_SIGNUP
                || serviceType == ServiceType.PUC_USER_SIGNUP || serviceType == ServiceType.DRIVING_INSTITUTE
                || serviceType == ServiceType.HAZARDOUS_VEH_TRAIN_INST || serviceType == ServiceType.MEDICAL_PRACTITIONER) {
            return userRegAuthenticationServiceImpl;
        } else if (serviceType == ServiceType.FRESH_RC_FINANCIER) {
            return freshRCAuthenticationService;
        } else if(serviceType == ServiceType.REGISTRATION_SUS_CANCELLATION){
            return rSCAuthenticationServiceImpl;
        } else  if (serviceType == ServiceType.PERMIT_VARIATIONS) {
            return pcvAuthenticationService;
        }

        else  if (serviceType == ServiceType.PERMIT_VARIATIONS) {
            return pcvAuthenticationService;
        } else  if (serviceType == ServiceType.PERMIT_VARIATIONS) {
            return pcvAuthenticationService;
        }
		
        else  if (serviceType == ServiceType.AADHAR_SEED_RC) {
            return rCASAuthenticationServiceImpl;
        }
		
        else  if (serviceType == ServiceType.PAY_TAX) {
            return payTaxAuthenticationService;
        }
        else  if (serviceType == ServiceType.FC_ISSUE_CFX) {
            return fcfxAuthenticationService;
        }else if(serviceType == ServiceType.STOPPAGE_TAX){
        	return stoppageTaxAuthenticationService;
        }else if(serviceType == ServiceType.STOPPAGE_TAX_REVOCATION){
        	return stoppageTaxRevocationAuthenticationService;
        }
		return null;
	}

}

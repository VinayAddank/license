package org.rta.citizen.common.service;

import org.rta.citizen.aadharseeding.rc.service.impl.RCASDetailsService;
import org.rta.citizen.addresschange.service.impl.AddressChangeDetailsService;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.duplicateregistration.service.impl.DuplicateRegistrationDetailsService;
import org.rta.citizen.fitness.cfx.service.FCFXDetailsService;
import org.rta.citizen.fitness.fresh.service.impl.FitnessFreshDetailsService;
import org.rta.citizen.fitness.otherstation.service.FitnessOtherStationDetailsService;
import org.rta.citizen.fitness.reinspection.service.FitnessReInspenctionDetailsService;
import org.rta.citizen.fitness.renewal.service.FitnessRenewalDetailsService;
import org.rta.citizen.fitness.revocation.service.FitnessRevocationDetailsService;
import org.rta.citizen.freshrc.FreshRCPRDetailService;
import org.rta.citizen.hpa.service.HPADetailsService;
import org.rta.citizen.hpt.service.HPTDetailsService;
import org.rta.citizen.licence.service.updated.impl.details.LicenseDetailsService;
import org.rta.citizen.noc.service.impl.NocDetailsService;
import org.rta.citizen.ownershiptransfer.service.impl.OwnershipTransferDetailsService;
import org.rta.citizen.paytax.service.PayTaxDetailsService;
import org.rta.citizen.permit.fresh.service.PermitFreshDetailsService;
import org.rta.citizen.permit.pcv.service.PCVDetailsService;
import org.rta.citizen.permit.renewal.service.PermitRenewalDetailsService;
import org.rta.citizen.permit.renewalauthcard.service.PermitRenewalAuthCardDetailsService;
import org.rta.citizen.permit.surrender.service.PermitSurrenderDetailsService;
import org.rta.citizen.permit.vehiclereplacement.service.PermitVehicleReplacementDetailsService;
import org.rta.citizen.registrationcancellation.service.impl.RegistrationCancellationDetailsService;
import org.rta.citizen.registrationrenewal.service.impl.RegistrationRenewalDetailsService;
import org.rta.citizen.rsc.service.RSCDetailsService;
import org.rta.citizen.slotbooking.service.impl.SlotBookingDetailsService;
import org.rta.citizen.stoppagetax.service.impl.StoppageTaxDetailsService;
import org.rta.citizen.suspensionrevocation.service.impl.SuspensionRevocationDetailsService;
import org.rta.citizen.theftintimation.service.impl.TheftIntimationDetailsService;
import org.rta.citizen.vehiclealteration.service.impl.VehicleAlterationDetailsService;
import org.rta.citizen.vehiclereassignment.service.impl.VehicleReassignmentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DetailsServiceFactory {

	@Autowired
	private OwnershipTransferDetailsService ownershipTransferDetailsService;

	@Autowired
	private SlotBookingDetailsService slotBookingDetailsService;

	@Autowired
	private HPTDetailsService hPTDetailsService;

	@Autowired
	private HPADetailsService hPADetailsService;

	@Autowired
	private NocDetailsService nocDetailsService;

	@Autowired
	private AddressChangeDetailsService addressChangeDetailsService;

	@Autowired
	private DuplicateRegistrationDetailsService duplicateRegistrationDetailsService;

	@Autowired
	private VehicleAlterationDetailsService vehicleAlterationDetailsService;

	@Autowired
	private VehicleReassignmentDetailsService vehicleReassignmentDetailsService;

	@Autowired
	private TheftIntimationDetailsService theftIntimationDetailsService;

	@Autowired
	private SuspensionRevocationDetailsService suspensionRevocationDetailsService;

	@Autowired
	private RegistrationRenewalDetailsService registrationRenewalDetailsService;

	@Autowired
	private RegistrationCancellationDetailsService registrationCancellationDetailsService;

	@Autowired
	private FreshRCPRDetailService freshRCPRDetailsService;

	@Autowired
	private FitnessRevocationDetailsService fitnessRevocationDetailsService;

	@Autowired
	private FitnessReInspenctionDetailsService fitnessSlotBookingDetailsService;

	@Autowired
	private PermitFreshDetailsService permitFreshDetailsService;

	@Autowired
	private PermitRenewalDetailsService permitRenewalDetailsService;

	@Autowired
	private FitnessFreshDetailsService fitnessDetailsService;

	@Autowired
	private FitnessRenewalDetailsService fitnessRenewalDetailsService;

	@Autowired
	private RSCDetailsService rSCDetailsService;

	@Autowired
	private PermitSurrenderDetailsService permitSurrenderDetailsService;

	@Autowired
	private FitnessOtherStationDetailsService fitnessOtherStationService;

	@Autowired
	private PermitRenewalAuthCardDetailsService permitRenewalAuthCardDetailsService;

	@Autowired
	private LicenseDetailsService driversLicenseDetailsService;

	@Autowired
	private PermitVehicleReplacementDetailsService permitVehicleReplacementDetailsService;

	@Autowired
	private PCVDetailsService pcvDetailsService;

	@Autowired
	private RCASDetailsService rCASDetailsService;

	@Autowired
	private PayTaxDetailsService payTaxDetailsService;

	@Autowired
	private FCFXDetailsService fcfxDetailsService;

	@Autowired
	private StoppageTaxDetailsService stoppageTaxDetailsService;

	@Autowired
	@Qualifier("appSearchHptServiceImpl")
	private AppSearchService appSearchServiceHPT;

	@Autowired
	@Qualifier("appSearchHpaServiceImpl")
	private AppSearchService appSearchServiceHPA;

	@Autowired
	@Qualifier("appSearchNocServiceImpl")
	private AppSearchService appSearchServiceNOC;

	@Autowired
	@Qualifier("appSearchACServiceImpl")
	private AppSearchService appSearchServiceAC;

	@Autowired
	@Qualifier("appSearchDRServiceImpl")
	private AppSearchService appSearchServiceDR;

	@Autowired
	@Qualifier("appSearchVAServiceImpl")
	private AppSearchService appSearchServiceVA;

	@Autowired
	@Qualifier("appSearchVRServiceImpl")
	private AppSearchService appSearchServiceVR;

	@Autowired
	@Qualifier("appSearchTIServiceImpl")
	private AppSearchService appSearchServiceTI;

	@Autowired
	@Qualifier("appSearchSRServiceImpl")
	private AppSearchService appSearchServiceSR;

	@Autowired
	@Qualifier("appSearchRRServiceImpl")
	private AppSearchService appSearchServiceRR;

	@Autowired
	@Qualifier("appSearchRCServiceImpl")
	private AppSearchService appSearchServiceRC;

	@Autowired
	@Qualifier("appSearchOTServiceImpl")
	private AppSearchService appSearchServiceOT;

	@Autowired
	@Qualifier("appSearchLLFServiceImpl")
	private AppSearchService appSearchServiceLLF;

	@Autowired
	@Qualifier("appSearchLLDServiceImpl")
	private AppSearchService appSearchServiceLLD;

	@Autowired
	@Qualifier("appSearchLLEServiceImpl")
	private AppSearchService appSearchServiceLLE;

	@Autowired
	@Qualifier("appSearchLLRServiceImpl")
	private AppSearchService appSearchServiceLLR;

	@Autowired
	@Qualifier("appSearchDLFServiceImpl")
	private AppSearchService appSearchServiceDLF;

	@Autowired
	@Qualifier("appSearchDLBServiceImpl")
	private AppSearchService appSearchServiceDLB;

	@Autowired
	@Qualifier("appSearchDLCAServiceImpl")
	private AppSearchService appSearchServiceDLCA;

	@Autowired
	@Qualifier("appSearchDLIServiceImpl")
	private AppSearchService appSearchServiceDLI;

	@Autowired
	@Qualifier("appSearchDLDServiceImpl")
	private AppSearchService appSearchServiceDLD;

	@Autowired
	@Qualifier("appSearchDLEServiceImpl")
	private AppSearchService appSearchServiceDLE;

	@Autowired
	@Qualifier("appSearchDLEXServiceImpl")
	private AppSearchService appSearchServiceDLEX;

	@Autowired
	@Qualifier("appSearchDLINServiceImpl")
	private AppSearchService appSearchServiceDLIN;

	@Autowired
	@Qualifier("appSearchDLMFCServiceImpl")
	private AppSearchService appSearchServiceDLMFC;

	@Autowired
	@Qualifier("appSearchDLRServiceImpl")
	private AppSearchService appSearchServiceDLR;

	@Autowired
	@Qualifier("appSearchDLREServiceImpl")
	private AppSearchService appSearchServiceDLRE;

	@Autowired
	@Qualifier("appSearchDLSServiceImpl")
	private AppSearchService appSearchServiceDLS;

	@Autowired
	@Qualifier("appSearchDLCServiceImpl")
	private AppSearchService appSearchServiceDLC;

	@Autowired
	@Qualifier("appSearchDLSCServiceImpl")
	private AppSearchService appSearchServiceDLSC;

	@Autowired
	@Qualifier("appSearchUserRegServiceImpl")
	private AppSearchService appSearchUserRegServiceImpl;

	@Autowired
	@Qualifier("appSearchPermitSurrenderServiceImpl")
	private AppSearchService appSearchPermitSurrenderServiceImpl;

	@Autowired
	@Qualifier("appSearchPermitFreshServiceImpl")
	private AppSearchService appSearchPermitFreshServiceImpl;

	@Autowired
	@Qualifier("appSearchPermitRenewalAuthCardServiceImpl")
	private AppSearchService appSearchPermitRenewalAuthCardServiceImpl;

	@Autowired
	@Qualifier("appSearchPermitRenewalServiceImpl")
	private AppSearchService appSearchPermitRenewalServiceImpl;

	@Autowired
	@Qualifier("appSearchFreshRCServiceImpl")
	private AppSearchService appSearchFreshRCServiceImpl;

	@Autowired
	@Qualifier("appSearchFitnessServiceImpl")
	private AppSearchService appSearchFitnessServiceImpl;

	@Autowired
	@Qualifier("appSearchRCASServiceImpl")
	private AppSearchService appSearchRCASServiceImpl;

	@Autowired
	@Qualifier("appSearchRscServiceImpl")
	private AppSearchService appSearchRscServiceImpl;

	@Autowired
	@Qualifier("appSearchSTServiceImpl")
	private AppSearchService appSearchSTServiceImpl;

	@Autowired
	@Qualifier("appSearchDTServiceImpl")
	private AppSearchService appSearchDTServiceImpl;
	
	@Autowired
	@Qualifier("appSearchPTServiceImpl")
	private AppSearchService appSearchPTServiceImpl;
	
	public DetailsService getDetailsService(ServiceType serviceType) {
		switch (serviceType) {
		case DEFAULT:
			break;
		case OWNERSHIP_TRANSFER_SALE:
		case OWNERSHIP_TRANSFER_DEATH:
		case OWNERSHIP_TRANSFER_AUCTION:
			return ownershipTransferDetailsService;
		case HPT:
			return hPTDetailsService;
		case HPA:
			return hPADetailsService;
		case NOC_ISSUE:
		case NOC_CANCELLATION:
			return nocDetailsService;
		case ADDRESS_CHANGE:
			return addressChangeDetailsService;
		case DUPLICATE_REGISTRATION:
			return duplicateRegistrationDetailsService;
		case DIFFERENTIAL_TAX:
			return slotBookingDetailsService;
		case VEHICLE_ATLERATION:
			return vehicleAlterationDetailsService;
		case VEHICLE_REASSIGNMENT:
			return vehicleReassignmentDetailsService;
		case THEFT_INTIMATION:
			return theftIntimationDetailsService;
		case SUSPENSION_REVOCATION:
			return suspensionRevocationDetailsService;
		case REGISTRATION_RENEWAL:
			return registrationRenewalDetailsService;
		case REGISTRATION_CANCELLATION:
			return registrationCancellationDetailsService;
		case FRESH_RC_FINANCIER:
			return freshRCPRDetailsService;
		case FC_FRESH:
			return fitnessDetailsService;
		case FC_RENEWAL:
			return fitnessRenewalDetailsService;
		case FC_RE_INSPECTION_SB:
			return fitnessSlotBookingDetailsService;
		case FC_REVOCATION_CFX:
			return fitnessRevocationDetailsService;
		case FC_OTHER_STATION:
			return fitnessOtherStationService;
		case FC_ISSUE_CFX:
			return fcfxDetailsService;
		case REGISTRATION_SUS_CANCELLATION:
			return rSCDetailsService;
		case PERMIT_FRESH:
			return permitFreshDetailsService;
		case PERMIT_RENEWAL:
			return permitRenewalDetailsService;
		case PERMIT_SURRENDER:
			return permitSurrenderDetailsService;
		case PERMIT_RENEWAL_AUTH_CARD:
			return permitRenewalAuthCardDetailsService;
		case PERMIT_REPLACEMENT_VEHICLE:
			return permitVehicleReplacementDetailsService;
		case LL_FRESH:
		case LL_DUPLICATE:
		case LL_ENDORSEMENT:
		case LL_RETEST:
		case DL_FRESH:
		case DL_DUPLICATE:
		case DL_RENEWAL:
		case DL_CHANGE_ADDRESS:
		case DL_BADGE:
		case DL_INT_PERMIT:
		case DL_SURRENDER:
		case DL_MILITRY:
		case DL_CHANGEADDRS_OS:
		case DL_EXPIRED:
		case DL_REVO_SUS:
		case DL_RETEST:
		case DL_ENDORSMENT:
			return driversLicenseDetailsService;
		case PERMIT_VARIATIONS:
			return pcvDetailsService;
		case AADHAR_SEED_RC:
			return rCASDetailsService;
		case PAY_TAX:
			return payTaxDetailsService;
		case STOPPAGE_TAX:
		case STOPPAGE_TAX_REVOCATION:
			return stoppageTaxDetailsService;
		}
		return null;
	}

	public AppSearchService getAppSearchService(ServiceType serviceType) {
		switch (serviceType) {
		case DEFAULT:
			break;
		case OWNERSHIP_TRANSFER_SALE:
		case OWNERSHIP_TRANSFER_DEATH:
		case OWNERSHIP_TRANSFER_AUCTION:
			return appSearchServiceOT;
		case DIFFERENTIAL_TAX:
			return appSearchDTServiceImpl;
		case HPT:
			return appSearchServiceHPT;
		case HPA:
			return appSearchServiceHPA;
		case NOC_ISSUE:
		case NOC_CANCELLATION:
			return appSearchServiceNOC;
		case ADDRESS_CHANGE:
			return appSearchServiceAC;
		case DUPLICATE_REGISTRATION:
			return appSearchServiceDR;
		case VEHICLE_ATLERATION:
			return appSearchServiceVA;
		case VEHICLE_REASSIGNMENT:
			return appSearchServiceVR;
		case THEFT_INTIMATION:
			return appSearchServiceTI;
		case SUSPENSION_REVOCATION:
			return appSearchServiceSR;
		case REGISTRATION_RENEWAL:
			return appSearchServiceRR;
		case REGISTRATION_CANCELLATION:
			return appSearchServiceRC;
		case REGISTRATION_SUS_CANCELLATION:
			return appSearchRscServiceImpl;
		case PAY_TAX:
			return appSearchPTServiceImpl;

		case FINANCIER_SIGNUP:
		case BODYBUILDER_SIGNUP:
		case DEALER_SIGNUP:
		case PUC_USER_SIGNUP:
		case ALTERATION_AGENCY_SIGNUP:
		case DRIVING_INSTITUTE:
		case HAZARDOUS_VEH_TRAIN_INST:
		case MEDICAL_PRACTITIONER:
			return appSearchUserRegServiceImpl;

		case LL_FRESH:
			return appSearchServiceLLF;
		case LL_DUPLICATE:
			return appSearchServiceLLD;
		case LL_ENDORSEMENT:
			return appSearchServiceLLE;
		case LL_RETEST:
			return appSearchServiceLLR;

		case DL_FRESH:
			return appSearchServiceDLF;
		case DL_BADGE:
			return appSearchServiceDLB;
		case DL_CHANGE_ADDRESS:
			return appSearchServiceDLCA;
		case DL_DLINFO:
			return appSearchServiceDLI;
		case DL_DUPLICATE:
			return appSearchServiceDLD;
		case DL_ENDORSMENT:
			return appSearchServiceDLE;
		case DL_EXPIRED:
			return appSearchServiceDLEX;
		case DL_INT_PERMIT:
			return appSearchServiceDLIN;
		case DL_MILITRY:
		case DL_CHANGEADDRS_OS:
		case DL_FOREIGN_CITIZEN:
			return appSearchServiceDLMFC;
		case DL_RENEWAL:
			return appSearchServiceDLR;
		case DL_RETEST:
			return appSearchServiceDLRE;
		case DL_SURRENDER:
			return appSearchServiceDLS;
		case DL_REVO_SUS:
			return appSearchServiceDLC;
		case DL_SUSU_CANC:
			return appSearchServiceDLSC;

		case PERMIT_SURRENDER:
			return appSearchPermitSurrenderServiceImpl;
		case PERMIT_FRESH:
			return appSearchPermitFreshServiceImpl;
		case PERMIT_RENEWAL_AUTH_CARD:
			return appSearchPermitRenewalAuthCardServiceImpl;
		case PERMIT_RENEWAL:
			return appSearchPermitRenewalServiceImpl;
		case FRESH_RC_FINANCIER:
			return appSearchFreshRCServiceImpl;
		case FC_FRESH:
		case FC_RENEWAL:
		case FC_REVOCATION_CFX:
		case FC_RE_INSPECTION_SB:
		case FC_OTHER_STATION:
			return appSearchFitnessServiceImpl;
		case AADHAR_SEED_RC:
			return appSearchRCASServiceImpl;
		case STOPPAGE_TAX:
		case STOPPAGE_TAX_REVOCATION:
			return appSearchSTServiceImpl;

		}
		return null;
	}
}

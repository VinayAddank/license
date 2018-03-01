package org.rta.citizen.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.ChallanDetailsModel;
import org.rta.citizen.common.model.CrimeDetailsListModel;
import org.rta.citizen.common.model.CrimeDetailsModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.FinanceModel;
import org.rta.citizen.common.model.FitnessDetailsModel;
import org.rta.citizen.common.model.InsuranceDetailsModel;
import org.rta.citizen.common.model.NocDetails;
import org.rta.citizen.common.model.PermitDetailsModel;
import org.rta.citizen.common.model.PermitHeaderModel;
import org.rta.citizen.common.model.PucDetailsModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.SuspendedRCNumberModel;
import org.rta.citizen.common.model.VehicleDetailsRequestModel;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.vcr.VcrBookingData;
import org.rta.citizen.common.model.vcr.VcrOffenseDetails;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.freshrc.FinancerFreshContactDetailsModel;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.licence.service.updated.impl.details.LicenseSelectedDetailsService;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

public abstract class AbstractDetailsService implements DetailsService {

	private static final Logger log = Logger.getLogger(AbstractDetailsService.class);

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private LicenseSelectedDetailsService driversLicenseSelectedDetailsService;

	@Value("${service.vcr.skip}")
	private Boolean skipVcr;

	protected CustomerDetailsRequestModel getCustomerDetails(Long vehicleRcId) {
		CustomerDetailsRequestModel cdrm = null;
		try {
			cdrm = applicationService.getCustomerDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting customer details" + e);
		} catch (Exception e) {
			log.error("error in getting customer details" + e);
		}
		return cdrm;
	}

	protected VehicleDetailsRequestModel getVehicleDetails(Long vehicleRcId) {
		VehicleDetailsRequestModel vdrm = null;
		try {
			vdrm = applicationService.getVehicleDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting vehicle details" + e);
		} catch (Exception e) {
			log.error("error in getting vehicle details" + e);
		}
		return vdrm;
	}

	/*
	 * protected List<ChallanDetailsModel> getChallanList(Long vehicleRcId) {
	 * List<ChallanDetailsModel> challanList = new
	 * ArrayList<ChallanDetailsModel>(); ChallanDetailsModel chalanDetails = new
	 * ChallanDetailsModel(); chalanDetails.setDate("23 July 2016");
	 * chalanDetails.setTime("12:30");
	 * chalanDetails.setViolationPlace("Vishakhapatnam");
	 * chalanDetails.setViolationDesc("Wrong Side Drive");
	 * chalanDetails.setTotalFineAmt("100"); challanList.add(chalanDetails);
	 * ChallanDetailsModel chalanDetails1 = new ChallanDetailsModel();
	 * chalanDetails1.setDate("23 July 2016"); chalanDetails1.setTime("12:30");
	 * chalanDetails1.setViolationPlace("Vishakhapatnam");
	 * chalanDetails1.setViolationDesc("Wrong Side Drive");
	 * chalanDetails1.setTotalFineAmt("100"); challanList.add(chalanDetails1);
	 * return challanList; }
	 */
	protected List<ChallanDetailsModel> getChallanList(String prNumber) {
		if (skipVcr)
			return null;
		List<VcrBookingData> vcrModel = null;
		List<ChallanDetailsModel> challanDetailsList;
		try {
			vcrModel = applicationService.getVcrDetails(prNumber);
			if (!ObjectsUtil.isNull(vcrModel)) {
				for (VcrBookingData vcrBookingData : vcrModel) {
					List<VcrOffenseDetails> offenses = vcrBookingData.getOffenseDetails();
					if (!ObjectsUtil.isNull(offenses)) {
						challanDetailsList = new ArrayList<>();
						for (VcrOffenseDetails offense : offenses) {
							ChallanDetailsModel challanDetails = new ChallanDetailsModel();
							challanDetails.setDate(vcrBookingData.getBookedDate());
							challanDetails.setStatus(vcrBookingData.getVcrStatus());
							challanDetails.setTime(vcrBookingData.getBookedTime());
							challanDetails.setTotalFineAmt(offense.getFineAmount());
							challanDetails.setViolationDesc(offense.getOffense());
							challanDetails.setViolationPlace(vcrBookingData.getPlaceBooked());
							challanDetailsList.add(challanDetails);
						}
						return challanDetailsList;
					}
				}
			}
		} catch (RestClientException e) {
			log.error("error in getting challandetails " + e);
		} catch (Exception e) {
			log.error("error in getting challandetails " + e);
		}
		return null;
	}

	protected List<CrimeDetailsModel> getCrimeDetails(String prNo) {
		try {
			List<CrimeDetailsModel> crimeDetailsModelList = null;
			CrimeDetailsListModel crimeDetailsListModel = applicationService.getCrimeDetails(prNo);
			if (crimeDetailsListModel != null) {
				crimeDetailsModelList = crimeDetailsListModel.getResult();
			}
			return crimeDetailsModelList;
		} catch (UnauthorizedException e) {
			log.error("error in getting CrimeDetails " + e);
		}
		return null;
	}

	protected FinanceModel getFinancierDetails(Long vehicleRcId) {
		FinanceModel financeModel = null;
		try {
			financeModel = applicationService.getFinancierDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting financeModel " + e);
		} catch (Exception e) {
			log.error("error in getting financeModel " + e);
		}
		return financeModel;
	}

	protected FinanceModel getFinancierDetails(Long vehicleRcId, String prNumber) {
		FinanceModel financeModel = null;
		try {
			financeModel = applicationService.getFinancierDetails(vehicleRcId);
			RegistrationServiceResponseModel<Boolean> response = registrationService.hasAppliedHPA(prNumber);
			if (response.getHttpStatus().equals(HttpStatus.OK)) {
				financeModel.setIsAppliedHPA(response.getResponseBody());
			} else {
				log.error("hasAppliedHPA Status is not OK...");
			}
		} catch (RestClientException e) {
			log.error("error in getting financeModel " + e);
		} catch (Exception e) {
			log.error("error in getting financeModel " + e);
		}
		return financeModel;
	}

	protected InsuranceDetailsModel getInsuranceDetails(Long vehicleRcId) {
		InsuranceDetailsModel insuranceDetailsModel = null;
		try {
			insuranceDetailsModel = applicationService.getInsuranceDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting insuranceDetailsModel " + e);
		} catch (Exception e) {
			log.error("error in getting insuranceDetailsModel " + e);
		}
		return insuranceDetailsModel;
	}

	protected ApplicationTaxModel getTaxDetails(String prTrNumber) {
		ApplicationTaxModel applicationTaxModel = null;
		try {
			applicationTaxModel = applicationService.getTaxDetails(prTrNumber);
		} catch (UnauthorizedException e) {
			log.error("error in getting insuranceDetailsModel " + e);
		}
		return applicationTaxModel;
	}

	protected List<PermitDetailsModel> getPermitDetails(Long vehicleRcId) {
		List<PermitDetailsModel> permitDetailsModelList = null;
		List<PermitHeaderModel> permitHeaderModel = null;
		try {
			permitHeaderModel = applicationService.getPermitDetails(vehicleRcId);
			if (!ObjectsUtil.isNullOrEmpty(permitHeaderModel)) {
				permitDetailsModelList = new ArrayList<>();
				for (PermitHeaderModel phm : permitHeaderModel) {
					PermitDetailsModel pdm = new PermitDetailsModel();
					pdm.setExpiryDate(phm.getValidToDate());
					pdm.setIssueDate(phm.getIssueDate());
					pdm.setPermitNumber(phm.getPermitNo());
					pdm.setName(phm.getPermitTypeName());
					pdm.setRtaOfficeName(phm.getRtaOfficeName());
					permitDetailsModelList.add(pdm);
				}
			}
		} catch (RestClientException e) {
			log.error("error in getting permitDetailsModel " + e);
		} catch (Exception e) {
			log.error("error in getting permitDetailsModel " + e);
		}
		return permitDetailsModelList;
	}

	protected FitnessDetailsModel getFitnessDetails(Long vehicleRcId) {
		FitnessDetailsModel fitnessDetailsModel = null;
		try {
			fitnessDetailsModel = applicationService.getFitnessDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting fitnessDetailsModel" + e);
		} catch (Exception e) {
			log.error("error in getting fitnessDetailsModel" + e);
		}
		return fitnessDetailsModel;
	}

	protected PucDetailsModel getPucDetails(Long vehicleRcId) {
		PucDetailsModel pucDetails = null;
		try {
			pucDetails = applicationService.getPucDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting PucDetails" + e);
		} catch (Exception e) {
			log.error("error in getting PucDetails" + e);
		}
		return pucDetails;
	}

	protected NocDetails getNocDetails(Long vehicleRcId) {
		NocDetails nocDetails = null;
		try {
			nocDetails = applicationService.getNOCDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting nocDetails" + e);
		} catch (Exception e) {
			log.error("error in getting nocDetails" + e);
		}
		return nocDetails;
	}

	protected LicenseHolderPermitDetails getDriversLicenseDtls(String aadharNumber, String uniqueKey) {
		LicenseHolderPermitDetails licenseHolderDtlsModel = null;
		try {
			if (!StringsUtil.isNullOrEmpty(uniqueKey)) {
				licenseHolderDtlsModel = driversLicenseSelectedDetailsService.getSelectedDetailsList(aadharNumber,
						uniqueKey);
			} else {
				RegLicenseServiceResponseModel<LicenseHolderPermitDetails> response = registrationLicenseService
						.getLicenseHolderDtlsForDriver(aadharNumber, uniqueKey);
				if (response.getHttpStatus().equals(HttpStatus.OK)) {
					licenseHolderDtlsModel = response.getResponseBody();
				}
			}
			if (!ObjectsUtil.isNull(licenseHolderDtlsModel)) {
				List<DriversLicenceDetailsModel> validCovs = licenseHolderDtlsModel.getDriversPermitDetailsList();
				List<DriversLicenceDetailsModel> expiredCOV = new ArrayList<>();
				for (DriversLicenceDetailsModel model : validCovs) {
					if (!StringsUtil.isNullOrEmpty(uniqueKey) && (uniqueKey.contains(ServiceType.DL_EXPIRED.getCode())
							|| uniqueKey.contains(ServiceType.DL_RENEWAL.getCode()))) {
						expiredCOV.add(null);
					} else if ((model.getValidTo() != null && model.getValidTo().before(new Date()))
							|| StringsUtil.isNullOrEmpty(model.getDlNo())) {
						expiredCOV.add(model);
					}
				}
				if (expiredCOV != null && expiredCOV.size() > 0) {
					validCovs.removeAll(expiredCOV);
				}
				licenseHolderDtlsModel.setDriversPermitDetailsList(validCovs);
			}
		} catch (Exception e) {
			log.error("error in getting getLicenseHolderDtls " + e);
		}
		return licenseHolderDtlsModel;
	}

	protected List<LearnersPermitDtlModel> getLearnersPermitDtlList(Long llHolderId) {
		List<LearnersPermitDtlModel> learnersPermitDtlList = null;
		try {
			RegLicenseServiceResponseModel<List<LearnersPermitDtlModel>> response = registrationLicenseService
					.getLearnerPermitDtls(llHolderId);
			if (response.getHttpStatus().equals(HttpStatus.OK)) {
				learnersPermitDtlList = response.getResponseBody();
			}
		} catch (Exception e) {
			log.error("error in getting getLearnersPermitDtlList " + e);
		}

		return learnersPermitDtlList;
	}

	/*
	 * protected Boolean hasAppliedHPA(String prNumber) { Boolean hasAppliedHPA
	 * = null; try { hasAppliedHPA = applicationService.hasAppliedHPA(prNumber);
	 * } catch (RestClientException e) { log.error(
	 * "error in getting fitnessDetailsModel" + e); } catch (Exception e) {
	 * log.error("error in getting fitnessDetailsModel" + e); } return
	 * hasAppliedHPA; }
	 */

	/*
	 * protected List<VcrBookingData> getVcrModel(String prNumber) {
	 * List<VcrBookingData> cdrm = null; try { cdrm =
	 * applicationService.getVcrDetails(prNumber); } catch (RestClientException
	 * e) { log.error("error in getting vcr details" + e); } catch (Exception e)
	 * { log.error("error in getting vcr details" + e); } return cdrm; }
	 */

	protected SuspendedRCNumberModel getSuspensionDetails(Long vehicleRcId) {

		RegistrationServiceResponseModel<SuspendedRCNumberModel> res = null;
		try {
			res = registrationService.getSuspensionDetails(vehicleRcId);
		} catch (Exception ex) {
			log.error("Error while calling registration for getSuspensionDetails : " + ex.getMessage());
		}
		if (res.getHttpStatus() == HttpStatus.OK) {
			return res.getResponseBody();
		} else {
			log.error("Http Status getSuspensionDetails + " + res.getHttpStatus());
			return null;
		}
	}

	// for Financier fresh rc
	protected RegistrationServiceResponseModel<FinancerFreshContactDetailsModel> getFinancierContactDetails(
			Long vehicleRcId) {
		RegistrationServiceResponseModel<FinancerFreshContactDetailsModel> response = null;
		try {
			response = registrationService.getFinancerFreshContactDetails(vehicleRcId);
		} catch (RestClientException e) {
			log.error("error in getting FinancerFreshContactDetailsModel " + e);
		} catch (Exception e) {
			log.error("error in getting FinancerFreshContactDetailsModel " + e);
		}
		return response;

	}

	public boolean vcrValidate(String prNumber) {
		log.info("::::::vcrValidate::::start::");
		List<ChallanDetailsModel> vcrModel = getChallanList(prNumber);
		if (!ObjectsUtil.isNull(vcrModel)) {
			for (ChallanDetailsModel vcrBookingData : vcrModel) {
				if ("P".equalsIgnoreCase(vcrBookingData.getStatus())
						|| "O".equalsIgnoreCase(vcrBookingData.getStatus())) {
					return true;
				}
			}
		}
		log.info("::::::vcrValidate::::start::");
		return false;
	}
	
	protected StoppageTaxDetailsModel getStoppageTaxDetails(String prNumber) {

		RegistrationServiceResponseModel<StoppageTaxDetailsModel> res = null;
		try {
			res = registrationService.getStoppageTaxDetails(prNumber);
		} catch (Exception ex) {
			log.error("Error while calling registration for getStoppageTaxDetails : " + ex.getMessage());
		}
		if (res.getHttpStatus() == HttpStatus.OK) {
			return res.getResponseBody();
		} else {
			log.error("Http Status getStoppageTaxDetails + " + res.getHttpStatus());
			return null;
		}
	}

}

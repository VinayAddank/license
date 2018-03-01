package org.rta.citizen.common.service.payment.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.converters.payment.FeeDetailConverter;
import org.rta.citizen.common.converters.payment.TaxDetailConverter;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.dao.UserSessionDAO;
import org.rta.citizen.common.dao.payment.FeeDetailDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.FeeDetailEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.entity.payment.TransactionDetailEntity;
import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.MonthType;
import org.rta.citizen.common.enums.PaymentGatewayType;
import org.rta.citizen.common.enums.RegistrationCategoryType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TaxType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.FcDetailsModel;
import org.rta.citizen.common.model.RegLicenseServiceResponseModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.VehicleBodyModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.model.payment.DifferentialTaxFeeModel;
import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.model.payment.FeeRuleModel;
import org.rta.citizen.common.model.payment.RegFeeDetailModel;
import org.rta.citizen.common.model.payment.TaxFeeModel;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.model.payment.TaxRuleModel;
import org.rta.citizen.common.model.payment.TaxTypeModel;
import org.rta.citizen.common.model.vcr.VcrModel;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.payment.TaxFeeService;
import org.rta.citizen.common.service.registration.RegistrationLicenseService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.service.rtaapplication.RtaApplicationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.NumberParser;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.dao.LicenceDAO;
import org.rta.citizen.licence.dao.updated.LicensePermitDetailsDAO;
import org.rta.citizen.licence.entity.LlrVehicleClassMasterEntity;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.enums.LicenceVehicleClass;
import org.rta.citizen.licence.model.updated.DriversLicenceDetailsModel;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.permit.model.PermitNewRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaxFeeServiceImpl implements TaxFeeService {

	private static final Logger log = Logger.getLogger(TaxFeeServiceImpl.class);

	public static long penaltyDate = 1480463999l;
	@Autowired
	private ApplicationDAO applicationDAO;
	@Autowired
	private TaxDetailDAO taxDetailDAO;
	@Autowired
	private FeeDetailDAO feeDetailDAO;
	@Autowired
	private TaxDetailConverter taxDetailConverter;
	@Autowired
	private FeeDetailConverter feeDetailConverter;
	@Autowired
	private RegistrationService registrationService;
	@Value("${drools.tax.url}")
	private String droolsTaxURL;
	@Value("${drools.fee.url}")
	private String droolsFeeURL;
	@Value("${contenttype}")
	String contenttype;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private TransactionDetailDAO transactionDetailDAO;
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;
	@Value("${drools.license.fee.url}")
	private String droolsLicenseFeeURL;
	@Value("${drools.user.fee.url}")
	private String droolsUserFeeURL;

	@Value("${dealer.sighnup.fee}")
	private double dealerSighnupFee;

	@Autowired
	private RtaApplicationService rtaApplicationService;
	@Autowired
	private LicensePermitDetailsDAO licencePermitDetailsDAO;
	@Autowired
	private LicenceDAO licenceDAO;
	@Value("${rta.lifetax.validty}")
	int lifetaxValidty;
	@Value("${rta.greenax.validty.transport}")
	int greentaxValidtyTransport;
	@Value("${rta.greenax.validty.nontransport}")
	int greentaxValidtyNonTransport;
	@Value(value = "${service.vcr.host}")
	private String HOST;

	@Value(value = "${service.vcr.port}")
	private String PORT;

	@Value(value = "${service.vcr.path}")
	private String ROOT_URL;

	@Autowired
	private RegistrationLicenseService registrationLicenseService;

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Autowired
	private ActivitiService activitiService;

	@Override
	@Transactional
	public TaxFeeModel taxFeeCal(long sessionId, Boolean isDispatch, String appNo, int quartelyType) {
		log.debug("::taxFeeCal:::::appNo " + appNo + " sessionId " + sessionId);
		ApplicationEntity appEntity = null;
		if (appNo != null)
			appEntity = applicationDAO.getApplication(appNo);
		else
			appEntity = applicationDAO.getApplicationFromSession(sessionId);
		RegistrationServiceResponseModel<ApplicationTaxModel> result = null;
		try {
			result = registrationService.getTaxDetails(appEntity.getLoginHistory().getUniqueKey());
		} catch (RestClientException e) {
			log.error("error when getting tr details : " + e);
		}
		if (ObjectsUtil.isNull(result)) {
			log.debug("tr details not found for tr number : " + appEntity.getLoginHistory().getUniqueKey());
			return null;
		}
		if (result.getHttpStatus() != HttpStatus.OK) {
			log.debug("error in http request " + result.getHttpStatus());
			return null;
		}
		ApplicationTaxModel appTaxModel = result.getResponseBody();
		TaxFeeModel taxFeeModel = new TaxFeeModel();
		String custName = appTaxModel.getCitizenName();
		FeeModel feeModel = null;
		TaxModel taxModel = null;
		feeModel = updateFeeDetails(appTaxModel, appEntity, isDispatch);
		taxModel = updateTaxDetails(appTaxModel, appEntity, quartelyType);
		if (taxModel != null)
			taxFeeModel.setTaxModel(taxModel);
		if (feeModel != null)
			taxFeeModel.setFeeModel(feeModel);
		if (taxModel != null) {
			if (feeModel != null) {
				taxFeeModel.setGrandTotal(String.valueOf((feeModel.getTotalFee() == null ? 0
						: Double.parseDouble(feeModel.getTotalFee())
								+ (taxModel.getTotalAmt() == null ? 0 : Double.parseDouble(taxModel.getTotalAmt())))));
			} else {
				taxFeeModel.setGrandTotal(taxModel.getTotalAmt());
			}
		} else {
			if (feeModel != null)
				taxFeeModel.setGrandTotal(feeModel.getTotalFee());
		}
		taxFeeModel.setCustomerName(custName);
		taxFeeModel = isSBIVerification(taxFeeModel, appEntity);
		log.info("::CITIZEN::::::::::taxFeeCal ::::end::::::::: " + taxFeeModel.getGrandTotal());
		return taxFeeModel;
	}

	public TaxModel updateTaxDetails(ApplicationTaxModel appTaxModel, ApplicationEntity appEntity, int quartelyType) {
		log.debug(":updateTaxDetails::::start:: " + appEntity.getServiceCode() + " - " + quartelyType
				+ -+appTaxModel.getTaxType());
		TaxRuleModel taxRuleModel = new TaxRuleModel();
		TaxModel taxModel = new TaxModel();
		taxRuleModel.setStateCode("AP");
		taxRuleModel.setFuelType(appTaxModel.getFuelType());
		taxRuleModel.setRegCategory(appTaxModel.getRegType());
		taxRuleModel.setServiceCode(appEntity.getServiceCode());
		taxRuleModel.setGvw(appTaxModel.getGvw());
		taxRuleModel.setVehicleClassCategory(appTaxModel.getVehicleSubClass());
		taxRuleModel.setVehicleClass(appTaxModel.getVehicleClass());
		taxRuleModel.setTaxType(TaxType.getTaxType(appTaxModel.getTaxType()).getCode());
		taxRuleModel.setSeatingCapacity(appTaxModel.getSeatingCapacity());
		taxRuleModel.setMonthType(calculateQuarterlyTax());
		taxRuleModel.setInvoiceAmount((int) appTaxModel.getInvoiceAmt());
		taxRuleModel.setOwnerType(appTaxModel.getOwnerType());
		taxRuleModel.setUlw(appTaxModel.getUlw());
		taxRuleModel.setTrIssueDate(appTaxModel.getTrIssueTime());
		taxRuleModel.setPrIssueDate(appTaxModel.getPrIssueTime());
		taxRuleModel.setTaxValidUpto(appTaxModel.getTaxValidUpto());
		taxRuleModel.setPeriodicTaxType(appTaxModel.getPeriodicTaxType());
		if (appTaxModel.getPermitHeaderModel() != null) {
			taxRuleModel.setPermitType(appTaxModel.getPermitHeaderModel().getPermitType());
			taxRuleModel.setPermitSubType(appTaxModel.getPermitHeaderModel().getPermitSubType());
			if (DateUtil.isSameOrGreaterDate(appTaxModel.getPermitHeaderModel().getValidToDate(),
					DateUtil.dateFormater(DateUtil.getDateInString(new Date())))) {
				taxRuleModel.setPermitValidTo(appTaxModel.getPermitHeaderModel().getValidToDate());
				taxRuleModel.setIsPermitValid(true);
			}
		}
		TaxDetailEntity taxDetailEntity = new TaxDetailEntity();
		switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
		case DIFFERENTIAL_TAX:
			taxDetailEntity = saveDiffTaxDetails(appEntity, appTaxModel, taxRuleModel);
			break;
		case REGISTRATION_RENEWAL:
			taxDetailEntity = saveGreenTaxRegistRenewalDetails(appEntity, appTaxModel, taxRuleModel);
			break;
		case FC_RENEWAL:
			taxDetailEntity = saveGreenTaxFitnessRenewalDetails(appEntity, appTaxModel, taxRuleModel);
			break;
		case VEHICLE_ATLERATION:
			taxDetailEntity = saveTaxDeatilsVehicleAlteration(appEntity, appTaxModel, taxRuleModel);
			break;
		case PERMIT_FRESH:
			taxDetailEntity = saveTaxDeatilsPermitNew(appEntity, appTaxModel, taxRuleModel);
			break;
		case PERMIT_VARIATIONS:
			taxDetailEntity = saveTaxDeatilsPermitVariations(appEntity, appTaxModel, taxRuleModel);
			break;
		case PAY_TAX:
			taxDetailEntity = saveTaxDeatilsPayTax(appEntity, appTaxModel, taxRuleModel, quartelyType);
		}
		if (taxDetailEntity == null)
			return null;
		taxDetailDAO.saveOrUpdate(taxDetailEntity);
		taxModel = taxDetailConverter.convertToModel(taxDetailEntity);
		return taxModel;
	}

	public TaxDetailEntity saveTaxDeatilsPayTax(ApplicationEntity appEntity, ApplicationTaxModel appTaxModel,
			TaxRuleModel taxRuleModel, int quartelyType) {
		TaxDetailEntity taxDetailEntity = null;

		double greenTax = 0.0;
		boolean isGreenTax = false;

		VcrModel vcrTaxData = null;
		double compoundFee = 0.0d;
		try {
			vcrTaxData = registrationService.getVCRTax("RC", appEntity.getLoginHistory().getUniqueKey());
			if (vcrTaxData.getVcrFlag()) {
				compoundFee = vcrTaxData.getFine();
				taxRuleModel.setVcrBookedDt(vcrTaxData.getVcrBookedDt());
				if (vcrTaxData.getPliedAs().equals(""))
					taxRuleModel.setPliedAs(null);
				else
					taxRuleModel.setPliedAs(vcrTaxData.getPliedAs());
				taxRuleModel.setVehicleSiezed(vcrTaxData.getVehicleSiezed());
				taxRuleModel.setVcrFlag(vcrTaxData.getVcrFlag());
			}
			log.info(" ::::::::: fee  :::::::::::::" + vcrTaxData.getFine());
		} catch (Exception e) {
			log.error(" ::::::::: Compound fee error :::::::::::::" + e.getLocalizedMessage());
		}

		log.info("::quartelyType:::111::::: " + quartelyType);
		if (!applicationService.taxValidate(appTaxModel) && null == vcrTaxData) {
			log.error(":::: Tax haven't Expire::::::");
			return null;
		}
		if (applicationService.payTaxValidate(appTaxModel)) {
			log.error("::::Life Tax aren't allowed::::::");
			return null;
		}

		if(DateUtil.compareDatePartOnly(DateUtil.toCurrentUTCTimeStamp(), appTaxModel.getCessFeeValidUpTo())){
			taxRuleModel.setIsCessFeeValid(true);	
		}
		log.debug(":::Is Cess fee need to collect ::::: "+ taxRuleModel.getIsCessFeeValid() + ":::appTaxModel.getCessFeeValidUpTo()="+appTaxModel.getCessFeeValidUpTo());	
		taxRuleModel.setGreenTax(false);
		taxRuleModel.setTaxValidUpto(appTaxModel.getTaxValidUpto());
		taxRuleModel.setQuarterlyTaxType(quartelyType);
		taxRuleModel = getTaxByRule(taxRuleModel);		
		taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		if (taxDetailEntity != null) {
			if(taxRuleModel.getIsCessFeeValid()){
				taxDetailEntity.setCessFeeValidUpto(DateUtil.addYearsWithNoReduceDay(appTaxModel.getCessFeeValidUpTo(), 1));

				taxDetailEntity.setCessFee(taxRuleModel.getCessFee());
			}
			taxDetailEntity.setServiceFee(taxRuleModel.getServiceFee());
			taxDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setGreenTaxAmt(greenTax);
			taxDetailEntity.setPenaltyAmt(NumberParser.getRoundNextTen(taxDetailEntity.getPenaltyAmt()));
			taxDetailEntity.setTotalAmt(NumberParser.getRoundNextTen(
					taxRuleModel.getTaxAmount() + taxRuleModel.getServiceFee() + taxRuleModel.getCessFee() + taxRuleModel.getTaxAmtArrears()
							+ taxRuleModel.getPenalty() + taxRuleModel.getPenaltyArrears()));
			if (isGreenTax) {
				taxDetailEntity.setTaxType(TaxType.GREEN_TAX.getCode());
				switch (RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())) {
				case NON_TRANSPORT:
					taxDetailEntity.setValidUpto(DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), lifetaxValidty));
					taxDetailEntity.setGreenTaxValidTo(
							DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyNonTransport));
					break;
				case TRANSPORT:
					taxDetailEntity.setValidUpto(taxValidty());
					taxDetailEntity.setGreenTaxValidTo(
							DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyTransport));
					break;
				}
			}
			switch (TaxType.getTaxType(TaxType.getTaxType(appTaxModel.getTaxType()).getValue())) {
			case QUARTERLY_TAX:
			case ANNUAL_TAX:
			case HALFYEARLY_TAX:
				taxDetailEntity.setValidUpto(taxValidty(quartelyType));
				if (quartelyType == 0 || quartelyType == 3)
					taxDetailEntity.setTaxType(TaxType.getTaxType(1).getCode());
				else
					taxDetailEntity.setTaxType(TaxType.getTaxType(quartelyType).getCode());
				break;
			}
			taxDetailEntity.setTaxAmtArrears(taxRuleModel.getTaxAmtArrears());
			taxDetailEntity.setPenaltyAmt(taxRuleModel.getPenalty());
			taxDetailEntity.setPenaltyAmtArrears(taxRuleModel.getPenaltyArrears());
			if (!ObjectsUtil.isNull(compoundFee)) {
				taxDetailEntity.setCompoundFee(compoundFee);
			}

		} else {
			taxDetailEntity = new TaxDetailEntity();
			if (taxRuleModel.getIsCessFeeValid()) {
				taxDetailEntity.setCessFeeValidUpto(DateUtil.addYears(appTaxModel.getCessFeeValidUpTo(), 1));
				taxDetailEntity.setCessFee(taxRuleModel.getCessFee());
			}
			taxDetailEntity.setServiceFee(taxRuleModel.getServiceFee());
			taxDetailEntity.setApplicationId(appEntity);
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setGreenTaxAmt(greenTax);
			taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTotalAmt(NumberParser.getRoundNextTen(
					taxRuleModel.getTaxAmount() + taxRuleModel.getServiceFee() + taxRuleModel.getCessFee() + taxRuleModel.getTaxAmtArrears()
							+ taxRuleModel.getPenalty() + taxRuleModel.getPenaltyArrears()));

			if (isGreenTax) {
				taxDetailEntity.setTaxType(TaxType.GREEN_TAX.getCode());
				switch (RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())) {
				case NON_TRANSPORT:
					taxDetailEntity.setValidUpto(DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), lifetaxValidty));
					taxDetailEntity.setGreenTaxValidTo(
							DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyNonTransport));
					break;
				case TRANSPORT:
					taxDetailEntity.setValidUpto(taxValidty());
					taxDetailEntity.setGreenTaxValidTo(
							DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyTransport));
					break;
				}
			}
			switch (TaxType.getTaxType(TaxType.getTaxType(appTaxModel.getTaxType()).getValue())) {
			case QUARTERLY_TAX:
			case ANNUAL_TAX:
			case HALFYEARLY_TAX:
				taxDetailEntity.setValidUpto(taxValidty(quartelyType));
				if (quartelyType == 0 || quartelyType == 3)
					taxDetailEntity.setTaxType(TaxType.getTaxType(1).getCode());
				else
					taxDetailEntity.setTaxType(TaxType.getTaxType(quartelyType).getCode());
				break;
			}
			taxDetailEntity.setTaxAmtArrears(taxRuleModel.getTaxAmtArrears());
			taxDetailEntity.setPenaltyAmt(taxRuleModel.getPenalty());
			taxDetailEntity.setPenaltyAmtArrears(taxRuleModel.getPenaltyArrears());
			if (!ObjectsUtil.isNull(compoundFee)) {
				taxDetailEntity.setCompoundFee(compoundFee);
			}
		}
		return taxDetailEntity;
	}

	public TaxDetailEntity saveTaxDeatilsPermitVariations(ApplicationEntity appEntity, ApplicationTaxModel appTaxModel,
			TaxRuleModel taxRuleModel) {

		taxRuleModel.setOldTaxAmt((int) appTaxModel.getTaxAmt());
		try {
			ApplicationFormDataEntity permitNewAppFormEntity = applicationFormDataDAO
					.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.PCV_FORM.getLabel());
			if (permitNewAppFormEntity != null) {
				PermitNewRequestModel permitNewRequestModel = new ObjectMapper()
						.readValue(permitNewAppFormEntity.getFormData(), PermitNewRequestModel.class);
				taxRuleModel.setPermitSubType(permitNewRequestModel.getPermitSubType());
				log.debug(":::Permit Type code::::: " + permitNewRequestModel.getPermitType() + " -  "
						+ permitNewRequestModel.getPermitClass() + " -- " + appEntity.getApplicationNumber());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		taxRuleModel = getTaxByRule(taxRuleModel);
		TaxDetailEntity taxDetailEntity = new TaxDetailEntity();
		taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		if (taxDetailEntity != null) {
			taxDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setPenaltyAmt(NumberParser.getRoundNextTen(taxDetailEntity.getPenaltyAmt()));
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		} else {
			taxDetailEntity = new TaxDetailEntity();
			taxDetailEntity.setApplicationId(appEntity);
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		}
		return taxDetailEntity;
	}

	public TaxDetailEntity saveTaxDeatilsPermitNew(ApplicationEntity appEntity, ApplicationTaxModel appTaxModel,
			TaxRuleModel taxRuleModel) {

		log.info("::::::" + appTaxModel.getTaxAmt());
		taxRuleModel.setOldTaxAmt((int) appTaxModel.getTaxAmt());
		try {
			ApplicationFormDataEntity permitNewAppFormEntity = applicationFormDataDAO
					.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.PCF_FORM.getLabel());
			if (permitNewAppFormEntity != null) {
				PermitNewRequestModel permitNewRequestModel = new ObjectMapper()
						.readValue(permitNewAppFormEntity.getFormData(), PermitNewRequestModel.class);
				taxRuleModel.setPermitSubType(permitNewRequestModel.getPermitSubType());
				taxRuleModel.setPermitType(permitNewRequestModel.getPermitType());
				log.debug(":::Permit Type code::::: " + permitNewRequestModel.getPermitType() + " -  "
						+ permitNewRequestModel.getPermitClass() + " -- " + appEntity.getApplicationNumber());
				if(permitNewRequestModel.getPermitClass().equalsIgnoreCase("TEMPORARY"))
					return null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		taxRuleModel = getTaxByRule(taxRuleModel);
		TaxDetailEntity taxDetailEntity = new TaxDetailEntity();
		taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		if (taxDetailEntity != null) {
			taxDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setPenaltyAmt(NumberParser.getRoundNextTen(taxDetailEntity.getPenaltyAmt()));
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		} else {
			taxDetailEntity = new TaxDetailEntity();
			taxDetailEntity.setApplicationId(appEntity);
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		}
		return taxDetailEntity;
	}

	public TaxDetailEntity saveTaxDeatilsVehicleAlteration(ApplicationEntity appEntity, ApplicationTaxModel appTaxModel,
			TaxRuleModel taxRuleModel) {

		taxRuleModel.setOldTaxAmt(appTaxModel.getTaxAmt());
		ApplicationFormDataEntity appFormEntity = applicationFormDataDAO
				.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.VA_FORM.getLabel());
		String instanceId = applicationService.getProcessInstanceId(appEntity.getLoginHistory().getSessionId());
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
		if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			for (RtaTaskInfo task : actRes.getActiveTasks()) {
				if (task.getTaskDefKey().equalsIgnoreCase("payment")) {
					log.debug(":::Vehicle Alteration:::: " + task.getTaskDefKey());
					return null;
				}
			}
		}
		boolean isTaxApply = true;
		if (appFormEntity != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				VehicleBodyModel vehicleBodyModel = mapper.readValue(appFormEntity.getFormData(),
						VehicleBodyModel.class);
				RegistrationServiceResponseModel<TaxTypeModel> taxTypeResponse = null;
				TaxTypeModel taxTypeModel = null;
				for (AlterationCategory alterationCategory : vehicleBodyModel.getAlterationCategory()) {
					switch (AlterationCategory.getAlterationCategory(alterationCategory.getLabel())) {
					case SEATING_CAPACITY:
						switch (RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())) {
						case NON_TRANSPORT:
							isTaxApply = false;
							break;
						case TRANSPORT:
							taxRuleModel.setSeatingCapacity(vehicleBodyModel.getSeatingCapacity());
							break;
						}
						break;
					case BODY_TYPE:
						break;
					case FUEL_TYPE:
						break;
					case VEHICLE_TYPE:
						taxTypeResponse = registrationService.getTaxTypeByCov(vehicleBodyModel.getVehicleSubClass());
						if (taxTypeResponse.getHttpStatus().equals(HttpStatus.OK)) {
							taxTypeModel = taxTypeResponse.getResponseBody();
							log.info("::::tax type in case of vehicle alteration: " + taxTypeModel.getTaxTypeCode());
							if (taxTypeModel.getTaxTypeCode().equalsIgnoreCase(TaxType.QUARTERLY_TAX.getCode())) {
								taxRuleModel.setTaxType(TaxType.QUARTERLY_TAX.getCode());
							} else {
								taxRuleModel.setTaxType(TaxType.LIFE_TAX.getCode());
							}
						}
						taxRuleModel.setOldTaxType(TaxType.getTaxType(appTaxModel.getOldTaxType()).getCode());
						int regCatId = 0;
						if (vehicleBodyModel.getRegistrationCategoryCode()
								.equals(RegistrationCategoryType.TRANSPORT.getCode())) {
							regCatId = RegistrationCategoryType.TRANSPORT.getValue();
						} else {
							regCatId = RegistrationCategoryType.NON_TRANSPORT.getValue();
						}
						taxRuleModel.setRegCategory(regCatId);
						taxRuleModel.setVehicleClassCategory(vehicleBodyModel.getVehicleSubClass());
						break;
					case ENGINE_ALTERATION:
						break;
					}
				}
				if (!isTaxApply) {
					return null;
				}
			} catch (Exception e) {
				log.error("Exception while getting form data for appId :" + appEntity.getApplicationId()
						+ " and form code : " + appEntity.getApplicationId());
			}
		}

		taxRuleModel = getTaxByRule(taxRuleModel);
		TaxDetailEntity taxDetailEntity = new TaxDetailEntity();
		taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		if (taxDetailEntity != null) {
			taxDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setTaxType(taxRuleModel.getTaxType());
			taxDetailEntity.setPenaltyAmt(NumberParser.getRoundNextTen(taxDetailEntity.getPenaltyAmt()));
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		} else {
			taxDetailEntity = new TaxDetailEntity();
			taxDetailEntity.setApplicationId(appEntity);
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setTaxType(taxRuleModel.getTaxType());
			taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		}
		return taxDetailEntity;
	}

	public TaxDetailEntity saveGreenTaxRegistRenewalDetails(ApplicationEntity appEntity,
			ApplicationTaxModel appTaxModel, TaxRuleModel taxRuleModel) {
		taxRuleModel.setGreenTax(true);
		taxRuleModel = getTaxByRule(taxRuleModel);
		TaxDetailEntity taxDetailEntity = new TaxDetailEntity();
		taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		if (taxDetailEntity != null) {
			taxDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setGreenTaxAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setTotalAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setTaxType(TaxType.GREEN_TAX.getLabel());
			switch (RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())) {
			case NON_TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyNonTransport));
				break;
			case TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyTransport));
				break;
			}
		} else {
			taxDetailEntity = new TaxDetailEntity();
			taxDetailEntity.setApplicationId(appEntity);
			taxDetailEntity.setGreenTaxAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTotalAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setTaxType(TaxType.GREEN_TAX.getLabel());
			switch (RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())) {
			case NON_TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyNonTransport));
				break;
			case TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyTransport));
				break;
			}
		}
		return taxDetailEntity;
	}

	public TaxDetailEntity saveGreenTaxFitnessRenewalDetails(ApplicationEntity appEntity,
			ApplicationTaxModel appTaxModel, TaxRuleModel taxRuleModel) {
		if ((DateUtil.toCurrentUTCTimeStamp() >= DateUtil.addYears(appTaxModel.getPrIssueTime(),
				RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())
						.equals(RegistrationCategoryType.TRANSPORT) ? SomeConstants.SEVEN : SomeConstants.FIFTEEN))
				&& (appTaxModel.getGreenTaxValidTo() < DateUtil.toCurrentUTCTimeStamp())) {
			taxRuleModel.setGreenTax(true);
		}
		taxRuleModel = getTaxByRule(taxRuleModel);
		TaxDetailEntity taxDetailEntity = new TaxDetailEntity();
		taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		if (taxDetailEntity != null) {
			taxDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setGreenTaxAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setTotalAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setTaxType(TaxType.GREEN_TAX.getCode());
			switch (RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())) {
			case NON_TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyNonTransport));
				break;
			case TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyTransport));
				break;
			}
		} else {
			taxDetailEntity = new TaxDetailEntity();
			taxDetailEntity.setApplicationId(appEntity);
			taxDetailEntity.setGreenTaxAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTotalAmt(taxRuleModel.getGreenTaxAmt());
			taxDetailEntity.setTaxType(TaxType.GREEN_TAX.getCode());
			switch (RegistrationCategoryType.getRegistrationCategoryType(appTaxModel.getRegType())) {
			case NON_TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyNonTransport));
				break;
			case TRANSPORT:
				taxDetailEntity.setGreenTaxValidTo(
						DateUtil.addYears(DateUtil.toCurrentUTCTimeStamp(), greentaxValidtyTransport));
				break;
			}
		}
		return taxDetailEntity;
	}

	public TaxDetailEntity saveDiffTaxDetails(ApplicationEntity appEntity, ApplicationTaxModel appTaxModel,
			TaxRuleModel taxRuleModel) {

		taxRuleModel.setOldTaxAmt(appTaxModel.getTaxAmt());
		taxRuleModel = getTaxByRule(taxRuleModel);
		TaxDetailEntity taxDetailEntity = new TaxDetailEntity();
		taxDetailEntity = taxDetailDAO.getByAppId(appEntity);
		if (taxDetailEntity != null) {
			taxDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setPenaltyAmt(NumberParser.getRoundNextTen(taxDetailEntity.getPenaltyAmt()));
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		} else {
			taxDetailEntity = new TaxDetailEntity();
			taxDetailEntity.setApplicationId(appEntity);
			taxDetailEntity.setTaxAmt(taxRuleModel.getTaxAmount());
			taxDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			taxDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
			taxDetailEntity.setTotalAmt(taxRuleModel.getTaxAmount());
		}
		return taxDetailEntity;
	}

	public long taxValidty() {
		int monthType = calculateQuarterlyTax();
		long taxValid = DateUtil.toLastDayOfMonth(monthType - 1);
		return taxValid;
	}

	public TaxRuleModel calculateDiffTax(ApplicationEntity appEntity, ApplicationTaxModel appTaxModel,
			TaxRuleModel taxRuleModel) {
		log.debug("::calculateDiffTax:::::start:::: ");
		taxRuleModel = getTaxByRule(taxRuleModel);
		log.debug("::calculateDiffTax::: " + taxRuleModel.getTaxAmount() + " - " + (int) appTaxModel.getTaxAmt());
		int diffTax = taxRuleModel.getTaxAmount() - (int) appTaxModel.getTaxAmt();
		if (diffTax < 0) {
			log.debug("::calculateDiffTax::diffTax less than 0:: " + diffTax);
			diffTax = 0;
			// throw new IllegalArgumentException("CalculateDiffTax diffTax less
			// than 0 Internal Server Error:");
		}
		taxRuleModel.setTaxAmount(diffTax);
		return taxRuleModel;
	}

	public FeeRuleModel calculateDiffFee(ApplicationEntity appEntity, ApplicationTaxModel appTaxModel,
			FeeRuleModel feeRuleModel) {
		// feeRuleModel = getFeeByRule(feeRuleModel);
		if (ServiceType.DIFFERENTIAL_TAX.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {

			try {
				RegFeeDetailModel regFeeDetailModel = null;
				regFeeDetailModel = appTaxModel.getRegFeeDetailModel();
				if (regFeeDetailModel == null) {
					return feeRuleModel;
				}
				double oldPrFee = Double.parseDouble(regFeeDetailModel.getRegFee());
				if (oldPrFee > feeRuleModel.getPrFee())
					feeRuleModel.setPrFee(0);
				else
					feeRuleModel.setPrFee(feeRuleModel.getPrFee() - oldPrFee);
				double oldServiceCharge = 0.0d;
				oldServiceCharge = Double.parseDouble(regFeeDetailModel.getServiceCharge());
				if (oldServiceCharge > feeRuleModel.getPrService())
					feeRuleModel.setPrService(0);
				else
					feeRuleModel.setPrService(feeRuleModel.getPrService() - oldServiceCharge);

				if (regFeeDetailModel.getFitnessFeeModel() != null) {

					double oldFitnessFee = Double.parseDouble(regFeeDetailModel.getFitnessFeeModel().getFitnessFee());
					if (oldFitnessFee > feeRuleModel.getFitnessFee())
						feeRuleModel.setFitnessFee(0);
					else
						feeRuleModel.setFitnessFee(feeRuleModel.getFitnessFee() - oldFitnessFee);
					double oldFitServiceCharge = Double
							.parseDouble(regFeeDetailModel.getFitnessFeeModel().getFitnessService());
					if (oldFitServiceCharge > feeRuleModel.getFitnessService())
						feeRuleModel.setFitnessService(0);
					else
						feeRuleModel.setFitnessService(feeRuleModel.getFitnessService() - oldFitServiceCharge);

				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				feeRuleModel.setPrFee(0);
				feeRuleModel.setPrService(0);
				feeRuleModel.setFitnessFee(0);
				feeRuleModel.setFitnessService(0);
				return feeRuleModel;
			}
		}

		return feeRuleModel;
	}

	public FeeModel updateFeeDetails(ApplicationTaxModel appTaxModel, ApplicationEntity appEntity, boolean isDiapatch) {
		log.debug("::updateFeeDetails::: " + appTaxModel.getRegType());
		FeeRuleModel feeRuleModel = new FeeRuleModel();
		FeeModel feeModel = null;
		feeRuleModel.setSeatingCapacity(appTaxModel.getSeatingCapacity());
		feeRuleModel.setRegCategory(appTaxModel.getRegType());
		feeRuleModel.setOldRegCategory(appTaxModel.getRegType());
		feeRuleModel.setServiceCode(appEntity.getServiceCode());
		feeRuleModel.setGvw(appTaxModel.getGvw());
		feeRuleModel.setVehicleClassCategory(appTaxModel.getVehicleSubClass());
		feeRuleModel.setOldClassOfVehicle(appTaxModel.getOldVehicleSubClass());
		feeRuleModel.setVehicleClass(appTaxModel.getVehicleClass());
		feeRuleModel = getPenaltyData(appEntity, feeRuleModel, appTaxModel);
		switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
		case VEHICLE_ATLERATION:
			feeModel = getFeeVehicleAlteration(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;
		case DIFFERENTIAL_TAX:
			feeModel = getFeeDiffTax(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;
		case PERMIT_FRESH:
			feeModel = getFeePermitNew(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;
		case OWNERSHIP_TRANSFER_SALE:
			feeModel = getFeeTOWSale(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;
		case OWNERSHIP_TRANSFER_DEATH:
			feeModel = getFeeTOWDeath(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;
		case PERMIT_RENEWAL:
		case PERMIT_RENEWAL_AUTH_CARD:
		case PERMIT_REPLACEMENT_VEHICLE:
		case PERMIT_SURRENDER:
		case PERMIT_VARIATIONS:
			feeModel = getFeeOtherPermit(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;
		case FC_FRESH:
		case FC_RENEWAL:
		case FC_OTHER_STATION:
		case FC_REVOCATION_CFX:
		case FC_RE_INSPECTION_SB:
			feeModel = getFeeFitness(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;

		case HPA:
		case HPT:
		case NOC_CANCELLATION:
		case NOC_ISSUE:
		case ADDRESS_CHANGE:
		case DUPLICATE_REGISTRATION:
		case VEHICLE_REASSIGNMENT:
		case SUSPENSION_REVOCATION:
		case THEFT_INTIMATION:
		case REGISTRATION_RENEWAL:
		case OWNERSHIP_TRANSFER_AUCTION:
		case FRESH_RC_FINANCIER:
			feeModel = getAllFee(feeRuleModel, appTaxModel, appEntity, isDiapatch);
			break;
		}
		return feeModel;
	}

	public FeeModel getAllFee(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel, ApplicationEntity appEntity,
			boolean isDiapatch) {
		FeeModel feeModel = null;
		feeRuleModel = getFeeByRule(feeRuleModel);
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setTotalFee(feeRuleModel.getPrFee() + feeRuleModel.getPrService()
					+ feeRuleModel.getPostalFee() + feeRuleModel.getCardFee() + feeRuleModel.getPenalty());
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setTotalFee(feeRuleModel.getPrFee() + feeRuleModel.getPrService()
					+ feeRuleModel.getPostalFee() + feeRuleModel.getCardFee() + feeRuleModel.getPenalty());
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		return feeModel;
	}

	public FeeModel getFeeFitness(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel,
			ApplicationEntity appEntity, boolean isDiapatch) {

		FeeModel feeModel = null;
		feeRuleModel = getFeeByRule(feeRuleModel);
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setFitnessFee(feeRuleModel.getFitnessFee());
			feeDetailEntity.setFitnessServiceCharge(feeRuleModel.getFitnessService());
			feeDetailEntity.setTotalFee(
					feeRuleModel.getFitnessFee() + feeRuleModel.getFitnessService() + feeRuleModel.getPenalty());
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setFitnessFee(feeRuleModel.getFitnessFee());
			feeDetailEntity.setFitnessServiceCharge(feeRuleModel.getFitnessService());
			feeDetailEntity.setTotalFee(
					feeRuleModel.getFitnessFee() + feeRuleModel.getFitnessService() + feeRuleModel.getPenalty());
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		return feeModel;
	}

	public FeeModel getFeeOtherPermit(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel,
			ApplicationEntity appEntity, boolean isDiapatch) {

		FeeModel feeModel = null;
		feeRuleModel.setPermitType(appTaxModel.getPermitHeaderModel().getPermitType());
		feeRuleModel = getFeeByRule(feeRuleModel);
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			feeDetailEntity.setPermitServiceCharge(feeRuleModel.getPermitService());
			feeDetailEntity.setOtherPermitFee(feeRuleModel.getOtherPermitFee());
			feeDetailEntity
					.setTotalFee(
							NumberParser.roundOff(
									feeRuleModel.getPermitFee() + feeRuleModel.getPermitService()
											+ feeRuleModel.getOtherPermitFee() + feeRuleModel.getPenalty(),
									"########.##"));
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			feeDetailEntity.setPermitServiceCharge(feeRuleModel.getPermitService());
			feeDetailEntity.setOtherPermitFee(feeRuleModel.getOtherPermitFee());
			feeDetailEntity
					.setTotalFee(
							NumberParser.roundOff(
									feeRuleModel.getPermitFee() + feeRuleModel.getPermitService()
											+ feeRuleModel.getOtherPermitFee() + feeRuleModel.getPenalty(),
									"########.##"));
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		return feeModel;
	}

	public FeeModel getFeeTOWDeath(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel,
			ApplicationEntity appEntity, boolean isDiapatch) {

		FeeModel feeModel = null;
		String permitTransferOrSurrender = null;
		AddressChangeModel addChangeModel = new AddressChangeModel();
		try {
			ApplicationFormDataEntity appFormDataEntity = applicationFormDataDAO
					.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.OTD_FORM.getLabel());
			if (appTaxModel.getPermitHeaderModel() != null)
				feeRuleModel.setPermitType(appTaxModel.getPermitHeaderModel().getPermitType());
			if (appFormDataEntity != null) {
				addChangeModel = new ObjectMapper().readValue(appFormDataEntity.getFormData(),
						AddressChangeModel.class);
				if (addChangeModel != null && addChangeModel.getPermitTransferType() != null
						&& addChangeModel.getPermitTransferType().equalsIgnoreCase("transfer")) {
					permitTransferOrSurrender = "transfer";
				}
				if (addChangeModel != null && addChangeModel.getPermitTransferType() != null
						&& addChangeModel.getPermitTransferType().equalsIgnoreCase("surrender")) {
					permitTransferOrSurrender = "surrender";
				}
				feeRuleModel.setPermitTransferNdSurrender(permitTransferOrSurrender);
				feeRuleModel = getFeeByRule(feeRuleModel);
				feeRuleModel.setServiceCode(ServiceType.OWNERSHIP_TRANSFER_DEATH.getCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(":::Permit Transfer/Surrender Fee calculation:::::: = " + e.getMessage());
		}

		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			if (feeRuleModel.getPrService() < feeRuleModel.getPermitService()) {
				feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPermitService());
				feeDetailEntity.setTotalFee(NumberParser.roundOff(
						(feeRuleModel.getTotalPrFee() + feeRuleModel.getTotalPermitFee()) - feeRuleModel.getPrService(),
						"########.##"));
			}
			feeDetailEntity.setTotalFee(NumberParser.roundOff((feeRuleModel.getTotalPermitFee()
					+ feeDetailEntity.getTotalFee() - feeRuleModel.getPermitService()), "########.##"));
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			if (feeRuleModel.getPrService() < feeRuleModel.getPermitService()) {
				feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPermitService());
				feeDetailEntity.setTotalFee(NumberParser.roundOff(
						(feeRuleModel.getTotalPrFee() + feeRuleModel.getTotalPermitFee()) - feeRuleModel.getPrService(),
						"########.##"));
			}
			feeDetailEntity.setTotalFee(NumberParser.roundOff((feeRuleModel.getTotalPermitFee()
					+ feeDetailEntity.getTotalFee() - feeRuleModel.getPermitService()), "########.##"));
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		feeModel.setPermitType(permitTransferOrSurrender);
		return feeModel;
	}

	public FeeModel getFeeTOWSale(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel,
			ApplicationEntity appEntity, boolean isDiapatch) {

		FeeModel feeModel = null;
		String permitTransferOrSurrender = null;
		AddressChangeModel addressChangeModel = new AddressChangeModel();
		try {
			ApplicationFormDataEntity appFormDataEntity = applicationFormDataDAO
					.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.OTS_FORM.getLabel());
			if (appTaxModel.getPermitHeaderModel() != null)
				feeRuleModel.setPermitType(appTaxModel.getPermitHeaderModel().getPermitType());
			if (appFormDataEntity != null) {
				addressChangeModel = new ObjectMapper().readValue(appFormDataEntity.getFormData(),
						AddressChangeModel.class);
				if (addressChangeModel != null && addressChangeModel.getPermitTransferType() != null
						&& addressChangeModel.getPermitTransferType().equalsIgnoreCase("transfer")) {
					permitTransferOrSurrender = "transfer";
				}
				if (addressChangeModel != null && addressChangeModel.getPermitTransferType() != null
						&& addressChangeModel.getPermitTransferType().equalsIgnoreCase("surrender")) {
					permitTransferOrSurrender = "surrender";
				}
				feeRuleModel.setPermitTransferNdSurrender(permitTransferOrSurrender);
				feeRuleModel = getFeeByRule(feeRuleModel);
				feeRuleModel.setServiceCode(ServiceType.OWNERSHIP_TRANSFER_SALE.getCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(":::Permit Transfer/Surrender Fee calculation:::::: = " + e.getMessage());
		}

		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			feeDetailEntity.setTotalFee(NumberParser.roundOff(feeRuleModel.getTotalPrFee(), "########.##"));
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			feeDetailEntity.setTotalFee(NumberParser.roundOff(feeRuleModel.getTotalPrFee(), "########.##"));
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		feeModel.setPermitType(permitTransferOrSurrender);
		return feeModel;
	}

	public FeeModel getFeePermitNew(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel,
			ApplicationEntity appEntity, boolean isDiapatch) {

		FeeModel feeModel = null;
		try {
			ApplicationFormDataEntity appFormEntity = applicationFormDataDAO
					.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.PCF_FORM.getLabel());
			if (appFormEntity != null) {
				PermitNewRequestModel permitNewRequestModel = new ObjectMapper().readValue(appFormEntity.getFormData(),
						PermitNewRequestModel.class);
				feeRuleModel.setPermitType(permitNewRequestModel.getPermitType());
				if (permitNewRequestModel.getPermitClass().equalsIgnoreCase("TEMPORARY"))
					feeRuleModel.setPermitClass("TEMPORARY");
				else
					feeRuleModel.setPermitClass("PUKKA");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		feeRuleModel = getFeeByRule(feeRuleModel);
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			feeDetailEntity.setPermitServiceCharge(feeRuleModel.getPermitService());
			feeDetailEntity.setOtherPermitFee(feeRuleModel.getOtherPermitFee());
			feeDetailEntity
					.setTotalFee(
							NumberParser.roundOff(
									feeRuleModel.getPermitFee() + feeRuleModel.getPermitService()
											+ feeRuleModel.getOtherPermitFee() + feeRuleModel.getPenalty(),
									"########.##"));
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			if (isDiapatch)
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			else
				feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setTotalFee(feeRuleModel.getTotalPrFee());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setPermitFee(feeRuleModel.getPermitFee());
			feeDetailEntity.setPermitServiceCharge(feeRuleModel.getPermitService());
			feeDetailEntity.setOtherPermitFee(feeRuleModel.getOtherPermitFee());
			feeDetailEntity
					.setTotalFee(
							NumberParser.roundOff(
									feeRuleModel.getPermitFee() + feeRuleModel.getPermitService()
											+ feeRuleModel.getOtherPermitFee() + feeRuleModel.getPenalty(),
									"########.##"));
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		return feeModel;
	}

	public FeeModel getFeeDiffTax(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel,
			ApplicationEntity appEntity, boolean isDiapatch) {

		FeeModel feeModel = null;
		feeRuleModel = getFeeByRule(feeRuleModel);
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeRuleModel = calculateDiffFee(appEntity, appTaxModel, feeRuleModel);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setFitnessFee(feeRuleModel.getFitnessFee());
			feeDetailEntity.setFitnessServiceCharge(feeRuleModel.getFitnessService());
			feeDetailEntity.setTotalFee(feeRuleModel.getPrFee() + feeRuleModel.getPrService()
					+ feeRuleModel.getFitnessFee() + feeRuleModel.getFitnessService());
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeRuleModel = calculateDiffFee(appEntity, appTaxModel, feeRuleModel);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setFitnessFee(feeRuleModel.getFitnessFee());
			feeDetailEntity.setFitnessServiceCharge(feeRuleModel.getFitnessService());
			feeDetailEntity.setTotalFee(feeRuleModel.getPrFee() + feeRuleModel.getPrService()
					+ feeRuleModel.getFitnessFee() + feeRuleModel.getFitnessService());
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		return feeModel;
	}

	public FeeModel getFeeVehicleAlteration(FeeRuleModel feeRuleModel, ApplicationTaxModel appTaxModel,
			ApplicationEntity appEntity, boolean isDiapatch) {

		FeeModel feeModel = null;
		boolean isPostalFeeApply = true;
		String instanceId = applicationService.getProcessInstanceId(appEntity.getLoginHistory().getSessionId());
		ActivitiResponseModel<List<RtaTaskInfo>> actRes = activitiService.getActiveTasks(instanceId);
		if (!(ObjectsUtil.isNull(actRes) || ObjectsUtil.isNull(actRes.getData()))) {
			for (RtaTaskInfo task : actRes.getActiveTasks())
				if (task.getTaskDefKey().equalsIgnoreCase("dt_tax"))
					return null;
		}

		ApplicationFormDataEntity appFormEntity = applicationFormDataDAO
				.getApplicationFormData(appEntity.getApplicationId(), FormCodeType.VA_FORM.getLabel());
		try {
			if (appFormEntity != null) {
				ObjectMapper mapper = new ObjectMapper();
				VehicleBodyModel vehicleBodyModel = mapper.readValue(appFormEntity.getFormData(),
						VehicleBodyModel.class);
				RegistrationServiceResponseModel<TaxTypeModel> taxTypeResponse = null;
				TaxTypeModel taxTypeModel = null;
				boolean isLifeTax = false;
				for (AlterationCategory alterationCategory : vehicleBodyModel.getAlterationCategory()) {
					switch (AlterationCategory.getAlterationCategory(alterationCategory.getLabel())) {
					case VEHICLE_TYPE:
						feeRuleModel.setAltrationFlag(true);
						taxTypeResponse = registrationService.getTaxTypeByCov(vehicleBodyModel.getVehicleSubClass());
						if (taxTypeResponse.getHttpStatus().equals(HttpStatus.OK)) {
							taxTypeModel = taxTypeResponse.getResponseBody();
							log.info("::::tax type in case of vehicle alteration: " + taxTypeModel.getTaxTypeCode());
							if (taxTypeModel.getTaxTypeCode().equalsIgnoreCase(TaxType.LIFE_TAX.getCode())
									&& appTaxModel.getTaxType() == TaxType.QUARTERLY_TAX.getValue())
								isLifeTax = true;
						}

						int regCatId = 0;
						if (vehicleBodyModel.getRegistrationCategoryCode().equalsIgnoreCase("T"))
							regCatId = 1;
						if (vehicleBodyModel.getRegistrationCategoryCode().equalsIgnoreCase("NT"))
							regCatId = 2;
						log.info("::Vehicle Type Alteration::reg type:::: " + regCatId);
						if (regCatId == RegistrationCategoryType.TRANSPORT.getValue()
								&& appTaxModel.getRegType() == RegistrationCategoryType.TRANSPORT.getValue()) {
							if (fitnessValidate(appTaxModel, regCatId)) {
								feeRuleModel.setFitnessExpireFlag(true);
							}
							feeRuleModel.setHsrpFlag(true);
						}
						if (regCatId == RegistrationCategoryType.TRANSPORT.getValue()
								&& appTaxModel.getRegType() == RegistrationCategoryType.NON_TRANSPORT.getValue()) {
							if (fitnessValidate(appTaxModel, regCatId)) {
								feeRuleModel.setFitnessExpireFlag(true);
							}
							feeRuleModel.setReassinmentFlag(true);
						}
						if (regCatId == RegistrationCategoryType.NON_TRANSPORT.getValue()
								&& appTaxModel.getRegType() == RegistrationCategoryType.NON_TRANSPORT.getValue()) {
							feeRuleModel.setHsrpFlag(true);
							if (isLifeTax)
								feeRuleModel.setCardFeeFlag(true);
						}
						if (regCatId == RegistrationCategoryType.NON_TRANSPORT.getValue()
								&& appTaxModel.getRegType() == RegistrationCategoryType.TRANSPORT.getValue()) {
							feeRuleModel.setReassinmentFlag(true);
							if (isLifeTax)
								feeRuleModel.setCardFeeFlag(true);
						}

						feeRuleModel.setRegCategory(regCatId);
						feeRuleModel.setVehicleClassCategory(vehicleBodyModel.getVehicleSubClass());
						log.info(":::vehicle Alteration vehicle sub class::::: " + vehicleBodyModel.getVehicleSubClass()
								+ " - " + feeRuleModel.getOldClassOfVehicle());
						break;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		feeRuleModel = getFeeByRule(feeRuleModel);
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setHSRPFee(feeRuleModel.getHsrpAmount());
			feeDetailEntity.setFitnessFee(feeRuleModel.getFitnessFee());
			feeDetailEntity.setFitnessServiceCharge(feeRuleModel.getFitnessService());
			feeDetailEntity.setTotalFee(feeRuleModel.getPrFee() + feeRuleModel.getPrService()
					+ feeRuleModel.getPostalFee() + feeRuleModel.getCardFee() + feeRuleModel.getHsrpAmount()
					+ feeRuleModel.getFitnessFee() + feeRuleModel.getFitnessService());
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(feeRuleModel.getPrFee());
			feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getPrService());
			feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
			feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
			feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
			feeDetailEntity.setLateFee(feeRuleModel.getLateFee());
			feeDetailEntity.setHSRPFee(feeRuleModel.getHsrpAmount());
			feeDetailEntity.setFitnessFee(feeRuleModel.getFitnessFee());
			feeDetailEntity.setFitnessServiceCharge(feeRuleModel.getFitnessService());
			feeDetailEntity.setTotalFee(feeRuleModel.getPrFee() + feeRuleModel.getPrService()
					+ feeRuleModel.getPostalFee() + feeRuleModel.getCardFee() + feeRuleModel.getHsrpAmount()
					+ feeRuleModel.getFitnessFee() + feeRuleModel.getFitnessService());
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel(feeDetailEntity);
		return feeModel;
	}

	public FeeRuleModel getPenaltyData(ApplicationEntity appEntity, FeeRuleModel feeRuleModel,
			ApplicationTaxModel appTaxModel) {
		long inputDate = 0l;
		int daysCount = 0;
		switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
		case REGISTRATION_RENEWAL:
			inputDate = appTaxModel.getPrExpiryTime();
			if (DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(), inputDate)) {
				if (!DateUtil.isSameOrGreaterDate(inputDate, penaltyDate)) {
					feeRuleModel.setOldVehicle(true);
					int exp = DateUtil.expiryMonthsCount(penaltyDate, inputDate);
					feeRuleModel.setExpiryMonthCount(exp);
					feeRuleModel.setMonthCount(DateUtil.monthsCount(inputDate) - exp);
				} else {
					feeRuleModel.setMonthCount(DateUtil.monthsCount(inputDate));
				}
			}
			break;
		case FC_RENEWAL:
		case FC_OTHER_STATION:
			inputDate = appTaxModel.getFitnessDetailsModel().getExpiryDate();
			if (DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(), inputDate)) {
				if (!DateUtil.isSameOrGreaterDate(inputDate, penaltyDate)) {
					feeRuleModel.setOldVehicle(true);
				}
				daysCount = DateUtil.monthsCount(inputDate);
				feeRuleModel.setMonthCount(daysCount);
				feeRuleModel.setDaysCount(DateUtil.daysCount(inputDate));
			}
			break;
		case PERMIT_RENEWAL:
			inputDate = appTaxModel.getPermitHeaderModel().getValidToDate();
			if (DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(), inputDate)) {
				daysCount = DateUtil.monthsCount(inputDate);
				feeRuleModel.setMonthCount(daysCount);
				feeRuleModel.setDaysCount(DateUtil.daysCount(inputDate));
			}
			break;
		case PERMIT_RENEWAL_AUTH_CARD:
			inputDate = appTaxModel.getPermitAuthValidTo();
			if (DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(), inputDate)) {
				daysCount = DateUtil.monthsCount(inputDate);
				feeRuleModel.setMonthCount(daysCount);
				feeRuleModel.setDaysCount(DateUtil.daysCount(inputDate));
			}
			break;
		case ADDRESS_CHANGE:
			inputDate = appTaxModel.getPrExpiryTime();
			if (DateUtil.isSameOrGreaterDate(DateUtil.toCurrentUTCTimeStamp(), inputDate)) {
				if (!DateUtil.isSameOrGreaterDate(inputDate, penaltyDate)) {
					feeRuleModel.setOldVehicle(true);
				}
				daysCount = DateUtil.monthsCount(inputDate);
				feeRuleModel.setMonthCount(daysCount);
				feeRuleModel.setDaysCount(DateUtil.daysCount(inputDate));
			}
			break;
		case DIFFERENTIAL_TAX:
			Long trExpiryDate = DateUtil.addDays(appTaxModel.getTrIssueTime(), SomeConstants.TWENTY_NINE);
			feeRuleModel
					.setExpiryMonthCount(DateUtil.getExpiryMonthsCount(trExpiryDate, DateUtil.toCurrentUTCTimeStamp()));
			break;
		}
		return feeRuleModel;
	}

	public FeeRuleModel getFeeByRule(FeeRuleModel feeRuleModel) {
		log.info(":::::::getFeeByRule:::::start::::::");
		ResponseEntity<String> responseEntity = callAPIPost4Fee(droolsFeeURL, feeRuleModel, contenttype);
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			ObjectMapper mapper = new ObjectMapper();
			FeeRuleModel feeRuleModelResponse = new FeeRuleModel();
			try {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				feeRuleModelResponse = mapper.readValue(responseEntity.getBody(), FeeRuleModel.class);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(" getFeeByRule Object json parser Internal Server Error:");
			}
			log.info(":::::::getFeeByRule:::::end:::::: " + responseEntity.getBody());
			return feeRuleModelResponse;
		} else {
			log.info(":::::::getFeeByRule:::::Internal Server Error:::::: " + responseEntity.getStatusCode());
			throw new IllegalArgumentException(" getFeeByRule Internal Server Error:");
		}
	}

	public TaxRuleModel getTaxByRule(TaxRuleModel taxRuleModel) {
		log.info(":::::::getFeeByRule:::::start::::::");
		ResponseEntity<String> responseEntity = callAPIPost4Tax(droolsTaxURL, taxRuleModel, contenttype);
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			ObjectMapper mapper = new ObjectMapper();
			TaxRuleModel taxRuleModelResponse = new TaxRuleModel();
			try {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				taxRuleModelResponse = mapper.readValue(responseEntity.getBody(), TaxRuleModel.class);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(" getTaxByRule Object json parser Internal Server Error:");
			}
			log.info(":::::::getTaxByRule:::::end:::::: " + responseEntity.getBody());
			return taxRuleModelResponse;
		} else {
			log.info(":::::::getTaxByRule:::::Internal Server Error:::::: " + responseEntity.getStatusCode());
			throw new IllegalArgumentException(" getTaxByRule Internal Server Error:");
		}
	}

	public ResponseEntity<String> callAPIPost4Fee(String apiPath, FeeRuleModel feeRuleModel, String contentType) {
		log.info("::::::::::callAPIPost4Fee:::::start::::::");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", contentType);
		HttpEntity<FeeRuleModel> entity = new HttpEntity<FeeRuleModel>(feeRuleModel, headers);
		log.info("::::::::callAPIPost:::::end::::::");
		try {
			return restTemplate.exchange(apiPath, HttpMethod.POST, entity, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(" getFeeByRule Internal Server Error: Calling Drolls API's");
		}

	}

	public ResponseEntity<String> callAPIPost4Tax(String apiPath, TaxRuleModel taxRuleModel, String contentType) {
		log.info("::::::::::callAPIPost4Tax:::::start::::::");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", contentType);
		HttpEntity<TaxRuleModel> entity = new HttpEntity<TaxRuleModel>(taxRuleModel, headers);
		log.info(":::::::::callAPIPostTax:::::end::::::");
		try {
			return restTemplate.exchange(apiPath, HttpMethod.POST, entity, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(" getTaxByRule Internal Server Error: Calling Drolls API's");
		}

	}

	public TaxFeeModel isSBIVerification(TaxFeeModel taxFeeModel, ApplicationEntity appeEntity) {
		log.info("::::Citizen:::::::isSBIVerification::::::start::::::");
		TransactionDetailEntity transactionDetailEntity = transactionDetailDAO.getByAppNdServiceType(appeEntity);
		if (transactionDetailEntity != null) {
			
			if (transactionDetailEntity.getPgType()!=null && transactionDetailEntity.getPgType().equalsIgnoreCase(PaymentGatewayType.PAYU.getLabel())) {
				taxFeeModel.setIsPayUVerification(1);
			} else {
				taxFeeModel.setIsSBIVerification(1);
			}
		} else {
			taxFeeModel.setIsSBIVerification(0);
		}
		log.info(":::::::::::isSBIVerification::::::end:::::: " + taxFeeModel.getIsPayUVerification());
		return taxFeeModel;
	}

	private int calculateQuarterlyTax() {
		int quartltyPart = 0;
		switch (MonthType.getMonthType(DateUtil.getMonth(DateUtil.toCurrentUTCTimeStamp()))) {
		case JANUARY:
			quartltyPart = 3;
			break;
		case FEBRUARY:
			quartltyPart = 2;
			break;
		case MARCH:
			quartltyPart = 1;
			break;
		case APRIL:
			quartltyPart = 3;
			break;
		case MAY:
			quartltyPart = 2;
			break;
		case JUNE:
			quartltyPart = 1;
			break;
		case JULY:
			quartltyPart = 3;
			break;
		case AUGUST:
			quartltyPart = 2;
			break;
		case SEPTEMBER:
			quartltyPart = 1;
			break;
		case OCTOBER:
			quartltyPart = 3;
			break;
		case NOVEMBER:
			quartltyPart = 2;
			break;
		case DECEMBER:
			quartltyPart = 1;
			break;
		}
		log.debug("::Quartely Tax Part:::::: " + quartltyPart);
		return quartltyPart;
	}

	@Override
	@Transactional
	public TaxFeeModel licenseTaxFeeCal(long sessionId) {
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		log.debug("::CITIZEN::::licenseTaxFeeCal:::::ServiceCode " + appEntity.getServiceCode());
		String custName = "";
		try {
			if (!appEntity.getServiceCode().equals(ServiceType.DL_FOREIGN_CITIZEN.getCode())) {
				AadharModel aadharModel = registrationService
						.getAadharDetails(Long.valueOf(appEntity.getLoginHistory().getAadharNumber()))
						.getResponseBody();
				custName = aadharModel.getName();
			} else {
				custName = "NA";
			}

		} catch (NumberFormatException | UnauthorizedException e1) {
			e1.printStackTrace();
			throw new IllegalArgumentException("Internal Server Error userTaxFeeCal !");
		}
		TaxFeeModel taxFeeModel = new TaxFeeModel();
		FeeRuleModel feeRuleModel = new FeeRuleModel();
		List<String> vehicleClass = new ArrayList<String>();
		int count = 0;
		if (ServiceType.LL_DUPLICATE.getCode().equalsIgnoreCase(appEntity.getServiceCode())
				|| ServiceType.DL_DUPLICATE.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
			try {
				RegLicenseServiceResponseModel<LicenseHolderPermitDetails> response = registrationLicenseService
						.getLicenseHolderDtls(appEntity.getLoginHistory().getAadharNumber(), null, null);
				if (ServiceType.LL_DUPLICATE.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
					for (LearnersPermitDtlModel model : response.getResponseBody().getLearnersPermitDetailsList()) {
						vehicleClass.add(model.getLlrVehicleClassCode());
						++count;
					}
				} else {
					for (DriversLicenceDetailsModel model : response.getResponseBody().getDriversPermitDetailsList()) {
						if (!model.getValidTo().before(new Date())) {
							vehicleClass.add(model.getDlVehicleClassCode());
							++count;
						}
					}
				}
			} catch (UnauthorizedException ex) {

			}
		} else if (ServiceType.DL_RETEST.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
			boolean flag = false;
			ApplicationEntity applicationEntity = null;
			List<UserSessionEntity> dlFreshRejectedSessions = userSessionDAO.getRejectedAppSessions(
					appEntity.getLoginHistory().getAadharNumber(), ServiceType.DL_FRESH.getCode());
			for (UserSessionEntity rejected : dlFreshRejectedSessions) {
				if (!ObjectsUtil.isNullOrEmpty(dlFreshRejectedSessions)
						&& rejected.getUniqueKey().equals(appEntity.getLoginHistory().getUniqueKey())) {
					applicationEntity = applicationDAO.getApplicationFromSession(rejected.getSessionId());
					flag = true;
					break;
				}
			}
			if (!flag) {
				List<UserSessionEntity> dlEndorseRejectedSessions = userSessionDAO.getRejectedAppSessions(
						appEntity.getLoginHistory().getAadharNumber(), ServiceType.DL_ENDORSMENT.getCode());
				List<UserSessionEntity> dlEndorseAppSession = userSessionDAO
						.getAppliedSessions(appEntity.getCreatedBy(), ServiceType.LL_ENDORSEMENT.getCode());
				for (UserSessionEntity rejected : dlEndorseRejectedSessions) {
					if (!ObjectsUtil.isNullOrEmpty(dlEndorseRejectedSessions)) {
						if (rejected.getUniqueKey().equals(appEntity.getLoginHistory().getUniqueKey())) {
							applicationEntity = applicationDAO.getApplicationFromSession(rejected.getSessionId());
							break;
						} else if (!ObjectsUtil.isNullOrEmpty(dlEndorseAppSession)) {
							for (UserSessionEntity appSesions : dlEndorseAppSession) {
								if (appEntity.getLoginHistory().getUniqueKey().equals(appSesions.getUniqueKey())) {
									applicationEntity = applicationDAO
											.getApplicationFromSession(appSesions.getSessionId());
									break;
								}
							}
						}
					}
				}
			}
			List<LicensePermitDetailsEntity> entities = licencePermitDetailsDAO
					.getLicensePermitDetails(applicationEntity.getApplicationId());
			for (LicensePermitDetailsEntity licensePermitDetailsEntity : entities) {
				vehicleClass.add(licensePermitDetailsEntity.getVehicleClassCode());
				++count;
			}
		} else {
			List<LicensePermitDetailsEntity> entities = licencePermitDetailsDAO
					.getLicensePermitDetails(appEntity.getApplicationId());
			for (LicensePermitDetailsEntity licensePermitDetailsEntity : entities) {
				vehicleClass.add(licensePermitDetailsEntity.getVehicleClassCode());
				++count;
			}
			log.debug("::Licence::::: " + entities.size());
			if (ServiceType.DL_FRESH.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
				for (LicensePermitDetailsEntity licensePermitDetailsEntity : entities) {
					if (licensePermitDetailsEntity.isBadge()) {
						feeRuleModel.setBadge(true);
						break;
					}
				}
			} else if (ServiceType.DL_ENDORSMENT.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
				if (vehicleClass.size() == SomeConstants.ONE
						&& vehicleClass.contains(LicenceVehicleClass.HAZARDDOUS_GOODS_CARRIAGE.getCode())) {
					feeRuleModel.setEndorseType("HZARDOUS");
				} else {
					feeRuleModel.setEndorseType("NORMAL");
				}
			}
		}
		// Checking for penalty fee in case of DL Renew and DL expired
		int DLExpiredYear = 0;
		if (ServiceType.DL_RENEWAL.getCode().equalsIgnoreCase(appEntity.getServiceCode())
				|| ServiceType.DL_EXPIRED.getCode().equalsIgnoreCase(appEntity.getServiceCode())) {
			try {
				RegLicenseServiceResponseModel<LicenseHolderPermitDetails> response = registrationLicenseService
						.getLicenseHolderDtls(appEntity.getLoginHistory().getAadharNumber(), null, null);
				for (DriversLicenceDetailsModel model : response.getResponseBody().getDriversPermitDetailsList()) {
					if (model.getValidTo().before(new Date())) {
						int diffrenceYear = DateUtil.getDiffYears(model.getValidTo(), new Date());
						if (DLExpiredYear > 0 && DLExpiredYear > diffrenceYear) {
							DLExpiredYear = diffrenceYear;
						} else if (DLExpiredYear == 0) {
							DLExpiredYear = diffrenceYear;
						}
					}
				}
			} catch (UnauthorizedException ex) {

			}
		}

		feeRuleModel.setRegCategory(RegistrationCategoryType.TRANSPORT.getValue());
		if (vehicleClass != null && vehicleClass.size() > 0) {
			List<LlrVehicleClassMasterEntity> llrVehicleClassMasterEntitys = licenceDAO.getCovListIn(vehicleClass);
			for (LlrVehicleClassMasterEntity llrVehicleClassMasterEntity : llrVehicleClassMasterEntitys) {
				if (llrVehicleClassMasterEntity.getVehicleTransportType().equalsIgnoreCase("N")) {
					feeRuleModel.setRegCategory(RegistrationCategoryType.NON_TRANSPORT.getValue());
					break;
				}

			}
		}
		feeRuleModel.setPenaltyYear(DLExpiredYear);
		feeRuleModel.setLicenseCOV(count);
		feeRuleModel.setGracePeriod(true);
		feeRuleModel.setLicenseCategory(appEntity.getServiceCode());
		feeRuleModel = getLicenseFeeByRule(feeRuleModel);
		FeeModel feeModel = updateLicenseFeeDetails(appEntity, feeRuleModel);
		taxFeeModel.setFeeModel(feeModel);
		taxFeeModel.setCustomerName(custName);
		taxFeeModel = isSBIVerification(taxFeeModel, appEntity);
		taxFeeModel.setGrandTotal(feeModel.getTotalFee());
		log.debug("::CITIZEN:::::licenseTaxFeeCal ::::end::: " + taxFeeModel);
		return taxFeeModel;
	}

	public FeeModel updateLicenseFeeDetails(ApplicationEntity appEntity, FeeRuleModel feeRuleModel) {
		log.info("::updateLicenseFeeDetails::start::");
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		if (feeDetailEntity != null) {
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
		} else {
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailEntity.setApplicationFee(feeRuleModel.getLicenseAppFee());
		feeDetailEntity.setApplicationServiceCharge(feeRuleModel.getLicenseServiceCharge());
		feeDetailEntity.setPostalCharge(feeRuleModel.getPostalFee());
		feeDetailEntity.setSmartCardFee(feeRuleModel.getCardFee());
		feeDetailEntity.setLicenseTestFee(feeRuleModel.getLicenseTestFee());
		feeDetailEntity.setPenaltyFee(feeRuleModel.getPenalty());
		feeDetailEntity.setTotalFee(feeRuleModel.getTotalFee());
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		FeeModel feeModel = feeDetailConverter.convertToModel4License(feeDetailEntity);
		log.debug("::updateLicenseFeeDetails::end::");
		return feeModel;

	}

	public FeeRuleModel getLicenseFeeByRule(FeeRuleModel feeRuleModel) {
		log.debug(":::::::getLicenseFeeByRule:::::start::::::");
		ResponseEntity<String> responseEntity = callAPIPost4Fee(droolsLicenseFeeURL, feeRuleModel, contenttype);
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			ObjectMapper mapper = new ObjectMapper();
			FeeRuleModel feeRuleModelResponse = new FeeRuleModel();
			try {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				feeRuleModelResponse = mapper.readValue(responseEntity.getBody(), FeeRuleModel.class);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(" getLicenseFeeByRule Object json parser Internal Server Error:");
			}
			log.debug(":::::::getLicenseFeeByRule:::::end:::::: " + responseEntity.getBody());
			return feeRuleModelResponse;
		} else {
			log.debug(":::::::getLicenseFeeByRule:::::Internal Server Error:::::: " + responseEntity.getStatusCode());
			throw new IllegalArgumentException(" getLicenseFeeByRule Internal Server Error:");
		}
	}

	@Override
	@Transactional
	public TaxFeeModel userTaxFeeCal(long sessionId) {
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);
		log.info("::CITIZEN::::userTaxFeeCal:::::ServiceCode " + appEntity.getServiceCode());

		String custName = "";
		AadharModel aadharModel = new AadharModel();
		try {
			aadharModel = registrationService
					.getAadharDetails(Long.valueOf(appEntity.getLoginHistory().getAadharNumber())).getResponseBody();
			custName = aadharModel.getName();
		} catch (NumberFormatException | UnauthorizedException e1) {
			e1.printStackTrace();
			throw new IllegalArgumentException("Internal Server Error userTaxFeeCal !");
		}
		TaxFeeModel taxFeeModel = new TaxFeeModel();
		FeeRuleModel feeRuleModel = new FeeRuleModel();
		FeeModel feeModel = updateUserFeeDetails(appEntity, feeRuleModel);
		taxFeeModel.setFeeModel(feeModel);
		taxFeeModel.setCustomerName(custName);
		taxFeeModel = isSBIVerification(taxFeeModel, appEntity);
		taxFeeModel.setGrandTotal(feeModel.getTotalFee());
		log.info("::CITIZEN::::::::::userTaxFeeCal ::::end::::::::: " + taxFeeModel);
		return taxFeeModel;
	}

	public FeeModel updateUserFeeDetails(ApplicationEntity appEntity, FeeRuleModel feeRuleModel) {
		log.info("::updateUserFeeDetails::start::");
		FeeModel feeModel = new FeeModel();
		FeeDetailEntity feeDetailEntity = null;
		double appFee = 0d;
		feeDetailEntity = feeDetailDAO.getByAppId(appEntity);
		appFee = getUserFee(appEntity);
		if (feeDetailEntity != null) {
			feeModel = new FeeModel();
			feeDetailEntity.setModifiedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
			feeDetailEntity.setApplicationFee(appFee);
			feeDetailEntity.setTotalFee(appFee);
		} else {
			feeModel = new FeeModel();
			feeDetailEntity = new FeeDetailEntity();
			feeDetailEntity.setApplicationId(appEntity);
			feeDetailEntity.setApplicationFee(appFee);
			feeDetailEntity.setTotalFee(appFee);
			feeDetailEntity.setCreatedBy(appEntity.getLoginHistory().getAadharNumber());
			feeDetailEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		}
		feeDetailDAO.saveOrUpdate(feeDetailEntity);
		feeModel = feeDetailConverter.convertToModel4License(feeDetailEntity);
		log.info("::updateUserFeeDetails::end::");
		return feeModel;

	}

	public double getUserFee(ApplicationEntity appEntity) {
		double appFee = 0.0d;
		switch (ServiceType.getServiceType(appEntity.getServiceCode())) {
		case ALTERATION_AGENCY_SIGNUP:
			break;
		case BODYBUILDER_SIGNUP:
			break;
		case DEALER_SIGNUP:
			appFee = dealerSighnupFee;
			break;
		case DRIVING_INSTITUTE:
			break;
		case FINANCIER_SIGNUP:
			break;
		case PUC_USER_SIGNUP:
			break;
		}
		return dealerSighnupFee;
	}

	public FeeRuleModel getUserFeeByRule(FeeRuleModel feeRuleModel) {
		log.info(":::::::getUserFeeByRule:::::start::::::");
		ResponseEntity<String> responseEntity = callAPIPost4Fee(droolsUserFeeURL, feeRuleModel, contenttype);
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			ObjectMapper mapper = new ObjectMapper();
			FeeRuleModel feeRuleModelResponse = new FeeRuleModel();
			try {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				feeRuleModelResponse = mapper.readValue(responseEntity.getBody(), FeeRuleModel.class);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(" getUserFeeByRule Object json parser Internal Server Error:");
			}
			log.info(":::::::getUserFeeByRule:::::end:::::: " + responseEntity.getBody());
			return feeRuleModelResponse;
		} else {
			log.info(":::::::getUserFeeByRule:::::Internal Server Error:::::: " + responseEntity.getStatusCode());
			throw new IllegalArgumentException(" getUserFeeByRule Internal Server Error:");
		}
	}

	@Override
	@Transactional
	public void testMailNdSMS(long sessionId, String status) {
		log.info(":::testMailNdSMS:::::start::::");
		ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(sessionId);

		Status statusEnum = Status.valueOf(status);
		rtaApplicationService.sendSMSEmail(statusEnum, appEntity);
		log.info(":::testMailNdSMS:::::end::::");

	}

	public long taxValidty(int quartelyPart) {
		int monthType = calculateQuarterlyTax();
		long taxValid = 0;
		if (quartelyPart == 0 || quartelyPart == 3)
			taxValid = DateUtil.toLastDayOfMonth(monthType - 1);
		if (quartelyPart == 6)
			taxValid = DateUtil.toLastDayOfMonth((monthType - 1) + 3);
		if (quartelyPart == 12)
			taxValid = DateUtil.toLastDayOfMonth((monthType - 1) + 9);
		return taxValid;
	}

	@Override
	@Transactional
	public DifferentialTaxFeeModel saveOrUpdateDifferentialTaxFee(String trNumber, Long applicationId) {
		UserSessionEntity userSessionEntity = null;
		ApplicationEntity applicationEntity = null;
		DifferentialTaxFeeModel differentialTaxFeeModel = null;
		try {
			if (!StringsUtil.isNullOrEmpty(trNumber)) {
				userSessionEntity = userSessionDAO.getLatestUserSession(null, trNumber, KeyType.TR,
						ServiceType.DIFFERENTIAL_TAX, Status.APPROVED);
				applicationEntity = applicationDAO.getApplicationFromSession(userSessionEntity.getSessionId());
			} else {
				applicationEntity = applicationDAO.getEntity(ApplicationEntity.class, applicationId);
			}
			TaxDetailEntity taxDetailEntity = taxDetailDAO.getByAppId(applicationEntity);
			FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(applicationEntity);
			differentialTaxFeeModel = new DifferentialTaxFeeModel();
			if (!ObjectsUtil.isNull(taxDetailEntity)) {
				differentialTaxFeeModel.setTaxAmount(taxDetailEntity.getTotalAmt());
				differentialTaxFeeModel.setTaxType(taxDetailEntity.getTaxType());
			}
			if (!ObjectsUtil.isNull(feeDetailEntity)) {
				differentialTaxFeeModel.setLateFee(feeDetailEntity.getLateFee());
				differentialTaxFeeModel.setPrFee(feeDetailEntity.getApplicationFee());
				differentialTaxFeeModel.setServiceFee(feeDetailEntity.getApplicationServiceCharge());
				differentialTaxFeeModel.setFitnessFee(feeDetailEntity.getFitnessFee());
				differentialTaxFeeModel.setFitnessServiceFee(feeDetailEntity.getFitnessServiceCharge());
				differentialTaxFeeModel.setPostalFee(feeDetailEntity.getPostalCharge());
				differentialTaxFeeModel.setSmartCardFee(feeDetailEntity.getSmartCardFee());
				differentialTaxFeeModel.setTotalFee(feeDetailEntity.getTotalFee());
			}
			double sumOfAll = differentialTaxFeeModel.getTaxAmount() + differentialTaxFeeModel.getTotalFee();
			differentialTaxFeeModel.setTotalTaxFeeAmount(sumOfAll);
		} catch (Exception e) {
			log.error("getting error ::::::::::::::::::::::::: saveOrUpdateDifferentialTaxFee");
		}
		return differentialTaxFeeModel;
	}

	@Override
	public TaxRuleModel getTaxCal(String appNo) {
		ApplicationEntity appEntity = null;
		appEntity = applicationDAO.getApplication(appNo);
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
		TaxFeeModel taxFeeModel = new TaxFeeModel();
		String custName = appTaxModel.getCitizenName();

		TaxRuleModel taxRuleModel = new TaxRuleModel();
		TaxModel taxModel = new TaxModel();
		taxRuleModel.setStateCode("AP");
		taxRuleModel.setFuelType(appTaxModel.getFuelType());
		taxRuleModel.setRegCategory(appTaxModel.getRegType());
		taxRuleModel.setServiceCode(appEntity.getServiceCode());
		taxRuleModel.setGvw(appTaxModel.getGvw());
		taxRuleModel.setVehicleClassCategory(appTaxModel.getVehicleSubClass());
		taxRuleModel.setVehicleClass(appTaxModel.getVehicleClass());
		taxRuleModel.setTaxType(TaxType.getTaxType(appTaxModel.getTaxType()).getCode());
		taxRuleModel.setSeatingCapacity(appTaxModel.getSeatingCapacity());
		taxRuleModel.setMonthType(calculateQuarterlyTax());
		taxRuleModel.setInvoiceAmount((int) appTaxModel.getInvoiceAmt());
		taxRuleModel.setOwnerType(appTaxModel.getOwnerType());
		taxRuleModel.setUlw(appTaxModel.getUlw());
		taxRuleModel.setPrIssueDate(appTaxModel.getPrIssueTime());
		taxRuleModel.setTaxValidUpto(appTaxModel.getTaxValidUpto());
		if (appTaxModel.getPermitHeaderModel() != null) {
			taxRuleModel.setPermitType(appTaxModel.getPermitHeaderModel().getPermitType());
			taxRuleModel.setPermitSubType(appTaxModel.getPermitHeaderModel().getPermitSubType());
			if (DateUtil.isSameOrGreaterDate(appTaxModel.getPermitHeaderModel().getValidToDate(),
					DateUtil.dateFormater(DateUtil.getDateInString(new Date()))))
				taxRuleModel.setIsPermitValid(true);
		}
		taxRuleModel.setOldTaxAmt(appTaxModel.getTaxAmt());
		taxRuleModel = getTaxByRule(taxRuleModel);
		return taxRuleModel;
	}

	public boolean fitnessValidate(ApplicationTaxModel appTaxModel, int regType) {
		FcDetailsModel fdm = null;
		fdm = appTaxModel.getFitnessDetailsModel();
		if (ObjectsUtil.isNull(fdm)) {
			return true;
		}
		switch (RegistrationCategoryType.getRegistrationCategoryType(regType)) {
		case NON_TRANSPORT:
			return false;
		case TRANSPORT:
			return DateUtil.toCurrentUTCTimeStamp() > fdm.getExpiryDate();
		}
		return false;
	}

}
package org.rta.citizen.aadharseeding.rc.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.aadharseeding.rc.model.RCAadharSeedModel;
import org.rta.citizen.aadharseeding.rc.service.RCASService;
import org.rta.citizen.common.constant.SomeConstants;
import org.rta.citizen.common.dao.ApplicationApprovalHistoryDAO;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.dao.ApplicationFormDataDAO;
import org.rta.citizen.common.entity.ApplicationApprovalHistoryEntity;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.ApplicationFormDataEntity;
import org.rta.citizen.common.enums.FormCodeType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.ApplicationModel;
import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserActionModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.xdrop.fuzzywuzzy.FuzzySearch;

@Service
public class RCASServiceImpl implements RCASService {

	private static final Logger logger = Logger.getLogger(RCASServiceImpl.class);
	
	@Autowired
	private ApplicationDAO applicationDAO;
	
	@Autowired  
	private RegistrationService registrationService;
	
	@Autowired
	private ApplicationFormDataDAO applicationFormDataDAO;
	
	@Autowired
	private ApplicationApprovalHistoryDAO applicationApprovalHistoryDAO;
	
	@Override
	@Transactional
	public Map<String, Object> getMatchDataBwAadhaarAndRC(String applicationNo) {
		
		ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNo);
		RegistrationServiceResponseModel<ApplicationModel> prDetails = null;
		RegistrationServiceResponseModel<AadharModel> aadharDetails = null;
        try {
        	prDetails = registrationService.getPRDetails(applicationEntity.getLoginHistory().getUniqueKey());
        	aadharDetails = registrationService.getAadharDetails(Long.valueOf(applicationEntity.getLoginHistory().getAadharNumber()));
        } catch (RestClientException e) {
            logger.error("error when getting pr details : " + e);
        } catch (UnauthorizedException e) {
            logger.error("unauthorized");
        }
        ApplicationModel applicationDetails = prDetails.getResponseBody();
        AadharModel aadharModel = aadharDetails.getResponseBody();
        CustomerDetailsRequestModel customerDetails= applicationDetails.getCustomerDetails();
        Map<String, Object> map = new HashMap<>();
        Integer overAllPercentage = 0;
        
    	Map<String, Object> customerName = new HashMap<>();
    	customerName.put("nameFormAadhaar", aadharModel.getName());
    	customerName.put("nameFormRC", customerDetails.getFirst_name() +" "+customerDetails.getLast_name());
    	int namePercentage = getMatchAadhaarNameAndRcName(aadharModel.getName(), customerDetails.getFirst_name()+" "+customerDetails.getLast_name());
    	overAllPercentage = overAllPercentage + namePercentage;
    	customerName.put("percentageMatching", namePercentage);
    	map.put("customerName", customerName);
    	Map<String, Object> customerCareOf = new HashMap<>();
    	customerCareOf.put("careOfFormAadhaar", aadharModel.getCo());
    	customerCareOf.put("careOfFormRC", customerDetails.getFather_name());
    	int careOfPercentage = getMatchAadhaarAndRcParameters( aadharModel.getCo(), customerDetails.getFather_name());
    	overAllPercentage = overAllPercentage + careOfPercentage;
    	customerCareOf.put("percentageMatching", careOfPercentage);
    	map.put("customerCareOf", customerCareOf);
    	Map<String, Object> customerDOB = new HashMap<>();
    	customerDOB.put("dobFormAadhaar", aadharModel.getDob());
    	customerDOB.put("dobFormRC", customerDetails.getDob());
    	if(!StringsUtil.isNullOrEmpty(aadharModel.getDob())){
    		Integer percentMatching = aadharModel.getDob().equalsIgnoreCase(customerDetails.getDob()) ? 100  : 0;
    		overAllPercentage = overAllPercentage + percentMatching;
        	customerDOB.put("percentageMatching", percentMatching);
    	}else{
        	customerDOB.put("percentageMatching", "0");
    	}
    	map.put("customerDOB", customerDOB);
    	Map<String, Object> customerGender = new HashMap<>();
    	customerGender.put("genderFormAadhaar", aadharModel.getGender());
    	customerGender.put("genderFormRC", customerDetails.getGender());
    	if(!(StringsUtil.isNullOrEmpty(aadharModel.getGender()) || StringsUtil.isNullOrEmpty(customerDetails.getGender()))){
    		Integer percentMatching = aadharModel.getGender().equalsIgnoreCase(customerDetails.getGender()) ? 100  : 0;
    		if(percentMatching == 0){
    			if(("M".equalsIgnoreCase(aadharModel.getGender()) && "Male".equalsIgnoreCase(customerDetails.getGender()))
    					|| ("Male".equalsIgnoreCase(aadharModel.getGender()) && "M".equalsIgnoreCase(customerDetails.getGender()))){
    				percentMatching = 100;
    			}
		    }
		    if(percentMatching == 0){
		    	if(("F".equalsIgnoreCase(aadharModel.getGender()) && "Female".equalsIgnoreCase(customerDetails.getGender()))
		    			|| ("Female".equalsIgnoreCase(aadharModel.getGender()) && "F".equalsIgnoreCase(customerDetails.getGender()))){
    				percentMatching = 100;
    			}
		    }
    		overAllPercentage = overAllPercentage + percentMatching;
    		customerGender.put("percentageMatching", percentMatching);
    	}else{
    		customerGender.put("percentageMatching", 0);
    	}
    	map.put("customerGender", customerGender);
    	Map<String, Object> customerDistrict = new HashMap<>();
    	customerDistrict.put("districtFormAadhaar", aadharModel.getDistrict_name());
    	customerDistrict.put("districtFormRC", customerDetails.getTemp_district_name());
    	Integer districtPercentage = StringsUtil.percentageEqualsIntoTwoString( aadharModel.getDistrict_name(), customerDetails.getTemp_district_name());
    	overAllPercentage = overAllPercentage + districtPercentage;
    	customerDistrict.put("percentageMatching", districtPercentage);
    	map.put("customerDistrict", customerDistrict);
    	Map<String, Object> customerMandal = new HashMap<>();
    	customerMandal.put("mandalFormAadhaar", aadharModel.getMandal_name());
    	customerMandal.put("mandalFormRC", customerDetails.getTemp_mandal_name());
    	Integer mandalPercentage = StringsUtil.percentageEqualsIntoTwoString( aadharModel.getMandal_name(), customerDetails.getTemp_mandal_name());
    	overAllPercentage = overAllPercentage + mandalPercentage;
    	customerMandal.put("percentageMatching", mandalPercentage);
    	map.put("customerMandal", customerMandal);
    	overAllPercentage = overAllPercentage/SomeConstants.SIX;
    	map.put("overAllPercentage", overAllPercentage);
        
		return map;
	}

	@Override
	@Transactional
	public ResponseModel<String> aadhaarSeedingWithSystem(Long applicationId, String prNumber, String aadhaarNumber) {
		ApplicationFormDataEntity  applicationFormDataEntity =  applicationFormDataDAO.getApplicationFormData(applicationId, FormCodeType.RCAS_FORM.getLabel());
		RCAadharSeedModel rcAadharSeedModel = new RCAadharSeedModel();
		try{
			if(!ObjectsUtil.isNull(applicationFormDataEntity)){
				ObjectMapper mapper = new ObjectMapper();
				rcAadharSeedModel = mapper.readValue(applicationFormDataEntity.getFormData(), RCAadharSeedModel.class);
			}
			rcAadharSeedModel.setAadharNumber(aadhaarNumber);
			rcAadharSeedModel.setPrNumber(prNumber);
			
			ApplicationEntity appEntity = applicationDAO.findByApplicationId(applicationId);
			List<ApplicationApprovalHistoryEntity> approvalHistoryList = applicationApprovalHistoryDAO.getApprovalHistories(appEntity.getApplicationId(), appEntity.getIteration(), null, null);
			List<UserActionModel> actionModelList = new ArrayList<>();
			for (ApplicationApprovalHistoryEntity history : approvalHistoryList) {
				UserActionModel actionModel = new UserActionModel();
				actionModel.setUserId(String.valueOf(history.getRtaUserId()));
				actionModel.setUserType(UserType.valueOf(history.getRtaUserRole()));
				actionModel.setUserAction(Status.getLabel(history.getStatus()));
				actionModelList.add(actionModel);
			}
			rcAadharSeedModel.setActionModelList(actionModelList);
			
			RegistrationServiceResponseModel<SaveUpdateResponse> regResponse = registrationService.aadhaarSeedingWithSystem(rcAadharSeedModel);
            if (regResponse.getHttpStatus() == HttpStatus.OK) {
                return new ResponseModel<String>(ResponseModel.SUCCESS);
            }
		}catch (Exception e) {
			logger.error("Getting error in update Or save in RC Aadhar Seeding details");
		}
		return new ResponseModel<String>(ResponseModel.FAILED);
	}

	private int getMatchAadhaarNameAndRcName(String aadhaarName, String rcName) {
		if(StringsUtil.isNullOrEmpty(aadhaarName) || StringsUtil.isNullOrEmpty(rcName)){
			return SomeConstants.ZERO;	
		}
		aadhaarName = aadhaarName.trim().toLowerCase();
		rcName = rcName.trim().toLowerCase();
		List<String> rcNameList = new ArrayList<String>(Arrays.asList(rcName.split(" ")));
		int count =0;
		int matchCount=0;
		int fuzzyRatio =0;
		for(String namePartition:rcNameList){
			if(count==0){
				if(!aadhaarName.contains(namePartition)){
					return SomeConstants.ZERO;
				}
				matchCount++;
			}else{
				if(aadhaarName.contains(namePartition)){
					matchCount ++;
				}else{
					fuzzyRatio = fuzzyRatio + FuzzySearch.weightedRatio(aadhaarName, namePartition);
				}
			}
			count++;
		}
        int nameMatchPercent =matchCount*SomeConstants.HUNDRED/count;
		int percentage = nameMatchPercent + (SomeConstants.HUNDRED-nameMatchPercent)*fuzzyRatio/SomeConstants.HUNDRED;
		return percentage;
	}
	
	private int getMatchAadhaarAndRcParameters(String aadhaarParameter, String rcParameter) {
		if(StringsUtil.isNullOrEmpty(aadhaarParameter) || StringsUtil.isNullOrEmpty(rcParameter)){
			return SomeConstants.ZERO;	
		}
		aadhaarParameter = aadhaarParameter.trim().toLowerCase();
		rcParameter = rcParameter.trim().toLowerCase();
		return FuzzySearch.weightedRatio(aadhaarParameter, rcParameter);
	}
}

package org.rta.citizen.vehiclealteration.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.enums.AlterationCategory;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.RegistrationCategoryModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.VehicleClassDescModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.vehiclealteration.service.VehicleAlterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *	@Author sohan.maurya created on Dec 26, 2016.
 */

@RestController
public class VAApplicationController {

	private static final Logger log = Logger.getLogger(VAApplicationController.class);
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private VehicleAlterationService vehicleAlterationService;
    
    @RequestMapping(value = "/vehicleclassdetails/{regcategorycode}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getVehicleClassDesc(@PathVariable("regcategorycode") String regCategoryCode, @RequestParam(value = "alteration_cat", required = false) String alterationCategory1) throws UnauthorizedException {
         Integer alterationCat = null;
        if(!ObjectsUtil.isNull(alterationCategory1)){
            AlterationCategory alterationCategory = AlterationCategory.valueOf(alterationCategory1.toUpperCase());
            if(ObjectsUtil.isNull(alterationCategory)){
                log.error("Invalid service category: " + alterationCategory1);
            } else {
                alterationCat = alterationCategory.getValue();
            }
        }
        RegistrationServiceResponseModel<List<VehicleClassDescModel>> res = registrationService.getVehicleClassDesc(regCategoryCode, alterationCat);
        log.info("Status from registration : " + res.getHttpStatus());
        List<VehicleClassDescModel> vehicleClassList = res.getResponseBody();
        return ResponseEntity.ok(new ResponseModel< List<VehicleClassDescModel>>(ResponseModel.SUCCESS, vehicleClassList));
    }
    /**
     * @author Gautam.kumar
     * @description Get vehicle type
     * @param prNumber
     * @return
     */
    @RequestMapping(value="/vehicleType/{prNumber}", method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPRType(@PathVariable("prNumber") String prNumber){
    	ResponseModel<RegistrationCategoryModel> vehicleTypeModel = vehicleAlterationService.getVehicleType(prNumber);
    	return ResponseEntity.ok(vehicleTypeModel);
    }
    
	@RequestMapping(value = "/getFuelType/{prNumber}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getFuelType(@PathVariable("prNumber") String prNumber) {
		ResponseModel<String> fuelType = vehicleAlterationService.getFuelType(prNumber);
		return ResponseEntity.ok(fuelType);
	}
	
	@RequestMapping(value = "/getAlterationCovList/{prNumber}/{regCatCode}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getAlterationCovList(@PathVariable("prNumber")String prNumber,@PathVariable("regCatCode") String regCatCode) {
		List<VehicleClassDescModel> covList;
		covList = vehicleAlterationService.getAlterationCovList(prNumber,regCatCode);
		return ResponseEntity.ok(covList);
	}
	
	// USING FOR TESTING PURPOSE ONLY..AFTER TESTING THIS SHOULD BE REMOVE
	@RequestMapping(value = "/test/alteration/{prNumber}/{applicationid}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> getAlterationCovList(@PathVariable("prNumber")String prNumber,@PathVariable("applicationid") Long applicationId) {
		
		ResponseModel<String> response = vehicleAlterationService.saveOrUpdateVehicleAlteration(prNumber,applicationId);
		return ResponseEntity.ok(response);
	}
	
	// USING FOR TESTING PURPOSE ONLY..AFTER TESTING THIS SHOULD BE REMOVE
		@RequestMapping(value = "/bodytypelist", method = RequestMethod.GET, produces = {
				MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
		public ResponseEntity<?> getAlterationBodyTypeList() {
			List<String> response = vehicleAlterationService.getBodyTypeList();
			return ResponseEntity.ok(response);
		}
	
}

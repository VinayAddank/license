package org.rta.citizen.licence.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.rta.citizen.common.model.AadhaarTCSDetailsRequestModel;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AadhaarLicenceController {

	private static final Logger log = Logger.getLogger(AadhaarLicenceController.class);

	@Autowired
	private RegistrationService registrationservice;

	
	@RequestMapping(path = "/getFreshLLRAadhaarDetails", method = RequestMethod.POST)
	public ResponseEntity<?> LicenceAadhaar(@Valid @RequestBody AadhaarTCSDetailsRequestModel requestModel) {
		log.debug("getFreshLLRAadhaarDetails ");
		RegistrationServiceResponseModel<AadharModel> licenceaadhaarModel = null;
		try {
			log.debug("getFreshLLRAadhaarDetails reqeustModel:" + requestModel);
			licenceaadhaarModel = registrationservice.aadharAuthentication(requestModel);
			if (null == licenceaadhaarModel) {
				return ResponseEntity.noContent().build();
			}
			log.debug("getFreshLLRAadhaarDetails response:" + licenceaadhaarModel);
		} catch (Exception ex) {
			log.debug("getFreshLLRAadhaarDetails error  : " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		return ResponseEntity.ok(new ResponseModel<RegistrationServiceResponseModel<AadharModel>>(ResponseModel.SUCCESS,
				licenceaadhaarModel));
	}

}

package org.rta.citizen.aadharseeding.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.rta.citizen.aadharseeding.rc.service.RCASService;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AadhaarSeedingController {

	private static final Logger logger = Logger.getLogger(AadhaarSeedingController.class);

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private RCASService rcasService;

	@RequestMapping(value = "/rc/aadhaarseeding/percentage/details", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<?> aadhaarSeedingPercentageDetails(HttpServletRequest request,
			@RequestParam(name = "applicationno", required = false) String applicationNumber) {

		String token = request.getHeader(tokenHeader);
		Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
		logger.info(" get session : " + sessionId);
		Map<String, Object> aadhaarSeedingDetails = null;
		try {
			aadhaarSeedingDetails = rcasService.getMatchDataBwAadhaarAndRC(applicationNumber);
		} catch (Exception e) {
			logger.error("error when getting Aadhar Seeding percentage details: " + e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
		} 
		if (ObjectsUtil.isNull(aadhaarSeedingDetails)) {
			logger.error("there is not aadhar seeding percentage details ");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
		}
		logger.info(" Aadhar seeding percentage details Send Succsssfully ");
		
		return ResponseEntity.ok(new ResponseModel<Map<String, Object> >(ResponseModel.SUCCESS, aadhaarSeedingDetails));

	}
}

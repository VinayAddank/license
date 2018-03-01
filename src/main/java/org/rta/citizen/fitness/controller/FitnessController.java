package org.rta.citizen.fitness.controller;

import org.apache.log4j.Logger;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.FitnessDetailsModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.fitness.cfx.model.CFXNoticeModel;
import org.rta.citizen.fitness.fresh.service.FitnessFreshService;
import org.rta.citizen.fitness.service.FitnessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *	@Author sohan.maurya created on Dec 20, 2016.
 */

@RestController
public class FitnessController {

	private static final Logger logger = Logger.getLogger(FitnessController.class);

    @Autowired
    private FitnessService fitnessService;

    @Autowired
    private FitnessFreshService fitnessFreshService;


    @RequestMapping(value = "/fitness/details", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> aadharDetails(@RequestParam(name = "applicationno", required = true) String applicationNumber ) {
    	
    	FitnessDetailsModel model=null;
        try {
        	model = fitnessFreshService.getFitnessDetails(applicationNumber);
        } catch (NotFoundException e) {
            logger.error("error when getting finess service: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<Object>(ResponseModel.FAILED));
        } 
        return ResponseEntity.ok(new ResponseModel<FitnessDetailsModel>(ResponseModel.SUCCESS, model));

    }
    
    @RequestMapping(path = "/fitness/certificate/app/{app_no}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getFitnessCertificate(@PathVariable("app_no") String appNo) throws UnauthorizedException {
        logger.info("getFitnessCertificate start for app no :" + appNo);
        ResponseModel<?> res = fitnessService.getFitnessCertificate(appNo);
        logger.info(" getFitnessCertificate end  ");
        return ResponseEntity.ok(res);
    }
    
    @RequestMapping(path = "/fitness/cfx/details/{app_no}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getFitnessCFXNote(@PathVariable("app_no") String appNo) throws UnauthorizedException {
        logger.info("getFitnessCertificate start for app no :" + appNo);
        CFXNoticeModel res = fitnessService.getFitnessCFXNote(appNo);
        if (res == null) {
            logger.error(" nothing found for fcfx note for application number :  " + appNo);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

}

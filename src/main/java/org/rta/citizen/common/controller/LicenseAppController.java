package org.rta.citizen.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.log4j.Logger;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.AadharAuthenticationFailedException;
import org.rta.citizen.common.exception.DataMismatchException;
import org.rta.citizen.common.exception.ExaminerFoundException;
import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.exception.SlotBookingException;
import org.rta.citizen.common.exception.TaskNotFound;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.AadhaarTCSDetailsRequestModel;
import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.service.rtaapplication.RtaApplicationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.licence.entity.tests.QuestionnaireFeedbackEntity;
import org.rta.citizen.licence.model.tests.QuestionModel;
import org.rta.citizen.licence.model.updated.ExamResultModel;
import org.rta.citizen.licence.model.updated.QuestionnaireFeedbackModel;
import org.rta.citizen.licence.service.LicenceService;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LicenseAppController {

	private static final Logger log = Logger.getLogger(LicenseAppController.class);
    
    @Autowired
    private RtaApplicationService rtaApplicationService;
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private LicenceService licenceService; 
    
    @Value("${exam.api.url}")
    private String examApiUrl;
    
    
    @Autowired
    private ApplicationService applicationService;
    
    @Value("${aadhar.authenticate.enabled}")
    protected Boolean authenticateAadhar;
    
    @RequestMapping(value = "/application/action/mvi/app/{app_no}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> mviCompleteAppWithAadhar(@RequestHeader("Authorization") String token, @PathVariable("app_no") String appNumber,
            @RequestBody AadhaarTCSDetailsRequestModel aadharAuthModel, @RequestParam(name = "slotid", required = false) String slotId) throws AadharAuthenticationFailedException, UnauthorizedException {
        log.info("Inside mviCompleteAppWithAadhar.....");
        ResponseModel<?> response = null;
        String userName = jwtTokenUtil.getUserNameFromRegToken(token);
        Long userId = jwtTokenUtil.getUserIdFromRegToken(token);
        String userRole = jwtTokenUtil.getUserRoleFromRegistrationTokenToken(token).get(0);
        log.info("userName: " + userName + " userId: " + userId + " userRole:" + userRole);
        
        //TODO: We can remove skip aadhaar logic, once aadhaar will start working again :(
        AadharModel aadharModel = null;
        boolean skipAadhaar= false;
        if (authenticateAadhar) {
            if(!applicationService.getSession(appNumber).getAadharNumber().trim().equalsIgnoreCase(aadharAuthModel.getUid_num().trim())) {
                response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
                response.setMessage("Provided Aadhaar number is not associated with application number:"+appNumber);
                return ResponseEntity.ok(response);
            }
            RegistrationServiceResponseModel<AadharModel> aadharResponse =
                    registrationService.aadharAuthentication(aadharAuthModel);
            if (ObjectsUtil.isNull(aadharResponse) || aadharResponse.getHttpStatus() != HttpStatus.OK) {
                log.error("eKYC authentication failed");
                throw new AadharAuthenticationFailedException();
            }
            aadharModel = aadharResponse.getResponseBody();
        } else {
            skipAadhaar = true;
        }
        if (skipAadhaar || (null != aadharModel && aadharModel.getAuth_status().equalsIgnoreCase("SUCCESS"))) {
            List<RtaTaskInfo> rtaTaskList = null;
            boolean examPosted = true;
            
            //Call Exam post api            
//            try {
//                ExamPostDataModel llrTestModel = licenceService.getTestDetailsByAppNo(appNumber);
//                log.info("Got Test details based on application number.");
//                URL url = new URL(examApiUrl);
//                ExamPostResponseBaseModel examPostResponse = examService.postExamData(url, llrTestModel);
//                log.info("For application number "+appNumber+" and EXam URI:"+examApiUrl+". Exam post data response: "+examPostResponse);
//                examPosted = "Y".equalsIgnoreCase(examPostResponse.getExamPostResponse().getPostResponse()) ? true :false;
//            } catch (Exception e) {
//                log.info("ERROR in exam post api:" + e.getMessage());
//            }

            if (examPosted) {
                try {
                    //TODO: Now application will move to EXAMINER USER
                    rtaTaskList = rtaApplicationService.actionOnApp(Status.APPROVED, appNumber, userId, userName, userRole, null, slotId);
                } catch (TaskNotFound e) {
                    response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED, null, e.getMessage());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } catch(NotFoundException e){
                    response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED, null, e.getMessage());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                response = new ResponseModel<HashMap<String, Object>>(ResponseModel.SUCCESS);
                response.setActivitiTasks(rtaTaskList);
                return ResponseEntity.ok(response);
            } else {
                response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
                response.setMessage("Exam Data Post Failed. Please Retry !!!");
                return ResponseEntity.ok(response);
            }            
        }
        response = new ResponseModel<HashMap<String, Object>>(ResponseModel.FAILED);
        response.setMessage("Aadhaar authentication failed !!!");
        return ResponseEntity.ok(response);
    }
    
    //This will be called by ROLE_EXAMINER
    @PreAuthorize("hasRole('ROLE_EXAMINER')")
    @RequestMapping(path = "/exam/result", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
    public ResponseEntity<?> postExamResult(@RequestHeader("Authorization") String token, @Valid @RequestBody ExamResultModel examResultModel) {
        Boolean resultsaved = false;
        CitizenApplicationModel appModel =null;
        try {
            log.info("EXAM RESULT is:" + examResultModel.getResult()); 
            if(!ObjectsUtil.isNull(examResultModel.getApplicationId())){
            	appModel = applicationService.getApplicationById(examResultModel.getApplicationId());
            }else{
            	String[] appNoArray = examResultModel.getApplicationNo().split("/");
                if(appNoArray.length > 1) {
                    appModel = applicationService.getApplicationById(Long.parseLong(appNoArray[1]));
                }
            }
            if(null != appModel) {
                resultsaved = licenceService.saveExamResults(examResultModel, appModel);
                String applicationNumber = appModel.getApplicationNumber();
                if("P".equalsIgnoreCase(examResultModel.getResult()) && resultsaved){
                    licenceService.completeExamTask(token, applicationNumber, Status.APPROVED);
                } else {
                    licenceService.completeExamTask(token, applicationNumber, Status.REJECTED);
                }
            } else  {
                log.info("Exam results not saved. WRONG ApplicationNo format:" + examResultModel.getApplicationNo()); 
            }
        }catch (Exception e) {
            log.info("ERROR while saving exam result:" + e.getMessage());
        }
        Map<String, String> response = new HashMap<String, String>();
        if(resultsaved){
        	response.put("status", ResponseModel.SUCCESS);
        	return ResponseEntity.ok(new ResponseModel<Object>(ResponseModel.SUCCESS, response));
        }
        response.put("status", ResponseModel.FAILED);
        return ResponseEntity.ok(new ResponseModel<Object>(ResponseModel.FAILED, response));
    }
    
    
    
    @PreAuthorize("hasRole('ROLE_MVI')")
    @RequestMapping(path = "/test/{testtype}/questions",
            produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> postExamResult(@RequestHeader("Authorization") String token, @PathVariable("testtype") String testType) {
        
        SlotServiceType sst = SlotServiceType.getSlotType(testType);
        if (ObjectsUtil.isNull(sst)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request, testtype is not valid"));
        }
        try {
             List<QuestionModel> questions = licenceService.getQuestions(sst);
             if (!ObjectsUtil.isNullOrEmpty(questions)) {
                 return ResponseEntity.status(HttpStatus.OK)
                         .body(new ResponseModel<List<QuestionModel>>(ResponseModel.SUCCESS, questions));
             }
        } catch (Exception e) {
            log.info("ERROR while getting questions:" + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ResponseModel<>(ResponseModel.SUCCESS, "couldn't find questions", HttpStatus.NO_CONTENT.value()));
    }
    
    @PreAuthorize("hasRole('ROLE_MVI')")
    @RequestMapping(path = "/application/{appnumber}/test/{testtype}/questions",
            produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
    public ResponseEntity<?> postQuestions(@RequestHeader("Authorization") String token, @PathVariable("testtype") String testType,
            @RequestBody List<QuestionModel> questions, @PathVariable("appnumber") String appNumber) {
        
        SlotServiceType sst = SlotServiceType.getSlotType(testType);
        if (ObjectsUtil.isNull(sst)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request, testtype is not valid"));
        }
        String userName = jwtTokenUtil.getUserNameFromRegToken(token);
        if (StringsUtil.isNullOrEmpty(appNumber)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request, appNumber is not valid"));
        }
        ResponseModel<List<QuestionnaireFeedbackEntity>> response = null;
        try {
            response = licenceService.saveQuestionsFeedbackEntities(appNumber, questions, sst, userName);
            /*if (response != null && response.getStatus().equals(ResponseModel.SUCCESS)) {
                licenceService.saveQuestionsFeedback(response.getData());
            }*/
        } catch (Exception e) {
            log.info("ERROR while saving questions feedback:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "unable to save"));
        }
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize("hasRole('ROLE_MVI')")
    @RequestMapping(path = "/application/{appnumber}/test/{testtype}/questions",
            produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> postQuestions(@RequestHeader("Authorization") String token, @PathVariable("testtype") String testType, @PathVariable("appnumber") String appNumber) {
        
        SlotServiceType sst = SlotServiceType.getSlotType(testType);
        if (ObjectsUtil.isNull(sst)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request, testtype is not valid"));
        }
        String userName = jwtTokenUtil.getUserNameFromRegToken(token);
        if (StringsUtil.isNullOrEmpty(appNumber)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "bad request, appNumber is not valid"));
        }
        ResponseModel<List<QuestionnaireFeedbackModel>> response = null;
        try {
            response = licenceService.getQuestionsFeedbackEntities(appNumber, sst, userName);
        } catch (Exception e) {
            log.info("ERROR while getting questions feedback:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .body(new ResponseModel<Object>(ResponseModel.FAILED, null, "unable to get questions feedback"));
        }
        return ResponseEntity.ok(response);
    }
}

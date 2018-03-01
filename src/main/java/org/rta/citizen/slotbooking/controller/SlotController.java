package org.rta.citizen.slotbooking.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.rta.citizen.common.CitizenConstants;
import org.rta.citizen.common.UserSessionService;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.DayModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.ReceiptModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.service.ApplicationService;
import org.rta.citizen.common.service.payment.PaymentService;
import org.rta.citizen.common.service.rtaapplication.RtaApplicationService;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.enums.SlotStatus;
import org.rta.citizen.slotbooking.exception.SlotNotOccupiedException;
import org.rta.citizen.slotbooking.exception.SlotUnavailableException;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javassist.NotFoundException;

@RestController
@RequestMapping("/{servicetype}/slots")
public class SlotController {

    private static final Logger log = Logger.getLogger(SlotController.class);

    /*
     * @Autowired private SlotService slotService;
     */

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SlotService slotService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${slot.activiti.task.key}")
    private String slotActivitiTaskKey;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RtaApplicationService rtaApplicationService;

    /**
     *     Returning free days , by selecting user's end     
     */
    @SuppressWarnings("incomplete-switch")
    @RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "start", required = false) Long start,
            @RequestParam(value = "end", required = false) Long end,
            @RequestParam(value = "slotservicetype", required = false) String slotType,
            @PathVariable(value = "servicetype") String st,
            @RequestParam(value = "rtaofficecode", required = false) String rtaOfficeCode) {
        ServiceType serviceType = ServiceType.getServiceType(st);
        if (ObjectsUtil.isNull(serviceType)) {
            return ResponseEntity.notFound().build();
        }
        SlotStatus slotStatus = SlotStatus.getSlotStatus(status);
        if (ObjectsUtil.isNull(slotStatus)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<>(ResponseModel.FAILED,
                    "slot status is invalid", HttpStatus.BAD_REQUEST.value()));
        }
        if (ObjectsUtil.isNull(slotType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseModel<>(ResponseModel.FAILED, "invalid slottype", HttpStatus.BAD_REQUEST.value()));
        }
        if (ObjectsUtil.isNull(start) || ObjectsUtil.isNull(end)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<>(ResponseModel.FAILED,
                    "required parameters are missing", HttpStatus.BAD_REQUEST.value()));
        }
        List<DayModel> daysList = null;
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        UserSessionModel session = getSession(sessionId);
        if (ObjectsUtil.isNull(session)) {
            log.info("unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
        }
        if (StringsUtil.isNullOrEmpty(slotType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseModel<>(ResponseModel.FAILED, "invalid slot type", HttpStatus.BAD_REQUEST.value()));
        }
        String[] slotServiceTypeArrray = slotType.split(",");
        if (slotServiceTypeArrray == null || slotServiceTypeArrray.length <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseModel<>(ResponseModel.FAILED, "invalid slot type", HttpStatus.BAD_REQUEST.value()));
        }

        List<SlotServiceType> slotServiceTypes = new ArrayList<>();
        for (String slotTypeString : slotServiceTypeArrray) {
            SlotServiceType sst = SlotServiceType.getSlotType(slotTypeString);
            if (sst == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseModel<>(ResponseModel.FAILED,
                        "invalid slot type : " + sst, HttpStatus.BAD_REQUEST.value()));
            }
            slotServiceTypes.add(sst);
        }
        // SlotService slotService = slotFactory.getSlotService(serviceType);
        switch (slotStatus) {
        case FREE: {
            daysList = slotService.getAvailableSlots(session, start, end, slotServiceTypes, serviceType, rtaOfficeCode);
            break;
        }
        case BOOKED: {
            daysList = slotService.getBookedSlots(session, start, end, slotServiceTypes, serviceType, null);
            break;
        }
        }
        if (ObjectsUtil.isNullOrEmpty(daysList)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseModel<>(ResponseModel.SUCCESS, "no slots", HttpStatus.OK.value()));
        }
        return ResponseEntity.ok(new ResponseModel<>(ResponseModel.SUCCESS, daysList));
    }

    /**
     *      User will select day , system will return slot for confirmation
     */
    @RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestHeader(value = "Authorization") String token,
            @Valid @RequestBody List<SlotModel> slotList, @PathVariable(value = "servicetype") String st,
            @RequestParam(value = "sameday") boolean isSameDay,
            @RequestParam(value = "rtaofficecode", required = false) String rtaOfficeCode) {
        ServiceType serviceType = ServiceType.getServiceType(st);
        if (ObjectsUtil.isNull(serviceType)) {
            return ResponseEntity.notFound().build();
        }
        if (ObjectsUtil.isNullOrEmpty(slotList)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseModel<>(ResponseModel.FAILED, "invalid slottype", HttpStatus.BAD_REQUEST.value()));
        }
        /*
         * if (ObjectsUtil.isNull(slotType) ||
         * ObjectsUtil.isNull(SlotServiceType.getSlotType(slotType))) { return
         * ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new
         * ResponseModel<>(ResponseModel.FAILED, "invalid slottype",
         * HttpStatus.BAD_REQUEST.value())); } SlotServiceType typeOfSlot =
         * SlotServiceType.getSlotType(slotType);
         */
        // SlotService slotService = slotFactory.getSlotService(serviceType);
        try {
            Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
            UserSessionModel session = getSession(sessionId);
            if (ObjectsUtil.isNull(session)) {
                log.info("unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
            }
            slotList = slotService.occupyUpdated(session, slotList, serviceType, isSameDay, rtaOfficeCode);
        } catch (SlotUnavailableException e) {
            log.error("unable to occupy slot" + e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ResponseModel<>(ResponseModel.FAILED, e.getMessage(), HttpStatus.NOT_ACCEPTABLE.value()));
        } catch (NotFoundException e) {
            log.error("not found exception " + e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(ResponseModel.FAILED, e.getMessage(), HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok(new ResponseModel<>(ResponseModel.SUCCESS, slotList));
    }

    /**
     *    If user confirm the given slot , call this for confirming slot
     */
    @RequestMapping(value = "/{task_def}", produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.PUT)
    public ResponseEntity<?> update(@RequestHeader(value = "Authorization") String token,
            @Valid @RequestBody List<SlotModel> slotModel, @PathVariable("task_def") String taskDef,
            @PathVariable(value = "servicetype") String st) {
        ServiceType serviceType = ServiceType.getServiceType(st);
        if (ObjectsUtil.isNull(serviceType)) {
            return ResponseEntity.notFound().build();
        }
        if (StringsUtil.isNullOrEmpty(taskDef) || !taskDef.equals(slotActivitiTaskKey)) {
            log.error("invalid slot activiti : " + taskDef);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseModel<>(ResponseModel.FAILED, "invalid slot activiti", HttpStatus.NOT_FOUND.value()));
        }
        if (ObjectsUtil.isNull(slotModel)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "slot body", HttpStatus.BAD_REQUEST.value()));
        }
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        UserSessionModel session = getSession(sessionId);
        if (ObjectsUtil.isNull(session)) {
            log.info("unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
        }
        // SlotServiceType typeOfSlot = SlotServiceType.getSlotType(slotType);
        // SlotService slotService = slotFactory.getSlotService(serviceType);
        ResponseModel<ReceiptModel> receiptModel;
        try {
            receiptModel = slotService.confirm(session, slotModel);
            // uncomment in case of process
            Assignee assignee = new Assignee();
            assignee.setUserId(CitizenConstants.CITIZEN_USERID);
            Map<String, Object> variableMap = new HashMap<>();
            if (serviceType == ServiceType.FC_OTHER_STATION) {
                try {
                    String rtaOfficeCode = receiptModel.getData().getRtaOfficeModel().getCode();
                    variableMap.put(ActivitiService.RTA_OFFICE_CODE, rtaOfficeCode);
                } catch (Exception e) {
                    log.error("rtaOfficeCode not found for sessionId : " + sessionId);
                    throw new NotFoundException("unable to get rtaoffice");
                }
            }
            //--------for vehicle alteration set app will go on pay diff tax or not -------------------
            if(serviceType == ServiceType.VEHICLE_ATLERATION){
            	variableMap.put(ActivitiService.PAY_DIFF_TAX, paymentService.isPayDiffTaxForVA(sessionId));
            }
            //-----------------------------------------------------------------------------------------
            String exeId = applicationService.getProcessInstanceId(sessionId);
            ActivitiResponseModel<List<RtaTaskInfo>> actResponse = activitiService.completeTask(assignee, taskDef,
            		exeId, true, variableMap);
            List<RtaTaskInfo> rtTaskList = actResponse.getActiveTasks();
            if (ObjectsUtil.isNull(rtTaskList) || rtTaskList.size() <= 0) {
                RtaTaskInfo tInfo = new RtaTaskInfo();
                tInfo.setTaskDefKey(ActivitiService.APP_COMPLETED);
                tInfo.setProcessDefId(session.getServiceType().getCode());
                rtTaskList.add(tInfo);
                actResponse.setActiveTasks(rtTaskList);
                rtaApplicationService.completeAppOnly(exeId, Status.APPROVED, "CITIZEN1", UserType.ROLE_CITIZEN);
            }
            receiptModel.setActivitiTasks(actResponse.getActiveTasks());
        } catch (SlotNotOccupiedException e) {
            log.error("unable to confirm slot" + e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "Slot not occupied"));
        } catch (SlotUnavailableException e) {
            log.error("unable to confirm slot" + e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "Slot already booked"));
        } catch (UnauthorizedException e) {
            log.error("unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
        } catch (NotFoundException e) {
            log.error("unable to get rtaoffice for sessionId : " + sessionId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
        }
        return ResponseEntity.ok(new ResponseModel<>(ResponseModel.SUCCESS, receiptModel));
    }

    public UserSessionModel getSession(Long sessionId) {
        return userSessionService.getSession(sessionId);
    }

    @RequestMapping(value = "/test")
    public ResponseEntity<?> test() {
        // slotService.removeExpiredSlots("AP002", 1482258600L,
        // DateUtil.toCurrentUTCTimeStamp());
        return null;
    }

    /**
     *   Return RTO office list except user's belonging RTO office     
     */
    @RequestMapping(value = "/rtaoffices", produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
    public ResponseEntity<?> getRTAOffice(@RequestHeader(value = "Authorization") String token,
            @RequestParam(name = "state", required = false) String stateCode,
            @PathVariable(value = "servicetype") String st) {
        Long sessionId = jwtTokenUtil.getSessionIdFromToken(token);
        UserSessionModel session = getSession(sessionId);
        if (ObjectsUtil.isNull(session)) {
            log.info("unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseModel<>(ResponseModel.FAILED, "", HttpStatus.UNAUTHORIZED.value()));
        }
        List<RTAOfficeModel> otherOffices = slotService.getRTAOfficeByState(session, stateCode);
        return ResponseEntity.ok(otherOffices);
    }

}

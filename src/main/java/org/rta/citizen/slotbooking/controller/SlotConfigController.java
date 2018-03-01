package org.rta.citizen.slotbooking.controller;

import java.util.List;

import org.rta.citizen.common.UserSessionService;
import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.utils.JwtTokenUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.slotbooking.model.RTAOfficeScheduleModel;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.RTAOfficeSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{servicetype}/slots/config")
public class SlotConfigController {

    @Autowired
    private RTAOfficeSchedulingService scheduleService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> getSlotsOfRTAOffice(@RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "slotservicetype", required = true) String slotType,
            @PathVariable(value = "servicetype") String st,
            @RequestParam(value = "rtaOfficeCode") String rtaOfficeCode) {
        RTAOfficeScheduleModel c = scheduleService.getSchedule(rtaOfficeCode);
        List<SlotModel> slotsMap = scheduleService.getTimeSlots(c.getStartTime(), c.getEndTime(), c.getDuration());
        if (ObjectsUtil.isNullOrEmpty(slotsMap)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseModel<>(ResponseModel.SUCCESS, "no slots", HttpStatus.NO_CONTENT.value()));
        }
        return ResponseEntity.ok(new ResponseModel<>(ResponseModel.SUCCESS, slotsMap));
    }
    
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET, value = "/insert")
    public ResponseEntity<?> insertSchedules(@RequestParam(name = "category") String category) {
        scheduleService.insertIntoDB(SlotCategory.getServiceTypeCat(category));
        return ResponseEntity.ok().build();
    }

}

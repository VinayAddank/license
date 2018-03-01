package org.rta.citizen.common.controller;

import java.util.List;

import org.rta.citizen.common.model.DayModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.service.CalendarService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @RequestMapping(value = "/holidays", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> getHolidays(@RequestParam(value = "Authorization", required = false) String token,
            @RequestParam(value = "year", required = false) Long year,
            @RequestParam(value = "startmonth", required = false) Long startMonth,
            @RequestParam(value = "endmonth", required = false) Long endMonth,
            @RequestParam(value = "startdate", required = false) Long startDate,
            @RequestParam(value = "enddate", required = false) Long endDate,
            @RequestParam(value = "start", required = false) Long start,
            @RequestParam(value = "end", required = false) Long end) {
        HttpStatus status;
        ResponseModel<List<DayModel>> responseModel;
        if (ObjectsUtil.isNull(start) || ObjectsUtil.isNull(end)) {
            status = HttpStatus.BAD_REQUEST;
            responseModel = new ResponseModel<>(ResponseModel.FAILED);
            responseModel.setMessage("invalid start or end parameters");
            responseModel.setStatusCode(status.value());
            return ResponseEntity.status(status).body(responseModel);
        }
        List<DayModel> holidayModelList = calendarService.getHolidaysList(start, end);
        if (ObjectsUtil.isNullOrEmpty(holidayModelList)) {
            responseModel = new ResponseModel<>(ResponseModel.SUCCESS);
            responseModel.setStatusCode(HttpStatus.NO_CONTENT.value());
            responseModel.setMessage(HttpStatus.NO_CONTENT.name());
            status = HttpStatus.OK;
        } else {
            responseModel = new ResponseModel<>(ResponseModel.SUCCESS);
            responseModel.setStatusCode(HttpStatus.OK.value());
            responseModel.setMessage(HttpStatus.OK.name());
            responseModel.setData(holidayModelList);
            status = HttpStatus.OK;
        }
        return ResponseEntity.status(status).body(responseModel);
    }

}

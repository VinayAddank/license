package org.rta.citizen.common.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.rta.citizen.common.converters.HolidayConverter;
import org.rta.citizen.common.dao.CalendarDAO;
import org.rta.citizen.common.entity.HolidayEntity;
import org.rta.citizen.common.model.DayModel;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {

    @Autowired
    private CalendarDAO calendarDAO;

    @Autowired
    private HolidayConverter holidayConverter;

    @Override
    @Transactional
    public List<DayModel> getHolidays(Integer year) {
        Long startTime = DateUtil.getSeconds(year, 0, 0);
        Long endTime = DateUtil.getSeconds(year, 11, 31);
        return getHolidaysList(startTime, endTime);
    }

    @Override
    @Transactional
    public List<DayModel> getHolidaysList(Long startTime, Long endTime) {
        List<HolidayEntity> holidaysList = calendarDAO.getHolidays(startTime, endTime);
        List<DayModel> holidaysModelList = new ArrayList<>();
        if (!ObjectsUtil.isNull(holidaysList)) {
            holidaysList.stream().forEach(e -> {
                holidaysModelList.add(holidayConverter.convertToHolidayEntity(e));
            });
        }
        return holidaysModelList;
    }

    @Override
    @Transactional
    public List<DayModel> getHolidays(Integer year, Integer startMonth, Integer endMonth) {
        Long startTime = DateUtil.getSeconds(year, startMonth, 0);
        Long endTime = DateUtil.getSeconds(year, endMonth, 31);
        return getHolidaysList(startTime, endTime);
    }

    @Override
    @Transactional
    public List<DayModel> getHolidays(Integer year, Integer startMonth, Integer endMonth, Integer startDay,
            Integer endDay) {
        Long startTime = DateUtil.getSeconds(year, startMonth, startDay);
        Long endTime = DateUtil.getSeconds(year, endMonth, endDay);
        return getHolidaysList(startTime, endTime);
    }
    
    @Override
    @Transactional
    public Boolean isHoliday(Long timestamp) {
        HolidayEntity holiday = calendarDAO.getHolidayForDay(timestamp);
        if (ObjectsUtil.isNull(holiday)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}

package org.rta.citizen.common.service;

import java.util.List;

import org.rta.citizen.common.model.DayModel;

public interface CalendarService {

    public List<DayModel> getHolidays(Integer year);
    public List<DayModel> getHolidays(Integer year, Integer startMonth, Integer endMonth);
    public List<DayModel> getHolidays(Integer year, Integer startMonth, Integer endMonth, Integer startDay, Integer endDay);
    public List<DayModel> getHolidaysList(Long startTime, Long endTime);
    public Boolean isHoliday(Long timestamp);
    
}

package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.entity.HolidayEntity;

public interface CalendarDAO {

    List<HolidayEntity> getHolidays(Long startTime, Long endTime);
    HolidayEntity getHolidayForDay(Long time);

}

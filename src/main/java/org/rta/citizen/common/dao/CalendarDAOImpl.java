package org.rta.citizen.common.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.entity.HolidayEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CalendarDAOImpl extends BaseDAO<HolidayEntity> implements CalendarDAO {

    public CalendarDAOImpl() {
        super(HolidayEntity.class);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<HolidayEntity> getHolidays(Long startTime, Long endTime) {
        Criteria criteria = getSession().createCriteria(HolidayEntity.class);
        criteria.add(Restrictions.between("date", startTime, endTime));
        criteria.add(Restrictions.eq("isEnabled", Boolean.TRUE));
        return (List<HolidayEntity>)criteria.list();
    }

    @Override
    public HolidayEntity getHolidayForDay(Long timestamp) {
        Criteria criteria = getSession().createCriteria(HolidayEntity.class);
        criteria.add(Restrictions.eq("date", timestamp));
        criteria.add(Restrictions.eq("isEnabled", Boolean.TRUE));
        return (HolidayEntity) criteria.uniqueResult();
    }

}

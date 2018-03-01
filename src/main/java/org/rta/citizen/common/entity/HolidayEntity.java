package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "holidays")
public class HolidayEntity extends BaseEntity {
    
    private static final long serialVersionUID = 4368777701635808366L;

    @Id
    @Column(name = "holiday_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holiday_seq")
    @SequenceGenerator(name = "holiday_seq", sequenceName = "holiday_seq", allocationSize = 1)
    private Long holidayId;

    @Column(name = "date")
    private Long date;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    public Long getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(Long holidayId) {
        this.holidayId = holidayId;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}

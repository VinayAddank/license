package org.rta.citizen.licence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.licence.entity.updated.BaseLicenseEntity;

@Entity
@Table(name = "dl_series_master")
public class DlSeriesMasterEntity extends BaseLicenseEntity {

	private static final long serialVersionUID = -2433931729426897343L;

	@Id
	@Column(name = "dl_series_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dl_series_seq")
	@SequenceGenerator(name = "dl_series_seq", sequenceName = "dl_series_seq", allocationSize = 1)
	private Long dlSeriesId;

	@Column(name = "use_number")
	private Integer useNumber;

	@Column(name = "start_number")
	private Integer startNumber;

	@Column(name = "end_number")
	private Integer endNumber;

	@Column(name = "year")
	private Integer year;

	public Long getDlSeriesId() {
		return dlSeriesId;
	}

	public void setDlSeriesId(Long dlSeriesId) {
		this.dlSeriesId = dlSeriesId;
	}

	public Integer getUseNumber() {
		return useNumber;
	}

	public void setUseNumber(Integer useNumber) {
		this.useNumber = useNumber;
	}

	public Integer getStartNumber() {
		return startNumber;
	}

	public void setStartNumber(Integer startNumber) {
		this.startNumber = startNumber;
	}

	public Integer getEndNumber() {
		return endNumber;
	}

	public void setEndNumber(Integer endNumber) {
		this.endNumber = endNumber;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
}

package org.rta.citizen.licence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.rta.citizen.common.entity.BaseEntity;

@Entity
@Table(name = "age_group_ref")
public class LlrAgeGroupRefEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 8387838928367969044L;

	@Id
	@Column(name = "age_group_cd")
	private String ageGroupCode;

	@Column(name = "age_group_desc")
	private String ageGroupDesc;

	@Column(name = "age_start")
	private Integer ageStart;

	@Column(name = "age_end")
	private Integer ageEnd;

	@Column(name = "is_active")
	private String isActive;

	public String getAge_group_cd() {
		return ageGroupCode;
	}

	public void setAge_group_cd(String age_group_cd) {
		this.ageGroupCode = age_group_cd;
	}

	public String getAgeGroupDesc() {
		return this.ageGroupDesc;
	}

	public void setAgeGroupDesc(String ageGroupDesc) {
		this.ageGroupDesc = ageGroupDesc;
	}

	public Integer getAge_start() {
		return ageStart;
	}

	public void setAge_start(Integer age_start) {
		this.ageStart = age_start;
	}

	public Integer getAge_end() {
		return ageEnd;
	}

	public void setAge_end(Integer age_end) {
		this.ageEnd = age_end;
	}

	public String isIs_active() {
		return isActive;
	}

	public void setIs_active(String is_active) {
		this.isActive = is_active;
	}
}

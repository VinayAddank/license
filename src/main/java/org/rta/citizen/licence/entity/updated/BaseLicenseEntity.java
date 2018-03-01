package org.rta.citizen.licence.entity.updated;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class BaseLicenseEntity implements Serializable {

	private static final long serialVersionUID = -1555152942774514424L;

	@Column(name = "created_by", length = 50)
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "modified_by", length = 50)
	private String modifiedBy;

	@Temporal(TemporalType.DATE)
	@Column(name = "modified_on")
	private Date modifiedOn;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

}

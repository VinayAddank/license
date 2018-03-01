package org.rta.citizen.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseMasterModel {
	
	@JsonIgnore
	private Long id;
	private String name;
	@JsonIgnore
	private Boolean status;
	@JsonIgnore
	private String createdBy;
	@JsonIgnore
	private Long createdOn;
	@JsonIgnore
	private String updatedBy;
	@JsonIgnore
	private Long updatedOn;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Long getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Long updatedOn) {
		this.updatedOn = updatedOn;
	}
	
    @Override
    public String toString() {
        return "BaseMasterModel [id=" + id + ", name=" + name + ", status=" + status + ", createdBy=" + createdBy
                + ", createdOn=" + createdOn + ", updatedBy=" + updatedBy + ", updatedOn=" + updatedOn + "]";
    }

	

}

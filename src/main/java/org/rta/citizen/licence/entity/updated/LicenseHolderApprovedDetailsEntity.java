package org.rta.citizen.licence.entity.updated;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.BaseEntity;

@Entity
@Table(name = "license_holder_approved_details")
public class LicenseHolderApprovedDetailsEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -6405445474970406584L;

	@Id
	@Column(name = "license_holder_approved_details_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_holder_approved_details_seq")
	@SequenceGenerator(name = "license_holder_approved_details_seq", sequenceName = "license_holder_approved_details_seq", allocationSize = 1)
	private Long holderApprovedDetailsId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private ApplicationEntity applicationEntity;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "guardian_name")
	private String guardianName;

	public Long getHolderApprovedDetailsId() {
		return holderApprovedDetailsId;
	}

	public void setHolderApprovedDetailsId(Long holderApprovedDetailsId) {
		this.holderApprovedDetailsId = holderApprovedDetailsId;
	}

	public ApplicationEntity getApplicationEntity() {
		return applicationEntity;
	}

	public void setApplicationEntity(ApplicationEntity applicationEntity) {
		this.applicationEntity = applicationEntity;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

}

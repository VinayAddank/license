package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.enums.KeyType;

@Entity
@Table(name = "login_attempt")
public class LoginAttemptHistoryEntity extends BaseEntity {

	private static final long serialVersionUID = 1112592383482317130L;

	@Id
	@Column(name = "login_attempt_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "login_attempt_seq")
	@SequenceGenerator(name = "login_attempt_seq", sequenceName = "login_attempt_seq", allocationSize = 1)
	private Long id;

	@Column(name = "aadhar_number")
	private String aadharNumber;

	@Column(name = "login_time")
	private Long loginTime;

	@Column(name = "unique_key")
	private String uniqueKey;

	@Column(name = "key_type")
	@Enumerated(EnumType.STRING)
	private KeyType keyType;

	@Column(name = "login_count")
	private Integer loginCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	public Integer getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

}

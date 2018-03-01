/**
 * 
 */
package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author arun.verma
 *
 */

@Entity
@Table(name = "user_attempt_log")
public class UserAttemptLogEntity extends BaseEntity {

	private static final long serialVersionUID = -2942752160589809106L;

	@Id
	@Column(name = "log_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_attempt_log_seq_gen")
	@SequenceGenerator(name = "user_attempt_log_seq_gen", sequenceName = "user_attempt_log_seq", allocationSize = 1)
	private Long logId;

	@Column(name = "service_type", length = 50)
	private String serviceType;

	@Column(name = "unique_key", length = 100)
	private String uniqueKey;

	@Column(name = "key_type", length = 50)
	private String keyType;

	@Column(name = "aadhar_number", length = 100)
	private String aadharNumber;
	
	@Column(name = "status", length = 100)
	private String status;

	@Column(name = "description", length = 250)
	private String description;
	
	@Column(name = "code")
	private Integer code;

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

}

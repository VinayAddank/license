package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;



@Entity
@Table(name = "event")
public class EventEntity extends BaseEntity {

	private static final long serialVersionUID = 7312215728427844033L;

	@Id
	@Column(name = "event_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_seq")
	@SequenceGenerator(name = "event_id_seq", sequenceName = "event_id_seq", allocationSize = 1)
	private Long eventId;
	
	@Column(name = "application_id")
	private Long applicationId;
	
	@Column(name = "sms_notify")
	private Boolean smsNotify;
	
	@Column(name = "iteration")
	private Integer iteration;
	
	@Column(name = "email_notify")
	private Boolean emailNotify;
	
	@Column(name = "event_type")
	private String eventType;
	
	@Column(name = "attachement")
	private String attachement;
	
	@Column(name = "service_type")
	private String serviceType;
	
	public EventEntity() {
		super();
		setEmailNotify(false);
		setSmsNotify(false);
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public Boolean getSmsNotify() {
		return smsNotify;
	}

	public void setSmsNotify(Boolean smsNotify) {
		this.smsNotify = smsNotify;
	}

	public Integer getIteration() {
		return iteration;
	}

	public void setIteration(Integer iteration) {
		this.iteration = iteration;
	}

	public Boolean getEmailNotify() {
		return emailNotify;
	}

	public void setEmailNotify(Boolean emailNotify) {
		this.emailNotify = emailNotify;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getAttachement() {
		return attachement;
	}

	public void setAttachement(String attachement) {
		this.attachement = attachement;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	
}

package org.rta.citizen.licence.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class LicenceFreshModel {

	@NotNull(message = "uid_num is missing")
	private String uid_num;

	public String getUid_num() {
		return uid_num;
	}

	public void setUid_num(String uid_num) {
		this.uid_num = uid_num;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@NotNull(message = "loginLLRFresh is missing")
	private String serviceType;

}

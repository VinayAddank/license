package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StateModel extends BaseMasterModel {
	
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

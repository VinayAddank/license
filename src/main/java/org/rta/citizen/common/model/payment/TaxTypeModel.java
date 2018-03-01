package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;
import org.rta.citizen.common.model.BaseMasterModel;

@XmlRootElement
public class TaxTypeModel extends BaseMasterModel {
	private String taxTypeCode;
	private double percentage;

	public String getTaxTypeCode() {
		return taxTypeCode;
	}

	public void setTaxTypeCode(String taxTypeCode) {
		this.taxTypeCode = taxTypeCode;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
}

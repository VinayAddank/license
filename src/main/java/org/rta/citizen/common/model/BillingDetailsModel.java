package org.rta.citizen.common.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class BillingDetailsModel extends BaseModel {

    private static final long serialVersionUID = -6277388528691591240L;

    @NotNull(message = "invoiceNumber is missing")
    private String invoiceNumber;

    @NotNull(message = "invoiceDate is missing")
    private Long invoiceDate;

    @NotNull(message = "invoiceValue is missing")
    private Double invoiceValue;

    // @NotNull(message = "helmetInvoiceAmount is missing")
    private Double helmetInvoiceAmount;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Long invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Double getInvoiceValue() {
        return invoiceValue;
    }

    public void setInvoiceValue(Double invoiceValue) {
        this.invoiceValue = invoiceValue;
    }

    public Double getHelmetInvoiceAmount() {
        return helmetInvoiceAmount;
    }

    public void setHelmetInvoiceAmount(Double helmetInvoiceAmount) {
        this.helmetInvoiceAmount = helmetInvoiceAmount;
    }


}

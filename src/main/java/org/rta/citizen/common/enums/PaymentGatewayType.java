package org.rta.citizen.common.enums;

import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.StringsUtil;

public enum PaymentGatewayType {

    SBI( "sbi"), PAYU("payu");

    private String label;

    private PaymentGatewayType() {}

    private PaymentGatewayType( String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static PaymentGatewayType getPaymentGatewayType(String value) {
        if (ObjectsUtil.isNull(value)) {
            return null;
        }
        if (value.equalsIgnoreCase(SBI.getLabel())) {
            return SBI;
        } else if (value.equalsIgnoreCase(PAYU.getLabel())) {
            return PAYU;
        } 
        return null;
    }
    
    public static PaymentGatewayType getPaymentGatewayType(String value,  PaymentGatewayType defaultValue) {
    
        if (StringsUtil.isNullOrEmpty(value) ) {
            return defaultValue;
        }
        if (value.equalsIgnoreCase( SBI.getLabel())) {
            return SBI;
        } else  if (value.equalsIgnoreCase( PAYU.getLabel())) {
            return PAYU;
        } 
        return defaultValue;
    }
}
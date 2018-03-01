package org.rta.citizen.common.converters.payment;

import org.rta.citizen.common.entity.payment.FeeDetailEntity;
import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.common.utils.NumberParser;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

@Component
public class FeeDetailConverter {

	public FeeModel convertToModel(FeeDetailEntity source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        FeeModel fd = new FeeModel();
        fd.setPostalCharge(NumberParser.numberFormat(source.getPostalCharge()));
        fd.setApplicationFee(NumberParser.numberFormat(source.getApplicationFee()));
        fd.setApplicationServiceCharge(NumberParser.numberFormat(source.getApplicationServiceCharge()));
        fd.setFitnessFee(NumberParser.numberFormat(source.getFitnessFee()));
        fd.setFitnessServiceCharge(NumberParser.numberFormat(source.getFitnessServiceCharge()));
        fd.setOtherPermitFee(NumberParser.numberFormat(source.getOtherPermitFee()));
        fd.setPermitFee(NumberParser.numberFormat(source.getPermitFee()));
        fd.setPermitServiceCharge(NumberParser.numberFormat(source.getPermitServiceCharge()));
        fd.setSmartCardFee(NumberParser.numberFormat(source.getSmartCardFee()));
        fd.setPenalty(NumberParser.numberFormat(source.getPenaltyFee()));
        fd.setLicenseTestFee(NumberParser.numberFormat(source.getLicenseTestFee()));
        fd.setLateFee(NumberParser.numberFormat(source.getLateFee()));
        fd.setTotalFee(NumberParser.numberFormat(source.getTotalFee()));
        fd.setSpecialNumberFee(NumberParser.numberFormat(source.getSpecialNumberFee()));
        fd.setHsrpFee(NumberParser.numberFormat(source.getHSRPFee()));
        return fd;
       
    }
	
	public FeeModel convertToModel4License(FeeDetailEntity source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        FeeModel fd = new FeeModel();
        fd.setApplicationFee(NumberParser.numberFormat(source.getApplicationFee()));
        fd.setApplicationServiceCharge(NumberParser.numberFormat(source.getApplicationServiceCharge()));
        fd.setLicenseTestFee(NumberParser.numberFormat(source.getLicenseTestFee()));
        fd.setPenalty(NumberParser.numberFormat(source.getPenaltyFee()));
        fd.setSmartCardFee(NumberParser.numberFormat(source.getSmartCardFee()));
        fd.setTotalFee(NumberParser.numberFormat(source.getTotalFee()));
        fd.setPostalCharge(NumberParser.numberFormat(source.getPostalCharge()));
        return fd;
       
    }
}

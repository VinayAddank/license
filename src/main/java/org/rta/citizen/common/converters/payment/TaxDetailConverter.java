package org.rta.citizen.common.converters.payment;

import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.model.payment.TaxModel;
import org.rta.citizen.common.utils.NumberParser;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

@Component
public class TaxDetailConverter {

	public TaxModel convertToModel(TaxDetailEntity source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        TaxModel td = new TaxModel();
        td.setTaxType(source.getTaxType());
        td.setTaxAmt(NumberParser.numberFormat(source.getTaxAmt()));
		td.setTaxPercent(NumberParser.numberFormat(source.getTaxPercentage()));
		td.setTotalAmt(NumberParser.numberFormat(source.getTotalAmt()));
		td.setTaxValidUpto(source.getValidUpto());
		td.setGreenTaxAmt(NumberParser.numberFormat(source.getGreenTaxAmt()));
		td.setPenalty(NumberParser.numberFormat(source.getPenaltyAmt()));
		td.setServiceFee(NumberParser.numberFormat(source.getServiceFee()));
		td.setCessFee(NumberParser.numberFormat(source.getCessFee()));
		td.setCessFeeValidUpto(source.getCessFeeValidUpto());
		td.setPenaltyArrears(NumberParser.numberFormat(source.getPenaltyAmtArrears()));
		td.setTaxArrears(NumberParser.numberFormat(source.getTaxAmtArrears()));
		if(!ObjectsUtil.isNull(source.getCompoundFee())){
			td.setCompoundFee(NumberParser.numberFormat(source.getCompoundFee()));
		}
        return td;
       
    }
	
	public TaxModel convertToModel4Sync(TaxDetailEntity source , UserSessionEntity userSessionEntity) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        TaxModel td = new TaxModel();
        td.setTaxType(source.getTaxType());
        td.setTaxAmt(NumberParser.numberFormat(source.getTaxAmt()));
		td.setTaxPercent(NumberParser.numberFormat(source.getTaxPercentage()));
		td.setTotalAmt(NumberParser.numberFormat(source.getTotalAmt()));
		td.setTaxValidUpto(source.getValidUpto());
		td.setGreenTaxAmt(NumberParser.numberFormat(source.getGreenTaxAmt()));
		td.setPenalty(NumberParser.numberFormat(source.getPenaltyAmt()));
		td.setServiceFee(NumberParser.numberFormat(source.getServiceFee()));
		td.setAdharNumber(userSessionEntity.getAadharNumber());
		td.setPrNumber(userSessionEntity.getUniqueKey());
		td.setCessFee(NumberParser.numberFormat(source.getCessFee()));
		td.setCessFeeValidUpto(source.getCessFeeValidUpto());
		td.setPenaltyArrears(NumberParser.numberFormat(source.getPenaltyAmtArrears()));
		td.setTaxArrears(NumberParser.numberFormat(source.getTaxAmtArrears()));
		if(source.getGreenTaxValidTo() != null)
		td.setGreenTaxValidTo(source.getGreenTaxValidTo());
		if(!ObjectsUtil.isNull(source.getCompoundFee())){
		td.setCompoundFee(NumberParser.numberFormat(source.getCompoundFee()));
		}
		return td;
       
    }
}

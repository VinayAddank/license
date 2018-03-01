package org.rta.citizen.common.service.payment.impl;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.payment.CitizenInvoiceDAO;
import org.rta.citizen.common.dao.payment.FeeDetailDAO;
import org.rta.citizen.common.dao.payment.TaxDetailDAO;
import org.rta.citizen.common.dao.payment.TransactionDetailDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.payment.CitizenInvoiceEntity;
import org.rta.citizen.common.entity.payment.FeeDetailEntity;
import org.rta.citizen.common.entity.payment.TaxDetailEntity;
import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.service.payment.CitizenInvoiceService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.NumberParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitizenInvoiceServiceImpl implements CitizenInvoiceService {

	private static final Logger log = Logger.getLogger(CitizenInvoiceServiceImpl.class);

	@Autowired
	private FeeDetailDAO feeDetailDAO;
	@Autowired
	private TaxDetailDAO taxDetailDAO;
	@Autowired
	private CitizenInvoiceDAO citizenInvoiceDAO;
	private TransactionDetailDAO transactionDetailDAO;

	@Override
	@Transactional
	public CitizenInvoiceEntity createInvoice(ApplicationEntity applicationEntity, ApplicationTaxModel appTaxModel) {
		CitizenInvoiceEntity citizenInvoiceEntity = null;
		FeeDetailEntity feeDetailEntity = feeDetailDAO.getByAppId(applicationEntity);
		TaxDetailEntity taxDetailEntity = taxDetailDAO.getByAppId(applicationEntity);
		citizenInvoiceEntity = citizenInvoiceDAO.getByAppNdServiceType(applicationEntity);
		if (citizenInvoiceEntity != null) {
			if(taxDetailEntity != null)
				citizenInvoiceEntity.setTotalAmount(NumberParser
						.roundOff((feeDetailEntity == null ? 0:feeDetailEntity.getTotalFee())  + (taxDetailEntity == null ? 0:taxDetailEntity.getTotalAmt()), "########.##"));
				else
					citizenInvoiceEntity.setTotalAmount(NumberParser
							.roundOff(feeDetailEntity == null ? 0:feeDetailEntity.getTotalFee() , "########.##"));
			citizenInvoiceEntity.setModifiedBy(applicationEntity.getLoginHistory().getAadharNumber());
			citizenInvoiceEntity.setModifiedOn(DateUtil.toCurrentUTCTimeStamp());
		} else {
			citizenInvoiceEntity = new CitizenInvoiceEntity();
			citizenInvoiceEntity.setCreatedBy(applicationEntity.getLoginHistory().getAadharNumber());
			citizenInvoiceEntity.setApplicationId(applicationEntity);
			citizenInvoiceEntity.setInvoiceAmt(appTaxModel.getInvoiceAmt());
			citizenInvoiceEntity.setInvoiceDate(appTaxModel.getInvoiceDate());
			citizenInvoiceEntity.setRegFeeDtlId(feeDetailEntity);
			citizenInvoiceEntity.setTaxDtlId(taxDetailEntity);
			if(taxDetailEntity != null)
			citizenInvoiceEntity.setTotalAmount(NumberParser
					.roundOff((feeDetailEntity == null ? 0:feeDetailEntity.getTotalFee())  + (taxDetailEntity == null ? 0:taxDetailEntity.getTotalAmt()), "########.##"));
			else
				citizenInvoiceEntity.setTotalAmount(NumberParser
						.roundOff(feeDetailEntity == null ? 0:feeDetailEntity.getTotalFee() , "########.##"));
		}
		citizenInvoiceDAO.saveOrUpdate(citizenInvoiceEntity);
		return citizenInvoiceEntity;
	}

}

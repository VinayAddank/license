package org.rta.citizen.licence.service.updated.impl;

import java.util.Calendar;

import org.rta.citizen.common.utils.NumberParser;
import org.rta.citizen.licence.dao.DlSeriesMasterDAO;
import org.rta.citizen.licence.entity.DlSeriesMasterEntity;
import org.rta.citizen.licence.service.updated.DrivingLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrivingLicenseServiceImpl implements DrivingLicenseService {

	@Autowired
	private DlSeriesMasterDAO dlSeriesMasterDAO;

	@Override
	public String getAndUpdateDrivingLicenseSeries(String rtaOfficeCode) {
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		DlSeriesMasterEntity dlSeries = dlSeriesMasterDAO.getByYear(year);
		Integer useNumber = dlSeries.getUseNumber();
		String series = NumberParser.getFormatedNumber(useNumber, "xxxxxxx", 'x', '0');
		dlSeries.setUseNumber(1 + useNumber);
		dlSeriesMasterDAO.update(dlSeries);

		return new StringBuilder().append(rtaOfficeCode).append(year).append(series).toString().toUpperCase();
	}
	
	@Override
	public String getIDPDrivingLicenseSeries(String rtaOfficeCode) {
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		DlSeriesMasterEntity dlSeries = dlSeriesMasterDAO.getByYear(year);
		Integer useNumber = dlSeries.getUseNumber();
		String series = NumberParser.getFormatedNumber(useNumber, "xxxxxxx", 'x', '0');
		//dlSeries.setUseNumber(1 + useNumber);
		//dlSeriesMasterDAO.update(dlSeries);

		return new StringBuilder().append(rtaOfficeCode).append(year).append(series).toString().toUpperCase();
	}

}

package org.rta.citizen.licence.utils;

import java.util.Calendar;
import org.rta.citizen.common.entity.ApplicationEntity;

public class ApplicationUtil {

	public static String getApplicationFormat(ApplicationEntity appEntity) {
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		String rtaOffCode = appEntity.getRtaOfficeCode();
		Long applicationId = appEntity.getApplicationId();
		return new StringBuilder(rtaOffCode).append("/").append(applicationId).append("/").append(year).append("/")
				.append('L').toString().toUpperCase();
	}

	public static String getLearnersLicenceFormat(ApplicationEntity appEntity) {
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		String rtaOffCode = appEntity.getRtaOfficeCode();
		Long applicationId = appEntity.getApplicationId();
		return new StringBuilder("LLR").append(rtaOffCode).append(applicationId).append(year).toString().toUpperCase();
	}
}

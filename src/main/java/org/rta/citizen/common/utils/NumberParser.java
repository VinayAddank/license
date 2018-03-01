package org.rta.citizen.common.utils;

import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

@Component
public class NumberParser {

	public static double roundOff(double value, String decimalFormat) {
		DecimalFormat df2 = new DecimalFormat(decimalFormat);
		return Double.valueOf(df2.format(value));
	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(.\\d+)?");
	}

	public static String numberFormat(double value) {
		return String.format("%.2f", value);
	}

	/**
	 * This method will convert the number into the format given in format fld
	 * and replace from fld with To. getFormatedNumber(20,"xxxxx",'x','0') will
	 * return 00020
	 * 
	 * @param number
	 *            : number
	 * @param format
	 *            :
	 * @param from
	 * @param to
	 * @return
	 */
	public static String getFormatedNumber(int number, String format, char from, char to) {
		StringBuilder formattedNo = new StringBuilder();
		int noOfCharsToAppend = format.length() - (number + "").length();
		while (noOfCharsToAppend > 0) {
			formattedNo = formattedNo.append(from);
			noOfCharsToAppend--;
		}
		formattedNo = formattedNo.append(number);
		return formattedNo.toString().replace(from, to);
	}

	public static int getRoundOff(double inputValue) {
		return (int) Math.ceil(inputValue);
	}

	public static double getRoundNextTen(double inputValue) {
		int i = (int) Math.ceil(inputValue);
		int module = i % 10;
		if (module == 0)
			return Math.ceil(inputValue);
		int y = i / 10;
		double nextTenValue = ++y * 10;
		return nextTenValue;
	}
}

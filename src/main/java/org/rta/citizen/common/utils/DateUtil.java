package org.rta.citizen.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * this util class only use Date Related
 * 
 * @Author sohan.maurya created on Jul 4, 2016.
 */
public class DateUtil {

	public final static String DATE_PATTERN = "dd MMM yyyy HH:mm:ss";

	public final static DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

	public static final Long ONE_DAY_SECONDS = 86400L;

	public static final Long SECONDS_OF_30_MINUTES = 1800L;
	
	public static final Long CONSENT_DAYS = 5l;
	/**
	 * this is convert date in long type </br>
	 * 
	 * @param date
	 * @return date in long
	 */
	public static Long getDateInLong(Date date) {

		return date.getTime();
	}

	public static String getDateInString(Date date) {
		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	/**
	 * convert from Current Date to UTC TimeStamp </br>
	 * 
	 * @return TimeStamp
	 */
	public static Long toCurrentUTCTimeStamp() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		return calendar.getTime().getTime() / 1000;
	}

	public static Long toCurrentUTCTimeStampMilliSeconds() {
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		Calendar calendar = Calendar.getInstance(timeZone);
		return calendar.getTime().getTime();
	}

	/**
	 * convert from Date to UTC TimeStamp </br>
	 * 
	 * @param date
	 * @return TimeStamp
	 */
	public static Long toUTCTimeStamp(Date date) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(date.getTime());
		return calendar.getTime().getTime();
	}

	/**
	 * convert from UTC TimeStamp to IST Date </br>
	 * 
	 * @param Long
	 *            utcTimeStamp
	 * @return Date
	 */
	public static Date fromUTCTimeStamp(Long utcTimeStamp) {
		TimeZone timeZone = TimeZone.getTimeZone("IST");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(utcTimeStamp);
		return calendar.getTime();
	}

	public static String extractDateAsString(Long timestamp) {
		Date date = new Date(timestamp * 1000);
		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	public static String extractDateAsStringWithHyphen(Long timestamp) {
		Date date = new Date(timestamp * 1000);
		return new SimpleDateFormat("dd-MM-yyyy").format(date);
	}

	public static String extractDateTimeAsString(Long timestamp) {
		Date date = new Date(timestamp * 1000);
		return new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss").format(date);
	}

	/**
	 * to get date as String format dd-MM-yyyy
	 * 
	 * @param dateTime
	 * @return
	 */
	public static String getDateAsString(Long dateTime) {
		Date date = new Date(dateTime * 1000);
		return new SimpleDateFormat("dd-MM-yyyy").format(date);
	}

	public static String extractTimeAsString(Long timestamp) {
		Date date = new Date(timestamp * 1000);
		return new SimpleDateFormat("hh:mm:ss a").format(date);
	}

	public static Long addYears(Long date, int year) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(date * 1000);
		calendar.add(Calendar.YEAR, year);
		Long addyeardate = calendar.getTime().getTime() / 1000;
		return reduceDays(addyeardate, 1);
	}

	public static Long reduceDays(Long date, int days) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis((date) - (days * 24 * 60 * 60));
		return calendar.getTime().getTime();
	}

	public static Long addDays(Long date, int days) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis((date) + (days * 24 * 60 * 60));
		return calendar.getTime().getTime();
	}

	public static Long dateFormater(String inputeDate) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		try {
			date = df.parse(inputeDate);
		} catch (ParseException e) {
			return null;
		}
		return date.getTime() / 1000;
	}

	public static Long dateSeconds(Long inputeDate) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dateString = df.format(inputeDate);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date;
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
		return date.getTime() / 1000;
	}

	public static Long getDateInLong(String inputDate, String time) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date();
		try {
			date = df.parse(inputDate + " " + time);
		} catch (ParseException e) {
			return null;
		}
		return date.getTime() / 1000;
	}

	/**
	 * To Get Long Date Input with Indian standard format
	 * 
	 * @param inputeDate
	 * @param time
	 * @return
	 */
	public static Long getLongDate(String inputeDate, String time) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date date = new Date();
		try {
			date = df.parse(inputeDate + " " + time);
		} catch (ParseException e) {
			return null;
		}
		return date.getTime() / 1000;
	}

	/**
	 * isSameOrGreaterDate compare date between two dates
	 * 
	 * @param currentDateTime
	 *            The high value
	 * @param oldDateTime
	 *            The low value
	 * @return
	 */

	public static Boolean isSameOrGreaterDate(Long currentDateTime, Long oldDateTime) {
		Date currentDate = Date.from(Instant.ofEpochSecond(currentDateTime));
		Date oldDate = Date.from(Instant.ofEpochSecond(oldDateTime));
		return currentDate.compareTo(oldDate) >= 0 ? true : false;

	}

	public static void main(String[] args) throws Exception {

		System.out.println(getDatefromString(extractDateAsStringWithHyphen(1486944000l)));
		System.out.println(getTimeStampTonight());
	}

	
	public static Long getTimeStampTonight() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date date = new Date();
		try {
			date = df.parse( new SimpleDateFormat("dd/MM/yyyy").format(new Date(toCurrentUTCTimeStamp() * 1000)) + " 18:51:59");
		} catch (ParseException e) {
			return null;
		}
		return date.getTime() / 1000;
	}
	
	// to do sandeep
	public static int cal() {
		int currentMonth = DateUtil.getMonth(DateUtil.toCurrentUTCTimeStamp());
		int expiryDate = DateUtil.getMonth(1483142399l);
		int diff = 0;
		int result = 0;
		if (currentMonth > expiryDate) {
			diff = (currentMonth - expiryDate) - 1;
			result = (diff / 3) * 3;
			System.out.println("cccccc " + result);
		} else {
			int d = 11 - expiryDate;
			diff = (d + currentMonth) + 1;
			result = ((diff - 1) / 3) * 3;
		}
		System.out.println("ddddddddddd " + result);

		return result;
	}

	public static int expiryMonthsCount(Long expiryDate, Long inputeDate) {
		long diff = expiryDate - inputeDate;
		int diffDays = (int) diff / (24 * 60 * 60);
		if (diffDays > 0) {
			int daysCount = diffDays / 30;
			return daysCount + 1;
		} else {
			return 0;
		}
	}

	public static Boolean isPastCalendarDay(Long inputeDate) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(inputeDate * 1000);
		Boolean pastDay = false;
		int dayDiff = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
		if (dayDiff > 0)
			pastDay = true;
		return pastDay;
	}

	public static Boolean isPastCalendarMonth(Long inputeDate) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(inputeDate * 1000);
		Boolean pastMonth = false;
		int monthDiff = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		if (monthDiff > 0)
			pastMonth = true;
		return pastMonth;
	}

	public static int getMonth(Long inputeDate) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(inputeDate * 1000);
		int month = c1.get(Calendar.MONTH);
		return month;
	}

	public static int getYear(Long inputeDate) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(inputeDate * 1000);
		int year = c1.get(Calendar.YEAR);
		System.out.println("Year::::::: " + year);
		return year;
	}

	public static Integer getCurrentAge(String dateOfBirth) throws Exception {
		String dateOfBirthAME = null;
		String[] strIND = dateOfBirth.split("-");
		String[] strAME = dateOfBirth.split("/");
		if (strIND.length == 3) {
			dateOfBirthAME = strIND[2] + "-" + strIND[1] + "-" + strIND[0];
		} else {
			dateOfBirthAME = strAME[2] + "-" + strAME[0] + "-" + strAME[1];
		}
		LocalDate sinceGraduation = LocalDate.parse(dateOfBirthAME);
		LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
		Period betweenDates = Period.between(sinceGraduation, currentDate);
		return betweenDates.getYears();
	}

	public static String getDate(String format, String timeZone, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		return sdf.format(date);
	}

	public static Long getSeconds(Integer year, Integer month, Integer day) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTimeInMillis() / 1000;
	}

	public static String dateOneToAnother(String inputeDate) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = df.parse(inputeDate);
		} catch (ParseException e) {
			return null;
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(date.getTime());
	}

	public static Date getDatefromString(String inputDate) {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		try {
			date = df.parse(inputDate);
		} catch (ParseException e) {
			return null;
		}
		return date;
	}
	
	public static Date addDays(Date date, int dates) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, dates);
		return c.getTime();
	}

	public static Date addMonths(Date date, int months) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, months);
		if (localDate.getDayOfMonth() != 31)
			c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	public static Date delMonth(Date date, int months) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -months);
		if (localDate.getDayOfMonth() != 30)
			c.add(Calendar.DATE, 1);
		return c.getTime();
	}

	public static Date addYears(Date date, int years) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, years);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	public static Date addYearToDate(Date date, int n) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, n);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	public static Date delYearToDate(Date date, int n) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, -n);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	public static Long convertToRunningTimeStamp(Date date) {
		if (ObjectsUtil.isNull(date)) {
			return null;
		}
		Long lDate = date.getTime() / 1000;
		return lDate;
	}

	/**
	 * return diff number of days date2 - date1
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Long getDateDiff(Date date1, Date date2) {
		if (ObjectsUtil.isNull(date1) || ObjectsUtil.isNull(date2)) {
			return null;
		}
		long diff = date2.getTime() - date1.getTime();
		Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return days;
	}

	public static Boolean getValidDate(Date issueDate) {
		long diff = new Date().getTime() - issueDate.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		System.out.print("diffDays:" + diffDays);
		if (diffDays < 365) {
			return true;
		} else
			return false;
	}

	public static Long getDateDiff(Long date1, Long date2) {
		if (ObjectsUtil.isNull(date1) || ObjectsUtil.isNull(date2)) {
			return null;
		}
		long diff = date2 * 1000 - date1 * 1000;
		Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return days;
	}

	public static int monthsCount(Long inputeDate) {
		long diff = DateUtil.toCurrentUTCTimeStamp() - inputeDate;
		int diffDays = (int) diff / (24 * 60 * 60);
		if (diffDays > 0) {
			int daysCount = diffDays / 30;
			return daysCount + 1;
		} else {
			return 0;
		}
	}

	public static int daysCount(Long inputeDate) {
		long diff = DateUtil.toCurrentUTCTimeStamp() - inputeDate;
		int diffDays = (int) diff / (24 * 60 * 60);
		return diffDays;
	}

	public static Integer getCurrentAgeInDays(String dateOfBirth) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		String[] strIND = dateOfBirth.split("-");
		String[] strAME = dateOfBirth.split("/");
		if (strIND.length == 3) {
			calendar.set(Integer.parseInt(strIND[2]), Integer.parseInt(strIND[1]), Integer.parseInt(strIND[0]), 0, 0,
					0);
		} else {
			calendar.set(Integer.parseInt(strAME[2]), Integer.parseInt(strAME[0]), Integer.parseInt(strAME[1]), 0, 0,
					0);
		}

		long diff = toCurrentUTCTimeStamp() * 1000 - calendar.getTime().getTime();
		return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static Long toLastDayOfMonth(int monthType) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.add(Calendar.MONTH, monthType + 1);
		calendar.set(Calendar.DAY_OF_MONTH, monthType + 1);
		calendar.add(Calendar.DATE, -(monthType + 1));
		return calendar.getTime().getTime() / 1000;
	}

	public static Long getNumberOfDays(long startDate, long endDate) {
		long difference = 0;
		if (startDate > endDate) {
			difference = startDate - endDate;
		} else {
			difference = endDate - startDate;
		}
		Long days = TimeUnit.DAYS.convert(difference * 1000, TimeUnit.MILLISECONDS);
		return days;
	}

	/**
	 * This method returns add year and reduces 1 day
	 * 
	 * @param date
	 * @param year
	 * @return
	 */
	public static Long addYearsByMiliSecOnly(Date date, int year) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, year);
		date = calendar.getTime();
		date.setTime(date.getTime() / 1000);
		return reduceDays(date.getTime(), 1);
	}

	/**
	 * @author neeraj.maletia
	 * @description This method will return the difference in year between two
	 *              dates.
	 * @param startDate
	 * @param currentDate
	 * @return
	 */
	public static int getDiffYears(Date startDate, Date currentDate) {
		Calendar a = getCalendar(startDate);
		Calendar b = getCalendar(currentDate);
		int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
		if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
				|| (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		return cal;
	}

	public static int getExpiryMonthsCount(Long expiryDate, Long inputeDate) {
		long diff = inputeDate - expiryDate;
		int diffDays = (int) diff / (24 * 60 * 60);
		if (diffDays > 0) {
			int daysCount = diffDays / 30;
			return daysCount;
		} else {
			return 0;
		}
	}
	
	public static Long convertDayToMilliseconds(){
		return CONSENT_DAYS * 24 * 60 * 60 ;
	}
	
	public static Long addYearsWithNoReduceDay(Long date, int year) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(date * 1000);
		calendar.add(Calendar.YEAR, year);
		return calendar.getTime().getTime() / 1000;
		
	}
	public static boolean compareDatePartOnly(Long date1, Long date2)
	{   boolean isDate1AfterDate2 =false;
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String date1AsString = getDateAsString(date1);
	    String date2AsString = getDateAsString(date2);
	    
	   try {
		if(df.parse(date1AsString).after(df.parse(date2AsString)))
			isDate1AfterDate2 = true;
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 return isDate1AfterDate2;
	}
	
}

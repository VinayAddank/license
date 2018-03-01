package org.rta.citizen.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class has mobile number validation, email Id validation, PAN card validation
 * 
 * @Author sohan.maurya created on Jul 4, 2016.
 */
public class CommonVailidationUtil {

    // PAN card pattern for small and capital latter both [A-Za-z]{5}[0-9]{4}[A-z]{1}
    public static Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    // Email Id pattern
    public static Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");

    // mobile Id pattern for starting digit of mobile no. at 7,8,9
    public static Pattern MOBILE_PATTERN = Pattern.compile("[7-8-9]{1}[0-9]{9}");

    /**
     * Check whether PAN number is valid or not. </br>
     * 
     * @param panNumber
     * @return true if valid else false
     */
    public static boolean isValidPanNumber(String panNumber) {

        if (panNumber == null || panNumber == "") {
            return false;
        }
        Matcher matcher = PAN_PATTERN.matcher(panNumber);
        // Check if pattern matches
        return matcher.matches();
    }

    /**
     * Check whether Email-Id is valid or not. </br>
     * 
     * @param email
     * @return true if valid else false
     */
    public static boolean isValidEmailAddress(String email) {

        if (email == null || email == "") {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        // Check if pattern matches
        return matcher.matches();
    }

    /**
     * Check whether mobile Number is valid or not. </br>
     * 
     * @param mobileNo
     * @return true if valid else false
     */
    public static boolean isValidMobileNumber(String mobileNo) {

        if (mobileNo == null || mobileNo == "") {
            return false;
        }
        Matcher matcher = MOBILE_PATTERN.matcher(mobileNo);
        // Check if pattern matches
        return matcher.matches();
    }


}

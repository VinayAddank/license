/**
 * 
 */
package org.rta.citizen.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@JsonInclude(Include.NON_NULL)
public class ChallanDetailsModel {

    private String date;
    private String time;
    private String violationPlace;
    private String violationDesc;
    private String totalFineAmt;
    private String status;

    public String getDate() {
        return date;
    }

    public void setDate(String dateTime) {
        this.date = dateTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getViolationPlace() {
        return violationPlace;
    }

    public void setViolationPlace(String violationPlace) {
        this.violationPlace = violationPlace;
    }

    public String getViolationDesc() {
        return violationDesc;
    }

    public void setViolationDesc(String violationDesc) {
        this.violationDesc = violationDesc;
    }

    public String getTotalFineAmt() {
        return totalFineAmt;
    }

    public void setTotalFineAmt(String totalFineAmt) {
        this.totalFineAmt = totalFineAmt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

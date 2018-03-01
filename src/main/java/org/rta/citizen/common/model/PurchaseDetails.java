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
public class PurchaseDetails extends BillingDetailsModel {

    private static final long serialVersionUID = -1761286997392706198L;

    private String originalRegAuthority;
    private String dealerName;
    private String cityName;
    private String dealerUserName;
    private Double onRoadAmt;
    private String districtName;

    public String getOriginalRegAuthority() {
        return originalRegAuthority;
    }

    public void setOriginalRegAuthority(String originalRegAuthority) {
        this.originalRegAuthority = originalRegAuthority;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getOnRoadAmt() {
        return onRoadAmt;
    }

    public void setOnRoadAmt(Double onRoadAmt) {
        this.onRoadAmt = onRoadAmt;
    }

    public String getDealerUserName() {
        return dealerUserName;
    }

    public void setDealerUserName(String dealerUserName) {
        this.dealerUserName = dealerUserName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
}

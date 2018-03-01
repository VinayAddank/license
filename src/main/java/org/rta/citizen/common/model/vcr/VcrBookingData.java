package org.rta.citizen.common.model.vcr;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Table")
@XmlAccessorType(XmlAccessType.FIELD)
public class VcrBookingData {

    @XmlElement(name = "VCR_NUM")
    private String vcrNum;

    @XmlElement(name = "Regn_no")
    private String regNo;

    @XmlElement(name = "DLNUMBER")
    private String dlNumber;

    @XmlElement(name = "BOOKEDMVI")
    private String bookedByMvi;

    @XmlElement(name = "BOOKEDDATE")
    private String bookedDate;
    
    @XmlElement(name = "BOOKEDTIME")
    private String bookedTime;

    @XmlElement(name = "PlaceBooked")
    private String placeBooked;

    @XmlElement(name = "VCRSTATUS")
    private String vcrStatus;

    @XmlElement(name = "OTYPE")
    private String idType;

    @XmlElement(name = "VEHSEZD_FLG")
    private String vehsezdFlg;
    
    @XmlElement(name = "PLIEDAS")
    private String pliedas;
    
    private List<VcrOffenseDetails> offenseDetails = new ArrayList<>();
    
    public String getVcrNum() {
        return vcrNum;
    }

    public void setVcrNum(String vcrNum) {
        this.vcrNum = vcrNum;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getDlNumber() {
        return dlNumber;
    }

    public void setDlNumber(String dlNumber) {
        this.dlNumber = dlNumber;
    }

    public String getBookedByMvi() {
        return bookedByMvi;
    }

    public void setBookedByMvi(String bookedByMvi) {
        this.bookedByMvi = bookedByMvi;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(String bookedDate) {
        this.bookedDate = bookedDate;
    }

    public String getBookedTime() {
        return bookedTime;
    }

    public void setBookedTime(String bookedTime) {
        this.bookedTime = bookedTime;
    }

    public String getPlaceBooked() {
        return placeBooked;
    }

    public void setPlaceBooked(String placeBooked) {
        this.placeBooked = placeBooked;
    }

    public String getVcrStatus() {
        return vcrStatus;
    }

    public void setVcrStatus(String vcrStatus) {
        this.vcrStatus = vcrStatus;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }
    
    public List<VcrOffenseDetails> getOffenseDetails() {
        return offenseDetails;
    }

    public void setOffenseDetails(List<VcrOffenseDetails> offenseDetails) {
        this.offenseDetails = offenseDetails;
    }

	public String getVehsezdFlg() {
		return vehsezdFlg;
	}

	public void setVehsezdFlg(String vehsezdFlg) {
		this.vehsezdFlg = vehsezdFlg;
	}

	public String getPliedas() {
		return pliedas;
	}

	public void setPliedas(String pliedas) {
		this.pliedas = pliedas;
	}
}

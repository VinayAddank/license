package org.rta.citizen.common.model.payment;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FitnessFeeModel {


    private String fitnessFee;
    private String fitnessService;
    private String totalFitnessFee;


    public String getFitnessFee() {
        return fitnessFee;
    }

    public void setFitnessFee(String fitnessFee) {
        this.fitnessFee = fitnessFee;
    }

    public String getFitnessService() {
        return fitnessService;
    }

    public void setFitnessService(String fitnessService) {
        this.fitnessService = fitnessService;
    }

    public String getTotalFitnessFee() {
        return totalFitnessFee;
    }

    public void setTotalFitnessFee(String totalFitnessFee) {
        this.totalFitnessFee = totalFitnessFee;
    }
}

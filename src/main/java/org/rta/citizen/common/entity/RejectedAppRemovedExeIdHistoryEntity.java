/**
 * After rejection of application with iteration, this table is used to maintain the history of execution ids of activiti which gets overriden if we reiterate.
 */
package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author arun.verma
 *
 */

@Entity
@Table(name = "rejected_app_removed_exe_id_hist")
public class RejectedAppRemovedExeIdHistoryEntity extends BaseEntity{

    /**
     * 
     */
    private static final long serialVersionUID = -4300936057493500960L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_removed_exe_id_seq")
    @SequenceGenerator(name = "app_removed_exe_id_seq", sequenceName = "app_removed_exe_id_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "app_number")
    private String applicationNumber;
    
    @Column(name = "execution_id")
    private String executionId;
    
    @Column(name = "iteration")
    private Integer iteration;
    
    @Column(name = "service_code")
    private String serviceCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public Integer getIteration() {
        return iteration;
    }

    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

}

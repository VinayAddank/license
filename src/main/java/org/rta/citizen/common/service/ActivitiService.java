/**
 * 
 */
package org.rta.citizen.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.ProcessUser;
import org.rta.citizen.common.model.activiti.RtaProcessInfo;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.activiti.VariableWrapper;
import org.rta.citizen.common.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author arun.verma
 *
 */
@Service
public class ActivitiService {

    //--------- variable used for activiti decisions -----
    public static final String SERVICE_CODE_KEY = "serviceCode";
    public static final String RTA_OFFICE_CODE = "rtaOfficeCode";
    public static final String IS_BODY_BUILDER = "isBodyBuilder";
    public static final String IS_ONLINE_FINANCED = "isOnlineFinanced";
    public static final String IS_PAYMENT = "isPayment";
    public static final String SERVICE_CATEGORY_KEY = "serviceCategoryCode";
    public static final String APP_COMPLETED = "completed";
    public static final String ISCCOSTEP = "isCcoStep";
    public static final String ISAORTOSTEP = "isAoRtoStep";
    public static final String AUTOAPPROVED_ACTIVITI = "autoApproved";
    public static final String ITERATION = "iteration";
    public static final String PAY_DIFF_TAX = "isDTTax";
    
    @Value("${url.citizen.activiti}")
    private String baseURL;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Create Users
     * 
     * @param users
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<Object> createUsers(List<ProcessUser> users) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<ProcessUser>> httpEntity = new HttpEntity<List<ProcessUser>>(users, headers);
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(baseURL + "/users", HttpMethod.PUT, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<Object> res = new ActivitiResponseModel<Object>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody();
        }
        return res;
    }

    /**
     * Start Process with assigning serviceCode
     * 
     * @param assignee
     * @param processId
     * @param serviceCode
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<RtaProcessInfo> startProcess(Assignee assignee, String processId, ServiceType service) {
        Map<String, Object> variables = new HashMap<>();
        VariableWrapper wrapper = new VariableWrapper();
        variables.put(SERVICE_CODE_KEY, service.getCode());
        variables.put(SERVICE_CATEGORY_KEY, ServiceUtil.getServiceCategory(service).getCode());
        wrapper.setVariables(variables);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VariableWrapper> httpEntity = new HttpEntity<VariableWrapper>(wrapper, headers);
        String path = baseURL + "/process/" + processId + "/user/" + assignee.getUserId();
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.PUT, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<RtaProcessInfo> res = new ActivitiResponseModel<RtaProcessInfo>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }

    /**
     * Get Active tasks with a instanceId
     * 
     * @param instanceId
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> getActiveTasks(String instanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        String path = baseURL + "/task/active/" + instanceId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.GET, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }

    /**
     * Get Active instances at a task def
     * 
     * @param taskDef
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> getActiveInstances(String taskDef) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        String path = baseURL + "/instances/task/" + taskDef;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.GET, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }

    /**
     * get tasks assigned or aligned to a user. Variables are used for conditions
     * 
     * @param userId
     * @param variables
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> getTasks(String userId, Map<String, Object> variables) {
        VariableWrapper wrapper = new VariableWrapper();
        wrapper.setVariables(variables);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VariableWrapper> httpEntity = new HttpEntity<VariableWrapper>(wrapper, headers);
        String path = baseURL + "/tasks/" + userId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    
    /**
     * get assigned tasks to a user
     * @param userId
     * @param variables
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> getAssignedTasks(String userId, Map<String, Object> variables) {
        VariableWrapper wrapper = new VariableWrapper();
        wrapper.setVariables(variables);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VariableWrapper> httpEntity = new HttpEntity<VariableWrapper>(wrapper, headers);
        String path = baseURL + "/tasks/user/" + userId + "/assigned";
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    /**
     * get tasks with userId and instanceId
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> getTasks(String userId, String instanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        String path = baseURL + "/tasks/" + userId + "/instance/" + instanceId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.GET, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }

    /**
     * Claim a task
     * 
     * @param assignee
     * @param instanceId
     * @param taskDef
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<Object> claimTask(Assignee assignee, String instanceId, String taskDef) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignee> httpEntity = new HttpEntity<Assignee>(assignee, headers);
        String path = baseURL + "/claim/instance/" + instanceId + "/task/" + taskDef;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<Object> res = new ActivitiResponseModel<Object>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    
    /**
     * Complete a task with claim = true/false
     * 
     * @param assignee
     * @param taskDef
     * @param instanceId
     * @param isClaim
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> completeTask(Assignee assignee, String taskDef, String instanceId, boolean isClaim, Map<String, Object> variables) {
        VariableWrapper wrapper = new VariableWrapper();
        wrapper.setVariables(variables);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VariableWrapper> httpEntity = new HttpEntity<VariableWrapper>(wrapper, headers);
        String path = baseURL + "/complete/instance/" + instanceId + "/task/" + taskDef + "/user/" + assignee.getUserId() + "?claim=" + isClaim;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }

    /**
     * Complete a task with claim=true/false with action approved or rejected
     * 
     * @param assignee
     * @param taskDef
     * @param action
     * @param instanceId
     * @param isClaim
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> completeTaskWithAction(Assignee assignee, String taskDef, String action, String instanceId,
            boolean isClaim) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignee> httpEntity = new HttpEntity<Assignee>(assignee, headers);
        String path = baseURL + "/complete/instance/" + instanceId + "/task/" + taskDef + "/action/" + action + "?claim=" + isClaim;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }

    /**
     * Delete a Instance
     * 
     * @param instanceId
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<RtaProcessInfo> deleteProcessInstance(String instanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        String path = baseURL + "/delete/instance/" + instanceId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.DELETE, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<RtaProcessInfo> res = new ActivitiResponseModel<RtaProcessInfo>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    
    /**
     * Get Next Completed task(by time)
     * 
     * @param taskDefKey
     * @param instanceId
     * @param userId
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<RtaTaskInfo> getNextCompletedTask(String taskDefKey, String instanceId, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        String path = baseURL + "/completedtask/next/task/" + taskDefKey + "/instance/" + instanceId + "/userid/" + userId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.GET, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<RtaTaskInfo> res = new ActivitiResponseModel<RtaTaskInfo>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    
    /**
     * Get process start and end time
     * 
     * @param instanceId
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<RtaTaskInfo> getProcessStartEndDate(String instanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        String path = baseURL + "/processdetails/" + instanceId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.GET, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<RtaTaskInfo> res = new ActivitiResponseModel<RtaTaskInfo>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<RtaProcessInfo> setProcessVariables(String executionId, Map<String, Object> variables) {
        VariableWrapper wrapper = new VariableWrapper();
        wrapper.setVariables(variables);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VariableWrapper> httpEntity = new HttpEntity<VariableWrapper>(wrapper, headers);
        String path = baseURL + "/set/variables/instance/" + executionId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<RtaProcessInfo> res = new ActivitiResponseModel<RtaProcessInfo>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    
    /**
     * Add Process variable
     * 
     * @param executionId
     * @param variables
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<Boolean> setProcessVaribales(String executionId,Map<String, Object> variables) {
        VariableWrapper wrapper = new VariableWrapper();
        wrapper.setVariables(variables);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VariableWrapper> httpEntity = new HttpEntity<VariableWrapper>(wrapper, headers);
        String path = baseURL + "/set/variables/instance/" + executionId;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<Boolean> res = new ActivitiResponseModel<Boolean>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
    
    /**
     * Complete current task and assign next task to a specific user by claiming the task.
     * 
     * @param assignee
     * @param taskDef
     * @param instanceId
     * @param isClaim
     * @param variables
     * @param nextTaskDef
     * @param nextAssignee
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ActivitiResponseModel<List<RtaTaskInfo>> completeTask(Assignee assignee, String taskDef, String instanceId, boolean isClaim, Map<String, Object> variables,
    		String nextTaskDef, Assignee nextAssignee) {
        VariableWrapper wrapper = new VariableWrapper();
        wrapper.setVariables(variables);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VariableWrapper> httpEntity = new HttpEntity<VariableWrapper>(wrapper, headers);
        String path = baseURL + "/complete/instance/" + instanceId + "/task/" + taskDef + "/user/" + assignee.getUserId() 
        + "/next/task/" + nextTaskDef + "/" + nextAssignee.getUserId() + "?claim=" + isClaim;
        ResponseEntity<ActivitiResponseModel> response = restTemplate.exchange(path, HttpMethod.POST, httpEntity, ActivitiResponseModel.class);
        HttpStatus httpStatus = response.getStatusCode();
        ActivitiResponseModel<List<RtaTaskInfo>> res = new ActivitiResponseModel<List<RtaTaskInfo>>();
        if (httpStatus == HttpStatus.OK) {
            res = response.getBody(); 
        }
        return res;
    }
}

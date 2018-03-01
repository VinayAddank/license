/**
 * This Controller access direct api exposed in activiti (used if there is discrepency in data with activiti as activiti server is private).........
 */
package org.rta.citizen.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.activiti.ActivitiResponseModel;
import org.rta.citizen.common.model.activiti.Assignee;
import org.rta.citizen.common.model.activiti.ProcessUser;
import org.rta.citizen.common.model.activiti.RtaProcessInfo;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;
import org.rta.citizen.common.model.activiti.VariableWrapper;
import org.rta.citizen.common.service.ActivitiService;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author arun.verma
 *
 */
@RestController
public class ActivitiApiController {

    @Autowired
    private ActivitiService activitiService;
    
    /**
     * Create users to use activiti
     * 
     * @param users
     * @return
     */
    @RequestMapping(value = "/activiti/create/users", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createUser(@RequestBody List<ProcessUser> users) {
        ActivitiResponseModel<Object> response = activitiService.createUsers(users);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a process instance by process id.
     * e.g. OTSaleProcess, OTAuctionProcess, OTDeathProcess
     * 
     * @param assignee
     * @param processId
     * @param serviceCode
     * @return
     */
    @RequestMapping(value = "/activiti/start/service/{serviceType}/process/{processId}/userName/{userName}", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> startProcess(@PathVariable("serviceType") String serviceType, @PathVariable("processId") String processId, @PathVariable("userName") String userName) {
        Assignee assignee = new Assignee();
        assignee.setUserId(userName);
        ServiceType service = ServiceType.getServiceType(serviceType);
        return ResponseEntity.ok(activitiService.startProcess(assignee, processId, service));
    }

    /**
     * Get all active tasks definition key by instance id
     * 
     * @param instanceId
     * @return
     */
    @RequestMapping(value = "/activiti/task/active/{instanceId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getActiveTasks(@PathVariable("instanceId") String instanceId) {
        ActivitiResponseModel<List<RtaTaskInfo>> tasks = activitiService.getActiveTasks(instanceId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get active instances at a task def node
     * 
     * @param taskDef
     * @return
     */
    @RequestMapping(value = "/activiti/instances/task/{taskDef}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getActiveInstances(@PathVariable("taskDef") String taskDef) {
        return ResponseEntity.ok(activitiService.getActiveInstances(taskDef));
    }

    /**
     *  Get task assigned or aligned to a user with variables as conditions
     * @param variables
     * @param userId
     * @param from
     * @param to
     * @param perPage
     * @return
     */
    @RequestMapping(value = "/activiti/tasks/{userId}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getTasks(@RequestBody VariableWrapper wrapper, @PathVariable("userId") String userId) {
        if(ObjectsUtil.isNull(wrapper)){
            Map<String, Object> variables = new HashMap<>();
            return ResponseEntity.ok(activitiService.getTasks(userId, variables));
        } else {
            return ResponseEntity.ok(activitiService.getTasks(userId, wrapper.getVariables()));
        }
    }
    
    /**
     * get assigned tasks to a user
     * @param userId
     * @param variables
     * @return
     */
    @RequestMapping(value = "/activiti/tasks/assigned/{userId}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAssignedTasks(@RequestBody VariableWrapper wrapper, @PathVariable("userId") String userId) {
        if(ObjectsUtil.isNull(wrapper)){
            Map<String, Object> variables = new HashMap<>();
            return ResponseEntity.ok(activitiService.getAssignedTasks(userId, variables));
        } else {
            return ResponseEntity.ok(activitiService.getAssignedTasks(userId, wrapper.getVariables()));
        }
    }
    
    /**
     * get tasks with userId and instanceId
     */
    @RequestMapping(value = "/activiti/tasks/{userId}/instance/{instanceId}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getTasks(@PathVariable("userId") String userId,@PathVariable("instanceId") String instanceId) {
    	return ResponseEntity.ok(activitiService.getTasks(userId, instanceId));
    }
    
    
    /**
     * Delete a running instance in activiti.
     * 
     * @param instanceId
     * @return
     */
    @RequestMapping(value = "/activiti/delete/instance/{instanceId}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteProcessInstance(@PathVariable("instanceId") String instanceId) {
        activitiService.deleteProcessInstance(instanceId);
        ResponseModel<Object> response = new ResponseModel<>(ResponseModel.SUCCESS, "Instance Deleted Successfully...");
        return ResponseEntity.ok(response);
    }
    
    
    /**
     * Get process details by instance id e.g. start date, end date
     * 
     * @param instanceId
     * @return
     */
    @RequestMapping(value = "/activiti/processdetails/{instanceId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getProcessDetails(@PathVariable("instanceId") String instanceId) {
        ActivitiResponseModel<RtaTaskInfo> task = activitiService.getProcessStartEndDate(instanceId);
        ResponseModel<Object> response = new ResponseModel<>(ResponseModel.SUCCESS, task.getData());
        return ResponseEntity.ok(response);
    }

    /**
     * Claim a task with instance id and task def key
     * 
     * @param userId
     * @param taskDef
     * @param instanceId
     * @return
     */
   @RequestMapping(value = "/activiti/claim/user/{user_id}/instance/{instanceId}/task/{taskDef}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> claimTask(@PathVariable("user_id") String userId, @PathVariable("taskDef") String taskDef,
            @PathVariable("instanceId") String instanceId) {
	   Assignee assignee = new Assignee();
	   assignee.setUserId(userId);
	   ActivitiResponseModel<Object> res = activitiService.claimTask(assignee, instanceId, taskDef);
	   ResponseModel<Object> response = new ResponseModel<>(ResponseModel.SUCCESS, res.getData());
       return ResponseEntity.ok(response);
    }

   /**
    * Complete a task with claim = true/false
    * 
    * @param wrapper
    * @param userId
    * @param taskDef
    * @param instanceId
    * @param isClaim
    * @return
    */
   @RequestMapping(value = "/activiti/complete/task/user/{user_id}/instance/{instanceId}/task/{taskDef}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
   public ResponseEntity<?> completeTask(@RequestBody VariableWrapper wrapper, @PathVariable("user_id") String userId, @PathVariable("taskDef") String taskDef,
           @PathVariable("instanceId") String instanceId, @RequestParam(name = "claim", required = false) boolean isClaim) {
	   Assignee assignee = new Assignee();
	   assignee.setUserId(userId);
	   ActivitiResponseModel<List<RtaTaskInfo>> res = activitiService.completeTask(assignee, taskDef, instanceId, isClaim, wrapper.getVariables());
	   ResponseModel<Object> response = new ResponseModel<>(ResponseModel.SUCCESS, res.getActiveTasks());
      return ResponseEntity.ok(response);
   }
   
   
   /**
    * Complete a task with claim=true/false with action approved or rejected
    * 
    * @param userId
    * @param taskDef
    * @param instanceId
    * @param action
    * @param isClaim
    * @return
    */
   @RequestMapping(value = "/activiti/complete/task/user/{user_id}/instance/{instanceId}/task/{taskDef}/action/{action}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
   public ResponseEntity<?> completeTaskWithAction(@PathVariable("user_id") String userId, @PathVariable("taskDef") String taskDef,
           @PathVariable("instanceId") String instanceId, @PathVariable("action") String action, @RequestParam(name = "claim", required = false) boolean isClaim) {
	   Assignee assignee = new Assignee();
	   assignee.setUserId(userId);
	   ActivitiResponseModel<List<RtaTaskInfo>> res = activitiService.completeTaskWithAction(assignee, taskDef, action, instanceId, isClaim);
	   ResponseModel<Object> response = new ResponseModel<>(ResponseModel.SUCCESS, res.getActiveTasks());
      return ResponseEntity.ok(response);
   }
   
   /**
    * Add Process variable In Process
    * 
    * @param instanceId
    * @param wrapper
    * @return
    */
   @RequestMapping(value = "/activiti/set/variable/instance/{instanceId}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
   public ResponseEntity<?> setVariable(@PathVariable("instanceId") String instanceId, @RequestBody VariableWrapper wrapper) {
	   ActivitiResponseModel<RtaProcessInfo> res = activitiService.setProcessVariables(instanceId, wrapper.getVariables());
	   ResponseModel<Object> response = new ResponseModel<>(ResponseModel.SUCCESS, res.getActiveTasks());
      return ResponseEntity.ok(response);
   }
}
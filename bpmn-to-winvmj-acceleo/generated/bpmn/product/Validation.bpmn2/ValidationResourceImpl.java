// @generated from Validation.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class ValidationResourceImpl extends ValidationResourceComponent {

    public static AccountService accountService;
    static class ProcessInstance {
        String id;
        String state;
        ProcessInstance(String id, String state) {
            this.id = id;
            this.state = state;
        }
    }

    static interface ProcessService {
        boolean upsert(ProcessInstance state); 
        List<ProcessInstance> getAllById(String id);
    }

    static class ProcessServiceImpl implements ProcessService {
        private static final List<ProcessInstance> STORE = new ArrayList<>();

        public boolean upsert(ProcessInstance state) {
            if (STORE.contains(state)) return true;
            return STORE.add(state);
        }

        public List<ProcessInstance> getAllById(String id) {
            return STORE.stream()
                .filter(p -> id.equals(p.id))
                .toList();
        }
    }

    static interface ValidationService {
        void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void validation(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class ValidationServiceImpl implements ValidationService {
	    @Override
	    public void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for User Task 2
			processService.upsert(new ProcessInstance(processid, "userTask2"));
	        System.out.println("Executing User Task 2");
			
	    }

	    @Override
	    public void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for serviceTask2
			processService.upsert(new ProcessInstance(processid, "serviceTask2"));
	        System.out.println("Executing serviceTask2");
			
	    }

	    @Override
	    public void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for serviceTask1
			processService.upsert(new ProcessInstance(processid, "serviceTask1"));
	        System.out.println("Executing serviceTask1");
			
	    }

	    @Override
	    public void validation(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for validation
			processService.upsert(new ProcessInstance(processid, "validation"));
	        System.out.println("Executing validation");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static ValidationService validationService = new ValidationServiceImpl();

    @Route(url = "call/userTask2")
    public Map<String, Object> userTask2(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "null"))
			) {
				response.put("status", "FAIL");
				response.put("message", "userTask2 DENIED");
            	return response;
			}

            response.put("status", "ok");
            response.put("message", "userTask2 SUCCESS");

            validationService.userTask2(requestBody, processid, response);
            boolean balance = false;
            boolean dailyLimit = false;
            int Amount = -1;
            int accumulate = -1;
            boolean Balance = false;
            int amount = -1;
            // From ScriptTask Script Task 5
            processService.upsert(new ProcessInstance(processid, "Script Task 5"));
            // TODO: implement 'script task 5'
            
            if (accumulate <= dailyLimit) {
                processService.upsert(new ProcessInstance(processid, "accumulate <= dailyLimit"));
                if (Amount < Balance) {
                    processService.upsert(new ProcessInstance(processid, "Amount < Balance"));
                    // From ScriptTask add message "uang kurang" to res
                    processService.upsert(new ProcessInstance(processid, "add message uang kurang to res"));
                    response.put("message", "uang kurang");
            
                    // From ScriptTask put status "FAIL" to res
                    processService.upsert(new ProcessInstance(processid, "put status FAIL to res"));
                    response.put("status", "FAIL");
            
                }
            }
		}

        return response;
    }

    @Route(url = "call/validation")
    public Map<String, Object> validation(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "validation"));
		response.put("processid", processid);

		validationService.validation(requestBody, processid, response);
		int Amount = -1;
		boolean Balance = false;
		boolean canContinue = true;
		// From ScriptTask init int balance as (int) accountAccount2Service.getAccount(requestBody).get("balance")
		processService.upsert(new ProcessInstance(processid, "init int balance as (int) accountAccount2Service.getAccount(requestBody).get(balance)"));
		int balance = (int) accountAccount2Service.getAccount(requestBody).get("balance");
		
		// From ScriptTask init int amount as (int) requestBody.get("amount")
		processService.upsert(new ProcessInstance(processid, "init int amount as (int) requestBody.get(amount)"));
		int amount = (int) requestBody.get("amount");
		
		// From ScriptTask init int dailyLimit as (int) accountAccount2Service.getAccount(requestBody).get("dailyLimit")
		processService.upsert(new ProcessInstance(processid, "init int dailyLimit as (int) accountAccount2Service.getAccount(requestBody).get(dailyLimit)"));
		int dailyLimit = (int) accountAccount2Service.getAccount(requestBody).get("dailyLimit");
		
		// From ScriptTask init int withdraw as (int) accountAccount2Service.getAccount(requestBody).get("withdraw")
		processService.upsert(new ProcessInstance(processid, "init int withdraw as (int) accountAccount2Service.getAccount(requestBody).get(withdraw)"));
		int withdraw = (int) accountAccount2Service.getAccount(requestBody).get("withdraw");
		
		if (null) {
		    processService.upsert(new ProcessInstance(processid, ""));
		    // From ScriptTask init int accumulate as withdraw + amount
		    processService.upsert(new ProcessInstance(processid, "init int accumulate as withdraw + amount"));
		    int accumulate = withdraw + amount;
		
		    if (null) {
		        processService.upsert(new ProcessInstance(processid, ""));
		        canContinue = false;
		    }
		    else if (null) {
		        processService.upsert(new ProcessInstance(processid, ""));
		        validationService.serviceTask1(requestBody, processid, response);
		        validationService.serviceTask2(requestBody, processid, response);
		    }
		    if (canContinue) {
		        // From ScriptTask Script Task 5
		        processService.upsert(new ProcessInstance(processid, "Script Task 5"));
		        // TODO: implement 'script task 5'
		
		    }
		}
		if (null) {
		    processService.upsert(new ProcessInstance(processid, ""));
		    // From ScriptTask init int accumulate as withdraw + 1
		    processService.upsert(new ProcessInstance(processid, "init int accumulate as withdraw + 1"));
		    int accumulate = withdraw + 1;
		
		}
		if (canContinue) {
		    if (accumulate <= dailyLimit) {
		        processService.upsert(new ProcessInstance(processid, "accumulate <= dailyLimit"));
		        if (Amount < Balance) {
		            processService.upsert(new ProcessInstance(processid, "Amount < Balance"));
		            // From ScriptTask add message "uang kurang" to res
		            processService.upsert(new ProcessInstance(processid, "add message uang kurang to res"));
		            response.put("message", "uang kurang");
		
		            // From ScriptTask put status "FAIL" to res
		            processService.upsert(new ProcessInstance(processid, "put status FAIL to res"));
		            response.put("status", "FAIL");
		
		        }
		    }
		}
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

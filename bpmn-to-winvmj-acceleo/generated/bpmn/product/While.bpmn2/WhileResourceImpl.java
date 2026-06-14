// @generated from While.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class WhileResourceImpl extends WhileResourceComponent {

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

    static interface WhileService {
        void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void userTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void userTask3(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class WhileServiceImpl implements WhileService {
	    @Override
	    public void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "serviceTask2"));
	        System.out.println("Executing Service Task 2");
			
	    }

	    @Override
	    public void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for userTask2
			processService.upsert(new ProcessInstance(processid, "userTask2"));
	        System.out.println("Executing userTask2");
			
	    }

	    @Override
	    public void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for serviceTask1
			processService.upsert(new ProcessInstance(processid, "serviceTask1"));
	        System.out.println("Executing serviceTask1");
			
	    }

	    @Override
	    public void userTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for userTask1
			processService.upsert(new ProcessInstance(processid, "userTask1"));
	        System.out.println("Executing userTask1");
			
	    }

	    @Override
	    public void userTask3(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for userTask3
			processService.upsert(new ProcessInstance(processid, "userTask3"));
	        System.out.println("Executing userTask3");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static WhileService whileService = new WhileServiceImpl();

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
			
			if (!(hasTaskState(processes, "c") || 
			    hasTaskState(processes, "c"))
			) {
				response.put("status", "FAIL");
				response.put("message", "userTask2 DENIED");
            	return response;
			}

            response.put("status", "ok");
            response.put("message", "userTask2 SUCCESS");

            whileService.userTask2(requestBody, processid, response);
            
		}

        return response;
    }

    @Route(url = "call/userTask1")
    public Map<String, Object> userTask1(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "userTask1"));
		response.put("processid", processid);

		whileService.userTask1(requestBody, processid, response);
		boolean a = false;
		boolean b = false;
		boolean c = false;
		while (a || b) {
		    if (a) {
		        return res;
		    }
		    else if (b) {
		        whileService.serviceTask1(requestBody, processid, response);
		    }
		    if (c) { processService.upsert(new ProcessInstance(processid, "c")); break; }
		}
		

        return response;
    }

    @Route(url = "call/userTask3")
    public Map<String, Object> userTask3(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "a"))
			) {
				response.put("status", "FAIL");
				response.put("message", "userTask3 DENIED");
            	return response;
			}

            response.put("status", "ok");
            response.put("message", "userTask3 SUCCESS");

            whileService.userTask3(requestBody, processid, response);
            boolean a = false;
            boolean b = false;
            boolean c = false;
            whileService.serviceTask2(requestBody, processid, response);
            while (a || b) {
                if (a) {
                    return res;
                }
                else if (b) {
                    whileService.serviceTask1(requestBody, processid, response);
                }
                if (c) { processService.upsert(new ProcessInstance(processid, "c")); break; }
            }
            whileService.userTask2(requestBody, processid, response);
		}

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

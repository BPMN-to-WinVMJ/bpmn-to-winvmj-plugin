// @generated from Switch.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class SwitchResourceImpl extends SwitchResourceComponent {

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

    static interface SwitchService {
        void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class SwitchServiceImpl implements SwitchService {
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


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static SwitchService switchService = new SwitchServiceImpl();

    @Route(url = "call/userTask2")
    public Map<String, Object> userTask2(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "userTask2"));
		response.put("processid", processid);

		switchService.userTask2(requestBody, processid, response);
		boolean a = false;
		boolean b = false;
		if (a) {
		    processService.upsert(new ProcessInstance(processid, "a"));
		    switchService.serviceTask1(requestBody, processid, response);
		}
		else if (b) {
		    processService.upsert(new ProcessInstance(processid, "b"));
		    switchService.serviceTask2(requestBody, processid, response);
		}
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

// @generated from Sequence.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class SequenceResourceImpl extends SequenceResourceComponent {

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

    static interface SequenceService {
        void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void userTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void manualTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class SequenceServiceImpl implements SequenceService {
	    @Override
	    public void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for Service Task 1
			processService.upsert(new ProcessInstance(processid, "serviceTask1"));
	        System.out.println("Executing Service Task 1");
			
	    }

	    @Override
	    public void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for serviceTask2
			processService.upsert(new ProcessInstance(processid, "serviceTask2"));
	        System.out.println("Executing serviceTask2");
			
	    }

	    @Override
	    public void userTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for userTask1
			processService.upsert(new ProcessInstance(processid, "userTask1"));
	        System.out.println("Executing userTask1");
			
	    }

	    @Override
	    public void manualTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for manualTask1
			processService.upsert(new ProcessInstance(processid, "manualTask1"));
	        System.out.println("Executing manualTask1");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static SequenceService sequenceService = new SequenceServiceImpl();

    @Route(url = "call/userTask1")
    public Map<String, Object> userTask1(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "userTask1"));
		response.put("processid", processid);

		sequenceService.userTask1(requestBody, processid, response);
		sequenceService.serviceTask2(requestBody, processid, response);
		sequenceService.serviceTask1(requestBody, processid, response);
		sequenceService.manualTask1(requestBody, processid, response);
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

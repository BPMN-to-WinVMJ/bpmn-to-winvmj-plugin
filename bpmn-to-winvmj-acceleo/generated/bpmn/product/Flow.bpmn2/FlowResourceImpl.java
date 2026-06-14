// @generated from Flow.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class FlowResourceImpl extends FlowResourceComponent {

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

    static interface FlowService {
        void serviceTask3(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask4(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class FlowServiceImpl implements FlowService {
	    @Override
	    public void serviceTask3(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for Service Task 3
			processService.upsert(new ProcessInstance(processid, "serviceTask3"));
	        System.out.println("Executing Service Task 3");
			
	    }

	    @Override
	    public void serviceTask4(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for serviceTask4
			processService.upsert(new ProcessInstance(processid, "serviceTask4"));
	        System.out.println("Executing serviceTask4");
			
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
	    public void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for userTask2
			processService.upsert(new ProcessInstance(processid, "userTask2"));
	        System.out.println("Executing userTask2");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static FlowService flowService = new FlowServiceImpl();

    @Route(url = "call/userTask2")
    public Map<String, Object> userTask2(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "userTask2"));
		response.put("processid", processid);

		flowService.userTask2(requestBody, processid, response);
		CompletableFuture<Void> all = null;
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		List<ProcessInstance> processes = new ArrayList<>();
		CompletableFuture<Void> flowSequenceFlow_2 = CompletableFuture.runAsync(() -> {
		    flowService.serviceTask3(requestBody, processid, response);
		    flowService.serviceTask4(requestBody, processid, response);
		});
		CompletableFuture<Void> flowSequenceFlow_5 = CompletableFuture.runAsync(() -> {
		    flowService.serviceTask1(requestBody, processid, response);
		    flowService.serviceTask2(requestBody, processid, response);
		});
		futures = List.of(flowSequenceFlow_2, flowSequenceFlow_5);
		all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		all.join();
		processes = processService.getAllById(processid);
		if (!(hasTaskState(processes, "serviceTask2") && 
		    hasTaskState(processes, "serviceTask4"))) {
		    response.put("status", "FAIL");
		    response.put("message", "Parallel branches not complete yet");
		    return response;
		}
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

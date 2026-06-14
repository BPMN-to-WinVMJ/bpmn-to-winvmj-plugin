// @generated from WhileMany.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class WhileManyResourceImpl extends WhileManyResourceComponent {

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

    static interface WhileManyService {
        void task2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void task1(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void task3(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void task4(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class WhileManyServiceImpl implements WhileManyService {
	    @Override
	    public void task2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for Task 2
			processService.upsert(new ProcessInstance(processid, "task2"));
	        System.out.println("Executing Task 2");
			
	    }

	    @Override
	    public void task1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for task1
			processService.upsert(new ProcessInstance(processid, "task1"));
	        System.out.println("Executing task1");
			
	    }

	    @Override
	    public void task3(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for task3
			processService.upsert(new ProcessInstance(processid, "task3"));
	        System.out.println("Executing task3");
			
	    }

	    @Override
	    public void task4(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for task4
			processService.upsert(new ProcessInstance(processid, "task4"));
	        System.out.println("Executing task4");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static WhileManyService whilemanyService = new WhileManyServiceImpl();

    @Route(url = "call/task2")
    public Map<String, Object> task2(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "task2"));
		response.put("processid", processid);

		whilemanyService.task2(requestBody, processid, response);
		while (null || null || null) {
		    if (null) {
		        return res;
		    }
		    else if (null) {
		        return res;
		    }
		    else if (null) {
		        return res;
		    }
		}
		processService.upsert(new ProcessInstance(processid, "tc2"));
		

        return response;
    }

    @Route(url = "call/task3")
    public Map<String, Object> task3(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "task3"));
		response.put("processid", processid);

		whilemanyService.task3(requestBody, processid, response);
		
		

        return response;
    }

    @Route(url = "call/task1")
    public Map<String, Object> task1(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "task1"));
		response.put("processid", processid);

		whilemanyService.task1(requestBody, processid, response);
		while (null || null || null) {
		    if (null) {
		        return res;
		    }
		    else if (null) {
		        return res;
		    }
		    else if (null) {
		        return res;
		    }
		}
		processService.upsert(new ProcessInstance(processid, "tc2"));
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

// @generated from RepeatMany.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class RepeatManyResourceImpl extends RepeatManyResourceComponent {

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

    static interface RepeatManyService {
        void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class RepeatManyServiceImpl implements RepeatManyService {
	    @Override
	    public void userTask2(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for User Task 2
			processService.upsert(new ProcessInstance(processid, "userTask2"));
	        System.out.println("Executing User Task 2");
			
	    }

	    @Override
	    public void serviceTask1(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for serviceTask1
			processService.upsert(new ProcessInstance(processid, "serviceTask1"));
	        System.out.println("Executing serviceTask1");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static RepeatManyService repeatmanyService = new RepeatManyServiceImpl();

    @Route(url = "call/userTask2")
    public Map<String, Object> userTask2(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "userTask2"));
		response.put("processid", processid);

		repeatmanyService.userTask2(requestBody, processid, response);
		boolean a = false;
		boolean b = false;
		boolean c = false;
		do {
		    repeatmanyService.serviceTask1(requestBody, processid, response);
		    if (c) { processService.upsert(new ProcessInstance(processid, "c")); break; }
		} while (b || null || a);
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

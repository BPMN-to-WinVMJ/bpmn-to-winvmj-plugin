// @generated from Flow.bpmn2

package bpmn.product.Flow;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class FlowResourceImpl extends FlowComponent {

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
        void UserTask1(Map<String, Object> body, String processid);
        void ServiceTask1(Map<String, Object> body, String processid);

    }

    static class FlowServiceImpl implements FlowService {
	    @Override
	    public void UserTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 1
			processService.upsert(new ProcessInstance(processid, "UserTask1"));
	        System.out.println("Executing User Task 1");
	    }

	    @Override
	    public void ServiceTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 1
			processService.upsert(new ProcessInstance(processid, "ServiceTask1"));
	        System.out.println("Executing Service Task 1");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private FlowService flowService = new FlowServiceImpl();

    @Route(url = "call/UserTask1")
    public Map<String, Object> UserTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		flowService.UserTask1(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "User Task 1 SUCCESS");
        return res;
    }

    @Route(url = "call/ServiceTask1")
    public Map<String, Object> ServiceTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		flowService.ServiceTask1(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "Service Task 1 SUCCESS");
        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

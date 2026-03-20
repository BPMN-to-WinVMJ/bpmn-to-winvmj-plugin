// @generated

package bpmn.product.Pick;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class PickResourceImpl extends PickComponent {

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

    static interface PickService {
        void Task3(Map<String, Object> body, String processid);
        void Task4(Map<String, Object> body, String processid);
        void Task2(Map<String, Object> body, String processid);

    }

    static class PickServiceImpl implements PickService {
	    @Override
	    public void Task3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Task 3
			processService.upsert(new ProcessInstance(processid, "Task3"));
	        System.out.println("Executing Task 3");
	    }

	    @Override
	    public void Task4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Task 4
			processService.upsert(new ProcessInstance(processid, "Task4"));
	        System.out.println("Executing Task 4");
	    }

	    @Override
	    public void Task2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Task 2
			processService.upsert(new ProcessInstance(processid, "Task2"));
	        System.out.println("Executing Task 2");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private PickService PickService = new PickServiceImpl();

    @Route(url = "call/Task2")
    public Map<String, Object> Task2(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		processService.upsert(new ProcessInstance(processid, "Task2"));
		PickService.Task2(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "Task 2 SUCCESS");
        return res;
    }

    @Route(url = "call/Task3")
    public Map<String, Object> Task3(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		processService.upsert(new ProcessInstance(processid, "Task3"));
		PickService.Task3(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "Task 3 SUCCESS");
        return res;
    }


}

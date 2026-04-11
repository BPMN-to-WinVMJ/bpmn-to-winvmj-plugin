// @generated from While.bpmn2

package bpmn.product.While;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class WhileResourceImpl extends WhileComponent {

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
        void serviceTask2(Map<String, Object> body, String processid);
        void scriptTask1(Map<String, Object> body, String processid);
        void scriptTask2(Map<String, Object> body, String processid);
        void userTask3(Map<String, Object> body, String processid);
        void userTask1(Map<String, Object> body, String processid);

    }

    static class WhileServiceImpl implements WhileService {
	    @Override
	    public void serviceTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "serviceTask2"));
	        System.out.println("Executing Service Task 2");
	    }

	    @Override
	    public void scriptTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 1
			processService.upsert(new ProcessInstance(processid, "scriptTask1"));
	        System.out.println("Executing Script Task 1");
	    }

	    @Override
	    public void scriptTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 2
			processService.upsert(new ProcessInstance(processid, "scriptTask2"));
	        System.out.println("Executing Script Task 2");
	    }

	    @Override
	    public void userTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 3
			processService.upsert(new ProcessInstance(processid, "userTask3"));
	        System.out.println("Executing User Task 3");
	    }

	    @Override
	    public void userTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 1
			processService.upsert(new ProcessInstance(processid, "userTask1"));
	        System.out.println("Executing User Task 1");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private WhileService whileService = new WhileServiceImpl();

    @Route(url = "call/userTask3")
    public Map<String, Object> userTask3(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState("userTask1")))
			) {
				res.put("status", "fail");
				res.put("message", "userTask3 DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "userTask3 SUCCESS");

		whileService.userTask3(requestBody, processid);
		boolean a = true;
		boolean b = true;
		boolean c = true;
		whileService.serviceTask2(requestBody, processid);
		while (a || b) {
		    if (a) {
		        return res;
		    }
		    else if (b) {
		        whileService.scriptTask1(requestBody, processid);
		    }
		    if (c) { processService.upsert(new ProcessInstance(processid, "c")); break; }
		}
		whileService.scriptTask2(requestBody, processid);

        return res;
    }

    @Route(url = "call/userTask1")
    public Map<String, Object> userTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

        res.put("status", "ok");
        res.put("message", "userTask1 SUCCESS");

		whileService.userTask1(requestBody, processid);
		boolean a = true;
		boolean b = true;
		boolean c = true;
		while (a || b) {
		    if (a) {
		        return res;
		    }
		    else if (b) {
		        whileService.scriptTask1(requestBody, processid);
		    }
		    if (c) { processService.upsert(new ProcessInstance(processid, "c")); break; }
		}
		whileService.scriptTask2(requestBody, processid);

        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

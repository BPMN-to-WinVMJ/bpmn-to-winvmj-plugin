// @generated from Whilerepeat.bpmn2

package bpmn.product.Whilerepeat;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class WhilerepeatResourceImpl extends WhilerepeatComponent {

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

    static interface WhilerepeatService {
        void scriptTask3(Map<String, Object> body, String processid);
        void scriptTask4(Map<String, Object> body, String processid);
        void task2(Map<String, Object> body, String processid);
        void task1(Map<String, Object> body, String processid);
        void serviceTask3(Map<String, Object> body, String processid);
        void serviceTask4(Map<String, Object> body, String processid);
        void scriptTask5(Map<String, Object> body, String processid);
        void serviceTask2(Map<String, Object> body, String processid);
        void userTask1(Map<String, Object> body, String processid);
        void userTask3(Map<String, Object> body, String processid);
        void scriptTask6(Map<String, Object> body, String processid);
        void scriptTask2(Map<String, Object> body, String processid);

    }

    static class WhilerepeatServiceImpl implements WhilerepeatService {
	    @Override
	    public void scriptTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 3
			processService.upsert(new ProcessInstance(processid, "scriptTask3"));
	        System.out.println("Executing Script Task 3");
	    }

	    @Override
	    public void scriptTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 4
			processService.upsert(new ProcessInstance(processid, "scriptTask4"));
	        System.out.println("Executing Script Task 4");
	    }

	    @Override
	    public void task2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for task 2
			processService.upsert(new ProcessInstance(processid, "task2"));
	        System.out.println("Executing task 2");
	    }

	    @Override
	    public void task1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for task 1
			processService.upsert(new ProcessInstance(processid, "task1"));
	        System.out.println("Executing task 1");
	    }

	    @Override
	    public void serviceTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 3
			processService.upsert(new ProcessInstance(processid, "serviceTask3"));
	        System.out.println("Executing Service Task 3");
	    }

	    @Override
	    public void serviceTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 4
			processService.upsert(new ProcessInstance(processid, "serviceTask4"));
	        System.out.println("Executing Service Task 4");
	    }

	    @Override
	    public void scriptTask5(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 5
			processService.upsert(new ProcessInstance(processid, "scriptTask5"));
	        System.out.println("Executing Script Task 5");
	    }

	    @Override
	    public void serviceTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "serviceTask2"));
	        System.out.println("Executing Service Task 2");
	    }

	    @Override
	    public void userTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 1
			processService.upsert(new ProcessInstance(processid, "userTask1"));
	        System.out.println("Executing User Task 1");
	    }

	    @Override
	    public void userTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 3
			processService.upsert(new ProcessInstance(processid, "userTask3"));
	        System.out.println("Executing User Task 3");
	    }

	    @Override
	    public void scriptTask6(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 6
			processService.upsert(new ProcessInstance(processid, "scriptTask6"));
	        System.out.println("Executing Script Task 6");
	    }

	    @Override
	    public void scriptTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 2
			processService.upsert(new ProcessInstance(processid, "scriptTask2"));
	        System.out.println("Executing Script Task 2");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private WhilerepeatService whilerepeatService = new WhilerepeatServiceImpl();

    @Route(url = "call/userTask1")
    public Map<String, Object> userTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

        res.put("status", "ok");
        res.put("message", "userTask1 SUCCESS");

		whilerepeatService.userTask1(requestBody, processid);
		boolean a = true;
		boolean b = true;
		boolean c = true;
		boolean d = true;
		boolean e = true;
		boolean var = true;
		boolean f = true;
		whilerepeatService.task1(requestBody, processid);
		while (a || b) {
		    if (a) {
		        whilerepeatService.task2(requestBody, processid);
		    }
		    else if (b) {
		        whilerepeatService.scriptTask2(requestBody, processid);
		    }
		    whilerepeatService.task1(requestBody, processid);
		    if (var) { processService.upsert(new ProcessInstance(processid, "var")); break; }
		}
		
		whilerepeatService.serviceTask2(requestBody, processid);
		whilerepeatService.serviceTask4(requestBody, processid);
		if (c) {
		    whilerepeatService.scriptTask3(requestBody, processid);
		}
		else if (d) {
		    whilerepeatService.scriptTask4(requestBody, processid);
		}
		else if (e) {
		    return res;
		}
		whilerepeatService.scriptTask5(requestBody, processid);
		while (f) {
		    if (f) {
		        whilerepeatService.scriptTask6(requestBody, processid);
		        whilerepeatService.serviceTask3(requestBody, processid);
		    }
		    whilerepeatService.serviceTask4(requestBody, processid);
		    if (c) {
		        whilerepeatService.scriptTask3(requestBody, processid);
		    }
		    else if (d) {
		        whilerepeatService.scriptTask4(requestBody, processid);
		    }
		    else if (e) {
		        return res;
		    }
		    whilerepeatService.scriptTask5(requestBody, processid);
		    if (var) { processService.upsert(new ProcessInstance(processid, "var")); break; }
		}

        return res;
    }

    @Route(url = "call/userTask3")
    public Map<String, Object> userTask3(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState("serviceTask4")))
			) {
				res.put("status", "fail");
				res.put("message", "userTask3 DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "userTask3 SUCCESS");

		whilerepeatService.userTask3(requestBody, processid);
		boolean c = true;
		boolean d = true;
		boolean e = true;
		boolean f = true;
		boolean var = true;
		whilerepeatService.scriptTask5(requestBody, processid);
		if (f) {
		    whilerepeatService.scriptTask6(requestBody, processid);
		    whilerepeatService.serviceTask3(requestBody, processid);
		    whilerepeatService.serviceTask4(requestBody, processid);
		    if (c) {
		        whilerepeatService.scriptTask3(requestBody, processid);
		    }
		    else if (d) {
		        whilerepeatService.scriptTask4(requestBody, processid);
		    }
		    else if (e) {
		        return res;
		    }
		    whilerepeatService.scriptTask5(requestBody, processid);
		    while (f) {
		        if (f) {
		            whilerepeatService.scriptTask6(requestBody, processid);
		            whilerepeatService.serviceTask3(requestBody, processid);
		        }
		        whilerepeatService.serviceTask4(requestBody, processid);
		        if (c) {
		            whilerepeatService.scriptTask3(requestBody, processid);
		        }
		        else if (d) {
		            whilerepeatService.scriptTask4(requestBody, processid);
		        }
		        else if (e) {
		            return res;
		        }
		        whilerepeatService.scriptTask5(requestBody, processid);
		        if (var) { processService.upsert(new ProcessInstance(processid, "var")); break; }
		}
		
		}

        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

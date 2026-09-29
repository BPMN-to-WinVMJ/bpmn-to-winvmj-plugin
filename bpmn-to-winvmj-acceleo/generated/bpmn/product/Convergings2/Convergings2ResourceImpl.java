// @generated from Convergings2.bpmn2

package bpmn.product.Convergings2;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class Convergings2ResourceImpl extends Convergings2Component {

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

    static interface Convergings2Service {
        void serviceTask2(Map<String, Object> body, String processid);
        void serviceTask6(Map<String, Object> body, String processid);
        void scriptTask3(Map<String, Object> body, String processid);
        void serviceTask9(Map<String, Object> body, String processid);
        void usertask2(Map<String, Object> body, String processid);
        void scriptTask2(Map<String, Object> body, String processid);
        void userTask1(Map<String, Object> body, String processid);
        void serviceTask1(Map<String, Object> body, String processid);
        void serviceTask8(Map<String, Object> body, String processid);
        void serviceTask4(Map<String, Object> body, String processid);
        void serviceTask7(Map<String, Object> body, String processid);
        void scriptTask4(Map<String, Object> body, String processid);
        void serviceTask3(Map<String, Object> body, String processid);
        void serviceTask5(Map<String, Object> body, String processid);

    }

    static class Convergings2ServiceImpl implements Convergings2Service {
	    @Override
	    public void serviceTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "serviceTask2"));
	        System.out.println("Executing Service Task 2");
	    }

	    @Override
	    public void serviceTask6(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 6
			processService.upsert(new ProcessInstance(processid, "serviceTask6"));
	        System.out.println("Executing Service Task 6");
	    }

	    @Override
	    public void scriptTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 3
			processService.upsert(new ProcessInstance(processid, "scriptTask3"));
	        System.out.println("Executing Script Task 3");
	    }

	    @Override
	    public void serviceTask9(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 9
			processService.upsert(new ProcessInstance(processid, "serviceTask9"));
	        System.out.println("Executing Service Task 9");
	    }

	    @Override
	    public void usertask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for UserTask 2
			processService.upsert(new ProcessInstance(processid, "usertask2"));
	        System.out.println("Executing UserTask 2");
	    }

	    @Override
	    public void scriptTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 2
			processService.upsert(new ProcessInstance(processid, "scriptTask2"));
	        System.out.println("Executing Script Task 2");
	    }

	    @Override
	    public void userTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 1
			processService.upsert(new ProcessInstance(processid, "userTask1"));
	        System.out.println("Executing User Task 1");
	    }

	    @Override
	    public void serviceTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 1
			processService.upsert(new ProcessInstance(processid, "serviceTask1"));
	        System.out.println("Executing Service Task 1");
	    }

	    @Override
	    public void serviceTask8(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 8
			processService.upsert(new ProcessInstance(processid, "serviceTask8"));
	        System.out.println("Executing Service Task 8");
	    }

	    @Override
	    public void serviceTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 4
			processService.upsert(new ProcessInstance(processid, "serviceTask4"));
	        System.out.println("Executing Service Task 4");
	    }

	    @Override
	    public void serviceTask7(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 7
			processService.upsert(new ProcessInstance(processid, "serviceTask7"));
	        System.out.println("Executing Service Task 7");
	    }

	    @Override
	    public void scriptTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 4
			processService.upsert(new ProcessInstance(processid, "scriptTask4"));
	        System.out.println("Executing Script Task 4");
	    }

	    @Override
	    public void serviceTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 3
			processService.upsert(new ProcessInstance(processid, "serviceTask3"));
	        System.out.println("Executing Service Task 3");
	    }

	    @Override
	    public void serviceTask5(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 5
			processService.upsert(new ProcessInstance(processid, "serviceTask5"));
	        System.out.println("Executing Service Task 5");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private Convergings2Service convergings2Service = new Convergings2ServiceImpl();

    @Route(url = "call/usertask2")
    public Map<String, Object> usertask2(VMJExchange exchange) {
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
				res.put("message", "usertask2 DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "usertask2 SUCCESS");

		convergings2Service.usertask2(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/userTask1")
    public Map<String, Object> userTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(((hasTaskState("serviceTask2") && hasTaskState("scriptTask2")) || (hasTaskState("serviceTask1") && hasTaskState("usertask2"))) && 
			    ((hasTaskState("serviceTask6") || hasTaskState("serviceTask5")) || hasTaskState("scriptTask3"))))
			) {
				res.put("status", "fail");
				res.put("message", "userTask1 DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "userTask1 SUCCESS");

		convergings2Service.userTask1(requestBody, processid);
		convergings2Service.serviceTask7(requestBody, processid);
		convergings2Service.serviceTask8(requestBody, processid);

        return res;
    }

    @Route(url = "call/serviceTask4")
    public Map<String, Object> serviceTask4(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

        res.put("status", "ok");
        res.put("message", "serviceTask4 SUCCESS");

		convergings2Service.serviceTask4(requestBody, processid);
		boolean c = true;
		boolean d = true;
		boolean e = true;
		boolean f = true;
		boolean var = true;
		boolean h = true;
		boolean k = true;
		boolean o = true;
		if (e) {
		    convergings2Service.serviceTask3(requestBody, processid);
		    convergings2Service.scriptTask3(requestBody, processid);
		    List<ProcessInstance> processes = processService.getAllById(processid);
		    if (!(hasTaskState("scriptTask3"))) {
		        res.put("status", "fail");
		        res.put("message", "Parallel branches not complete");
		        return res;
		    }
		}
		else if (f) {
		    if (h) {
		        if (o) {
		            convergings2Service.serviceTask5(requestBody, processid);
		        }
		    }
		    else if (k) {
		        if (var) {
		            convergings2Service.serviceTask9(requestBody, processid);
		            convergings2Service.scriptTask4(requestBody, processid);
		            List<ProcessInstance> processes = processService.getAllById(processid);
		            if (!(hasTaskState("serviceTask9") && 
		    hasTaskState("scriptTask4"))) {
		                res.put("status", "fail");
		                res.put("message", "Parallel branches not complete");
		                return res;
		            }
		            convergings2Service.serviceTask6(requestBody, processid);
		        }
		    }
		}
		if (c) {
		    convergings2Service.serviceTask1(requestBody, processid);
		    List<ProcessInstance> processes = processService.getAllById(processid);
		    if (!(hasTaskState("serviceTask1") && 
		    hasTaskState("usertask2"))) {
		        res.put("status", "fail");
		        res.put("message", "Parallel branches not complete");
		        return res;
		    }
		}
		else if (d) {
		    convergings2Service.serviceTask2(requestBody, processid);
		    convergings2Service.scriptTask2(requestBody, processid);
		    List<ProcessInstance> processes = processService.getAllById(processid);
		    if (!(hasTaskState("serviceTask2") && 
		    hasTaskState("scriptTask2"))) {
		        res.put("status", "fail");
		        res.put("message", "Parallel branches not complete");
		        return res;
		    }
		}
		List<ProcessInstance> processes = processService.getAllById(processid);
		if (!(((hasTaskState("serviceTask2") && hasTaskState("scriptTask2")) || (hasTaskState("serviceTask1") && hasTaskState("usertask2"))) && 
		    ((hasTaskState("serviceTask6") || hasTaskState("serviceTask5")) || hasTaskState("scriptTask3")))) {
		    res.put("status", "fail");
		    res.put("message", "Parallel branches not complete");
		    return res;
		}
		

        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

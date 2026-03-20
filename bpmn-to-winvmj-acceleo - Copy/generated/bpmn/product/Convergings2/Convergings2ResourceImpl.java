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
        void ServiceTask2(Map<String, Object> body, String processid);
        void ServiceTask6(Map<String, Object> body, String processid);
        void ScriptTask3(Map<String, Object> body, String processid);
        void ServiceTask9(Map<String, Object> body, String processid);
        void UserTask2(Map<String, Object> body, String processid);
        void ScriptTask2(Map<String, Object> body, String processid);
        void UserTask1(Map<String, Object> body, String processid);
        void ServiceTask1(Map<String, Object> body, String processid);
        void ServiceTask8(Map<String, Object> body, String processid);
        void ServiceTask4(Map<String, Object> body, String processid);
        void ServiceTask7(Map<String, Object> body, String processid);
        void ScriptTask4(Map<String, Object> body, String processid);
        void ServiceTask3(Map<String, Object> body, String processid);
        void ServiceTask5(Map<String, Object> body, String processid);

    }

    static class Convergings2ServiceImpl implements Convergings2Service {
	    @Override
	    public void ServiceTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "ServiceTask2"));
	        System.out.println("Executing Service Task 2");
	    }

	    @Override
	    public void ServiceTask6(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 6
			processService.upsert(new ProcessInstance(processid, "ServiceTask6"));
	        System.out.println("Executing Service Task 6");
	    }

	    @Override
	    public void ScriptTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 3
			processService.upsert(new ProcessInstance(processid, "ScriptTask3"));
	        System.out.println("Executing Script Task 3");
	    }

	    @Override
	    public void ServiceTask9(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 9
			processService.upsert(new ProcessInstance(processid, "ServiceTask9"));
	        System.out.println("Executing Service Task 9");
	    }

	    @Override
	    public void UserTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for UserTask 2
			processService.upsert(new ProcessInstance(processid, "UserTask2"));
	        System.out.println("Executing UserTask 2");
	    }

	    @Override
	    public void ScriptTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 2
			processService.upsert(new ProcessInstance(processid, "ScriptTask2"));
	        System.out.println("Executing Script Task 2");
	    }

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

	    @Override
	    public void ServiceTask8(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 8
			processService.upsert(new ProcessInstance(processid, "ServiceTask8"));
	        System.out.println("Executing Service Task 8");
	    }

	    @Override
	    public void ServiceTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 4
			processService.upsert(new ProcessInstance(processid, "ServiceTask4"));
	        System.out.println("Executing Service Task 4");
	    }

	    @Override
	    public void ServiceTask7(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 7
			processService.upsert(new ProcessInstance(processid, "ServiceTask7"));
	        System.out.println("Executing Service Task 7");
	    }

	    @Override
	    public void ScriptTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 4
			processService.upsert(new ProcessInstance(processid, "ScriptTask4"));
	        System.out.println("Executing Script Task 4");
	    }

	    @Override
	    public void ServiceTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 3
			processService.upsert(new ProcessInstance(processid, "ServiceTask3"));
	        System.out.println("Executing Service Task 3");
	    }

	    @Override
	    public void ServiceTask5(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 5
			processService.upsert(new ProcessInstance(processid, "ServiceTask5"));
	        System.out.println("Executing Service Task 5");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private Convergings2Service convergings2Service = new Convergings2ServiceImpl();

    @Route(url = "call/UserTask2")
    public Map<String, Object> UserTask2(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("ServiceTask4"))
			) {
				res.put("message", "UserTask 2 DENIED");
            	return res;
			}
		}

		convergings2Service.UserTask2(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "UserTask 2 SUCCESS");
        return res;
    }

    @Route(url = "call/UserTask1")
    public Map<String, Object> UserTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (((hasTaskState("ServiceTask2") && hasTaskState("ScriptTask2")) || (hasTaskState("ServiceTask1") && hasTaskState("UserTask2")))&&
			((hasTaskState("ServiceTask6") || hasTaskState("ServiceTask5")) || hasTaskState("ScriptTask3")))
			) {
				res.put("message", "User Task 1 DENIED");
            	return res;
			}
		}

		convergings2Service.UserTask1(requestBody, processid);
		convergings2Service.ServiceTask7(requestBody, processid);
		convergings2Service.ServiceTask8(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "User Task 1 SUCCESS");
        return res;
    }

    @Route(url = "call/ServiceTask4")
    public Map<String, Object> ServiceTask4(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		convergings2Service.ServiceTask4(requestBody, processid);
		boolean null = true;
		boolean c = true;
		boolean d = true;
		boolean e = true;
		boolean f = true;
		boolean h = true;
		boolean k = true;
		boolean o = true;
		if (e) {
		  convergings2Service.ServiceTask3(requestBody, processId);
		  convergings2Service.ScriptTask3(requestBody, processId);
		} else if (f) {
		  if (h) {
		    if (o) {
		      convergings2Service.ServiceTask5(requestBody, processid);
		    }
		  } else if (k) {
		    if (null) {
		      convergings2Service.ServiceTask9(requestBody, processid);
		      convergings2Service.ScriptTask4(requestBody, processid);
		    }
		  }
		}
		if (c) {
		  convergings2Service.ServiceTask1(requestBody, processid);
		  	return res;
		} else if (d) {
		  convergings2Service.ServiceTask2(requestBody, processid);
		  convergings2Service.ScriptTask2(requestBody, processid);
		}
		

        res.put("status", "ok");
        res.put("message", "Service Task 4 SUCCESS");
        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

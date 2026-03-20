// @generated from Convergings.bpmn2

package bpmn.product.Convergings;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class ConvergingsResourceImpl extends ConvergingsComponent {

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

    static interface ConvergingsService {
        void ScriptTask2(Map<String, Object> body, String processid);
        void ServiceTask5(Map<String, Object> body, String processid);
        void ServiceTask8(Map<String, Object> body, String processid);
        void ServiceTask9(Map<String, Object> body, String processid);
        void ServiceTask7(Map<String, Object> body, String processid);
        void UserTask1(Map<String, Object> body, String processid);
        void ServiceTask1(Map<String, Object> body, String processid);
        void ServiceTask3(Map<String, Object> body, String processid);
        void ServiceTask6(Map<String, Object> body, String processid);
        void ServiceTask2(Map<String, Object> body, String processid);
        void ScriptTask4(Map<String, Object> body, String processid);
        void ServiceTask4(Map<String, Object> body, String processid);
        void UserTask2(Map<String, Object> body, String processid);
        void ScriptTask3(Map<String, Object> body, String processid);

    }

    static class ConvergingsServiceImpl implements ConvergingsService {
	    @Override
	    public void ScriptTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 2
			processService.upsert(new ProcessInstance(processid, "ScriptTask2"));
	        System.out.println("Executing Script Task 2");
	    }

	    @Override
	    public void ServiceTask5(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 5
			processService.upsert(new ProcessInstance(processid, "ServiceTask5"));
	        System.out.println("Executing Service Task 5");
	    }

	    @Override
	    public void ServiceTask8(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 8
			processService.upsert(new ProcessInstance(processid, "ServiceTask8"));
	        System.out.println("Executing Service Task 8");
	    }

	    @Override
	    public void ServiceTask9(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 9
			processService.upsert(new ProcessInstance(processid, "ServiceTask9"));
	        System.out.println("Executing Service Task 9");
	    }

	    @Override
	    public void ServiceTask7(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 7
			processService.upsert(new ProcessInstance(processid, "ServiceTask7"));
	        System.out.println("Executing Service Task 7");
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
	    public void ServiceTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 3
			processService.upsert(new ProcessInstance(processid, "ServiceTask3"));
	        System.out.println("Executing Service Task 3");
	    }

	    @Override
	    public void ServiceTask6(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 6
			processService.upsert(new ProcessInstance(processid, "ServiceTask6"));
	        System.out.println("Executing Service Task 6");
	    }

	    @Override
	    public void ServiceTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "ServiceTask2"));
	        System.out.println("Executing Service Task 2");
	    }

	    @Override
	    public void ScriptTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 4
			processService.upsert(new ProcessInstance(processid, "ScriptTask4"));
	        System.out.println("Executing Script Task 4");
	    }

	    @Override
	    public void ServiceTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 4
			processService.upsert(new ProcessInstance(processid, "ServiceTask4"));
	        System.out.println("Executing Service Task 4");
	    }

	    @Override
	    public void UserTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for UserTask 2
			processService.upsert(new ProcessInstance(processid, "UserTask2"));
	        System.out.println("Executing UserTask 2");
	    }

	    @Override
	    public void ScriptTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 3
			processService.upsert(new ProcessInstance(processid, "ScriptTask3"));
	        System.out.println("Executing Script Task 3");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private ConvergingsService convergingsService = new ConvergingsServiceImpl();

    @Route(url = "call/UserTask1")
    public Map<String, Object> UserTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (((hasTaskState("UserTask2") || hasTaskState("ServiceTask1")) || (hasTaskState("ServiceTask2") || hasTaskState("ScriptTask2")))||
			((hasTaskState("ServiceTask6") || hasTaskState("ServiceTask5")) || hasTaskState("ScriptTask3")))
			) {
				res.put("message", "User Task 1 DENIED");
            	return res;
			}
		}

		convergingsService.UserTask1(requestBody, processid);
		convergingsService.ServiceTask7(requestBody, processid);
		convergingsService.ServiceTask8(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "User Task 1 SUCCESS");
        return res;
    }

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

		convergingsService.UserTask2(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "UserTask 2 SUCCESS");
        return res;
    }

    @Route(url = "call/ServiceTask4")
    public Map<String, Object> ServiceTask4(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		convergingsService.ServiceTask4(requestBody, processid);
		boolean null = true;
		boolean a = true;
		boolean b = true;
		boolean c = true;
		boolean d = true;
		boolean e = true;
		boolean f = true;
		boolean g = true;
		boolean h = true;
		boolean i = true;
		boolean j = true;
		boolean k = true;
		boolean o = true;
		boolean p = true;
		boolean s = true;
		boolean y = true;
		if (b) {
		  if (e) {
		    if (g) {
		      convergingsService.ServiceTask3(requestBody, processId);
		      convergingsService.ScriptTask3(requestBody, processId);
		    }
		  } else if (f) {
		    if (h) {
		      if (o) {
		        convergingsService.ServiceTask5(requestBody, processid);
		      }
		    } else if (k) {
		      if (null) {
		        if (null) {
		          convergingsService.ServiceTask9(requestBody, processid);
		        } else if (p) {
		          convergingsService.ScriptTask4(requestBody, processid);
		        }
		        convergingsService.ServiceTask6(requestBody, processId);
		      }
		    }
		  }
		} else if (a) {
		  if (c) {
		    if (i) {
		      convergingsService.ServiceTask1(requestBody, processid);
		    } else if (s) {
		      	return res;
		    }
		  } else if (d) {
		    if (j) {
		      convergingsService.ServiceTask2(requestBody, processid);
		    } else if (y) {
		      convergingsService.ScriptTask2(requestBody, processid);
		    }
		  }
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

// @generated from Repeat.bpmn2

package bpmn.product.Repeat;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class RepeatResourceImpl extends RepeatComponent {

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

    static interface RepeatService {
        void UserTask2(Map<String, Object> body, String processid);
        void ServiceTask3(Map<String, Object> body, String processid);
        void UserTask1(Map<String, Object> body, String processid);
        void ScriptTask1(Map<String, Object> body, String processid);
        void ServiceTask6(Map<String, Object> body, String processid);
        void ServiceTask2(Map<String, Object> body, String processid);
        void ServiceTask1(Map<String, Object> body, String processid);

    }

    static class RepeatServiceImpl implements RepeatService {
	    @Override
	    public void UserTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 2
			processService.upsert(new ProcessInstance(processid, "UserTask2"));
	        System.out.println("Executing User Task 2");
	    }

	    @Override
	    public void ServiceTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 3
			processService.upsert(new ProcessInstance(processid, "ServiceTask3"));
	        System.out.println("Executing Service Task 3");
	    }

	    @Override
	    public void UserTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 1
			processService.upsert(new ProcessInstance(processid, "UserTask1"));
	        System.out.println("Executing User Task 1");
	    }

	    @Override
	    public void ScriptTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 1
			processService.upsert(new ProcessInstance(processid, "ScriptTask1"));
	        System.out.println("Executing Script Task 1");
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
	    public void ServiceTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 1
			processService.upsert(new ProcessInstance(processid, "ServiceTask1"));
	        System.out.println("Executing Service Task 1");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private RepeatService repeatService = new RepeatServiceImpl();

    @Route(url = "call/UserTask2")
    public Map<String, Object> UserTask2(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("ScriptTask1"))
			) {
				res.put("message", "User Task 2 DENIED");
            	return res;
			}
		}

		repeatService.UserTask2(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "User Task 2 SUCCESS");
        return res;
    }

    @Route(url = "call/UserTask1")
    public Map<String, Object> UserTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		repeatService.UserTask1(requestBody, processid);
		boolean null = true;
		boolean d = true;
		do {
		    repeatService.ServiceTask1(requestBody, processId);
		    repeatService.ServiceTask2(requestBody, processId);
		    repeatService.ServiceTask3(requestBody, processId);
		    repeatService.ScriptTask1(requestBody, processid);
		    while (null) {
		        if (null) {
		            repeatService.ServiceTask6(requestBody, processid);
		        }
		        repeatService.ScriptTask1(requestBody, processid);
		        if (null) break;
		}
		
		    if (d) break;
		} while (c || b);
		

        res.put("status", "ok");
        res.put("message", "User Task 1 SUCCESS");
        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

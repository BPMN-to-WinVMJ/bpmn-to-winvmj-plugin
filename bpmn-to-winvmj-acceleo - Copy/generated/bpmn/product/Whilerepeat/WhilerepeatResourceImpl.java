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
        void ScriptTask3(Map<String, Object> body, String processid);
        void ScriptTask4(Map<String, Object> body, String processid);
        void task2(Map<String, Object> body, String processid);
        void task1(Map<String, Object> body, String processid);
        void ServiceTask4(Map<String, Object> body, String processid);
        void ScriptTask5(Map<String, Object> body, String processid);
        void ServiceTask2(Map<String, Object> body, String processid);
        void UserTask1(Map<String, Object> body, String processid);
        void UserTask3(Map<String, Object> body, String processid);
        void ScriptTask6(Map<String, Object> body, String processid);
        void UserTask4(Map<String, Object> body, String processid);
        void ScriptTask2(Map<String, Object> body, String processid);

    }

    static class WhilerepeatServiceImpl implements WhilerepeatService {
	    @Override
	    public void ScriptTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 3
			processService.upsert(new ProcessInstance(processid, "ScriptTask3"));
	        System.out.println("Executing Script Task 3");
	    }

	    @Override
	    public void ScriptTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 4
			processService.upsert(new ProcessInstance(processid, "ScriptTask4"));
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
	    public void ServiceTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 4
			processService.upsert(new ProcessInstance(processid, "ServiceTask4"));
	        System.out.println("Executing Service Task 4");
	    }

	    @Override
	    public void ScriptTask5(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 5
			processService.upsert(new ProcessInstance(processid, "ScriptTask5"));
	        System.out.println("Executing Script Task 5");
	    }

	    @Override
	    public void ServiceTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "ServiceTask2"));
	        System.out.println("Executing Service Task 2");
	    }

	    @Override
	    public void UserTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 1
			processService.upsert(new ProcessInstance(processid, "UserTask1"));
	        System.out.println("Executing User Task 1");
	    }

	    @Override
	    public void UserTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 3
			processService.upsert(new ProcessInstance(processid, "UserTask3"));
	        System.out.println("Executing User Task 3");
	    }

	    @Override
	    public void ScriptTask6(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 6
			processService.upsert(new ProcessInstance(processid, "ScriptTask6"));
	        System.out.println("Executing Script Task 6");
	    }

	    @Override
	    public void UserTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 4
			processService.upsert(new ProcessInstance(processid, "UserTask4"));
	        System.out.println("Executing User Task 4");
	    }

	    @Override
	    public void ScriptTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Script Task 2
			processService.upsert(new ProcessInstance(processid, "ScriptTask2"));
	        System.out.println("Executing Script Task 2");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private WhilerepeatService whilerepeatService = new WhilerepeatServiceImpl();

    @Route(url = "call/UserTask1")
    public Map<String, Object> UserTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		whilerepeatService.UserTask1(requestBody, processid);
		boolean null = true;
		boolean a = true;
		boolean b = true;
		boolean c = true;
		boolean d = true;
		boolean e = true;
		boolean f = true;
		whilerepeatService.task1(requestBody, processid);
		while (a || b) {
		    if (a) {
		        whilerepeatService.task2(requestBody, processid);
		    }
		    else if (b) {
		        whilerepeatService.ScriptTask2(requestBody, processid);
		    }
		    whilerepeatService.task1(requestBody, processid);
		    if (null) break;
		}
		
		whilerepeatService.ServiceTask2(requestBody, processid);
		whilerepeatService.ServiceTask4(requestBody, processId);
		if (c) {
		    whilerepeatService.ScriptTask3(requestBody, processid);
		} else if (d) {
		    whilerepeatService.ScriptTask4(requestBody, processid);
		} else if (e) {
		    return res;
		}
		whilerepeatService.ScriptTask5(requestBody, processId);
		while (f) {
		    if (f) {
		        whilerepeatService.ScriptTask6(requestBody, processId);
		        return res;
		    }
		    whilerepeatService.ServiceTask4(requestBody, processId);
		    if (c) {
		        whilerepeatService.ScriptTask3(requestBody, processid);
		    } else if (d) {
		        whilerepeatService.ScriptTask4(requestBody, processid);
		    } else if (e) {
		        return res;
		    }
		    whilerepeatService.ScriptTask5(requestBody, processId);
		    if (null) break;
		}
		
		

        res.put("status", "ok");
        res.put("message", "User Task 1 SUCCESS");
        return res;
    }

    @Route(url = "call/UserTask3")
    public Map<String, Object> UserTask3(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("ServiceTask4"))
			) {
				res.put("message", "User Task 3 DENIED");
            	return res;
			}
		}

		whilerepeatService.UserTask3(requestBody, processid);
		boolean f = true;
		whilerepeatService.ScriptTask5(requestBody, processid);
		ifa (f) {
		    whilerepeatService.ScriptTask6(requestBody, processId);
		}
		

        res.put("status", "ok");
        res.put("message", "User Task 3 SUCCESS");
        return res;
    }

    @Route(url = "call/UserTask4")
    public Map<String, Object> UserTask4(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("ScriptTask6"))
			) {
				res.put("message", "User Task 4 DENIED");
            	return res;
			}
		}

		whilerepeatService.UserTask4(requestBody, processid);
		whilerepeatService.ServiceTask4(requestBody, processId);
		if (c) {
		    whilerepeatService.ScriptTask3(requestBody, processid);
		} else if (d) {
		    whilerepeatService.ScriptTask4(requestBody, processid);
		} else if (e) {
		    return res;
		}
		whilerepeatService.ScriptTask5(requestBody, processId);
		while (f) {
		    if (f) {
		        whilerepeatService.ScriptTask6(requestBody, processId);
		        return res;
		    }
		    whilerepeatService.ServiceTask4(requestBody, processId);
		    if (c) {
		        whilerepeatService.ScriptTask3(requestBody, processid);
		    } else if (d) {
		        whilerepeatService.ScriptTask4(requestBody, processid);
		    } else if (e) {
		        return res;
		    }
		    whilerepeatService.ScriptTask5(requestBody, processId);
		    if (null) break;
		}
		
		

        res.put("status", "ok");
        res.put("message", "User Task 4 SUCCESS");
        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

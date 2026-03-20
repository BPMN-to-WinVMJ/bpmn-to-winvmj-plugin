// @generated from Deepbranch.bpmn2

package bpmn.product.Deepbranch;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class DeepbranchResourceImpl extends DeepbranchComponent {

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

    static interface DeepbranchService {
        void ServiceTask4(Map<String, Object> body, String processid);
        void ServiceTask5(Map<String, Object> body, String processid);
        void ServiceTask2(Map<String, Object> body, String processid);
        void ServiceTask3(Map<String, Object> body, String processid);
        void UserTask1(Map<String, Object> body, String processid);
        void ServiceTask1(Map<String, Object> body, String processid);
        void UserTask3(Map<String, Object> body, String processid);
        void UserTask2(Map<String, Object> body, String processid);
        void UserTask4(Map<String, Object> body, String processid);

    }

    static class DeepbranchServiceImpl implements DeepbranchService {
	    @Override
	    public void ServiceTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 4
			processService.upsert(new ProcessInstance(processid, "ServiceTask4"));
	        System.out.println("Executing Service Task 4");
	    }

	    @Override
	    public void ServiceTask5(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 5
			processService.upsert(new ProcessInstance(processid, "ServiceTask5"));
	        System.out.println("Executing Service Task 5");
	    }

	    @Override
	    public void ServiceTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "ServiceTask2"));
	        System.out.println("Executing Service Task 2");
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
	    public void ServiceTask1(Map<String, Object> body, String processid {
	        // TODO: Implement logic for Service Task 1
			processService.upsert(new ProcessInstance(processid, "ServiceTask1"));
	        System.out.println("Executing Service Task 1");
	    }

	    @Override
	    public void UserTask3(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 3
			processService.upsert(new ProcessInstance(processid, "UserTask3"));
	        System.out.println("Executing User Task 3");
	    }

	    @Override
	    public void UserTask2(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 2
			processService.upsert(new ProcessInstance(processid, "UserTask2"));
	        System.out.println("Executing User Task 2");
	    }

	    @Override
	    public void UserTask4(Map<String, Object> body, String processid {
	        // TODO: Implement logic for User Task 4
			processService.upsert(new ProcessInstance(processid, "UserTask4"));
	        System.out.println("Executing User Task 4");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private DeepbranchService deepbranchService = new DeepbranchServiceImpl();

    @Route(url = "call/UserTask1")
    public Map<String, Object> UserTask1(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		deepbranchService.UserTask1(requestBody, processid);
		boolean null = true;
		if (null) {
		  deepbranchService.ServiceTask1(requestBody, processId);
		} else if (null) {
		} else if (null) {
		  deepbranchService.ServiceTask3(requestBody, processId);
		  if (null) {
		    	return res;
		  } else if (null) {
		    deepbranchService.ServiceTask4(requestBody, processid);
		  }
		}
		deepbranchService.ServiceTask5(requestBody, processid);
		

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
			
			if (hasTaskState("UserTask1"))
			) {
				res.put("message", "User Task 3 DENIED");
            	return res;
			}
		}

		deepbranchService.UserTask3(requestBody, processid);
		deepbranchService.ServiceTask2(requestBody, processid);
		deepbranchService.ServiceTask5(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "User Task 3 SUCCESS");
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
			
			if (hasTaskState("ServiceTask1"))
			) {
				res.put("message", "User Task 2 DENIED");
            	return res;
			}
		}

		deepbranchService.UserTask2(requestBody, processid);
		deepbranchService.ServiceTask1(requestBody, processId);
		

        res.put("status", "ok");
        res.put("message", "User Task 2 SUCCESS");
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
			
			if (hasTaskState("ServiceTask3"))
			) {
				res.put("message", "User Task 4 DENIED");
            	return res;
			}
		}

		deepbranchService.UserTask4(requestBody, processid);
		deepbranchService.ServiceTask5(requestBody, processid);
		

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

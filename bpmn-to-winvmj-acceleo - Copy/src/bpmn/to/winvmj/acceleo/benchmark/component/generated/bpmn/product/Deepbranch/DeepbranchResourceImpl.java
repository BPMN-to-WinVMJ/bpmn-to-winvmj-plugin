// @generated from Deepbranch.bpmn2

package bpmn.product.Deepbranch;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class DeepbranchResourceImpl extends DeepbranchResourceComponent {

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
        void serviceTask1(Map<String, Object> body, String processid);
        void userTask4(Map<String, Object> body, String processid);
        void userTask2(Map<String, Object> body, String processid);
        void serviceTask2(Map<String, Object> body, String processid);
        void serviceTask5(Map<String, Object> body, String processid);
        void serviceTask4(Map<String, Object> body, String processid);
        void userTask1(Map<String, Object> body, String processid);
        void userTask3(Map<String, Object> body, String processid);
        void serviceTask3(Map<String, Object> body, String processid);

    }

    static class DeepbranchServiceImpl implements DeepbranchService {
	    @Override
	    public void serviceTask1(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Service Task 1
			processService.upsert(new ProcessInstance(processid, "serviceTask1"));
	        System.out.println("Executing Service Task 1");
	    }

	    @Override
	    public void userTask4(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for User Task 4
			processService.upsert(new ProcessInstance(processid, "userTask4"));
	        System.out.println("Executing User Task 4");
	    }

	    @Override
	    public void userTask2(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for User Task 2
			processService.upsert(new ProcessInstance(processid, "userTask2"));
	        System.out.println("Executing User Task 2");
	    }

	    @Override
	    public void serviceTask2(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Service Task 2
			processService.upsert(new ProcessInstance(processid, "serviceTask2"));
	        System.out.println("Executing Service Task 2");
	    }

	    @Override
	    public void serviceTask5(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Service Task 5
			processService.upsert(new ProcessInstance(processid, "serviceTask5"));
	        System.out.println("Executing Service Task 5");
	    }

	    @Override
	    public void serviceTask4(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Service Task 4
			processService.upsert(new ProcessInstance(processid, "serviceTask4"));
	        System.out.println("Executing Service Task 4");
	    }

	    @Override
	    public void userTask1(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for User Task 1
			processService.upsert(new ProcessInstance(processid, "userTask1"));
	        System.out.println("Executing User Task 1");
	    }

	    @Override
	    public void userTask3(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for User Task 3
			processService.upsert(new ProcessInstance(processid, "userTask3"));
	        System.out.println("Executing User Task 3");
	    }

	    @Override
	    public void serviceTask3(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Service Task 3
			processService.upsert(new ProcessInstance(processid, "serviceTask3"));
	        System.out.println("Executing Service Task 3");
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static DeepbranchService deepbranchService = new DeepbranchServiceImpl();

    @Route(url = "call/userTask4")
    public Map<String, Object> userTask4(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "var"))
			) {
				res.put("status", "fail");
				res.put("message", "userTask4 DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "userTask4 SUCCESS");

		deepbranchService.userTask4(requestBody, processid);
		deepbranchService.serviceTask5(requestBody, processid);

        return res;
    }

    @Route(url = "call/userTask2")
    public Map<String, Object> userTask2(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "serviceTask1"))
			) {
				res.put("status", "fail");
				res.put("message", "userTask2 DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "userTask2 SUCCESS");

		deepbranchService.userTask2(requestBody, processid);
		deepbranchService.serviceTask5(requestBody, processid);

        return res;
    }

    @Route(url = "call/userTask1")
    public Map<String, Object> userTask1(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "userTask1"));
		res.put("processid", processid);

        res.put("status", "ok");
        res.put("message", "userTask1 SUCCESS");

		deepbranchService.userTask1(requestBody, processid);
		boolean var = true;
		if (var) {
		    processService.upsert(new ProcessInstance(processid, "var"));
		    deepbranchService.serviceTask1(requestBody, processid);
		    return res;
		}
		else if (var) {
		    processService.upsert(new ProcessInstance(processid, "var"));
		    return res;
		}
		else if (var) {
		    processService.upsert(new ProcessInstance(processid, "var"));
		    deepbranchService.serviceTask3(requestBody, processid);
		    if (var) {
		        processService.upsert(new ProcessInstance(processid, "var"));
		        return res;
		    }
		    else if (var) {
		        processService.upsert(new ProcessInstance(processid, "var"));
		        deepbranchService.serviceTask4(requestBody, processid);
		    }
		}
		deepbranchService.serviceTask5(requestBody, processid);

        return res;
    }

    @Route(url = "call/userTask3")
    public Map<String, Object> userTask3(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "var"))
			) {
				res.put("status", "fail");
				res.put("message", "userTask3 DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "userTask3 SUCCESS");

		deepbranchService.userTask3(requestBody, processid);
		deepbranchService.serviceTask2(requestBody, processid);
		deepbranchService.serviceTask5(requestBody, processid);

        return res;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

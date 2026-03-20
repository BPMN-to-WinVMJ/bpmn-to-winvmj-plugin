// @generated from Paymentstudyplan.bpmn2

package bpmn.product.Paymentstudyplan;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class PaymentstudyplanResourceImpl extends PaymentstudyplanComponent {

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

    static interface PaymentstudyplanService {
        void confirmstudyplan(Map<String, Object> body, String processid);
        void createstudyplan(Map<String, Object> body, String processid);
        void viewstudyplanandpaymentstatus(Map<String, Object> body, String processid);
        void viewcourseschedule(Map<String, Object> body, String processid);
        void makepayment(Map<String, Object> body, String processid);

    }

    static class PaymentstudyplanServiceImpl implements PaymentstudyplanService {
	    @Override
	    public void confirmstudyplan(Map<String, Object> body, String processid {
	        // TODO: Implement logic for confirm study plan
			processService.upsert(new ProcessInstance(processid, "confirmstudyplan"));
	        System.out.println("Executing confirm study plan");
	    }

	    @Override
	    public void createstudyplan(Map<String, Object> body, String processid {
	        // TODO: Implement logic for create study plan
			processService.upsert(new ProcessInstance(processid, "createstudyplan"));
	        System.out.println("Executing create study plan");
	    }

	    @Override
	    public void viewstudyplanandpaymentstatus(Map<String, Object> body, String processid {
	        // TODO: Implement logic for view study plan and payment status
			processService.upsert(new ProcessInstance(processid, "viewstudyplanandpaymentstatus"));
	        System.out.println("Executing view study plan and payment status");
	    }

	    @Override
	    public void viewcourseschedule(Map<String, Object> body, String processid {
	        // TODO: Implement logic for view course schedule
			processService.upsert(new ProcessInstance(processid, "viewcourseschedule"));
	        System.out.println("Executing view course schedule");
	    }

	    @Override
	    public void makepayment(Map<String, Object> body, String processid {
	        // TODO: Implement logic for make payment
			processService.upsert(new ProcessInstance(processid, "makepayment"));
	        System.out.println("Executing make payment");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
	private PaymentstudyplanService paymentstudyplanService = new PaymentstudyplanServiceImpl();

    @Route(url = "call/confirmstudyplan")
    public Map<String, Object> confirmstudyplan(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("makepayment"))
			) {
				res.put("message", "confirm study plan DENIED");
            	return res;
			}
		}

		paymentstudyplanService.confirmstudyplan(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "confirm study plan SUCCESS");
        return res;
    }

    @Route(url = "call/createstudyplan")
    public Map<String, Object> createstudyplan(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("confirmstudyplan"))
			) {
				res.put("message", "create study plan DENIED");
            	return res;
			}
		}

		paymentstudyplanService.createstudyplan(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "create study plan SUCCESS");
        return res;
    }

    @Route(url = "call/viewstudyplanandpaymentstatus")
    public Map<String, Object> viewstudyplanandpaymentstatus(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("createstudyplan"))
			) {
				res.put("message", "view study plan and payment status DENIED");
            	return res;
			}
		}

		paymentstudyplanService.viewstudyplanandpaymentstatus(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "view study plan and payment status SUCCESS");
        return res;
    }

    @Route(url = "call/viewcourseschedule")
    public Map<String, Object> viewcourseschedule(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (hasTaskState("viewstudyplanandpaymentstatus"))
			) {
				res.put("message", "view course schedule DENIED");
            	return res;
			}
		}

		paymentstudyplanService.viewcourseschedule(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "view course schedule SUCCESS");
        return res;
    }

    @Route(url = "call/makepayment")
    public Map<String, Object> makepayment(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		paymentstudyplanService.makepayment(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "make payment SUCCESS");
        return res;
    }

	private static boolean hasTaskState(List<Process> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

// @generated

package bpmn.product.Process1;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class Process1ResourceImpl extends Process1Component {

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

    static interface Process1Service {
        void createstudyplan(Map<String, Object> body, String processid);
        void viewcourseschedule(Map<String, Object> body, String processid);
        void makepayment(Map<String, Object> body, String processid);
        void viewstudyplanandpaymentstatus(Map<String, Object> body, String processid);
        void confirmpayment(Map<String, Object> body, String processid);

    }

    static class Process1ServiceImpl implements Process1Service {
	    @Override
	    public void createstudyplan(Map<String, Object> body, String processid {
	        // TODO: Implement logic for create study plan
			processService.upsert(new ProcessInstance(processid, "createstudyplan"));
	        System.out.println("Executing create study plan");
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

	    @Override
	    public void viewstudyplanandpaymentstatus(Map<String, Object> body, String processid {
	        // TODO: Implement logic for view study plan and payment status
			processService.upsert(new ProcessInstance(processid, "viewstudyplanandpaymentstatus"));
	        System.out.println("Executing view study plan and payment status");
	    }

	    @Override
	    public void confirmpayment(Map<String, Object> body, String processid {
	        // TODO: Implement logic for confirm payment
			processService.upsert(new ProcessInstance(processid, "confirmpayment"));
	        System.out.println("Executing confirm payment");
	    }


    }

    private ProcessService processService = new ProcessServiceImpl();
    
	private Process1Service Process1Service = new Process1ServiceImpl();


    @Route(url = "call/createstudyplan")
    public Map<String, Object> createstudyplan(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

		processService.upsert(new ProcessInstance(processid, "createstudyplan"));
		Process1Service.createstudyplan(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "create study plan SUCCESS");
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
			
			if (processes.stream()
				.filter(
					x -> x.state.equalsIgnoreCase("viewstudyplanandpaymentstatus")
					
				).toList().isEmpty()
			) {
				res.put("message", "view course schedule DENIED");
            	return res;
			}
		}

		processService.upsert(new ProcessInstance(processid, "viewcourseschedule"));
		Process1Service.viewcourseschedule(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "view course schedule SUCCESS");
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
			
			if (processes.stream()
				.filter(
					x -> x.state.equalsIgnoreCase("confirmpayment")
					
				).toList().isEmpty()
			) {
				res.put("message", "view study plan and payment status DENIED");
            	return res;
			}
		}

		processService.upsert(new ProcessInstance(processid, "viewstudyplanandpaymentstatus"));
		Process1Service.viewstudyplanandpaymentstatus(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "view study plan and payment status SUCCESS");
        return res;
    }

    @Route(url = "call/confirmpayment")
    public Map<String, Object> confirmpayment(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (processes.stream()
				.filter(
					x -> x.state.equalsIgnoreCase("makepayment")
					
				).toList().isEmpty()
			) {
				res.put("message", "confirm payment DENIED");
            	return res;
			}
		}

		processService.upsert(new ProcessInstance(processid, "confirmpayment"));
		Process1Service.confirmpayment(requestBody, processid);
		

        res.put("status", "ok");
        res.put("message", "confirm payment SUCCESS");
        return res;
    }


}

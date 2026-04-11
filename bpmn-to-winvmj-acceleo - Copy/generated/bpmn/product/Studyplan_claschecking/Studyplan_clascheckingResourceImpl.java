// @generated from Studyplan_claschecking.bpmn2

package bpmn.product.Studyplan_claschecking;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class Studyplan_clascheckingResourceImpl extends Studyplan_clascheckingResourceComponent {

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

    static interface Studyplan_clascheckingService {
        void viewInvalidFormNotification(Map<String, Object> body, String processid);
        void viewWaitingForApproval(Map<String, Object> body, String processid);
        void fillAndSubmitTheStudyForm(Map<String, Object> body, String processid);
        void viewValidFormNotificationAndWaitingForApproval(Map<String, Object> body, String processid);

    }

    static class Studyplan_clascheckingServiceImpl implements Studyplan_clascheckingService {
	    @Override
	    public void viewInvalidFormNotification(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for view invalid form notification
			processService.upsert(new ProcessInstance(processid, "viewInvalidFormNotification"));
	        System.out.println("Executing view invalid form notification");
	    }

	    @Override
	    public void viewWaitingForApproval(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for view waiting for approval
			processService.upsert(new ProcessInstance(processid, "viewWaitingForApproval"));
	        System.out.println("Executing view waiting for approval");
	    }

	    @Override
	    public void fillAndSubmitTheStudyForm(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Fill and submit the study form
			processService.upsert(new ProcessInstance(processid, "fillAndSubmitTheStudyForm"));
	        System.out.println("Executing Fill and submit the study form");
	    }

	    @Override
	    public void viewValidFormNotificationAndWaitingForApproval(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for view valid form notification and waiting for approval
			processService.upsert(new ProcessInstance(processid, "viewValidFormNotificationAndWaitingForApproval"));
	        System.out.println("Executing view valid form notification and waiting for approval");
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static Studyplan_clascheckingService studyplan_clascheckingService = new Studyplan_clascheckingServiceImpl();

    @Route(url = "call/viewInvalidFormNotification")
    public Map<String, Object> viewInvalidFormNotification(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "clashTrue"))
			) {
				res.put("status", "fail");
				res.put("message", "viewInvalidFormNotification DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "viewInvalidFormNotification SUCCESS");

		studyplan_clascheckingService.viewInvalidFormNotification(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/viewWaitingForApproval")
    public Map<String, Object> viewWaitingForApproval(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "fillAndSubmitTheStudyForm"))
			) {
				res.put("status", "fail");
				res.put("message", "viewWaitingForApproval DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "viewWaitingForApproval SUCCESS");

		studyplan_clascheckingService.viewWaitingForApproval(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/fillAndSubmitTheStudyForm")
    public Map<String, Object> fillAndSubmitTheStudyForm(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "fillAndSubmitTheStudyForm"));
		res.put("processid", processid);

        res.put("status", "ok");
        res.put("message", "fillAndSubmitTheStudyForm SUCCESS");

		studyplan_clascheckingService.fillAndSubmitTheStudyForm(requestBody, processid);
		boolean noClash = true;
		boolean clashTrue = true;
		if (noClash) {
		    processService.upsert(new ProcessInstance(processid, "noClash"));
		    List<ProcessInstance> processes = processService.getAllById(processid);
		    if (!(hasTaskState(processes, "viewWaitingForApproval") && 
		    hasTaskState(processes, "viewValidFormNotificationAndWaitingForApproval"))) {
		        res.put("status", "fail");
		        res.put("message", "Parallel branches not complete");
		        return res;
		    }
		}
		if (clashTrue) {
		    processService.upsert(new ProcessInstance(processid, "clashTrue"));
		    return res;
		}

        return res;
    }

    @Route(url = "call/viewValidFormNotificationAndWaitingForApproval")
    public Map<String, Object> viewValidFormNotificationAndWaitingForApproval(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "fillAndSubmitTheStudyForm"))
			) {
				res.put("status", "fail");
				res.put("message", "viewValidFormNotificationAndWaitingForApproval DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "viewValidFormNotificationAndWaitingForApproval SUCCESS");

		studyplan_clascheckingService.viewValidFormNotificationAndWaitingForApproval(requestBody, processid);
		List<ProcessInstance> processes = processService.getAllById(processid);
		    if (!(hasTaskState(processes, "viewWaitingForApproval") && 
		    hasTaskState(processes, "viewValidFormNotificationAndWaitingForApproval"))) {
		        res.put("status", "fail");
		        res.put("message", "Parallel branches not complete");
		        return res;
		    }

        return res;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

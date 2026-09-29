// @generated from Studyplan_approval.bpmn2

package bpmn.product.Studyplan_approval;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class Studyplan_approvalResourceImpl extends Studyplan_approvalResourceComponent {

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

    static interface Studyplan_approvalService {
        void approveTheStudyPlan(Map<String, Object> body, String processid);
        void reviewTheStudyPlan(Map<String, Object> body, String processid);
        void rejectStudyPlan(Map<String, Object> body, String processid);

    }

    static class Studyplan_approvalServiceImpl implements Studyplan_approvalService {
	    @Override
	    public void approveTheStudyPlan(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for approve the study plan
			processService.upsert(new ProcessInstance(processid, "approveTheStudyPlan"));
	        System.out.println("Executing approve the study plan");
	    }

	    @Override
	    public void reviewTheStudyPlan(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Review the Study Plan
			processService.upsert(new ProcessInstance(processid, "reviewTheStudyPlan"));
	        System.out.println("Executing Review the Study Plan");
	    }

	    @Override
	    public void rejectStudyPlan(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Reject Study Plan
			processService.upsert(new ProcessInstance(processid, "rejectStudyPlan"));
	        System.out.println("Executing Reject Study Plan");
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static Studyplan_approvalService studyplan_approvalService = new Studyplan_approvalServiceImpl();

    @Route(url = "call/approveTheStudyPlan")
    public Map<String, Object> approveTheStudyPlan(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "approvalTrue"))
			) {
				res.put("status", "fail");
				res.put("message", "approveTheStudyPlan DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "approveTheStudyPlan SUCCESS");

		studyplan_approvalService.approveTheStudyPlan(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/reviewTheStudyPlan")
    public Map<String, Object> reviewTheStudyPlan(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "reviewTheStudyPlan"));
		res.put("processid", processid);

        res.put("status", "ok");
        res.put("message", "reviewTheStudyPlan SUCCESS");

		studyplan_approvalService.reviewTheStudyPlan(requestBody, processid);
		boolean approvalFalse = true;
		boolean approvalTrue = true;
		if (approvalFalse) {
		    processService.upsert(new ProcessInstance(processid, "approvalFalse"));
		    return res;
		}
		else if (approvalTrue) {
		    processService.upsert(new ProcessInstance(processid, "approvalTrue"));
		    return res;
		}

        return res;
    }

    @Route(url = "call/rejectStudyPlan")
    public Map<String, Object> rejectStudyPlan(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "approvalFalse"))
			) {
				res.put("status", "fail");
				res.put("message", "rejectStudyPlan DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "rejectStudyPlan SUCCESS");

		studyplan_approvalService.rejectStudyPlan(requestBody, processid);
		

        return res;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

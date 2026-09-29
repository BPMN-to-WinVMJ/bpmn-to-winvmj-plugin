// @generated from Clo_subclo.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class Clo_subcloResourceImpl extends Clo_subcloResourceComponent {

    public static AccountService accountService;
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

    static interface Clo_subcloService {
        void conductTeachingAndStudentAssessmentActivities(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void mapGradeComponentsToCloAndSubClo(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void entryComponentGrades(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void manageSubClo(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void gradeStudentAssessmentResult(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void manageGradeComponent(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class Clo_subcloServiceImpl implements Clo_subcloService {
	    @Override
	    public void conductTeachingAndStudentAssessmentActivities(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for Conduct Teaching and Student Assessment Activities
			processService.upsert(new ProcessInstance(processid, "conductTeachingAndStudentAssessmentActivities"));
	        System.out.println("Executing Conduct Teaching and Student Assessment Activities");
			
	    }

	    @Override
	    public void mapGradeComponentsToCloAndSubClo(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for mapGradeComponentsToCloAndSubClo
			processService.upsert(new ProcessInstance(processid, "mapGradeComponentsToCloAndSubClo"));
	        System.out.println("Executing mapGradeComponentsToCloAndSubClo");
			
	    }

	    @Override
	    public void entryComponentGrades(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for entryComponentGrades
			processService.upsert(new ProcessInstance(processid, "entryComponentGrades"));
	        System.out.println("Executing entryComponentGrades");
			
	    }

	    @Override
	    public void manageSubClo(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for manageSubClo
			processService.upsert(new ProcessInstance(processid, "manageSubClo"));
	        System.out.println("Executing manageSubClo");
			
	    }

	    @Override
	    public void gradeStudentAssessmentResult(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for gradeStudentAssessmentResult
			processService.upsert(new ProcessInstance(processid, "gradeStudentAssessmentResult"));
	        System.out.println("Executing gradeStudentAssessmentResult");
			
	    }

	    @Override
	    public void manageGradeComponent(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for manageGradeComponent
			processService.upsert(new ProcessInstance(processid, "manageGradeComponent"));
	        System.out.println("Executing manageGradeComponent");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static Clo_subcloService clo_subcloService = new Clo_subcloServiceImpl();

    @Route(url = "call/mapGradeComponentsToCloAndSubClo")
    public Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "manageGradeComponent"))
			) {
				response.put("status", "FAIL");
				response.put("message", "mapGradeComponentsToCloAndSubClo DENIED");
            	return response;
			}

            response.put("status", "ok");
            response.put("message", "mapGradeComponentsToCloAndSubClo SUCCESS");

            clo_subcloService.mapGradeComponentsToCloAndSubClo(requestBody, processid, response);
            clo_subcloService.conductTeachingAndStudentAssessmentActivities(requestBody, processid, response);
            clo_subcloService.gradeStudentAssessmentResult(requestBody, processid, response);
		}

        return response;
    }

    @Route(url = "call/entryComponentGrades")
    public Map<String, Object> entryComponentGrades(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        List<ProcessInstance> processes = processService.getAllById(processid);
			if (!(hasTaskState(processes, "gradeStudentAssessmentResult"))
			) {
				response.put("status", "FAIL");
				response.put("message", "entryComponentGrades DENIED");
            	return response;
			}
            response.put("status", "ok");
            response.put("message", "entryComponentGrades SUCCESS");

            clo_subcloService.entryComponentGrades(requestBody, processid, response);   
		}
        return response;
    }

    @Route(url = "call/manageSubClo")
    public Map<String, Object> manageSubClo(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "manageSubClo"));
		response.put("processid", processid);

		clo_subcloService.manageSubClo(requestBody, processid, response);
		// From ScriptTask Map Sub-CLO to CLO
		processService.upsert(new ProcessInstance(processid, "Map Sub-CLO to CLO"));
		// TODO: implement 'map sub-clo to clo'

        return response;
    }

    @Route(url = "call/manageGradeComponent")
    public Map<String, Object> manageGradeComponent(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "manageGradeComponent"));
		response.put("processid", processid);

		clo_subcloService.manageGradeComponent(requestBody, processid, response);
		
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

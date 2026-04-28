// @generated from Clo_subclo.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class Clo_subcloResourceImpl extends Clo_subcloResourceComponent {

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
        void manageGradeComponent(Map<String, Object> body, String processid);
        void gradeStudentAssessmentResult(Map<String, Object> body, String processid);
        void entryComponentGrades(Map<String, Object> body, String processid);
        void mapGradeComponentsToCloAndSubClo(Map<String, Object> body, String processid);
        void starter(Map<String, Object> body, String processid);
        void manageSubClo(Map<String, Object> body, String processid);
        void conductTeachingAndStudentAssessmentActivities(Map<String, Object> body, String processid);

    }

    static class Clo_subcloServiceImpl implements Clo_subcloService {
	    @Override
	    public void manageGradeComponent(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for manage Grade Component
			processService.upsert(new ProcessInstance(processid, "manageGradeComponent"));
	        System.out.println("Executing manage Grade Component");
			// From ScriptTask init uang as int
			lalalalallalalalallalalalla
			
			
			// From ScriptTask increment uang 100
			uang += 100;
	    }

	    @Override
	    public void gradeStudentAssessmentResult(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for gradeStudentAssessmentResult
			processService.upsert(new ProcessInstance(processid, "gradeStudentAssessmentResult"));
	        System.out.println("Executing gradeStudentAssessmentResult");
			
	    }

	    @Override
	    public void entryComponentGrades(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for entryComponentGrades
			processService.upsert(new ProcessInstance(processid, "entryComponentGrades"));
	        System.out.println("Executing entryComponentGrades");
			
	    }

	    @Override
	    public void mapGradeComponentsToCloAndSubClo(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for mapGradeComponentsToCloAndSubClo
			processService.upsert(new ProcessInstance(processid, "mapGradeComponentsToCloAndSubClo"));
	        System.out.println("Executing mapGradeComponentsToCloAndSubClo");
			
	    }

	    @Override
	    public void starter(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for starter
			processService.upsert(new ProcessInstance(processid, "starter"));
	        System.out.println("Executing starter");
			
	    }

	    @Override
	    public void manageSubClo(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for manageSubClo
			processService.upsert(new ProcessInstance(processid, "manageSubClo"));
	        System.out.println("Executing manageSubClo");
			
	    }

	    @Override
	    public void conductTeachingAndStudentAssessmentActivities(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for conductTeachingAndStudentAssessmentActivities
			processService.upsert(new ProcessInstance(processid, "conductTeachingAndStudentAssessmentActivities"));
	        System.out.println("Executing conductTeachingAndStudentAssessmentActivities");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static Clo_subcloService clo_subcloService = new Clo_subcloServiceImpl();

    @Route(url = "call/manageGradeComponent")
    public Map<String, Object> manageGradeComponent(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		Object taskVar1 = (Object) requestBody.get("taskVar1");
		Object A = (Object) requestBody.get("A");
		
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "no") || 
			    hasTaskState(processes, "Map Sub-CLO to CLO"))
			) {
				res.put("status", "fail");
				res.put("message", "manageGradeComponent DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "manageGradeComponent SUCCESS");

		clo_subcloService.manageGradeComponent(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/entryComponentGrades")
    public Map<String, Object> entryComponentGrades(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "gradeStudentAssessmentResult"))
			) {
				res.put("status", "fail");
				res.put("message", "entryComponentGrades DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "entryComponentGrades SUCCESS");

		clo_subcloService.entryComponentGrades(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/mapGradeComponentsToCloAndSubClo")
    public Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
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
				res.put("status", "fail");
				res.put("message", "mapGradeComponentsToCloAndSubClo DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "mapGradeComponentsToCloAndSubClo SUCCESS");

		clo_subcloService.mapGradeComponentsToCloAndSubClo(requestBody, processid);
		clo_subcloService.conductTeachingAndStudentAssessmentActivities(requestBody, processid);
		clo_subcloService.gradeStudentAssessmentResult(requestBody, processid);

        return res;
    }

    @Route(url = "call/starter")
    public Map<String, Object> starter(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "starter"));
		res.put("processid", processid);
		

        res.put("status", "ok");
        res.put("message", "starter SUCCESS");

		clo_subcloService.starter(requestBody, processid);
		boolean no = false;
		boolean needcloYes = false;
		if (needcloYes) {
		    boolean canContinue = true;
		    processService.upsert(new ProcessInstance(processid, "needcloYes"));
		    canContinue = false;
		}
		if (no) {
		    boolean canContinue = true;
		    processService.upsert(new ProcessInstance(processid, "no"));
		}
		if (canContinue) {
		}

        return res;
    }

    @Route(url = "call/manageSubClo")
    public Map<String, Object> manageSubClo(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "needcloYes"))
			) {
				res.put("status", "fail");
				res.put("message", "manageSubClo DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "manageSubClo SUCCESS");

		clo_subcloService.manageSubClo(requestBody, processid);
		// From ScriptTask Map Sub-CLO to CLO
		// TODO: implement 'map sub-clo to clo'

        return res;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

// @generated from Clo_subclo.bpmn2

package bpmn.product.Clo_subclo;

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
        void starter(Map<String, Object> body, String processid);
        void mapGradeComponentsToCloAndSubClo(Map<String, Object> body, String processid);
        void entryComponentGrades(Map<String, Object> body, String processid);
        void mapSubCloToClo(Map<String, Object> body, String processid);
        void gradeStudentAssessmentResult(Map<String, Object> body, String processid);
        void crudSubClo(Map<String, Object> body, String processid);
        void crudGradeComponent(Map<String, Object> body, String processid);
        void conductTeachingAndStudentAssessmentActivities(Map<String, Object> body, String processid);

    }

    static class Clo_subcloServiceImpl implements Clo_subcloService {
	    @Override
	    public void starter(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for starter
			processService.upsert(new ProcessInstance(processid, "starter"));
	        System.out.println("Executing starter");
	    }

	    @Override
	    public void mapGradeComponentsToCloAndSubClo(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Map Grade Components To CLO And Sub-CLO
			processService.upsert(new ProcessInstance(processid, "mapGradeComponentsToCloAndSubClo"));
	        System.out.println("Executing Map Grade Components To CLO And Sub-CLO");
	    }

	    @Override
	    public void entryComponentGrades(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Entry Component Grades
			processService.upsert(new ProcessInstance(processid, "entryComponentGrades"));
	        System.out.println("Executing Entry Component Grades");
	    }

	    @Override
	    public void mapSubCloToClo(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Map Sub-CLO to CLO
			processService.upsert(new ProcessInstance(processid, "mapSubCloToClo"));
	        System.out.println("Executing Map Sub-CLO to CLO");
	    }

	    @Override
	    public void gradeStudentAssessmentResult(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Grade Student Assessment Result
			processService.upsert(new ProcessInstance(processid, "gradeStudentAssessmentResult"));
	        System.out.println("Executing Grade Student Assessment Result");
	    }

	    @Override
	    public void crudSubClo(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for CRUD Sub-CLO
			processService.upsert(new ProcessInstance(processid, "crudSubClo"));
	        System.out.println("Executing CRUD Sub-CLO");
	    }

	    @Override
	    public void crudGradeComponent(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for CRUD Grade Component
			processService.upsert(new ProcessInstance(processid, "crudGradeComponent"));
	        System.out.println("Executing CRUD Grade Component");
	    }

	    @Override
	    public void conductTeachingAndStudentAssessmentActivities(Map<String, Object> body, String processid) {
	        // TODO: Implement logic for Conduct Teaching and Student Assessment Activities
			processService.upsert(new ProcessInstance(processid, "conductTeachingAndStudentAssessmentActivities"));
	        System.out.println("Executing Conduct Teaching and Student Assessment Activities");
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static Clo_subcloService clo_subcloService = new Clo_subcloServiceImpl();

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
		boolean no = true;
		boolean needcloYes = true;
		if (needcloYes) {
		    processService.upsert(new ProcessInstance(processid, "needcloYes"));
		    return res;
		}
		if (no) {
		    processService.upsert(new ProcessInstance(processid, "no"));
		}
		

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
			
			if (!(hasTaskState(processes, "crudGradeComponent"))
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

    @Route(url = "call/mapSubCloToClo")
    public Map<String, Object> mapSubCloToClo(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "crudSubClo"))
			) {
				res.put("status", "fail");
				res.put("message", "mapSubCloToClo DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "mapSubCloToClo SUCCESS");

		clo_subcloService.mapSubCloToClo(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/crudSubClo")
    public Map<String, Object> crudSubClo(VMJExchange vmjExchange) {
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
				res.put("message", "crudSubClo DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "crudSubClo SUCCESS");

		clo_subcloService.crudSubClo(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/crudGradeComponent")
    public Map<String, Object> crudGradeComponent(VMJExchange vmjExchange) {
        Map<String, Object> res = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

		String processid = (String) requestBody.get("processInstanceId");
		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState(processes, "no") || 
			    hasTaskState(processes, "mapSubCloToClo"))
			) {
				res.put("status", "fail");
				res.put("message", "crudGradeComponent DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "crudGradeComponent SUCCESS");

		clo_subcloService.crudGradeComponent(requestBody, processid);
		

        return res;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

// @generated from Paymentstudyplan.bpmn2

package bpmn.product.Paymentstudyplan;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;

public class PaymentstudyplanResourceImpl extends PaymentstudyplanComponent {

    ...
    
	private PaymentstudyplanService paymentstudyplanService = new PaymentstudyplanServiceImpl();

    @Route(url = "call/confirmStudyPlan")
    public Map<String, Object> confirmStudyPlan(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState("makePayment")))
			) {
				res.put("status", "fail");
				res.put("message", "confirmStudyPlan DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "confirmStudyPlan SUCCESS");

		paymentstudyplanService.confirmStudyPlan(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/createStudyPlan")
    public Map<String, Object> createStudyPlan(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState("confirmStudyPlan")))
			) {
				res.put("status", "fail");
				res.put("message", "createStudyPlan DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "createStudyPlan SUCCESS");

		paymentstudyplanService.createStudyPlan(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/viewStudyPlanAndPaymentStatus")
    public Map<String, Object> viewStudyPlanAndPaymentStatus(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState("createStudyPlan")))
			) {
				res.put("status", "fail");
				res.put("message", "viewStudyPlanAndPaymentStatus DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "viewStudyPlanAndPaymentStatus SUCCESS");

		paymentstudyplanService.viewStudyPlanAndPaymentStatus(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/viewCourseSchedule")
    public Map<String, Object> viewCourseSchedule(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

		if (vmjExchange.getHttpMethod().equals("POST")) {

	        // Cek apakah step sebelumnya pernah dilakukan
	        // This also allows user yang mundur page trus isi form ulang
	        // karena langkah sebelum page ini pasti udh dilakukan
	        // ini juga mencegah orang dari asal tembak api
	        List<ProcessInstance> processes = processService.getAllById(processid);
			
			if (!(hasTaskState("viewStudyPlanAndPaymentStatus")))
			) {
				res.put("status", "fail");
				res.put("message", "viewCourseSchedule DENIED");
            	return res;
			}
		}

        res.put("status", "ok");
        res.put("message", "viewCourseSchedule SUCCESS");

		paymentstudyplanService.viewCourseSchedule(requestBody, processid);
		

        return res;
    }

    @Route(url = "call/makePayment")
    public Map<String, Object> makePayment(VMJExchange exchange) {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> res = new HashMap<>();
        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "%s"));

        res.put("status", "ok");
        res.put("message", "makePayment SUCCESS");

		paymentstudyplanService.makePayment(requestBody, processid);
		

        return res;
    }

    ...
}

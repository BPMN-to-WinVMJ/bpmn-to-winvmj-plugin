// @generated from Bankaccount.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.routing.route.Route;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.BPMN;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.Component;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.FlowComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.NonStructuredComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.PickComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.RepeatComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.SequenceComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.SwitchComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.Variable;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.WhileComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.WhileRepeatComponent;

public class BankaccountResourceImpl extends BankaccountResourceComponent {

    AccountService accountService;
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

    static interface BankaccountService {
        void get3highest(Map<String, Object> body, String processid, Map<String, Object> response);
        void queryhighestsaver(Map<String, Object> body, String processid, Map<String, Object> response);

    }

    static class BankaccountServiceImpl implements BankaccountService {
	    @Override
	    public void get3highest(Map<String, Object> body, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for get3Highest
			processService.upsert(new ProcessInstance(processid, "get3highest"));
	        System.out.println("Executing get3Highest");
			boolean get = false;
			Account temp = null;
			List<Account> accounts = new ArrayList<>();
			int i = -1;
			int j = -1;
			int n = -1;
			// From ScriptTask List<Account> accounts = accountservice.getAllAccount();
			accounts = accountservice.getAllAccount();
			
			// From ScriptTask assign int n as accounts.size();
			n = accounts.size();;
			
			// From ScriptTask create int i as 0
			i = 0;
			
			while (i < n - 1) {
			    if (i < n - 1) {
			        // From ScriptTask init int j as 0
			        j = 0;
			
			        while (j < n -1) {
			            if (j < n -1) {
			                if (accounts.get(i) < accounts.get(j)) {
			                    processService.upsert(new ProcessInstance(processid, "accounts.get(i) < accounts.get(j)"));
			                    // From ScriptTask init Account temp = accounts.get(j)
			                    temp = accounts.get(j);
			
			                    // From ScriptTask accounts.set(j, accounts.get(j+1))
			                    accounts.set(j, accounts.get(j+1));
			
			                    // From ScriptTask accounts.set(j+1, temp)
			                    accounts.set(j+1, temp);
			
			                }
			            }
			            // From ScriptTask init int j as 0
			            j = 0;
			
			            if (null) { processService.upsert(new ProcessInstance(processid, "null")); break; }
			        }
			
			    }
			    // From ScriptTask create int i as 0
			    i = 0;
			
			    if (null) { processService.upsert(new ProcessInstance(processid, "null")); break; }
			}// From ScriptTask add accounts.get(n-1) to res
			response.put("accounts.get(n-1)", accounts.get(n-1));
			
			// From ScriptTask add accounts.get(n-2) to res
			response.put("accounts.get(n-2)", accounts.get(n-2));
			
			// From ScriptTask add accounts.get(n-3) to res
			response.put("accounts.get(n-3)", accounts.get(n-3));
	    }

	    @Override
	    public void queryhighestsaver(Map<String, Object> body, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for queryhighestsaver
			processService.upsert(new ProcessInstance(processid, "queryhighestsaver"));
	        System.out.println("Executing queryhighestsaver");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static BankaccountService bankaccountService = new BankaccountServiceImpl();

    @Route(url = "call/queryhighestsaver")
    public Map<String, Object> queryhighestsaver(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "queryhighestsaver"));
		response.put("processid", processid);
		

        response.put("status", "ok");
        response.put("message", "queryhighestsaver SUCCESS");

		bankaccountService.queryhighestsaver(requestBody, processid, response);
		bankaccountService.get3highest(requestBody, processid, response);

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

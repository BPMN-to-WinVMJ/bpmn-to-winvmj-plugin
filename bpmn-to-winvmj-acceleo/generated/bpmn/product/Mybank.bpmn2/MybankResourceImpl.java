// @generated from Mybank.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class MybankResourceImpl extends MybankResourceComponent {

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

    static interface MybankService {
        void transfer(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class MybankServiceImpl implements MybankService {
	    @Override
	    public void transfer(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for transfer
			processService.upsert(new ProcessInstance(processid, "transfer"));
	        System.out.println("Executing transfer");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static MybankService mybankService = new MybankServiceImpl();

    @Route(url = "call/transfer")
    public Map<String, Object> transfer(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();
        String currency = (String) requestBody.get("currency");
        String targetId = (String) requestBody.get("targetId");
        String id_account = (String) requestBody.get("id_account");
        Integer amount = (Integer) requestBody.get("amount");
        

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "transfer"));
		response.put("processid", processid);

		mybankService.transfer(requestBody, processid, response);
		CompletableFuture<Void> all = null;
		List<ProcessInstance> processes = new ArrayList<>();
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		HashMap<String, Object> account = null;
		// From ScriptTask init int balance as (int) accountAccount2Service.getAccount(requestBody.get("id_account").toString()).get("balance")
		processService.upsert(new ProcessInstance(processid, "init int balance as (int) accountAccount2Service.getAccount(requestBody.get(id_account).toString()).get(balance)"));
		int balance = (int) accountAccount2Service.getAccount(requestBody.get("id_account").toString()).get("balance");
		
		// From ScriptTask init int dailyLimit as (int) dailylimitAccount2Service.getAccount(requestBody.get("id_account").toString()).get("dailyLimit")
		processService.upsert(new ProcessInstance(processid, "init int dailyLimit as (int) dailylimitAccount2Service.getAccount(requestBody.get(id_account).toString()).get(dailyLimit)"));
		int dailyLimit = (int) dailylimitAccount2Service.getAccount(requestBody.get("id_account").toString()).get("dailyLimit");
		
		// From ScriptTask init int withdraw as (int) dailylimitAccount2Service.getAccount(requestBody.get("id_account").toString()).get("withdraw")
		processService.upsert(new ProcessInstance(processid, "init int withdraw as (int) dailylimitAccount2Service.getAccount(requestBody.get(id_account).toString()).get(withdraw)"));
		int withdraw = (int) dailylimitAccount2Service.getAccount(requestBody.get("id_account").toString()).get("withdraw");
		
		// From ScriptTask init int finalAmount as amount
		processService.upsert(new ProcessInstance(processid, "init int finalAmount as amount"));
		int finalAmount = amount;
		
		// From ScriptTask init int finalBalance as balance
		processService.upsert(new ProcessInstance(processid, "init int finalBalance as balance"));
		int finalBalance = balance;
		
		CompletableFuture<Void> flowSequenceFlow_4 = CompletableFuture.runAsync(() -> {
		    if (finalAmount >= finalBalance) {
		        processService.upsert(new ProcessInstance(processid, "finalAmount >= finalBalance"));
		        // From ScriptTask add message "uang kurang" to res
		        processService.upsert(new ProcessInstance(processid, "add message uang kurang to res"));
		        response.put("message", "uang kurang");
		
		        // From ScriptTask put status "FAIL" to res
		        processService.upsert(new ProcessInstance(processid, "put status FAIL to res"));
		        response.put("status", "FAIL");
		
		    }
		    else if (finalAmount < finalBalance) {
		        processService.upsert(new ProcessInstance(processid, "finalAmount < finalBalance"));
		        // From ScriptTask init int accumulate as withdraw + finalAmount
		        processService.upsert(new ProcessInstance(processid, "init int accumulate as withdraw + finalAmount"));
		        int accumulate = withdraw + finalAmount;
		
		        if (accumulate > dailyLimit) {
		            processService.upsert(new ProcessInstance(processid, "accumulate > dailyLimit"));
		            // From ScriptTask add message "above daily limit"
		            processService.upsert(new ProcessInstance(processid, "add message above daily limit"));
		            // TODO: implement 'add message "above daily limit"'
		
		            // From ScriptTask put status "FAIL" to res
		            processService.upsert(new ProcessInstance(processid, "put status FAIL to res"));
		            response.put("status", "FAIL");
		
		        }
		    }
		});
		futures = List.of(flowSequenceFlow_4);
		all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		all.join();
		processes = processService.getAllById(processid);
		if (!(((hasTaskState(processes, "put status FAIL to res") || hasTaskState(processes, "init int accumulate as withdraw + finalAmount")) || hasTaskState(processes, "put status FAIL to res")))) {
		    response.put("status", "FAIL");
		    response.put("message", "Parallel branches not complete yet");
		    return response;
		}
		if (response.containsKey("status") || !response.get("status").equals("FAILED")) {
		    processService.upsert(new ProcessInstance(processid, "response.containsKey(status) || !response.get(status).equals(FAILED)"));
		    // From ScriptTask minus amount from balance
		    processService.upsert(new ProcessInstance(processid, "minus amount from balance"));
		    balance -= amount;
		
		    // From ScriptTask init double finalBalance2 as balance
		    processService.upsert(new ProcessInstance(processid, "init double finalBalance2 as balance"));
		    double finalBalance2 = balance;
		
		    // From ScriptTask init int finalAmount2 as amount
		    processService.upsert(new ProcessInstance(processid, "init int finalAmount2 as amount"));
		    int finalAmount2 = amount;
		
		    CompletableFuture<Void> flowSequenceFlow_86 = CompletableFuture.runAsync(() -> {
		        // From ScriptTask log finalBalance2
		        processService.upsert(new ProcessInstance(processid, "log finalBalance2"));
		        System.out.println(finalBalance2);
		
		    });
		    CompletableFuture<Void> flowSequenceFlow_87 = CompletableFuture.runAsync(() -> {
		        // From ScriptTask put finalBalance2 as String.valueOf(finalBalance) to requestBody
		        processService.upsert(new ProcessInstance(processid, "put finalBalance2 as String.valueOf(finalBalance) to requestBody"));
		        requestBody.put("finalBalance2", String.valueOf(finalBalance));
		
		        // From ScriptTask accountAccount2Service.updateAccount(requestBody)
		        processService.upsert(new ProcessInstance(processid, "accountAccount2Service.updateAccount(requestBody)"));
		        accountAccount2Service.updateAccount(requestBody);
		
		    });
		    CompletableFuture<Void> flowSequenceFlow_88 = CompletableFuture.runAsync(() -> {
		        if (currency.equals("IDR")) {
		            processService.upsert(new ProcessInstance(processid, "currency.equals(IDR)"));
		            // From ScriptTask init int converted as mult finalAmount2 with 1
		            processService.upsert(new ProcessInstance(processid, "init int converted as mult finalAmount2 with 1"));
		            int converted = finalAmount2 * 1;
		
		            // From ScriptTask put balance as finalBalance2 to res
		            processService.upsert(new ProcessInstance(processid, "put balance as finalBalance2 to res"));
		            response.put("balance", finalBalance2);
		
		            // From ScriptTask put finalAmount2 converted to res
		            processService.upsert(new ProcessInstance(processid, "put finalAmount2 converted to res"));
		            response.put("finalAmount2", converted);
		
		        }
		        else if (currency.equals("USD")) {
		            processService.upsert(new ProcessInstance(processid, "currency.equals(USD)"));
		            // From ScriptTask init int converted as mult finalAmount2 with 2
		            processService.upsert(new ProcessInstance(processid, "init int converted as mult finalAmount2 with 2"));
		            int converted = finalAmount2 * 2;
		
		            // From ScriptTask put balance as finalBalance2 to res
		            processService.upsert(new ProcessInstance(processid, "put balance as finalBalance2 to res"));
		            response.put("balance", finalBalance2);
		
		            // From ScriptTask put finalAmount converted to res
		            processService.upsert(new ProcessInstance(processid, "put finalAmount converted to res"));
		            response.put("finalAmount", converted);
		
		        }
		        else if (currency.equals("JPY")) {
		            processService.upsert(new ProcessInstance(processid, "currency.equals(JPY)"));
		            // From ScriptTask init int converted as mult finalAmount2 with 3
		            processService.upsert(new ProcessInstance(processid, "init int converted as mult finalAmount2 with 3"));
		            int converted = finalAmount2 * 3;
		
		            // From ScriptTask put balance as finalBalance2 to res
		            processService.upsert(new ProcessInstance(processid, "put balance as finalBalance2 to res"));
		            response.put("balance", finalBalance2);
		
		            // From ScriptTask put finalAmount converted to res
		            processService.upsert(new ProcessInstance(processid, "put finalAmount converted to res"));
		            response.put("finalAmount", converted);
		
		        }
		    });
		    futures = List.of(flowSequenceFlow_86, flowSequenceFlow_87, flowSequenceFlow_88);
		    all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		    all.join();
		    processes = processService.getAllById(processid);
		    if (!(hasTaskState(processes, "log finalBalance2") && 
		    hasTaskState(processes, "accountAccount2Service.updateAccount(requestBody)") && 
		    (hasTaskState(processes, "put finalAmount2 converted to res") || hasTaskState(processes, "put finalAmount converted to res") || hasTaskState(processes, "put finalAmount converted to res")))) {
		        response.put("status", "FAIL");
		        response.put("message", "Parallel branches not complete yet");
		        return response;
		    }
		    // From ScriptTask HashMap<String, Object> account = accountAccount2Service.getAccount(targetId)
		    processService.upsert(new ProcessInstance(processid, "HashMap<String, Object> account = accountAccount2Service.getAccount(targetId)"));
		    account = accountAccount2Service.getAccount(targetId);
		
		    if (currency.equals("IDR")) {
		        processService.upsert(new ProcessInstance(processid, "currency.equals(IDR)"));
		        // From ScriptTask mult amount with 1
		        processService.upsert(new ProcessInstance(processid, "mult amount with 1"));
		        amount *= 1;
		
		    }
		    else if (currency.equals ("USD")) {
		        processService.upsert(new ProcessInstance(processid, "currency.equals (USD)"));
		        // From ScriptTask mult amount with 2
		        processService.upsert(new ProcessInstance(processid, "mult amount with 2"));
		        amount *= 2;
		
		    }
		    else if (currency.equals ("JPY")) {
		        processService.upsert(new ProcessInstance(processid, "currency.equals (JPY)"));
		        // From ScriptTask mult amount with 3
		        processService.upsert(new ProcessInstance(processid, "mult amount with 3"));
		        amount *= 3;
		
		    }
		    // From ScriptTask add amount to balance
		    processService.upsert(new ProcessInstance(processid, "add amount to balance"));
		    balance += amount;
		
		    // From ScriptTask put balance to account
		    processService.upsert(new ProcessInstance(processid, "put balance to account"));
		    account.put("balance", balance);
		
		    // From ScriptTask accountAccount2Service.updateAccount(account)
		    processService.upsert(new ProcessInstance(processid, "accountAccount2Service.updateAccount(account)"));
		    accountAccount2Service.updateAccount(account);
		
		}
		

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

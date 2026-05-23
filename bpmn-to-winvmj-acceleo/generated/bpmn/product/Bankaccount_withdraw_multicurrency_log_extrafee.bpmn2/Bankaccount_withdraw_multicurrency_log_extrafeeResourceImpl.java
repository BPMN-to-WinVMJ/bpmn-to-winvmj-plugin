// @generated from Bankaccount_withdraw_multicurrency_log_extrafee.bpmn2

package .core.resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import id.ac.ui.cs.prices.winvmj.core.exceptions.*;
import id.ac.ui.cs.prices.winvmj.auth.annotations.Restricted;
import .core.service.AccountService;

public class Bankaccount_withdraw_multicurrency_log_extrafeeResourceImpl extends Bankaccount_withdraw_multicurrency_log_extrafeeResourceComponent {

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

    static interface Bankaccount_withdraw_multicurrency_log_extrafeeService {
        void validateLimit(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void depositTo(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void withdraw(Map<String, Object> requestBody, String processid, Map<String, Object> response);
        void transfer(Map<String, Object> requestBody, String processid, Map<String, Object> response);

    }

    static class Bankaccount_withdraw_multicurrency_log_extrafeeServiceImpl implements Bankaccount_withdraw_multicurrency_log_extrafeeService {
	    @Override
	    public void validateLimit(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for validate limit
			processService.upsert(new ProcessInstance(processid, "validateLimit"));
	        System.out.println("Executing validate limit");
			// From ScriptTask init int balance as (int) accountService.getAccount(requestBody).get("balance")
			processService.upsert(new ProcessInstance(processid, "init int balance as (int) accountService.getAccount(requestBody).get(balance)"));
			int balance = (int) accountService.getAccount(requestBody).get("balance");
			
			// From ScriptTask init double amount as (double) requestBody.get("amount")
			processService.upsert(new ProcessInstance(processid, "init double amount as (double) requestBody.get(amount)"));
			double amount = (double) requestBody.get("amount");
			
			if (amount > balance) {
			    processService.upsert(new ProcessInstance(processid, "amount > balance"));
			    // From ScriptTask add message "uang kurang" to res
			    processService.upsert(new ProcessInstance(processid, "add message uang kurang to res"));
			    response.put("message", "uang kurang");
			
			    // From ScriptTask put status "FAIL" to res
			    processService.upsert(new ProcessInstance(processid, "put status FAIL to res"));
			    response.put("status", "fail");
			
			}
	    }

	    @Override
	    public void depositTo(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for depositTo
			processService.upsert(new ProcessInstance(processid, "depositTo"));
	        System.out.println("Executing depositTo");
			HashMap<String, Object> account = null;
			// From ScriptTask init double amount as (double) requestBody.get("amount")
			processService.upsert(new ProcessInstance(processid, "init double amount as (double) requestBody.get(amount)"));
			double amount = (double) requestBody.get("amount");
			
			// From ScriptTask HashMap<String, Object> account = accountService.getAccountById((String) requestBody.get("targetUserId"))
			processService.upsert(new ProcessInstance(processid, "HashMap<String, Object> account = accountService.getAccountById((String) requestBody.get(targetUserId))"));
			account = accountService.getAccountById((String) requestBody.get("targetUserId"));
			
			// From ScriptTask init double balance as (int) account.get("balance")
			processService.upsert(new ProcessInstance(processid, "init double balance as (int) account.get(balance)"));
			double balance = (int) account.get("balance");
			
			// From ScriptTask init String currency as (String) requestBody.get("currency")
			processService.upsert(new ProcessInstance(processid, "init String currency as (String) requestBody.get(currency)"));
			String currency = (String) requestBody.get("currency");
			
			if (currency.equals("USD")) {
			    processService.upsert(new ProcessInstance(processid, "currency.equals(USD)"));
			    // From ScriptTask mult amount with 0.000058
			    processService.upsert(new ProcessInstance(processid, "mult amount with 0.000058"));
			    amount *= 0.000058;
			
			}
			else if (currency.equals("JPY")) {
			    processService.upsert(new ProcessInstance(processid, "currency.equals(JPY)"));
			    // From ScriptTask mult amount with 0.0090
			    processService.upsert(new ProcessInstance(processid, "mult amount with 0.0090"));
			    amount *= 0.0090;
			
			}
			else if (currency.equals("IDR")) {
			    processService.upsert(new ProcessInstance(processid, "currency.equals(IDR)"));
			    // From ScriptTask mult amount with 1
			    processService.upsert(new ProcessInstance(processid, "mult amount with 1"));
			    amount *= 1;
			
			}
			// From ScriptTask add amount to balance
			processService.upsert(new ProcessInstance(processid, "add amount to balance"));
			balance += amount;
			
			// From ScriptTask put balance to account
			processService.upsert(new ProcessInstance(processid, "put balance to account"));
			account.put("balance", balance);
			
			// From ScriptTask accountService.updateAccount(account)
			processService.upsert(new ProcessInstance(processid, "accountService.updateAccount(account)"));
			accountService.updateAccount(account);
	    }

	    @Override
	    public void withdraw(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for withdraw
			processService.upsert(new ProcessInstance(processid, "withdraw"));
	        System.out.println("Executing withdraw");
			// From ScriptTask init double balance as (double) accountService.getAccount(requestBody).get("balance")
			processService.upsert(new ProcessInstance(processid, "init double balance as (double) accountService.getAccount(requestBody).get(balance)"));
			double balance = (double) accountService.getAccount(requestBody).get("balance");
			
			// From ScriptTask init double amount as (double) requestBody.get("amount")
			processService.upsert(new ProcessInstance(processid, "init double amount as (double) requestBody.get(amount)"));
			double amount = (double) requestBody.get("amount");
			
			// From ScriptTask init String currency as (String) requestBody.get("currency")
			processService.upsert(new ProcessInstance(processid, "init String currency as (String) requestBody.get(currency)"));
			String currency = (String) requestBody.get("currency");
			
			// From ScriptTask minus amount from balance
			processService.upsert(new ProcessInstance(processid, "minus amount from balance"));
			balance -= amount;
			
			// From ScriptTask init int finalBalance as balance
			processService.upsert(new ProcessInstance(processid, "init int finalBalance as balance"));
			int finalBalance = balance;
			
			CompletableFuture<Void> flowSequenceFlow_4 = CompletableFuture.runAsync(() -> {
			    if (currency.equals("USD")) {
			        processService.upsert(new ProcessInstance(processid, "currency.equals(USD)"));
			        // From ScriptTask init int converted as mult amount with 0.000058
			        processService.upsert(new ProcessInstance(processid, "init int converted as mult amount with 0.000058"));
			        int converted = amount * 0.000058;
			
			        // From ScriptTask put balance to res
			        processService.upsert(new ProcessInstance(processid, "put balance to res"));
			        response.put("balance", balance);
			
			        // From ScriptTask put amount converted to res
			        processService.upsert(new ProcessInstance(processid, "put amount converted to res"));
			        response.put("amount", converted);
			
			    }
			    else if (currency.equals("JPY")) {
			        processService.upsert(new ProcessInstance(processid, "currency.equals(JPY)"));
			        // From ScriptTask init int converted as mult amount with 0.0090
			        processService.upsert(new ProcessInstance(processid, "init int converted as mult amount with 0.0090"));
			        int converted = amount * 0.0090;
			
			        // From ScriptTask put balance to res
			        processService.upsert(new ProcessInstance(processid, "put balance to res"));
			        response.put("balance", balance);
			
			        // From ScriptTask put amount as converted to res
			        processService.upsert(new ProcessInstance(processid, "put amount as converted to res"));
			        response.put("amount", converted);
			
			    }
			    else if (currency.equals("IDR")) {
			        processService.upsert(new ProcessInstance(processid, "currency.equals(IDR)"));
			        // From ScriptTask init int converted as mult amount with 1
			        processService.upsert(new ProcessInstance(processid, "init int converted as mult amount with 1"));
			        int converted = amount * 1;
			
			        // From ScriptTask put balance to res
			        processService.upsert(new ProcessInstance(processid, "put balance to res"));
			        response.put("balance", balance);
			
			        // From ScriptTask put amount converted to res
			        processService.upsert(new ProcessInstance(processid, "put amount converted to res"));
			        response.put("amount", converted);
			
			    }
			});
			CompletableFuture<Void> flowSequenceFlow_19 = CompletableFuture.runAsync(() -> {
			    // From ScriptTask put balance as String.valueof(balance) to requestBody
			    processService.upsert(new ProcessInstance(processid, "put balance as String.valueof(balance) to requestBody"));
			    requestbody.put("balance", string.valueof(balance));
			
			    // From ScriptTask accountService.updateAccount(requestBody)
			    processService.upsert(new ProcessInstance(processid, "accountService.updateAccount(requestBody)"));
			    accountService.updateAccount(requestBody);
			
			});
			CompletableFuture<Void> flowSequenceFlow_3 = CompletableFuture.runAsync(() -> {
			    // From ScriptTask log balance
			    processService.upsert(new ProcessInstance(processid, "log balance"));
			    System.out.println(balance);
			
			});
			List<CompletableFuture<Void>> futures = List.of(flowSequenceFlow_4, flowSequenceFlow_19, flowSequenceFlow_3);
			CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
			all.join();
			List<ProcessInstance> processes = processService.getAllById(processid);
			if (!(hasTaskState(processes, "log balance") && 
			    hasTaskState(processes, "accountService.updateAccount(requestBody)") && 
			    (hasTaskState(processes, "put amount converted to res") || hasTaskState(processes, "put amount converted to res") || hasTaskState(processes, "put amount as converted to res")))) {
			    response.put("status", "FAIL");
			    response.put("message", "Parallel branches not complete yet");
			}
	    }

	    @Override
	    public void transfer(Map<String, Object> requestBody, String processid, Map<String, Object> response) {
	        // TODO: Implement logic for transfer
			processService.upsert(new ProcessInstance(processid, "transfer"));
	        System.out.println("Executing transfer");
			
	    }


    }

    private static ProcessService processService = new ProcessServiceImpl();
	private static Bankaccount_withdraw_multicurrency_log_extrafeeService bankaccount_withdraw_multicurrency_log_extrafeeService = new Bankaccount_withdraw_multicurrency_log_extrafeeServiceImpl();

    @Route(url = "call/transfer")
    public Map<String, Object> transfer(VMJExchange vmjExchange) {
        Map<String, Object> response = new HashMap<>();
		Map<String, Object> requestBody = vmjExchange.getPayload();

        String processid = UUID.randomUUID().toString();
        processService.upsert(new ProcessInstance(processid, "transfer"));
		response.put("processid", processid);
		String currency = (String) requestBody.get("currency");
		String id_account = (String) requestBody.get("id_account");
		Double amount = (Double) requestBody.get("amount");
		String targetUserId = (String) requestBody.get("targetUserId");
		

        response.put("status", "ok");
        response.put("message", "transfer SUCCESS");

		bankaccount_withdraw_multicurrency_log_extrafeeService.transfer(requestBody, processid, response);
		bankaccount_withdraw_multicurrency_log_extrafeeService.validateLimit(requestBody, processid, response);
		if (response.containsKey("status") || !response.get("status").equals("FAILED")) {
		    processService.upsert(new ProcessInstance(processid, "response.containsKey(status) || !response.get(status).equals(FAILED)"));
		    bankaccount_withdraw_multicurrency_log_extrafeeService.withdraw(requestBody, processid, response);
		    bankaccount_withdraw_multicurrency_log_extrafeeService.depositTo(requestBody, processid, response);
		}

        return response;
    }

	private static boolean hasTaskState(List<ProcessInstance> processes, String... states) {
	    return processes.stream()
	        .anyMatch(x -> Arrays.stream(states)
	            .anyMatch(state -> x.state.equalsIgnoreCase(state)));
	}
}

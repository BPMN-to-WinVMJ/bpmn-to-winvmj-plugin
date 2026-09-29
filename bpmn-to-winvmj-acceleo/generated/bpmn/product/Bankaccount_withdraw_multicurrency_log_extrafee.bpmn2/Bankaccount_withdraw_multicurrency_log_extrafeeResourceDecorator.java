// @generated from Bankaccount_withdraw_multicurrency_log_extrafee.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class Bankaccount_withdraw_multicurrency_log_extrafeeResourceDecorator extends Bankaccount_withdraw_multicurrency_log_extrafeeResourceComponent {
	protected Bankaccount_withdraw_multicurrency_log_extrafeeResourceComponent record;
	
	public Bankaccount_withdraw_multicurrency_log_extrafeeResourceDecorator(Bankaccount_withdraw_multicurrency_log_extrafeeResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> transfer(VMJExchange vmjExchange) {
		return record.transfer(vmjExchange);
	}
}

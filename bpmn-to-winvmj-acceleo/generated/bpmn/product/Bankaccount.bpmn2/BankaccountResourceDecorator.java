// @generated from Bankaccount.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class BankaccountResourceDecorator extends BankaccountResourceComponent {
	protected BankaccountResourceComponent record;
	
	public BankaccountResourceDecorator(BankaccountResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> queryhighestsaver(VMJExchange vmjExchange) {
		return record.queryhighestsaver(vmjExchange);
	}
}

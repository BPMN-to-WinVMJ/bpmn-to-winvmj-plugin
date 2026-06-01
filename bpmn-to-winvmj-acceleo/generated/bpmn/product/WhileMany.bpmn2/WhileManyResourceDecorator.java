// @generated from WhileMany.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class WhileManyResourceDecorator extends WhileManyResourceComponent {
	protected WhileManyResourceComponent record;
	
	public WhileManyResourceDecorator(WhileManyResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> task2(VMJExchange vmjExchange) {
		return record.task2(vmjExchange);
	}
    public Map<String, Object> task3(VMJExchange vmjExchange) {
		return record.task3(vmjExchange);
	}
    public Map<String, Object> task1(VMJExchange vmjExchange) {
		return record.task1(vmjExchange);
	}
}

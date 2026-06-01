// @generated from While.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class WhileResourceDecorator extends WhileResourceComponent {
	protected WhileResourceComponent record;
	
	public WhileResourceDecorator(WhileResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> userTask2(VMJExchange vmjExchange) {
		return record.userTask2(vmjExchange);
	}
    public Map<String, Object> userTask1(VMJExchange vmjExchange) {
		return record.userTask1(vmjExchange);
	}
    public Map<String, Object> userTask3(VMJExchange vmjExchange) {
		return record.userTask3(vmjExchange);
	}
}

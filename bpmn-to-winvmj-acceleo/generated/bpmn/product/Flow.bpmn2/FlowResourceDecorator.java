// @generated from Flow.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class FlowResourceDecorator extends FlowResourceComponent {
	protected FlowResourceComponent record;
	
	public FlowResourceDecorator(FlowResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> userTask2(VMJExchange vmjExchange) {
		return record.userTask2(vmjExchange);
	}
}

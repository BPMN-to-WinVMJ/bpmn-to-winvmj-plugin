// @generated from Sequence.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class SequenceResourceDecorator extends SequenceResourceComponent {
	protected SequenceResourceComponent record;
	
	public SequenceResourceDecorator(SequenceResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> userTask1(VMJExchange vmjExchange) {
		return record.userTask1(vmjExchange);
	}
}

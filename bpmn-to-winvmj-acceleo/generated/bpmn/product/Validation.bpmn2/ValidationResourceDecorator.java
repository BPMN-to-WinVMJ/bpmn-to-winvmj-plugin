// @generated from Validation.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class ValidationResourceDecorator extends ValidationResourceComponent {
	protected ValidationResourceComponent record;
	
	public ValidationResourceDecorator(ValidationResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> validation(VMJExchange vmjExchange) {
		return record.validation(vmjExchange);
	}
}

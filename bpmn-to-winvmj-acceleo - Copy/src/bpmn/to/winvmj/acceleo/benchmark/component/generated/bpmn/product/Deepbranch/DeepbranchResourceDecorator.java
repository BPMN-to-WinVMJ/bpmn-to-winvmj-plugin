// @generated from Deepbranch.bpmn2

package bpmn.product.Deepbranch;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class DeepbranchResourceDecorator extends DeepbranchResourceComponent {
	protected DeepbranchResourceComponent record;
	
	public DeepbranchResourceDecorator(DeepbranchResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> userTask4(VMJExchange vmjExchange) {
		return record.userTask4(vmjExchange);
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

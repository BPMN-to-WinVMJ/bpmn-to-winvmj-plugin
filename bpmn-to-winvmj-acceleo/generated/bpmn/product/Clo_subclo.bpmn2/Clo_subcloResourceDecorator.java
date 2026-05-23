// @generated from Clo_subclo.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class Clo_subcloResourceDecorator extends Clo_subcloResourceComponent {
	protected Clo_subcloResourceComponent record;
	
	public Clo_subcloResourceDecorator(Clo_subcloResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> manageGradeComponent(VMJExchange vmjExchange) {
		return record.manageGradeComponent(vmjExchange);
	}
    public Map<String, Object> entryComponentGrades(VMJExchange vmjExchange) {
		return record.entryComponentGrades(vmjExchange);
	}
    public Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange) {
		return record.mapGradeComponentsToCloAndSubClo(vmjExchange);
	}
    public Map<String, Object> manageSubClo(VMJExchange vmjExchange) {
		return record.manageSubClo(vmjExchange);
	}
}

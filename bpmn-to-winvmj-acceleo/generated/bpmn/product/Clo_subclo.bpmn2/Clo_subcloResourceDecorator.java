// @generated from Clo_subclo.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Clo_subcloResourceDecorator extends Clo_subcloResourceComponent {
	protected Clo_subcloResourceComponent record;
	
	public Clo_subcloResourceDecorator(Clo_subcloResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> mapSubCloToClo(VMJExchange vmjExchange) {
		return record.mapSubCloToClo(vmjExchange);
	}
    public Map<String, Object> entryComponentGrades(VMJExchange vmjExchange) {
		return record.entryComponentGrades(vmjExchange);
	}
    public Map<String, Object> starter(VMJExchange vmjExchange) {
		return record.starter(vmjExchange);
	}
    public Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange) {
		return record.mapGradeComponentsToCloAndSubClo(vmjExchange);
	}
    public Map<String, Object> crudGradeComponent(VMJExchange vmjExchange) {
		return record.crudGradeComponent(vmjExchange);
	}
    public Map<String, Object> crudSubClo(VMJExchange vmjExchange) {
		return record.crudSubClo(vmjExchange);
	}
}

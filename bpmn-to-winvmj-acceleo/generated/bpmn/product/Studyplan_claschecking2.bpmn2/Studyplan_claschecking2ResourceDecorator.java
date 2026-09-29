// @generated from Studyplan_claschecking2.bpmn2

package ;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Studyplan_claschecking2ResourceDecorator extends Studyplan_claschecking2ResourceComponent {
	protected Studyplan_claschecking2ResourceComponent record;
	
	public Studyplan_claschecking2ResourceDecorator(Studyplan_claschecking2ResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> viewValidFormNotificationAndWaitingForApproval(VMJExchange vmjExchange) {
		return record.viewValidFormNotificationAndWaitingForApproval(vmjExchange);
	}
    public Map<String, Object> fillAndSubmitTheStudyForm(VMJExchange vmjExchange) {
		return record.fillAndSubmitTheStudyForm(vmjExchange);
	}
    public Map<String, Object> viewWaitingForApproval(VMJExchange vmjExchange) {
		return record.viewWaitingForApproval(vmjExchange);
	}
}

// @generated from Studyplan_claschecking.bpmn2

package bpmn.product.Studyplan_claschecking;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Studyplan_clascheckingResourceDecorator extends Studyplan_clascheckingResourceComponent {
	protected Studyplan_clascheckingResourceComponent record;
	
	public Studyplan_clascheckingResourceDecorator(Studyplan_clascheckingResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> viewInvalidFormNotification(VMJExchange vmjExchange) {
		return record.viewInvalidFormNotification(vmjExchange);
	}
    public Map<String, Object> viewWaitingForApproval(VMJExchange vmjExchange) {
		return record.viewWaitingForApproval(vmjExchange);
	}
    public Map<String, Object> fillAndSubmitTheStudyForm(VMJExchange vmjExchange) {
		return record.fillAndSubmitTheStudyForm(vmjExchange);
	}
    public Map<String, Object> viewValidFormNotificationAndWaitingForApproval(VMJExchange vmjExchange) {
		return record.viewValidFormNotificationAndWaitingForApproval(vmjExchange);
	}
}

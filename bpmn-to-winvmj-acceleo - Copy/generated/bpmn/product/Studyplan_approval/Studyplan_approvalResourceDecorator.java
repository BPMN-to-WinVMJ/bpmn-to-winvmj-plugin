// @generated from Studyplan_approval.bpmn2

package bpmn.product.Studyplan_approval;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Studyplan_approvalResourceDecorator extends Studyplan_approvalResourceComponent {
	protected Studyplan_approvalResourceComponent record;
	
	public Studyplan_approvalResourceDecorator(Studyplan_approvalResourceComponent record) {
        this.record = record;
    }

    public Map<String, Object> approveTheStudyPlan(VMJExchange vmjExchange) {
		return record.approveTheStudyPlan(vmjExchange);
	}
    public Map<String, Object> reviewTheStudyPlan(VMJExchange vmjExchange) {
		return record.reviewTheStudyPlan(vmjExchange);
	}
    public Map<String, Object> rejectStudyPlan(VMJExchange vmjExchange) {
		return record.rejectStudyPlan(vmjExchange);
	}
}

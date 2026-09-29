// @generated from Studyplan_approval.bpmn2

package bpmn.product.Studyplan_approval;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Studyplan_approvalResourceComponent implements Studyplan_approvalResource {

    public abstract Map<String, Object> approveTheStudyPlan(VMJExchange vmjExchange);
    public abstract Map<String, Object> reviewTheStudyPlan(VMJExchange vmjExchange);
    public abstract Map<String, Object> rejectStudyPlan(VMJExchange vmjExchange);
}

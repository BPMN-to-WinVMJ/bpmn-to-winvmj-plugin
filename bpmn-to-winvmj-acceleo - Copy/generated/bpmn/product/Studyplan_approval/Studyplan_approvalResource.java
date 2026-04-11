// @generated from Studyplan_approval.bpmn2

package bpmn.product.Studyplan_approval;

import java.util.*;
import vmj.routing.route.VMJExchange;

public interface Studyplan_approvalResource {
    Map<String, Object> approveTheStudyPlan(VMJExchange vmjExchange);
    Map<String, Object> reviewTheStudyPlan(VMJExchange vmjExchange);
    Map<String, Object> rejectStudyPlan(VMJExchange vmjExchange);
}

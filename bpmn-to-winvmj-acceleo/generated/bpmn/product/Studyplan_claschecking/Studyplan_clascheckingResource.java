// @generated from Studyplan_claschecking.bpmn2

package bpmn.product.Studyplan_claschecking;

import java.util.*;
import vmj.routing.route.VMJExchange;

public interface Studyplan_clascheckingResource {
    Map<String, Object> viewInvalidFormNotification(VMJExchange vmjExchange);
    Map<String, Object> viewWaitingForApproval(VMJExchange vmjExchange);
    Map<String, Object> fillAndSubmitTheStudyForm(VMJExchange vmjExchange);
    Map<String, Object> viewValidFormNotificationAndWaitingForApproval(VMJExchange vmjExchange);
}

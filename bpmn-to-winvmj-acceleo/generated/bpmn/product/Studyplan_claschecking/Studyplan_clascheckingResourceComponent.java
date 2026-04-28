// @generated from Studyplan_claschecking.bpmn2

package bpmn.product.Studyplan_claschecking;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Studyplan_clascheckingResourceComponent implements Studyplan_clascheckingResource {

    public abstract Map<String, Object> viewInvalidFormNotification(VMJExchange vmjExchange);
    public abstract Map<String, Object> viewWaitingForApproval(VMJExchange vmjExchange);
    public abstract Map<String, Object> fillAndSubmitTheStudyForm(VMJExchange vmjExchange);
    public abstract Map<String, Object> viewValidFormNotificationAndWaitingForApproval(VMJExchange vmjExchange);
}

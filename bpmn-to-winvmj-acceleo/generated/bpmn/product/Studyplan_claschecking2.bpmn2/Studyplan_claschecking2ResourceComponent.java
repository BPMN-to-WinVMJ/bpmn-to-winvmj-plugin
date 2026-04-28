// @generated from Studyplan_claschecking2.bpmn2

package ;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Studyplan_claschecking2ResourceComponent implements Studyplan_claschecking2Resource {

    public abstract Map<String, Object> viewValidFormNotificationAndWaitingForApproval(VMJExchange vmjExchange);
    public abstract Map<String, Object> fillAndSubmitTheStudyForm(VMJExchange vmjExchange);
    public abstract Map<String, Object> viewWaitingForApproval(VMJExchange vmjExchange);
}

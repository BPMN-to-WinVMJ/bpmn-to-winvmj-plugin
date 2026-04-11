// @generated from Clo_subclo.bpmn2

package bpmn.product.Clo_subclo;

import java.util.*;
import vmj.routing.route.VMJExchange;
import vmj.hibernate.integrator.RepositoryUtil;

public abstract class Clo_subcloResourceComponent implements Clo_subcloResource {

    public abstract Map<String, Object> starter(VMJExchange vmjExchange);
    public abstract Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange);
    public abstract Map<String, Object> entryComponentGrades(VMJExchange vmjExchange);
    public abstract Map<String, Object> mapSubCloToClo(VMJExchange vmjExchange);
    public abstract Map<String, Object> crudSubClo(VMJExchange vmjExchange);
    public abstract Map<String, Object> crudGradeComponent(VMJExchange vmjExchange);
}

// @generated from Clo_subclo.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;

public interface Clo_subcloResource {
    Map<String, Object> mapSubCloToClo(VMJExchange vmjExchange);
    Map<String, Object> entryComponentGrades(VMJExchange vmjExchange);
    Map<String, Object> starter(VMJExchange vmjExchange);
    Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange);
    Map<String, Object> crudGradeComponent(VMJExchange vmjExchange);
    Map<String, Object> crudSubClo(VMJExchange vmjExchange);
}

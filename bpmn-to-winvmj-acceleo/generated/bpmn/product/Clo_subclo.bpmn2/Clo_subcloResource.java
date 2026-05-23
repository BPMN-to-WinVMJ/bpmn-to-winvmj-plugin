// @generated from Clo_subclo.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public interface Clo_subcloResource {
    Map<String, Object> manageGradeComponent(VMJExchange vmjExchange);
    Map<String, Object> entryComponentGrades(VMJExchange vmjExchange);
    Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange);
    Map<String, Object> manageSubClo(VMJExchange vmjExchange);
}

// @generated from Clo_subclo.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class Clo_subcloResourceComponent implements Clo_subcloResource {

    public abstract Map<String, Object> manageGradeComponent(VMJExchange vmjExchange);
    public abstract Map<String, Object> entryComponentGrades(VMJExchange vmjExchange);
    public abstract Map<String, Object> mapGradeComponentsToCloAndSubClo(VMJExchange vmjExchange);
    public abstract Map<String, Object> manageSubClo(VMJExchange vmjExchange);
}

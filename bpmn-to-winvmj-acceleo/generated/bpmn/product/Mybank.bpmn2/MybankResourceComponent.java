// @generated from Mybank.bpmn2

package .core.resource;

import java.util.*;
import id.ac.ui.cs.prices.winvmj.core.Route;
import id.ac.ui.cs.prices.winvmj.core.VMJExchange;

public abstract class MybankResourceComponent implements MybankResource {

    public abstract Map<String, Object> transfer(VMJExchange vmjExchange);
}

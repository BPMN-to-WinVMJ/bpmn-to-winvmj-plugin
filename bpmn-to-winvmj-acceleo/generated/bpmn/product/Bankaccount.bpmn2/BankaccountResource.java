// @generated from Bankaccount.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.BPMN;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.Component;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.FlowComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.NonStructuredComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.PickComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.RepeatComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.SequenceComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.SwitchComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.Variable;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.WhileComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.model.WhileRepeatComponent;

public interface BankaccountResource {
    Map<String, Object> queryhighestsaver(VMJExchange vmjExchange);
}

// @generated from Bankaccount.bpmn2

package .core;

import java.util.*;
import vmj.routing.route.VMJExchange;
import bpmn.to.winvmj.acceleo.model.BPMN;
import bpmn.to.winvmj.acceleo.model.Component;
import bpmn.to.winvmj.acceleo.model.FlowComponent;
import bpmn.to.winvmj.acceleo.model.NonStructuredComponent;
import bpmn.to.winvmj.acceleo.model.PickComponent;
import bpmn.to.winvmj.acceleo.model.RepeatComponent;
import bpmn.to.winvmj.acceleo.model.SequenceComponent;
import bpmn.to.winvmj.acceleo.model.SwitchComponent;
import bpmn.to.winvmj.acceleo.model.Variable;
import bpmn.to.winvmj.acceleo.model.WhileComponent;
import bpmn.to.winvmj.acceleo.model.WhileRepeatComponent;

public interface BankaccountResource {
    Map<String, Object> queryhighestsaver(VMJExchange vmjExchange);
}

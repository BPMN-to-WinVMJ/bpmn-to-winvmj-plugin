package bpmn.to.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.Util;

public class SwitchComponent extends Component {

	@Override
	public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();

        for (SequenceFlow f : getStart().getOutgoing()) {
            if (GenerateQuery.canContinueFrom(f.getTargetRef(), visited, this.getEnd())) {
                return true; // at least one branch can continue
            }
        }

        // all branches are blocked
        return false;
	}

    @Override
    public String getFromStartToUser(String bpmnName, Map<String, String> usedVariables, int indent) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = getStart().getOutgoing();
        boolean first = true;

        for (SequenceFlow f : outs) {

        	Set<String> variables = Util.extractVariablesFromExpression(f.getName());
        	for (String var : variables) {
        		String varType = Util.inferTypeFromVariable(var, f.getName());
        		usedVariables.put(var, varType); 
        	}
        	
            if (first || (GatewayType.INCLUSIVE_GATEWAY.equals(((GatewayWrapper)this.getStart()).getGatewayType()))) {
                builder.append(Util.SPACE.repeat(indent) + String.format("if (%s) {\n", f.getName()));
                first = false;
            } else {
                builder.append(Util.SPACE.repeat(indent) + String.format("else if (%s) {\n", f.getName()));
            }
            
            StringBuilder builderTemp = new StringBuilder();
            GenerateQuery.buildResource(builderTemp, bpmnName, f.getTargetRef(), visited, usedVariables, indent + 1);

        	builder.append(Util.SPACE.repeat(indent + 1) + String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", f.getName()));
            builder.append(builderTemp.toString());
            builder.append(Util.SPACE.repeat(indent) + "}\n");
        }

        return builder.toString();
    }
}

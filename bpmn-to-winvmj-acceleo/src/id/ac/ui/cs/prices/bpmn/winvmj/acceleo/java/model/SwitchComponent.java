package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.GenerateQuery;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.Util;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.GatewayType;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.GatewayWrapper;

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
    public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent, boolean isProcess) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = getStart().getOutgoing();
        boolean first = true;
        
        boolean isInclusive = GatewayType.INCLUSIVE_GATEWAY.equals(((GatewayWrapper)this.getStart()).getGatewayType());

        boolean canContinueInclusive = true;
        
    	for (SequenceFlow f : outs) {
        	Set<String> variables = Util.extractVariablesFromExpression(f.getName());
        	for (String var : variables) {
        		String varType = Util.inferTypeFromVariable(var, f.getName());
        		usedVariables.add(new Variable(var, varType)); 
        	}
        	
            StringBuilder builderTemp = new StringBuilder();
            canContinueInclusive &= GenerateQuery.buildStraightLine(builderTemp, bpmnName, f.getTargetRef(), visited, usedVariables, indent + 1, isProcess);

            if ((first || isInclusive) && !builderTemp.isEmpty()) {
                builder.append(Util.SPACE.repeat(indent) + String.format("if (%s) {\r\n", f.getName()));
                if (isInclusive) builder.append(Util.SPACE.repeat(indent + 1) + "boolean canContinue = true;\r\n");
                first = false;
            } else if (!builderTemp.isEmpty()){
                builder.append(Util.SPACE.repeat(indent) + String.format("else if (%s) {\r\n", f.getName()));
            }
            
            if (!builderTemp.isEmpty()) {
            	builder.append(Util.SPACE.repeat(indent + 1) + String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", Util.removeWeirdChar(f.getName() == null ? "" : f.getName())));
                builder.append(builderTemp.toString());
                builder.append(Util.SPACE.repeat(indent) + "}\r\n");
            }

        }
        
        return new FromStartToUserResult(builder.toString(), canContinueInclusive);
    }
}

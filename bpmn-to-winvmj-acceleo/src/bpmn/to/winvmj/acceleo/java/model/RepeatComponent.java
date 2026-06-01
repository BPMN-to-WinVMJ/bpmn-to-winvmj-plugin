 package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.Util;
import bpmn.to.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;

public class RepeatComponent extends Component {

    @Override
    public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();
        return GenerateQuery.canContinueFrom(getStart().getOutgoing().get(0).getTargetRef(), visited, this.getEnd());
    }
    
    @Override
    public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent, boolean isProcess) {
    	StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();
        
        List<String> branches = new ArrayList<>();
        
        Component parent = this;
        // parent == null should never happen. If it does happen, there are bigger problems at hands
        while (parent != null && parent.getOutgoing().isEmpty()) {
        	parent = parent.getOwnerComponent();
        }
        List<String> exitBranch = parent.getOutgoing().stream().filter(x -> x.getName() != null).map(x -> x.getName()).toList();
        branches.addAll(exitBranch);
        
    	String loopCondition = this.getEnd().getOutgoing().stream().filter(x -> x.getName() != null).map(SequenceFlow::getName).collect(Collectors.joining(" || "));
    	branches.addAll(this.getEnd().getOutgoing().stream().map(SequenceFlow::getName).toList());
    	
        for (String expression : branches) {
        	Set<String> variables = Util.extractVariablesFromExpression(expression);
        	for (String var : variables) {
        		String varType = Util.inferTypeFromVariable(var, expression);
        		usedVariables.add(new Variable(var, varType)); 
        	}
        }
    	
        if (this.canContinue()) {
        	builder.append(Util.SPACE.repeat(indent) + "do {\r\n");
        	GenerateQuery.buildStraightLine(builder, bpmnName, this.start.getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent + 1, isProcess);
        	
        	if (!exitBranch.isEmpty()) {
            	String joined = exitBranch.stream().collect(Collectors.joining(" || "));
            	builder.append(Util.SPACE.repeat(indent + 1) + String.format("if (%s) { processService.upsert(new ProcessInstance(processid, \"%s\")); break; }\r\n", joined, Util.removeWeirdChar(joined)));
        	}
        	builder.append(Util.SPACE.repeat(indent) + String.format("} while (%s);\n", loopCondition));

	        if (exitBranch.isEmpty()) {
		        builder.append(Util.SPACE.repeat(indent) + String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));", Util.removeWeirdChar(this.getName())));
	        }
	        
        	return new FromStartToUserResult(builder.toString(), true);
        } else {
        	GenerateQuery.buildStraightLine(builder, bpmnName, this.start.getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent, isProcess);
        	return new FromStartToUserResult(builder.toString(), false);
        }
    }
}

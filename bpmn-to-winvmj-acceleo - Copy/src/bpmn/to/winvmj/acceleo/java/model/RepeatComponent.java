 package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.Util;

public class RepeatComponent extends Component {

    @Override
    public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();
        return GenerateQuery.canContinueFrom(getStart().getOutgoing().get(0).getTargetRef(), visited, this.getEnd());
    }
    
    @Override
    public String getFromStartToUser(String bpmnName, Map<String, String> usedVariables, int indent) {
    	StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();
        
        List<String> branches = new ArrayList<>();
        
        Component parent = this;
        // parent == null should never happen. If it does happen, there are bigger problems at hands
        while (parent != null && parent.getOutgoing().isEmpty()) {
        	parent = parent.getOwnerComponent();
        }
        List<String> exitBranch = parent.getOutgoing().stream().map(x -> x.getName()).toList();
        branches.addAll(exitBranch);
        
    	String loopCondition = this.getEnd().getOutgoing().stream().map(SequenceFlow::getName).collect(Collectors.joining(" || "));
    	branches.addAll(this.getEnd().getOutgoing().stream().map(SequenceFlow::getName).toList());
    	
        for (String expression : branches) {
        	Set<String> variables = Util.extractVariablesFromExpression(expression);
        	for (String var : variables) {
        		String varType = Util.inferTypeFromVariable(var, expression);
        		usedVariables.put(var, varType); 
        	}
        }
    	
        if (this.canContinue()) {
        	builder.append(Util.SPACE.repeat(indent) + "do {\r\n");
        	GenerateQuery.buildResource(builder, bpmnName, this.start.getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent + 1);
        	
        	String joined = exitBranch.stream().collect(Collectors.joining(" || "));
        	builder.append(Util.SPACE.repeat(indent + 1) + String.format("if (%s) { processService.upsert(new ProcessInstance(processid, \"%s\")); break; }\r\n", joined, joined));
        	builder.append(Util.SPACE.repeat(indent) + String.format("} while (%s);\n", loopCondition));
        	return builder.toString();
        } else {
        	GenerateQuery.buildResource(builder, bpmnName, this.start.getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent);
        	return builder.toString();
        }
    }
}

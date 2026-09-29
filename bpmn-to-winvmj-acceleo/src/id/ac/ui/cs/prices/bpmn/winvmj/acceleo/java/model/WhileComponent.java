package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SequenceFlow;

import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.GenerateQuery;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.Util;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.Looping;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.TaskWrapper;

public class WhileComponent extends Component implements Looping {

    @Override
    public boolean canContinue() {
        return true;
    }
    
	@Override
	public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent, boolean isProcess) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();
        visited.add(start);

        List<SequenceFlow> outs = this.getEnd().getOutgoing();
        
        List<String> branches = new ArrayList<>();
        
        String loopCondition = outs.stream().map(SequenceFlow::getName).collect(Collectors.joining(" || "));
        branches.addAll(outs.stream().map(SequenceFlow::getName).toList());
        
        Component parent = this;
        // parent == null should never happen. If it does happen, there are bigger problems at hands
        while (parent != null && parent.getOutgoing().isEmpty()) {
        	parent = parent.getOwnerComponent();
        }
        List<String> exitBranch = parent.getOutgoing().stream().filter(x -> x.getName() != null).map(x -> x.getName()).toList();
        branches.addAll(exitBranch);
        
        for (String expression : branches) {
        	Set<String> variables = Util.extractVariablesFromExpression(expression);
        	for (String var : variables) {
        		String varType = Util.inferTypeFromVariable(var, expression);
        		usedVariables.add(new Variable(var, varType)); 
        	}
        }
        
        builder.append(Util.SPACE.repeat(indent) + String.format("while (%s) {\n", loopCondition));
        
        boolean first = true;
        boolean canContinueInclusive = true;
        for (SequenceFlow f : outs) {
            if (first) {
                builder.append(Util.SPACE.repeat(indent + 1) + String.format("if (%s) {\n", f.getName()));
                first = false;
            } else {
                builder.append(Util.SPACE.repeat(indent + 1) + String.format("else if (%s) {\n", f.getName()));
            }
            builder.append(Util.SPACE.repeat(indent + 2) + String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", Util.removeWeirdChar(f.getName())));
            
            canContinueInclusive &= GenerateQuery.buildStraightLine(builder, bpmnName, f.getTargetRef(), new HashSet<>(visited), usedVariables, indent + 2, isProcess);
        	
            builder.append(Util.SPACE.repeat(indent + 1) + "}\n");
        }
        boolean isEmpty = exitBranch.isEmpty();
        if (!isEmpty) {
        	String joined = exitBranch.stream().collect(Collectors.joining(" || "));
            builder.append(Util.SPACE.repeat(indent + 1) + String.format("if (%s) { processService.upsert(new ProcessInstance(processid, \"%s\")); break; }\r\n", joined, Util.removeWeirdChar(joined)));
        }
        
        builder.append(Util.SPACE.repeat(indent) + "}\n");
        
        if (isEmpty) {
	        builder.append(Util.SPACE.repeat(indent) + String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));", Util.removeWeirdChar(this.getName())));
        }
        return new FromStartToUserResult(builder.toString(), canContinueInclusive);
	}
	
	@Override
	public List<TaskWrapper> getFirstTask() {
		List<SequenceFlow> outs = this.getEnd().getOutgoing();
		List<TaskWrapper> res = new ArrayList<>();

        for (SequenceFlow f : outs) {
            if (f.getTargetRef() instanceof TaskWrapper tw) {
            	res.add(tw);
            } else {
            	res.addAll(((Component) f.getTargetRef()).getFirstTask());
            }
        }
		return res;
	}

	@Override
	public boolean hasTaskInLoopSequence(TaskWrapper t) {
		List<FlowNode> q = new ArrayList<>();
		Set<FlowNode> visited = new HashSet<>();
		
		FlowNode curr = this.getEnd();
		
		while (!q.isEmpty()) {
			if (curr != this.getStart() || !visited.add(curr)) continue;
			if (curr instanceof TaskWrapper tw) {
				if (tw.equals(t)) return true;
				q.add(tw.getOutgoing().get(0).getTargetRef());
			}
			if (curr instanceof Component c) {
				q.addAll(c.getStart().getOutgoing().stream().map(x -> x.getTargetRef()).toList());
			}
			if (curr instanceof Gateway g) {
				q.addAll(g.getOutgoing().stream().map(x -> x.getTargetRef()).toList());
			}
		}
		
		return false;
	}
}

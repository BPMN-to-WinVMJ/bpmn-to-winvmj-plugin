package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.Util;
import bpmn.to.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;
import bpmn.to.winvmj.acceleo.java.model.modelutil.Looping;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskWrapper;
/*
kalau top line dan bawah continuable -> simple loop
kalau top line !continuable dan ada bawah continuable -> call until topline end (bawah ada gk ada, gk ngaruh karena bakal dipanggil oleh getAfterUserService)
kalau top line continuable dan bottom line !continuable semua -> anggap sebagai switch
kalau top line !continuable dan bottom line !continuable semua -> call until topline end (bawah ada gk ada, gk ngaruh karena bakal dipanggil oleh getAfterUserService)
 */
public class WhileRepeatComponent extends Component implements Looping {

    @Override
    public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();
        return GenerateQuery.canContinueFrom(getStart().getOutgoing().get(0).getTargetRef(), visited, this.getEnd());
    }

	@Override
	public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent, boolean isProcess) {
		StringBuilder builder = new StringBuilder();
		Set<FlowNode> visited = new HashSet<>();
		
		boolean topContinuable = GenerateQuery.canContinueFrom(getStart().getOutgoing().get(0).getTargetRef(), new HashSet<>(visited), this.getEnd());
		if (topContinuable) {
			
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

	        // get top line method calls first
	        GenerateQuery.buildStraightLine(builder, bpmnName, this.getStart().getOutgoing().get(0).getTargetRef(), new HashSet<>(visited), usedVariables, indent, isProcess);
	        
	        builder.append(String.format(Util.SPACE.repeat(indent) + "while (%s) {\r\n", loopCondition));
	        
	        // build loops
	        boolean first = true;
	        boolean canContinueInclusive = true;
	        for (SequenceFlow f : this.getEnd().getOutgoing()) {
	            if (first) {
	                builder.append(Util.SPACE.repeat(indent + 1) + String.format("if (%s) {\r\n", f.getName()));
	                first = false;
	            } else {
	                builder.append(Util.SPACE.repeat(indent + 1) + String.format("else if (%s) {\r\n", f.getName()));
	            }
	            builder.append(Util.SPACE.repeat(indent + 1) + String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", f.getName(), Util.removeWeirdChar(f.getName())));
	            
	            canContinueInclusive &= GenerateQuery.buildStraightLine(builder, bpmnName, f.getTargetRef(), new HashSet<>(visited), usedVariables, indent + 2, isProcess);
	        	
	            builder.append(Util.SPACE.repeat(indent + 1) + "}\r\n");
	        }
	        // get top line method calls after each loop sequence
	        
	        GenerateQuery.buildStraightLine(builder, bpmnName, this.getStart().getOutgoing().get(0).getTargetRef(), new HashSet<>(visited), usedVariables, indent + 1, isProcess);
	        
	        if (!exitBranch.isEmpty()) {
		        String joined = exitBranch.stream().collect(Collectors.joining(" || "));
		        builder.append(Util.SPACE.repeat(indent + 1) + String.format("if (%s) { processService.upsert(new ProcessInstance(processid, \"%s\")); break; }\r\n", joined, Util.removeWeirdChar(joined)));
	        }

	        builder.append(Util.SPACE.repeat(indent) + "}\r\n\r\n");
	        if (exitBranch.isEmpty()) {
		        builder.append(Util.SPACE.repeat(indent) + String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));", Util.removeWeirdChar(this.getName())));
	        }
	        return new FromStartToUserResult(builder.toString(), canContinueInclusive);
		} else if (!topContinuable) {
			// get top line until can't continue
			GenerateQuery.buildStraightLine(builder, bpmnName, this.getStart().getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent + 1, isProcess);
			return new FromStartToUserResult(builder.toString(), false);
		}
		return new FromStartToUserResult("", true);
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

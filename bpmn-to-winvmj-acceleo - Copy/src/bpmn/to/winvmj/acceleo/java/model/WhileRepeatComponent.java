package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.java.GenerateUtil;
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
        return GenerateUtil.canContinueFrom(getStart().getOutgoing().get(0).getTargetRef(), visited, this.getEnd());
    }

	@Override
	public String getFromStartToUser(String bpmnName, Set<String> usedVariables, int indent) {
		StringBuilder builder = new StringBuilder();
		Set<FlowNode> visited = new HashSet<>();
		
		boolean topContinuable = GenerateUtil.canContinueFrom(getStart().getOutgoing().get(0).getTargetRef(), new HashSet<>(visited), this.getEnd());
		if (topContinuable) {
			
			List<SequenceFlow> outs = this.getEnd().getOutgoing();
	        String loopCondition = outs.stream().map(SequenceFlow::getName).collect(Collectors.joining(" || "));
	        
	        usedVariables.addAll(outs.stream().map(SequenceFlow::getName).toList());
	        
	        Component parent = this;
	        // parent == null should never happen. If it does happen, there are bigger problems at hands
	        while (parent != null && parent.getOutgoing().isEmpty()) {
	        	parent = parent.getOwnerComponent();
	        }
	        List<String> exitBranch = parent.getOutgoing().stream().map(x -> x.getName()).toList();
	        usedVariables.addAll(exitBranch);

	        // get top line method calls first
	        GenerateUtil.buildResource(builder, bpmnName, this.getStart().getOutgoing().get(0).getTargetRef(), new HashSet<>(visited), usedVariables, indent);
	        
	        builder.append(String.format(GenerateUtil.SPACE.repeat(indent) + "while (%s) {\n", loopCondition));
	        
	        // build loops
	        boolean first = true;
	        for (SequenceFlow f : this.getEnd().getOutgoing()) {
	            if (first) {
	                builder.append(GenerateUtil.SPACE.repeat(indent + 1) + String.format("if (%s) {\n", f.getName()));
	                first = false;
	            } else {
	                builder.append(GenerateUtil.SPACE.repeat(indent + 1) + String.format("else if (%s) {\n", f.getName()));
	            }
	            
	        	GenerateUtil.buildResource(builder, bpmnName, f.getTargetRef(), new HashSet<>(visited), usedVariables, indent + 2);
	        	
	            // this branch stops mid way due to userTask
	            if (!GenerateUtil.canContinueFrom(f.getTargetRef(), new HashSet<>(), this.getStart())) {
	            	builder.append(GenerateUtil.SPACE.repeat(indent + 2) + "return res;\r\n");
	            }
	            builder.append(GenerateUtil.SPACE.repeat(indent + 1) + "}\n");
	        }
	        // get top line method calls after each loop sequence
	        GenerateUtil.buildResource(builder, bpmnName, this.getStart().getOutgoing().get(0).getTargetRef(), new HashSet<>(visited), usedVariables, indent + 1);
	        builder.append(GenerateUtil.SPACE.repeat(indent + 1) + String.format("if (%s) break;\r\n", exitBranch.stream().collect(Collectors.joining(" || "))));

	        builder.append("}\r\n\r\n");
	        return builder.toString();
		} else if (!topContinuable) {
			// get top line until can't continue
			GenerateUtil.buildResource(builder, bpmnName, this.getStart().getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent + 1);
			return builder.toString();
		}
		return "";
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

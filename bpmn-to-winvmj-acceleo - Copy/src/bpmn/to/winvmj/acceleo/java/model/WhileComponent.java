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

public class WhileComponent extends Component implements Looping {

    @Override
    public boolean canContinue() {
        return true;
    }
    
	@Override
	public String getFromStartToUser(String bpmnName, Set<String> usedVariables, int indent) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();
        visited.add(start);

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
        
        builder.append(GenerateUtil.SPACE.repeat(indent) + String.format("while (%s) {\n", loopCondition));
        
        boolean first = true;
        for (SequenceFlow f : outs) {
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
            builder.append(GenerateUtil.SPACE.repeat(indent +1) + "}\n");
        }
        builder.append(GenerateUtil.SPACE.repeat(indent + 1) + String.format("if (%s) break;\r\n", exitBranch.stream().collect(Collectors.joining(" || "))));

        builder.append(GenerateUtil.SPACE.repeat(indent) + "}\n");
        return builder.toString();
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

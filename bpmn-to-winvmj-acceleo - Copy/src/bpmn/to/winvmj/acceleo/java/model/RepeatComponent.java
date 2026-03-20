 package bpmn.to.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.java.GenerateUtil;

public class RepeatComponent extends Component {

    @Override
    public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();
        return GenerateUtil.canContinueFrom(getStart().getOutgoing().get(0).getTargetRef(), visited, this.getEnd());
    }
    
    @Override
    public String getFromStartToUser(String bpmnName, Set<String> usedVariables, int indent) {
    	StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();
        
        Component parent = this;
        // parent == null should never happen. If it does happen, there are bigger problems at hands
        while (parent != null && parent.getOutgoing().isEmpty()) {
        	parent = parent.getOwnerComponent();
        }
        List<String> exitBranch = parent.getOutgoing().stream().map(x -> x.getName()).toList();
        usedVariables.addAll(exitBranch);
        
    	String loopCondition = this.getEnd().getOutgoing().stream().map(SequenceFlow::getName).collect(Collectors.joining(" || "));
        if (this.canContinue()) {
        	builder.append(GenerateUtil.SPACE.repeat(indent) + "do {\r\n");
        	GenerateUtil.buildResource(builder, bpmnName, this.start.getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent + 1);
        	builder.append(GenerateUtil.SPACE.repeat(indent + 1) + String.format("if (%s) break;\r\n", exitBranch.stream().collect(Collectors.joining(" || "))));
        	builder.append(GenerateUtil.SPACE.repeat(indent) + String.format("} while (%s);\n", loopCondition));
        	return builder.toString();
        } else {
        	GenerateUtil.buildResource(builder, bpmnName, this.start.getOutgoing().get(0).getTargetRef(), visited, usedVariables, indent);
        	return builder.toString();
        }
    }
}

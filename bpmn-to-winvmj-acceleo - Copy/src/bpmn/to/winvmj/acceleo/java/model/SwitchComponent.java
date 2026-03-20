package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.java.GenerateUtil;

public class SwitchComponent extends Component {

	@Override
	public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();

        for (SequenceFlow f : getStart().getOutgoing()) {
            if (GenerateUtil.canContinueFrom(f.getTargetRef(), visited)) {
                return true; // at least one branch can continue
            }
        }

        // all branches are blocked
        return false;
	}

    @Override
    public String getFromStartToUser(String bpmnName, Set<String> usedVariables, int indent) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = getStart().getOutgoing();
        boolean first = true;

        for (SequenceFlow f : outs) {
        	usedVariables.add(f.getName());
            if (first) {
                builder.append(GenerateUtil.SPACE.repeat(indent) + String.format("if (%s) {\n", f.getName()));
                first = false;
            } else {
                builder.append(GenerateUtil.SPACE.repeat(indent) + String.format("} else if (%s) {\n", f.getName()));
            }

            GenerateUtil.buildResource(builder, bpmnName, f.getTargetRef(), visited, usedVariables, indent + 1);
        }

        builder.append(GenerateUtil.SPACE.repeat(indent) + "}\n");
        return builder.toString();
    }
}

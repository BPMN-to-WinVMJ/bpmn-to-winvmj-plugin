package bpmn.to.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.Util;
import bpmn.to.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;

public class PickComponent extends Component {
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
    public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = getStart().getOutgoing();
        boolean first = true;

        for (SequenceFlow f : outs) {
            if (first) {
                builder.append(Util.SPACE.repeat(indent) + String.format("if (%s) {\n", f.getName()));
                first = false;
            } else {
                builder.append(Util.SPACE.repeat(indent) + String.format("} else if (%s) {\n", f.getName()));
            }

            GenerateQuery.buildStraightLine(builder, bpmnName, f.getTargetRef(), visited, usedVariables, indent + 1);
        }

        builder.append("}\n");
        return new FromStartToUserResult(builder.toString(), true);
    }
}

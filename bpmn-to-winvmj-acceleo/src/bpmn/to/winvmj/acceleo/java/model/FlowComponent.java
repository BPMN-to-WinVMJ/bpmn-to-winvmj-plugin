package bpmn.to.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.Util;
import bpmn.to.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;

public class FlowComponent extends Component{

    @Override
    public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();
        
        boolean all = true;
        // All branch must be able to follow through
        for (SequenceFlow f : getStart().getOutgoing()) {
            all &= GenerateQuery.canContinueFrom(f.getTargetRef(), visited, this.getEnd());
        }

        return all;
    }
    
    @Override
    public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = this.getStart().getOutgoing();
        
        StringBuilder builderTemp = new StringBuilder();

        boolean canContinueInclusive = true;
        
        for (SequenceFlow f : outs) {
        	canContinueInclusive &= GenerateQuery.buildStraightLine(builderTemp, bpmnName, f.getTargetRef(), visited, usedVariables, indent);
        }
        if (!canContinueInclusive) {
        	builder.append(Util.SPACE.repeat(indent) + "canContinue = false;\r\n");
        }
        
        builder.append(builderTemp.toString());
        try {
            builder.append(GenerateQuery.buildParallelSafeGuard(this.getEnd(), indent - 1));
        } catch (Exception e) {
        	System.err.println("[ERROR] You're FUCKED!");
        	e.printStackTrace();
        }
        return new FromStartToUserResult(builder.toString(), canContinueInclusive);
    }

}

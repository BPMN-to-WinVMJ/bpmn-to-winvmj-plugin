package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.java.GenerateUtil;

public class FlowComponent extends Component{

    @Override
    public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();
        
        boolean all = true;
        // All branch must be able to follow through
        for (SequenceFlow f : getStart().getOutgoing()) {
            all &= GenerateUtil.canContinueFrom(f.getTargetRef(), visited);
        }

        return all;
    }
    
    @Override
    public String getFromStartToUser(String bpmnName, Set<String> usedVariables, int indent) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = getStart().getOutgoing();

        for (SequenceFlow f : outs) {
            GenerateUtil.buildResource(builder, bpmnName, f.getTargetRef(), visited, usedVariables, indent);
        }
        return builder.toString();
    }

}

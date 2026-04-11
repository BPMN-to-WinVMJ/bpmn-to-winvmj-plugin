package bpmn.to.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;

public class FlowComponent extends Component{

    @Override
    public boolean canContinue() {
        Set<FlowNode> visited = new HashSet<>();
        
        boolean all = true;
        // All branch must be able to follow through
        for (SequenceFlow f : getStart().getOutgoing()) {
            all &= GenerateQuery.canContinueFrom(f.getTargetRef(), visited, this.getEnd());
        }
        
        System.out.println("[DEBUG] canContinue " + this.getName() + " " + all);

        return all;
    }
    
    @Override
    public String getFromStartToUser(String bpmnName, Map<String, String> usedVariables, int indent) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = this.getStart().getOutgoing();
        
        StringBuilder builderTemp = new StringBuilder();

        for (SequenceFlow f : outs) {
            GenerateQuery.buildResource(builderTemp, bpmnName, f.getTargetRef(), visited, usedVariables, indent);
        }
        
        builder.append(builderTemp.toString());
        try {
            builder.append(GenerateQuery.buildParallelSafeGuard(this.getEnd(), indent - 1));
        } catch (Exception e) {
        	System.err.println("[ERROR] You're FUCKED!");
        	e.printStackTrace();
        }
        return builder.toString();
    }

}

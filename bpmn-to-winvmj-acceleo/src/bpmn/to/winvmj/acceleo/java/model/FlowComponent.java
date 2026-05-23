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
    public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent, boolean isProcess) {
        StringBuilder builder = new StringBuilder();
        Set<FlowNode> visited = new HashSet<>();

        List<SequenceFlow> outs = this.getStart().getOutgoing();

        boolean canContinueInclusive = true;
        
    	StringBuilder builderTemp = new StringBuilder();
    	String futureNames = "";
        for (SequenceFlow f : outs) {
        	String futureName = "flow" + f.getId();
        	futureNames += futureName + ", ";
        	builderTemp.append(Util.SPACE.repeat(indent) + "CompletableFuture<Void> %s = CompletableFuture.runAsync(() -> {\r\n".formatted(futureName));
        	canContinueInclusive &= GenerateQuery.buildStraightLine(builderTemp, bpmnName, f.getTargetRef(), visited, usedVariables, indent + 1, isProcess);
        	builderTemp.append(Util.SPACE.repeat(indent) + "});\r\n");
        }
        if (!canContinueInclusive) {
        	builder.append(Util.SPACE.repeat(indent) + "canContinue = false;\r\n");
        }
        
        usedVariables.add(new Variable("futures", "List<CompletableFuture<Void>>"));
        usedVariables.add(new Variable("all", "CompletableFuture<Void>"));
        
        builderTemp.append(Util.SPACE.repeat(indent) + "futures = List.of(%s);\r\n".formatted(futureNames.substring(0, futureNames.length() - 2)));
        
        builderTemp.append(Util.SPACE.repeat(indent) + "all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));\r\n");
        builderTemp.append(Util.SPACE.repeat(indent) + "all.join();\r\n");
        
        builder.append(builderTemp.toString());
        try {
            builder.append(GenerateQuery.buildParallelSafeGuard(this.getEnd(), indent - 1, isProcess, usedVariables));
        } catch (Exception e) {
        	System.err.println("[ERROR] You're FUCKED!");
        	e.printStackTrace();
        }
        return new FromStartToUserResult(builder.toString(), canContinueInclusive);
    }

}

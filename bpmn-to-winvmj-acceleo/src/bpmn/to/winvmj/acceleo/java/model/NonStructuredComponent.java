package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.Util;
import bpmn.to.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskWrapper;
import bpmn.to.winvmj.acceleo.java.model.precond.EndPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.FlowPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.PickPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.PreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.StartPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.SwitchPreCond;

public class NonStructuredComponent extends Component {
	private Map<FlowNode, List<PreCond>> allPreCondSets;

	@Override
	public boolean canContinue() {
		return GenerateQuery.canContinueFrom(start, new HashSet<>(), this.getEnd());
	}

	public void setPreConds(Map<FlowNode, List<PreCond>> allPreCondSets) {
		this.allPreCondSets = allPreCondSets;
	}
	
	public Map<FlowNode, List<PreCond>> getPreConds() {
		return this.allPreCondSets;
	}
	
    @Override
    public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent) {
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append("while(true) {\n");
    	
    	for (FlowNode el : allPreCondSets.keySet()) {
            for (PreCond p : allPreCondSets.get(el)) {
                String event = getPreEvent(p);
            	if (el instanceof TaskWrapper t) {
                    builder.append(Util.SPACE + "if (").append(event).append(") {\n");
                    builder.append(Util.SPACE.repeat(2) + bpmnName.replaceAll(" ", "")).append("Service.").append(t.getName().replaceAll(" ", "")).append("(requestBody, processid)");
                    builder.append(Util.SPACE.repeat(2) + "end_").append(el.getName().replaceAll(" ", "")).append(" = true;\n");
                    builder.append(Util.SPACE + "}\n");
            	} else if (el instanceof Component c) {
            		builder.append(Util.SPACE + "if (").append(event).append(") {\n");
                    builder.append(c.getFromStartToUser(bpmnName, usedVariables, indent + 1));
                    builder.append(Util.SPACE.repeat(2) + "end_").append(el.getName().replaceAll(" ", "")).append(";\n");
                    builder.append(Util.SPACE + "}\n");
                } else if (el instanceof ParallelGateway && el.getIncoming().size() == 1) {
                    builder.append(Util.SPACE + "<onEvent name=\"").append(event).append("\">\n")
                        .append(Util.SPACE.repeat(2) + "  <flow name=\"").append(el.getName()).append("\">\n");

                    for (SequenceFlow out : el.getOutgoing()) {
                        builder.append(Util.SPACE.repeat(3) + "<invoke name=\"flow(")
                            .append(el.getName()).append(", ")
                            .append(out.getName()).append(")\"/>\n");
                    }

                    builder.append(Util.SPACE.repeat(2) + "</flow>\n")
                        .append(Util.SPACE + "</onEvent>\n");
                } else if (el instanceof ExclusiveGateway && el.getIncoming().size() == 1) {
                    builder.append(Util.SPACE + "<onEvent name=\"").append(event).append("\">\n")
                        .append(Util.SPACE.repeat(2) + "<switch name=\"").append(el.getName()).append("\">\n");

                    for (SequenceFlow out : el.getOutgoing()) {
                        String cond = el.getName(); // ci // TODO: Change to support cases
                        builder.append(Util.SPACE.repeat(3) + "<case condition=\"")
                            .append(cond).append("\">\n")
                            .append(Util.SPACE.repeat(4) + "<invoke name=\"switch(")
                            .append(el.getId()).append(", ")
                            .append(out.getId()).append(", ")
                            .append(cond).append(")\"/>\n")
                            .append(Util.SPACE.repeat(3) + "</case>\n");
                    }

                    builder.append(Util.SPACE.repeat(2) + "</switch>\n")
                        .append(Util.SPACE.repeat(1) + "</onEvent>\n");
                } else if (el instanceof ParallelGateway && el.getIncoming().size() > 1) {
                    builder.append(Util.SPACE.repeat(1) + "<onEvent name=\"").append(event).append("\">\n")
                            .append(Util.SPACE.repeat(2) + "    <invoke name=\"end(").append(el.getName()).append(")\"/>\n")
                            .append(Util.SPACE.repeat(1) + "</onEvent>\n");

                } else if (el instanceof ExclusiveGateway && el.getIncoming().size() > 1) {
                    builder.append(Util.SPACE.repeat(1) + "<onEvent name=\"").append(event).append("\">\n")
                            .append(Util.SPACE.repeat(2) + "    <invoke name=\"end(").append(el.getName()).append(")\"/>\n")
                            .append(Util.SPACE.repeat(1) + "</onEvent>\n");

                } else if (el instanceof EventBasedGateway && el.getIncoming().size() == 1) {
                    builder.append(Util.SPACE.repeat(1) + "<onEvent name=\"").append(event).append("\">\n")
                        .append(Util.SPACE.repeat(2) + "  <pick name=\"").append(el.getName()).append("\">\n");

                    for (SequenceFlow out : el.getOutgoing()) {
                        builder.append(Util.SPACE.repeat(3) + "<onEvent name=\"")
                            .append(out.getId()).append("\">\n")
                            .append(Util.SPACE.repeat(4) + "<invoke name=\"pick()")
                            .append(el.getId()).append(", ")
                            .append(out.getId()).append(")\"/>\n")
                            .append(Util.SPACE.repeat(3) + "</onEvent>\n");
                    }

                    builder.append(Util.SPACE.repeat(2) + "</pick>\n")
                        .append(Util.SPACE.repeat(1) + "</onEvent>\n");
                }
            }
    	}
    	
    	builder.append("}\n");
    	return new FromStartToUserResult(builder.toString(), true);
    }
    
    private String getPreEvent(PreCond p) {
        if (p instanceof EndPreCond) {
            return String.format("end_%s", p.xs.getName());
        } else if (p instanceof FlowPreCond) {
            return String.format("flow_%s_%s", p.xs.getName(), p.x.getName());
        } else if (p instanceof PickPreCond) {
            return String.format("pick_%s_%s", p.xs.getName(), p.x.getName());
        } else if (p instanceof StartPreCond) {
            return String.format("start", this.name);
        } else if (p instanceof SwitchPreCond pe) {
            return String.format("switch_%s_%s_%s", pe.xs.getName(), pe.x.getName(), pe.c);
        }
        return "";
    }
    
	@Override
	public List<TaskWrapper> getFirstTask() {
		List<TaskWrapper> res = new ArrayList<>();
		return res;
	}
}

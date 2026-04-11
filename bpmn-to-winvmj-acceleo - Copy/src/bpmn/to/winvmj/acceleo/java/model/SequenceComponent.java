package bpmn.to.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;

import bpmn.to.winvmj.acceleo.GenerateQuery;

/**
 * SequenceComponent - concrete, extends Component.
 * No additional features beyond Component.
 */
public class SequenceComponent extends Component {

	@Override
	public boolean canContinue() {
		return getElements().stream().allMatch(x -> ((Continuable)x).canContinue());
	}

	@Override
	public String getFromStartToUser(String bpmnName, Map<String, String> usedVariables, int indent) {
		StringBuilder builder = new StringBuilder();
        GenerateQuery.buildResource(builder, bpmnName, this.getElements().get(0), new HashSet<>(), usedVariables, indent);
        return builder.toString();
	}
}
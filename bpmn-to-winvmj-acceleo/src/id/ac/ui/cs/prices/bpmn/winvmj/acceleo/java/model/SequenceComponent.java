package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model;

import java.util.HashSet;
import java.util.Set;

import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.GenerateQuery;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.Continuable;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;

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
	public FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariables, int indent, boolean isProcess) {
		StringBuilder builder = new StringBuilder();
        boolean canContinueInclusive = GenerateQuery.buildStraightLine(builder, bpmnName, this.getElements().get(0), new HashSet<>(), usedVariables, indent, isProcess);
        return new FromStartToUserResult(builder.toString(), canContinueInclusive);
	}
}
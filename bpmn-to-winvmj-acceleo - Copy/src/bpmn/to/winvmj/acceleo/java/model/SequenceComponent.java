package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.java.GenerateUtil;

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
	public String getFromStartToUser(String bpmnName, Set<String> usedVariables, int indent) {
		StringBuilder builder = new StringBuilder();
        for (FlowNode el : getElements()) {
            if (!(el instanceof Component)) {
                if (el instanceof TaskWrapper t && TaskType.isContinuable(t.getTaskType())) {
                    builder.append(GenerateUtil.SPACE.repeat(indent) + String.format("%sService.%s(requestBody, processId);\n", bpmnName.toLowerCase(), el.getName().replaceAll(" ", "")));
                } else {
                    break;
                }
            } else {
                Component c = (Component) el;
                builder.append(c.getFromStartToUser(bpmnName, usedVariables, indent));
                if (!c.canContinue()) break;
            }
        }

        return builder.toString();
	}
}
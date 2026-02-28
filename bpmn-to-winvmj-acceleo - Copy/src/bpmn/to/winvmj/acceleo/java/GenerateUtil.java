package bpmn.to.winvmj.acceleo.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.UserTask;

import bpmn.to.winvmj.acceleo.java.model.BPMN;
import bpmn.to.winvmj.acceleo.java.model.Component;
import bpmn.to.winvmj.acceleo.java.model.TaskWrapper;

public class GenerateUtil {
	public static List<FlowNode> getPrior(UserTask userTask, org.eclipse.bpmn2.Process process) throws Exception {
		BPMN bpmn = getOrGenerateBPMN(process);
		
		FlowNode e = findById(userTask.getId(), bpmn);
		if (e == null) {
			throw new IllegalArgumentException("ID " + userTask.getId() + "not found on folded BPMN");
		}
		
		// Assume that the previous element can only be task as it is only sequence component
		// For pick / loop it will differ a lot
		return List.of(e.getIncoming().get(0).getSourceRef());
	}

	public static List<Task> getServiceTaskAfter(UserTask userTask, org.eclipse.bpmn2.Process process) throws Exception {
		BPMN bpmn = getOrGenerateBPMN(process);
		
		FlowNode e = findById(userTask.getId(), bpmn);
		if (e == null) {
			throw new IllegalArgumentException("ID " + userTask.getId() + "not found on folded BPMN");
		}
		System.out.println("getServiceTaskAfter");
		
		
		List<Task> tasks = new ArrayList<>();
		
		// Assume that the elements after userTask can only be task as it is only sequence component
		// For pick / loop it will differ a lot
		System.out.println(userTask.getName());
		System.out.println(e.getOutgoing());
		if (e.getOutgoing().isEmpty()) {
			return List.of();
		}

		FlowNode curr = e.getOutgoing().get(0).getTargetRef();
		while (curr instanceof TaskWrapper) {
			if ((curr instanceof ServiceTask) || (curr instanceof ScriptTask)) {
				tasks.add(((Task) curr));
				if (curr.getOutgoing().isEmpty()) {
					break;
				}
				curr = curr.getOutgoing().get(0).getTargetRef();
			}
		}
		System.out.println(userTask.getName() + " " + tasks);
		System.out.println();
		return tasks;
	}
	
	private static FlowNode findById(String id, Component component) {
	    for (FlowNode element : component.getElements()) {
	        if (element.getId().equals(id)) {
	            return element;
	        }
	        // Recurse into nested components
	        if (element instanceof Component nested) {
	        	FlowNode found = findById(id, nested);
	            if (found != null) return found;
	        }
	    }
	    return null;
	}

	// Overload to search from root BPMN
	private static FlowNode findById(String id, BPMN bpmn) {
	    for (FlowNode element : bpmn.getElements()) {
	        // Check the component itself first
	        if (element.getId().equals(id)) {
	            return element;
	        }
	     // Recurse into nested components
	        if (element instanceof Component nested) {
	        	FlowNode found = findById(id, nested);
	            if (found != null) return found;
	        }
	    }
	    return null;
	}

	// Overload to search from a list of BPMNElements
	private static FlowNode findById(String id, List<FlowNode> elements) {
	    for (FlowNode element : elements) {
	        if (element.getId().equals(id)) {
	            return element;
	        }
	        if (element instanceof Component nested) {
	        	FlowNode found = findById(id, nested);
	            if (found != null) return found;
	        }
	    }
	    return null;
	}
	
	// TODO: This approach only works on Sequence component
	public static boolean isTaskFromStart(UserTask userTask, BPMN bpmn) {
		FlowNode highLevel = bpmn.getElements().get(0);
		if (highLevel instanceof Task) {
			return userTask.getId().equals(highLevel.getId());
		}
		Component highLevelComponent = (Component) highLevel;
		return highLevel.getIncoming().stream().anyMatch(x -> x.getTargetRef().getId().equals(highLevelComponent.getId()));
	}
	
    private static List<Task> traverseForward(FlowNode e) {
        Set<FlowNode> visited = new HashSet<>();
        List<FlowNode> q = new ArrayList<>();
        List<Task> res = new ArrayList<>();
        q.add(e);

        while (!q.isEmpty()) {
        	FlowNode curr = q.remove(0);
            if (!visited.add(curr)) {
                continue;
            }
            if (curr instanceof Task t) {
                res.add(t);
            } else if (curr instanceof Component c) {
                res.addAll(traverseForward(c.getStart()));
            } else {
                q.addAll(curr.getOutgoing().stream().map(x -> x.getTargetRef()).toList());
            }
        }
        return res;
    } 
    
    public static BPMN getOrGenerateBPMN(org.eclipse.bpmn2.Process process) throws Exception {
		if (BPMNParser.getBPMN() != null) {
			return BPMNParser.getBPMN();
		} else {
			BPMNParser.parse(process);
			return BPMNParser.getBPMN();
		}
    }
}

package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.impl.TaskImpl;

import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.Continuable;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.OwnedComponent;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.TaskWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Component extends TaskImpl implements Comparable<Component>, Continuable, OwnedComponent {
    protected List<FlowNode> elements = new ArrayList<>();
    protected FlowNode start;
    protected FlowNode end;
    protected Component ownerComponent;
    
    public abstract FromStartToUserResult getFromStartToUser(String bpmnName, Set<Variable> usedVariable, int indent, boolean isProcess);
    
	public List<TaskWrapper> getFirstTask() {
		List<SequenceFlow> outs = getStart().getOutgoing();
		List<TaskWrapper> res = new ArrayList<>();

        for (SequenceFlow f : outs) {
            if (f.getTargetRef() instanceof TaskWrapper tw) {
            	res.add(tw);
            } else {
            	res.addAll(((Component) f.getTargetRef()).getFirstTask());
            }
        }
		return res;
	}

    public List<FlowNode> getElements() { 
    	return elements;
	}
    
    public void setElements(List<FlowNode> v) { 
    	this.elements = v;
    }
    
    public void addElement(FlowNode v) {
    	this.elements.add(v);
	}

    public FlowNode getStart() {
    	return start; 
    }
    
    public void setStart(FlowNode v) {
    	this.start = v;
	}

    public FlowNode getEnd() {
    	return end;
	}
    
    public void setEnd(FlowNode v) {
    	this.end = v;
	}

    @Override
    public int compareTo(Component other) {
        return Integer.compare(this.elements.size(), other.getElements().size());
    }
    
	@Override
	public void setOwnerComponent(Component c) {
		this.ownerComponent = c;
	}
	
	@Override
	public Component getOwnerComponent() {
		return this.ownerComponent;
	}
}
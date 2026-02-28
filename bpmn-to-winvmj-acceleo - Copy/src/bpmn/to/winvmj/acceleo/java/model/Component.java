package bpmn.to.winvmj.acceleo.java.model;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.impl.TaskImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class Component extends TaskImpl implements Comparable<Component> {
    protected List<FlowNode> elements = new ArrayList<>();
    protected FlowNode start;
    protected FlowNode end;

    public List<FlowNode> getElements()       { return elements; }
    public void setElements(List<FlowNode> v) { this.elements = v; }
    public void addElement(FlowNode v)        { this.elements.add(v); }

    public FlowNode getStart()                { return start; }
    public void setStart(FlowNode v)          { this.start = v; }

    public FlowNode getEnd()                  { return end; }
    public void setEnd(FlowNode v)            { this.end = v; }

    @Override
    public int compareTo(Component other) {
        return Integer.compare(this.elements.size(), other.getElements().size());
    }
}
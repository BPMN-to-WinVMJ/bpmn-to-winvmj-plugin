package bpmn.to.winvmj.acceleo.java.model;

import java.util.List;

import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.impl.TaskImpl;
import org.eclipse.emf.ecore.EClass;

/**
 * TaskWrapper - extends TaskImpl (matching ecore eSuperTypes="...TaskImpl").
 * Adds: delegate, fromStart, taskType, ownerComponent.
 *
 * Note: by extending TaskImpl we inherit all bpmn2::Task features
 * (getId, getName, getIncoming, getOutgoing etc.) for free.
 */
public class TaskWrapper extends TaskImpl {

    protected Task delegate;
    protected boolean fromStart = false;
    protected String taskType;
    protected Component ownerComponent;

    public TaskWrapper() {
        super();
    }

    // ── Accessors ──────────────────────────────────────────────────────────────

    public Task getDelegate()            { return delegate; }
    public void setDelegate(Task v)      { this.delegate = v; }
    
    public String getId() {
    	return delegate.getId();
    }
    
    public String getName() {
    	return delegate.getName();
    }

    public List<SequenceFlow> getIncoming() {
    	return delegate.getIncoming();
    }
    
    public List<SequenceFlow> getOutgoing() {
    	return delegate.getOutgoing();
    }
    
    public boolean isFromStart()         { return fromStart; }
    public void setFromStart(boolean v)  { this.fromStart = v; }

    public String getTaskType()          { return taskType; }
    public void setTaskType(String v)    { this.taskType = v; }

    public Component getOwnerComponent()       { return ownerComponent; }
    public void setOwnerComponent(Component v) { this.ownerComponent = v; }
}
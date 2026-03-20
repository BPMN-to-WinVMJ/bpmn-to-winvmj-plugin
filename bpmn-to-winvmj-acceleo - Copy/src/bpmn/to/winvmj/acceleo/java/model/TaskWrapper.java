package bpmn.to.winvmj.acceleo.java.model;

import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.impl.TaskImpl;

/**
 * TaskWrapper - extends TaskImpl (matching ecore eSuperTypes="...TaskImpl").
 * Adds: delegate, fromStart, taskType, ownerComponent.
 *
 * Note: by extending TaskImpl we inherit all bpmn2::Task features
 * (getId, getName, getIncoming, getOutgoing etc.) for free.
 */
public class TaskWrapper extends TaskImpl implements Continuable, OwnedComponent {

    protected Task delegate;
    protected boolean fromStart = false;
	protected TaskType taskType;
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
    
    public boolean isFromStart()         { return fromStart; }
    public void setFromStart(boolean v)  { this.fromStart = v; }

    public TaskType getTaskType()          { return taskType; }
    public void setTaskType(Task v)    { this.taskType = TaskType.getTaskType(v); }

    @Override
    public Component getOwnerComponent() { 
    	return ownerComponent; 
	}
    
    @Override
    public void setOwnerComponent(Component v) {
    	this.ownerComponent = v; 
    }

	@Override
	public boolean canContinue() {
		return TaskType.isContinuable(this.getTaskType());
	}
}
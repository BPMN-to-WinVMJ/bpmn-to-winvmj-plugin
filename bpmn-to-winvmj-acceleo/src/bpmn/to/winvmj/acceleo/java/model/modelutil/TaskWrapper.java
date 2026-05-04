package bpmn.to.winvmj.acceleo.java.model.modelutil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.impl.TaskImpl;

import bpmn.to.winvmj.acceleo.java.model.Component;

/**
 * TaskWrapper - extends TaskImpl (matching ecore eSuperTypes="...TaskImpl").
 * Adds: delegate, fromStart, taskType, ownerComponent.
 *
 * Note: by extending TaskImpl we inherit all bpmn2::Task features
 * (getId, getName, getIncoming, getOutgoing etc.) for free.
 */
public class TaskWrapper extends TaskImpl implements Continuable, OwnedComponent, OwnSubProcess {

    protected Task delegate;
    protected boolean fromStart = false;
	protected TaskType taskType;
    protected Component ownerComponent;
    
    private List<SubProcess> subProcesses = new ArrayList<>();

    public TaskWrapper() {
        super();
    }

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
    
    public void setSubProcess(List<SubProcess> subProcesses) {
    	this.subProcesses = subProcesses;
    }
    
    public void addSubProcess(SubProcess subProcess) {
    	System.out.println("addSubProcess " + this.subProcesses.size());
    	this.subProcesses.add(subProcess);
    }

    public List<SubProcess> getSubProcesses() {
    	System.out.println("getSubProces " + this.subProcesses.size());
    	return this.subProcesses;
    }
    
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
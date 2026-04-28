package bpmn.to.winvmj.acceleo.java.model.modelutil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.impl.GatewayImpl;

import bpmn.to.winvmj.acceleo.java.model.Component;

public class GatewayWrapper extends GatewayImpl implements OwnedComponent, Continuable, OwnSubProcess {
	
	protected Gateway delegate;
	protected GatewayType gatewayType;
    protected Component ownerComponent;

    private List<SubProcess> subProcesses = new ArrayList<>();

    public GatewayWrapper() {
        super();
    }

    public Gateway getDelegate()            { return delegate; }
    public void setDelegate(Gateway v)      { this.delegate = v; }
    
    public GatewayType getGatewayType()          { return gatewayType; }
    public void setGatewayType(Gateway v)    { this.gatewayType = GatewayType.getGatewayType(v); }
    
    public String getId() {
    	return delegate.getId();
    }
    
    public String getName() {
    	return delegate.getName();
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
		return true;
	}
	
    public void setSubProcess(List<SubProcess> subProcesses) {
    	this.subProcesses = subProcesses;
    }
    
    public void addSubProcess(SubProcess subProcess) {
    	this.subProcesses.add(subProcess);
    }

    public List<SubProcess> getSubProcesses() {
    	return this.subProcesses;
    }

}

package bpmn.to.winvmj.acceleo.java.model;

import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.impl.GatewayImpl;

public class GatewayWrapper extends GatewayImpl implements OwnedComponent, Continuable {
	
	protected Gateway delegate;
	protected GatewayType gatewayType;
    protected Component ownerComponent;

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

}

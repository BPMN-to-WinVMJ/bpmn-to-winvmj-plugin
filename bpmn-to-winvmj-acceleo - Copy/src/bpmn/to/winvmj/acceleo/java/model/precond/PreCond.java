package bpmn.to.winvmj.acceleo.java.model.precond;

import org.eclipse.bpmn2.FlowNode;

public class PreCond {
    public FlowNode xs;
    public FlowNode x;
    
    public void setXs(FlowNode xs) {
    	this.xs = xs;
    }
    
    public void setX(FlowNode x) {
    	this.x = x;
    }
    
    public FlowNode getX() {
    	return this.x;
    }
    
    public FlowNode getXs() {
    	return this.xs;
    }
}

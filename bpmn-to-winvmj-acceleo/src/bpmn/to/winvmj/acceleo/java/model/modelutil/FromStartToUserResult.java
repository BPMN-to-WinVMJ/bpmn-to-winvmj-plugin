package bpmn.to.winvmj.acceleo.java.model.modelutil;

import org.eclipse.bpmn2.FlowNode;

import bpmn.to.winvmj.acceleo.java.Util;

public class FromStartToUserResult {
	private String result;
	private boolean canContinueInclusive;
	
	public FromStartToUserResult(String result, boolean cannotContinueInclusive) {
		this.result = result;
		this.canContinueInclusive = cannotContinueInclusive;
	}
	
	public String getResult() {
		return result;
	}
	
	public boolean getCanContinueInclusive() {
		return canContinueInclusive;
	}
}

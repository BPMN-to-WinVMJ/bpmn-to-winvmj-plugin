package bpmn.to.winvmj.acceleo.java.model.modelutil;

import bpmn.to.winvmj.acceleo.java.model.Component;

public interface OwnedComponent {
	void setOwnerComponent(Component c);
	Component getOwnerComponent();
}


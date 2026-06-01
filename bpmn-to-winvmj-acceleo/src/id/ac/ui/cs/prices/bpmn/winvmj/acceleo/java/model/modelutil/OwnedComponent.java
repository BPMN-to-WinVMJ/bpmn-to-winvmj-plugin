package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil;

import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.Component;

public interface OwnedComponent {
	void setOwnerComponent(Component c);
	Component getOwnerComponent();
}


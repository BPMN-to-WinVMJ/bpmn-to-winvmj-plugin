package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.FlowNode;

public class BPMN {

    protected String id;
    protected String name;
    protected List<FlowNode> elements = new ArrayList<>();  // Field name matches Ecore

    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public List<FlowNode> getElements() {  // Method name matches Ecore
        return elements;
    }
}
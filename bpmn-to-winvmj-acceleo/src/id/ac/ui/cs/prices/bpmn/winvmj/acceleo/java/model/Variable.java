package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model;

import java.util.Objects;

public class Variable {
    private String name;
    private String type;
    private String value;

    public Variable() {}

    public Variable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Variable(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setValue(String value) {
        this.value= value;
    }
    
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Variable{name='" + name + "', type='" + type + "'}";
    }
}

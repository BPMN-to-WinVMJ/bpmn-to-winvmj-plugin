package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model;

import java.util.Objects;

public class Variable {
    private String name;
    private String type;

    public Variable() {}

    public Variable(String name, String type) {
        this.name = name;
        this.type = type;
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

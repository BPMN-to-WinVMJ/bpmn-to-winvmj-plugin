package bpmn.to.winvmj.acceleo.java.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;

public class BPMN {

    private String id;
    private String name;
    private List<FlowNode> elements = new ArrayList<>();
    private Map<String, SubProcess> subProcesses = new HashMap<>();

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
    
    public Map<String, SubProcess> getSubProcesses() {
    	return this.subProcesses;
    }
    
    public void setSubProcesses(Map<String, SubProcess> subProcesses) {
    	this.subProcesses = subProcesses;
    }
    
    public void addSubProcess(String id, SubProcess subProcess) {
    	this.subProcesses.put(id, subProcess);
    }
    
    public SubProcess getSubProcess(String index) {
    	return this.subProcesses.get(index);
    }
    
    // All flow elements (Tasks, Gateways, Events)
    private Map<String, FlowNode> O = new HashMap<>();

    // Tasks
    private Map<String, Task> T = new HashMap<>();
    private Map<String, ReceiveTask> Tr = new HashMap<>();

    // Events
    private Map<String, Event> E = new HashMap<>();
    private Map<String, StartEvent> Es = new HashMap<>();
    private Map<String, IntermediateCatchEvent> Ei = new HashMap<>();  	// IntermediateEvent -> IntermediateCatchEvent
    private Map<String, EndEvent> Ee = new HashMap<>();
    private Map<String, IntermediateCatchEvent> Eet = new HashMap<>(); 	// timer intermediate events

    // Gateways
    private Map<String, Gateway> G = new HashMap<>();
    private Map<String, ParallelGateway> Gf = new HashMap<>();         	// parallel fork
    private Map<String, ParallelGateway> Gj = new HashMap<>();         	// parallel join
    private Map<String, Gateway> Gd = new HashMap<>();        			// data xor
    private Map<String, EventBasedGateway> Gv = new HashMap<>();       	// event xor
    private Map<String, Gateway> Gm = new HashMap<>();        			// merge xor

    // Flows
    private Map<String, SequenceFlow> F = new HashMap<>();

	public Map<String, FlowNode> getO() {
		return O;
	}

	public void setO(Map<String, FlowNode> o) {
		O = o;
	}

	public Map<String, StartEvent> getEs() {
		return Es;
	}

	public void setEs(Map<String, StartEvent> es) {
		Es = es;
	}

	public Map<String, Event> getE() {
		return E;
	}

	public void setE(Map<String, Event> e) {
		E = e;
	}

	public Map<String, EndEvent> getEe() {
		return Ee;
	}

	public void setEe(Map<String, EndEvent> ee) {
		Ee = ee;
	}

	public Map<String, IntermediateCatchEvent> getEi() {
		return Ei;
	}

	public void setEi(Map<String, IntermediateCatchEvent> ei) {
		Ei = ei;
	}

	public Map<String, IntermediateCatchEvent> getEet() {
		return Eet;
	}

	public void setEet(Map<String, IntermediateCatchEvent> eet) {
		Eet = eet;
	}

	public Map<String, Gateway> getG() {
		return G;
	}

	public void setG(Map<String, Gateway> g) {
		G = g;
	}

	public Map<String, ParallelGateway> getGf() {
		return Gf;
	}

	public void setGf(Map<String, ParallelGateway> gf) {
		Gf = gf;
	}

	public Map<String, ParallelGateway> getGj() {
		return Gj;
	}

	public void setGj(Map<String, ParallelGateway> gj) {
		Gj = gj;
	}

	public Map<String, Gateway> getGd() {
		return Gd;
	}

	public void setGd(Map<String, Gateway> gd) {
		Gd = gd;
	}

	public Map<String, Gateway> getGm() {
		return Gm;
	}

	public void setGm(Map<String, Gateway> gm) {
		Gm = gm;
	}

	public Map<String, EventBasedGateway> getGv() {
		return Gv;
	}

	public void setGv(Map<String, EventBasedGateway> gv) {
		Gv = gv;
	}

	public Map<String, SequenceFlow> getF() {
		return F;
	}

	public void setF(Map<String, SequenceFlow> f) {
		F = f;
	}

	public Map<String, Task> getT() {
		return T;
	}

	public void setT(Map<String, Task> t) {
		T = t;
	}

	public Map<String, ReceiveTask> getTr() {
		return Tr;
	}

	public void setTr(Map<String, ReceiveTask> tr) {
		Tr = tr;
	}
}
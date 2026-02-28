package bpmn.to.winvmj.acceleo.java.model;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.SequenceFlow;

import java.util.HashMap;
import java.util.Map;

public class BPMNWrapper {

	public BPMN bpmn = new BPMN();
	
    // All flow elements (Tasks, Gateways, Events)
    private Map<String, FlowNode> O = new HashMap<>();

    // Tasks
    private Map<String, Task> T = new HashMap<>();
    private Map<String, ReceiveTask> Tr = new HashMap<>();

    // Events
    private Map<String, Event> E = new HashMap<>();
    private Map<String, StartEvent> Es = new HashMap<>();
    private Map<String, IntermediateCatchEvent> Ei = new HashMap<>();  // IntermediateEvent -> IntermediateCatchEvent
    private Map<String, EndEvent> Ee = new HashMap<>();
    private Map<String, IntermediateCatchEvent> Eet = new HashMap<>(); // timer intermediate events

    // Gateways
    private Map<String, Gateway> G = new HashMap<>();
    private Map<String, ParallelGateway> Gf = new HashMap<>();         // parallel fork
    private Map<String, ParallelGateway> Gj = new HashMap<>();         // parallel join
    private Map<String, ExclusiveGateway> Gd = new HashMap<>();        // data xor
    private Map<String, EventBasedGateway> Gv = new HashMap<>();       // event xor
    private Map<String, ExclusiveGateway> Gm = new HashMap<>();        // merge xor

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

	public Map<String, ExclusiveGateway> getGd() {
		return Gd;
	}

	public void setGd(Map<String, ExclusiveGateway> gd) {
		Gd = gd;
	}

	public Map<String, ExclusiveGateway> getGm() {
		return Gm;
	}

	public void setGm(Map<String, ExclusiveGateway> gm) {
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
	
	public BPMN getBPMN() {
		return bpmn;
	}

	public void setBpmn(BPMN bpmn) {
		this.bpmn = bpmn;
	}
	
}
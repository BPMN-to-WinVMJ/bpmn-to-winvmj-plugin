package bpmn.to.winvmj.acceleo.java;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.model.*;
import bpmn.to.winvmj.acceleo.java.model.modelutil.GatewayWrapper;
import bpmn.to.winvmj.acceleo.java.model.modelutil.OwnedComponent;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskWrapper;
import bpmn.to.winvmj.acceleo.java.model.precond.EndPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.FlowPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.PickPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.PreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.StartPreCond;
import bpmn.to.winvmj.acceleo.java.model.precond.SwitchPreCond;

public class BPMNParser {
    private static final String COMPONENT_STRING = "tc";
    private static int componentCount = 0;
    
    private static BPMN bpmnProcess;
    private static BPMN bpmnSubProcess;
    
    public static BPMN getBPMNProcess() {
    	return bpmnProcess;
    }
    
    public static BPMN getBPMNSubProcess() {
    	return bpmnSubProcess;
    }

    public static BPMN parse(org.eclipse.bpmn2.FlowElementsContainer process) throws Exception {
    	boolean isProcess = process instanceof Process;
    	BPMN temp = new BPMN();
        try {
            addElements(temp, process);
            temp.setName(process.getId());
            if (isProcess) bpmnProcess = temp;
            if (!isProcess) bpmnSubProcess = temp;
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void addElements(BPMN bpmn, org.eclipse.bpmn2.FlowElementsContainer process) throws Exception {

        // Classify all flow elements
        for (FlowElement fe : process.getFlowElements()) {
        	
        	if (fe.eContainer() instanceof SubProcess sp) {
        		bpmn.addSubProcess(sp.getId(), sp);
        	}
        	
        	if (!(fe instanceof ScriptTask || fe instanceof SequenceFlow)) {
        		fe.setName(GenerateQuery.toValidVariableName(fe.getName()));
        	}

            // Tasks
            if (fe instanceof Task task) {
            	bpmn.getO().put(task.getId(), task);
                bpmn.getT().put(task.getId(), task);
                if (task instanceof ReceiveTask rt) {
                	bpmn.getTr().put(rt.getId(), rt);
                }
            }

            // Events
            else if (fe instanceof StartEvent se) {
            	bpmn.getO().put(se.getId(), se);
            	bpmn.getE().put(se.getId(), se);
            	bpmn.getEs().put(se.getId(), se);
            }
            else if (fe instanceof EndEvent ee) {
            	bpmn.getO().put(ee.getId(), ee);
            	bpmn.getE().put(ee.getId(), ee);
            	bpmn.getEe().put(ee.getId(), ee);
            }
            else if (fe instanceof IntermediateCatchEvent ice) {
            	bpmn.getO().put(ice.getId(), ice);
            	bpmn.getE().put(ice.getId(), ice);
            	bpmn.getEi().put(ice.getId(), ice);
                boolean isTimer = ice.getEventDefinitions().stream()
                    .anyMatch(ed -> ed instanceof TimerEventDefinition);
                if (isTimer) {
                	bpmn.getEet().put(ice.getId(), ice);
                }
            }
            else if (fe instanceof IntermediateThrowEvent ite) {
            	bpmn.getO().put(ite.getId(), ite);
                bpmn.getE().put(ite.getId(), ite);
            }

            // Gateways
            else if (fe instanceof Gateway gw) {
            	bpmn.getG().put(gw.getId(), gw);
            	bpmn.getO().put(gw.getId(), gw);
            }
        }

        // Validate flows
        for (FlowElement fe : process.getFlowElements()) {
            if (fe instanceof SequenceFlow flow) {
                FlowNode sourceElem = flow.getSourceRef();
                FlowNode targetElem = flow.getTargetRef();

                if (sourceElem instanceof Task && sourceElem.getOutgoing().size() > 1) {
                    throw new IllegalArgumentException(
                        "Task is branching: " + sourceElem.getName());
                }
                if (targetElem instanceof Task && targetElem.getIncoming().size() > 1) {
                    throw new IllegalArgumentException(
                        "Task is branching: " + targetElem.getName());
                }

                if (targetElem instanceof SubProcess || sourceElem instanceof SubProcess) {
                    List<SubProcess> subProcessChain = new ArrayList<>();

                    // Traverse FORWARD from targetElem
                    FlowNode current = targetElem;
                    FlowNode lastNonSub = null;
                    while (current instanceof SubProcess sp) {
                        subProcessChain.add(sp);
                        FlowNode next = sp.getOutgoing().isEmpty() ? null
                                : sp.getOutgoing().get(0).getTargetRef();
                        for (SequenceFlow sq : sp.getOutgoing()) sq.setTargetRef(null);
                        sp.getOutgoing().clear();
                        lastNonSub = next;
                        current = next;
                    }

                    // If sourceElem itself is a SubProcess, walk BACKWARD until Task/Gateway
                    FlowNode realSource = sourceElem;
                    if (sourceElem instanceof SubProcess) {
                        current = sourceElem;
                        while (current instanceof SubProcess sp) {
                            if (!subProcessChain.contains(sp)) subProcessChain.add(0, sp);
                            FlowNode prev = sp.getIncoming().isEmpty() ? null
                                    : sp.getIncoming().get(0).getSourceRef();
                            current = prev;
                        }
                        realSource = current; // the Task or Gateway at the start of the chain
                    }

                    if (lastNonSub != null) flow.setTargetRef(lastNonSub);

                    if (realSource instanceof Task task) {
                    	
                    	FlowNode existing = bpmn.getO().get(task.getId());
                        boolean shouldAdd = existing == null  || (existing instanceof Task && !(existing instanceof TaskWrapper))
                            || (existing instanceof TaskWrapper t && t.getSubProcesses().size() < subProcessChain.size());
                        
                        if (shouldAdd) {
	                        TaskWrapper tw = new TaskWrapper();
	                        tw.setDelegate(task);
	                        tw.setTaskType(task);
	                        List<SequenceFlow> temp = List.copyOf(task.getIncoming());
	                        for (SequenceFlow floww : temp) floww.setTargetRef(tw);
	                        temp = List.copyOf(task.getOutgoing());
	                        for (SequenceFlow floww : temp) floww.setSourceRef(tw);
	                        for (SubProcess sp : subProcessChain) {
	                        	tw.addSubProcess(sp);
	                        }
	                        bpmn.getO().put(task.getId(), tw); // store tw, not task
	                        bpmn.getT().put(task.getId(), tw); // store tw, not task
	                        if (task instanceof ReceiveTask rt) bpmn.getTr().put(rt.getId(), rt);
                        }
                    } else if (realSource instanceof Gateway gateway) {
                    	FlowNode existing = bpmn.getO().get(gateway.getId());
                        boolean shouldAdd = existing == null  || (existing instanceof Task && !(existing instanceof GatewayWrapper))
                            || (existing instanceof GatewayWrapper t && t.getSubProcesses().size() < subProcessChain.size());
                        
                        if (shouldAdd) {
	                        GatewayWrapper gw = new GatewayWrapper();
	                        gw.setDelegate(gateway);
	                        gw.setGatewayType(gateway);
	                        List<SequenceFlow> temp = List.copyOf(gateway.getIncoming());
	                        for (SequenceFlow floww : temp) floww.setTargetRef(gw);
	                        temp = List.copyOf(gateway.getOutgoing());
	                        for (SequenceFlow floww : temp) floww.setSourceRef(gw);
	                        for (SubProcess sp : subProcessChain) gw.addSubProcess(sp);
	                        bpmn.getG().put(gw.getId(), gw);
	                        bpmn.getO().put(gw.getId(), gw);
                        }
                    }
                }
                
                bpmn.getF().put(flow.getId(), flow);
            }
        }

        // Classify gateways
        for (Gateway g : bpmn.getG().values()) {
            int inSize = g.getIncoming().size();
            int outSize = g.getOutgoing().size();

            if (g instanceof ParallelGateway pg && inSize == 1 && outSize >= 1) {
                pg.setGatewayDirection(GatewayDirection.DIVERGING);
                bpmn.getGf().put(g.getId(), pg);
            }
            if (g instanceof ParallelGateway pg && inSize >= 1 && outSize == 1) {
                pg.setGatewayDirection(GatewayDirection.CONVERGING);
                bpmn.getGj().put(g.getId(), pg);
            }
            if ((g instanceof ExclusiveGateway eg || g instanceof InclusiveGateway ig) && inSize == 1 && outSize >= 1) {
                g.setGatewayDirection(GatewayDirection.DIVERGING);
                bpmn.getGd().put(g.getId(), g);
            }
            if ((g instanceof ExclusiveGateway eg || g instanceof InclusiveGateway ig) && inSize >= 1 && outSize == 1) {
                g.setGatewayDirection(GatewayDirection.CONVERGING);
                bpmn.getGm().put(g.getId(), g);
            }
            if (g instanceof EventBasedGateway evg && inSize == 1 && outSize > 1) {
                evg.setGatewayDirection(GatewayDirection.DIVERGING);
                bpmn.getGv().put(g.getId(), evg);
            }
        }
        
        bpmn.setId(process.getId());
        
        loopFold(bpmn);
    }

    public static void loopFold(BPMN bpmn) throws Exception {

        // All foldable elements
        List<FlowNode> X = new ArrayList<>(bpmn.getO().values());

        // Remove start and end events from foldable
        X.remove(bpmn.getEs().values().iterator().next());
        X.remove(bpmn.getEe().values().iterator().next());

        PriorityQueue<Component> seqComponents = findMaxSequence(bpmn);

        while (X.size() > 1) {
            System.out.println(X.stream().map(x -> isBlankOrNull(((FlowNode)x).getName()) ? ((FlowNode)x).getId() : ((FlowNode)x).getName()).toList());
            Component component = seqComponents.poll();

            if (component != null) {
                componentCount++;
                component.setName(COMPONENT_STRING + componentCount);
                component.setId(COMPONENT_STRING + componentCount);

                // First and last elements of the sequence
                FlowNode enterNode = component.getElements().get(0);
                FlowNode exitNode = component.getElements()
                    .get(component.getElements().size() - 1);

                // Wire incoming/outgoing onto component
                component.getIncoming().clear();
                component.getOutgoing().clear();
                component.getIncoming().addAll(enterNode.getIncoming());
                component.getOutgoing().addAll(exitNode.getOutgoing());

                setOwnerComponent(component);

                Set<String> elementIds = component.getElements().stream()
                	    .map(e -> e.getId())
                	    .collect(Collectors.toSet());
                
            	X.removeIf(e -> elementIds.contains(e.getId()));
                X.add(component);
                
                bpmn.getT().put(component.getName(), component);

                // Remove folded elements from wrapper maps
                bpmn.getT().entrySet().removeIf(e -> elementIds.contains(e.getValue().getId()));
                bpmn.getTr().entrySet().removeIf(e -> elementIds.contains(e.getValue().getId()));
                bpmn.getEi().entrySet().removeIf(e -> elementIds.contains(e.getValue().getId()));

                System.out.println(printComponent(component));
                continue;
            }

            Component nonSeq = findMaxNonSequence(bpmn);
            if (nonSeq != null) {
                componentCount++;
                nonSeq.setName(COMPONENT_STRING + componentCount);
                nonSeq.setId(COMPONENT_STRING + componentCount);

                setOwnerComponent(nonSeq);

                Set<String> elementIds = nonSeq.getElements().stream()
                	    .map(e -> e.getId())
                	    .collect(Collectors.toSet());

            	X.removeIf(e -> elementIds.contains(e.getId()));
                X.add(nonSeq);
                
                bpmn.getT().put(nonSeq.getName(), nonSeq);
                
                removeAllElements(bpmn, nonSeq);

                System.out.println(printComponent(nonSeq));
                seqComponents = findMaxSequence(bpmn);

            } else {
                Component nonWellStructured = findMinNonWellStructuredComponent(bpmn);
                componentCount++;
                nonWellStructured.setName(COMPONENT_STRING + componentCount);
                nonWellStructured.setId(COMPONENT_STRING + componentCount);

                System.out.println(printComponent(nonWellStructured));

                for (SequenceFlow entering : nonWellStructured.getIncoming()) {
                    entering.setTargetRef(nonWellStructured);
                }
                
                for (SequenceFlow exiting : nonWellStructured.getOutgoing()) {
                    exiting.setSourceRef(nonWellStructured);
                }

                setOwnerComponent(nonWellStructured);

                Set<String> elementIds = nonWellStructured.getElements().stream()
                	    .map(e -> e.getId())
                	    .collect(Collectors.toSet());

            	X.removeIf(e -> elementIds.contains(e.getId()));
                X.add(nonWellStructured);
                
                bpmn.getT().put(nonWellStructured.getName(), nonWellStructured);
                
                removeAllElements(bpmn, nonWellStructured);

                seqComponents = findMaxSequence(bpmn);
            }
        }
        System.out.println("done");
        FlowNode highestComponent = X.get(0);
        bpmn.getElements().add(highestComponent);
        bpmn.getElements().add(highestComponent.getIncoming().get(0).getSourceRef());
        bpmn.getElements().add(highestComponent.getOutgoing().get(0).getTargetRef());
    }
    
    private static PriorityQueue<Component> findMaxSequence(BPMN bpmn) {
        System.out.println("Finding max sequence..");
        PriorityQueue<Component> components = new PriorityQueue<>();

        List<FlowElement> elements = new ArrayList<>();
        elements.addAll(bpmn.getT().values());
        elements.addAll(bpmn.getEi().values());

        for (FlowElement fe : elements) {
        	
            // Only BPMNElements have in/out flows
            if (!(fe instanceof FlowNode node)) continue;
            if (node.getIncoming().isEmpty()) continue;

            SequenceFlow inFlow = node.getIncoming().get(0);
            FlowNode source = inFlow.getSourceRef();
            
            // If previous element is event gateway -> is a pick, skip
            if (bpmn.getGv().containsValue(source)) continue;

            // LEFT-MAXIMAL: do not start in the middle of a sequence
            if (hasOneInOut(source)) continue;

            FlowNode current = node;
            List<FlowNode> visited = new ArrayList<>();
            boolean isLoop = false;

            while (true) {
                if (visited.contains(current)) {
                    isLoop = true;
                    break;
                }
                visited.add(current);

                if (current.getOutgoing().isEmpty()) break;
                FlowNode target = current.getOutgoing().get(0).getTargetRef();

                // Stop if next is diverging/converging gateway or end event
                if (!hasOneInOut(target) || bpmn.getEe().containsValue(target)) break;

                current = target;
            }

            if (!isLoop && visited.size() >= 2) {
                SequenceComponent sc = new SequenceComponent();
                sc.getElements().addAll(
                		visited.stream().map(x -> {return mapToWrapper(x);}
        				).toList());
                sc.setStart(sc.getElements().get(0));
                sc.setEnd(sc.getElements().get(sc.getElements().size() - 1));
                components.add(sc);
            }
        }

        return components;
    }

    /**
     * Returns true if the given FlowNode has exactly one incoming and one outgoing flow
     * i.e. it is a simple pass-through element in a sequence
     */
    private static boolean hasOneInOut(FlowNode node) {
        if (node == null) return false;
        return (node instanceof Task || node instanceof Event) && node.getIncoming().size() == 1 && node.getOutgoing().size() == 1;
    }

    /**
     * Group other types of components beside Sequence and NonStructured
     */
    private static Component findMaxNonSequence(BPMN bpmn) {
        Component component = findFlow(bpmn);
        if (component != null) {
            return component;
        }
        component = findWhile(bpmn);
        if (component != null) {
            return component;
        }
        component = findRepeat(bpmn);
        if (component != null) {
            return component;
        }
        component = findRepeatWhile(bpmn);
        if (component != null) {
            return component;
        }
        component = findSwitch(bpmn);
        if (component != null) {
            return component;
        }
        component = findPick(bpmn);
        if (component != null) {
            return component;
        }
        return null;
    }
    
    /**
     * Find Flow Components, the ones with parallel gateway
     */
    private static Component findFlow(BPMN bpmn) {
    	System.out.println("Finding flow..");
        for (ParallelGateway ic : bpmn.getGf().values()) {

            List<FlowNode> candidates = new ArrayList<>();
            candidates.add(ic);

            List<SequenceFlow> outFlows = ic.getOutgoing();

            if (outFlows.size() < 1) continue; // not a FLOW

            ParallelGateway oc = null;

            for (SequenceFlow f : outFlows) {
            	FlowNode mid = f.getTargetRef();

                // Must be Task or Intermediate Event
                if (!(hasOneInOut(mid)) && !(mid instanceof StartEvent) && !(mid instanceof EndEvent)) {
                    oc = null;
                    break;
                }

                FlowNode target = mid.getOutgoing().get(0).getTargetRef();

                // All branches must converge to same join
                if (oc == null) {
                    if (!(target instanceof ParallelGateway) || (target instanceof ParallelGateway && ((ParallelGateway)target).getOutgoing().size() > 1 )) break;
                    oc = (ParallelGateway) target;
                } else if (!oc.equals(target)) {
                    oc = null;
                    break;
                }

                candidates.add(mid);
            }
            if (oc == null) continue;

            // oc must be a join gateway
            if (!bpmn.getGj().containsValue(oc)) continue;

            candidates.add(oc);

            // check whether end gateway is part of something larger
            if (!oc.getIncoming().stream().allMatch(x -> {
                return candidates.contains(x.getSourceRef());
            })) {
                continue;
            }
            
            Gateway oc2 = oc;
            // Verify oc incoming flows
            if (!oc.getIncoming().stream()
                .map(SequenceFlow::getSourceRef)
                .collect(Collectors.toSet())
                .equals(
                    candidates.stream()
                                .filter(x -> x != ic && x != oc2)
                                .collect(Collectors.toSet())
                )) {
                continue;
            }

            Component c = new FlowComponent();
            
            candidates.remove(ic);
            candidates.remove(oc);
            FlowNode start = mapToWrapper(ic);
            FlowNode end = mapToWrapper(oc);
            candidates.add(start);
            candidates.add(end);
            c.setElements(candidates.stream().map(x -> {return mapToWrapper(x);}).toList());
            c.setStart(start);
            c.setEnd(end);
            
            c.getIncoming().clear();
            c.getOutgoing().clear();
            c.getIncoming().add(start.getIncoming().get(0));
            c.getOutgoing().add(end.getOutgoing().get(0));
            
            return c;
        }

        return null;
    }
    
    /**
     * Find While Components, empty straight line on top
     */
    private static Component findWhile(BPMN bpmn) {
    	System.out.println("Finding while..");

        for (Gateway ic : bpmn.getGm().values()) { // Exclusive and inclusive gateway are handled the same
            FlowNode gd = ic.getOutgoing().get(0).getTargetRef();

            if (bpmn.getGd().containsValue(gd)) {
                boolean isLoop = false;
                List<SequenceFlow> loopFlows = new ArrayList<>();
                List<FlowNode> candidates = new ArrayList<>();

                candidates.add(ic);

                for (SequenceFlow f : gd.getOutgoing()) {
                	FlowNode mid = f.getTargetRef();
                    if (!hasOneInOut(mid)) continue;

                    if (!(mid instanceof EndEvent) && mid.getOutgoing().get(0).getTargetRef().equals(ic)){
                        isLoop = true;
                        candidates.add(mid);
                        loopFlows.add(f); // supaya flow c1 gk ke ganti targetnya
                        loopFlows.add(mid.getOutgoing().get(0)); // supaya flow t1 -> sink gk keubah targetnya
                    }
                }
                if (isLoop) {
                    candidates.add(gd);
                    List<SequenceFlow> inFlowCopy = new ArrayList<>(ic.getIncoming());
                    inFlowCopy.removeAll(loopFlows);
                    List<SequenceFlow> outFlowCopy = new ArrayList<>(gd.getOutgoing());
                    outFlowCopy.removeAll(loopFlows);

                    // component can only have 1 in and 
                    if (inFlowCopy.size() > 1 || outFlowCopy.size() > 1) continue;
                    
                    Component c = new WhileComponent();
                    
                    candidates.remove(ic);
                    candidates.remove(gd);
                    FlowNode start = mapToWrapper(ic);
                    FlowNode end = mapToWrapper(gd);
                    candidates.add(start);
                    candidates.add(end);
                    c.setStart(start);
                    c.setEnd(end);
                    
                    c.setElements(candidates.stream().map(x -> mapToWrapper(x)).toList());

                    c.getIncoming().clear();
                    c.getOutgoing().clear();
                    c.getIncoming().addAll(inFlowCopy);
                    c.getOutgoing().addAll(outFlowCopy);
                    return c;
                }
            }
        }

        return null;
    }

    /**
     * Find Repeat Components, empty looping line
     */
    private static Component findRepeat(BPMN bpmn) {
    	System.out.println("Finding repeat..");

        for (Gateway ic : bpmn.getGd().values()) {

            Set<FlowNode> candidates = new HashSet<>();
            List<SequenceFlow> loopFlows = new ArrayList<>();
            FlowNode oc = null;

            candidates.add(ic);
            boolean isLoop = false;

            for (SequenceFlow f : ic.getOutgoing()) {
                
            	FlowNode gm = f.getTargetRef();

                if (bpmn.getGm().containsValue(gm)) {

                	SequenceFlow f2 = gm.getOutgoing().get(0);
                    FlowNode mid = f2.getTargetRef();
                    
                    if (hasOneInOut(mid)) {
                        if (mid.getOutgoing().get(0).getTargetRef().equals(ic)) {
                            isLoop = true;
                            candidates.add(mid);
                            loopFlows.add(f);
                            loopFlows.add(f2);
                            loopFlows.add(mid.getOutgoing().get(0));
                            oc = gm;
                        }
                    }
                }
            }
            if (isLoop){
                candidates.add(oc);
                List<SequenceFlow> outFlowCopy = new ArrayList<>(ic.getOutgoing());
                outFlowCopy.removeAll(loopFlows);
                List<SequenceFlow> inFlowCopy = new ArrayList<>(oc.getIncoming());
                inFlowCopy.removeAll(loopFlows);

                // component can only have 1 in and 
                if (inFlowCopy.size() > 1 || outFlowCopy.size() > 1) continue;
                Component c = new RepeatComponent();
                
                candidates.remove(oc);
                candidates.remove(ic);
                FlowNode start = mapToWrapper(oc);
                FlowNode end = mapToWrapper(ic);
                candidates.add(start);
                candidates.add(end);
                c.setStart(start);
                c.setEnd(end);
                
                c.setElements(List.copyOf(candidates).stream().map(x -> mapToWrapper(x)).toList());

                c.getIncoming().clear();
                c.getOutgoing().clear();
                c.getIncoming().addAll(inFlowCopy);
                c.getOutgoing().addAll(outFlowCopy);
                
                return c;
            }
        }
        return null;
    }
    
    private static Component findRepeatWhile(BPMN bpmn) {
    	System.out.println("Finding repeat while..");
        
        for (Gateway sink : bpmn.getGm().values()) {
            boolean isLoop = false;
            List<SequenceFlow> loopFlows = new ArrayList<>();
            List<FlowNode> candidates = new ArrayList<>();
            candidates.add(sink);

            FlowNode mid = sink.getOutgoing().get(0).getTargetRef();
            if (!hasOneInOut(mid)) continue;
            candidates.add(mid);

            FlowNode gd = mid.getOutgoing().get(0).getTargetRef();
            if (bpmn.getGd().containsValue(gd)) {
                Gateway diverging = (Gateway) gd;

                for (SequenceFlow f : diverging.getOutgoing()) {
                	FlowNode mid2 = f.getTargetRef();
                    if (!hasOneInOut(mid2)) continue;

                    if (mid2.getOutgoing().get(0).getTargetRef().equals(sink)) {
                        isLoop = true;
                        candidates.add(mid2);
                        loopFlows.add(f);
                        loopFlows.add(mid2.getOutgoing().get(0));
                    }
                }

                if (isLoop) {
                    candidates.add(gd);
                    List<SequenceFlow> inFlowCopy = new ArrayList<>(sink.getIncoming());
                    inFlowCopy.removeAll(loopFlows);
                    List<SequenceFlow> outFlowCopy = new ArrayList<>(gd.getOutgoing());
                    outFlowCopy.removeAll(loopFlows);

                    // component can only have 1 in and 
                    if (inFlowCopy.size() > 1 || outFlowCopy.size() > 1) continue;
                    
                    Component c = new WhileRepeatComponent();
                    candidates.remove(sink);
                    candidates.remove(gd);
                    FlowNode start = mapToWrapper(sink);
                    FlowNode end = mapToWrapper(gd);
                    candidates.add(start);
                    candidates.add(end);
                    c.setStart(start);
                    c.setEnd(end);
                    c.setElements(candidates.stream().map(x -> mapToWrapper(x)).toList());
                    c.getIncoming().addAll(inFlowCopy);
                    c.getOutgoing().addAll(outFlowCopy);
                    return c;
                }
            }
        } 

        return null;
    }
    
    /**
     * Find Switch Components, the one with event gateway
     */
    private static Component findSwitch(BPMN bpmn) {
        System.out.println("Finding switch..");
        for (Gateway ic : bpmn.getGd().values()) {

            List<FlowNode> candidates = new ArrayList<>();
            candidates.add(ic);

            List<SequenceFlow> outFlows = ic.getOutgoing();
            
            if (outFlows.size() < 1) continue; // not a FLOW

            Gateway oc = null;

            for (SequenceFlow f : outFlows) {
                FlowNode mid = f.getTargetRef();
                
                // Branch doesn't have any task in the middle
                if (oc != null && mid.equals(oc)) {
                	continue;
                }

                // Must be Task or Intermediate Event
                if (!(hasOneInOut(mid)) || (mid instanceof StartEvent) || (mid instanceof EndEvent)) {
                    // Found an empty branch on first flow checked
                	if (oc == null && mid instanceof Gateway) {
                    	oc = (Gateway) mid;
                    	continue;
                    }
                    oc = null;
                    break;
                }

                FlowNode target = mid.getOutgoing().get(0).getTargetRef();

                // All branches must converge to same join
                if (oc == null) {
                    if (!(target instanceof ExclusiveGateway || target instanceof InclusiveGateway) || ((target instanceof ExclusiveGateway || target instanceof InclusiveGateway) && ((Gateway)target).getOutgoing().size() > 1)) break;
                    oc = (Gateway) target;
                } else if (!oc.equals(target)) {
                    oc = null;
                    break;
                }

                candidates.add(mid);
            }

            if (oc == null) continue;

            candidates.add(oc);
            
            Gateway oc2 = oc;
            // Verify oc incoming flows
            if (!oc.getIncoming().stream()
                .map(SequenceFlow::getSourceRef)
                .filter(x -> x != ic && x != oc2)
                .collect(Collectors.toSet())
                .equals(
                    candidates.stream()
                                .filter(x -> x != ic && x != oc2)
                                .collect(Collectors.toSet())
                )) {
                continue;
            }

            Component c = new SwitchComponent();
            candidates.remove(ic);
            candidates.remove(oc);
            FlowNode start = mapToWrapper(ic);
            FlowNode end = mapToWrapper(oc);
            candidates.add(start);
            candidates.add(end);
            c.setStart(start);
            c.setEnd(end);
            c.setElements(candidates.stream().map(x -> mapToWrapper(x)).toList());
            c.getIncoming().addAll(List.of(start.getIncoming().get(0)));
            c.getOutgoing().addAll(List.of(end.getOutgoing().get(0)));
            return c;
        }

        return null;
    }
    
    /**
     * Find Pick Components, the one with data gateway
     */
    private static Component findPick(BPMN bpmn) {
    	System.out.println("Finding pick..");
        for (Gateway ic : bpmn.getGv().values()) {

            List<FlowNode> candidates = new ArrayList<>();
            candidates.add(ic);

            List<SequenceFlow> outFlows = ic.getOutgoing();

            Gateway oc = null;

            for (SequenceFlow f : outFlows) {
            	FlowNode curr = f.getTargetRef();

                List<FlowNode> tempCandidates = new ArrayList<>();

                while (hasOneInOut(curr) && 
                    !(curr instanceof EndEvent) && 
                    !(curr instanceof StartEvent)) {
                    tempCandidates.add(curr);
                    curr = curr.getOutgoing().get(0).getTargetRef();   
                }

                // curr bukan gateway ujung
                if (!bpmn.getGm().containsValue(curr)) {
                    tempCandidates.clear();
                    oc = null;
                } else{
                    // curr itu gateway ujung
                    if (oc == null) {
                        oc = (Gateway) curr;
                    } else {
                        if (!oc.equals(curr)) {
                            oc = null;
                            candidates.clear();
                            break;
                        }
                    }
                }

                candidates.addAll(tempCandidates);
            }

            if (oc == null) continue;

            // oc must be a join gateway
            if (!bpmn.getGm().containsValue(oc)) continue;

            candidates.add(oc);
            
            Gateway oc2 = oc;
            // Verify oc incoming flows
            if (!oc.getIncoming().stream()
                .map(SequenceFlow::getSourceRef)
                .allMatch( y ->
                    candidates.stream()
                                .filter(x -> x != ic && x != oc2)
                                .collect(Collectors.toSet()).contains(y)
                )) {
                continue;
            }

            Component c = new PickComponent();
            candidates.remove(ic);
            candidates.remove(oc);
            FlowNode start = mapToWrapper(ic);
            FlowNode end = mapToWrapper(oc);
            candidates.add(start);
            candidates.add(end);
            c.setStart(start);
            c.setEnd(end);
            c.setElements(candidates.stream().map(x -> mapToWrapper(x)).toList());
            c.getIncoming().addAll(List.of(ic.getIncoming().get(0)));
            c.getOutgoing().addAll(List.of(oc.getOutgoing().get(0)));
            return c;
        }

        return null;
    }


    /**
     * Find NonWellStructured Components, whatever else type of component
     */
    private static Component findMinNonWellStructuredComponent(BPMN bpmn) {
        System.out.println("Finding NonWellStructuredComponent");
        Component sese = findSESE(bpmn);
        NonStructuredComponent c = new NonStructuredComponent();
        c.setPreConds(allPreCondSets(sese));
        c.setEnd(sese.getEnd());
        c.setStart(sese.getStart());
        c.getIncoming().addAll(sese.getIncoming());
        c.getOutgoing().addAll(sese.getOutgoing());
        return c;
    }

    /**
     * Util function to set what component the task / component is in
     */
    private static void setOwnerComponent(Component component) {
        for (FlowNode e : component.getElements()) {
        	if (e instanceof OwnedComponent t) {
        		t.setOwnerComponent(component);
        	}
        }
    }
    
    /**
     * Util function to find single entrance and single exit components for nonwellstructured
     */
    private static Component findSESE(BPMN bpmn) {
        List<Component> result = new ArrayList<>();
        
        for (Gateway ic : bpmn.getG().values()) {
            for (Gateway oc : bpmn.getG().values()) {

                Set<FlowNode> region = collectNodesBetween(ic, oc);

                if (region == null) continue;
                if (!allPathsConverge(ic, oc, region)) continue;

                if (region.size() <= 2) continue;
                
                Component c = new NonStructuredComponent();
                c.setStart(ic);
                c.setEnd(oc);
                c.setElements(List.copyOf(region).stream().map(x-> {
    				if (x instanceof Task task && 
    						!(x instanceof Component) &&
    						!(x instanceof TaskWrapper)) {
    					TaskWrapper tw = new TaskWrapper();
    					tw.setDelegate(task);
    					tw.setTaskType(task);
    					List<SequenceFlow> temp = List.copyOf(task.getIncoming());
    					for (SequenceFlow flow : temp) {
    						flow.setTargetRef(tw);
    					}
    					temp = List.copyOf(task.getOutgoing());
    					for (SequenceFlow flow : temp) {
    						flow.setSourceRef(tw);
    					}
    					return tw;
    				}
    				return (FlowNode)x;
				}).toList());
                c.getOutgoing().addAll(oc.getOutgoing());
                c.getIncoming().addAll(ic.getIncoming());
                result.add(c);
            }
        }
        return filterMinimal(result).get(0);
    }

    /**
     * Util function to help findSESE to guarantee all path from start element can reach end element
     */
    private static boolean allPathsConverge(
        Gateway ic,
        Gateway oc,
        Set<FlowNode> region) {

        HashSet<FlowNode> visited = new HashSet<>();
        
        int count = 0;
        for (FlowNode n : region) {
            for (SequenceFlow out : n.getOutgoing()) {

            	FlowNode tgt = out.getTargetRef();

                if (oc.equals(n)) {
                    if (!region.contains(tgt)) {
                        count += 1;
                    } 
                    if (count > 1) {
                        return false;
                    }
                } else if (!pathConvergesToOC(tgt, oc, region, visited)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Util function to help DFS on a single path towards end element on a region
     */
    private static boolean pathConvergesToOC(
        FlowNode current,
        Gateway oc,
        Set<FlowNode> region,
        Set<FlowNode> visited) {

        // Prevent infinite loops
        if (!visited.add(current)) {
            return true; // loop is OK, doesn't violate convergence
        }

        // If we reached oc → valid
        if (current.equals(oc)) {
            return true;
        }

        // If we exit region NOT via oc → invalid
        if (!region.contains(current)) {
            return false;
        }

        // Dead-end inside region → invalid
        if (current.getOutgoing().isEmpty()) {
            return false;
        }

        boolean result = true;
        // All outgoing paths must converge
        for (SequenceFlow out : current.getOutgoing()) {
            result &= pathConvergesToOC(out.getTargetRef(), oc, region, visited);
        }

        return result;
    }

    /**
     * Util function to help findSESE to collect all nodes between two elements that are visitable by each other
     */
    private static Set<FlowNode> collectNodesBetween(
        Gateway ic,
        Gateway oc) {

        Set<FlowNode> fromIc = new HashSet<>();
        fromIc.add(oc);
        Set<FlowNode> toOc   = new HashSet<>();
        toOc.add(ic);

        Util.forwardDFS(ic, fromIc);
        Util.backwardDFS(oc, toOc);


        if (fromIc.stream().anyMatch(x -> toOc.contains(x)) && 
            toOc.stream().anyMatch(y -> fromIc.contains(y))) {
                return fromIc;
        }
        return null;
    }
    
    /**
     * Util function to take smallest available non well structured component
     */
    private static List<Component> filterMinimal(List<Component> candidates) {
        List<Component> minimal = new ArrayList<>();

        for (Component c : candidates) {
            boolean isMinimal = true;

            List<FlowNode> nodesC = c.getElements();

            for (Component other : candidates) {
                if (c == other) continue;

                List<FlowNode> nodesO = other.getElements();

                // Strict containment
                if (nodesC.containsAll(nodesO) &&
                    nodesC.size() > nodesO.size()) {

                    isMinimal = false;
                    break;
                }
            }

            if (isMinimal) {
                minimal.add(c);
            }
        }

        return minimal;
    }

    private static Map<FlowNode, List<PreCond>> allPreCondSets(Component component){
        return component.getElements().stream()
        .collect(Collectors.toMap(
            el -> el,
            el -> preCondSet(el, component)
        ));
    }

    private static List<PreCond> preCondSet(FlowNode el, Component c) {
        if (el instanceof ExclusiveGateway d && d.getOutgoing().size() == 1) {
            return d.getIncoming().stream().map(x -> eventOnFlow(x, c)).toList();
        } else if (el instanceof ParallelGateway p) {
            return p.getIncoming().stream().map(x -> eventOnFlow(x, c)).toList();
        } else {
            return List.of(eventOnFlow(el.getIncoming().get(0), c));
        }
    }

    private static PreCond eventOnFlow(SequenceFlow f, Component c) {
        FlowNode xs = f.getSourceRef();
        FlowNode x = f.getTargetRef();
        if (!c.getElements().contains(xs)) {
        	PreCond p = new StartPreCond();
        	p.setX(x);
        	p.setXs(null);
        	return p;
        } else if (x instanceof Task || 
            x instanceof Event || 
            (x instanceof ExclusiveGateway d && d.getOutgoing().size() == 1) || 
            (x instanceof ParallelGateway d && d.getOutgoing().size() == 1)
        ) {
        	PreCond p = new EndPreCond();
        	p.setX(null);
        	p.setXs(xs);
        	return p;
        } else if (x instanceof ParallelGateway) {
        	PreCond p = new FlowPreCond();
        	p.setX(x);
        	p.setXs(xs);
        	return p;
        } else if (x instanceof ExclusiveGateway) {
        	SwitchPreCond p = new SwitchPreCond();
        	p.setX(x);
        	p.setXs(xs);
        	p.c = f.getName();
        	return p;
        } else if (x instanceof EventBasedGateway) {
        	PreCond p = new PickPreCond();
        	p.setX(x);
        	p.setXs(xs);
        	return p;
        }
        return null;
    }
    
    private static String printComponent(Component c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Component ").append(c.getName()).append("\n");
        sb.append("  Entry: ").append(c.getIncoming().stream().map(x -> x.getSourceRef()).filter(x -> !x.equals(c)).map(x -> x.getName()).toList()).append("\n");
        for (FlowNode n : c.getElements()) {
            sb.append("  ").append(isBlankOrNull(n.getName()) ? n.getId() : n.getName()).append(" " + n.getClass() + " ").append("\n");
        }

        sb.append("  Exit : ").append(c.getOutgoing().stream().map(x->x.getTargetRef()).filter(x -> !x.equals(c)).map(x -> x.getName()).toList()).append("\n\n");

        return sb.toString();
    }
    
    private static boolean isBlankOrNull(String s) {
        return s == null || "".equals(s);
    }
    
    private static void removeAllElements(BPMN bpmn, Component component) {
        bpmn.getG().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof GatewayWrapper)
	            .map(el -> (GatewayWrapper) el)
	            .anyMatch(gw -> gw.getDelegate() == e.getValue())
	    );
        bpmn.getGf().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof GatewayWrapper)
	            .map(el -> (GatewayWrapper) el)
	            .anyMatch(gw -> gw.getDelegate() == e.getValue())
	    );
        bpmn.getGd().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof GatewayWrapper)
	            .map(el -> (GatewayWrapper) el)
	            .anyMatch(gw -> gw.getDelegate() == e.getValue())
	    );
        bpmn.getGv().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof GatewayWrapper)
	            .map(el -> (GatewayWrapper) el)
	            .anyMatch(gw -> gw.getDelegate() == e.getValue())
	    );
        bpmn.getGj().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof GatewayWrapper)
	            .map(el -> (GatewayWrapper) el)
	            .anyMatch(gw -> gw.getDelegate() == e.getValue())
	    );
        bpmn.getGm().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof GatewayWrapper)
	            .map(el -> (GatewayWrapper) el)
	            .anyMatch(gw -> gw.getDelegate() == e.getValue())
	    );
        bpmn.getT().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof TaskWrapper)
	            .map(el -> (TaskWrapper) el)
	            .anyMatch(tw -> tw.getDelegate() == e.getValue())
	    );
        bpmn.getTr().entrySet().removeIf(e ->
	        component.getElements().contains(e.getValue()) ||
	        component.getElements().stream()
	            .filter(el -> el instanceof TaskWrapper)
	            .map(el -> (TaskWrapper) el)
	            .anyMatch(tw -> tw.getDelegate() == e.getValue())
	    );
        bpmn.getE().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getEi().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getEet().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
    }
    
    private static FlowNode mapToWrapper(FlowNode x) {
    	if (x instanceof Task task && 
				!(x instanceof Component) &&
				!(x instanceof TaskWrapper)) {
			TaskWrapper tw = new TaskWrapper();
			tw.setDelegate(task);
			tw.setTaskType(task);
			List<SequenceFlow> temp = List.copyOf(x.getIncoming());
			for (SequenceFlow flow : temp) {
				flow.setTargetRef(tw);
			}
			temp = List.copyOf(x.getOutgoing());
			for (SequenceFlow flow : temp) {
				flow.setSourceRef(tw);
			}
			return tw;
		}
    	if (x instanceof Gateway g &&
				!(x instanceof GatewayWrapper)) {
    		GatewayWrapper gw = new GatewayWrapper();
			gw.setDelegate(g);
			gw.setGatewayType(g);
			List<SequenceFlow> temp = List.copyOf(x.getIncoming());
			for (SequenceFlow flow : temp) {
				flow.setTargetRef(gw);
			}
			temp = List.copyOf(x.getOutgoing());
			for (SequenceFlow flow : temp) {
				flow.setSourceRef(gw);
			}
			return gw;
		}
		return x;
    }
}
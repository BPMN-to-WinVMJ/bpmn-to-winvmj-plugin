package bpmn.to.winvmj.acceleo.java;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.*;

import bpmn.to.winvmj.acceleo.java.model.*;

public class BPMNParser {
    private static final String COMPONENT_STRING = "tc";
    private static int componentCount = 0;
    
    private static BPMN bpmn;
    
    public static BPMN getBPMN() {
    	return bpmn;
    }

    public static BPMN parse(org.eclipse.bpmn2.Process process) throws Exception {
    	BPMN v = new BPMN();
        try {
            BPMNWrapper wrapper = new BPMNWrapper();
            addElements(wrapper, process);
            bpmn = wrapper.getBPMN();
            return bpmn;
        } catch (Exception e) {
            e.printStackTrace(); // will print to Eclipse console
            throw e;
        }
    }

    public static void addElements(BPMNWrapper wrapper, org.eclipse.bpmn2.Process process) throws Exception {

        // Classify all flow elements
        for (FlowElement fe : process.getFlowElements()) {

            // Tasks
            if (fe instanceof Task task) {
                wrapper.getO().put(task.getId(), task);
                wrapper.getT().put(task.getId(), task);
                if (task instanceof ReceiveTask rt) {
                    wrapper.getTr().put(rt.getId(), rt);
                }
            }

            // Events
            else if (fe instanceof StartEvent se) {
                wrapper.getO().put(se.getId(), se);
                wrapper.getE().put(se.getId(), se);
                wrapper.getEs().put(se.getId(), se);
            }
            else if (fe instanceof EndEvent ee) {
                wrapper.getO().put(ee.getId(), ee);
                wrapper.getE().put(ee.getId(), ee);
                wrapper.getEe().put(ee.getId(), ee);
            }
            else if (fe instanceof IntermediateCatchEvent ice) {
                wrapper.getO().put(ice.getId(), ice);
                wrapper.getE().put(ice.getId(), ice);
                wrapper.getEi().put(ice.getId(), ice);
                boolean isTimer = ice.getEventDefinitions().stream()
                    .anyMatch(ed -> ed instanceof TimerEventDefinition);
                if (isTimer) {
                    wrapper.getEet().put(ice.getId(), ice);
                }
            }
            else if (fe instanceof IntermediateThrowEvent ite) {
                wrapper.getO().put(ite.getId(), ite);
                wrapper.getE().put(ite.getId(), ite);
            }

            // Gateways
            else if (fe instanceof Gateway gw) {
                wrapper.getG().put(gw.getId(), gw);
                wrapper.getO().put(gw.getId(), gw);
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

                wrapper.getF().put(flow.getId(), flow);
            }
        }

        // Classify gateways
        for (Gateway g : wrapper.getG().values()) {
            int inSize = g.getIncoming().size();
            int outSize = g.getOutgoing().size();

            if (g instanceof ParallelGateway pg && inSize == 1 && outSize >= 1) {
                pg.setGatewayDirection(GatewayDirection.DIVERGING);
                wrapper.getGf().put(g.getId(), pg);
            } else if (g instanceof ParallelGateway pg && inSize >= 1 && outSize == 1) {
                pg.setGatewayDirection(GatewayDirection.CONVERGING);
                wrapper.getGj().put(g.getId(), pg);
            } else if (g instanceof ExclusiveGateway eg && inSize == 1 && outSize > 1) {
                eg.setGatewayDirection(GatewayDirection.DIVERGING);
                wrapper.getGd().put(g.getId(), eg);
            } else if (g instanceof ExclusiveGateway eg && inSize > 1 && outSize == 1) {
                eg.setGatewayDirection(GatewayDirection.CONVERGING);
                wrapper.getGm().put(g.getId(), eg);
            } else if (g instanceof EventBasedGateway evg && inSize == 1 && outSize > 1) {
                evg.setGatewayDirection(GatewayDirection.DIVERGING);
                wrapper.getGv().put(g.getId(), evg);
            } else {
                throw new Exception(String.format(
                    "Gateway %s is both diverging and merging", g.getName()));
            }
        }

        // Now group elements into BPMN components and store in wrapper.getBpmn()
        // This is where the structural analysis (grouping into SequenceComponent,
        // SwitchComponent, FlowComponent, etc.) will happen
        BPMN bpmn = wrapper.getBPMN();
        bpmn.setId(process.getId());
        bpmn.setName(process.getName());
        
        loopFold(wrapper);
    }

    public static void loopFold(BPMNWrapper wrapper) throws Exception {

        // All foldable elements
        List<FlowNode> X = new ArrayList<>(wrapper.getO().values());

        // Remove start and end events from foldable
        X.remove(wrapper.getEs().values().iterator().next());
        X.remove(wrapper.getEe().values().iterator().next());

        BPMN bpmn = wrapper.getBPMN();

        PriorityQueue<Component> seqComponents = findMaxSequence(wrapper);

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

                System.out.println("a" + exitNode.getOutgoing().isEmpty());

                // Wire incoming/outgoing onto component
                component.getIncoming().clear();
                component.getOutgoing().clear();
                component.getIncoming().addAll(enterNode.getIncoming());
                component.getOutgoing().addAll(exitNode.getOutgoing());
                
                System.out.println("c" + exitNode.getOutgoing().isEmpty());

                setOwnerComponent(component);

                Set<String> elementIds = component.getElements().stream()
                	    .map(e -> e.getId())
                	    .collect(Collectors.toSet());
                
            	X.removeIf(e -> elementIds.contains(e.getId()));
                X.add(component);

                // Remove folded elements from wrapper maps
                wrapper.getT().entrySet().removeIf(e -> elementIds.contains(e.getValue().getId()));
                wrapper.getTr().entrySet().removeIf(e -> elementIds.contains(e.getValue().getId()));
                wrapper.getEi().entrySet().removeIf(e -> elementIds.contains(e.getValue().getId()));

                System.out.println(printComponent(component));
                continue;
            }

            Component nonSeq = findMaxNonSequence(wrapper);
            if (nonSeq != null) {
                componentCount++;
                nonSeq.setName(COMPONENT_STRING + componentCount);
                nonSeq.setId(COMPONENT_STRING + componentCount);

                for (SequenceFlow entering : nonSeq.getIncoming()) {
                    entering.setTargetRef(nonSeq);
                }
                for (SequenceFlow exiting : nonSeq.getOutgoing()) {
                    exiting.setSourceRef(nonSeq);
                }

                setOwnerComponent(nonSeq);

                Set<String> elementIds = component.getElements().stream()
                	    .map(e -> e.getId())
                	    .collect(Collectors.toSet());

            	X.removeIf(e -> elementIds.contains(e.getId()));
                X.add(nonSeq);
                removeAllElements(wrapper, nonSeq);

                System.out.println(printComponent(nonSeq));
                seqComponents = findMaxSequence(wrapper);

            } else {
                Component nonWellStructured = findMinNonWellStructuredComponent(wrapper);
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

                Set<String> elementIds = component.getElements().stream()
                	    .map(e -> e.getId())
                	    .collect(Collectors.toSet());

            	X.removeIf(e -> elementIds.contains(e.getId()));
                X.add(nonWellStructured);
                removeAllElements(wrapper, nonWellStructured);

                seqComponents = findMaxSequence(wrapper);
            }
        }
        System.out.println("done");
        bpmn.getElements().add(X.get(0));
    }
    
    private static PriorityQueue<Component> findMaxSequence(BPMNWrapper wrapper) {
        System.out.println("Finding max sequence..");
        PriorityQueue<Component> components = new PriorityQueue<>();

        List<FlowElement> elements = new ArrayList<>();
        elements.addAll(wrapper.getT().values());
        elements.addAll(wrapper.getEi().values());

        for (FlowElement fe : elements) {
            // Only BPMNElements have in/out flows
            if (!(fe instanceof FlowNode node)) continue;
            if (node.getIncoming().isEmpty()) continue;

            SequenceFlow inFlow = node.getIncoming().get(0);
            FlowNode source = inFlow.getSourceRef();

            // If previous element is event gateway -> is a pick, skip
            if (wrapper.getGv().containsValue(source)) continue;

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
                if (!hasOneInOut(target) || wrapper.getEe().containsValue(target)) break;

                current = target;
            }

            if (!isLoop && visited.size() >= 2) {
                SequenceComponent sc = new SequenceComponent();
                sc.getElements().addAll(
                		visited.stream().map(
                					x -> {
                							if (x instanceof Task task) {
                								TaskWrapper tw = new TaskWrapper();
                								tw.setDelegate(task);
                								return tw;
                							}
                							return x;
                						}
                				).toList());
                sc.setStart(visited.get(0));
                sc.setEnd(visited.get(visited.size() - 1));
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
        return node.getIncoming().size() == 1 && node.getOutgoing().size() == 1;
    }

    private static Component findMaxNonSequence(BPMNWrapper wrapper) {
        // TODO: implement — find maximal non-sequential structure
        // (e.g. SwitchComponent, FlowComponent)
        throw new UnsupportedOperationException("findMaxNonSequence not yet implemented");
    }

    private static Component findMinNonWellStructuredComponent(BPMNWrapper wrapper) {
        // TODO: implement — find smallest non-well-structured component
        // (e.g. NonStructuredComponent)
        throw new UnsupportedOperationException("findMinNonWellStructuredComponent not yet implemented");
    }

    private static void setOwnerComponent(Component component) {
        for (FlowNode e : component.getElements()) {
        	if (e instanceof TaskWrapper t) {
        		t.setOwnerComponent(component);
        	}
        }
    }
    
    private static String printComponent(Component c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Component ").append(c.getName()).append("\n");
        System.out.println(c.getName());
        sb.append("  Entry: ").append(c.getIncoming().stream().map(x->x.getSourceRef()).filter(x -> !x.equals(c)).map(x -> x.getName()).toList()).append("\n");

        for (FlowNode n : c.getElements()) {
            sb.append("  ").append(isBlankOrNull(n.getName()) ? n.getId() : n.getName()).append("\n");
        }

        sb.append("  Exit : ").append(c.getOutgoing().stream().map(x->x.getTargetRef()).filter(x -> !x.equals(c)).map(x -> x.getName()).toList()).append("\n\n");

        return sb.toString();
    }
    
    private static boolean isBlankOrNull(String s) {
        return s == null || "".equals(s);
    }
    
    private static void removeAllElements(BPMNWrapper bpmn, Component component) {
        bpmn.getG().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getGf().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getGm().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getGd().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getGv().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getGj().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getT().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getTr().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getE().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getEi().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
        bpmn.getEet().entrySet().removeIf(e -> component.getElements().contains(e.getValue()));
    }
}
package bpmn.to.winvmj.acceleo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;

import bpmn.to.winvmj.acceleo.java.BPMNParser;
import bpmn.to.winvmj.acceleo.java.Util;
import bpmn.to.winvmj.acceleo.java.model.BPMN;
import bpmn.to.winvmj.acceleo.java.model.Component;
import bpmn.to.winvmj.acceleo.java.model.Continuable;
import bpmn.to.winvmj.acceleo.java.model.FlowComponent;
import bpmn.to.winvmj.acceleo.java.model.GatewayType;
import bpmn.to.winvmj.acceleo.java.model.GatewayWrapper;
import bpmn.to.winvmj.acceleo.java.model.Looping;
import bpmn.to.winvmj.acceleo.java.model.OwnedComponent;
import bpmn.to.winvmj.acceleo.java.model.RepeatComponent;
import bpmn.to.winvmj.acceleo.java.model.SwitchComponent;
import bpmn.to.winvmj.acceleo.java.model.TaskType;
import bpmn.to.winvmj.acceleo.java.model.TaskWrapper;
import bpmn.to.winvmj.acceleo.java.model.WhileComponent;
import bpmn.to.winvmj.acceleo.java.model.WhileRepeatComponent;

public class GenerateQuery {
	
	/*
	 * Used by acceleo query to get checker whether user can use the api or not
	 */
	public static String getPrior(FlowNode node, org.eclipse.bpmn2.Process process, Boolean startFromPrev) throws Exception {
		BPMN bpmn = getOrGenerateBPMN(process);
		
		FlowNode e = Util.findById(node.getId(), bpmn);
		if (e == null) {
			throw new IllegalArgumentException("ID " + node.getId() + "not found on folded BPMN");
		}
		
		System.out.println("[START] getPrior on node " + node.getName() + " " + node.getClass());

		List<TaskWrapper> after =  List.of();
		
		if (e instanceof OwnedComponent o && isFirstTaskOfLoop(o)) {
			after = new ArrayList<>(traverseForward(e.getOutgoing().get(0).getTargetRef(), process, false).stream().map(x -> (TaskWrapper) x).toList());
			after.add((TaskWrapper)e);
		}
		
		if (e.getIncoming().isEmpty() && e instanceof OwnedComponent) {
    		// current is the end of component find higher level component that has exit flow
    		System.out.println("[DEBUG][" +  e.getName() + "] getPrior's initiated on start of component");
    		while (e.getIncoming().isEmpty() && e instanceof OwnedComponent oc)  {
    			e = oc.getOwnerComponent();
    		}
    	}

		List<String> result = new ArrayList<>();
		
		FlowNode prev = e;
		if (startFromPrev) {
			prev = e.getIncoming().get(0).getSourceRef();
		}
        
        String join = " || ";
        Set<FlowNode> visited = new HashSet<>();
        if (prev instanceof Component c) {
        	if (c instanceof WhileComponent || c instanceof WhileRepeatComponent || c instanceof RepeatComponent) {
        		result.add(String.format("hasTaskState(processes, \"%s\")", c.getOutgoing().stream().map(x -> x.getName()).collect(Collectors.joining(" || "))));
        	}
        	if (c instanceof SwitchComponent) {
        		List<SequenceFlow> emptySequenceFlow = c.getEnd().getIncoming().stream().filter(x -> !(x.getSourceRef() instanceof Task)).toList();
        		if (!emptySequenceFlow.isEmpty()) {
        			result.addAll(emptySequenceFlow.stream().map(x -> String.format("hasTaskState(processes, \"%s\")", x.getName())).toList());
        			visited.add(c.getStart());
        		}
        	}
        	
            prev = c.getEnd();
            
        	if (c instanceof FlowComponent || prev instanceof ParallelGateway) {
                join = " && ";
            }
        	
            if (prev instanceof Component) {
            	result.addAll(getPriorHelper(prev, after, new HashSet<>()));
            } else if (prev instanceof TaskWrapper t) {
                result.add(String.format("hasTaskState(processes, \"%s\")",t.getName()));
            } else { // Gateway
            	// diverging gateway, use the condition as filter
            	if (prev.getOutgoing().size() > 1) {
            		result.add(String.format("hasTaskState(processes, \"%s\")",e.getIncoming().get(0).getName()));
            	} else {
            		result.addAll(getPriorHelper(prev, after, visited));
            	}
            }
        } else if (prev instanceof Task t && !after.contains(t)){
    		result.add(String.format("hasTaskState(processes, \"%s\")", t.getName()));
        } else if (prev instanceof GatewayWrapper g) {
            if (GatewayType.PARALLEL_GATEWAY.equals(g.getGatewayType()) && g.getIncoming().size() > 1) {
                join = " && ";
            }
            if (!GatewayType.PARALLEL_GATEWAY.equals(g.getGatewayType()) && g.getOutgoing().size() > 1) {
        		result.add(String.format("hasTaskState(processes, \"%s\")",e.getIncoming().get(0).getName()));
        	} else {
        		result.addAll(getPriorHelper(g, after, new HashSet<>()));
        	}
        } else if (prev instanceof Event g) {
            result.add(g.getName());
        }
        
        StringBuilder builder = new StringBuilder();
        
        // no additional filter
        if (result.isEmpty()) {
            builder.replace(0, 5, "true");
        }
        
        builder.append(String.join(join + "\r\n" + Util.SPACE, result));
        
        StringBuilder builder2 = new StringBuilder();
        builder2.append(builder.toString());
        
        System.out.println("[DONE] getPrior on userTask " + node.getName());
        
        return builder2.toString();
	}

	/*
	 * Used by acceleo query to get methods representation of task after the user task
	 */
	public static String getServiceTaskAfter(Task task, org.eclipse.bpmn2.Process process, String bpmnName) throws Exception {
		System.out.println("[START] getServiceTaskAfter " + task.getName());
		if (task.getName().equals("viewInvalidFormNotification")) {
			System.out.println();
		}
		BPMN bpmn = getOrGenerateBPMN(process);
		
		FlowNode e = Util.findById(task.getId(), bpmn);
		if (e == null) {
			throw new IllegalArgumentException("ID " + task.getId() + "not found on folded BPMN");
		}
		
		StringBuilder builder = new StringBuilder();
		
		FlowNode curr = null;
		// current is the end of component find higher level component that has exit flow
		if (e.getOutgoing().isEmpty() && e instanceof OwnedComponent oc1) {
			
			System.out.println("[DEBUG] getServiceTaskAfter started on component end");
			curr = oc1.getOwnerComponent();
    		while (curr.getOutgoing().isEmpty() && curr instanceof OwnedComponent oc)  {
    			curr = oc.getOwnerComponent();
    		}
    		
    		// owner component still cannot continue
    		if (!((Component)curr).canContinue()) {
    			curr = curr.getOutgoing().get(0).getTargetRef();
    		}
    		
    		// component doesn't have any task after it
    		if (!curr.getOutgoing().isEmpty() && curr.getOutgoing().get(0).getTargetRef() instanceof EndEvent) {
    			return builder.toString();
    		}
    	} else {
            curr = e.getOutgoing().get(0).getTargetRef();
    	}
		
		int indent = 0;
		
		Looping ownerLoop = Util.getOwnerLoop((OwnedComponent) e);
		
		Map<String, String> usedVariables = new HashMap<>();
		
		String result = getServiceTaskAfterHelper(builder, ownerLoop, curr, usedVariables, indent, bpmnName, new HashSet<>()).trim();
		if (result.endsWith("return res;")) {
			result = result.substring(0, result.length() - "return res;".length());
		}
        
        System.out.println("[DONE] getServiceTaskAfter " + task.getName());
        
        return result;
	}
	
	private static String getServiceTaskAfterHelper(
			StringBuilder builder,
			Looping ownerLoop, // this attribute is solely for usertasks in a loop sequence
			FlowNode curr,
			Map<String, String> usedVariables,
			int indent,
			String bpmnName,
			Set<FlowNode> visited
		) throws Exception {
		
        while (curr instanceof Continuable con && con.canContinue() && visited.add(curr)) {
        	if (ownerLoop != null && curr.equals(((Component)ownerLoop).getStart())) {
        		Component c = (Component)ownerLoop;
        		builder.append(c.getFromStartToUser(bpmnName, usedVariables, indent));
        		
        		curr = c;
        		// current is the end of component find higher level component that has exit flow
        		while (curr.getOutgoing().isEmpty() && curr instanceof OwnedComponent oc)  {
        			curr = oc.getOwnerComponent();
        		}
        		
    			curr = curr.getOutgoing().get(0).getTargetRef();
        		
        		if (curr instanceof EndEvent) {
        			return builder.toString();
        		}
            	
        	}
        	if (curr instanceof Component c) {
                builder.append(c.getFromStartToUser(bpmnName, usedVariables, indent));
            } else {
                if (curr instanceof TaskWrapper tw) {
                    builder.append(
                        String.format(Util.SPACE.repeat(indent) + 
                            "%sService.%s(requestBody, processid);\n", 
                            bpmnName.toLowerCase(), curr.getName()
                        )
                    );
                } else if (curr instanceof Gateway && curr.getOutgoing().size() > 1 || ( 
                		// if di bawah ini perlu untuk definisiin diverging gateway di loop component yang sebenarnya pasti punya >= 2 
                		// tapi sequence flow yang keluar dari komponen itu dimiliki oleh komponen bukan branch maka kalau end gateway nya punya outgoing.size() == 1, sebenarnya
                		// dia punya branching path tapi ke catatnya agak cacat aja.
                		((curr instanceof Gateway && curr.getOutgoing().size() == 1) &&
                		(((OwnedComponent) curr).getOwnerComponent() instanceof WhileComponent && ((OwnedComponent) curr).getOwnerComponent().getEnd().equals(curr)) ||
                		(((OwnedComponent) curr).getOwnerComponent() instanceof RepeatComponent && ((OwnedComponent) curr).getOwnerComponent().getEnd().equals(curr)) || 
                		((OwnedComponent) curr).getOwnerComponent() instanceof WhileRepeatComponent && ((OwnedComponent) curr).getOwnerComponent().getEnd().equals(curr)))) {
                    
                	boolean first = true;

                    for (SequenceFlow f : curr.getOutgoing()) {
                        FlowNode branchStart = f.getTargetRef();

                        StringBuilder builderTemp = new StringBuilder();

                        String res = getServiceTaskAfterHelper(builderTemp, ownerLoop, branchStart, usedVariables, indent + 1, bpmnName, visited);

                        if ((first || GatewayType.INCLUSIVE_GATEWAY.equals(((GatewayWrapper)curr).getGatewayType())) && !res.isEmpty()) {
                        	Set<String> variables = Util.extractVariablesFromExpression(f.getName());
                        	for (String var : variables) {
                        		String varType = Util.inferTypeFromVariable(var, f.getName());
                        		usedVariables.put(var, varType); 
                        	}
                            builder.append(Util.SPACE.repeat(indent) + 
                                String.format("if (%s) {\n", f.getName()))
                            .append(Util.SPACE.repeat(indent + 1) + 
                                    String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", f.getName()));
                            first = false;
                        } else if (!res.isEmpty()) {
                            builder.append(Util.SPACE.repeat(indent) + 
                                String.format("} else if (%s) {\n", f.getName()))
                            .append(Util.SPACE.repeat(indent + 1) + 
                                String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", f.getName()));
                        }
                        builder.append(builderTemp.toString().indent(indent));
                    }
                    if (!first){
                        builder.append(Util.SPACE.repeat(indent) + "}\n");
                    }
                        
                    return usedVariables.entrySet().stream()
                    .map(entry -> String.format("%s %s = true;\r\n", entry.getValue(), entry.getKey()))
                    .collect(Collectors.joining("")) + builder.toString();
                    
                // pasang safeguard paralelgateway 
                } else if (GatewayType.PARALLEL_GATEWAY.equals(((GatewayWrapper)curr).getGatewayType()) 
                		&& (curr.getOutgoing().size() == 1 || ((OwnedComponent)curr).getOwnerComponent().getEnd().equals(curr))) { 
                    builder.append(buildParallelSafeGuard(curr, indent));
                }
            }
        	if (curr.getOutgoing().isEmpty() && curr instanceof OwnedComponent) {
        		// current is the end of component find higher level component that has exit flow
        		while (curr.getOutgoing().isEmpty() && curr instanceof OwnedComponent oc)  {
        			curr = oc.getOwnerComponent();
        		}
        	}
            curr = curr.getOutgoing().get(0).getTargetRef();
        }

        // Loops break on un-continuable component, but that component might still have some call-able tasks
        if (curr instanceof Component c && !c.canContinue()) {
        	System.out.println("last current is component " + c.getClass());
        	builder.append(c.getFromStartToUser(bpmnName, usedVariables, indent));
    	}
        
        return usedVariables.entrySet().stream()
        .map(entry -> String.format("%s %s = true;\r\n", entry.getValue(), entry.getKey()))
        .collect(Collectors.joining("")) + builder.toString();
	}
	
    private static boolean isFirstTaskOfLoop(OwnedComponent e) {
        Component parent = e.getOwnerComponent();
        while (
            parent != null
            && !(parent instanceof WhileRepeatComponent)
            && !(parent instanceof WhileComponent)
            && !(parent instanceof RepeatComponent)
        ) {
            parent = parent.getOwnerComponent();
        }
        if (parent == null) return false;
    	return ((FlowNode)e).getIncoming().size() == 0 || parent.getFirstTask().contains(e);
    }
    
    // Loops must have unique canContinueFrom as it needs to stop searching when target gateway is found
    public static boolean canContinueFrom(FlowNode curr, Set<FlowNode> visited, FlowNode target) {
    	if (target.equals(curr)) {
    		return true;
    	}
    	
        // prevent infinite loops
        if (!visited.add(curr)) {
            return false;
        }

        // this element itself blocks continuation
        if (curr instanceof Continuable && !((Continuable)curr).canContinue()) {
            return false;
        }

        // no outgoing flow = end node → still valid continuation
        if (curr.getOutgoing().isEmpty()) {
            return true;
        }

        // OR semantics: if ANY outgoing branch can continue, we're good
        for (SequenceFlow f : curr.getOutgoing()) {
            if (canContinueFrom(f.getTargetRef(), visited, target)) {
                return true;
            }
        }

        // all downstream branches are blocked
        return false;
    }
	
    // Used by getPrior to filter out tasks in a loop
    public static List<Task> traverseForward(FlowNode e, org.eclipse.bpmn2.Process p, Boolean unwrap) throws Exception {
		BPMN bpmn = getOrGenerateBPMN(p);
		
		e = Util.findById(e.getId(), bpmn);
		
        Set<FlowNode> visited = new HashSet<>();
        List<FlowNode> q = new ArrayList<>();
        Set<Task> res = new HashSet<>();
        q.add(e);

        while (!q.isEmpty()) {
        	FlowNode curr = q.remove(0);
            if (!visited.add(curr)) {
                continue;
            }
            if (curr instanceof TaskWrapper t) {
                res.add(t);
            } else if (curr instanceof Component c) {
                res.addAll(traverseForward(c.getStart(), p, unwrap));
            } else {
                q.addAll(curr.getOutgoing().stream().map(x -> x.getTargetRef()).toList());
            }
        }
        System.out.println("traverseForward result " + e.getName() + " " + res.stream().map(x->x.getName()).toList());
        return res.stream().map(x -> unwrap ? 
        		x instanceof TaskWrapper ?  ((TaskWrapper)x).getDelegate() : x
			: x).toList();
    }
    
    private static List<String> getPriorHelper(FlowNode e, List<TaskWrapper> after,  Set<FlowNode> visited) {
        List<FlowNode> q = new ArrayList<>();
        List<String> res = new ArrayList<>();
        
        System.out.println("[DEBUG] getPriorHelper on Element " + e.getName());
        
        q.add(e);

        while (!q.isEmpty()) {
        	FlowNode curr = q.remove(0);
            if (!visited.add(curr)) {
                continue;
            }
            if (curr instanceof TaskWrapper t) {
            	if (!after.contains(t)) {
            		res.add(String.format("hasTaskState(processes, \"%s\")",t.getName()));
            	}
            } else if (curr instanceof Component c) {
            	List<String> fromComponent = getPriorHelper(c.getEnd(), after, visited);
                if (fromComponent.isEmpty()) {
                	if (curr.getIncoming().isEmpty() && curr instanceof OwnedComponent) {
                		// current is the start of component find higher level component that has entry flow
                		while (curr.getIncoming().isEmpty() && curr instanceof OwnedComponent oc)  {
                			curr = oc.getOwnerComponent();
                		}
                	}
                	q.addAll(curr.getIncoming().stream().map(x -> x.getSourceRef()).toList());
                } else {
                	if (fromComponent.size() > 1) res.add("(" + String.join(c instanceof FlowComponent ? " && " : " || ", fromComponent) + ")");
                	else res.addAll(fromComponent);
                }
            } else {
                // Gateway found - collect incoming tasks and join based on gateway type
                List<String> incomingResults = new ArrayList<>();
                if (curr.getIncoming().isEmpty() || Util.isStartOfLoopComponent(curr)) {
            		// current is the start of component find higher level component that has exit flow
            		while ((curr.getIncoming().isEmpty() && curr instanceof OwnedComponent oc) || (Util.isStartOfLoopComponent(curr) && curr instanceof OwnedComponent oc))  {
            			curr = ((OwnedComponent) curr).getOwnerComponent();
            		}
                }
                
                for (FlowNode incoming : curr.getIncoming().stream().map(x -> x.getSourceRef()).toList()) {
                    List<String> subResults = getPriorHelper(incoming, after, visited);
                    if (subResults.size() > 1) {
                        String operator = curr instanceof ParallelGateway ? " && " : " || ";
                        incomingResults.add("("+String.join(operator, subResults)+")");
                    } else {
                        incomingResults.addAll(subResults);
                    }
                }
                res.addAll(incomingResults);
            }
        }
        return res;
    }
    
    // Used in  Component.getFromStartToUser to build a straight line of tasks without branchings
    public static void buildResource(
        StringBuilder builder,
        String bpmnName,
        FlowNode el,
        Set<FlowNode> visited,
        Map<String, String> usedVariables, 
        int indent
    ) {
    	System.out.println("[DEBUG] buildResource " + el.getName());
    	FlowNode curr = el;

        while (curr != null && visited.add(curr)) {

            if (curr instanceof TaskWrapper t && TaskType.isContinuable(t.getTaskType())) {
                builder.append(Util.SPACE.repeat(indent) + String.format(
                        "%sService.%s(requestBody, processid);\r\n", 
						bpmnName.toLowerCase(), curr.getName()
                    )
                );
            }
            
            // first blocking element ends the branch
            if (curr instanceof Component c) {
                builder.append(c.getFromStartToUser(bpmnName, usedVariables, indent));
                if (c instanceof Continuable con && !con.canContinue()) {
                	return;
                }
            } else if (curr instanceof Continuable con && !con.canContinue()) {
            	if (!Util.isInsideFlowComponent(curr)) builder.append(Util.SPACE.repeat(indent) + "return res;\r\n");
                return;
            }

            // on components, end objects have 0 outgoing elements
            if (curr.getOutgoing().isEmpty()) {
                return;
            }
            
            // handle loops
            if (curr instanceof Gateway) {
            	return;
            }

            curr = curr.getOutgoing().get(0).getTargetRef();
        }
    }
    
    public static String toValidVariableName(String input) {
        if (input == null || input.isEmpty()) {
            return "var";
        }
        
        // Split on common delimiters: spaces, slashes, hyphens, underscores, dots
        String[] parts = input.split("[\\s/\\-_.]+");
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            
            String part = parts[i].replaceAll("[^a-zA-Z0-9]", ""); // Remove any remaining non-alphanumeric
            
            if (part.isEmpty()) continue;
            
            if (i == 0) {
                // First part: lower case first letter
                result.append(Character.toLowerCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            } else {
                // Subsequent parts: capitalize first letter
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        
        // Ensure it starts with a letter (Java variable naming rule)
        if (result.length() == 0 || !Character.isLetter(result.charAt(0))) {
            result.insert(0, "var");
        }
        
        return result.toString();
    }
    
    public static String buildParallelSafeGuard(FlowNode curr, int indent) throws Exception {
    	StringBuilder builder = new StringBuilder();
        builder.append(Util.SPACE.repeat(indent + 1) + "List<ProcessInstance> processes = processService.getAllById(processid);\r\n");
        builder.append(Util.SPACE.repeat(indent + 1) + "if (!(");
        // Generate hasAllTaskStates check for all branches
        String parallelBranches = GenerateQuery.getPrior(curr, null, false);
        
        builder.append(parallelBranches);
        builder.append(")) {\n");
        builder.append(Util.SPACE.repeat(indent + 2) + "res.put(\"status\", \"fail\");\r\n");
        builder.append(Util.SPACE.repeat(indent + 2) + "res.put(\"message\", \"Parallel branches not complete\");\r\n");
        builder.append(Util.SPACE.repeat(indent + 2) + "return res;\r\n");
        builder.append(Util.SPACE.repeat(indent + 1) + "}\r\n");
        
        return builder.toString();
    }
    
    private static BPMN getOrGenerateBPMN(org.eclipse.bpmn2.Process process) throws Exception {
		if (BPMNParser.getBPMN() != null) {
			return BPMNParser.getBPMN();
		} else {
			if (process == null && BPMNParser.getBPMN() != null) {
				return BPMNParser.getBPMN();
			}
			BPMNParser.parse(process);
			return BPMNParser.getBPMN();
		}
    }
}

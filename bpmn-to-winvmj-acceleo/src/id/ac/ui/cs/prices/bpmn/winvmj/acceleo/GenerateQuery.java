package bpmn.to.winvmj.acceleo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;

import bpmn.to.winvmj.acceleo.java.BPMNParser;
import bpmn.to.winvmj.acceleo.java.Util;
import bpmn.to.winvmj.acceleo.java.model.BPMN;
import bpmn.to.winvmj.acceleo.java.model.Component;
import bpmn.to.winvmj.acceleo.java.model.FlowComponent;
import bpmn.to.winvmj.acceleo.java.model.RepeatComponent;
import bpmn.to.winvmj.acceleo.java.model.SequenceComponent;
import bpmn.to.winvmj.acceleo.java.model.SwitchComponent;
import bpmn.to.winvmj.acceleo.java.model.Variable;
import bpmn.to.winvmj.acceleo.java.model.WhileComponent;
import bpmn.to.winvmj.acceleo.java.model.WhileRepeatComponent;
import bpmn.to.winvmj.acceleo.java.model.modelutil.Continuable;
import bpmn.to.winvmj.acceleo.java.model.modelutil.FromStartToUserResult;
import bpmn.to.winvmj.acceleo.java.model.modelutil.GatewayType;
import bpmn.to.winvmj.acceleo.java.model.modelutil.GatewayWrapper;
import bpmn.to.winvmj.acceleo.java.model.modelutil.Looping;
import bpmn.to.winvmj.acceleo.java.model.modelutil.OwnSubProcess;
import bpmn.to.winvmj.acceleo.java.model.modelutil.OwnedComponent;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskType;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskWrapper;

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
        		String condition = c.getOutgoing().stream().map(x -> x.getName()).collect(Collectors.joining(" || "));
        		if (!condition.isBlank()) {
            		result.add(String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(condition)));
        		} else {
        			result.add(String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(c.getName())));
        		}
        	}
        	if (c instanceof SwitchComponent) {
        		// On switch component, if exist a path that consist only of sequence flow, then use that sequence flow name as prerequisite
        		List<SequenceFlow> emptySequenceFlow = c.getEnd().getIncoming().stream().filter(x -> !(x.getSourceRef() instanceof Task)).toList();
        		// Special case found when empty sequence flow starts immediately after start event -> don't add this
        		FlowNode temp = c;
        		while (temp.getIncoming().isEmpty() && temp instanceof OwnedComponent oc)  {
        			temp = oc.getOwnerComponent();
        		}
        		if (!emptySequenceFlow.isEmpty() && !(temp.getIncoming().get(0) instanceof StartEvent)) {
        			result.addAll(emptySequenceFlow.stream().map(seqFlow -> String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(seqFlow.getName()))).toList());
        			visited.add(c.getStart());
        		}
        	}
        	
        	// Get last element of previous component
            prev = c.getEnd();
            
        	if (c instanceof FlowComponent || prev instanceof ParallelGateway) {
                join = " && ";
            }
        	
            if (prev instanceof Component) {
            	result.addAll(getPriorHelper(prev, after, new HashSet<>()));
            } else if (prev instanceof TaskWrapper t) {
                result.add(String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(t.getName())));
            } else { // Gateway
            	// diverging gateway, use the condition as filter
            	if (prev.getOutgoing().size() > 1) {
            		result.add(String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(e.getIncoming().get(0).getName())));
            	} else { // converging, traverse backwards again
            		result.addAll(getPriorHelper(prev, after, visited));
            	}
            }
        
        } else if (prev instanceof Task t && !after.contains(t)){
    		result.add(String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(t.getName())));
        } else if (prev instanceof GatewayWrapper g) {
            if (GatewayType.PARALLEL_GATEWAY.equals(g.getGatewayType()) && g.getIncoming().size() > 1) {
                join = " && ";
            }
            // On parallel gateway, diverging branch does not have boolean requirements
            if (!GatewayType.PARALLEL_GATEWAY.equals(g.getGatewayType()) && g.getOutgoing().size() > 1) {
        		result.add(String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(e.getIncoming().get(0).getName())));
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
	 * Used by acceleo query to get method's body from subprocess
	 */
	public static String getSubProcessIn(FlowNode node, org.eclipse.bpmn2.Process process, String bpmnName) throws Exception {
		System.out.println("[START] getSubProcess " + node.getName());
		BPMN bpmn = getOrGenerateBPMN(process);
		
		OwnSubProcess resultingNode = (OwnSubProcess) Util.findById(node.getId(), bpmn);
		if (resultingNode == null) {
			throw new IllegalArgumentException("ID " + node.getId() + "not found on folded BPMN");
		}
		StringBuilder builder = new StringBuilder();
		for (SubProcess sp : resultingNode.getSubProcesses()) {
			builder.append(getServiceTaskAfter(resultingNode, sp, bpmnName));
		}
		System.out.println("[DONE] getSubProcess " + node.getName());
		return builder.toString();
	}
	
	/*
	 * Used by acceleo query to get methods representation of task after the user task
	 */
	public static String getServiceTaskAfter(FlowNode node, org.eclipse.bpmn2.FlowElementsContainer process, String bpmnName) throws Exception {
		boolean isProcess = process instanceof org.eclipse.bpmn2.Process;
		System.out.println(isProcess ? "[START] getServiceTaskAfter " + node.getName() : "[START] getServiceTaskAfter SubProcess " + node.getName());
		
		BPMN bpmn = getOrGenerateBPMN(process);
		
		FlowNode e;
		if (isProcess) e = Util.findById(node.getId(), bpmn);
		else e = node;
		
		if (e == null) {
			throw new IllegalArgumentException("ID " + node.getId() + "not found on folded BPMN");
		}
		
		StringBuilder builder = new StringBuilder();
		
		FlowNode curr = null;
		if (!isProcess) {
			System.out.println(bpmn.getEs().values().stream().toList().get(0).getName());
			curr = bpmn.getEs().values().stream().toList().get(0).getOutgoing().get(0).getTargetRef();
		}
		
		// current is the end of component find higher level component that has exit flow
		if (isProcess && e.getOutgoing().isEmpty() && e instanceof OwnedComponent oc1) {
			
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
    	} else if (isProcess) {
            curr = e.getOutgoing().get(0).getTargetRef();
    	}
		
		int indent = 0;
		
		Looping ownerLoop = Util.getOwnerLoop((OwnedComponent) e);
		
		Set<Variable> usedVariables = new HashSet<>();
		usedVariables.add(new Variable("response", ""));
		usedVariables.add(new Variable("res", ""));
		usedVariables.add(new Variable("requestBody", ""));
		usedVariables.add(new Variable("body", ""));
		if (e instanceof TaskWrapper tw) {
			for (Property property : tw.getProperties()) {
			    ItemDefinition itemDef = (ItemDefinition) property.getItemSubjectRef();
			    
			    if (itemDef == null) continue;
			    usedVariables.add(new Variable(property.getName(), ""));
			}
		}
		
		String result = getServiceTaskAfterHelper(builder, ownerLoop, curr, usedVariables, indent, bpmnName, new HashSet<>(), isProcess).trim();
		if (result.endsWith("return res;")) {
			result = result.substring(0, result.length() - "return res;".length());
		}
        
		System.out.println(isProcess ? "[DONE] getServiceTaskAfter " + node.getName() : "[START] getServiceTaskAfter SubProcess " + node.getName());
		
        
        return result;
	}
	
	private static String getServiceTaskAfterHelper(
			StringBuilder builder,
			Looping ownerLoop, // this attribute is solely for usertasks in a loop sequence
			FlowNode curr,
			Set<Variable> usedVariables,
			int indent,
			String bpmnName,
			Set<FlowNode> visited,
			boolean isProcess
		) throws Exception {
		
		int indentIfInclusive = 0;
        while (curr instanceof Continuable con && con.canContinue() && visited.add(curr)) {
        	// Current is gateway of a loop component
        	if (ownerLoop != null && curr.equals(((Component)ownerLoop).getStart())) {
        		Component c = (Component)ownerLoop;
        		FromStartToUserResult result = c.getFromStartToUser(bpmnName, usedVariables, indent, isProcess);
        		builder.append(result.getResult());
        		
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
        	if (curr instanceof Component co) {
        		FromStartToUserResult result = co.getFromStartToUser(bpmnName, usedVariables, indent, isProcess);
                builder.append(result.getResult());
                if (!result.getCanContinueInclusive() && Util.isInsideFlowComponent(curr)) {
                	builder.append(Util.SPACE.repeat(indent + indentIfInclusive) + "if (canContinue) {\r\n");
                	indentIfInclusive += 1;
                }
            } else {
                if (curr instanceof TaskWrapper tw) {
                	Util.writeTask(tw, builder, bpmnName, usedVariables, indent + indentIfInclusive);
                } else if (curr instanceof Gateway && curr.getOutgoing().size() > 1 || ( 
                		// if di bawah ini perlu untuk definisiin diverging gateway di loop component yang sebenarnya pasti punya >= 2 
                		// tapi sequence flow yang keluar dari komponen itu dimiliki oleh komponen bukan branch maka kalau end gateway nya punya outgoing.size() == 1, sebenarnya
                		// dia punya branching path tapi ke catat oleh komponen 1, dan gateway 1.
                		((curr instanceof Gateway && curr.getOutgoing().size() == 1) &&
                		(((OwnedComponent) curr).getOwnerComponent() instanceof WhileComponent && ((OwnedComponent) curr).getOwnerComponent().getEnd().equals(curr)) ||
                		(((OwnedComponent) curr).getOwnerComponent() instanceof RepeatComponent && ((OwnedComponent) curr).getOwnerComponent().getEnd().equals(curr)) || 
                		((OwnedComponent) curr).getOwnerComponent() instanceof WhileRepeatComponent && ((OwnedComponent) curr).getOwnerComponent().getEnd().equals(curr)))) {
                    
                	boolean first = true;

                    for (SequenceFlow f : curr.getOutgoing()) {
                        FlowNode branchStart = f.getTargetRef();

                        StringBuilder builderTemp = new StringBuilder();

                        String res = getServiceTaskAfterHelper(builderTemp, ownerLoop, branchStart, usedVariables, indent + 1, bpmnName, visited, isProcess);

                        boolean inclusive = GatewayType.INCLUSIVE_GATEWAY.equals(((GatewayWrapper)curr).getGatewayType());
                        if ((first || inclusive) && !res.isEmpty()) {
                        	Set<String> variables = Util.extractVariablesFromExpression(f.getName());
                        	for (String var : variables) {
                        		String varType = Util.inferTypeFromVariable(var, f.getName());
                        		usedVariables.add(new Variable(var, varType)); 
                        	}
                            builder.append(Util.SPACE.repeat(indent + indentIfInclusive) + 
                                String.format("if (%s) {\n", f.getName()))
                            .append(Util.SPACE.repeat(indent + indentIfInclusive + 1) + 
                                    String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", Util.removeWeirdChar(f.getName())));
                            first = false;
                        } else if (!res.isEmpty()) {
                            builder.append(Util.SPACE.repeat(indent) + 
                                String.format("} else if (%s) {\n", f.getName()))
                            .append(Util.SPACE.repeat(indent + indentIfInclusive + 1) + 
                                String.format("processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n", Util.removeWeirdChar(f.getName())));
                        }
                        builder.append(builderTemp.toString().indent(indent + indentIfInclusive));
                    }
                    if (!first){
                        builder.append(Util.SPACE.repeat(indent + indentIfInclusive) + "}\n");
                    }
                        
                    return usedVariables.stream()
                    .map(entry -> {
                    		if (!(entry.getType() == null || "".equals(entry.getType()))) {
                    			return String.format("%s %s = %s;\r\n", entry.getType(), entry.getName(), Util.getDefaultValue(entry.getType()));
                    		}
                    		return "";
                		}
                    )
                    .collect(Collectors.joining("")) + builder.toString();
                    
                // add safeguard for parallel gateway 
                } else if (GatewayType.PARALLEL_GATEWAY.equals(((GatewayWrapper)curr).getGatewayType()) 
                		&& (curr.getOutgoing().size() == 1 || ((OwnedComponent)curr).getOwnerComponent().getEnd().equals(curr))) { 
                    builder.append(buildParallelSafeGuard(curr, indent, isProcess, usedVariables));
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
        	FromStartToUserResult result = c.getFromStartToUser(bpmnName, usedVariables, indent, isProcess);
        	builder.append(result.getResult());
    	}
        
        closeInclusiveIf(builder, indent, indentIfInclusive);
        
        return usedVariables.stream()
        .map(entry -> {
	    		if (!(entry.getType() == null || "".equals(entry.getType()) || entry.getName() == null || "".equals(entry.getName()))) {
	    			return String.format("%s %s = %s;\r\n", entry.getType(), entry.getName(), Util.getDefaultValue(entry.getType()));
	    		}
	    		return "";
			}
        )
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
	
    // Used by acceleo to check whether a task is after start event or not
    // Used by getPrior to filter out tasks in a loop
    public static List<Task> traverseForward(FlowNode e, org.eclipse.bpmn2.Process p, Boolean unwrap) throws Exception {
    	System.out.println("[DEBUG] traverseForward " + e.getName());
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
            	while (curr.getOutgoing().size() == 0 && curr instanceof OwnedComponent oc) {
            		curr = oc.getOwnerComponent();
            	}
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
            		res.add(String.format("hasTaskState(processes, \"%s\")", Util.removeWeirdChar(t.getName())));
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
    // return false if inside inclusive gateway and not continuable
    public static boolean buildStraightLine(
        StringBuilder builder,
        String bpmnName,
        FlowNode el,
        Set<FlowNode> visited,
        Set<Variable> usedVariables, 
        int indent,
        boolean isProcess
    ) {
    	FlowNode curr = el;
    	
    	int indentIfInclusive = 0;

        while (curr != null && visited.add(curr)) {
        	
            // ------------------- First blocking element ends the branch -----------------
            // Found un-continuable component
            if (curr instanceof Component c) {
            	FromStartToUserResult result = c.getFromStartToUser(bpmnName, usedVariables, indent, isProcess);
                builder.append(result.getResult());
                if (!(curr instanceof SequenceComponent) && !result.getCanContinueInclusive() && Util.isInsideFlowComponent(curr)) {
                	builder.append(Util.SPACE.repeat(indent + indentIfInclusive) + "if (canContinue) {\r\n");
                	indentIfInclusive += 1;
                }
                if (c instanceof Continuable con && !con.canContinue()) {
                	closeInclusiveIf(builder, indent, indentIfInclusive);
                	return result.getCanContinueInclusive() && Util.isInsideFlowComponent(curr);
                }
            } 
            // Found un-continuable task
            else if (curr instanceof Continuable con && !con.canContinue()) {
            	if (!Util.isInsideFlowComponent(curr) && !(Util.isInsideSwitchInclusiveComponent(curr))) {
            		builder.append(Util.SPACE.repeat(indent + indentIfInclusive) + "return res;\r\n");
            	} else if (Util.isInsideSwitchInclusiveComponent(curr)) {
            		if (!Util.isInsideFlowComponent(curr)) {
            			builder.append(Util.SPACE.repeat(indent + indentIfInclusive) + "canContinue = false;\r\n");
            		}
            		return false;
            	}
            	return true;
            }
            // ----------------------------------------------------------------------------

            if (curr instanceof TaskWrapper tw && TaskType.isContinuable(tw.getTaskType())) {
            	Util.writeTask(tw, builder, bpmnName, usedVariables, indent + indentIfInclusive);
            }

            // on components, end objects have 0 outgoing elements
            if (curr.getOutgoing().isEmpty()) {
            	closeInclusiveIf(builder, indent, indentIfInclusive);
            	return true;
            }
            
            // handle loops and forks and switches
            if (curr instanceof Gateway) {
            	if (Util.isInsideFlowComponent(curr)) builder.append(Util.SPACE.repeat(indent + indentIfInclusive) + "if (canContinue) {\r\n");
            	return true;
            }

            curr = curr.getOutgoing().get(0).getTargetRef();
        }
        return true;
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
    
    public static String buildParallelSafeGuard(FlowNode curr, int indent, boolean isProcess, Set<Variable> usedVariables) throws Exception {
    	StringBuilder builder = new StringBuilder();
    	usedVariables.add(new Variable("processes", "List<ProcessInstance>"));
        builder.append(Util.SPACE.repeat(indent + 1) + "processes = processService.getAllById(processid);\r\n");
        builder.append(Util.SPACE.repeat(indent + 1) + "if (!(");
        // Generate hasAllTaskStates check for all branches
        String parallelBranches = GenerateQuery.getPrior(curr, null, false);
        
        builder.append(parallelBranches);
        builder.append(")) {\n");
        builder.append(Util.SPACE.repeat(indent + 2) + "response.put(\"status\", \"FAIL\");\r\n");
        builder.append(Util.SPACE.repeat(indent + 2) + "response.put(\"message\", \"Parallel branches not complete yet\");\r\n");
        builder.append(isProcess ? Util.SPACE.repeat(indent + 2) + "return response;\r\n" : "");
        builder.append(Util.SPACE.repeat(indent + 1) + "}\r\n");
        
        return builder.toString();
    }
    
    public static String importAll(String bpmnName, String targetPath) {
    	System.out.println("[START] importAll from " + targetPath);
    	return Util.getAllAccessibleFileAsImport(bpmnName, targetPath);
    }
    
    private static BPMN getOrGenerateBPMN(org.eclipse.bpmn2.FlowElementsContainer process) throws Exception {
    	boolean isProcess = process instanceof org.eclipse.bpmn2.Process;
    	if (isProcess && BPMNParser.getBPMNProcess() != null) {
			return BPMNParser.getBPMNProcess();
		} else if (!isProcess && BPMNParser.getBPMNSubProcess() != null && (process == null || BPMNParser.getBPMNSubProcess().getName().equals(process.getId()))) {
			return BPMNParser.getBPMNSubProcess();
		} else if (isProcess && process != null)  {
			return BPMNParser.parse(process);
		} else if (!isProcess && process != null)  {
			return BPMNParser.parse(process);
		}
		return BPMNParser.getBPMNProcess();
    }
    
    private static void closeInclusiveIf(StringBuilder builder, int indent, int indentationIf) {
    	while(indentationIf > 0) {
    		builder.append(Util.SPACE.repeat(indent + indentationIf - 1) + "}\r\n");
    		indentationIf -= 1;
    	}
    }
}

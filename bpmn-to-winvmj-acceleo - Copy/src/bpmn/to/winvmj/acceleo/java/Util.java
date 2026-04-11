package bpmn.to.winvmj.acceleo.java;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.java.model.BPMN;
import bpmn.to.winvmj.acceleo.java.model.Component;
import bpmn.to.winvmj.acceleo.java.model.FlowComponent;
import bpmn.to.winvmj.acceleo.java.model.Looping;
import bpmn.to.winvmj.acceleo.java.model.OwnedComponent;
import bpmn.to.winvmj.acceleo.java.model.RepeatComponent;
import bpmn.to.winvmj.acceleo.java.model.WhileComponent;
import bpmn.to.winvmj.acceleo.java.model.WhileRepeatComponent;

public class Util {
	public final static String SPACE = "    ";
	
	public static FlowNode findById(String id, Component component) {
	    for (FlowNode element : component.getElements()) {
	        if (element.getId().equals(id)) {
	            return element;
	        }
	        // Recurse into nested components
	        if (element instanceof Component nested) {
	        	FlowNode found = findById(id, nested);
	            if (found != null) return found;
	        }
	    }
	    return null;
	}

	// Overload to search from root BPMN
	public static FlowNode findById(String id, BPMN bpmn) {
	    for (FlowNode element : bpmn.getElements()) {
	        // Check the component itself first
	        if (element.getId().equals(id)) {
	            return element;
	        }
	        // Recurse into nested components
	        if (element instanceof Component nested) {
	        	FlowNode found = findById(id, nested);
	            if (found != null) return found;
	        }
	    }
	    return null;
	}
	
    public static void forwardDFS(
        FlowNode current,
        Set<FlowNode> visited) {

        if (!visited.add(current)) return;

        for (SequenceFlow out : current.getOutgoing()) {
            forwardDFS(out.getTargetRef(), visited);
        }
    }
    
    public static void backwardDFS(
    	FlowNode current,
        Set<FlowNode> visited) {

        if (!visited.add(current)) return;

        for (SequenceFlow in : current.getIncoming()) {
            backwardDFS(in.getSourceRef(), visited);
        }
    }
    
    // used to check whether gateway is the start of loop or not
    public static boolean isStartOfLoopComponent(FlowNode e) {
        if (e instanceof OwnedComponent oc) {
            Component parent = oc.getOwnerComponent();
            return (parent instanceof WhileComponent ||
                    parent instanceof RepeatComponent ||
                    parent instanceof WhileRepeatComponent) &&
                    parent.getStart().equals(e);
        }
        return false;
    }
    
    public static boolean isInsideFlowComponent(FlowNode e) {
        FlowNode curr = e;
        while (curr instanceof OwnedComponent oc) {
            Component parent = oc.getOwnerComponent();
            if (parent instanceof FlowComponent) return true;
            curr = parent;  // climb up and keep checking
        }
        return false;
    }
    
    public static Looping getOwnerLoop(OwnedComponent curr) {
		while(curr != null) {
			if (curr instanceof Looping) return (Looping) curr;
			curr = curr.getOwnerComponent();
		}
    	return null;
    }
    

    
    public static String inferTypeFromVariable(String variable, String expression) {
        // Check how the variable is used in the expression
        
        // If it's compared with == true/false or used with !, it's likely boolean
        if (expression.matches(".*!?" + Pattern.quote(variable) + "\\s*[?!].*") ||
            expression.matches(".*" + Pattern.quote(variable) + "\\s*(==|!=)\\s*(true|false).*")) {
            return "boolean";
        }
        
        // If it's used with comparison operators <, >, <=, >=, it's likely numeric
        if (expression.matches(".*" + Pattern.quote(variable) + "\\s*[<>=]+.*")) {
            return "int";
        }
        
        // If it's used with .equalsIgnoreCase or string methods, it's a String
        if (expression.matches(".*" + Pattern.quote(variable) + "\\..*")) {
            return "String";
        }
        
        // Default to boolean (most common for BPMN conditions)
        return "boolean";
    }
    
    public static Set<String> extractVariablesFromExpression(String expression) {
        Set<String> variables = new HashSet<>();
        
        if (expression == null || expression.isEmpty()) {
            return variables;
        }
        
        // Remove whitespace
        String cleaned = expression.replaceAll("\\s+", "");
        
        // Split on operators and keep only valid identifiers
        // Pattern matches: word characters (a-zA-Z0-9_) that form valid Java identifiers
        Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(cleaned);
        
        while (matcher.find()) {
            String token = matcher.group();
            // Exclude Java keywords
            if (!isJavaKeyword(token)) {
                variables.add(token);
            }
        }
        
        return variables;
    }

    private static boolean isJavaKeyword(String token) {
        Set<String> keywords = Set.of(
            "true", "false", "null",
            "if", "else", "while", "for", "return",
            "class", "interface", "enum", "abstract"
        );
        return keywords.contains(token);
    }
}

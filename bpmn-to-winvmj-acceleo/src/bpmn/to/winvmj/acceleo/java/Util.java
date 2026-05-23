package bpmn.to.winvmj.acceleo.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import bpmn.to.winvmj.acceleo.java.model.BPMN;
import bpmn.to.winvmj.acceleo.java.model.Component;
import bpmn.to.winvmj.acceleo.java.model.FlowComponent;
import bpmn.to.winvmj.acceleo.java.model.RepeatComponent;
import bpmn.to.winvmj.acceleo.java.model.SwitchComponent;
import bpmn.to.winvmj.acceleo.java.model.Variable;
import bpmn.to.winvmj.acceleo.java.model.WhileComponent;
import bpmn.to.winvmj.acceleo.java.model.WhileRepeatComponent;
import bpmn.to.winvmj.acceleo.java.model.modelutil.GatewayType;
import bpmn.to.winvmj.acceleo.java.model.modelutil.GatewayWrapper;
import bpmn.to.winvmj.acceleo.java.model.modelutil.Looping;
import bpmn.to.winvmj.acceleo.java.model.modelutil.OwnedComponent;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskType;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskWrapper;

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
        if (e instanceof FlowComponent) return true;
        while (curr instanceof OwnedComponent oc) {
            Component parent = oc.getOwnerComponent();
            if (parent instanceof FlowComponent) return true;
            curr = parent;  // climb up and keep checking
        }
        return false;
    }
    
    public static boolean isInsideSwitchInclusiveComponent(FlowNode e) {
        FlowNode curr = e;
        while (curr instanceof OwnedComponent oc) {
            Component parent = oc.getOwnerComponent();
            if ((parent instanceof SwitchComponent) && 
            		((parent.getStart() instanceof GatewayWrapper) && 
            				GatewayType.INCLUSIVE_GATEWAY.equals(
            						((GatewayWrapper)parent.getStart()).getGatewayType())))
            	return true;
            curr = parent;  // climb up and keep checking
        }
        return false;
    }
    
    public static boolean isHighestSwitchInclusiveComponent(FlowNode e) {
        FlowNode curr = e;
        boolean found = false;
        boolean foundSecondHigher = false;
        while (curr instanceof OwnedComponent oc) {
            Component parent = oc.getOwnerComponent();
            if ((parent instanceof SwitchComponent) && 
            		((parent.getStart() instanceof GatewayWrapper) && 
            				GatewayType.INCLUSIVE_GATEWAY.equals(
            						((GatewayWrapper)parent.getStart()).getGatewayType()))) {
            	if (found) {
            		foundSecondHigher = true;
            	}
            	found = true;
            }
            curr = parent;  // climb up and keep checking
        }
        if (!found) return false;
        return !foundSecondHigher;
    }
    
    public static Looping getOwnerLoop(OwnedComponent curr) {
		while(curr != null) {
			if (curr instanceof Looping) return (Looping) curr;
			curr = curr.getOwnerComponent();
		}
    	return null;
    }
    
    public static void writeTask(
    		TaskWrapper task, 
    		StringBuilder builder, 
    		String bpmnName, 
    		Set<Variable> usedVariables, 
    		int indent) {
    	if (TaskType.SCRIPT_TASK.equals(task.getTaskType())) {
    		ScriptTaskConverter.appendStatementFromScriptTask(builder, task, bpmnName, usedVariables, indent);
    	} else {
    		builder.append(
    				String.format(Util.SPACE.repeat(indent) + 
                        "%sService.%s(requestBody, processid, response);\n", 
                        bpmnName.toLowerCase(), task.getName()));
    	}
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

        // Remove string literals first: "balance", "anything"
        String cleaned = expression.replaceAll("\"[^\"]*\"", "");

        // Remove cast expressions like (int), (String) when followed by identifier
        cleaned = cleaned.replaceAll("\\([a-zA-Z_][a-zA-Z0-9_]*\\)(?=[\\s a-zA-Z0-9_\"'\\(])", "");

        // Remove whitespace
        cleaned = cleaned.replaceAll("\\s+", "");

        Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(cleaned);

        while (matcher.find()) {
            String token = matcher.group();
            int end = matcher.end();
            int start = matcher.start();

            if (end < cleaned.length() && cleaned.charAt(end) == '(') continue;   // method call
            if (start > 0 && cleaned.charAt(start - 1) == '.') continue;          // object member

            if (!isJavaKeyword(token)) {
                variables.add(token);
            }
        }

        return variables;
    }
    
    public static String getDefaultValue(String type) {
        if (type == null) return "null";

        // Strip generic (e.g. List<Long> -> List)
        String baseType = type.contains("<") ? type.substring(0, type.indexOf("<")) : type;
        // Strip xs: prefix if present
        baseType = baseType.contains(":") ? baseType.split(":")[1] : baseType;

        switch (baseType.toLowerCase().trim()) {
            // Boolean
            case "boolean":             return "false";

            // Integer types
            case "int":
            case "integer":             return "-1";

            case "short":               return "(short) -1";
            case "byte":                return "(byte) -1";

            // Long
            case "long":                return "-1L";

            // Floating point
            case "float":               return "-1.0f";
            case "double":              return "-1.0";
            case "decimal":             return "BigDecimal.ZERO";

            // String / text
            case "string":
            case "normalizedstring":
            case "token":               return "\"\"";

            // Date/Time
            case "date":
            case "datetime":
            case "time":                return "new Date()";

            // Binary
            case "base64binary":
            case "hexbinary":           return "new byte[0]";

            // Collections (List<X>)
            case "list":                return "new ArrayList<>()";

            // Fallback
            default:                    return "null";
        }
    }

    private static boolean isJavaKeyword(String token) {
        Set<String> keywords = Set.of(
            "true", "false", "null",
            "if", "else", "while", "for", "return",
            "class", "interface", "enum", "abstract"
        );
        return keywords.contains(token);
    }
    
    public static String getAllAccessibleFileAsImport(String bpmnName, String targetPath) {
        Path srcPath = Paths.get(targetPath);
        if (!Files.exists(srcPath) || !Files.isDirectory(srcPath)) {
        	System.out.println("Not found");
            return "";
        }
        try (Stream<Path> walk = Files.walk(srcPath)) {
            return walk
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !p.getFileName().toString().equals("package-info.java")
                          && !p.getFileName().toString().equals("module-info.java"))
                .filter(p -> !p.getFileName().toString().equals(bpmnName + "ResourceImpl.java"))
                .filter(p -> !p.toString().contains(".product."))
                .filter(p -> {
                    String parentFolder = p.getParent().getFileName().toString();
                    return parentFolder.equals("model") || parentFolder.equals("resource");
                })
                .map(p -> {
                    Path relative = srcPath.relativize(p);
                    System.out.println(p.getFileName());
                    // Skip the first folder, take the rest as the import path
                    Path withoutFirstFolder = relative.subpath(1, relative.getNameCount());
                    String importPath = withoutFirstFolder.toString()
                        .replace(File.separatorChar, '.')
                        .replace(".java", "");
                    return "import " + importPath + ";";
                })
                .sorted()
                .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static String removeWeirdChar(String s) {
    	if (s == null) return null;
    	return s.replace("\"", "").replace("\\", "");
    }
}

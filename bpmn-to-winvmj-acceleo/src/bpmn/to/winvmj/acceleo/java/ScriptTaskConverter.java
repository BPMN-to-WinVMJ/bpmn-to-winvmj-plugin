package bpmn.to.winvmj.acceleo.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SubProcess;

import bpmn.to.winvmj.acceleo.GenerateQuery;
import bpmn.to.winvmj.acceleo.java.model.Variable;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskType;
import bpmn.to.winvmj.acceleo.java.model.modelutil.TaskWrapper;

public class ScriptTaskConverter {
	
	public static void appendStatementFromScriptTask(
			StringBuilder sb, 
			TaskWrapper scriptTask, 
			String bpmnName, 
			Set<Variable> usedVariable,
			int indent) {
	    if (!TaskType.SCRIPT_TASK.equals(scriptTask.getTaskType()) || scriptTask == null || scriptTask.getName() == null) return;

	    String name = scriptTask.getName().trim();
	    String lower = name.toLowerCase();
	    
	    String stmt;
	    ScriptTask task = (ScriptTask) scriptTask.getDelegate();
	    if (task.getScriptFormat() != null && !task.getScriptFormat().isBlank()) {
	        try {
	            stmt = Files.readString(Path.of(task.getScriptFormat())).indent(indent);
	        } catch (IOException e) {
	        	System.err.println("File not found : " + task.getScriptFormat());
	        	stmt = task.getScript().indent(indent);
	        }
	    } else if (task.getScriptFormat() != null && !task.getScript().isBlank()) {
	    	stmt = task.getScript().indent(indent);
	    } else {
	    	stmt = resolveStatement(lower, name, usedVariable);
	    }
	    
	    sb.append(Util.SPACE.repeat(indent) + "// From ScriptTask ").append(name).append("\n");
	    sb.append(Util.SPACE.repeat(indent) + stmt).append("\n\n");
	    
    	try {
    		StringBuilder builder = new StringBuilder();
    		for (SubProcess sp : scriptTask.getSubProcesses()) {
    			builder.append(GenerateQuery.getServiceTaskAfter(scriptTask, sp, bpmnName));
    		}
    		sb.append(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	}
	
    public static String resolveStatement(String s, String real, Set<Variable> usedVariable) {
    	
    	Matcher m;
    	s = s.trim();
    	
        // -------------------------------------------------------------------------
        // DECLARATION / INITIALIZATION
        // -------------------------------------------------------------------------

    	// "create int a = 1" | "declare String name = hello" | "init char x = x | init char x as x"
    	m = match(s, "(assign|create|init|initiate|initialize|declare|define)\\s+([\\w<>,\\s]+?)\\s+(\\w+)\\s*(?:=|as)\\s*(.+)");
    	if (m.find()) {
    	    Matcher mOriginal = Pattern.compile(
    	    		"(assign|create|init|initiate|initialize|declare|define)\\s+([\\w<>,\\s]+?)\\s+(\\w+)\\s*(?:=|as)\\s*(.+)",
    	            Pattern.CASE_INSENSITIVE
	        ).matcher(real);
	        mOriginal.find();
    	    String type = mOriginal.group(2);
    	    String name = mOriginal.group(3);
    	    String value = mOriginal.group(4).trim();
    	    usedVariable.add(new Variable(name, type));
    	    return name + " = " + formatValue(type, value) + ";";
    	}

    	// "create a = 1" | "declare name = hello" (infer type from value)
    	m = match(s, "(assign|create|init|initiate|initialize|declare|define)\\s+(\\w+)\\s*=\\s*(.+)");
    	if (m.find()) {
    	    String name = m.group(2);
    	    String value = m.group(3).trim();
    	    String type = inferType(value);
    	    usedVariable.add(new Variable(name, type));
    	    return name + " = " + formatValue(type, value) + ";";
    	}
    	
        // "create order as Order" | "create user as UserEntity"
        m = match(s, "(create|init|initiate|initialize|declare|define)\\s+(\\w+)\\s+as\\s+(\\w+)");
        if (m.find()) {
    	    Matcher mOriginal = Pattern.compile(
    	            "(create|init|initiate|initialize|declare|define)\\\\s+(\\\\w+)\\\\s+as\\\\s+(\\\\w+)",
    	            Pattern.CASE_INSENSITIVE
	        ).matcher(real);
	        mOriginal.find();
    	    String name = mOriginal.group(2);
    	    String type = mOriginal.group(3);
        	usedVariable.add(new Variable(name, type));
        	return m.group(2) + " = new " + m.group(3) + "();";
        }

        // "create order" | "create user"
        m = match(s, "(create|init|initiate|initialize|declare|define)\\s+(\\w+)");
        if (m.find()) {
    	    String name = m.group(2);
        	usedVariable.add(new Variable(name, "Object"));
        	return m.group(1) + " = new Object();";
        }
        
        m = match(real, "([A-Z]\\w*(?:<[\\w<>, ]+>)?)\\s+(\\w+)\\s*=\\s*(.+?)\\s*;?$");
        if (m.find()) {
            String type  = m.group(1); // "List<Account>"
            String name  = m.group(2); // "accounts"
            String value = m.group(3); // "accountservice.getAllAccount()"
            usedVariable.add(new Variable(name, type));
            return name + " = " + value + ";";
        }
        
        // -------------------------------------------------------------------------
        // LOGGING / PRINTING
        // -------------------------------------------------------------------------
        m = match(s, "(log|print)\\s+(.+)");
        if (m.find()) return "System.out.println(" + m.group(1) + ");";
        
        // ARITHMETIC
        // "add a and b" / "multiply a by b" / "divide a by b" / "subtract a from b"
        // -------------------------------------------------------------------------
        m = match(s, "(add|increment)\\s+(\\w+)\\s+(?:(?:and|to)\\s+)?(\\w+)");
        if (m.find()) {
            if (s.contains(" to ")) {
                // "add a to b" → b += a
                return m.group(3) + " += " + m.group(2) + ";";
            } else if (s.contains(" and ")) {
                // "add a and b" → int result = a + b
                return "int result = " + m.group(2) + " + " + m.group(3) + ";";
            } else {
                // "add a b" → default to += (same as "to" behaviour)
                return m.group(2) + " += " + m.group(3) + ";";
            }
        }
        
        m = match(s, "(subtract|minus)\\s+(\\w+)\\s+from\\s+(\\w+)");
        if (m.find()) return m.group(2) + " -= " + m.group(1) + ";";

        m = match(s, "multiply\\s+(\\w+)\\s+(by|with)\\s+(\\w+)");
        if (m.find()) return m.group(1) + " *= " + m.group(2) + ";";

        m = match(s, "divide\\s+(\\w+)\\s+(by|with)\\s+(\\w+)");
        if (m.find()) return m.group(1) + " /= " + m.group(2) + ";";

        m = match(s, "mod(?:ulo)?\\s+(\\w+)\\s+(with|by)\\s+(\\w+)");
        if (m.find()) return m.group(1) + " %= " + m.group(2) + ";";
        
        // -------------------------------------------------------------------------
        // RESPONSE / MAP OPERATIONS
        // "add a to response" / "put a in map" / "remove a from map"
        // -------------------------------------------------------------------------
        m = match(s, "add\\s+(.+?)\\s+to\\s+(?:response|res)");
        if (m.find()) return "response.put(\"" + m.group(1) + "\", " + m.group(1) + ");";

        m = match(s, "put\\s+(\\w+)\\s+in(?:to)?\\s+(\\w+)");
        if (m.find()) return m.group(2) + ".put(\"" + m.group(1) + "\", " + m.group(1) + ");";

        m = match(s, "get\\s+(\\w+)\\s+from\\s+(\\w+)");
        if (m.find()) return "Object " + m.group(1) + " = " + m.group(2) + ".get(\"" + m.group(1) + "\");";

        m = match(s, "remove\\s+(\\w+)\\s+from\\s+(\\w+)");
        if (m.find()) return m.group(2) + ".remove(\"" + m.group(1) + "\");";
        
        m = match(s, "([a-zA-Z_][\\w.]*?)\\s*\\.?\\s*([a-zA-Z_]\\w*)\\s*\\(([^)]*)\\)");
        if (m.find()) return s + ";";
        
        return "// TODO: implement '" + s + "'";
    }
    
    private static Matcher match(String input, String pattern) {
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(input);
    }
    
    private static String inferType(String value) {
        if (value.matches("-?\\d+"))              return "Integer";
        if (value.matches("-?\\d+\\.\\d+"))       return "Double";
        if (value.equalsIgnoreCase("true") || 
            value.equalsIgnoreCase("false"))       return "Boolean";
        if (value.length() == 1)                  return "Char";
        return "String";
    }
    
    private static String formatValue(String type, String value) {
        switch (type) {
            case "String": return "\"" + value + "\"";
            case "char":   return "'" + value.charAt(0) + "'";
            default:       return value; // int, double, float, long, boolean
        }
    }
}
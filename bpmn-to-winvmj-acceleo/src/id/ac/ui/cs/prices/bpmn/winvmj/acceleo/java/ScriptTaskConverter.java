package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SubProcess;

import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.GenerateQuery;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.Variable;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.TaskType;
import id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil.TaskWrapper;

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
	        	if (task.getScript() != null && !task.getScript().isBlank()) {
	        		stmt = task.getScript().indent(indent);
	        	}
	        	stmt = "";
	        	System.out.println("File not found : " + task.getScriptFormat());
	        }
	    } else if (task.getScriptFormat() != null && task.getScript() != null && !task.getScript().isBlank()) {
	    	stmt = task.getScript().indent(indent);
	    } else {
	    	stmt = resolveStatement(lower, name, usedVariable);
	    }
	    
	    sb.append(Util.SPACE.repeat(indent) + "// From ScriptTask ").append(name).append("\n");
	    sb.append(Util.SPACE.repeat(indent) + "processService.upsert(new ProcessInstance(processid, \"%s\"));\r\n".formatted(Util.removeWeirdChar(name)));
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
    	
    	// DECLARATION WITH ARTIHMETICS
    	// init TYPE NAME as add A and B  →  int result = a + b;
    	m = match(real, "init\\s+(\\w+)\\s+(\\w+)\\s+as\\s+(?:add|increment)\\s+(\\w+)\\s+and\\s+(\\w+)");
    	if (m.find()) return m.group(1) + " " + m.group(2) + " = " + m.group(3) + " + " + m.group(4) + ";";

    	// init TYPE NAME as add A to B  →  int result = b + a;
    	m = match(real, "init\\s+(\\w+)\\s+(\\w+)\\s+as\\s+(?:add|increment)\\s+(\\w+)\\s+to\\s+(\\w+)");
    	if (m.find()) return m.group(1) + " " + m.group(2) + " = " + m.group(4) + " + " + m.group(3) + ";";

    	// init TYPE NAME as subtract/minus A from B  →  int result = b - a;
    	m = match(real, "init\\s+(\\w+)\\s+(\\w+)\\s+as\\s+(?:subtract|minus)\\s+(\\w+)\\s+(?:with|from)\\s+(\\w+)");
    	if (m.find()) return m.group(1) + " " + m.group(2) + " = " + m.group(4) + " - " + m.group(3) + ";";

    	// init TYPE NAME as mult A with B  →  int result = a * b;
    	m = match(real, "init\\s+(\\w+)\\s+(\\w+)\\s+as\\s+mult(?:iply)?\\s+(\\w+)\\s+(?:by|with)\\s+([\\w.]+)");
    	if (m.find()) return m.group(1) + " " + m.group(2) + " = " + m.group(3) + " * " + m.group(4) + ";";

    	// init TYPE NAME as div A by B  →  int result = a / b;
    	m = match(real, "init\\s+(\\w+)\\s+(\\w+)\\s+as\\s+div(?:ide)?\\s+(\\w+)\\s+(?:by|with)\\s+([\\w.]+)");
    	if (m.find()) return m.group(1) + " " + m.group(2) + " = " + m.group(3) + " / " + m.group(4) + ";";

    	// init TYPE NAME as mod A by B  →  int result = a % b;
    	m = match(real, "init\\s+(\\w+)\\s+(\\w+)\\s+as\\s+mod(?:ulo)?\\s+(\\w+)\\s+(?:with|by)\\s+([\\w.]+)");
    	if (m.find()) return m.group(1) + " " + m.group(2) + " = " + m.group(3) + " % " + m.group(4) + ";";
    	
    	// DECLARATION WITHOUT ARITHMETICS

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
    	    usedVariable.add(new Variable(name, ""));
    	    return type + " " + name + " = " + value + ";";
    	}

    	// "create a = 1" | "declare name = hello" (infer type from value)
    	m = match(s, "(assign|create|init|initiate|initialize|declare|define)\\s+(\\w+)\\s*=\\s*(.+)");
    	if (m.find()) {
    	    String name = m.group(2);
    	    String value = m.group(3).trim();
    	    String type = inferType(value);
    	    usedVariable.add(new Variable(name, ""));
    	    return type + " " + name + " = " + value + ";";
    	}
    	
        // "create order as Order" | "create user as UserEntity"
        m = match(s, "(create|init|initiate|initialize|declare|define)\\s+(\\w+)\\s+as\\s+(\\w+)");
        if (m.find()) {
    	    Matcher mOriginal = Pattern.compile(
    	            "(create|init|initiate|initialize|declare|define)\\\\s+(\\\\w+)\\\\s+as\\\\s+(\\\\w+)",
    	            Pattern.CASE_INSENSITIVE
	        ).matcher(real);
	        mOriginal.find();
	        if (mOriginal.matches()) {
	    	    String name = mOriginal.group(2);
	    	    String type = mOriginal.group(3);
	        	usedVariable.add(new Variable(name, ""));
	        	return type + " " + m.group(2) + " = new " + m.group(3) + "();";
	        }
        }

        // "create order" | "create user"
        m = match(s, "(create|init|initiate|initialize|declare|define)\\s+(\\w+)");
        if (m.find()) {
    	    String name = m.group(2);
        	usedVariable.add(new Variable(name, "Object"));
        	return "Object " + m.group(1) + " = new Object();";
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
        m = match(real, "(log|print)\\s+(.+)");
        if (m.find()) return "System.out.println(" + m.group(2) + ");";
        
        // ARITHMETIC
        // "add a and b" / "multiply a by b" / "divide a by b" / "subtract a from b"
        // -------------------------------------------------------------------------
        m = match(real, "(add|increment)\\s+(\\w+)\\s+(?:(?:and|to)\\s+)?(\\w+)");
        if (m.find()) {
            if (real.contains(" to ")) {
                // "add a to b" → b += a
                return m.group(3) + " += " + m.group(2) + ";";
            } else if (real.contains(" and ")) {
                // "add a and b" → int result = a + b
                return "int result = " + m.group(2) + " + " + m.group(3) + ";";
            } else {
                // "add a b" → default to += (same as "to" behaviour)
                return m.group(2) + " += " + m.group(3) + ";";
            }
        }
        
        m = match(s, "(subtract|minus)\\s+(\\w+)\\s+(?:with|from)\\s+(\\w+)");
        if (m.find()) return m.group(3) + " -= " + m.group(2) + ";";

        m = match(s, "mult(?:iply)?\\s+(\\w+)\\s+(?:by|with)\\s+([\\w.]+)");
        if (m.find()) return m.group(1) + " *= " + m.group(2) + ";";

        m = match(s, "div(?:ide)?\\s+(\\w+)\\s+(?:by|with)\\s+([\\w.]+)");
        if (m.find()) return m.group(1) + " /= " + m.group(2) + ";";

        m = match(s, "mod(?:ulo)?\\s+(\\w+)\\s+(?:with|by)\\s+([\\w.]+)");
        if (m.find()) return m.group(1) + " %= " + m.group(2) + ";";
        
        // -------------------------------------------------------------------------
        // RESPONSE / MAP OPERATIONS
        // "add a to response" / "put a in map" / "remove a from map"
        // -------------------------------------------------------------------------
        
        
        // "put amount (as) converted to res"
        m = match(real, "(?:put|add)\\s+(\\w+)\\s+(?:as\\s+)?([\\w.]+(?:\\([^)]*\\))?)\\s+to\\s+(?:response|res)");
        if (m.find()) return "response.put(\"" + m.group(1) + "\", " + m.group(2) + ");";
        
        // "put amount (as) converted to whatever"
        m = match(real, "(?:put|add)\\s+(\\w+)\\s+(?:as\\s+)?([\\w.]+(?:\\([^)]*\\))?)\\s+to\\s+(\\w+)");
        if (m.find()) return m.group(3) + ".put(\"" + m.group(1) + "\", " + m.group(2) + ");";
        
        // "add message "uang kurang" to res"  →  response.put("message", "uang kurang");
        m = match(real, "(?:put|add)\\s+(\\w+)\\s+\"([^\"]+)\"\\s+to\\s+(?:response|res)");
        if (m.find()) return "response.put(\"" + m.group(1) + "\", \"" + m.group(2) + "\");";
        
        m = match(real, "(?:put|add)\\s+(.+?)\\s+to\\s+(?:response|res)");
        if (m.find()) return "response.put(\"" + m.group(1) + "\", " + m.group(1) + ");";
        
        // 1. quoted value — "add message "uang kurang" to res"
        m = match(real, "(?:put|add)\\s+(\\w+)\\s+\"([^\"]+)\"\\s+to\\s+(\\w+)");
        if (m.find()) return m.group(3) + ".put(\"" + m.group(1) + "\", \"" + m.group(2) + "\");";

        // 2. variable value — "put balance to account" / "add amount to response"
        m = match(real, "(?:put|add)\\s+(\\w+)\\s+to\\s+(\\w+)");
        if (m.find()) return m.group(2) + ".put(\"" + m.group(1) + "\", " + m.group(1) + ");";
        
        // "put balance as String.valueOf(balance) to requestBody"
        m = match(real, "(?:put|add)\\s+(\\w+)\\s+as\\s+([\\w.]+\\([^)]*\\))\\s+to\\s+(\\w+)");
        if (m.find()) return m.group(3) + ".put(\"" + m.group(1) + "\", " + m.group(2) + ");";
        
        Pattern pattern = Pattern.compile("put (\\w+) as (.+?)to (\\w+)");
        m = pattern.matcher(real);
        if (m.find()) {
            String expression = m.group(2); // ((UUID)account.get("id_account")).toString()
            String key        = m.group(1); // id_account
            String map        = m.group(3); // account
            
            return String.format("%s.put(\"%s\", %s);", map, key, expression);
        }
        
        // "get balance from account" / "get balance from account as int"
        m = match(s, "get\\s+(\\w+)\\s+from\\s+(\\w+)(?:\\s+as\\s+(\\w+))?");
        if (m.find()) {
            String type = m.group(3) != null ? m.group(3) : "Object";
            // Capitalise for cast: int→Integer, etc. only if raw cast preferred, else keep as-is
            return type + " " + m.group(1) + " = (" + type + ") " + m.group(2) + ".get(\"" + m.group(1) + "\");";
        }

        m = match(s, "remove\\s+(\\w+)\\s+from\\s+(\\w+)");
        if (m.find()) return m.group(2) + ".remove(\"" + m.group(1) + "\");";
        
        m = match(s, "([a-zA-Z_][\\w.]*?)\\s*\\.?\\s*([a-zA-Z_]\\w*)\\s*\\(([^)]*)\\)");
        if (m.find()) return real + ";";
        
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
}
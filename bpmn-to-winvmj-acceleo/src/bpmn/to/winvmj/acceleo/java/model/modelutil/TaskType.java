package bpmn.to.winvmj.acceleo.java.model.modelutil;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.UserTask;

public enum TaskType {
    USER_TASK("userTask"),
    SERVICE_TASK("serviceTask"),
    SCRIPT_TASK("scriptTask"),
    SEND_TASK("sendTask"),
    RECEIVE_TASK("receiveTask"),
    MANUAL_TASK("manualTask"),
    UNKNOWN("unknown");
	

	private static List<TaskType> continuable = List.of(SERVICE_TASK, SCRIPT_TASK, MANUAL_TASK);
    
    private final String xmlName;

    TaskType(String xmlName) {
        this.xmlName = xmlName;
    }

    public String getXmlName() {
        return xmlName;
    }
    
    public static TaskType getTaskType(BaseElement element) {
        if (element == null) {
            return TaskType.UNKNOWN;
        }

        // Check task types
        if (element instanceof UserTask) {
            return TaskType.USER_TASK;
        }
        if (element instanceof ServiceTask) {
            return TaskType.SERVICE_TASK;
        }
        if (element instanceof ScriptTask) {
            return TaskType.SCRIPT_TASK;
        }
        if (element instanceof ManualTask) {
            return TaskType.MANUAL_TASK;
        }
        return TaskType.UNKNOWN;
    }
    
    public static boolean isContinuable(TaskType type) {
    	return continuable.contains(type);
    }
}

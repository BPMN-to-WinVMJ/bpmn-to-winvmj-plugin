package id.ac.ui.cs.prices.bpmn.winvmj.acceleo.java.model.modelutil;

import java.util.List;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SubProcess;

public interface OwnSubProcess extends FlowNode {
    public void setSubProcess(List<SubProcess> subProcesses);
    
    public void addSubProcess(SubProcess subProcess);

    public List<SubProcess> getSubProcesses();
}

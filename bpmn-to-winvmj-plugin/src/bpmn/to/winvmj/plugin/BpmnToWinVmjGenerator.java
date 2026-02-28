package bpmn.to.winvmj.plugin;

import org.eclipse.emf.common.util.URI;

import bpmn.to.winvmj.acceleo.Generate;

import org.eclipse.emf.common.util.BasicMonitor;
import java.io.File;
import java.util.ArrayList;

public class BpmnToWinVmjGenerator {
    
    public static boolean transformBpmnFile(String bpmnFilePath, File outputFolder) {
        try {
            URI modelURI = URI.createFileURI(
                new File(bpmnFilePath).getAbsolutePath()
            );
            
            Generate generator = new Generate(modelURI, outputFolder, new ArrayList<>());
            generator.doGenerate(new BasicMonitor());
            
            System.out.println("BPMN file transformation completed: " + bpmnFilePath);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error transforming BPMN file: " + bpmnFilePath);
            e.printStackTrace();
            return false;
        }
    }
}
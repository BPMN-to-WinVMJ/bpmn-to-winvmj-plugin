package bpmn.to.winvmj.plugin;

import org.eclipse.emf.common.util.URI;
import bpmn.to.winvmj.acceleo.Generate;

import org.eclipse.acceleo.common.AcceleoServicesRegistry;
import org.eclipse.emf.common.util.BasicMonitor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BpmnToWinVmjGenerator {
    
    public static boolean transformBpmnFile(String bpmnFilePath, File outputFolder) {

        try {
            
            URI modelURI = URI.createFileURI(
                new File(bpmnFilePath).getAbsolutePath()
            );
            
            File file = new File(bpmnFilePath);
            String fileName = file.getName();
            String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            
            List<String> argument = new ArrayList<>();
            argument.add(nameWithoutExtension);
            
            AcceleoServicesRegistry.INSTANCE.addServiceClass("bpmn.to.winvmj.acceleo",
            		"bpmn.to.winvmj.acceleo.GenerateQuery");
            Generate generator = new Generate(modelURI, outputFolder, argument);
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
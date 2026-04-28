package bpmn.to.winvmj.plugin;

import org.eclipse.emf.common.util.URI;

import bpmn.to.winvmj.acceleo.Generate;

import org.eclipse.acceleo.common.AcceleoServicesRegistry;
import org.eclipse.emf.common.util.BasicMonitor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BpmnToWinVmjGenerator {
    
	/*
	 * bpmnFilePath is the location of the bpmn2 file
	 * outputFolder is the location where the generate resource file will be put in
	 * importPath is the package path on the bpmn2 generated files
	 * targetPath is the src folder from uml-dop
	 */
    public static boolean transformBpmnFile(String bpmnFilePath, File outputFolder, String importPath, String targetPath) {

        try {
            
            URI modelURI = URI.createFileURI(
                new File(bpmnFilePath).getAbsolutePath()
            );
            
            File file = new File(bpmnFilePath);
            String fileName = file.getName();
            String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            
            List<String> argument = new ArrayList<>();
            
            // Add fileName to acceleo generator
            argument.add(nameWithoutExtension);
            
            // Add output prefix location as argument
            if (importPath.isEmpty()) {
            	// Add outputPath to acceleo generator
            	argument.add("bpmn/product/" + fileName.substring(0, 1).toUpperCase() + fileName.substring(1).replaceAll(" ", "") + '/');
            	argument.add("");
            } else {
            	// Add outputPath to acceleo generator
            	argument.add("");
            	// Add importPath to acceleo generator
            	argument.add(importPath);
            }
            argument.add(targetPath);
            
            // Add 
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
    
    /*
     * umlDopProductPath is the location of generated .product. folder before compiled to .jar in src/
     */
    public static boolean editProductRouter(File umlDopProductPath, File productJavaFile, String bpmnFilePath, String importPath) throws IOException {
        File file = new File(bpmnFilePath);
        String fileName = file.getName();
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        String nameCapital = nameWithoutExtension.substring(0, 1).toUpperCase() + nameWithoutExtension.substring(1).replaceAll(" ", "");
        
    	Path filePath = Path.of(productJavaFile.getAbsolutePath());
    	List<String> lines = new ArrayList<>(Files.readAllLines(filePath));

    	// Find the line to insert after
    	int insertImportIndex = -1;
    	for (int i = 0; i < lines.size(); i++) {
    	    if (lines.get(i).contains("import vmj.routing.route.Router;")) {
    	    	insertImportIndex = i++; // insert AFTER this line
    	        break;
    	    }
    	}
    	
    	int insertImportRouterAdd = -1;
    	for (int i = 0; i < lines.size(); i++) {
    	    if (lines.get(i).contains("Router.route(userResource);")) {
    	    	insertImportRouterAdd = i++; // insert AFTER this line
    	        break;
    	    }
    	}

    	if (insertImportIndex != -1) {
    	    lines.add(insertImportIndex++, "import %s.%sResourceFactory;".formatted(importPath, nameCapital));
    	    lines.add(insertImportIndex, "import %s.core.%sResource;".formatted(importPath, nameCapital));
    	    
    	    lines.add(insertImportRouterAdd++, 
    	    		"\t\t%sResource %sresource = %sResourceFactory.createResource(\"%s.core.%sResourceImpl\");"
    	    		.formatted(nameCapital, nameWithoutExtension, nameCapital, importPath, nameCapital)
    		);
    	    lines.add(insertImportRouterAdd++, 
    	    		"\t\tRouter.route(%sresource);"
    	    		.formatted(nameWithoutExtension)
    		);
    	    lines.add(insertImportRouterAdd++, 
    	    		"\t\tSystem.out.println(\"%sResource endpoints binding\");"
    	    		.formatted(nameCapital)
    		);
    	    Files.write(filePath, lines);   
    	    return true;
    	}
    	return false;
    }
}
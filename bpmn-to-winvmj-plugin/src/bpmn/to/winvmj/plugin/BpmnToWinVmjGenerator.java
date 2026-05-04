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
import java.util.Map;

public class BpmnToWinVmjGenerator {
    
	/*
	 * bpmnFilePath is the location of the bpmn2 file
	 * outputFolder is the location where the generate resource file will be put in
	 * importPath is the package path on the bpmn2 generated files
	 * targetPath is the src folder from uml-dop
	 */
    public static boolean transformBpmnFile(String bpmnFilePath, File outputFolder, String importPath, String targetPath, Map<String, String> services) {

        try {
            
            URI modelURI = URI.createFileURI(
                new File(bpmnFilePath).getAbsolutePath()
            );
            
            File file = new File(bpmnFilePath);
            String fileName = file.getName();
            String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            
            List<Object> argument = new ArrayList<>();
            
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
            argument.add(services.keySet().stream().toList());
            argument.add(services.values().stream().toList());
            
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
    public static boolean editProductRouter(
    		File umlDopProductPath, 
    		File productJavaFile, 
    		String bpmnFilePath, 
    		String importPath, 
    		Map<String, String> serviceMap) throws IOException {
        File file = new File(bpmnFilePath);
        String fileName = file.getName();
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        String nameCapital = nameWithoutExtension.substring(0, 1).toUpperCase() + nameWithoutExtension.substring(1).replaceAll(" ", "");
        
    	Path filePath = Path.of(productJavaFile.getAbsolutePath());
    	List<String> lines = new ArrayList<>(Files.readAllLines(filePath));

    	// Find the line to insert after
    	int insertImportIndex = -1;
    	for (int i = 0; i < lines.size(); i++) {
    		if (lines.get(i).matches(".*import\\s+.*\\.Router;.*")) {
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
    		String resourceVar = nameWithoutExtension + "resource";
    		
    		addWithCheck(lines, insertImportIndex++, "import %s.%sResourceFactory;".formatted(importPath, nameCapital));
    		addWithCheck(lines, insertImportIndex, "import %s.core.resource.%sResourceImpl;".formatted(importPath, nameCapital));
    	    
    		addWithCheck(lines, insertImportRouterAdd++, 
    	    		"\t\t%sResourceImpl %s = %sResourceFactory.createResource(\"%s.core.resource.%sResourceImpl\");"
    	    		.formatted(nameCapital, resourceVar, nameCapital, importPath, nameCapital)
    		);
    		addWithCheck(lines, insertImportRouterAdd++, 
    	    		"\t\tRouter.route(%s);"
    	    		.formatted(resourceVar)
    		);
    		addWithCheck(lines, insertImportRouterAdd++, 
    	    		"\t\tSystem.out.println(\"%sResource endpoints binding\");"
    	    		.formatted(nameCapital)
    		);
    		
    	    // --- Inject service assignments after Router.route ---
    	    for (Map.Entry<String, String> entry : serviceMap.entrySet()) {
    	        String serviceVarName  = entry.getKey();   // e.g. overdraftAccount2Service
    	        String fieldName = serviceVarName;         // e.g. resource.overdraftAccount2Service
    	        addWithCheck(lines, insertImportRouterAdd++,
    	                "\t\t%s.%s = %s;".formatted(resourceVar, fieldName, serviceVarName));
    	    }
    	    
    	    Files.write(filePath, lines);   
    	    return true;
    	}
    	return false;
    }
    
    private static void addWithCheck(List<String> lines, int index, String s) {
    	if (lines.contains(s)) {
    		return;
    	}
    	lines.add(index, s);
    }
}
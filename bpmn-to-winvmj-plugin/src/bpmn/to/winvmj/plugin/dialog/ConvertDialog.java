package bpmn.to.winvmj.plugin.dialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpmn.to.winvmj.plugin.BpmnToWinVmjGenerator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

public class ConvertDialog extends TitleAreaDialog {

    private Text bpmnText;
    private Text ifmlText;
    private Text umlDopText;
    private IFile file;

    public ConvertDialog(Shell parentShell, IFile file) {
        super(parentShell);
        this.file = file;
    }

    @Override
    public void create() {
        super.create();
        setTitle("BPMN to WinVMJ Converter");
        setMessage("Select BPMN and optional IFML file");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);

        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(3, false));

        // BPMN
        new Label(container, SWT.NONE).setText("BPMN File: ");
        bpmnText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        bpmnText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bpmnText.setText(file.getLocation().toOSString());
        bpmnText.setEnabled(false);
        
        GridData bpmnTextData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        bpmnTextData.horizontalSpan = 2;
        bpmnText.setLayoutData(bpmnTextData);

        // IFML
//        new Label(container, SWT.NONE).setText("IFML File:");
//        ifmlText = new Text(container, SWT.BORDER);
//        ifmlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//        Button ifmlBrowse = new Button(container, SWT.PUSH);
//        ifmlBrowse.setText("Browse...");
//        ifmlBrowse.addListener(SWT.Selection, e -> chooseFile(ifmlText, "ifml"));
        
        // UML-DOP
        new Label(container, SWT.NONE).setText("Generated UML-DOP src/ :");
        umlDopText = new Text(container, SWT.BORDER);
        umlDopText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Button umlDop = new Button(container, SWT.PUSH);
        umlDop.setText("Browse...");
        umlDop.addListener(SWT.Selection, e -> chooseFolder(umlDopText));

        return area;
    }
    
    private void chooseFolder(Text targetText) {
        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
        dialog.setText("Select Folder");
        dialog.setMessage("Select a folder");
        
        String selectedPath = dialog.open();
        if (selectedPath != null) {
            targetText.setText(selectedPath);
        }
    }

    @Override
    protected void okPressed() {
        String bpmnPath = bpmnText.getText();
//        String ifmlPath = ifmlText.getText(); // may be empty
        String umlDopPath = umlDopText.getText().trim(); // may be empty

        if (bpmnPath == null || bpmnPath.isBlank()) {
            setErrorMessage("BPMN file is required");
            return;
        }
        
        // Validate BPMN file exists
        File bpmnFile = new File(bpmnPath);
        if (!bpmnFile.exists()) {
            setErrorMessage("BPMN file does not exist: " + bpmnPath);
            return;
        }

        File umlDopDir = new File(umlDopPath);
        try {
        	File bpmnDir = bpmnFile.getParentFile();
        	File outputFolder = new File(bpmnDir, "generated");
        	
        	String importPath = "";
        	if (!umlDopPath.isBlank()) {
        		
        		// Find resource folder
        		File coreFolder = findFolderEndingWith(umlDopDir, ".core");
                if (coreFolder != null) {
                    File resourceFolder = findFolderFileContainingName(coreFolder, "resource");
                    if (resourceFolder != null) {
                    	// Change output folder to real resource dir
                        outputFolder = resourceFolder;
                        
                        // Get import path for project
                        String corePath = coreFolder.getAbsolutePath();
                        importPath = corePath.substring(umlDopPath.length() + 1, corePath.length() - 5);
                        importPath = importPath.replaceAll(" ", "");
                        importPath = importPath.replaceAll("/", ".");
                        importPath = importPath.replaceAll("\\\\", ".");
                    } else {
                        System.err.println("Warning: 'resource' folder not found inside " + coreFolder.getAbsolutePath());
                        throw new IllegalArgumentException();
                    }
                    
                } else {
                    System.err.println("Warning: No '.core' folder found under " + umlDopPath);
                    throw new IllegalArgumentException();
                }
        	}
            
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

			File productFolder = findFolderFileContainingName(umlDopDir, ".product.");
			File productJavaFile = findFolderFileContainingName(productFolder, ".java");
			
			System.out.println("product file name : " + productJavaFile.getName());
			Map<String, String> services = scrapeServiceInstances(productJavaFile);
			boolean success = BpmnToWinVmjGenerator.transformBpmnFile(bpmnPath, outputFolder, importPath, umlDopDir.getAbsolutePath(), services);
			boolean successEdit = BpmnToWinVmjGenerator.editProductRouter(
					productFolder, 
					productJavaFile, 
					bpmnPath, 
					importPath,
					services);
			if (success && successEdit) {
                System.out.println("Conversion completed successfully!");
                setErrorMessage(null);
                setMessage("Conversion completed! Files generated in: " + outputFolder.getAbsolutePath());
            } else {
                setErrorMessage("Conversion failed. Check console for details.");
                return;
            }
        	
            // IFML can be handled later
//             File ifmlFile = ifmlPath.isBlank() ? null : new File(ifmlPath);
             
             refreshFolder(outputFolder);
             

        } catch (Exception ex) {
            setErrorMessage("Failed to parse BPMN: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        super.okPressed();
    }
    
    /**
     * Searches the given directory (non-recursively) for a subfolder whose name ends with the given suffix.
     */
    private File findFolderEndingWith(File searchRoot, String suffix) {
        if (searchRoot == null || !searchRoot.isDirectory()) return null;

        File[] children = searchRoot.listFiles();
        if (children == null) return null;

        for (File child : children) {
            if (child.isDirectory() && child.getName().endsWith(suffix)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Recursively searches the given directory for a subfolder with the exact given name.
     */
    private File findFolderFileContainingName(File searchRoot, String folderName) {
        if (searchRoot == null || !searchRoot.isDirectory()) return null;

        File[] children = searchRoot.listFiles();
        if (children == null) return null;

        for (File child : children) {
            if (child.isDirectory()) {
                if (child.getName().contains(folderName)) {
                    return child;
                }
                // Recurse into subdirectory
                File found = findFolderFileContainingName(child, folderName);
                if (found != null) return found;
            }
            else if (child.isFile()) {
                if (child.getName().contains(folderName)) {
                    return child;
                }
            }
        }
        return null;
    }
    
    private static void refreshFolder(File folder) {
        try {
            IResource resource = ResourcesPlugin.getWorkspace().getRoot()
                .findMember(folder.getAbsolutePath());
            
            if (resource != null) {
                resource.refreshLocal(IResource.DEPTH_INFINITE, null);
                System.out.println("Refreshed folder: " + folder.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error refreshing folder: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static Map<String, String> scrapeServiceInstances(File productJavaFile) throws IOException {
        Map<String, String> serviceMap = new LinkedHashMap<>();
        List<String> lines = Files.readAllLines(productJavaFile.toPath());

        // Matches: SomeType varName = SomeFactory\n.createSomeService("fully.qualified.ClassName"
        // Works across single-line or two-line declarations
        String joined = String.join("\n", lines);

        Pattern pattern = Pattern.compile(
        	    "(\\w+Service)\\s+(\\w+)\\s*=\\s*\\w+ServiceFactory\\s*\\.\\s*\\w+\\(\\s*\"([^\"]+)\""
        	);

    	Matcher matcher = pattern.matcher(joined);
    	while (matcher.find()) {
    	    String type      = matcher.group(1); // AccountService  ← compile-time type
    	    String varName   = matcher.group(2); // overdraftAccount2Service
    	    String implClass = matcher.group(3); // accountpl.account.overdraft.service.AccountServiceImpl
    	    serviceMap.put(varName, type);       // store type instead of implClass
    	}
    
        System.out.println("serviceMap " + serviceMap.toString());

        return serviceMap;
    }
}

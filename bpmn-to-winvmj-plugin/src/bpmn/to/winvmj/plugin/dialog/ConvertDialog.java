package bpmn.to.winvmj.plugin.dialog;

import java.io.File;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpmn.to.winvmj.plugin.BpmnToWinVmjGenerator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

public class ConvertDialog extends TitleAreaDialog {

    private Text bpmnText;
    private Text ifmlText;

    public ConvertDialog(Shell parentShell) {
        super(parentShell);
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
        new Label(container, SWT.NONE).setText("BPMN File:");
        bpmnText = new Text(container, SWT.BORDER);
        bpmnText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Button bpmnBrowse = new Button(container, SWT.PUSH);
        bpmnBrowse.setText("Browse...");
        bpmnBrowse.addListener(SWT.Selection, e -> chooseFile(bpmnText));

        // IFML
        new Label(container, SWT.NONE).setText("IFML File:");
        ifmlText = new Text(container, SWT.BORDER);
        ifmlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Button ifmlBrowse = new Button(container, SWT.PUSH);
        ifmlBrowse.setText("Browse...");
        ifmlBrowse.addListener(SWT.Selection, e -> chooseFile(ifmlText));

        return area;
    }

    private void chooseFile(Text target) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] {"*.bpmn2"});
        String path = dialog.open();
        if (path != null) {
            target.setText(path);
        }
    }

    @Override
    protected void okPressed() {
        String bpmnPath = bpmnText.getText();
        String ifmlPath = ifmlText.getText(); // may be empty

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

        try {
        	File bpmnDir = bpmnFile.getParentFile();
            File outputFolder = new File(bpmnDir, "generated");
            
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

        	BpmnToWinVmjGenerator generator = new BpmnToWinVmjGenerator();
			boolean success = generator.transformBpmnFile(bpmnPath, outputFolder);
			if (success) {
                System.out.println("Conversion completed successfully!");
                setErrorMessage(null);
                setMessage("Conversion completed! Files generated in: " + outputFolder.getAbsolutePath());
            } else {
                setErrorMessage("Conversion failed. Check console for details.");
                return;
            }
        	
            // IFML can be handled later
             File ifmlFile = ifmlPath.isBlank() ? null : new File(ifmlPath);
             
             refreshFolder(outputFolder);
             

        } catch (Exception ex) {
            setErrorMessage("Failed to parse BPMN: " + ex.getMessage());
            return;
        }

        super.okPressed();
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
}

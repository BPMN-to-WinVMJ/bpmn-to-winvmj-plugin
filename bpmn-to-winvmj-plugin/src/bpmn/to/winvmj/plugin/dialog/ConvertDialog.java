package bpmn.to.winvmj.plugin.dialog;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
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

import com.example.model.BPMN;
import com.example.parser.Parser;

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

        try {
            File bpmnFile = new File(bpmnPath);

            BPMN bpmn = Parser.parse(bpmnFile);
            String bpmnXml = bpmn.buildXml();
            
         // 1. Create temp file
            File tempXml = File.createTempFile("bpmn-", ".xml");
            tempXml.deleteOnExit(); // optional but nice

            // 2. Write content
            Files.write(
                tempXml.toPath(),
                bpmnXml.getBytes(StandardCharsets.UTF_8)
            );

            // IFML can be handled later
             File ifmlFile = ifmlPath.isBlank() ? null : new File(ifmlPath);
             
             runAcceleo(tempXml);

        } catch (Exception ex) {
            setErrorMessage("Failed to parse BPMN: " + ex.getMessage());
            return;
        }

        super.okPressed();
    }
    
    private void runAcceleo(File tempXml) throws IOException {

        ResourceSet rs = new ResourceSetImpl();

        // Register default factory for XML/XMI
        rs.getResourceFactoryRegistry()
          .getExtensionToFactoryMap()
          .put("xml", new XMIResourceFactoryImpl());

        URI uri = URI.createFileURI(tempXml.getAbsolutePath());
        Resource resource = rs.getResource(uri, true);

        // Now pass `resource` or `resource.getContents()` to Acceleo
    }
}

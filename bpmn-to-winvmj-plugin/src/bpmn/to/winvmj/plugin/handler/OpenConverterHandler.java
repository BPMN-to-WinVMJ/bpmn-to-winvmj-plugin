package bpmn.to.winvmj.plugin.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import bpmn.to.winvmj.plugin.dialog.ConvertDialog;  // Updated import

public class OpenConverterHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        System.out.println("BPMN Handler: Execute called!");
        
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        
        try {
            System.out.println("BPMN Handler: Opening dialog...");
            ConvertDialog dialog = new ConvertDialog(window.getShell());  // Updated class name
            int result = dialog.open();  // This should work now
            System.out.println("BPMN Handler: Dialog returned: " + result);
            
        } catch (Exception e) {
            System.err.println("BPMN Handler Error: " + e.getMessage());
            e.printStackTrace();
            MessageDialog.openError(window.getShell(), "Error", 
                "Failed to open dialog: " + e.getMessage());
        }
        
        return null;
    }
}
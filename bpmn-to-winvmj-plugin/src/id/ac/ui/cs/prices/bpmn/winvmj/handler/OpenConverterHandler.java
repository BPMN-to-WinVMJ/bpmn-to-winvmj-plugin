package id.ac.ui.cs.prices.bpmn.winvmj.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import id.ac.ui.cs.prices.bpmn.winvmj.dialog.ConvertDialog;

public class OpenConverterHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        System.out.println("BPMN Handler: Execute called!");
        
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection).getFirstElement();
            IFile file = (IFile) Platform.getAdapterManager().getAdapter(element, IFile.class);
            
            IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
            
            try {
                System.out.println("BPMN Handler: Opening dialog...");
                ConvertDialog dialog = new ConvertDialog(window.getShell(), file);
                int result = dialog.open();
                System.out.println("BPMN Handler: Dialog returned: " + result);
                
            } catch (Exception e) {
                System.err.println("BPMN Handler Error: " + e.getMessage());
                e.printStackTrace();
                MessageDialog.openError(window.getShell(), "Error", 
                    "Failed to open dialog: " + e.getMessage());
            }
        }
        return null;
    }
}
package bpmn.to.winvmj.plugin.handler;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import bpmn.to.winvmj.plugin.dialog.ConvertDialog;

public class PopupConvertAction implements IObjectActionDelegate {

    private IFile selectedFile;
    private IWorkbenchPart workbenchPart;

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.workbenchPart = targetPart;
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        selectedFile = null;
        if (selection instanceof IStructuredSelection) {
            Object firstElement = ((IStructuredSelection) selection).getFirstElement();
            if (firstElement instanceof IFile) {
                selectedFile = (IFile) firstElement;
            }
        }
    }

    @Override
    public void run(IAction action) {
        if (workbenchPart == null || selectedFile == null) {
            return;
        }

        try {
            ConvertDialog dialog = new ConvertDialog(workbenchPart.getSite().getShell(), selectedFile);
            dialog.open();
        } catch (Exception e) {
            MessageDialog.openError(
                workbenchPart.getSite().getShell(),
                "Error",
                "Failed to open dialog: " + e.getMessage()
            );
        }
    }
}
